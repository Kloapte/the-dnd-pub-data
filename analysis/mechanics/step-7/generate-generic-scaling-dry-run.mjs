import { readFile, writeFile } from 'node:fs/promises';
import { join } from 'node:path';

const root = process.cwd();
const previous = JSON.parse(await readFile(join(root,
  'analysis/mechanics/step-7/generic-attack-damage-relationship-dry-run-v3-results.json'), 'utf8'));
const owners = [...previous.owners].map(owner => owner.ownerId).sort((a, b) => a.localeCompare(b));
if (owners.length !== 969 || new Set(owners).size !== owners.length) {
  throw new Error('Step 7.5 v3 baseline does not contain exactly 969 unique owners');
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
    if (!interpretation || interpretation.metadata?.engineVersion !== 'generic-strategy-pipeline-v2.3') {
      throw new Error(ownerId + ': expected experimental pipeline v2.3');
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
      previousV3: old.experimental,
      experimental: sortProjection(interpretation),
    };
  }
}
await Promise.all(Array.from({ length: 8 }, worker));
if (records.some(item => item == null)) throw new Error('Scaling dry run omitted an owner');

const mechanics = records.flatMap(record => record.experimental.mechanics);
const relations = records.flatMap(record => record.experimental.relationships);
const scaling = mechanics.filter(item => item.type === 'SCALING');
const scalingRelations = relations.filter(item => item.type === 'SCALING_APPLIES_TO');
const previousMechanics = records.flatMap(record => record.previousV3.mechanics);
const previousRelations = records.flatMap(record => record.previousV3.relationships);
const sourceDistribution = countBy(records, item => item.sourceCode);
const resources = [...new Map(records.map(item => [item.experimental.metadata.sourceResource, {
  resourcePath: item.experimental.metadata.sourceResource,
  sha256: item.experimental.metadata.sourceSha256,
  datasetVersion: item.experimental.metadata.datasetVersion,
}])).values()].sort((a, b) => a.resourcePath.localeCompare(b.resourcePath));
const previousSave = previousRelations.filter(item => item.type === 'SAVING_THROW_DAMAGE_OUTCOME');
const currentSave = relations.filter(item => item.type === 'SAVING_THROW_DAMAGE_OUTCOME');
const previousAttack = previousRelations.filter(item => item.type === 'ATTACK_DAMAGE_OUTCOME');
const currentAttack = relations.filter(item => item.type === 'ATTACK_DAMAGE_OUTCOME');
const summary = {
  ownerCount: records.length,
  resourceCount: resources.length,
  ownersWithMechanics: records.filter(item => item.experimental.mechanics.length > 0).length,
  ownersWithoutMechanics: records.filter(item => item.experimental.mechanics.length === 0).length,
  candidateOutcomes: countBy(records, item => item.experimental.outcome),
  mechanicsByFamily: countBy(mechanics, item => item.type),
  ownersWithScaling: new Set(scaling.map(item => records.find(owner =>
    owner.experimental.mechanics.some(mechanic => mechanic.id === item.id)).ownerId)).size,
  scalingMechanics: scaling.length,
  scalingByAxis: countBy(scaling, item => item.properties.axis),
  scalingBySourceAndAxis: countBy(scaling, item => {
    const owner = records.find(record => record.experimental.mechanics.some(mechanic => mechanic.id === item.id));
    return owner.sourceCode + ':' + item.properties.axis;
  }),
  scalingByProgression: countBy(scaling, item => item.properties.progressionType),
  scalingByQuantity: countBy(scaling, item => item.properties.quantity),
  scalingRelationships: scalingRelations.length,
  ownersWithScalingRelationships: new Set(scalingRelations.map(item => item.ownerId.value)).size,
  scalingRelationshipsByQuantityAndTarget: countBy(scalingRelations, item => {
    const target = mechanics.find(mechanic => mechanic.id === item.targetMechanicId);
    return item.scalingRule.affectedQuantity + '->' + (target?.type ?? 'MISSING');
  }),
  unboundScalingMechanics: scaling.filter(item => !scalingRelations.some(rel => rel.sourceMechanicId === item.id)).length,
  scalingDiagnostics: countBy(records.flatMap(item => item.experimental.diagnostics)
    .filter(item => item.code.startsWith('SCALING_')), item => item.code),
  relationshipsByType: countBy(relations, item => item.type),
  saveRelationshipComparison: compareRelations(previousSave, currentSave),
  attackRelationshipComparison: compareRelations(previousAttack, currentAttack),
  priorV3CandidateMechanics: previousMechanics.length,
  currentCandidateMechanics: mechanics.length,
  mechanicPayloadComparison: comparePayloads(previousMechanics, mechanics),
  canonicalOwners: records.filter(item => item.canonicalCurrent.processingState === 'PROCESSED').length,
  canonicalClaims: records.reduce((total, item) => total + item.canonicalCurrent.claimCount, 0),
  canonicalClaimsExactlyStable: records.every(item =>
    JSON.stringify(item.canonical.claims) === JSON.stringify(item.canonicalCurrent.claims)),
  sourceDistribution,
};
summary.unboundScalingByReason = countBy(records.filter(item => item.experimental.mechanics.some(mechanic =>
  mechanic.type === 'SCALING' && !scalingRelations.some(rel => rel.sourceMechanicId === mechanic.id)))
  .flatMap(item => item.experimental.diagnostics.filter(diagnostic => diagnostic.code.startsWith('SCALING_'))),
item => item.code);

