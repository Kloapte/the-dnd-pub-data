# Step 2 Phase 5 — Rule Conversion Batch 04 (RULE-CONV-04)

## Verified checkpoint

The RULE-CONV-03 checkpoint matched before changes: 231 Rule identities and semantic reviews, 104 conversion-accounted Rules, 127 pending conversions, 100 canonical Rule outputs, 420 Phase 5 outputs, and 679 permanent checks. The validator passed before conversion.

The plan assigned four identities, seven source-entry roots, and zero nested clause paths. The source paths were verified against the immutable Rule catalog.

| Canonical ID | Source path | Roots / nested paths | Coverage | Conversion |
|---|---|---:|---|---|
| `rule:xphb:stable` | `$.variantrule[212]` | 1 / 0 | FULL | Uses the approved stabilized death-state component and references Hit Points and Death Saving Throw. |
| `rule:xphb:teleportation` | `$.variantrule[220]` | 4 / 0 | UNSUPPORTED | Reference-only. All four entries depend on skipped R-GAP-21 teleportation side effects or spatial fallback; no part of that gap is encoded. |
| `rule:xphb:temporary-hit-points` | `$.variantrule[221]` | 1 / 0 | FULL | Uses the approved temporary Hit Points state and references real Hit Points. |
| `rule:xphb:unoccupied-space` | `$.variantrule[226]` | 1 / 0 | PARTIAL | Links the creature-occupancy inverse to Occupied Space. The object-filled alternative is omitted because there is no canonical Object target. |

Two outputs contain structured mechanics; two are reference-only. All emitted references resolve. No schema or runtime behavior was added.

## Accounting and validation

All seven roots are accounted: 3 INTERPRETED, 0 DUPLICATE, and 5 OMITTED dispositions. The four Teleportation roots are omitted as `OUT_OF_APPROVED_SCOPE` under R-GAP-21. The Unoccupied Space object alternative is omitted as `REFERENCE_UNRESOLVED`. Complete original source entries are in the Rule omission register.

| Metric | Before | After |
|---|---:|---:|
| Conversion-accounted | 104 | 108 |
| Pending conversion | 127 | 123 |
| Canonical Rule outputs | 100 | 104 |
| Phase 5 canonical outputs | 420 | 424 |
| Permanent checks | 679 | 684 |

Global Rule coverage is now 62 FULL, 29 PARTIAL, and 17 UNSUPPORTED. Semantic review remains complete at 231 reviewed and 0 pending. The legacy `pendingSemanticReviewCount` remains unchanged.

`npm run validate:core-rules` passes: 9 schemas, 89 examples, 34 Phase 3 records, 424 Phase 5 records, and 684 permanent checks. `git diff --check` passes with line-ending warnings only; no whitespace errors were reported.

## Files changed for RULE-CONV-04

- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-stable.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-teleportation.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-temporary-hit-points.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-unoccupied-space.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_04.md`

RULE-CONV-04 is complete. Continue only to the next authorized batch.
