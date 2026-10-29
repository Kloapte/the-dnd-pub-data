# Phase 5 Rule Conversion - Batch 09

## Verified checkpoint before batch

RULE-CONV-08 passed validation with 126 Rules conversion-accounted, 105 pending, 122 canonical Rule outputs, 442 Phase 5 outputs, and 706 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rule and conversion

The approved plan selects `rule:gos:encounters-at-sea` (`$.variantrule[98]`): 2 source-entry roots and 162 nested-clause paths. The full source object, including the encounter tables and references, was inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:gos:encounters-at-sea` | UNSUPPORTED | Reference-only. The daily encounter trigger and level-banded tables are meaningful mechanics, but the frozen Rule schema has no random encounter table/result model. Outcomes depend on Creature, Vehicle, Island, Hazard, and another GoS Rule record. |

The daily d20 procedure and independent hazard/encounter result are not expressible by an existing Rule mechanics pattern. No substitute references or generic randomization mechanics were invented.

## Accounting and dependencies

Both roots and all 162 nested paths are accounted for: 0 INTERPRETED, 0 DUPLICATE, and 2 OMITTED. The introductory root is `NON_MECHANICAL`; the procedure and encounter tables are `SCHEMA_UNSUPPORTED`. The referenced Creature, ship, island, hazard, and Ocean Environs targets are not available as canonical records in this batch. Gap families R-GAP-31 and R-GAP-28 were encountered and remain deferred; neither was implemented. No duplicate register changes were needed.

## Validation and checkpoint

One permanent source-backed assertion verifies identity, provenance, root and nested-path totals, unsupported reference-only shape, gap/dependency accounting, and manifest reconciliation.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 126 | 127 |
| Rule conversions pending | 105 | 104 |
| Canonical Rule outputs | 122 | 123 |
| Phase 5 canonical outputs | 442 | 443 |
| Permanent checks | 706 | 708 |

Coverage is 62 FULL, 29 PARTIAL, and 36 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 443 Phase 5 records validated, and 708 permanent checks passed after adding the source-backed batch assertion. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/gos-encounters-at-sea.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_09.md`
