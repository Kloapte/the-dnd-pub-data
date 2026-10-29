# Phase 5 Rule Conversion - Batch 13

## Verified checkpoint before batch

RULE-CONV-12 passed validation with 130 Rules conversion-accounted, 101 pending, 126 canonical Rule outputs, 446 Phase 5 outputs, and 714 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rule and conversion

The approved plan selects `rule:gos:travel-at-sea` (`$.variantrule[223]`): 4 source-entry roots and 307 nested-clause paths. The full source record, including officer activities and all daily hazard procedures/tables, was inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:gos:travel-at-sea` | UNSUPPORTED | Reference-only. Ship travel speed and damage, role-restricted daily activities, crew quality checks, repairs, and daily group-check hazard outcomes depend on deferred vessel, crew, and encounter state. |

The Rule schema has no hazard/group-check model that can preserve the distinct roles, cadence, table-selected DCs, repeated fire resolution, and ship/crew outcomes. Encoding isolated checks or damage would misstate the triggers and resolution. The source-cited PHB Travel Pace and DMG Exploration Rules have no edition-matched canonical Rule outputs.

## Accounting and gap decisions

All 4 roots and 307 nested paths are accounted for: 0 INTERPRETED, 0 DUPLICATE, and 5 OMITTED dispositions. The introduction is `NON_MECHANICAL`, its PHB/DMG references are `REFERENCE_UNRESOLVED`, and the other three roots are `OUT_OF_APPROVED_SCOPE` under the approved deferred maritime families.

Deferred R-GAP-31 and R-GAP-28 were encountered. Crew-conflict mutiny outcomes also touch skipped R-GAP-44. No gap family was implemented. No duplicate register changes were needed.

## Validation and checkpoint

One permanent source-backed assertion checks identity, provenance, exact source roots/nested paths, reference-only shape, deferred/skipped gap decisions, unresolved edition-matched Rule citations, and full accounting.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 130 | 131 |
| Rule conversions pending | 101 | 100 |
| Canonical Rule outputs | 126 | 127 |
| Phase 5 canonical outputs | 446 | 447 |
| Permanent checks | 714 | 716 |

Coverage is 62 FULL, 31 PARTIAL, and 38 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 447 Phase 5 records validated, and 716 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/gos-travel-at-sea.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_13.md`
