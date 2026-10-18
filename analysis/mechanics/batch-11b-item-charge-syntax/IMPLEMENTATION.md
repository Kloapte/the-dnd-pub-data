# Batch 11B: Internal Item charge syntax index

## Scope and implementation

This batch adds a startup-loaded, immutable index over authored Item source text. It records the
original tag syntax and the location in which it appeared. The index does not describe an applied
mechanic and is not included in Item definitions or public DTOs.

The implementation reuses Batch 5's `AuthoredSyntaxTextWalker`, `BalancedMarkupParser`,
`SyntaxObservationClassifier`, `SyntaxTextLocation`, `SyntaxSourceOwner`, `SyntaxEvidence`,
`SyntaxSpan`, and typed observation variants. The new types are:

- `ItemChargeSyntaxOwner`: an authored Item ID/name/source and the root source locator.
- `ItemChargeSyntaxObservation`: authored Item ID, sequential per-owner occurrence index, a
  bounded context category, and the Batch 5 syntax observation.
- `ItemChargeSyntaxCategory`: field-role and entry-context categories.
- `ItemChargeSyntaxIndex`: immutable owner and observation lists plus an immutable index by
  authored Item ID.
- `ItemChargeSyntaxAdapter` and `ItemChargeSyntaxCatalog`: strict startup loading and source scan.

The adapter strictly deserializes `data/items.json` as `ItemFileDte`, then walks the corresponding
original JSON nodes for exact JSON Pointers and text. Each observation retains the exact token,
arguments, nested tags, source resource, SHA-256 of the source resource, hierarchy, and UTF-16
`[start,end)` offsets. Source order is retained; no sorting by interpreted formula values occurs.
Transform payloads retain the Batch 5 `AUTHORED_TRANSFORM_INSTRUCTION` origin.

## Source coverage and categories

The authored Item source contains 2,512 roots. All are indexed as owners and each matches exactly
one existing Item catalog identity. The runtime Item catalog still contains 2,742 Item/Base Item
definitions. Base Items are not syntax owners because the source audit found no charge markers or
dedicated charge fields there.

The index contains 1,391 observations:

| Category | Observations | Meaning |
|---|---:|---|
| `COUNT_FIELD_SYNTAX` | 1 | Tag syntax directly in an authored `charges` field (Stonemaker War Pick BMT). |
| `RECOVERY_FIELD_SYNTAX` | 219 | Tag syntax directly in an authored `rechargeAmount` field. |
| `CHARGE_OWNER_ENTRY_FORMULA` | 469 | Formula syntax in an entry of an Item with an authored `charges`, `recharge`, or `rechargeAmount` field, plus formula syntax in a lexical charge-marked entry of a fieldless Item. |
| `CHARGE_MARKED_ENTRY_OTHER_SYNTAX` | 702 | Non-formula tags in entry strings containing a charge/recharge lexical marker. |

The first two categories reconcile to the 220 authored tagged-field roots. They are direct syntax
observations: 219 recovery-field tags and one count-field tag. The exact argument text is retained;
for example Stonemaker's `{@dice 1d6 + 1}` remains a count-field expression. A matching token in a
`rechargeAmount` field remains a recovery-field expression. Neither is evaluated.

The entry-formula category is intentionally broader than formula tokens on the 220 tagged-field
roots. It contains 399 formulas on those roots, 62 formulas on the remaining Items with a
structured charge-related field, and eight formulas in charge-marked entry strings on Items with
no such field. This includes outcome-selection and other unrelated formulas. Formula membership
in this category does not establish expenditure, recharge, resource ownership, or any relationship
between a formula and nearby prose. The 469 entry formulas comprise 376 `dice` and 93 `damage`
tags; none are converted into evaluated or applied effects. The 220 dedicated field tags are all
`dice` tags.

The source evidence identifies 633 entry strings on 316 authored roots with charge/recharge word
matches. These are lexical review locations, not confirmed charge mechanics. The 702 non-formula
observations preserve tags in those strings without interpreting them. The index has observations
for 295 authored Items; marker strings without tags do not produce syntax observations.

## Gold-set reconciliation and abstention

