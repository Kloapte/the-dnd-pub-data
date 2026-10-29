# Phase 5 Rule Conversion — Batch 06

## Verified checkpoint before batch

RULE-CONV-05 passed `npm run validate:core-rules` and `git diff --check` with 114 of 231 Rules conversion-accounted, 117 pending, 110 canonical Rule outputs, 430 Phase 5 canonical outputs, and 691 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan assigns four Acquisitions Incorporated franchise activities to RULE-CONV-06: 4 Rules, 9 source-entry roots, and 197 nested-clause paths. All source objects and nested entries were inspected.

| Canonical ID | Source path | Roots / nested paths | Coverage | Conversion |
|---|---|---:|---|---|
| `rule:ai:downtime-and-franchise-activity-schmoozing` | `$.variantrule[90]` | 2 / 46 | UNSUPPORTED | Reference-only. The activity procedure, expenses, checks, outcome bands, and complications fall under skipped R-GAP-13. Introductory narrative is nonmechanical. |
| `rule:ai:downtime-and-franchise-activity-scrutineering` | `$.variantrule[91]` | 2 / 44 | UNSUPPORTED | Reference-only. Resources, expenses, checks, outcome bands, and complications fall under skipped R-GAP-13. Introductory examples do not define an independent mechanic. |
| `rule:ai:downtime-and-franchise-activity-shady-business-practice` | `$.variantrule[92]` | 2 / 53 | UNSUPPORTED | Reference-only. Activity resources, checks, risk outcomes, consequences, and complications fall under skipped R-GAP-13. Introductory narrative is nonmechanical. |
| `rule:ai:downtime-and-franchise-activity-team-building` | `$.variantrule[93]` | 3 / 54 | UNSUPPORTED | Reference-only. Head Office triggers and the procedure's participant, duration, expense, check, result, memory, and complication rules fall under skipped R-GAP-13. Introductory examples are nonmechanical. |

The records preserve their AI source metadata and source-page provenance. No mechanics or references were added because none of these procedures has an independently meaningful portion outside the approved skipped downtime subsystem. Existing reference-only Rule conventions were reused; no schema or mechanics pattern was added.

## Source accounting and gaps

All 9 roots and 197 nested paths are covered by root dispositions whose portions explicitly include the observed nested paths. The 9 root dispositions are 0 INTERPRETED, 0 DUPLICATE, and 9 OMITTED: 4 `NON_MECHANICAL` introductory portions and 5 `OUT_OF_APPROVED_SCOPE` procedural portions. R-GAP-13 is the only gap family encountered. No unresolved references or external dependencies were identified.

The omission register now records the source-root paths, omitted content, approved gap decision, and full original source entry for each root. No duplicate register entries were required.

## Validation assertions and checkpoint

One permanent source-backed assertion was added. It checks the four identities, source paths and metadata, exact root/nested counts, reference-only schema shape, UNSUPPORTED classification, R-GAP-13 linkage, complete nested-path accounting, and batch-level accounting totals.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 114 | 118 |
| Rule conversions pending | 117 | 113 |
| Canonical Rule outputs | 110 | 114 |
| Phase 5 canonical outputs | 430 | 434 |
| Permanent checks | 691 | 696 |

Coverage is now 62 FULL, 29 PARTIAL, and 27 UNSUPPORTED. The conversion-accounting count is distinct from the 231/231 complete semantic reviews.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 434 Phase 5 records validated, and 696 permanent checks passed. `git diff --check` passed (Git may report configured LF-to-CRLF notices).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-schmoozing.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-scrutineering.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-shady-business-practice.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-team-building.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_06.md`
