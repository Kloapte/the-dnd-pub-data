# Phase 5 Rule Conversion - Batch 17

## Verified checkpoint before batch

RULE-CONV-16 passed validation with 150 Rules conversion-accounted, 81 pending, 146 canonical Rule outputs, 466 Phase 5 outputs, and 738 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan selects six XPHB glossary Rules, totaling 7 source-entry roots and 61 nested-clause paths. All complete source objects were inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:xphb:hit-point-dice` | PARTIAL | Represents spending Hit Dice to regain Hit Points during a Short Rest and references the related Rules. The Hit Dice contribution to maximum Hit Points is omitted. |
| `rule:xphb:object` | UNSUPPORTED | Referenceable identity only. The general object and composite-object definition has no approved Rule mechanic. |
| `rule:xphb:per-day` | FULL | Reference-only relationship to Long Rest preserves the source's exact recharge condition. |
| `rule:xphb:round-down` | UNSUPPORTED | Referenceable identity only. The contract has no standalone general arithmetic rounding mechanic; existing rounding fields are scoped to other mechanics. |
| `rule:xphb:stat-block` | PARTIAL | Typed Rule references preserve the listed Rule-owned stat block concepts and notation. The stat block structure and creature-specific statistics remain Creature-owned. |
| `rule:xphb:weapon` | PARTIAL | References the Object Rule. Simple/Martial weapon categories belong to Equipment and the source filter has no single canonical target. |

## Accounting and references

All 7 roots and 61 nested paths are accounted for: 5 INTERPRETED, 5 OMITTED, 0 DUPLICATE. Partial entries carry separate interpreted and omitted dispositions. Nested paths are explicitly listed in the covering parent disposition. The omission register contains complete source entries.

No approved gap family was encountered. All emitted references resolve to available canonical Rule outputs. No unresolved references or external dependencies remain for this batch.

## Validation and checkpoint

One permanent source-backed assertion verifies the six identities, provenance, exact root/nested totals, Hit Dice mechanic, Long Rest relationship, Stat Block and Weapon references, unsupported round-down/object shapes, and manifest reconciliation.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 150 | 156 |
| Rule conversions pending | 81 | 75 |
| Canonical Rule outputs | 146 | 152 |
| Phase 5 canonical outputs | 466 | 472 |
| Permanent checks | 738 | 745 |

Coverage is 66 FULL, 43 PARTIAL, and 47 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 472 Phase 5 records validated, and 745 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-hit-point-dice.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-object.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-per-day.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-round-down.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-stat-block.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-weapon.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_17.md`
