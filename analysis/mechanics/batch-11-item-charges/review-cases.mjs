import assert from 'node:assert/strict';
import fs from 'node:fs';

const directory = 'analysis/mechanics/batch-11-item-charges';
const evidence = JSON.parse(fs.readFileSync(`${directory}/source-evidence.json`, 'utf8'));
const runtime = JSON.parse(fs.readFileSync(`${directory}/runtime-evidence.json`, 'utf8'));
const items = JSON.parse(fs.readFileSync('src/main/resources/data/items.json', 'utf8')).item;

// Reviewed classifications of every marker-bearing root without the three dedicated fields.
// This manifest is source-specific human-readable analysis, not production keyword interpretation.
const classifications = [
  [[1532, 1664, 1819, 1820, 2086, 2163, 2379], 'OWN_CHARGE_RULE_IN_PROSE',
    'Own charge quantities/use are described, but no dedicated root charge fields exist.'],
  [[2445], 'DELEGATED_CHARGE_RULE_IN_PROSE',
    'Own recovery/risk description refers to another Item function; no independently authored count.'],
  [[86, 103, 157, 212, 252, 920, 1378, 1733, 2446, 2462], 'OTHER_USE_OR_RECHARGE_RULE',
    'Uses, cooldown, creature breath recharge, or restoration is described; not a demonstrated Item charge pool.'],
  [[102, 564, 2507], 'OTHER_ITEM_CHARGE_RULE',
    'Recovery or inspection concerns another Item; do not assign its charges to this source owner.'],
  [[410], 'COPY_INHERITS_CHARGES',
    'Authored modification describes charges; actual scalar fields come from the copied parent.'],
  [[1276, 1277, 1282, 1283, 1286, 1287, 1690, 2317, 2318, 2319, 2320, 2325, 2326],
    'CHARGED_WORD_NOT_CHARGE_POOL', 'Charged-with-magic/crystals language does not establish a charge resource.'],
];
const reviews = [];
for (const [indexes, classification, reason] of classifications) {
  for (const index of indexes) {
    const owner = items[index];
    const pointer = `/item/${index}`;
    const leaves = evidence.fieldlessCandidates.filter((candidate) => candidate.ownerPointer === pointer);
    assert.ok(leaves.length > 0, `Reviewed case no longer matches pinned evidence: ${pointer}`);
    reviews.push({ ownerPointer: pointer, name: owner.name, source: owner.source, classification, reason,
      leafPointers: leaves.map((leaf) => leaf.pointer) });
  }
}
assert.equal(reviews.length, evidence.summary.fieldlessLexicalOwnerCount);
assert.equal(new Set(reviews.map((review) => review.ownerPointer)).size, reviews.length);

const gold = [];
function node(pointer) {
  const value = pointer.split('/').slice(2).reduce((current, key) => current[key], items);
  assert.notEqual(value, undefined, `Missing original evidence: ${pointer}`);
  return { resource: 'data/items.json', pointer };
}
function span(tag) {
  return { ...node(tag.pointer), span: [tag.startInclusive, tag.endExclusive], token: tag.originalText };
}
function add(subject, object, relation, reliability, decision, reason) {
  let scope = 'EXISTING_STRUCTURED_BASELINE';
  if (relation === 'FIELD_CONTAINS_FORMULA') {
    scope = 'AUTHORED_FIELD_SYNTAX_POSITIVE';
  } else if (relation === 'ENTRY_CONTAINS_FORMULA') {
    scope = 'ENTRY_SYNTAX_CONTROL_OUTSIDE_INITIAL_SCOPE';
  } else if (relation === 'INHERITS_SOURCE_FIELD_FORMULA') {
    scope = 'OPTIONAL_EFFECTIVE_PROVENANCE_REVIEW';
  } else if (decision === 'ABSTAIN') {
    scope = 'SEMANTIC_ABSTENTION';
  } else if (decision === 'REJECT') {
    scope = 'ORIGIN_NEGATIVE';
  }
  gold.push({ id: `G${String(gold.length + 1).padStart(2, '0')}`, subject, object,
    relation, reliability, decision, scope, reason });
}

// Every distinct recovery token has one representative field/tag membership label.
const seenTokens = new Set();
for (const record of evidence.inventory) {
  for (const tag of record.dedicatedTags) {
    if (!seenTokens.has(tag.originalText)) {
      seenTokens.add(tag.originalText);
      add(node(tag.pointer), span(tag), 'FIELD_CONTAINS_FORMULA', 'TAG_SYNTAX', 'ACCEPT',
        'Exact shared-parser span under an explicitly authored charge/recovery field; no evaluation.');
    }
  }
}