The proposed Batch 11 gold set contains 40 labels and remains unchanged. The 19
`AUTHORED_FIELD_SYNTAX_POSITIVE` labels and six
`ENTRY_SYNTAX_CONTROL_OUTSIDE_INITIAL_SCOPE` labels are eligible syntax-membership positives. All
25 match exact source pointer, span, and token in the new index:

- True positives: 25.
- False negatives: 0 among the 25 labeled positive locations.
- Recall over those labeled positives: 25/25.

The proposed gold set contains no eligible negative syntax-membership labels, so syntax precision
and corpus-wide false positives are not estimable from this set. Its separate explicit
parent-token-as-child-origin negative is one true negative and zero false positives for that
origin claim. This is a narrow location-membership check, not a corpus-wide semantic accuracy
estimate. Six
`SEMANTIC_ABSTENTION` labels remain abstentions: syntax is retained, while no formula-to-recharge
relationship is emitted. The `ORIGIN_NEGATIVE` label remains rejected because the parent's token
location is never reassigned to the child. The remaining labels concern already structured source
fields, a schedule bucket, or the optional effective-provenance review; they are not measures of
new syntax extraction.

The fieldless positive/negative examples include Arcane Battery (a formula restoring another
Item), Bag of Tricks Gray (a creature-selection roll), Staff of Charming (recovery and destruction
rolls in one entry), and Staff of Fate (conditional destruction/recovery rolls in nested entries).
Their formulas remain separate observations, including duplicate tags at different spans.

## Authored versus effective provenance

The index covers all 2,512 authored Item roots and does not materialize effective copy observations.
The existing Item pipeline has 67 copy roots and no Item versions; it supports entry
replacements/additions, but does not attach exact original text provenance to every transformed
effective entry. The source audit
identified 224 effective Items exposing tagged charge text, including four inherited tagged
recovery strings. Those four remain represented at their authored source roots only; no child-local
span or duplicate effective observation is fabricated. Effective observation materialization is
explicitly reported as unsupported by this index.

Batch 11A's 28 `ItemRechargeDeclaration` values, existing `ItemChargeDto` values, focus values,
signed bonuses, Magic Variant matching, and resolver data are not changed.

## Publication boundary

All new syntax and evidence structures are internal. Item public DTOs, REST endpoints, serialized
Item charge objects, and resolver projections are unchanged. No charge counts, reset conditions,
resource ownership, expenditure amounts, or effects are inferred. The existing raw public charge
field behavior is unchanged; this batch does not make a new publication decision about it.

## Verification

Seven focused Spring tests cover full owner/observation counts, field role separation, required gold
labels, source pointers and spans, UTF-16 substring reconstruction, source origin, deterministic
per-owner ordering, immutable collections, a nested multi-formula source, and no effective-copy
materialization. The gold reconciliation is implemented as follows:

- Syntax membership positives: 25 TP, zero FN.
- Explicit parent-token-as-child-origin negative: one TN; no child observation points to the
  parent's JSON Pointer.
- False positives: zero against the labeled negative-origin claim.
- Semantic formula-to-recharge claims: six abstentions; the index has no relationship type for
  these claims.

These labels are a small proposed set, not an exhaustive negative syntax set. Therefore the FP/TN
figures apply only to the explicitly labeled origin claim, while syntax precision/recall beyond the
25 positive labels is not established.

Verification completed:

- Full Maven package: **529 tests passed**, zero failures/errors (522 baseline + seven new tests).
- The packaged Spring Boot JAR started successfully and initialized the internal index.
- `verify-source-hashes.mjs` verified all 175 pinned source JSON hashes.
- Existing resolver tests retained 6,246 results with the unchanged outcome distribution.
- Existing Item catalog tests retained 2,742 Item/Base Item definitions and IDs; no Item copy,
  charge normalization, Magic Variant, or public DTO behavior changed.
- `git diff --check` passed. Git reported only its existing LF-to-CRLF working-tree notices for
  previously modified files.

## Remaining limitations

- Entry formulas are associated only with an explicit charge-related Item field or a lexical marker
  in the same entry string; this is a bounded candidate scope and not a semantic relation.
- The lexical marker scan is not complete for prose that describes uses/recovery without charge
  vocabulary.
- Effective copy entries lack sufficient source-span lineage for reliable annotation expansion.
- Licensing/publication review of any future public projection remains separate.
