# Phase 5 Rule Conversion - Batch 15

## Verified checkpoint before batch

RULE-CONV-14 passed validation with 133 Rules conversion-accounted, 98 pending, 129 canonical Rule outputs, 449 Phase 5 outputs, and 719 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan selects five XPHB environment and sense definitions (5 source-entry roots, 0 nested-clause paths). Each complete source object was inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:xphb:bright-light` | UNSUPPORTED | Referenceable identity only. The source defines normal illumination, which has no approved Rule mechanic. |
| `rule:xphb:darkness` | FULL | Retains the exact relationship that Darkness is Heavily Obscured using a canonical Rule reference. |
| `rule:xphb:dim-light` | FULL | Retains the exact relationship that Dim Light is Lightly Obscured using a canonical Rule reference. |
| `rule:xphb:heavily-obscured` | PARTIAL | Represents heavily obscured level and blocked vision; references XPHB Blinded. The conditional application of Blinded while trying to see is not expressible by the obscuration component. |
| `rule:xphb:lightly-obscured` | PARTIAL | Represents lightly obscured level and disadvantage on sight-dependent Perception. The exact Wisdom (Perception), target, and “to see” restriction is not expressible by the component. |

## Accounting and references

All five source roots are accounted for; there are no nested paths or duplicates. The root dispositions contain 5 INTERPRETED and 3 OMITTED classifications because two roots contain both a represented portion and an explicit schema omission. The omissions are 3 `SCHEMA_UNSUPPORTED`: normal illumination, the heavy obscuration condition trigger, and the light obscuration check scope.

Both obscuration references resolve to the selected XPHB Rule records. The Blinded reference resolves to the Phase 3 XPHB Condition. No unresolved references or gap families were encountered. The omission register now includes complete source evidence for all three omitted portions.

## Validation and checkpoint

The permanent assertion verifies the five identities, exact source paths and root counts, provenance, the two reference relationships, both structured obscuration mechanics, explicit partial-coverage omissions, and manifest totals.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 133 | 138 |
| Rule conversions pending | 98 | 93 |
| Canonical Rule outputs | 129 | 134 |
| Phase 5 canonical outputs | 449 | 454 |
| Permanent checks | 719 | 725 |

Coverage is 64 FULL, 33 PARTIAL, and 41 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 454 Phase 5 records validated, and 725 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-bright-light.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-darkness.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-dim-light.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-heavily-obscured.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-lightly-obscured.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_15.md`
