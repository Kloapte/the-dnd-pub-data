# Phase 5 Rule Conversion - Batch 16

## Verified checkpoint before batch

RULE-CONV-15 passed validation with 138 Rules conversion-accounted, 93 pending, 134 canonical Rule outputs, 454 Phase 5 outputs, and 725 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan selects the following 12 Rules, totaling 34 source-entry roots and 254 nested-clause paths. All complete source objects were inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:cos:prophet-s-gambit` | UNSUPPORTED | Reference-only. The complete card game depends on skipped R-GAP-22. |
| `rule:dmg:alien-technology` | PARTIAL | Represents the Intelligence ability check. Repeated success/failure counters, result table, charge loss, breakage, and conditional Advantage are unsupported. |
| `rule:dmg:fear-and-horror` | PARTIAL | Represents Wisdom and Charisma saves with circumstance-determined DCs and references Frightened. Failure outcomes, timing, and madness selection are not modeled. |
| `rule:dmg:hero-points` | UNSUPPORTED | Reference-only because gain, reset, expenditure, and result conversion belong to deferred R-GAP-46. |
| `rule:phb:equipment-sizes` | UNSUPPORTED | Reference-only. Equipment fit, alterations, disguise visibility, and alteration prices are not expressible by the Rule contract and remain Equipment-owned. |
| `rule:scag:human-languages` | PARTIAL | References the source-listed edition-matched language identities and Common. Ethnicity mapping and conditional language grants are not represented. |
| `rule:vrgr:fear-and-stress` | PARTIAL | Represents the DC 15 Wisdom save and references Frightened. Seeds of Fear/stress mechanics are excluded under skipped R-GAP-16; fear triggers and failure timing are unsupported. |
| `rule:xphb:attunement` | PARTIAL | Represents the Attunement category; the three-item concurrency limit has no schema field. |
| `rule:xphb:challenge-rating` | UNSUPPORTED | Reference-only. Challenge Rating threat and encounter-planning interpretation has no approved Rule mechanic. |
| `rule:xphb:condition` | PARTIAL | References all 15 XPHB Conditions. Non-stacking and the Exhaustion exception cannot be imposed through Rule references. |
| `rule:xphb:dead` | PARTIAL | Retains references to Hit Points, Condition, Attunement, and Exhaustion. Revival state transitions, spirit refusal, and effect restoration are unsupported. |
| `rule:xphb:healing` | FULL | Represents healing as regaining Hit Points through the approved Healing mechanic. |

## Accounting, gaps, and references

All 34 roots and 254 nested-clause paths are accounted for. The root entries contain 11 INTERPRETED and 32 OMITTED classifications, with no DUPLICATE dispositions; roots containing both an interpreted and omitted portion are represented as separate classifications. Each omitted nested path is listed in its parent disposition. The omission register includes the full source entries.

Skipped R-GAP-22 (Prophet's Gambit), R-GAP-46 (Hero Points), and R-GAP-16 (Fear and Stress) were encountered but not implemented. No deferred gap was revived.

SCAG language references resolve to canonical language records, XPHB Condition references resolve to exact edition records, and the PHB Frightened reference resolves. The DMG Alien Technology source reprint to `rule:xdmg:alien-technology` is unresolved because that identity is absent from the pinned Rule catalog; it is recorded as `REFERENCE_UNRESOLVED` and omitted from canonical metadata references. Raise Dead and Revivify are source-cited but lack canonical Spell outputs; those targets are likewise documented as unresolved rather than invented.

## Validation and checkpoint

One permanent source-backed assertion checks all 12 identities, exact source roots and nested counts, provenance, mechanical details, reference resolution, skipped gap accounting, and reference-only shapes. Existing aggregate checks were updated to the new manifest checkpoint without weakening their assertions.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 138 | 150 |
| Rule conversions pending | 93 | 81 |
| Canonical Rule outputs | 134 | 146 |
| Phase 5 canonical outputs | 454 | 466 |
| Permanent checks | 725 | 738 |

Coverage is 65 FULL, 40 PARTIAL, and 45 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 466 Phase 5 records validated, and 738 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/cos-prophet-s-gambit.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-alien-technology.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-fear-and-horror.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-hero-points.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/phb-equipment-sizes.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/scag-human-languages.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/vrgr-fear-and-stress.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-attunement.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-challenge-rating.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-condition.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-dead.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-healing.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_16.md`
