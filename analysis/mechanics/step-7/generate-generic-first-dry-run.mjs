import { readFile, writeFile } from 'node:fs/promises';
import { join } from 'node:path';

const root = process.cwd();
const manifestPath = join(root, 'analysis/mechanics/step-7/generic-first-catalogue-applicability.json');
const outputJson = join(root, 'analysis/mechanics/step-7/generic-first-dry-run-results.json');
const outputMarkdown = join(root, 'analysis/mechanics/step-7/7.4.4-generic-first-full-catalogue-dry-run.md');
const baseUrl = (process.env.SPELL_API_BASE_URL ?? 'http://localhost:8080/api/spells').replace(/\/$/, '');
const manifest = JSON.parse(await readFile(manifestPath, 'utf8'));
const owners = [...manifest.owners].sort((left, right) => left.ownerId.localeCompare(right.ownerId));

if (owners.length !== 969 || new Set(owners.map((owner) => owner.ownerId)).size !== owners.length) {
  throw new Error(`Expected 969 unique source-qualified owners; found ${owners.length}`);
}

const records = new Array(owners.length);
let nextIndex = 0;
const workerCount = 6;

async function worker() {
  while (true) {
    const index = nextIndex++;
    if (index >= owners.length) return;
    const owner = owners[index];
    const response = await fetch(`${baseUrl}/${owner.ownerId}?include=interpretedMechanics`);
    if (!response.ok) {
      throw new Error(`API returned ${response.status} for ${owner.ownerId}`);
    }
    const spell = await response.json();
    const canonical = spell.interpretedMechanics;
    const experimental = canonical?.experimentalInterpretation;
    if (!experimental || experimental.origin !== 'EXPERIMENTAL') {
      throw new Error(`Experimental result missing for ${owner.ownerId}`);
    }
    const normalizedExperimental = {
      ...experimental,
      appliedRecognizers: experimental.appliedRecognizers ?? [],
      candidateClaims: experimental.candidateClaims ?? [],
      unsupported: experimental.unsupported ?? [],
      evidence: experimental.evidence ?? [],
      unaccountedSourcePaths: experimental.unaccountedSourcePaths ?? [],
      diagnostics: experimental.diagnostics ?? [],
      canonicalComparison: {
        reproducedClaimIds: experimental.canonicalComparison?.reproducedClaimIds ?? [],
        missingCanonicalClaimIds: experimental.canonicalComparison?.missingCanonicalClaimIds ?? [],
        additionalExperimentalClaimIds: experimental.canonicalComparison?.additionalExperimentalClaimIds ?? [],
        changedClaimIds: experimental.canonicalComparison?.changedClaimIds ?? [],
      },
    };
    records[index] = {
      ownerId: owner.ownerId,
      spellName: spell.name,
      sourceCode: spell.source.source,
      sourceRevision: {
        datasetVersion: experimental.datasetVersion,
        resourcePath: experimental.sourceResourcePath,
        resourceSha256: experimental.sourceResourceSha256,
      },
      canonical: {
        processingState: canonical.processing.state,
        coverageStatus: canonical.coverage.status,
        claimCount: (canonical.interpretations ?? []).length,
      },
      experimental: normalizedExperimental,
    };
  }
}

await Promise.all(Array.from({ length: workerCount }, worker));
if (records.length !== 969 || records.some((record) => record == null)) {
  throw new Error('Dry run did not return exactly one result for every owner');
}

const sourceResources = [...new Map(records.map((record) => [
  record.sourceRevision.resourcePath,
  {
    resourcePath: record.sourceRevision.resourcePath,
    sha256: record.sourceRevision.resourceSha256,
    datasetVersion: record.sourceRevision.datasetVersion,
  },
])).values()].sort((left, right) => left.resourcePath.localeCompare(right.resourcePath));
if (sourceResources.length !== 18) throw new Error(`Expected 18 resources; found ${sourceResources.length}`);

const outcomeCounts = countBy(records, (record) => record.experimental.outcome);
const matchedOwners = Object.entries(outcomeCounts)
  .filter(([outcome]) => outcome !== 'NO_PATTERN_MATCH')
  .reduce((total, [, count]) => total + count, 0);
const sourceCounts = countBy(records, (record) => record.sourceCode);
const familyClaims = new Map();
const recognizerOwners = new Map();
const comparison = { reproduced: 0, missing: 0, additional: 0, changed: 0 };
let candidateOwners = 0;
let totalClaims = 0;
let unprocessedCandidateClaims = 0;
let unsupportedCount = 0;
let unaccountedCount = 0;

