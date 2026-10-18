import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

const directory = 'analysis/mechanics/public-v1/spells';
const read = (path) => JSON.parse(fs.readFileSync(path, 'utf8').replace(/^\uFEFF/, ''));
const hash = (value) => crypto.createHash('sha256').update(value).digest('hex');
const runtime = read(`${directory}/runtime-evidence.json`);
const pinned = read('analysis/mechanics/batch-10a-object-membership/pre-implementation-hashes.json');
const sourcePaths = Object.keys(pinned)
  .filter((path) => path.startsWith('src/main/resources/data/') && path.endsWith('.json'))
  .sort();
assert.equal(sourcePaths.length, 175);
for (const path of sourcePaths) {
  assert.equal(hash(fs.readFileSync(path)), pinned[path], `Pinned source changed: ${path}`);
}

function group(values, key) {
  const counts = new Map();
  for (const value of values) {
    const name = key(value);
    counts.set(name, (counts.get(name) ?? 0) + 1);
  }
  return Object.fromEntries([...counts].sort(([left], [right]) => left.localeCompare(right, 'en')));
}

function slug(value) {
  return value.normalize('NFKD').replace(/\p{M}+/gu, '').toLowerCase()
    .replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
}

function at(document, pointer) {
  return pointer.split('/').slice(1).reduce((value, segment) =>
    value[segment.replace(/~1/g, '/').replace(/~0/g, '~')], document);
}

const documents = new Map();
const owners = new Map();
const fields = [
  'level', 'school', 'time', 'range', 'components', 'duration', 'meta',
  'abilityCheck', 'affectsCreatureType', 'areaTags', 'conditionImmune', 'conditionInflict',
  'damageImmune', 'damageInflict', 'damageResist', 'damageVulnerable', 'miscTags',
  'savingThrow', 'spellAttack', 'scalingLevelDice', 'entries', 'entriesHigherLevel',
  'srd', 'srd52', 'basicRules', 'basicRules2024', 'additionalSources', 'otherSources',
  'referenceSources', 'reprintedAs',
];
const fieldInventory = Object.fromEntries(fields.map((field) => [field, []]));
let transformDeclarations = 0;
for (const name of fs.readdirSync('src/main/resources/data/spells').filter((name) => name.endsWith('.json')).sort()) {
  const resource = `data/spells/${name}`;
  const bytes = fs.readFileSync(`src/main/resources/${resource}`);
  assert.equal(hash(bytes), runtime.resourceHashes[resource]);
  const document = JSON.parse(bytes);
  documents.set(resource, document);
  for (const [index, source] of document.spell.entries()) {
    const id = `spell:${slug(source.source)}:${slug(source.name)}`;
    assert.ok(!owners.has(id), `Duplicate source identity ${id}`);
    const owner = { id, name: source.name, source: source.source, resource,
      pointer: `/spell/${index}`, sourceData: source };
    owners.set(id, owner);
    for (const field of fields) {
      if (Object.hasOwn(source, field)) {
        const value = source[field];
        fieldInventory[field].push({ id, value,
          type: value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value });
      }
    }
    for (const field of ['_copy', '_mod', '_versions', '_templates', '_abstract', '_implementations']) {
      transformDeclarations += Object.hasOwn(source, field) ? 1 : 0;
    }
  }
}
assert.equal(documents.size, 18);
assert.equal(owners.size, 969);
assert.equal(transformDeclarations, 0);
assert.deepEqual([...owners.keys()].sort(), runtime.records.map((row) => row.id).sort());

for (const observation of [...runtime.formulas, ...runtime.auxiliarySyntax]) {
  const text = at(documents.get(observation.resource), observation.pointer);
  const owner = owners.get(observation.spellId);
  assert.equal(owner.resource, observation.resource);
  assert.equal(owner.pointer, observation.ownerPointer);
  assert.equal(hash(text), observation.textSha256);
  assert.equal(text.slice(...observation.spanUtf16), observation.originalTag);
  assert.equal(observation.origin, 'AUTHORED');
}

const formulas = runtime.formulas;
const byId = new Map(runtime.records.map((record) => [record.id, record]));
const ownerCount = (values) => new Set(values.map((value) => value.spellId)).size;
const simple = formulas.filter((value) => ['damage', 'dice'].includes(value.tag)
  && value.arguments.length === 1
  && ['DICE_NOTATION', 'SIGNED_INTEGER', 'UNSIGNED_INTEGER'].includes(value.expressionKind));
