import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

const directory = 'analysis/mechanics/batch-11-item-charges';
const read = (file) => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));
const hash = (value) => crypto.createHash('sha256').update(value).digest('hex');
const fields = ['charges', 'recharge', 'rechargeAmount'];
const keyword = /\b(?:charg(?:e[sd]?|ing)|recharg\w*)\b/i;
function filesUnder(target) {
  return fs.statSync(target).isDirectory()
    ? fs.readdirSync(target).flatMap((name) => filesUnder(`${target}/${name}`)).sort()
    : [target];
}
const protectedFiles = read(`${directory}/protected-files.json`);
assert.deepEqual(filesUnder('src'), Object.keys(protectedFiles).filter((file) => file.startsWith('src/')).sort());
assert.deepEqual(filesUnder('analysis').filter((file) => !file.startsWith(`${directory}/`)),
  Object.keys(protectedFiles).filter((file) => file.startsWith('analysis/')).sort());
for (const [file, expected] of Object.entries(protectedFiles)) {
  assert.equal(hash(fs.readFileSync(file)), expected, `Protected pre-existing file changed: ${file}`);
}
const pinned = read('analysis/mechanics/batch-10a-object-membership/pre-implementation-hashes.json');
const pinnedSources = Object.keys(pinned).filter((file) =>
  file.startsWith('src/main/resources/data/') && file.endsWith('.json'));
assert.equal(pinnedSources.length, 175);
for (const file of pinnedSources) {
  assert.equal(hash(fs.readFileSync(file)), pinned[file], `Pinned data changed: ${file}`);
}

const items = read('src/main/resources/data/items.json').item;
const baseItems = read('src/main/resources/data/items-base.json').baseitem;
assert.ok(baseItems.every((owner) => !keyword.test(JSON.stringify(owner))), 'Review new Base Item marker candidates');
const runtime = read(`${directory}/runtime-evidence.json`);
assert.equal(runtime.effectiveDefinitionCount, 2742);
assert.equal(runtime.itemRoots, items.length);
assert.equal(runtime.baseItemRoots, baseItems.length);
assert.equal(runtime.sourceSha256, hash(fs.readFileSync('src/main/resources/data/items.json')));
const registry = read('src/main/resources/data/sources/registry.json').sources;
const sourceCodes = new Set(registry.map((source) => source.code));
const byPointer = new Map(runtime.effectiveItems.map((row) => [row.ownerPointer, row]));
const fieldShapes = {};
const recoveryVariants = {};
const sourceDistribution = {};
const rechargeCodes = {};
const inventory = [];
const keywordLeaves = [];
const scheduleInventory = [];
const copyInstructions = [];

