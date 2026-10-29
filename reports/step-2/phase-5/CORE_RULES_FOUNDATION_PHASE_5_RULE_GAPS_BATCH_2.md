# Core Rules Foundation Phase 5 — Rule Gaps Batch 2

**Status:** Implemented for human review. Step 2 Phase 5 remains open and unapproved.

## Scope

This batch implements the approved portions of R-GAP-04, R-GAP-07, the standard-Cover scope of R-GAP-08, and R-GAP-11. It does not begin Batch 3. The original R-GAP-08 inventory concerned optional DMG Hitting Cover; the approved implementation scope instead covers the standard XPHB Cover rule. Hitting Cover remains mechanically unsupported and is kept as a reference-only Rule.

## Source-backed changes

| Gap | Source identity and locator | Canonical result | Coverage |
|---|---|---|---|
| R-GAP-04 | `rule:xphb:death-saving-throw`, `$.variantrule[51].entries[0]` | Extended with `appliesTo: PLAYER_CHARACTER`; retained the start-of-turn at 0 Hit Points trigger. | FULL |
| R-GAP-07 | `rule:dmg:flanking`, `$.variantrule[113].entries[0]` and nested square/hex procedures | Added optional Flanking with miniature use, enemy and participant roles, visibility and Incapacitated requirements, square/hex alternatives, size-dependent hex separations, Large-or-larger space qualification, and attack Advantage. | FULL |
| R-GAP-08 | `rule:xphb:cover`, `$.variantrule[32].entries[0]` | Added half (+2 AC and Dexterity saves), three-quarters (+5 AC and Dexterity saves), and total Cover (direct targeting blocked); only the most protective degree applies. | FULL |
| R-GAP-11 | `rule:phb:skills-with-different-abilities`, `$.variantrule[203].entries[0]` | Replaced two general Ability Check components with a typed discretionary Skill/ability variation and the two source examples. | FULL |
| R-GAP-11 | `rule:dmg:skill-variants`, `$.variantrule[202].entries[0..4]` | Added the bounded Ability Check Proficiency variant for two proficiency sources and level-1 application. Class-table and Expertise changes, plus Background and Personality Trait variants, remain omitted. | PARTIAL |
| R-GAP-08 inventory scope | `rule:dmg:hitting-cover`, `$.variantrule[128].entries[0..1]` | Added identity-only `OPTIONAL` Rule with source metadata and no mechanics. | UNSUPPORTED |

## Contract changes

The Rule schema adds a Player Character eligibility value to the specialized Death Saving Throw; a bounded `POSITIONAL_MODIFIER/FLANKING`; `SKILL_PROCEDURE/ABILITY_VARIATION` with source-qualified Skill references and discretionary decision ownership; and `PROFICIENCY_RULE/ABILITY_CHECK_PROFICIENCY_VARIANT`. Existing `COVER` components now constrain source-exact benefits by degree, identify Dexterity as the saving-throw ability, and express `MOST_PROTECTIVE_ONLY`. Total Cover cannot carry numerical bonuses. No damage-reduction structure was introduced.

Flanking distinguishes the square-grid center-line tie-break from the hex-grid procedure. Hex separation is asserted as 2 for Tiny/Small/Medium, 4 for Large, 5 for Huge, and at least 6 for Gargantuan. For a Large-or-larger flanking creature, at least one square or hex of its own space must qualify. The canonical data describes the rule; it does not calculate positions, adjacency, line of sight, or encounter state.

## Source accounting and coverage

The Phase 5 Rule batch now covers 26 non-representative source records, excluding five Phase 3/4 representatives: 18 FULL, 4 PARTIAL, and 4 UNSUPPORTED, with 21 Phase 5 canonical Rule outputs and 51 source-entry roots. Its dispositions are 25 INTERPRETED, 3 DUPLICATE, and 28 OMITTED. Four canonical files were added in this batch: Flanking, standard Cover, Skill Variants, and reference-only Hitting Cover.

Across the Rule catalog, 31 records are accounted: 22 FULL, 5 PARTIAL, and 4 UNSUPPORTED, with 26 canonical Rule outputs. The remaining 200 Rule records are still pending Phase 5 conversion accounting; semantic discovery remains a separate artifact.

The Phase 5 manifest now records 346 canonical outputs, 197 FULL, 150 PARTIAL, and 7 UNSUPPORTED source records; 327 roots are accounted as 229 INTERPRETED, 27 DUPLICATE, and 218 OMITTED classification parts. Death Saving Throw and PHB Skills with Different Abilities no longer have omissions for represented mechanics. Flanking's former schema omission was removed. DMG Skill Variants preserves complete original entries in the duplicate/omission registers for classified portions. Hitting Cover's two mechanical omissions remain intact and its reference-only output path is recorded.

## Explicit exclusions and dependencies

- Hitting Cover's attack-roll comparison and redirection outcomes are not represented. Standard Cover does not imply those optional mechanics, and Hitting Cover remains UNSUPPORTED.
- The DMG Skill Variants class-to-ability table and Expertise changes depend on Class and feature contracts. Background selection, personality traits, and proficiency acquisition depend on Character Building. These clauses remain accounted rather than being forced into the Rule component.
- No PHB Cover source record exists in the inspected `variantrules.json` collection; standard Cover is represented from XPHB without asserting cross-edition identity.
- Flanking is optional and miniature-based. It preserves the source's square/hex criteria; future encounter logic determines whether those criteria are met in a live state.

## Validation

`npm run validate:core-rules` passes: 9 Draft 2020-12 schemas compiled, 73 examples validated, 34 Phase 3/4 canonical records validated, 346 Phase 5 canonical records validated, 569 permanent checks passed, 30 Rule components checked, and 27 Action records checked. Positive and negative tests cover Death Saving Throw eligibility, Flanking hex separation, standard Cover values and prohibited damage reduction, Skill/ability pairing integrity, and the DMG proficiency variant. Rule references, metadata, registers, uniqueness, and Batch 1 assertions also pass.

These checks establish schema validity and selected semantic invariants. They do not replace source review or establish complete mechanical correctness. `git diff --check` is run after report updates.

No Batch 3, Phase 6, or other domain was started. Phase 5 remains open and unapproved.