const artifact = {
  schemaVersion: 4,
  artifactVersion: 'generic-scaling-dry-run-v4',
  engineVersion: 'generic-strategy-pipeline-v2.3',
  datasetVersion: '5eTools v2.37.0',
  ownerCount: records.length,
  resourceCount: resources.length,
  resources,
  summary,
  owners: records,
};
const outputJson = join(root, 'analysis/mechanics/step-7/generic-scaling-dry-run-v4-results.json');
const outputMarkdown = join(root, 'analysis/mechanics/step-7/7.5-generic-scaling-dry-run-v4.md');
await writeFile(outputJson, JSON.stringify(artifact, null, 2) + '\n', 'utf8');
await writeFile(outputMarkdown, render(artifact), 'utf8');
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
function compareRelations(before, after) {
  const signature = item => JSON.stringify({ type: item.type, source: item.sourceMechanicId,
    target: item.targetMechanicId, outcomes: item.outcomes, attackOutcomes: item.attackOutcomes });
  const oldCounts = countBy(before, signature);
  const newCounts = countBy(after, signature);
  const keys = new Set([...Object.keys(oldCounts), ...Object.keys(newCounts)]);
  let added = 0, removed = 0;
  for (const key of keys) {
    added += Math.max(0, (newCounts[key] ?? 0) - (oldCounts[key] ?? 0));
    removed += Math.max(0, (oldCounts[key] ?? 0) - (newCounts[key] ?? 0));
  }
  return { before: before.length, after: after.length, added, removed };
}
function comparePayloads(before, after) {
  const key = item => JSON.stringify({ type: item.type, properties: item.properties });
  const remaining = before.map(key);
  let exact = 0;
  let added = 0;
  for (const item of after) {
    const value = key(item);
    const index = remaining.indexOf(value);
    if (index >= 0) { remaining.splice(index, 1); exact++; } else added++;
  }
  return { exactMatches: exact, removed: remaining.length, added };
}
function render(value) {
  const s = value.summary;
  const lines = [
    '# Generic Scaling Dry Run v4', '',
    '**Status:** experimental only. Canonical interpretations, review, licensing, and publication states are unchanged.', '',
    '## Catalogue summary', '',
    `- Owners/resources: ${value.ownerCount} / ${value.resourceCount}.`,
    `- Candidate owners/mechanics: ${s.ownersWithMechanics} / ${s.currentCandidateMechanics}; no mechanics: ${s.ownersWithoutMechanics}.`,
    `- Scaling mechanics/owners: ${s.scalingMechanics} / ${s.ownersWithScaling}.`,
    `- Scaling relationships/owners: ${s.scalingRelationships} / ${s.ownersWithScalingRelationships}.`,
    `- Unbound scaling mechanics: ${s.unboundScalingMechanics}.`,
    `- Candidate outcomes: ${JSON.stringify(s.candidateOutcomes)}.`, '',
    '## Scaling distribution', '',
    `- Axis: ${JSON.stringify(s.scalingByAxis)}.`,
    `- Source and axis: ${JSON.stringify(s.scalingBySourceAndAxis)}.`,
    `- Progression: ${JSON.stringify(s.scalingByProgression)}.`,
    `- Quantity: ${JSON.stringify(s.scalingByQuantity)}.`,
    `- Relationships by affected quantity and target family: ${JSON.stringify(s.scalingRelationshipsByQuantityAndTarget)}.`,
    `- Unbound reason diagnostics: ${JSON.stringify(s.unboundScalingByReason)}.`,
    `- Scaling diagnostics: ${JSON.stringify(s.scalingDiagnostics)}.`, '',
    '## Existing relationship preservation', '',
    `- Save → damage: ${JSON.stringify(s.saveRelationshipComparison)}.`,
    `- Attack → damage: ${JSON.stringify(s.attackRelationshipComparison)}.`,
    `- All relationship families: ${JSON.stringify(s.relationshipsByType)}.`, '',
    '## v3 comparison and canonical boundary', '',
    `- v3 mechanics baseline/current: ${s.priorV3CandidateMechanics} / ${s.currentCandidateMechanics}.`,
    `- Payload comparison: ${JSON.stringify(s.mechanicPayloadComparison)}.`,
    `- Canonical claims: ${s.canonicalClaims} across ${s.canonicalOwners} processed owners; exact snapshot stable: ${s.canonicalClaimsExactlyStable}.`,
    `- Source distribution: ${JSON.stringify(s.sourceDistribution)}.`, '',
    '## Representative spell findings', '',
  ];
  for (const id of ['spell:phb:fireball', 'spell:phb:healing-word', 'spell:phb:scorching-ray',
    'spell:phb:eldritch-blast', 'spell:phb:ray-of-frost', 'spell:phb:magic-missile', 'spell:xge:ice-knife']) {
    const owner = value.owners.find(item => item.ownerId === id);
    const claims = owner.experimental.mechanics.filter(item => item.type === 'SCALING');
    const rels = owner.experimental.relationships.filter(item => item.type === 'SCALING_APPLIES_TO');
    lines.push(`- \`${id}\`: ${claims.length} scaling mechanic(s), ${rels.length} scaling relationship(s); `
      + `diagnostics=${owner.experimental.diagnostics.filter(item => item.code.startsWith('SCALING_'))
        .map(item => item.code).join(', ') || 'none'}.`);
  }
  lines.push('', '## Interpretation limits', '',
    'A typed progression is not evaluated. Relationships are emitted only when source wording and extracted mechanic type/qualifiers identify exactly one target. Unbound progressions remain visible with diagnostics. Candidate recognition does not establish full-spell completeness or correctness. All candidates remain experimental, pending review, rights-unreviewed, and unauthorized for publication.', '');
  return lines.join('\n');
}