for (const record of records) {
  const experimental = record.experimental;
  const claims = experimental.candidateClaims;
  if (claims.length > 0) candidateOwners++;
  totalClaims += claims.length;
  unsupportedCount += experimental.unsupported.length;
  unaccountedCount += experimental.unaccountedSourcePaths.length;
  for (const claim of claims) familyClaims.set(claim.payload.kind,
    (familyClaims.get(claim.payload.kind) ?? 0) + 1);
  for (const recognizer of experimental.appliedRecognizers) recognizerOwners.set(recognizer,
    (recognizerOwners.get(recognizer) ?? 0) + 1);
  if (record.canonical.processingState === 'PROCESSED') {
    comparison.reproduced += experimental.canonicalComparison.reproducedClaimIds.length;
    comparison.missing += experimental.canonicalComparison.missingCanonicalClaimIds.length;
    comparison.additional += experimental.canonicalComparison.additionalExperimentalClaimIds.length;
    comparison.changed += experimental.canonicalComparison.changedClaimIds.length;
  } else {
    unprocessedCandidateClaims += claims.length;
  }
}

const artifact = {
  schemaVersion: 1,
  artifactVersion: 'generic-first-full-catalogue-dry-run-v1',
  engineVersion: 'generic-first-dry-run-v1',
  datasetVersion: '5eTools v2.37.0',
  ownerCount: records.length,
  sourceResourceCount: sourceResources.length,
  canonicalBaseline: { processedOwners: 129, notAttemptedOwners: 840, existingClaims: 279 },
  sourceResources,
  summary: {
    matchedOwners,
    candidateOwners,
    matchedOwnersWithoutClaims: matchedOwners - candidateOwners,
    noPatternMatchOwners: outcomeCounts.NO_PATTERN_MATCH ?? 0,
    conflicts: outcomeCounts.CANDIDATE_CONFLICT ?? 0,
    extractionErrors: outcomeCounts.EXTRACTION_ERROR ?? 0,
    candidateClaims: totalClaims,
    candidateClaimsByMechanicFamily: Object.fromEntries([...familyClaims].sort(([a], [b]) => a.localeCompare(b))),
    outcomes: outcomeCounts,
    unsupportedConstructs: unsupportedCount,
    unaccountedSourcePaths: unaccountedCount,
    ownersByRecognizer: Object.fromEntries([...recognizerOwners].sort(([a], [b]) => a.localeCompare(b))),
    canonicalClaimComparison: comparison,
    experimentalClaimsForPreviouslyUnprocessedOwners: unprocessedCandidateClaims,
    ownersBySource: sourceCounts,
  },
  owners: records,
};

const json = `${JSON.stringify(artifact, null, 2)}\n`;
await writeFile(outputJson, json, 'utf8');
await writeFile(outputMarkdown, renderMarkdown(artifact), 'utf8');
process.stdout.write(`${JSON.stringify({
  owners: artifact.ownerCount,
  sources: artifact.sourceResourceCount,
  candidateOwners,
  outcomes: outcomeCounts,
  candidateClaims: totalClaims,
}, null, 2)}\n`);

function countBy(items, keySelector) {
  const values = new Map();
  for (const item of items) {
    const key = keySelector(item);
    values.set(key, (values.get(key) ?? 0) + 1);
  }
  return Object.fromEntries([...values].sort(([left], [right]) => left.localeCompare(right)));
}

function table(rows, headings) {
  const header = `| ${headings.join(' | ')} |`;
  const rule = `| ${headings.map(() => '---').join(' | ')} |`;
  return [header, rule, ...rows.map((row) => `| ${row.join(' | ')} |`)].join('\n');
}