const simpleKeys = new Set(simple.map((value) => `${value.spellId}:${value.occurrenceIndex}`));
const formulaOwners = runtime.records.filter((record) => record.formulaCount > 0);
const whollySimpleOwners = formulaOwners.filter((owner) => formulas
  .filter((value) => value.spellId === owner.id)
  .every((value) => simpleKeys.has(`${value.spellId}:${value.occurrenceIndex}`)));

const fieldCoverage = Object.fromEntries(Object.entries(fieldInventory).map(([field, rows]) => [field, {
  ownerCount: rows.length,
  types: group(rows, (row) => row.type),
  nullCount: rows.filter((row) => row.value === null).length,
  emptyCount: rows.filter((row) => row.value === '' || (Array.isArray(row.value) && row.value.length === 0)).length,
}]));
const publicPathCounts = group(runtime.records.flatMap((row) => row.publicPaths), (value) => value);
const sourceRecords = [...owners.values()].map((owner) => owner.sourceData);
const scalingValues = sourceRecords.flatMap((source) => {
  const groups = source.scalingLevelDice === undefined ? []
    : Array.isArray(source.scalingLevelDice) ? source.scalingLevelDice : [source.scalingLevelDice];
  return groups.flatMap((value) => Object.entries(value.scaling).map(([levelKey, formula]) => ({ levelKey, formula })));
});
const families = Object.fromEntries(['damage', 'dice', 'scaledamage', 'scaledice'].map((tag) => {
  const selected = formulas.filter((formula) => formula.tag === tag);
  return [tag, { observations: selected.length, owners: ownerCount(selected),
    argumentCounts: group(selected, (value) => String(value.arguments.length)),
    expressionKinds: group(selected, (value) => value.expressionKind) }];
}));

const coverage = {
  dataset: '5eTools v2.37.0',
  authoredSpells: owners.size,
  normalizedSpells: runtime.normalizedSpellCount,
  spellSourceFiles: documents.size,
  pinnedSourceFilesVerified: sourcePaths.length,
  sourceDistribution: group([...owners.values()], (owner) => owner.source),
  transformDeclarations,
  fieldCoverage,
  observedRootFields: [...new Set(sourceRecords.flatMap((source) => Object.keys(source)))].sort(),
  coreDetails: {
    publicMechanicsOwners: publicPathCounts.mechanics,
    publicCoreMetadataOwners: runtime.records.filter((row) =>
      ['id', 'name', 'source', 'level', 'school', 'castingTimes', 'range', 'components', 'durations']
        .every((field) => row.publicPaths.includes(field))).length,
    publicMechanicsAbsentOwners: runtime.records.length - publicPathCounts.mechanics,
    multipleCastingTimeOwners: sourceRecords.filter((source) => source.time.length > 1).length,
    castingConditionOwners: sourceRecords.filter((source) => source.time.some((time) => Object.hasOwn(time, 'condition'))).length,
    castingNoteOwners: sourceRecords.filter((source) => source.time.some((time) => Object.hasOwn(time, 'note'))).length,
    rangeTypes: group(sourceRecords, (source) => source.range.type),
    nonPointNumericRangeOwners: sourceRecords.filter((source) => source.range.type !== 'point'
      && typeof source.range.distance?.amount === 'number').length,
    concentrationTrueOwners: sourceRecords.filter((source) => source.duration.some((duration) => duration.concentration === true)).length,
    materialShapes: group(sourceRecords.filter((source) => Object.hasOwn(source.components, 'm')),
      (source) => typeof source.components.m),
    scalingValues: scalingValues.length,
    scalingLevelKeys: group(scalingValues, (value) => value.levelKey),
  },
  currentPublicPathCounts: publicPathCounts,
  mechanicsTraceComparisons: runtime.mechanicsTraceComparisons,
  formulas: { observations: formulas.length, owners: ownerCount(formulas), families,
    sectionCounts: group(formulas, (value) => value.section),
    expressionKinds: group(formulas, (value) => value.expressionKind),
    opaqueSpellings: group(formulas.filter((value) => value.expressionKind === 'OPAQUE_OR_SYMBOLIC'),
      (value) => value.expression),
    tableObservations: formulas.filter((value) => value.hierarchy.some((node) => node.authoredType === 'table')).length,
    nestedTagCount: formulas.reduce((sum, value) => sum + value.nestedTagCount, 0),
    auxiliaryCounts: group(runtime.auxiliarySyntax, (value) => value.tag),
    auxiliaryOwners: ownerCount(runtime.auxiliarySyntax),
    noFormulaOwners: runtime.records.filter((record) => record.formulaCount === 0).length,
    unknownEffectRoleDiceTags: formulas.filter((value) => ['dice', 'scaledice'].includes(value.tag)).length,
    unknownEffectRoleDiceOwners: ownerCount(formulas.filter((value) => ['dice', 'scaledice'].includes(value.tag))),
    scalingSyntaxOwners: ownerCount(formulas.filter((value) => value.tag.startsWith('scale'))),
    hypotheticalLiteralSubset: { approvalStatus: 'REQUIRES_PUBLICATION_APPROVAL',
      observations: simple.length, owners: ownerCount(simple), whollyRepresentedOwners: whollySimpleOwners.length,
      rules: 'One argument; damage/dice tag; existing bounded numeric/dice lexical shape; no scaling or opaque token' },
    spanFailures: 0,
  },
  flagsOnFormulaOwners: Object.fromEntries(['srd', 'srd52', 'basicRules', 'basicRules2024'].map((flag) => [flag,
    formulaOwners.filter((owner) => Object.hasOwn(owners.get(owner.id).sourceData, flag)).length])),
  records: [...owners.values()].sort((a, b) => a.id.localeCompare(b.id, 'en')).map((owner) => ({
    id: owner.id, resource: owner.resource, pointer: owner.pointer,
    formulaCount: byId.get(owner.id).formulaCount,
    publicMechanicsPresent: byId.get(owner.id).publicPaths.includes('mechanics'),
  })),
};
assert.deepEqual(Object.fromEntries(Object.entries(families).map(([tag, value]) => [tag, value.observations])),
  { damage: 642, dice: 126, scaledamage: 148, scaledice: 14 });
