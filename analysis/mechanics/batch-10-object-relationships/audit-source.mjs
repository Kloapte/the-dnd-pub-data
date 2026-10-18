import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';

const directory = 'analysis/mechanics/batch-10-object-relationships';
const resource = 'src/main/resources/data/objects.json';
const hash = (value) => crypto.createHash('sha256').update(value).digest('hex');
const read = (file) => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));

function filesUnder(target) {
  if (!fs.statSync(target).isDirectory()) {
    return [target];
  }
  return fs.readdirSync(target, { withFileTypes: true }).flatMap((entry) =>
    filesUnder(path.posix.join(target, entry.name))).sort();
}

function protectedSnapshot() {
  return Object.fromEntries(['src', 'analysis'].map((target) => {
    const entries = filesUnder(target)
      .filter((file) => !file.startsWith(`${directory}/`))
      .map((file) => [file, hash(fs.readFileSync(file))]);
    return [target, { fileCount: entries.length, sha256: hash(JSON.stringify(entries)) }];
  }));
}

if (process.argv.includes('--snapshot')) {
  const file = `${directory}/scope-baseline.json`;
  assert.ok(!fs.existsSync(file), 'Refusing to overwrite the protected baseline');
  fs.writeFileSync(file, `${JSON.stringify(protectedSnapshot(), null, 2)}\n`);
  console.log('Recorded existing src and analysis fingerprints');
  process.exit(0);
}

assert.deepEqual(protectedSnapshot(), read(`${directory}/scope-baseline.json`),
  'Production, tests, source data or existing reports changed');

const pinnedHashes = read('analysis/mechanics/batch-9a-class-choice-completeness/pre-implementation-hashes.json');
const sourceHashes = Object.entries(pinnedHashes)
  .filter(([file]) => file.startsWith('src/main/resources/data/') && file.endsWith('.json'));
assert.equal(sourceHashes.length, 175);
assert.deepEqual(filesUnder('src/main/resources/data').filter((file) => file.endsWith('.json')),
  sourceHashes.map(([file]) => file).sort(), 'Pinned file list must equal current source JSON file list');
for (const [file, expected] of sourceHashes) {
  assert.equal(hash(fs.readFileSync(file)), expected, `Source hash changed: ${file}`);
}

