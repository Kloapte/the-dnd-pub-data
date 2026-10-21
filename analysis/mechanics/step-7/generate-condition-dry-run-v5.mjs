import { readFile, writeFile } from 'node:fs/promises';
import { join } from 'node:path';

const root = process.cwd();
const previous = JSON.parse(await readFile(join(root,
  'analysis/mechanics/step-7/generic-scaling-dry-run-v4-results.json'), 'utf8'));
const owners = previous.owners.map(owner => owner.ownerId).sort((a, b) => a.localeCompare(b));
if (owners.length !== 969 || new Set(owners).size !== owners.length) {
  throw new Error('Step 7.5 v4 baseline does not contain exactly 969 unique owners');
}
const baseUrl = (process.env.SPELL_API_BASE_URL ?? 'http://localhost:8081/api/spells').replace(/\/$/, '');
const records = new Array(owners.length);
let cursor = 0;
async function worker() {
  while (true) {
    const index = cursor++;
    if (index >= owners.length) return;
    const ownerId = owners[index];
    const headers = process.env.SPELL_API_AUTHORIZATION
      ? { Authorization: process.env.SPELL_API_AUTHORIZATION } : {};
    const response = await fetch(baseUrl + '/' + ownerId + '?include=interpretedMechanics', { headers });
    if (!response.ok) throw new Error(ownerId + ': API ' + response.status);
    const spell = await response.json();
    const interpretation = spell.interpretedMechanics?.experimentalInterpretationV2;
    if (!interpretation || interpretation.metadata?.engineVersion !== 'generic-strategy-pipeline-v2.4') {
      throw new Error(ownerId + ': expected experimental pipeline v2.4');
    }
    const old = previous.owners.find(item => item.ownerId === ownerId);
    records[index] = {
      ownerId,
      spellName: spell.name,
      sourceCode: spell.source.source,
      canonical: old.canonical,
      canonicalCurrent: {
        processingState: spell.interpretedMechanics.processing.state,
        coverageStatus: spell.interpretedMechanics.coverage.status,
        claimCount: spell.interpretedMechanics.interpretations?.length ?? 0,
        claims: (spell.interpretedMechanics.interpretations ?? []).map(claim => ({
          id: typeof claim.id === 'string' ? claim.id : claim.id?.value,
          hash: claim.revision.contentSha256,
          family: claim.payload.kind,
        })).sort((a, b) => a.id.localeCompare(b.id)),
      },
      previousV4: old.experimental,
      experimental: sortProjection(interpretation),
    };
  }
}
await Promise.all(Array.from({ length: 8 }, worker));
if (records.some(item => item == null)) throw new Error('Condition dry run omitted an owner');

const mechanics = records.flatMap(record => record.experimental.mechanics);
const relationships = records.flatMap(record => record.experimental.relationships);
const conditions = mechanics.filter(item => item.type === 'CONDITION');
const conditionRelations = relationships.filter(item => item.type.startsWith('CONDITION_')
  || item.type === 'SAVE_CONDITION_OUTCOME');