assert.equal(coverage.formulas.owners, 422);

const gold = [];
function label(spellId, predicate, decision, claim, expected, reason) {
  const observation = formulas.find((value) => value.spellId === spellId && predicate(value));
  assert.ok(observation, `Missing proposed gold example: ${spellId}: ${claim}`);
  gold.push({ id: `S${String(gold.length + 1).padStart(2, '0')}`, spellId,
    resource: observation.resource, pointer: observation.pointer, spanUtf16: observation.spanUtf16,
    occurrenceIndex: observation.occurrenceIndex, token: observation.originalTag,
    decision, claim, expected, reason });
}

label('spell:phb:fireball', (value) => value.tag === 'damage', 'ACCEPT', 'TAG_KIND', 'DAMAGE_TAG',
  'Damage notation is explicit; an applied damage effect is not.');
label('spell:phb:fireball', (value) => value.tag === 'scaledamage', 'ACCEPT', 'SCALING_ARGUMENTS', null,
  'Preserve all three exact syntax arguments without slot-level arithmetic.');
label('spell:xphb:fireball', (value) => value.tag === 'damage', 'ACCEPT', 'SOURCE_OWNER', 'spell:xphb:fireball',
  'PHB and XPHB ownership remains separate.');
label('spell:phb:healing-word', (value) => value.tag === 'dice', 'ACCEPT', 'TAG_KIND', 'DICE_TAG',
  'The tag contains a formula; it does not specify healing semantics.');
label('spell:phb:healing-word', (value) => value.tag === 'dice', 'ABSTAIN', 'HEALING_APPLICATION', null,
  'Healing role and spellcasting modifier require prose.');
label('spell:phb:false-life', () => true, 'ABSTAIN', 'TEMPORARY_HP_APPLICATION', null,
  'Temporary hit points are not a role encoded in the dice tag.');
label('spell:phb:magic-missile', () => true, 'ABSTAIN', 'PROJECTILE_TOTAL', null,
  'Do not multiply the formula or infer target distribution.');
label('spell:phb:scorching-ray', (value) => value.tag === 'damage', 'ABSTAIN', 'ATTACK_COUNT', null,
  'The formula does not specify number of rays or attack rolls.');
label('spell:tce:booming-blade', (value) => value.occurrenceIndex === 0, 'ACCEPT', 'SOURCE_LOCATION', null,
  'Keep each authored occurrence separate even when expressions repeat.');