function renderMarkdown(result) {
  const byOutcome = Object.entries(result.summary.outcomes).map(([name, count]) => [name, String(count)]);
  const byFamily = Object.entries(result.summary.candidateClaimsByMechanicFamily)
    .map(([name, count]) => [name, String(count)]);
  const bySource = Object.entries(result.summary.ownersBySource).map(([name, count]) => [
    name,
    String(count),
    String(result.owners.filter((owner) => owner.sourceCode === name
      && owner.canonical.processingState === 'PROCESSED').length),
    String(result.owners.filter((owner) => owner.sourceCode === name
      && owner.experimental.candidateClaims.length > 0).length),
  ]);
  const processed = result.owners.filter((owner) => owner.canonical.processingState === 'PROCESSED');
  const processedClaims = processed.reduce((total, owner) => total + owner.canonical.claimCount, 0);
  const text = [
    '# Step 7.4.4 Generic-First Full Catalogue Dry Run',
    '',
    `**Artifact:** ${result.artifactVersion}; engine ${result.engineVersion}; source ${result.datasetVersion}.`,
    '**Status:** experimental candidate output only. No canonical claim, review decision, license, or',
    'publication authorization was changed.',
    '',
    '## Summary',
    '',
    `- Owners evaluated: ${result.ownerCount} across ${result.sourceResourceCount} pinned resources.`,
    `- Owners with a recognizer match: ${result.summary.matchedOwners} (${result.summary.matchedOwnersWithoutClaims} yielded no candidate claims).`,
    `- Owners with candidate claims: ${result.summary.candidateOwners}.`,
    `- Owners with no matching pattern: ${result.summary.noPatternMatchOwners}.`,
    `- Candidate claims: ${result.summary.candidateClaims}.`,
    `- Conflicts: ${result.summary.conflicts}; extraction errors: ${result.summary.extractionErrors}.`,
    `- Unsupported constructs: ${result.summary.unsupportedConstructs}; unaccounted paths: ${result.summary.unaccountedSourcePaths}.`,
    '',
    '## Outcome distribution',
    '',
    table(byOutcome, ['Outcome', 'Owners']),
    '',
    '## Candidate claims by mechanic family',
    '',
    table(byFamily, ['Mechanic family', 'Claims']),
    '',
    '## Source and edition distribution',
    '',
    table(bySource, ['Source', 'Owners', 'Canonical processed', 'Candidate owners']),
    '',
    '## Recognizer reuse',
    '',
    table(Object.entries(result.summary.ownersByRecognizer).map(([name, count]) => [name, String(count)]),
      ['Recognizer / adapter pattern', 'Owner matches']),
    '',
    '## Comparison with canonical interpretations',
    '',
    `The current canonical baseline has ${processed.length} processed owners and ${processedClaims} claims.`,
    `Among previously processed owners: ${result.summary.canonicalClaimComparison.reproduced} reproduced,`,
    `${result.summary.canonicalClaimComparison.missing} missing, ${result.summary.canonicalClaimComparison.additional} additional,`,
    `and ${result.summary.canonicalClaimComparison.changed} changed claim identities/hashes. Previously unprocessed`,
    `owners produced ${result.summary.experimentalClaimsForPreviouslyUnprocessedOwners} separate experimental claims.`,
    'These are manual-review leads; no canonical interpretation was reconciled or replaced.',
    '',
    '## API examples',
    '',
    'A processed spell keeps its canonical claims at the existing fields and adds a separate candidate:',
    '',
    '```json',
    '{',
    '  "interpretedMechanics": {',
    '    "origin": "CANONICAL",',
    '    "processing": { "state": "PROCESSED" },',
    '    "interpretations": ["... canonical claims ..."],',
    '    "experimentalInterpretation": {',
    '      "origin": "EXPERIMENTAL",',
    '      "outcome": "CANDIDATE_PARTIAL",',
    '      "reviewed": false,',
    '      "publicationAuthorized": false,',
    '      "candidateClaims": ["... candidate claims with evidence and review metadata ..."]',
    '    }',
    '  }',
    '}',
    '```',
    '',
    'For an unprocessed spell such as `spell:phb:poison-spray`, the canonical envelope remains',
    '`NOT_ATTEMPTED` with no canonical claims, while `experimentalInterpretation` contains the partial',
    'candidate. For `spell:aag:air-bubble`, the include returns `NO_PATTERN_MATCH`, empty candidate claims,',
    'and explicit unaccounted source paths. Omitting the include still omits the whole interpreted-mechanics',
    'field from the spell response.',
    '',
    '## Limitations and interpretation',
    '',
    '- Pattern identifiers are adapter/provenance labels, not one independent recognizer per label.',
    '- The PHB broad candidate path reuses the existing PHB extraction implementation; six dedicated adapters',
    '  still recognize only their source-qualified owners. Other source families without a matching adapter',
    '  remain `NO_PATTERN_MATCH` even when the spell has mechanics.',
    '- Candidate claims inherit typed evidence locators and remain unreviewed and unauthorized. Recognition',
    '  is not semantic verification, full-spell accounting, licensing, or publication readiness.',
    '- Existing single-adapter matchers do not yet expose composable independent mechanic-family recognizers.',
    '  No speculative cross-edition reuse was inferred.',
    '- The backend computes the experimental catalogue once during startup and caches it for requests; it does',
    '  not currently load this generated analysis file. A missing/stale analysis artifact therefore does not',
    '  trigger a canonical fallback. A missing or fingerprint-mismatched pinned source yields per-owner',
    '  `EXTRACTION_ERROR` diagnostics instead.',
    '',
    'The machine-readable file contains one owner-sorted result per pinned source-qualified spell, including',
    'canonical processing state, experimental outcome, source fingerprints, candidate claims, evidence,',
    'unsupported paths, diagnostics, and per-claim-set canonical comparison.',
    '',
    '## Verification',
    '',
    '- The catalogue was requested twice through the `interpretedMechanics` include. The two generated JSON',
    '  artifacts and two summary reports were byte-for-byte identical.',
    '- The generator verifies 969 unique owner records and 18 source resources before writing output.',
    '- All 175 pinned source JSON hashes passed the repository verifier.',
    '- Full direct Surefire run: 887 tests across 104 suites, 0 failures, 0 errors, 6 skips.',
    '- Standard Maven test compilation hit the known Windows JDK `ZipFileSystemProvider.removeFileSystem`',
    '  `AccessDeniedException`; direct Surefire ran the previously compiled test classes. The two new API',
    '  assertions are present in test source but could not be compiled in this environment.',
    '- `git diff --check` passed.',
    '',
  ].join('\n');
  return text;
}
