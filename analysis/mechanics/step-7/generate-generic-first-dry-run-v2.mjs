import { readFile, writeFile } from 'node:fs/promises';
import { join } from 'node:path';

const root = process.cwd();
const manifest = JSON.parse(await readFile(join(root,
  'analysis/mechanics/step-7/generic-first-catalogue-applicability.json'), 'utf8'));
const v1 = JSON.parse(await readFile(join(root,
  'analysis/mechanics/step-7/generic-first-dry-run-results.json'), 'utf8'));
const v1ByOwner = new Map(v1.owners.map(owner => [owner.ownerId, owner]));
const owners = [...manifest.owners].sort((left, right) => left.ownerId.localeCompare(right.ownerId));
if (owners.length !== 969 || new Set(owners.map(owner => owner.ownerId)).size !== 969) {
  throw new Error(`Expected 969 unique catalogue owners; found ${owners.length}`);
}
const baseUrl = (process.env.SPELL_API_BASE_URL ?? 'http://localhost:8081/api/spells').replace(/\/$/, '');
const records = new Array(owners.length);
let next = 0;
async function worker() {
  while (true) {
    const index = next++;
    if (index >= owners.length) return;
    const owner = owners[index];
    const response = await fetch(`${baseUrl}/${owner.ownerId}?include=interpretedMechanics`);
    if (!response.ok) throw new Error(`${owner.ownerId}: API ${response.status}`);
    const spell = await response.json();
    const mechanics = spell.interpretedMechanics;
    const experimental = mechanics?.experimentalInterpretationV2;
    if (!experimental || experimental.apiVersion !== 2 || experimental.origin !== 'EXPERIMENTAL') {
      throw new Error(`Missing v2 experimental result for ${owner.ownerId}`);
    }
    const prior = v1ByOwner.get(owner.ownerId);
    const v1Families = [...new Set((prior?.experimental?.candidateClaims ?? [])
      .map(claim => claim.payload?.kind).filter(Boolean))].sort();
    const v2Mechanics = experimental.mechanics ?? [];
    const v2Evidence = experimental.evidence ?? [];
    const v2Diagnostics = experimental.diagnostics ?? [];
    const v2Metadata = experimental.metadata ?? {};
    const v2Families = [...new Set(v2Mechanics.map(mechanic => mechanic.type))].sort();
    records[index] = {
      ownerId: owner.ownerId,
      spellName: spell.name,
      sourceCode: spell.source.source,
      sourceRevision: {
        datasetVersion: v2Metadata.datasetVersion,
        resourcePath: v2Metadata.sourceResource,
        resourceSha256: v2Metadata.sourceSha256,
      },
      canonical: {
        processingState: mechanics.processing.state,
        coverageStatus: mechanics.coverage.status,
        claimCount: (mechanics.interpretations ?? []).length,
        claims: (mechanics.interpretations ?? []).map(claim => ({
          id: typeof claim.id === 'string' ? claim.id : claim.id?.value,
          hash: claim.revision.contentSha256,
          family: claim.payload.kind,
        })).sort((a, b) => a.id.localeCompare(b.id)),
      },
      experimentalV1: {
        outcome: prior?.experimental?.outcome ?? 'NOT_IN_V1_ARTIFACT',
        claimCount: prior?.experimental?.candidateClaims?.length ?? 0,
        mechanicFamilies: v1Families,
      },
      experimentalV2: {
        ...experimental,
        mechanics: [...v2Mechanics].sort((a, b) => a.id.localeCompare(b.id)),
        evidence: [...v2Evidence].sort((a, b) => a.id.localeCompare(b.id)),
        diagnostics: [...v2Diagnostics].sort((a, b) =>
          `${a.code}|${a.pointer ?? ''}|${a.mechanicId ?? ''}`.localeCompare(
            `${b.code}|${b.pointer ?? ''}|${b.mechanicId ?? ''}`)),
        metadata: {
          ...v2Metadata,
          strategyIds: [...(v2Metadata.strategyIds ?? [])].sort(),
          canonicalComparison: [...(v2Metadata.canonicalComparison ?? [])].sort(),
        },
      },
      v1V2Comparison: {
        v1ClaimCount: prior?.experimental?.candidateClaims?.length ?? 0,
        v2MechanicCount: v2Mechanics.length,
        v1OnlyFamilies: v1Families.filter(family => !v2Families.includes(family)),
        v2OnlyFamilies: v2Families.filter(family => !v1Families.includes(family)),
      },
    };
  }
}
await Promise.all(Array.from({ length: 8 }, worker));
if (records.some(record => record == null)) throw new Error('Some catalogue owners were omitted');
const resources = [...new Map(records.map(record => [record.sourceRevision.resourcePath,
  { resourcePath: record.sourceRevision.resourcePath, sha256: record.sourceRevision.resourceSha256,
    datasetVersion: record.sourceRevision.datasetVersion }])).values()]
  .sort((a, b) => a.resourcePath.localeCompare(b.resourcePath));