label('spell:tce:booming-blade', (value) => value.occurrenceIndex === 1, 'ABSTAIN', 'MERGED_DAMAGE_EFFECT', null,
  'Distinct source clauses must not become one effect.');
label('spell:phb:prismatic-spray', (value) => value.expression === 'd8', 'ACCEPT', 'LEXICAL_KIND', 'OPAQUE_OR_SYMBOLIC',
  'The existing classifier does not insert an omitted die count.');
label('spell:phb:prismatic-spray', (value) => value.hierarchy.some((node) => node.authoredType === 'entries'),
  'ABSTAIN', 'RESULT_BRANCH_APPLICATION', null, 'Named entry nesting does not select an outcome for a target.');
label('spell:phb:animate-objects', (value) => value.arguments.length === 2, 'REJECT', 'SECOND_ARGUMENT_IS_EFFECT_VALUE',
  null, 'The extra numeric argument is source table presentation, not an evaluated effect amount or caster bonus.');
label('spell:phb:confusion', (value) => value.pointer.includes('/colLabels/'), 'ACCEPT', 'TABLE_COLUMN_CONTEXT',
  'TABLE_COLUMN_LABEL', 'A header formula must remain distinguishable from formula syntax in a body or row.');
label('spell:phb:lightning-arrow', (value) => value.expression.includes(';'), 'ACCEPT', 'LEXICAL_KIND',
  'OPAQUE_OR_SYMBOLIC', 'Preserve semicolon alternatives; do not split them into effects.');
label('spell:phb:control-weather', (value) => value.expression.includes('×'), 'ACCEPT', 'EXACT_TOKEN',
  '1d4 × 10', 'Do not rewrite Unicode multiplication or evaluate the expression.');
label('spell:xphb:heal', (value) => value.expression === '70', 'ACCEPT', 'LEXICAL_KIND', 'UNSIGNED_INTEGER',
  'A scaling tag can have a numeric base; it is not necessarily damage.');
label('spell:xphb:heal', (value) => value.expression === '70', 'REJECT', 'SCALEDICE_IS_DAMAGE', null,
  'Dice/scaling tag names do not establish an applied damage role.');
label('spell:phb:acid-splash', (value) => value.tag === 'damage', 'REJECT', 'SRD_FLAG_IS_APPROVAL', null,
  'A source flag does not verify the licensed passage or approve this field.');
gold.push({ id: `S${String(gold.length + 1).padStart(2, '0')}`, spellId: 'spell:phb:shield',
  decision: 'ACCEPT', claim: 'NO_AUTHORED_FORMULA_OBSERVATION', expected: 0,
  resource: owners.get('spell:phb:shield').resource, pointer: owners.get('spell:phb:shield').pointer,
  reason: 'No observed formula tags does not mean no mechanical rules.' });
gold.push({ id: `S${String(gold.length + 1).padStart(2, '0')}`, spellId: 'spell:phb:shield',
  decision: 'REJECT', claim: 'NO_FORMULAS_MEANS_NO_EFFECTS', expected: null,
  reason: 'A formula-presence status describes only the bounded authored index.' });
const zero = runtime.auxiliarySyntax.find((value) => value.originalTag === '{@d20 0}');
assert.ok(zero);
gold.push({ id: `S${String(gold.length + 1).padStart(2, '0')}`, spellId: zero.spellId,
  resource: zero.resource, pointer: zero.pointer, spanUtf16: zero.spanUtf16, token: zero.originalTag,
  decision: 'ACCEPT', claim: 'AUXILIARY_ZERO_MODIFIER', expected: '0',
  reason: 'Retain meaningful zero, but do not count this auxiliary tag in the 930 formulas.' });

fs.writeFileSync(`${directory}/coverage.json`, `${JSON.stringify(coverage, null, 2)}\n`);
fs.writeFileSync(`${directory}/gold-set.json`, `${JSON.stringify({
  status: 'PROPOSED_SOURCE_REVIEW_LABELS_NOT_INDEPENDENTLY_ADJUDICATED',
  purpose: 'Syntax, ownership, publication and semantic-abstention acceptance criteria; no gameplay accuracy claim',
  labels: gold,
}, null, 2)}\n`);
console.log(JSON.stringify({ authored: owners.size, formulas: coverage.formulas,
  publicMechanicsOwners: publicPathCounts.mechanics, goldLabels: gold.length }, null, 2));
