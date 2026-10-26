# Phase 5 Rule Conversion - Batch 10

## Verified checkpoint before batch

RULE-CONV-09 is accounted at 127 of 231 Rules, with 104 pending, 123 canonical Rule outputs, 443 Phase 5 outputs, and 708 permanent checks after its source-backed assertion was added. Semantic review remains complete for all 231 Rules.

## Selected Rule and conversion

The approved plan selects `rule:gos:mysterious-islands` (`$.variantrule[158]`): 4 roots and 417 nested paths. The full source object and its linked theme, creature, curse, reaction, encounter, and story-hook tables were inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:gos:mysterious-islands` | UNSUPPORTED | Reference-only. Island dimension generation and linked theme/outcome tables require unsupported location/random-table models. Conditional curse and social effects depend on a selected result, so encoding them without the table trigger would change their meaning. |

The existing Rule mechanics patterns cannot represent the table-driven generation sequence or preserve its outcome selection. The canonical identity preserves source and page provenance; no generic mechanics or guessed references were added.

## Accounting and dependencies

All 4 roots and 417 nested paths are accounted for: 0 INTERPRETED, 0 DUPLICATE, and 4 OMITTED (2 `NON_MECHANICAL`, 2 `SCHEMA_UNSUPPORTED`). R-GAP-28 is deferred and was not implemented. Creature, Hazard, Island, Spell, and Item/Equipment references remain unresolved in this conversion; no substitute edition targets were used. No duplicate register changes were needed.

## Validation and checkpoint

One permanent source-backed assertion checks identity, source provenance, exact root/nested totals, unsupported reference-only shape, gap linkage, and nested-path accounting. The canonical record validates against the approved Rule schema.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 127 | 128 |
| Rule conversions pending | 104 | 103 |
| Canonical Rule outputs | 123 | 124 |
| Phase 5 canonical outputs | 443 | 444 |
| Permanent checks | 708 | 710 |

Coverage is 62 FULL, 29 PARTIAL, and 38 UNSUPPORTED. Semantic review remains 231/231.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 444 Phase 5 records validated, and 710 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/gos-mysterious-islands.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_10.md`
