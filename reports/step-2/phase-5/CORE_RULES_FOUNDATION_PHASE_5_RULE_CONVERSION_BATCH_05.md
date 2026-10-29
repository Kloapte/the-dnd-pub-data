# Step 2 Phase 5 — Rule Conversion Batch 05 (RULE-CONV-05)

## Verified checkpoint and batch scope

RULE-CONV-04 validated with 108 conversion-accounted Rules, 123 pending conversions, 104 canonical Rule outputs, 424 Phase 5 outputs, and 684 permanent checks. Batch 05 membership was read from the approved conversion plan and checked against the immutable catalog: 6 identities, 14 source-entry roots, and 323 nested clause paths.

## Conversion results

All six selected records have canonical identities. Each is UNSUPPORTED mechanically and reference-only. Their complete procedures are within skipped R-GAP-13, so the coarse DOWNTIME activity component was not used to encode fragments of that subsystem.

| Canonical ID | Source path | Roots / nested paths | Coverage | Source-backed reason |
|---|---|---:|---|---|
| `rule:ai:downtime-and-franchise-activity-explore-territory` | `$.variantrule[84]` | 2 / 69 | UNSUPPORTED | Repeated exploration, Survival resolution, discovery tables, economic effects, and complications are within skipped R-GAP-13. |
| `rule:ai:downtime-and-franchise-activity-franchise-restructuring` | `$.variantrule[85]` | 2 / 44 | UNSUPPORTED | Duration, expenses, sequential checks, success bands, and complications are within skipped R-GAP-13. |
| `rule:ai:downtime-and-franchise-activity-headquarters-modification` | `$.variantrule[86]` | 3 / 44 | UNSUPPORTED | Activity permissions, team requirements, costs, multiple checks, outcome bands, and complications are within skipped R-GAP-13. |
| `rule:ai:downtime-and-franchise-activity-marketeering` | `$.variantrule[87]` | 2 / 54 | UNSUPPORTED | The three checks, DM-selected skills, random DCs, outcome bands, and complications are within skipped R-GAP-13. |
| `rule:ai:downtime-and-franchise-activity-philanthropic-enterprise` | `$.variantrule[88]` | 3 / 48 | UNSUPPORTED | Availability conditions, activity resources, DM-selected check, outcome bands, and complications are within skipped R-GAP-13. |
| `rule:ai:downtime-and-franchise-activity-running-a-franchise` | `$.variantrule[89]` | 2 / 64 | UNSUPPORTED | Monthly cadence, participant/day allocation, percentile resolution, financial outcomes, payment penalties, and complications are within skipped R-GAP-13. |

The six source introductions that are descriptive are omitted as `NON_MECHANICAL`; substantive procedure roots are omitted as `OUT_OF_APPROVED_SCOPE` under R-GAP-13. No canonical references were emitted for unresolved book, creature, or franchise entities. Full original source entries and nested paths are preserved in the omission register and manifest.

## Accounting and validation

All 14 roots and all 323 semantic-inventory nested paths are accounted. The disposition count is 0 INTERPRETED, 0 DUPLICATE, and 14 OMITTED. Nested paths are traced by their root dispositions; they are not added to the source-root count.

| Metric | Before | After |
|---|---:|---:|
| Conversion-accounted | 108 | 114 |
| Pending conversion | 123 | 117 |
| Canonical Rule outputs | 104 | 110 |
| Phase 5 canonical outputs | 424 | 430 |
| Permanent checks | 684 | 691 |

Rule coverage is now 62 FULL, 29 PARTIAL, and 23 UNSUPPORTED. Semantic review remains complete at 231 reviewed and 0 pending. The legacy `pendingSemanticReviewCount` remains unchanged.

`npm run validate:core-rules` passes: 9 schemas, 89 examples, 34 Phase 3 records, 430 Phase 5 records, and 691 permanent checks. `git diff --check` passes with line-ending warnings only; no whitespace errors were reported.

## Files changed for RULE-CONV-05

- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-explore-territory.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-franchise-restructuring.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-headquarters-modification.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-marketeering.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-philanthropic-enterprise.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/ai-downtime-and-franchise-activity-running-a-franchise.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_05.md`

No schema, immutable source, unrelated Rule, or other domain record was changed. Continue only with the next authorized plan batch.
