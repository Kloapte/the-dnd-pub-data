import { readFile, writeFile } from 'node:fs/promises';
import { join } from 'node:path';

const root = process.cwd();
const manifest = JSON.parse(await readFile(join(root,
  'analysis/mechanics/step-7/generic-first-catalogue-applicability.json'), 'utf8'));
const baseline = JSON.parse(await readFile(join(root,
  'analysis/mechanics/step-7/generic-save-damage-relationship-dry-run-v2-results.json'), 'utf8'));
const baselineByOwner = new Map(baseline.owners.map(owner => [owner.ownerId, owner]));
const owners = [...manifest.owners].sort((a, b) => a.ownerId.localeCompare(b.ownerId));
if (owners.length !== 969 || new Set(owners.map(owner => owner.ownerId)).size !== 969) {
  throw new Error('Pinned spell manifest does not contain 969 unique owners');
}
const baseUrl = (process.env.SPELL_API_BASE_URL ?? 'http://localhost:8081/api/spells').replace(/\/$/, '');
const records = new Array(owners.length);
let cursor = 0;
async function worker() {
  while (true) {
    const index = cursor++;
    if (index >= owners.length) return;
    const owner = owners[index];
    const response = await fetch(baseUrl + '/' + owner.ownerId + '?include=interpretedMechanics', {
      headers: { Authorization: process.env.SPELL_API_AUTHORIZATION },
    });
    if (!response.ok) throw new Error(owner.ownerId + ': API ' + response.status);
    const spell = await response.json();
    const experimental = spell.interpretedMechanics?.experimentalInterpretationV2;
    if (!experimental || experimental.apiVersion !== 2 || experimental.origin !== 'EXPERIMENTAL') {
      throw new Error('Missing v2 interpretation for ' + owner.ownerId);
    }
    const previous = baselineByOwner.get(owner.ownerId)?.experimentalV2;
    const sorted = {
      ...experimental,
      mechanics: [...(experimental.mechanics ?? [])].sort((a, b) => a.id.localeCompare(b.id)),
      relationships: [...(experimental.relationships ?? [])].sort((a, b) => a.id.localeCompare(b.id)),
      evidence: [...(experimental.evidence ?? [])].sort((a, b) => a.id.localeCompare(b.id)),
      diagnostics: [...(experimental.diagnostics ?? [])].sort((a, b) =>
        (a.code + '|' + (a.pointer ?? '') + '|' + (a.mechanicId ?? '')).localeCompare(
          b.code + '|' + (b.pointer ?? '') + '|' + (b.mechanicId ?? ''))),
    };
    records[index] = {
      ownerId: owner.ownerId,
      spellName: spell.name,
      sourceCode: spell.source.source,
      canonical: {
        processingState: spell.interpretedMechanics.processing.state,
        coverageStatus: spell.interpretedMechanics.coverage.status,
        claimCount: spell.interpretedMechanics.interpretations?.length ?? 0,
        claims: (spell.interpretedMechanics.interpretations ?? []).map(claim => ({
          id: typeof claim.id === 'string' ? claim.id : claim.id?.value,
          hash: claim.revision.contentSha256,
          family: claim.payload.kind,
        })).sort((a, b) => a.id.localeCompare(b.id)),
      },
      experimental: sorted,
      previousSaveDamage: {
        relationshipCount: (previous?.relationships ?? []).filter(r => r.type === 'SAVING_THROW_DAMAGE_OUTCOME').length,
        currentSaveDamageCount: sorted.relationships.filter(r => r.type === 'SAVING_THROW_DAMAGE_OUTCOME').length,
        saveRelationshipChanges: compareRelations(
          (previous?.relationships ?? []).filter(r => r.type === 'SAVING_THROW_DAMAGE_OUTCOME'),
          sorted.relationships.filter(r => r.type === 'SAVING_THROW_DAMAGE_OUTCOME')),
        payloadComparison: comparePayloads(previous?.mechanics ?? [], sorted.mechanics),
      },
    };
  }
}
await Promise.all(Array.from({ length: 8 }, worker));
if (records.some(record => record == null)) throw new Error('The catalogue run omitted an owner');
const resources = [...new Map(records.map(record => [record.experimental.metadata.sourceResource, {
  resourcePath: record.experimental.metadata.sourceResource,
  sha256: record.experimental.metadata.sourceSha256,
  datasetVersion: record.experimental.metadata.datasetVersion,
}])).values()].sort((a, b) => a.resourcePath.localeCompare(b.resourcePath));
if (resources.length !== 18) throw new Error('Expected 18 resources, found ' + resources.length);
const relationships = records.flatMap(r => r.experimental.relationships);
const attackRelations = relationships.filter(r => r.type === 'ATTACK_DAMAGE_OUTCOME');
const saveRelations = relationships.filter(r => r.type === 'SAVING_THROW_DAMAGE_OUTCOME');
const outcomeRules = relationships.flatMap(r => [...(r.outcomes ?? []), ...(r.attackOutcomes ?? [])]);
const mechanics = records.flatMap(r => r.experimental.mechanics);
const summary = {
  ownerCount: records.length,
  resourceCount: resources.length,
  ownersWithMechanics: records.filter(r => r.experimental.mechanics.length > 0).length,
  ownersWithoutMechanics: records.filter(r => r.experimental.mechanics.length === 0).length,
  candidateMechanics: mechanics.length,
  outcomes: countBy(records, r => r.experimental.outcome),
  mechanicsByFamily: countBy(mechanics, m => m.type),
  ownersWithAttackCandidates: records.filter(r => r.experimental.mechanics.some(m => m.type === 'ATTACK')).length,
  attackCandidates: mechanics.filter(m => m.type === 'ATTACK').length,
  ownersWithAttackDamageRelationships: new Set(attackRelations.map(r => r.ownerId.value)).size,
  attackDamageRelationships: attackRelations.length,
  attackOutcomeRules: countBy(attackRelations.flatMap(r => r.attackOutcomes ?? []), rule => rule.outcome + ':' + rule.application),
  attackRelationsByOwner: countBy(attackRelations, r => r.ownerId.value),
  ownersWithMultipleAttackDamageRelations: Object.values(countBy(attackRelations, r => r.ownerId.value))
    .filter(count => count > 1).length,
  relationshipsByType: countBy(relationships, r => r.type),
  allOutcomeRules: countBy(outcomeRules, rule => rule.outcome + ':' + rule.application),
  diagnostics: countBy(records.flatMap(r => r.experimental.diagnostics), d => d.code),
  sourceDistribution: countBy(records, r => r.sourceCode),
  saveRelationshipBaseline: compareRelations(baseline.owners.flatMap(r => r.experimentalV2.relationships ?? [])
    .filter(r => r.type === 'SAVING_THROW_DAMAGE_OUTCOME'), saveRelations),
  priorBaselineMechanics: baseline.owners.reduce((sum, r) => sum + (r.experimentalV2.mechanics?.length ?? 0), 0),
  experimentalPayloadComparison: records.reduce((total, r) => {
    total.exactMatches += r.previousSaveDamage.payloadComparison.exactMatches;
    total.removedCount += r.previousSaveDamage.payloadComparison.removedCount;
    total.addedCount += r.previousSaveDamage.payloadComparison.addedCount;
    return total;
  }, { exactMatches: 0, removedCount: 0, addedCount: 0 }),
  canonicalOwners: records.filter(r => r.canonical.processingState === 'PROCESSED').length,
  canonicalClaims: records.reduce((sum, r) => sum + r.canonical.claimCount, 0),
  canonicalPayloadComparison: compareClaims(baseline.owners.flatMap(r => r.canonical?.claims ?? []),
    records.flatMap(r => r.canonical.claims)),
  canonicalClaimsExactlyStable: records.every(r => {
    const old = baselineByOwner.get(r.ownerId)?.canonical?.claims ?? [];
    return JSON.stringify(old) === JSON.stringify(r.canonical.claims);
  }),
};
const artifact = { schemaVersion: 3, artifactVersion: 'generic-attack-damage-relationship-dry-run-v3',
  engineVersion: 'generic-strategy-pipeline-v2.2', datasetVersion: '5eTools v2.37.0',
  ownerCount: records.length, resourceCount: resources.length, resources, summary, owners: records };
