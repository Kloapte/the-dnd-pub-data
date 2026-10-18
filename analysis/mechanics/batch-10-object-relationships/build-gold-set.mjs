import assert from 'node:assert/strict';
import fs from 'node:fs';

const directory = 'analysis/mechanics/batch-10-object-relationships';
const document = JSON.parse(fs.readFileSync('src/main/resources/data/objects.json', 'utf8'));
const runtime = JSON.parse(fs.readFileSync(`${directory}/runtime-evidence.json`, 'utf8'));

// Manually selected/reviewed locations and lexical anchors, not inferred gameplay facts.
// Changing pinned source must fail anchor validation and require review of this manifest.
const cases = [
  { key: 'A1', index: 0, name: 'Animated Ghaunadaur Statue', source: 'WDMM',
    attackType: 'MW', formula: '2d6 + 5', modifier: '+10', damageWord: 'slashing',
    rangeWords: 'reach 5 ft.', targetWords: 'one target' },
  { key: 'A2', index: 2, name: 'Ballista', source: 'DMG',
    attackType: 'RW', formula: '3d10', modifier: '+6', damageWord: 'piercing',
    rangeWords: 'range 120/480 ft.', targetWords: 'one target' },
  { key: 'A3', index: 5, name: 'Cannon', source: 'DMG',
    attackType: 'RW', formula: '8d10', modifier: '+6', damageWord: 'bludgeoning',
    rangeWords: 'range 600/2,400 ft.', targetWords: 'one target' },
  { key: 'A4', index: 7, name: 'Captain A-Hole', source: 'RMBRE',
    attackType: 'RW', formula: '1d6', modifier: '+5', damageWord: 'bludgeoning',
    rangeWords: 'range 30 ft.', targetWords: 'one target' },
  { key: 'A5', index: 23, name: 'Mangonel', source: 'DMG',
    attackType: 'RW', formula: '5d10', modifier: '+5', damageWord: 'bludgeoning',
    rangeWords: 'range 200/800 ft.', targetWords: 'one target',
    restrictionWords: "can't hit targets within 60 feet of it" },
  { key: 'A6', index: 25, name: 'Ram', source: 'DMG',
    attackType: 'MW', formula: '3d10', modifier: '+8', damageWord: 'bludgeoning',
    rangeWords: 'reach 5 ft.', targetWords: 'one object' },
  { key: 'A7', index: 34, name: 'Trebuchet', source: 'DMG',
    attackType: 'RW', formula: '8d10', modifier: '+5', damageWord: 'bludgeoning',
    rangeWords: 'range 300/1,200 ft.', targetWords: 'one target',
    restrictionWords: "can't hit targets within 60 feet of it" },
];

function resolve(pointer) {
  return pointer.split('/').slice(1).reduce((value, key) => value[key], document);
}

function node(pointer) {
  assert.notEqual(resolve(pointer), undefined);
  return { resource: 'data/objects.json', pointer };
}

function words(pointer, token) {
  const text = resolve(pointer);
  const start = text.indexOf(token);
  assert.ok(start >= 0, `Missing reviewed anchor ${pointer}: ${token}`);
  assert.equal(text.indexOf(token, start + 1), -1, 'Anchor must identify a unique occurrence');
  return { ...node(pointer), span: [start, start + token.length], token };
}

function tag(pointer, name) {
  const matches = runtime.tags.filter((value) => value.pointer === pointer && value.name === name);
  assert.equal(matches.length, 1, `Expected exactly one ${name} tag at ${pointer}`);
  const value = matches[0];
  return { ...node(pointer), span: [value.startInclusive, value.endExclusive], token: value.originalText };
}

const labels = [];
function label(id, container, subject, object, relationship, reliability, decision, rule, reason) {
  labels.push({ id, container, subject, object, relationship, reliability, decision,
    abstain: decision === 'ABSTAIN', rule, reason });
}

for (const item of cases) {
  const owner = document.object[item.index];
  assert.equal(owner.name, item.name);
  assert.equal(owner.source, item.source);
  const pointer = `/object/${item.index}/actionEntries/0/entries/0`;
  assert.equal(resolve(pointer).attackType, item.attackType);
  const attack = `${pointer}/attackEntries`;
  const hit = `${pointer}/hitEntries`;
  const formula = tag(`${hit}/0`, 'damage');
  const modifier = tag(`${attack}/0`, 'hit');
  assert.equal(formula.token, `{@damage ${item.formula}}`);
  assert.equal(modifier.token, `{@hit ${item.modifier}}`);
  const add = (suffix, subject, object, relation, reliability, decision, rule, reason) =>
    label(`${item.key}-${suffix}`, item.key, subject, object, relation, reliability, decision, rule, reason);

  add('01', node(pointer), node(attack), 'HAS_ATTACK_SECTION', 'DIRECT_STRUCTURE', 'ACCEPT', 'S1',
    'The typed attackEntries field belongs to this exact type=attack node.');
  add('02', node(pointer), node(hit), 'HAS_HIT_SECTION', 'DIRECT_STRUCTURE', 'ACCEPT', 'S1',
    'The typed hitEntries field belongs to this exact type=attack node.');
  add('03', node(pointer), node(`${pointer}/attackType`), 'HAS_AUTHORED_ATTACK_TYPE',
    'DIRECT_STRUCTURE', 'ACCEPT', 'S2', 'Preserve the authored attackType code without expansion.');
  add('04', node(attack), modifier, 'SECTION_CONTAINS_MODIFIER_TAG', 'TAG_SYNTAX', 'ACCEPT', 'T1',
    'The shared parser identifies a hit modifier in a string member of attackEntries.');
  add('05', node(hit), formula, 'SECTION_CONTAINS_FORMULA_TAG', 'TAG_SYNTAX', 'ACCEPT', 'T1',
    'The shared parser identifies a formula in a string member of hitEntries.');
  add('06', formula, words(`${hit}/0`, item.damageWord), 'FORMULA_HAS_DAMAGE_TYPE',
    'LOCAL_CONTEXT', 'ABSTAIN', 'A1', 'Adjacent untagged damage word is not a typed damage-type relationship.');
  add('07', node(pointer), words(`${attack}/0`, item.rangeWords), 'HAS_INTERPRETED_RANGE_OR_REACH',
    'PROSE_INFERENCE', 'ABSTAIN', 'A2', 'Range units, slash values and reach meaning require a separate text grammar.');
  add('08', node(pointer), words(`${attack}/0`, item.targetWords), 'HAS_TARGET_RESTRICTION',
    'PROSE_INFERENCE', 'ABSTAIN', 'A2', 'The target phrase has no typed target field or target tag.');
  add('09', formula, node(`/object/${item.index}`), 'APPLIES_DAMAGE_TO_OWNER',
    'UNSUPPORTED', 'ABSTAIN', 'A3', 'Source ownership does not identify a damage recipient; no receiver is structured.');
  add('10', node(attack), formula, 'SECTION_CONTAINS_FORMULA_TAG', 'DIRECT_STRUCTURE', 'REJECT', 'N1',
    'This formula is under the sibling hitEntries field, not attackEntries.');
  if (item.restrictionWords) {
    add('11', node(pointer), words(`${attack}/0`, item.restrictionWords), 'HAS_MINIMUM_DISTANCE_RESTRICTION',
      'PROSE_INFERENCE', 'ABSTAIN', 'A2', 'The parenthetical exception is authored prose, not structured range metadata.');
  }
}