if (resources.length !== 18) throw new Error(`Expected 18 resources; found ${resources.length}`);
const summary = {
  outcomes: countBy(records, record => record.experimentalV2.outcome),
  ownersWithMechanics: records.filter(record => record.experimentalV2.mechanics.length > 0).length,
  ownersWithoutMechanics: records.filter(record => record.experimentalV2.mechanics.length === 0).length,
  candidateMechanics: records.reduce((sum, record) => sum + record.experimentalV2.mechanics.length, 0),
  mechanicFamilies: countBy(records.flatMap(record => record.experimentalV2.mechanics), mechanic => mechanic.type),
  diagnostics: countBy(records.flatMap(record => record.experimentalV2.diagnostics), diagnostic => diagnostic.code),
  sourceDistribution: countBy(records, record => record.sourceCode),
  v1Claims: records.reduce((sum, record) => sum + record.experimentalV1.claimCount, 0),
  v1OwnersWithClaims: records.filter(record => record.experimentalV1.claimCount > 0).length,
    canonicalOwners: records.filter(record => record.canonical.processingState === 'PROCESSED').length,
  canonicalClaims: records.reduce((sum, record) => sum + record.canonical.claimCount, 0),
  canonicalComparison: sumComparisons(records),
};
const artifact = { schemaVersion: 2, artifactVersion: 'generic-first-full-catalogue-dry-run-v2',
  engineVersion: 'generic-strategy-pipeline-v2.0', datasetVersion: '5eTools v2.37.0',
  ownerCount: records.length, resourceCount: resources.length, resources, summary, owners: records };
await writeFile(join(root, 'analysis/mechanics/step-7/generic-first-dry-run-v2-results.json'),
  `${JSON.stringify(artifact, null, 2)}\n`, 'utf8');
await writeFile(join(root, 'analysis/mechanics/step-7/7.4.4-generic-first-full-catalogue-dry-run-v2.md'),
  render(artifact), 'utf8');
process.stdout.write(`${JSON.stringify({ ownerCount: artifact.ownerCount, resourceCount: artifact.resourceCount,
  summary }, null, 2)}\n`);