function leaves(value, pointer, output) {
  if (typeof value === 'string') {
    output.push({ pointer, value });
  } else if (value !== null && typeof value === 'object') {
    for (const [key, child] of Object.entries(value)) {
      const escaped = key.replace(/~/g, '~0').replace(/\//g, '~1');
      leaves(child, `${pointer}/${escaped}`, output);
    }
  }
}

function at(pointer) {
  return pointer.split('/').slice(2).reduce((value, key) =>
    value[key.replace(/~1/g, '/').replace(/~0/g, '~')], items);
}

for (const tag of runtime.authoredTags) {
  const value = at(tag.pointer);
  assert.equal(hash(value), tag.textSha256);
  assert.equal(value.slice(tag.startInclusive, tag.endExclusive), tag.originalText);
}

for (const [index, owner] of items.entries()) {
  const ownerPointer = `/item/${index}`;
  const effective = byPointer.get(ownerPointer);
  assert.ok(sourceCodes.has(owner.source), `Unknown primary source ${owner.source}`);
  const record = {
    resource: 'data/items.json', ownerPointer, id: effective.id,
    name: owner.name, source: owner.source, page: owner.page ?? null,
    srd: owner.srd ?? null, srd52: owner.srd52 ?? null,
    fields: {}, entryChargeMarkerLeaves: [], entryFormulaOccurrences: 0,
  };
  for (const field of fields) {
    if (!Object.hasOwn(owner, field)) {
      continue;
    }
    const value = owner[field];
    const type = value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
    fieldShapes[field] ??= {};
    fieldShapes[field][type] = (fieldShapes[field][type] ?? 0) + 1;
    record.fields[field] = { pointer: `${ownerPointer}/${field}`, type, value };
    if (field === 'rechargeAmount') {
      const key = `${type}:${value}`;
      recoveryVariants[key] = (recoveryVariants[key] ?? 0) + 1;
    }
    if (field === 'recharge') {
      rechargeCodes[value] = (rechargeCodes[value] ?? 0) + 1;
    }
  }
  const text = [];
  leaves(owner, ownerPointer, text);
  for (const leaf of text) {
    if (keyword.test(leaf.value)) {
      const evidence = {
        ownerPointer, name: owner.name, source: owner.source,
        pointer: leaf.pointer, textSha256: hash(leaf.value),
        utf16Length: leaf.value.length,
        transformInstruction: leaf.pointer.includes('/_copy/'),
        markers: {
          expenditureWord: /\bexpend\w*\b/i.test(leaf.value),
          recoveryWord: /\b(?:regain\w*|recharg\w*|restor\w*)\b/i.test(leaf.value),
          conditionalWord: /\b(?:if|when|unless|until)\b/i.test(leaf.value),
          formulaTagPresent: runtime.authoredTags.some((tag) =>
            tag.pointer === leaf.pointer && tag.observationType === 'FormulaSyntaxObservation'),
        },
        classification: 'LEXICAL_REVIEW_LEAD_NOT_AUTOMATIC_RESOURCE_FACT',
      };
      keywordLeaves.push(evidence);
      if (leaf.pointer.startsWith(`${ownerPointer}/entries/`)
          || leaf.pointer.startsWith(`${ownerPointer}/additionalEntries/`)) {
        record.entryChargeMarkerLeaves.push(evidence);
      }
    }
  }
  record.dedicatedTags = runtime.authoredTags.filter((tag) =>
    tag.pointer === `${ownerPointer}/charges` || tag.pointer === `${ownerPointer}/rechargeAmount`);
  record.entryFormulaOccurrences = runtime.authoredTags.filter((tag) =>
    tag.ownerPointer === ownerPointer && tag.observationType === 'FormulaSyntaxObservation'
    && (tag.pointer.startsWith(`${ownerPointer}/entries/`)
      || tag.pointer.startsWith(`${ownerPointer}/additionalEntries/`))).length;
  if (Object.keys(record.fields).length > 0) {
    inventory.push(record);
  }
  if (record.dedicatedTags.length > 0) {
    sourceDistribution[owner.source] = (sourceDistribution[owner.source] ?? 0) + 1;
  }
  if (owner._copy) {
    copyInstructions.push({
      ownerPointer, name: owner.name, source: owner.source,
      targetName: owner._copy.name, targetSource: owner._copy.source,
      modificationFields: Object.keys(owner._copy._mod ?? {}),
      entryModificationMode: owner._copy._mod?.entries?.mode ?? null,
    });
  }
  if (owner.attachedSpells?.charges !== undefined) {
    assert.ok(!Array.isArray(owner.attachedSpells.charges));
    assert.ok(Object.values(owner.attachedSpells.charges).every((spells) =>
      Array.isArray(spells) && spells.every((spell) => typeof spell === 'string')));
    scheduleInventory.push({ ownerPointer, name: owner.name, source: owner.source,
      pointer: `${ownerPointer}/attachedSpells/charges`, value: owner.attachedSpells.charges });
  }
}

const effectiveCounts = {};
const inherited = [];
const lostRecharge = [];
for (const row of runtime.effectiveItems) {
  assert.deepEqual(row.normalizedCharges, row.publicCharges);
  for (const [field, evidence] of Object.entries(row.effectiveFields)) {
    effectiveCounts[field] = (effectiveCounts[field] ?? 0) + 1;
    if (evidence.inherited) {
      effectiveCounts[`${field}Inherited`] = (effectiveCounts[`${field}Inherited`] ?? 0) + 1;
      inherited.push({ id: row.id, ownerPointer: row.ownerPointer, field, ...evidence });
    }
    const value = at(evidence.originPointer);
    if (field === 'recharge') {
      assert.equal(evidence.typedValue, value);
    } else if (typeof value === 'number') {
      assert.deepEqual(evidence.typedValue, { number: value, text: null });
    } else {
      assert.deepEqual(evidence.typedValue, { number: null, text: value });
    }
  }
  if (Object.keys(row.effectiveFields).length > 0) {
    effectiveCounts.owners = (effectiveCounts.owners ?? 0) + 1;
  }
  if (row.normalizedCharges !== null) {
    effectiveCounts.normalizedChargeOwners = (effectiveCounts.normalizedChargeOwners ?? 0) + 1;
  }
  if (row.publicCharges?.countText?.includes('{@') || row.publicCharges?.rechargeAmountText?.includes('{@')) {
    effectiveCounts.publicTaggedOwners = (effectiveCounts.publicTaggedOwners ?? 0) + 1;
  }
  if (row.effectiveFields.recharge && row.normalizedCharges === null) {
    lostRecharge.push({ id: row.id, ownerPointer: row.ownerPointer, name: row.name, source: row.source,
      authored: row.effectiveFields.recharge });
  }
}

const taggedInventory = inventory.filter((record) => record.dedicatedTags.length > 0);
for (const record of taggedInventory) {
  for (const tag of record.dedicatedTags) {
    const original = at(tag.pointer);
    assert.equal(tag.originalText, original);
    assert.equal(tag.startInclusive, 0);
    assert.equal(tag.endExclusive, original.length);
    assert.equal(tag.name, 'dice');
    assert.equal(tag.arguments.length, 1);
    assert.equal(tag.expression.kind, 'DICE_NOTATION');
  }
}
assert.equal(taggedInventory.length, 220);
assert.equal(taggedInventory.flatMap((record) => record.dedicatedTags).length, 220);
assert.equal(lostRecharge.length, 28);
assert.equal(baseItems.filter((owner) => fields.some((field) => Object.hasOwn(owner, field))).length, 0);

const fieldlessIndexes = new Set(items.map((owner, index) =>
  fields.some((field) => Object.hasOwn(owner, field)) ? -1 : index));
const fieldlessCandidates = keywordLeaves.filter((leaf) => fieldlessIndexes.has(Number(leaf.ownerPointer.split('/')[2])));
const fieldlessOwners = [...new Set(fieldlessCandidates.map((leaf) => leaf.ownerPointer))];
const summary = {
  sourceFilesHashVerified: 175,
  authoredItemRoots: items.length,
  authoredBaseItemRoots: baseItems.length,
  effectiveDefinitions: runtime.effectiveDefinitionCount,
  authoredStructuredOwners: inventory.length,
  authoredStructuredFieldOccurrences: inventory.reduce((sum, record) => sum + Object.keys(record.fields).length, 0),
  authoredTaggedOwners: taggedInventory.length,
  authoredDedicatedFormulaTags: taggedInventory.flatMap((record) => record.dedicatedTags).length,
  taggedOwnersWithCountField: taggedInventory.filter((record) => record.fields.charges).length,
  taggedOwnersWithRechargeCode: taggedInventory.filter((record) => record.fields.recharge).length,
  taggedOwnersWithExpenditureWordInChargeLeaf: taggedInventory.filter((record) =>
    record.entryChargeMarkerLeaves.some((leaf) => leaf.markers.expenditureWord)).length,
  taggedOwnersWithRecoveryWordInChargeLeaf: taggedInventory.filter((record) =>
    record.entryChargeMarkerLeaves.some((leaf) => leaf.markers.recoveryWord)).length,
  taggedOwnersWithConditionalWordInChargeLeaf: taggedInventory.filter((record) =>
    record.entryChargeMarkerLeaves.some((leaf) => leaf.markers.conditionalWord)).length,
  otherEntryFormulaOccurrencesOnTaggedOwners: taggedInventory.reduce((sum, record) =>
    sum + record.entryFormulaOccurrences, 0),
  dedicatedTaggedFieldCounts: {
    charges: taggedInventory.filter((record) => record.fields.charges?.type === 'string').length,
    rechargeAmount: taggedInventory.filter((record) => record.dedicatedTags.some((tag) =>
      tag.pointer.endsWith('/rechargeAmount'))).length,
  },
  fieldShapes, rechargeCodes, recoveryVariants, taggedOwnerSourceDistribution: sourceDistribution,
  fieldlessLexicalOwnerCount: fieldlessOwners.length,
  lexicalChargeMarkerLeaves: keywordLeaves.length,
  lexicalChargeMarkerOwners: new Set(keywordLeaves.map((leaf) => leaf.ownerPointer)).size,
  lexicalExpenditureLeaves: keywordLeaves.filter((leaf) => leaf.markers.expenditureWord).length,
  lexicalRecoveryLeaves: keywordLeaves.filter((leaf) => leaf.markers.recoveryWord).length,
  lexicalConditionalLeaves: keywordLeaves.filter((leaf) => leaf.markers.conditionalWord).length,
  copies: copyInstructions.length,
  versions: items.filter((owner) => Object.hasOwn(owner, '_versions')).length,
  effectiveCounts,
  inheritedFieldOccurrences: inherited.length,
  inheritedOwners: new Set(inherited.map((field) => field.id)).size,
  rechargeOmittedOwners: lostRecharge.length,
  scheduleOwners: scheduleInventory.length,
  scheduleBuckets: scheduleInventory.reduce((sum, record) => sum + Object.keys(record.value).length, 0),
  scheduleSpellOccurrences: scheduleInventory.reduce((sum, record) => sum
    + Object.values(record.value).reduce((total, spells) => total + spells.length, 0), 0),
  effectiveScheduleOwners: runtime.effectiveItems.filter((row) => row.normalizedAttachedSpells?.charges).length,
  effectiveScheduleSpellOccurrences: runtime.effectiveItems.reduce((sum, row) => sum
    + Object.values(row.normalizedAttachedSpells?.charges?.byUses ?? {})
      .reduce((count, spells) => count + spells.length, 0), 0),
  baseReloadOwners: baseItems.filter((owner) => Object.hasOwn(owner, 'reload')).length,
  baseChargeMarkerOwners: 0,
  allItemTagsParsed: runtime.allAuthoredTagsParsed,
  retainedCandidateTags: runtime.authoredTags.length,
  semanticCoverage: 'No lexical marker is asserted to establish a resource mechanic',
};
const report = { summary, inventory, taggedOwnerPointers: taggedInventory.map((record) => record.ownerPointer),
  keywordLeaves, fieldlessCandidates, lostRecharge, inherited, copyInstructions, scheduleInventory };
fs.writeFileSync(`${directory}/source-evidence.json`, `${JSON.stringify(report, null, 2)}\n`);
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify({
  protectedExistingFilesUnchanged: Object.keys(protectedFiles).length,
  pinnedSourceHashesUnchanged: 175,
  sourceSpanChecks: runtime.authoredTags.length,
  allParsedItemTagSpansCheckedInJava: runtime.allAuthoredTagsParsed,
  mavenRun: false,
  reportedTestBaseline: 518,
  reportedResolverBaseline: 6246,
  note: 'No production, test, prior analysis or resolver bytes changed; runtime outcome suite not rerun',
}, null, 2)}\n`);
console.log(JSON.stringify(summary, null, 2));