const automatic = '/object/1/actionEntries/0';
const automaticText = `${automatic}/entries/0`;
label('C01', 'CONTROL', node(automaticText), tag(automaticText, 'h'), 'TEXT_CONTAINS_HIT_LABEL',
  'TAG_SYNTAX', 'ACCEPT', 'T1', 'Recognizing the h tag alone does not create a hitEntries field.');
label('C02', 'CONTROL', node(automatic), node(automaticText), 'HAS_HIT_SECTION',
  'DIRECT_STRUCTURE', 'REJECT', 'N2', 'The entries wrapper contains text, not a typed attack with hitEntries.');
label('C03', 'CONTROL', tag(automaticText, 'h'), tag(automaticText, 'damage'), 'HIT_LABEL_GOVERNS_FORMULA',
  'LOCAL_CONTEXT', 'ABSTAIN', 'A1', 'Tag order is explicit; rendered section extent is not represented by the tag parser.');

const list = '/object/10/actionEntries/0/entries/1/items';
const firstFormula = tag(`${list}/0/entries/0`, 'damage');
const secondFormula = tag(`${list}/1/entries/0`, 'damage');
label('C04', 'CONTROL', node(`${list}/0`), firstFormula, 'ITEM_CONTAINS_FORMULA_TAG',
  'TAG_SYNTAX', 'ACCEPT', 'T1', 'Nested list-item ancestry is explicit; it does not make an ObjectAttackDte.');
label('C05', 'CONTROL', node(`${list}/1`), secondFormula, 'ITEM_CONTAINS_FORMULA_TAG',
  'TAG_SYNTAX', 'ACCEPT', 'T1', 'Identical 2d8 syntax in another list item has a distinct pointer and span.');
label('C06', 'CONTROL', node(`${list}/0`), secondFormula, 'ITEM_CONTAINS_FORMULA_TAG',
  'DIRECT_STRUCTURE', 'REJECT', 'N1', 'A sibling item formula must not be reassigned to the first item.');
label('C07', 'CONTROL', firstFormula, secondFormula, 'FORMULAS_SHARE_APPLIED_HIT',
  'UNSUPPORTED', 'ABSTAIN', 'A3', 'Two formulas under a common action wrapper do not establish a single applied hit.');
const protectorText = '/object/13/actionEntries/0/entries/0';
label('C08', 'CONTROL', tag(protectorText, 'damage'), words(protectorText, 'temporary hit points'),
  'DAMAGE_TAG_PROVES_DAMAGE_APPLICATION', 'PROSE_INFERENCE', 'ABSTAIN', 'A2',
  'Protector uses a damage formula tag in temporary-hit-point prose; tag family is not an applied effect.');

const report = {
  status: 'PROPOSED_REVIEWABLE_GOLD_SET',
  review: 'Source anchors and labels reviewed in this analysis; no independent second human adjudication claimed',
  scope: 'Seven exhaustive typed attack containers plus selected actual-corpus boundary controls',
  offsetUnit: 'Decoded string UTF-16 code units, inclusive start / exclusive end',
  rules: {
    S1: 'Typed field membership: exact original type=attack parent, without interpreting text.',
    S2: 'Exact authored attackType scalar belongs to its original parent.',
    T1: 'Original ancestor relationship plus shared-parser tag span; no applied gameplay meaning.',
    N1: 'Reject an alleged descendant relationship across sibling field/item boundaries.',
    N2: 'Reject a structured hit section on a text-only action wrapper.',
    A1: 'Abstain from semantic association inferred only by local word/tag adjacency.',
    A2: 'Abstain from interpreted range, targeting, exceptions and applied-effect prose.',
    A3: 'Abstain when source lacks a structured actor/recipient/shared-outcome relationship.',
  },
  reviewedCases: cases,
  labels,
};
fs.writeFileSync(`${directory}/gold-set.json`, `${JSON.stringify(report, null, 2)}\n`);
console.log(`Wrote ${labels.length} reviewable labels`);