const priorRelations = records.flatMap(record => record.previousV4.relationships);
const sourceDistribution = countBy(records, item => item.sourceCode);
const resources = [...new Map(records.map(item => [item.experimental.metadata.sourceResource, {
  resourcePath: item.experimental.metadata.sourceResource,
  sha256: item.experimental.metadata.sourceSha256,
  datasetVersion: item.experimental.metadata.datasetVersion,
}])).values()].sort((a, b) => a.resourcePath.localeCompare(b.resourcePath));
const summary = {
  ownerCount: records.length,
  resourceCount: resources.length,
  ownersWithMechanics: records.filter(item => item.experimental.mechanics.length > 0).length,
  ownersWithoutMechanics: records.filter(item => item.experimental.mechanics.length === 0).length,
  outcomes: countBy(records, item => item.experimental.outcome),
  mechanicsByFamily: countBy(mechanics, item => item.type),
  conditionMechanics: conditions.length,
  conditionsByOperation: countBy(conditions, item => item.properties.operation ?? 'CHOICE'),
  conditionChoicePayloads: conditions.filter(item => item.properties.kind === 'CONDITION_CHOICE').length,
  conditionTerminationEvents: mechanics.filter(item => item.type === 'CONDITION_TERMINATION').length,
  conditionRelationships: conditionRelations.length,
  relationshipsByType: countBy(relationships, item => item.type),
  conditionRelationshipOwners: new Set(conditionRelations.map(item => item.ownerId.value)).size,
  conditionOutcomes: countBy(conditionRelations.flatMap(item => item.conditionOutcomes ?? []),
    item => item.timing + ':' + item.outcome + ':' + item.effect),
  terminationTriggers: countBy(conditionRelations.map(item => item.terminationRule).filter(Boolean),
    item => item.trigger),
  diagnosticsByCode: countBy(records.flatMap(item => item.experimental.diagnostics), item => item.code),
  canonicalOwners: records.filter(item => item.canonicalCurrent.processingState === 'PROCESSED').length,
  canonicalClaims: records.reduce((total, item) => total + item.canonicalCurrent.claimCount, 0),
  canonicalClaimsExactlyStable: records.every(item =>
    JSON.stringify(item.canonical.claims) === JSON.stringify(item.canonicalCurrent.claims)),
  sourceDistribution,
  v4CandidateMechanics: records.reduce((count, item) => count + item.previousV4.mechanics.length, 0),
  v5CandidateMechanics: mechanics.length,
  v4RelationshipsByType: countBy(priorRelations, item => item.type),
  v5RelationshipsByType: countBy(relationships, item => item.type),
  existingRelationshipsPreserved: compareRelationships(priorRelations, relationships),
  candidatePayloadComparison: comparePayloads(records.flatMap(item => item.previousV4.mechanics), mechanics),
};
const artifact = {
  schemaVersion: 5,
  artifactVersion: 'generic-condition-dry-run-v5',
  engineVersion: 'generic-strategy-pipeline-v2.4',
  baselineVersion: 'generic-scaling-dry-run-v4',
  datasetVersion: '5eTools v2.37.0',
  ownerCount: records.length,
  resourceCount: resources.length,
  resources,
  summary,
  owners: records,
};
await writeFile(join(root, 'analysis/mechanics/step-7/generic-condition-dry-run-v5-results.json'),
  JSON.stringify(artifact, null, 2) + '\n', 'utf8');
await writeFile(join(root, 'analysis/mechanics/step-7/7.5-generic-condition-dry-run-v5.md'),
  render(artifact), 'utf8');
process.stdout.write(JSON.stringify(summary, null, 2) + '\n');

