# Core Rules Foundation Phase 5 — Rule Gaps Batch 1

**Status:** Implemented for human review. Step 2 Phase 5 remains open and unapproved.

## Scope

This batch implements R-GAP-01, R-GAP-05, R-GAP-06, and R-GAP-10 only. It does not implement any other Rule gap family or begin Batch 2.

## Implemented mechanics

### R-GAP-01 — Ability score modifiers

Added a bounded `ABILITY_SCORE_RULE/MODIFIER_MAPPING` component to [rule.schema.json](../../schemas/core-rules/v1/rule.schema.json). It records the six ability identities, score range 1–30, and the 16 exact score bands and modifiers. The immutable XPHB source record is `$.variantrule[1]` (`rule:xphb:ability-score-and-modifier`); its entry establishes six abilities and corresponding modifiers but does not include the table itself. The band values are corroborated by the official [2024 Basic Rules, Playing the Game](https://www.dndbeyond.com/sources/dnd/br-2024/playing-the-game#AbilityModifiers) ability-modifier table. This evidence distinction is retained in the implementation: the canonical record links the mapping to the XPHB source identity, while the table itself is verified against the official rules publication.

### R-GAP-05 — Difficulty Class

Added `D20_TEST/DIFFICULTY_CLASS` to `rule:xphb:difficulty-class` from `$.variantrule[54].entries[0]`, which defines DC as the target number for ability checks and saving throws. `abilityCheck.dc` now accepts either a positive fixed integer or the existing shared typed `difficultyClass` value. No formula language was added. The shared union remains limited to its established variants: `FIXED`, `ABILITY_PLUS_PROFICIENCY`, `FIXED_PLUS_SPELL_LEVEL`, and `GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE`.

The XPHB Difficulty Class entry does not itself define formulas. The finite formulas are retained as source-qualified values already used by approved procedures, including Identify a Spell and Influence. The existing Ability Check record does not receive an invented default DC: the DM may determine a DC, and a particular procedure may supply a fixed or formula-based DC.

### R-GAP-06 — Proficiency and scaling

Added separate `PROFICIENCY_RULE` subtypes rather than combining distinct source procedures:

- `BONUS_APPLICATION`: add the proficiency bonus to the source-listed check/roll types (`rule:xphb:proficiency`, `$.variantrule[175].entries[0]`).
- `BONUS_PROGRESSION`: the official 2024 Proficiency Bonus table gives the value by character level or monster Challenge Rating, from +2 through +9 (`$.variantrule[175]` identifies the Rule and uses its page/source metadata). Level bands and Challenge Rating bands are explicit and separate; fractional CR values remain representable. This table is corroborated against the official [2024 Basic Rules, Playing the Game](https://www.dndbeyond.com/sources/dnd/br-2024/playing-the-game#ProficiencyBonus).
- `BONUS_SCALING`: Expertise doubles the bonus for a proficient skill ability check, with the source's already-doubled exception and one-assignment-per-skill limit (`rule:xphb:expertise`, `$.variantrule[103].entries[0..1]`).
- `PROFICIENCY_DICE_ALTERNATIVE`: DMG's optional replacement, level bands, Expertise rolling two dice, and player-character/leveled-NPC applicability (`rule:dmg:proficiency-dice`, `$.variantrule[176].entries[0..3]`). Its source `ruleType` is `O`, represented as `OPTIONAL`.
- `BONUS_LEVEL_BASIS`: PHB Multiclassing says proficiency bonus is based on total character level (`rule:phb:multiclassing`, `$.variantrule[157].entries[6].entries[0]`). The class, character, XP, Hit Point, proficiency-acquisition, and multiclass aggregation procedures remain external or deferred; the Rule is PARTIAL.
- `SKILL_PROFICIENCY_APPLICATION`: Skill identifies the associated ability check and adds proficiency when the creature has the relevant proficiency (`rule:xphb:skill`, `$.variantrule[201].entries[0]`).
- `TOOL_SKILL_PROFICIENCY_SYNERGY`: XGE's optional Advantage guidance when both tool and skill apply and both proficiencies are held (`rule:xge:tool-proficiencies`, `$.variantrule[222].entries[0..1]`). The record remains PARTIAL: tool-specific crafting, repair, item, and open-ended added-benefit procedures are not copied into Rule mechanics and depend on other approved/future contracts.

Character Building still owns proficiency acquisition and character state; Classes own feature grants and progression. This batch represents only the Rule-side calculation or application statements. It does not implement multiclass level aggregation, class features, or spell-slot progression (R-GAP-30).

### R-GAP-10 — Passive-score construction

Extended `rule:xphb:passive-perception` using `PASSIVE_SCORE_CONSTRUCTION`: base 10, add the check bonus, increase by 5 with Advantage, and decrease by 5 with Disadvantage. This accounts for `$.variantrule[166].entries[1]`; entry 0's use as general awareness without a conscious check remains represented by the existing passive-score flag. The record is now FULL.

## Canonical records

Eight new Phase 5 Rule records were created:

| Gap | Canonical identity | Source locator | Coverage |
|---|---|---|---|
| R-GAP-01 | `rule:xphb:ability-score-and-modifier` | `$.variantrule[1]` | FULL |
| R-GAP-05 | `rule:xphb:difficulty-class` | `$.variantrule[54]` | FULL |
| R-GAP-06 | `rule:xphb:expertise` | `$.variantrule[103]` | FULL |
| R-GAP-06 | `rule:xphb:proficiency` | `$.variantrule[175]` | FULL |
| R-GAP-06 | `rule:dmg:proficiency-dice` | `$.variantrule[176]` | FULL |
| R-GAP-06 | `rule:xphb:skill` | `$.variantrule[201]` | FULL |
| R-GAP-06 | `rule:phb:multiclassing` | `$.variantrule[157]` | PARTIAL |
| R-GAP-06 | `rule:xge:tool-proficiencies` | `$.variantrule[222]` | PARTIAL |

The existing `rule:xphb:passive-perception` record was extended and reclassified from PARTIAL to FULL. Six previously UNSUPPORTED Rule identities now have canonical records; no unsupported Rule was given placeholder mechanics.

## Accounting and coverage

The Phase 5 Rule batch now accounts for 25 examined non-representative records: 14 FULL, 5 PARTIAL, and 6 UNSUPPORTED. It has 17 canonical outputs because two FULL alias records (`Save` and `Weapon Attack`) reuse already represented identities and do not receive duplicate canonical definitions. The batch has 50 source-entry roots and 22 INTERPRETED, 2 DUPLICATE, and 31 OMITTED classification parts. The two newly added source records account for 12 roots: 9 for PHB Multiclassing and 3 for XGE Tool Proficiencies. Existing complete omitted-source entries were reconciled in `doc/rule/omitted.md`; represented portions no longer remain marked `SCHEMA_UNSUPPORTED`.

Across the Phase 5 expansion manifest, there are now 342 canonical records, with 193 FULL, 151 PARTIAL, and 9 UNSUPPORTED source records across the examined batches. Its source-accounting totals are 226 INTERPRETED, 26 DUPLICATE, and 221 OMITTED classification parts. The Rule catalog checkpoint has 30 accounted records (including five Phase 3/4 representatives): 18 FULL, 6 PARTIAL, and 6 UNSUPPORTED, with 22 total Rule canonical outputs. The semantic discovery inventory remains separate and unchanged at 231 reviewed source records, 201 pending canonical accounting, and its previously reported semantic contract-fit classifications.

## Validation

Permanent source-backed assertions cover the exact ability score bands; the DC target procedures and finite shared formula variants; Expertise, proficiency application, proficiency-dice bands and eligible creature categories; multiclass total-level basis; XGE tool/skill Advantage guidance; and the passive-score formula. Negative cases reject an incorrect ability boundary, arbitrary DC expression, unsupported proficiency die, incorrect passive adjustment, and repeated Expertise assignment.

`npm run validate:core-rules` passes: 9 Draft 2020-12 schemas compiled, 68 examples validated, 34 Phase 3/4 canonical records validated, 342 Phase 5 canonical records validated, 552 permanent checks passed, 24 Rule components checked, and 27 Action records checked. Reference-only Rule support remains valid. `git diff --check` is reported after the final edit pass. Structural validation and assertions do not replace human source-mechanics review.

## Remaining limitations

- The XPHB Ability Score and Modifier JSON entry names the abilities and corresponding modifiers but does not embed the modifier table; the table was checked against the official 2024 Basic Rules page cited above.
- Difficulty Class defines a target number but does not prescribe a universal formula. Formula variants remain attached to procedures that establish them; DM-selected DCs are not converted into formulas.
- Multiclass class-level aggregation, prerequisite tables, XP advancement, class proficiencies, Hit Dice/Hit Points, and class-feature interactions remain partial and outside this batch.
- XGE Tool Proficiencies includes extensive tool-specific benefits and procedures. This batch models the bounded optional Advantage interaction only; the rest remains external, specialized, or out of the approved gap scope.
- No Java execution behavior, other Rule gap family, or later batch was implemented.