function countBy(items, selector) {
  const counts = new Map();
  for (const item of items) { const key = selector(item); counts.set(key, (counts.get(key) ?? 0) + 1); }
  return Object.fromEntries([...counts].sort(([a], [b]) => a.localeCompare(b)));
}
function sumComparisons(values) {
  const totals = { reproducedExactPayloads: 0, missingCanonicalPayloads: 0,
    additionalExperimentalPayloads: 0, changedPayloadsSameFamily: 0 };
  for (const record of values) for (const entry of record.experimentalV2.metadata.canonicalComparison) {
    const [key, value] = entry.split('=');
    if (Object.hasOwn(totals, key)) totals[key] += Number(value);
  }
  return totals;
}
function table(rows, headers) {
  return [`| ${headers.join(' | ')} |`, `| ${headers.map(() => '---').join(' | ')} |`,
    ...rows.map(row => `| ${row.join(' | ')} |`)].join('\n');
}
function render(value) {
  const sourceRows = Object.entries(value.summary.sourceDistribution).map(([source, count]) => [source, String(count)]);
  const familyRows = Object.entries(value.summary.mechanicFamilies).map(([family, count]) => [family, String(count)]);
  const outcomeRows = Object.entries(value.summary.outcomes).map(([outcome, count]) => [outcome, String(count)]);
  const diagnosticRows = Object.entries(value.summary.diagnostics).map(([diagnostic, count]) => [diagnostic, String(count)]);
  const unsupported = value.owners.find(owner => owner.experimentalV2.diagnostics.some(d =>
    d.code === 'INSUFFICIENT_SEMANTIC_RELATIONSHIP'));
  const fireball = value.owners.find(owner => owner.ownerId === 'spell:phb:fireball');
  const poisonSpray = value.owners.find(owner => owner.ownerId === 'spell:phb:poison-spray');
  return `# Generic-First Full Catalogue Dry Run v2\n\n` +
    `**Status:** experimental candidate output only. Canonical claims, human review, licensing and publication decisions were not changed.\n\n` +
    `## Summary\n\n- Owners evaluated: ${value.ownerCount} across ${value.resourceCount} pinned resources.\n` +
    `- Owners with candidate mechanics: ${value.summary.ownersWithMechanics}.\n` +
    `- Owners with no candidate mechanics: ${value.summary.ownersWithoutMechanics}.\n` +
    `- Candidate mechanics: ${value.summary.candidateMechanics}.\n\n` +
    `A candidate mechanic is not a complete interpretation. All v2 coverage is limited to recognized fragments; no spell is marked complete.\n\n` +
    `## Outcomes\n\n${table(outcomeRows, ['Outcome', 'Owners'])}\n\n` +
    `## Mechanics by family\n\n${table(familyRows, ['Family', 'Candidates'])}\n\n` +
    `## Diagnostics\n\n${table(diagnosticRows, ['Diagnostic', 'Count'])}\n\n` +
    `## Source distribution\n\n${table(sourceRows, ['Source', 'Owners'])}\n\n` +
    `## v1 and canonical comparison\n\n- v1 candidate claims: ${value.summary.v1Claims} across ${value.summary.v1OwnersWithClaims} owners.\n` +
    `- v2 candidate mechanics: ${value.summary.candidateMechanics}.\n` +
    `- Canonical baseline: ${value.summary.canonicalOwners} owners and ${value.summary.canonicalClaims} claims.\n` +
    `- Payload comparison: ${JSON.stringify(value.summary.canonicalComparison)}. Exact payload equality does not establish equivalent source evidence or review status.\n\n` +
    `## Example API projections\n\nFireball retains canonical claims and adds independent v1/v2 experimental projections: ` +
    `canonical=${fireball?.canonical.claimCount ?? 0}, v2 mechanics=${fireball?.experimentalV2.mechanics.length ?? 0}.\n\n` +
    `Previously unprocessed Poison Spray: canonical=${poisonSpray?.canonical.processingState ?? 'unknown'}, ` +
    `v2=${poisonSpray?.experimentalV2.outcome ?? 'unknown'} with ${poisonSpray?.experimentalV2.mechanics.length ?? 0} candidate mechanics.\n\n` +
    `Recognized but unsupported example: ${unsupported?.ownerId ?? 'none found'}; ` +
    `${unsupported?.experimentalV2.diagnostics.find(d => d.code === 'INSUFFICIENT_SEMANTIC_RELATIONSHIP')?.explanation ?? ''}\n\n` +
    `## Limitations\n\n- Strategies use generic source fragments and do not use owner ID or source-code allowlists.\n` +
    `- Scaling is classified but remains unextracted until a source-backed affected-mechanic relationship is clear.\n` +
    `- Ambiguous condition, attack and damage relationships remain diagnostic.\n` +
    `- Experimental review is pending, licensing needs review and publication authorization is false.\n`;
}
