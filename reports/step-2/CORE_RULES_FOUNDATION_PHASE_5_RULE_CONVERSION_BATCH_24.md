# Phase 5 Rule Conversion — Batch 24

## Verified checkpoint before batch

RULE-CONV-23 passed validation with 193 Rules conversion-accounted, 38 pending conversions, 189 canonical Rule outputs, 509 Phase 5 canonical outputs, and 788 permanent checks. All 231 Rules remain semantically reviewed.

The plan selects these 13 Rules and exact source paths: `rule:vrgr:survivors` (`$.variantrule[215]`), `rule:xge:spellcasting` (`$.variantrule[209]`), `rule:xphb:armor-training` (`$.variantrule[13]`), `rule:xphb:cantrip` (`$.variantrule[23]`), `rule:xphb:curses` (`$.variantrule[40]`), `rule:xphb:illusions` (`$.variantrule[132]`), `rule:xphb:magical-effect` (`$.variantrule[150]`), `rule:xphb:possession` (`$.variantrule[174]`), `rule:xphb:ritual` (`$.variantrule[184]`), `rule:xphb:shape-shifting` (`$.variantrule[189]`), `rule:xphb:spell` (`$.variantrule[206]`), `rule:xphb:spellcasting-focus` (`$.variantrule[210]`), and `rule:xphb:telepathy` (`$.variantrule[219]`). The batch contains 26 roots and 155 nested paths. Complete source objects were inspected.

## Conversion results

| Canonical ID | Coverage | Representation |
|---|---|---|
| `rule:vrgr:survivors` | PARTIAL | Represents Desperate Scream's Advantage on a saving throw. Survivor progression, talent use limits/recovery, and other talents are omitted; progression is deferred under R-GAP-37. |
| `rule:xge:spellcasting` | PARTIAL | Represents an Intelligence (Arcana) check to identify a perceived spell at DC 15 + spell level. Casting observability and invalid-target resolution are skipped under R-GAP-35; action/reaction timing, class-matching Advantage, and grid template/token placement are omitted. |
| `rule:xphb:armor-training` | PARTIAL | Typed references preserve the Disadvantage and D20 Test relationships. Armor category/proficiency prerequisites, spellcasting restriction, and shield AC consequence are not structured. |
| `rule:xphb:cantrip` | PARTIAL | A typed Rule relationship preserves the level-zero/no-slot relationship to Spell; the schema has no structured spell level or slot-expenditure property. |
| `rule:xphb:curses` | UNSUPPORTED | Reference-only; generic curse state/removal is not modeled, and the named spells have no canonical Spell outputs here. |
| `rule:xphb:illusions` | UNSUPPORTED | Reference-only; effect-defined sensory deception and environment-coupled physical appearance are not represented by the Rule schema. |
| `rule:xphb:magical-effect` | UNSUPPORTED | Reference-only; the schema has no predicate classifying an effect by spell, item, or rule label. |
| `rule:xphb:possession` | UNSUPPORTED | Reference-only; effect-defined possession state is unsupported, and the named prevention/termination spells are unresolved. |
| `rule:xphb:ritual` | UNSUPPORTED | Reference-only; additive casting time, no-slot casting, Ritual eligibility, and no-upcast restriction cannot be expressed together. |
| `rule:xphb:shape-shifting` | PARTIAL | Typed references preserve Condition and death relationships. Ongoing effect carryover and death-triggered reversion are not modeled. |
| `rule:xphb:spell` | PARTIAL | Typed reference preserves that a Spell is a Magical Effect; detailed Spell characteristics remain in the Spell domain. |
| `rule:xphb:spellcasting-focus` | UNSUPPORTED | Reference-only; focus substitution depends on Item, Class, and Spell-component identities not represented by the Rule contract. |
| `rule:xphb:telepathy` | PARTIAL | Typed reference preserves the Incapacitated restriction. Range, language comprehension, asymmetric initiation/reply, and contact state are unsupported. |

Two Rules contain mechanics and 11 are reference-only. Existing `D20_TEST / TEST_MODIFIER`, `D20_TEST / ABILITY_CHECK`, fixed-plus-spell-level DC, and typed reference patterns were reused. No new patterns or schema properties were introduced.

## Source accounting and dependencies

All 26 roots and 155 nested paths are accounted for. Disposition totals are 7 INTERPRETED, 28 OMITTED, and 0 DUPLICATE; multiple portions within roots account for the disposition count exceeding roots. Nested paths are explicitly attached to their parent dispositions. Complete omitted source entries were added to the omission register.

Encountered skipped R-GAP-35 (spell observability and invalid-target resolution), the already-implemented R-GAP-36 area origin/geometry family (the XGE template/token grid procedure remains outside the approved geometry contract), and deferred R-GAP-37 (survivor progression/talent lifecycle). None was expanded.

Unresolved Spell references are Remove Curse, Greater Restoration, Protection from Evil and Good, and Dispel Evil and Good. Survivor stat blocks, Class features, focus Items, and spell-component data are documented as external domain dependencies; no target identities were invented. All emitted Rule/Condition/Skill references resolve.

## Validation and checkpoint

A permanent source-backed assertion verifies all 13 identities, source metadata/edition, exact root and nested-path totals, the two structured mechanics, exact DC/Skill values, reference resolution, the reference-only records, and omission of unsupported state, grid, spell, and equipment procedures.

| Metric | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Conversion-accounted | 193 | 206 |
| Pending conversion | 38 | 25 |
| Canonical Rule outputs | 189 | 202 |
| Phase 5 canonical outputs | 509 | 522 |
| Permanent checks | 788 | 802 |

Coverage is 68 FULL, 65 PARTIAL, and 73 UNSUPPORTED. `npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 522 Phase 5 records validated, and 802 permanent checks passed. `git diff --check` passed.

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/vrgr-survivors.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xge-spellcasting.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-armor-training.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-cantrip.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-curses.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-illusions.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-magical-effect.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-possession.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-ritual.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-shape-shifting.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-spell.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-spellcasting-focus.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-telepathy.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_24.md`
