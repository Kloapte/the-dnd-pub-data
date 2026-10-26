# Phase 5 Rule Conversion — Batch 22

## Verified checkpoint before batch

RULE-CONV-21 passed validation with 178 Rules conversion-accounted, 53 conversions pending, 174 canonical Rule outputs, 494 Phase 5 canonical outputs, and 771 permanent checks. Semantic review remains complete for all 231 Rule identities.

The plan assigns exactly five Rules: `rule:dmg:morale` (`$.variantrule[155]`), `rule:phb:inspiration` (`$.variantrule[140]`), `rule:psi:emrakul-s-madness` (`$.variantrule[96]`), `rule:xge:shared-campaign-variant-rules` (`$.variantrule[190]`), and `rule:xphb:heroic-inspiration` (`$.variantrule[124]`). The batch contains 25 source-entry roots and 173 nested clause paths. Complete source objects were inspected.

## Conversion results

| Canonical ID | Coverage | Representation |
|---|---|---|
| `rule:dmg:morale` | UNSUPPORTED | Reference-only. Its triggers, Wisdom save, modifiers, leader substitution, and flee/surrender outcomes are the coupled procedure skipped by R-GAP-44. |
| `rule:phb:inspiration` | PARTIAL | Represents spending Inspiration as Advantage on an eligible D20 test with the approved `D20_TEST / TEST_MODIFIER` pattern. Award criteria, possession limit, and transfer are deferred under R-GAP-25. |
| `rule:psi:emrakul-s-madness` | PARTIAL | Reference-only with a typed link to the Honor and Sanity Rule. Sanity checks/score bands and setting-specific creature corruption or transformation are deferred/skipped under R-GAP-26/R-GAP-27; creature stat blocks are external dependencies. |
| `rule:xge:shared-campaign-variant-rules` | UNSUPPORTED | Reference-only. Advancement checkpoints, shared-campaign treasure points, level-linked wealth, and purchase/sale procedures form the deferred R-GAP-33 campaign economy. |
| `rule:xphb:heroic-inspiration` | PARTIAL | Reference-only with a typed Player Character recipient reference. Arbitrary-die reroll with mandatory replacement is not in the approved mechanics schema; resource possession and transfer are deferred under R-GAP-25. |

One record contains structured mechanics; four are reference-only. Two reference-only records carry typed references, while Morale and the shared-campaign Rule have no emitted cross-reference. No new mechanics patterns were introduced.

## Source accounting and dependencies

All 25 roots and 173 nested paths are accounted for. Disposition totals are 3 INTERPRETED and 25 OMITTED root classifications, with 0 DUPLICATE; two roots have separate interpreted-reference/mechanic and omitted-resource dispositions. Nested paths are explicitly attached to their parent dispositions. Complete omitted source entries were added to the omission register.

Encountered gap families: skipped R-GAP-24 (Madness outcomes, through the related Madness boundary), R-GAP-27 (Emrakul score-banded corruption), and R-GAP-44 (Morale); deferred R-GAP-25 (Inspiration resource lifecycle), R-GAP-26 (Honor/Sanity), and R-GAP-33 (shared-campaign advancement/economy). No gap was implemented.

Typed Rule references to `rule:dmg:new-ability-scores-honor-and-sanity` and `rule:xphb:player-character` resolve. Creature traits/stat blocks and the shared campaign's Equipment, Item, and Spell catalog dependencies remain external and were not invented or referenced as canonical outputs. No unresolved canonical references were emitted.

## Validation and checkpoint

A permanent source-backed assertion verifies all five identities, exact provenance, root and nested-path totals, coverage and gap IDs, the Advantage mechanic, typed reference targets, reference-only boundaries, and omission of unsupported Morale, transformation, reroll, and campaign-economy procedures.

| Metric | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Conversion-accounted | 178 | 183 |
| Pending conversion | 53 | 48 |
| Canonical Rule outputs | 174 | 179 |
| Phase 5 canonical outputs | 494 | 499 |
| Permanent checks | 771 | 777 |

Coverage is 68 FULL, 57 PARTIAL, and 58 UNSUPPORTED. `npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 499 Phase 5 records validated, and 777 permanent checks passed. `git diff --check` passed.

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-morale.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/phb-inspiration.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/psi-emrakul-s-madness.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xge-shared-campaign-variant-rules.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-heroic-inspiration.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_22.md`