await writeFile(join(root, 'analysis/mechanics/step-7/generic-attack-damage-relationship-dry-run-v3-results.json'),
  JSON.stringify(artifact, null, 2) + '\n', 'utf8');
await writeFile(join(root, 'analysis/mechanics/step-7/7.5-generic-attack-damage-relationship-dry-run.md'), render(artifact), 'utf8');
process.stdout.write(JSON.stringify(summary, null, 2) + '\n');

function countBy(items, keyOf) {
  const counts = new Map();
  for (const item of items) { const key = keyOf(item); counts.set(key, (counts.get(key) ?? 0) + 1); }
  return Object.fromEntries([...counts].sort(([a], [b]) => a.localeCompare(b)));
}
function compareRelations(before, after) {
  const signature = r => JSON.stringify({ source: r.sourceMechanicId, target: r.targetMechanicId, outcomes: r.outcomes });
  const left = countBy(before, signature);
  const right = countBy(after, signature);
  const keys = new Set([...Object.keys(left), ...Object.keys(right)]);
  const added = [], removed = [];
  for (const key of keys) {
    for (let i = left[key] ?? 0; i < (right[key] ?? 0); i++) added.push(JSON.parse(key));
    for (let i = right[key] ?? 0; i < (left[key] ?? 0); i++) removed.push(JSON.parse(key));
  }
  return { beforeCount: before.length, afterCount: after.length, addedCount: added.length, removedCount: removed.length,
    added, removed };
}
function comparePayloads(before, after) {
  const key = item => JSON.stringify({ type: item.type ?? item.family, properties: item.properties ?? item.payload });
  const remaining = before.map(key);
  let exactMatches = 0;
  const additions = [];
  for (const item of after) {
    const value = key(item); const index = remaining.indexOf(value);
    if (index >= 0) { remaining.splice(index, 1); exactMatches++; } else additions.push(value);
  }
  return { exactMatches, removedCount: remaining.length, addedCount: additions.length };
}
function compareClaims(before, after) {
  const key = item => JSON.stringify({ id: item.id, hash: item.hash, family: item.family });
  const remaining = before.map(key);
  let exactMatches = 0;
  for (const item of after) {
    const value = key(item); const index = remaining.indexOf(value);
    if (index >= 0) { remaining.splice(index, 1); exactMatches++; }
  }
  return { exactMatches, removedCount: remaining.length, addedCount: after.length - exactMatches };
}
function render(value) {
  const s = value.summary;
  const table = (object, name) => '| ' + name + ' | Count |\n| --- | ---: |\n' +
    Object.entries(object).map(([key, count]) => '| ' + key + ' | ' + count + ' |').join('\n');
  const byId = new Map(value.owners.map(r => [r.ownerId, r]));
  const examples = ['spell:phb:chill-touch', 'spell:phb:scorching-ray', 'spell:phb:melf-s-acid-arrow',
    'spell:xge:ice-knife', 'spell:xge:steel-wind-strike', 'spell:xphb:chromatic-orb'];
  return '# Attack to Damage Relationship Dry Run v3\n\n' +
    '**Status:** experimental only; canonical claims and review/licensing/publication decisions remain unchanged.\n\n' +
    '## Catalogue summary\n\n- Owners: ' + s.ownerCount + ' across ' + s.resourceCount + ' pinned resources.\n' +
    '- Candidate mechanics: ' + s.candidateMechanics +
    ' across ' + s.ownersWithMechanics + ' owners; no candidates: ' + s.ownersWithoutMechanics + '.\n' +
    '- Attack candidates: ' + s.attackCandidates + ' across ' + s.ownersWithAttackCandidates + ' owners.\n' +
    '- Attack→damage relationships: ' + s.attackDamageRelationships + ' across ' + s.ownersWithAttackDamageRelationships + ' owners.\n' +
    '- Save→damage relationships: ' + saveRelations.length + '; baseline was ' + s.saveRelationshipBaseline.beforeCount +
    ' (added ' + s.saveRelationshipBaseline.addedCount + ', removed ' + s.saveRelationshipBaseline.removedCount + ').\n\n' +
    table(s.outcomes, 'Outcome') + '\n\n' + table(s.mechanicsByFamily, 'Mechanic family') + '\n\n' +
    table(s.relationshipsByType, 'Relationship family') + '\n\n' +
    table(s.attackOutcomeRules, 'Attack outcome/application') + '\n\n' +
    '## Diagnostics\n\n' + table(s.diagnostics, 'Diagnostic') + '\n\n' +
    '## Source distribution\n\n' + table(s.sourceDistribution, 'Source') + '\n\n' +
    '## Representative source-qualified cases\n\n' + examples.map(id => {
      const r = byId.get(id); const relations = r.experimental.relationships;
      return '- `' + id + '`: ' + relations.filter(x => x.type === 'ATTACK_DAMAGE_OUTCOME').length +
        ' attack/damage relationship(s); ' + relations.filter(x => x.type === 'SAVING_THROW_DAMAGE_OUTCOME').length +
        ' save/damage relationship(s); diagnostics=' + r.experimental.diagnostics.map(d => d.code).filter(c => c.includes('ATTACK')).join(', ');
    }).join('\n') + '\n\n' +
    '## Baseline and canonical comparison\n\n- Previous save/damage baseline: ' + s.priorBaselineMechanics +
    ' mechanics, 216 relationships across 185 owners per the v2.1 report.\n- Current source-preserving payload comparison: ' +
    JSON.stringify(s.canonicalPayloadComparison) + '.\n- Canonical claim payload/identity snapshot stable across this run: ' +
    s.canonicalClaimsExactlyStable + '; canonical owners=' + s.canonicalOwners + ', claims=' + s.canonicalClaims + '.\n\n' +
    '## Limitations\n\n- An attack is emitted only when its attack kind and singular/group count are supported by the local authored fragment.\n' +
    '- Attacks expressed once per ray or target across sibling entries remain unmodeled until shared source context is resolved generically.\n' +
    '- Conditional chains and delayed timing are not executed; delayed damage keeps an explicit timing diagnostic.\n' +
    '- Relationship evidence identifies source fragments and exact source revisions; current evidence spans cover the normalized authored fragment.\n' +
    '- Every candidate remains experimental, PARTIAL, PENDING_REVIEW, NEEDS_REVIEW for licensing, and unauthorized for publication.\n';
}