const countTag = runtime.authoredTags.find((tag) => tag.pointer === '/item/2224/charges');
assert.ok(countTag);
add(node(countTag.pointer), span(countTag), 'FIELD_CONTAINS_FORMULA', 'TAG_SYNTAX', 'ACCEPT',
  'Same dice syntax in a count field must remain distinct from rechargeAmount field syntax.');

for (const [index, field, relation] of [
  [2224, 'charges', 'HAS_AUTHORED_COUNT_EXPRESSION'],
  [1175, 'rechargeAmount', 'HAS_AUTHORED_TEXT_AMOUNT'],
  [978, 'recharge', 'HAS_AUTHORED_RECHARGE_CODE'],
  [2168, 'recharge', 'HAS_AUTHORED_RECHARGE_CODE'],
  [1823, 'recharge', 'HAS_AUTHORED_RECHARGE_CODE'],
  [145, 'recharge', 'HAS_AUTHORED_RECHARGE_CODE'],
]) {
  assert.notEqual(items[index][field], undefined);
  add(node(`/item/${index}`), node(`/item/${index}/${field}`), relation, 'DIRECT_STRUCTURE', 'ACCEPT',
    'Preserve the exact source field/value; it does not establish an evaluated resource rule.');
}

for (const [pointer, ownerIndex, decision, reason] of [
  ['/item/102/entries/0', 102, 'ABSTAIN', 'Formula concerns another Item, not the battery own charge recovery.'],
  ['/item/145/entries/1', 145, 'ABSTAIN', 'Creature selection dice are not the bag recharge amount.'],
  ['/item/2163/entries/2', 2163, 'ABSTAIN', 'Recovery and last-charge destruction dice share one text leaf.'],
  ['/item/2168/entries/1/entries/1', 2168, 'ABSTAIN', 'Conditional last-charge roll and recovery require branch interpretation.'],
]) {
  const tags = runtime.authoredTags.filter((tag) => tag.pointer === pointer
    && tag.observationType === 'FormulaSyntaxObservation');
  assert.ok(tags.length > 0);
  for (const tag of tags) {
    add(node(pointer), span(tag), 'ENTRY_CONTAINS_FORMULA', 'TAG_SYNTAX', 'ACCEPT',
      'Syntax membership is observable, irrespective of gameplay purpose.');
    add(span(tag), node(`/item/${ownerIndex}`), 'FORMULA_IS_OWNER_RECHARGE',
      'PROSE_INFERENCE', decision, reason);
  }
}
const baseTag = runtime.authoredTags.find((tag) => tag.pointer === '/item/2408/rechargeAmount');
assert.ok(baseTag);
add(node('/item/410'), span(baseTag), 'INHERITS_SOURCE_FIELD_FORMULA', 'DIRECT_STRUCTURE', 'ACCEPT',
  'The verified scalar copy chain preserves the parent origin, not a fabricated child pointer.');
const zero = evidence.scheduleInventory.find((row) => Object.hasOwn(row.value, '0'));
assert.ok(zero);
add(node(zero.pointer), node(`${zero.pointer}/0`), 'HAS_LITERAL_SCHEDULE_BUCKET', 'DIRECT_STRUCTURE', 'ACCEPT',
  'Preserve the literal zero key; no expenditure or selected spell is inferred.');
add(node('/item/410'), span(baseTag), 'FORMULA_IS_AUTHORED_LOCALLY', 'UNSUPPORTED', 'REJECT',
  'The valid formula endpoint belongs to the parent, not this child; never manufacture a child-local span.');

const report = { status: 'PROPOSED_GOLD_SET_NOT_INDEPENDENTLY_ADJUDICATED',
  exhaustiveCoverageOracle: 'source-evidence inventory: all 220 field/tag observations, plus explicit scalar fields',
  reviewCoverage: 'All 35 fieldless marker owners manually classified; contextual labels are abstentions',
  fieldlessReviews: reviews,
  labels: gold,
};
fs.writeFileSync(`${directory}/review-cases.json`, `${JSON.stringify(report, null, 2)}\n`);
console.log(`Wrote ${reviews.length} fieldless owner reviews and ${gold.length} proposed gold labels`);