function sortProjection(value) {
  return {
    ...value,
    mechanics: [...(value.mechanics ?? [])].sort((a, b) => a.id.localeCompare(b.id)),
    relationships: [...(value.relationships ?? [])].sort((a, b) => a.id.localeCompare(b.id)),
    evidence: [...(value.evidence ?? [])].sort((a, b) => a.id.localeCompare(b.id)),
    diagnostics: [...(value.diagnostics ?? [])].sort((a, b) =>
      (a.code + '|' + (a.pointer ?? '') + '|' + (a.mechanicId ?? '')).localeCompare(
        b.code + '|' + (b.pointer ?? '') + '|' + (b.mechanicId ?? ''))),
  };
}
function countBy(items, keyOf) {
  const counts = new Map();
  for (const item of items) {
    const key = keyOf(item);
    counts.set(key, (counts.get(key) ?? 0) + 1);
  }
  return Object.fromEntries([...counts].sort(([left], [right]) => left.localeCompare(right)));
}
function compareRelationships(before, after) {
  const signatures = values => {
    const counts = new Map();
    for (const value of values) {
      const signature = JSON.stringify({ type: value.type, source: value.sourceMechanicId,
        target: value.targetMechanicId, outcomes: value.outcomes, attackOutcomes: value.attackOutcomes,
        conditionOutcomes: value.conditionOutcomes, terminationRule: value.terminationRule,
        scalingRule: value.scalingRule });
      counts.set(signature, (counts.get(signature) ?? 0) + 1);
    }
    return counts;
  };
  const oldCounts = signatures(before), newCounts = signatures(after);
  const keys = new Set([...oldCounts.keys(), ...newCounts.keys()]);
  let added = 0, removed = 0;
  for (const key of keys) {
    added += Math.max(0, (newCounts.get(key) ?? 0) - (oldCounts.get(key) ?? 0));
    removed += Math.max(0, (oldCounts.get(key) ?? 0) - (newCounts.get(key) ?? 0));
  }
  return { before: before.length, after: after.length, added, removed };
}
function comparePayloads(before, after) {
  const key = item => JSON.stringify({ type: item.type, properties: item.properties });
  const remaining = before.map(key);
  let exactMatches = 0, added = 0;
  for (const item of after) {
    const value = key(item), index = remaining.indexOf(value);
    if (index >= 0) { remaining.splice(index, 1); exactMatches++; } else added++;
  }
  return { exactMatches, removed: remaining.length, added };
}
function render(value) {
  const summary = value.summary;
  const lines = [
    '# Generic Condition Dry Run v5', '',
    '**Status:** experimental only. Canonical claims, review, licensing, and publication decisions are unchanged.', '',
    '## Catalogue results', '',
    `- Owners/resources: ${value.ownerCount} / ${value.resourceCount}.`,
    `- Owners with/without candidate mechanics: ${summary.ownersWithMechanics} / ${summary.ownersWithoutMechanics}.`,
    `- Outcomes: ${JSON.stringify(summary.outcomes)}.`,
    `- Candidate mechanics: ${JSON.stringify(summary.mechanicsByFamily)}.`,
    `- Canonical owners/claims unchanged: ${summary.canonicalOwners} / ${summary.canonicalClaims}; exact snapshot stable: ${summary.canonicalClaimsExactlyStable}.`, '',
    '## Condition operations and relationships', '',
    `- Condition operation mechanics: ${summary.conditionMechanics}; by operation: ${JSON.stringify(summary.conditionsByOperation)}.`,
    `- Mutually exclusive condition choices: ${summary.conditionChoicePayloads}; action-based termination event mechanics: ${summary.conditionTerminationEvents}.`,
    `- Condition relationship owners/count: ${summary.conditionRelationshipOwners} / ${summary.conditionRelationships}.`,
    `- Relationships by type: ${JSON.stringify(summary.relationshipsByType)}.`,
    `- Save outcomes: ${JSON.stringify(summary.conditionOutcomes)}.`,
    `- Termination triggers: ${JSON.stringify(summary.terminationTriggers)}.`,
    `- Condition diagnostics: ${JSON.stringify(Object.fromEntries(Object.entries(summary.diagnosticsByCode)
      .filter(([code]) => code.includes('CONDITION'))))}.`, '',
    '## v4 comparison', '',
    `- Candidate payload comparison: ${JSON.stringify(summary.candidatePayloadComparison)}.`,
    `- Relationship inventory v4/v5: ${JSON.stringify(summary.v4RelationshipsByType)} / ${JSON.stringify(summary.v5RelationshipsByType)}.`,
    `- Existing relationship delta (signature-based): ${JSON.stringify(summary.existingRelationshipsPreserved)}.`, '',
    '## Representative source owners', '',
  ];
  for (const id of ['spell:phb:hold-person', 'spell:phb:lesser-restoration',
    'spell:phb:protection-from-evil-and-good', 'spell:phb:blindness-deafness',
    'spell:phb:ray-of-sickness', 'spell:phb:chill-touch', 'spell:xphb:hold-person']) {
    const record = value.owners.find(item => item.ownerId === id);
    const ownerConditions = record.experimental.mechanics.filter(item => item.type === 'CONDITION');
    const ownerRelations = record.experimental.relationships.filter(item => item.type.startsWith('CONDITION_')
      || item.type === 'SAVE_CONDITION_OUTCOME');
    lines.push(`- \`${id}\`: ${ownerConditions.length} condition mechanics, ${ownerRelations.length} condition relationships; `
      + `condition diagnostics=${record.experimental.diagnostics.filter(item => item.code.includes('CONDITION'))
        .map(item => item.code).join(', ') || 'none'}.`);
  }
  lines.push('', '## Limitations', '',
    'Recognition remains conservative. Condition tags alone do not establish an operation. Choice extraction currently supports one selected alternative; unsupported choice cardinalities and unclear lifecycle links remain diagnostics. A condition mechanic or relationship does not imply full-spell coverage or human approval. All experimental results remain pending review, rights-unreviewed, and unauthorized for publication.', '');
  return lines.join('\n');
}
