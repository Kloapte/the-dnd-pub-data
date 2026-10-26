# Phase 5 Rule Conversion - Batch 19

## Verified checkpoint before batch

RULE-CONV-18 passed validation with 167 Rules conversion-accounted, 64 pending, 163 canonical Rule outputs, 483 Phase 5 outputs, and 757 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan selects eight Rules, totaling 10 source-entry roots and 10 nested-clause paths. All full source records were inspected.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:dmg:loyalty` | PARTIAL | Represents the 10-point high-loyalty threshold with SOCIAL_PROCEDURE/LOYALTY. The 0–20 scale, Charisma-based maximum/start, d4 changes, triggers, and behavior outcomes are omitted. |
| `rule:xphb:ally` | PARTIAL | References Creature and Attitude concepts. Party membership, friendship, combat-side, and designation predicates are not modeled. |
| `rule:xphb:enemy` | PARTIAL | References Creature and Attitude concepts. Combat opposition, intent to harm, and designation predicates are not modeled. |
| `rule:xphb:friendly-attitude` | PARTIAL | Represents Friendly attitude and references Ability Check and Advantage. The Advantage trigger cannot be bound to influencing a Friendly creature. |
| `rule:xphb:hostile-attitude` | PARTIAL | Represents Hostile attitude and references Ability Check and Disadvantage. The Disadvantage trigger cannot be bound to influencing a Hostile creature. |
| `rule:xphb:indifferent-attitude` | FULL | Represents Indifferent attitude and references its default use for Monsters. |
| `rule:xphb:attitude` | FULL | Represents the Friendly/Hostile/Indifferent starting choices and Monster-to-Player Character relationship. |
| `rule:xphb:nonplayer-character` | PARTIAL | References Monster. The personal-name and distinct-personality properties are not modeled. |

Five Rules contain Social mechanics; Ally, Enemy, and Nonplayer Character are reference-only. Existing patterns R-PAT-13 (Social procedures/attitudes) and R-PAT-02 (D20 check modifiers) were reused without adding a new mechanic pattern.

## Accounting and references

All 10 roots and 10 nested paths are accounted for: 10 INTERPRETED, 8 OMITTED, 0 DUPLICATE. Partial root entries have both represented and omitted dispositions. Nested paths are identified in their covering dispositions, and complete omitted source entries are preserved in the omission register.

All attitude, Creature, Ability Check, Advantage, Disadvantage, and Monster relationships resolve to canonical outputs. The DMG Loyalty reprint target `rule:xdmg:loyalty` is absent from the pinned Rule catalog; it remains recorded as `REFERENCE_UNRESOLVED` and is not invented. The Renown-to-attitude references from Batch 18 now resolve to these outputs.

No approved gap family was encountered.

## Validation and checkpoint

One permanent source-backed assertion verifies the eight identities, exact paths/counts, provenance, SOCIAL mechanics, reference relationships, loyalty reprint omission, coverage, and manifest reconciliation.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 167 | 175 |
| Rule conversions pending | 64 | 56 |
| Canonical Rule outputs | 163 | 171 |
| Phase 5 canonical outputs | 483 | 491 |
| Permanent checks | 757 | 766 |

Coverage is 68 FULL, 54 PARTIAL, and 53 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 491 Phase 5 records validated, and 766 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-loyalty.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-ally.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-enemy.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-friendly-attitude.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-hostile-attitude.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-indifferent-attitude.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-attitude.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-nonplayer-character.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_19.md`