// Check typed discriminators throughout every pinned file, not raw keyword frequency.
const allCorpusAttackLocations = [];
function findTypedAttacks(value, file, pointer = '') {
  if (value === null || typeof value !== 'object') {
    return;
  }
  if (!Array.isArray(value) && value.type === 'attack') {
    allCorpusAttackLocations.push({ file, pointer });
  }
  for (const [key, child] of Object.entries(value)) {
    const escaped = key.replace(/~/g, '~0').replace(/\//g, '~1');
    findTypedAttacks(child, file, `${pointer}/${escaped}`);
  }
}
for (const [file] of sourceHashes) {
  findTypedAttacks(read(file), file);
}

const document = read(resource);
const attacks = [];
const contentTypes = {};

function visit(value, pointer, ownerIndex) {
  if (value === null || typeof value !== 'object') {
    return;
  }
  if (!Array.isArray(value) && typeof value.type === 'string') {
    contentTypes[value.type] = (contentTypes[value.type] ?? 0) + 1;
    if (value.type === 'attack') {
      attacks.push({ pointer, ownerIndex, value });
    }
  }
  for (const [key, child] of Object.entries(value)) {
    const escaped = key.replace(/~/g, '~0').replace(/\//g, '~1');
    visit(child, `${pointer}/${escaped}`, ownerIndex);
  }
}

document.object.forEach((owner, index) => visit(owner, `/object/${index}`, index));
assert.equal(document.object.length, 37);
assert.equal(attacks.length, 7);
assert.equal(new Set(attacks.map((attack) => attack.ownerIndex)).size, 7);
assert.equal(allCorpusAttackLocations.length, attacks.length);
assert.ok(document.object.every((owner) =>
  !['_copy', '_versions', '_templates'].some((key) => Object.hasOwn(owner, key))));

// Syntax spans/classification are supplied by the existing Java foundation, never a new tag parser.
const runtime = read(`${directory}/runtime-evidence.json`);
assert.equal(runtime.sourceSha256, hash(fs.readFileSync(resource)));
assert.equal(runtime.definitionCount, 37);
assert.equal(runtime.actionsPreserved, 37);
assert.equal(runtime.publicActionsOmitted, 37);
assert.equal(runtime.typedAttacksReconstructed, 7);

function resolve(pointer) {
  return pointer.split('/').slice(1).reduce((value, token) =>
    value[token.replace(/~1/g, '/').replace(/~0/g, '~')], document);
}

for (const tag of runtime.tags) {
  const text = resolve(tag.pointer);
  assert.equal(hash(text), tag.textSha256);
  assert.equal(text.slice(tag.startInclusive, tag.endExclusive), tag.originalText);
  assert.equal(tag.endExclusive - tag.startInclusive, tag.originalText.length);
}

const slug = (name) => name.normalize('NFKD').replace(/\p{M}/gu, '').toLowerCase()
  .replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
const inventory = attacks.map(({ pointer, ownerIndex, value }, index) => {
  const owner = document.object[ownerIndex];
  assert.deepEqual(Object.keys(value), ['type', 'attackType', 'attackEntries', 'hitEntries']);
  assert.equal(value.attackEntries.length, 1);
  assert.equal(value.hitEntries.length, 1);
  assert.ok([...value.attackEntries, ...value.hitEntries].every((entry) => typeof entry === 'string'));
  const tags = runtime.tags.filter((tag) => tag.pointer.startsWith(`${pointer}/`));
  assert.deepEqual(tags.map((tag) => tag.name), ['hit', 'damage']);
  const definition = runtime.definitions[ownerIndex];
  const id = `object:${owner.source}:${slug(owner.name)}`;
  assert.equal(definition.id, id);
  return {
    key: `A${index + 1}`,
    id,
    name: owner.name,
    source: owner.source,
    page: owner.page ?? null,
    srd: owner.srd ?? null,
    ownerPointer: `/object/${ownerIndex}`,
    pointer,
    actionName: owner.actionEntries[0].name,
    parentType: owner.actionEntries[0].type,
    attackType: value.attackType,
    attackEntryCount: value.attackEntries.length,
    hitEntryCount: value.hitEntries.length,
    tags,
    internalPreservation: 'EXACT_TYPED_ACTION_EQUALITY',
    publicRepresentation: 'ACTIONS_INTENTIONALLY_OMITTED',
  };
});

const gold = read(`${directory}/gold-set.json`);
const labelCounts = {};
for (const label of gold.labels) {
  assert.ok(label.id && label.rule && label.relationship && label.reason);
  for (const endpoint of [label.subject, label.object]) {
    assert.equal(endpoint.resource, 'data/objects.json');
    const value = resolve(endpoint.pointer);
    assert.notEqual(value, undefined, `Missing evidence: ${endpoint.pointer}`);
    if (endpoint.span) {
      assert.equal(typeof value, 'string');
      assert.ok(endpoint.span[0] >= 0 && endpoint.span[1] <= value.length);
      assert.ok(endpoint.span[1] > endpoint.span[0]);
      assert.equal(value.slice(...endpoint.span), endpoint.token);
    }
  }
  assert.equal(typeof label.abstain, 'boolean');
  assert.ok(['ACCEPT', 'REJECT', 'ABSTAIN'].includes(label.decision));
  assert.equal(label.abstain, label.decision === 'ABSTAIN');
  if (label.rule === 'S1') {
    const parent = resolve(label.subject.pointer);
    assert.equal(parent.type, 'attack');
    const field = label.relationship === 'HAS_ATTACK_SECTION' ? 'attackEntries' : 'hitEntries';
    assert.equal(label.object.pointer, `${label.subject.pointer}/${field}`);
    assert.ok(Array.isArray(parent[field]));
  }
  if (label.rule === 'S2') {
    assert.equal(label.object.pointer, `${label.subject.pointer}/attackType`);
    assert.equal(resolve(label.subject.pointer).type, 'attack');
  }
  if (label.rule === 'T1') {
    const sameText = label.subject.pointer === label.object.pointer;
    assert.ok(sameText || label.object.pointer.startsWith(`${label.subject.pointer}/`));
    assert.ok(runtime.tags.some((tag) => tag.pointer === label.object.pointer
      && tag.startInclusive === label.object.span[0]
      && tag.endExclusive === label.object.span[1]));
  }
  if (label.rule === 'N1') {
    assert.ok(!label.object.pointer.startsWith(`${label.subject.pointer}/`));
  }
  if (label.rule === 'N2') {
    assert.notEqual(resolve(label.subject.pointer).type, 'attack');
    assert.equal(resolve(label.subject.pointer).hitEntries, undefined);
  }
  const countKey = `${label.reliability}:${label.decision}`;
  labelCounts[countKey] = (labelCounts[countKey] ?? 0) + 1;
}
assert.equal(new Set(gold.labels.map((label) => label.id)).size, gold.labels.length);
for (const item of inventory) {
  assert.ok(gold.labels.some((label) => label.container === item.key));
}

const evidence = {
  corpus: 'Repository-pinned 5eTools v2.37.0',
  resource,
  resourceSha256: runtime.sourceSha256,
  offsetUnit: 'UTF-16 code units in decoded string; [startInclusive, endExclusive)',
  rawObjectRoots: 37,
  sourceFilesStructurallyScanned: sourceHashes.length,
  allCorpusAttackLocations,
  effectiveObjects: 37,
  copiesVersionsTemplates: 0,
  typedAttackContainers: attacks.length,
  typedAttackOwners: 7,
  attackContainersWithMultipleFormulas: 0,
  attackContainersWithNestedEntryObjects: 0,
  attackContainersWithNestedTags: 0,
  contentTypeCountsAcrossAllObjectRoots: contentTypes,
  allObjectTagCount: runtime.tags.length,
  typedAttackTagCount: inventory.reduce((count, item) => count + item.tags.length, 0),
  inventory,
  goldLabelCount: gold.labels.length,
  goldLabelCounts: labelCounts,
  scope: 'Exact structural attack inventory; semantic labels are a review proposal, not extractor output',
};
fs.writeFileSync(`${directory}/source-evidence.json`, `${JSON.stringify(evidence, null, 2)}\n`);
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify({
  protectedTreesUnchanged: protectedSnapshot(),
  sourceHashesVerified: sourceHashes.length,
  sourceSpansVerified: runtime.tags.length,
  goldEndpointsVerified: gold.labels.length * 2,
  sourceToTypedToNormalizedActionsEqual: runtime.actionsPreserved,
  publicObjectsWithoutActions: runtime.publicActionsOmitted,
  mavenRun: false,
  baselineTestsReported: 504,
  baselineResolverOccurrencesReported: 6246,
  note: 'Runtime baseline not rerun; src and every previous analysis artifact retain pre-task bytes',
}, null, 2)}\n`);
console.log(JSON.stringify({ attacks: attacks.length, goldLabels: gold.labels.length, labelCounts }, null, 2));
