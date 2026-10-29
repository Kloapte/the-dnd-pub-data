# Core Rules Foundation — Phase 3 Representative Conversion

## Corrected execution

**Status:** corrected representative conversion completed; awaiting human review. Phase 4 has not started.

This execution supersedes the previous Phase 3 conversion results. The six approved Phase 2 domains and their examples were not modified in this re-execution. The current Phase 2 report records Action and Rule as still awaiting explicit approval, so Phase 2 as a whole is not approved. The approved v1 schemas and Phase 2 examples referenced here concern the approved domain baseline only.

## Method and scope

Codex directly interpreted each of the same 34 selected source records against its complete source object, relevant nested entries and structured fields, the Phase 1 analysis, both Phase 2 research/validation reports, all approved schemas, and the 34 approved Phase 2 examples. Canonical JSON was authored from those interpretations. The prior deterministic converter was retired and was not used to determine mechanics. The replacement source-register synchronizer only extracts complete source entries according to the manually reviewed accounting manifest; it does not interpret or convert mechanics.

Outputs are in `datasets/phase-3/step-2/core-rules/v1/`. The selection remains limited to 34 records: **16 fully represented, 17 partially represented, and 1 unsupported**. There are **33 canonical records**; Cackle Fever has no shell because the approved Disease contract cannot represent a reliable, meaningful subset without guessing its disease state and references.

| Domain | Source-qualified identity | Original source object | Outcome | Canonical record |
| --- | --- | --- | --- | --- |
| Action | `action:phb:attack` | `actions.json $.action[1]` | Partial | `action/phb-attack.json` |
| Action | `action:xphb:dodge` | `actions.json $.action[11]` | Fully represented | `action/xphb-dodge.json` |
| Action | `action:phb:grapple` | `actions.json $.action[18]` | Partial | `action/phb-grapple.json` |
| Action | `action:phb:shove` | `actions.json $.action[38]` | Fully represented | `action/phb-shove.json` |
| Action | `action:phb:cast-a-spell` | `actions.json $.action[3]` | Partial | `action/phb-cast-a-spell.json` |
| Action | `action:dmg:mark` | `actions.json $.action[29]` | Fully represented | `action/dmg-mark.json` |
| Rule | `rule:xphb:d20-test` | `variantrules.json $.variantrule[44]` | Fully represented | `rule/xphb-d20-test.json` |
| Rule | `rule:xphb:critical-hit` | `variantrules.json $.variantrule[38]` | Fully represented | `rule/xphb-critical-hit.json` |
| Rule | `rule:xphb:unarmed-strike` | `variantrules.json $.variantrule[225]` | Partial | `rule/xphb-unarmed-strike.json` |
| Rule | `rule:xphb:difficult-terrain` | `variantrules.json $.variantrule[53]` | Partial | `rule/xphb-difficult-terrain.json` |
| Rule | `rule:dmg:downtime-activity-crafting-a-magic-item` | `variantrules.json $.variantrule[62]` | Partial | `rule/dmg-downtime-activity-crafting-a-magic-item.json` |
| Skill | `skill:phb:acrobatics` | `skills.json $.skill[0]` | Fully represented | `skill/phb-acrobatics.json` |
| Skill | `skill:phb:animal-handling` | `skills.json $.skill[2]` | Fully represented | `skill/phb-animal-handling.json` |
| Skill | `skill:xphb:survival` | `skills.json $.skill[35]` | Fully represented | `skill/xphb-survival.json` |
| Condition | `condition:phb:blinded` | `conditionsdiseases.json $.condition[0]` | Fully represented | `condition/phb-blinded.json` |
| Condition | `condition:xphb:blinded` | `conditionsdiseases.json $.condition[1]` | Fully represented | `condition/xphb-blinded.json` |
| Condition | `condition:phb:exhaustion` | `conditionsdiseases.json $.condition[6]` | Fully represented | `condition/phb-exhaustion.json` |
| Condition | `condition:xphb:exhaustion` | `conditionsdiseases.json $.condition[7]` | Fully represented | `condition/xphb-exhaustion.json` |
| Disease | `disease:phb:blinding-sickness` | `conditionsdiseases.json $.disease[1]` | Fully represented | `disease/phb-blinding-sickness.json` |
| Disease | `disease:xdmg:sewer-plague` | `conditionsdiseases.json $.disease[18]` | Partial | `disease/xdmg-sewer-plague.json` |
| Disease | `disease:dmg:cackle-fever` | `conditionsdiseases.json $.disease[4]` | Unsupported | No shell |
| Status | `status:xphb:bloodied` | `conditionsdiseases.json $.status[0]` | Fully represented | `status/xphb-bloodied.json` |
| Status | `status:phb:concentration` | `conditionsdiseases.json $.status[1]` | Partial | `status/phb-concentration.json` |
| Status | `status:xphb:concentration` | `conditionsdiseases.json $.status[2]` | Partial | `status/xphb-concentration.json` |
| Status | `status:phb:surprised` | `conditionsdiseases.json $.status[3]` | Partial | `status/phb-surprised.json` |
| Status | `status:xphb:surprised` | `conditionsdiseases.json $.status[4]` | Fully represented | `status/xphb-surprised.json` |
| Sense | `sense:phb:darkvision` | `senses.json $.sense[2]` | Partial | `sense/phb-darkvision.json` |
| Sense | `sense:mm:tremorsense` | `senses.json $.sense[4]` | Partial | `sense/mm-tremorsense.json` |
| Sense | `sense:xphb:tremorsense` | `senses.json $.sense[5]` | Partial | `sense/xphb-tremorsense.json` |
| Sense | `sense:phb:truesight` | `senses.json $.sense[6]` | Partial | `sense/phb-truesight.json` |
| Language | `language:phb:common` | `languages.json $.language[30]` | Fully represented | `language/phb-common.json` |
| Language | `language:xphb:common` | `languages.json $.language[32]` | Partial | `language/xphb-common.json` |
| Language | `language:mm:gith` | `languages.json $.language[71]` | Partial | `language/mm-gith.json` |
| Language | `language:phb:thieves-cant` | `languages.json $.language[173]` | Partial | `language/phb-thieves-cant.json` |

All five Status records are included: Bloodied, PHB/XPHB Concentration, and PHB/XPHB Surprised. Source editions remain separate.

## Corrected interpretations

### Action

- **Attack:** the typed operation records melee, ranged, and unarmed forms and the source-linked action options. The schema has no explicit attack-count or feature-granted repeat-count field. The source's one attack and Extra Attack exception are therefore documented as a real contract limitation; no count is inferred from the number of JSON operations.
- **Dodge (XPHB):** records the attack disadvantage, Dexterity-save advantage, duration, and early-ending state. The visibility predicate correctly states that the affected creature sees the attacker. Source-linked references to Advantage, Disadvantage, Saving Throw, and Speed are retained.
- **Grapple:** records the acting creature's Strength (Athletics) check, target-selected opposing Strength (Athletics) or Dexterity (Acrobatics), size/reach limits, free hand, automatic success against Incapacitated, Grappled application, voluntary release, and Attack replacement. Dragging/carrying while moving and the two-size speed exception have no suitable Action movement structure.
- **Shove:** records the acting creature's Strength (Athletics) check, opposing skill choices, size/reach, automatic success against Incapacitated, Attack replacement, and exactly-one post-success choice between a five-foot push and Prone.
- **Cast a Spell:** uses the approved `alternatives` timing object for action, spell-defined reaction, and variable in-game time. It does not substitute a fixed duration. The chapter-level casting procedure remains an external spell-domain dependency.
- **Mark:** requires reaction availability and does not consume the reaction. Its melee-attack trigger, marked target, opportunity-attack Advantage, next-turn expiry, and one-use-per-turn limit are represented. The unqualified Condition and spell example links do not identify PHB versus XPHB canonical targets, so they remain externally accounted rather than guessed.

### Rule

- **D20 Test:** represents its three included test kinds and the DM's decision whether a test is warranted. Its explicit links to Ability Check, Attack Roll, and Saving Throw are typed Rule references.
- **Critical Hit:** records a natural 20, automatic hit, and doubled attack damage dice with modifiers applied once. The attack forms include unarmed attacks as well as melee, ranged, spell, and weapon attacks.
- **Unarmed Strike:** records Damage, Grapple, and Shove under one Rule identity. The source says to choose one option whenever the strike is used; the `OR` composition expresses that record-specific choice before the chosen branch resolves, while `ON_HIT`/`ON_FAILED_SAVE` express the outcomes. One saving-throw component is shared by Grapple and Shove. A typed relationship records that the Grapple save DC also governs escape attempts. The five-foot range is represented for the attack/damage component, but the option subtype cannot attach that range to the Grapple/Shove save options; that limitation remains documented.
- **Difficult Terrain:** records one extra foot of movement per foot and non-cumulative application. The list of qualifying spaces and the open-ended “similar” adjudication are outside the approved component.
- **Magic-item crafting:** the approved downtime subtype supports an activity and item-created outcome, so it is represented as a partial Rule. The source-specific formula and spell prerequisites, rarity tables, variable cost/work, slot/component consumption, group contributions, lifestyle exception, and campaign discretion cannot be expressed without losing procedure behavior.

### Other domains

- **Skill:** all three records preserve the structured ability association. Descriptions are task examples or DM guidance, not additional check procedures in the approved Skill contract. PHB Acrobatics uses source page 176.
- **Condition:** PHB and XPHB Blinded and Exhaustion remain distinct. Blinded's Advantage/Disadvantage directions are correct. PHB Exhaustion retains cumulative thresholds, recovery, and the hit-point-maximum effect at level 4. XPHB Exhaustion retains the per-level D20 penalty, five-foot-per-level Speed reduction, level cap, and Long Rest recovery.
- **Disease:** Blinding Sickness retains Wisdom check/save Disadvantage, Blinded, and its explicit Contagion reference. Sewer Plague retains contact/wound transmission, the DC 11 Constitution infection save, magical-contagion classification, and the XPHB Exhaustion condition identity. The source-supported one-level increase after 1d4 days, Humanoid-only susceptibility and transmission-source restrictions, Hit Point Dice and Long Rest restrictions while Exhausted, daily-at-dawn save, conditional Exhaustion increase/decrease, and recovery only at Exhaustion 0 are not representable by the approved Disease contract. Cackle Fever has no shell: its stress trigger, edition-ambiguous condition links, timed symptoms, damage, repeated saves, secondary infection/immunity, random DC decrease, and failure-count outcome are interdependent and cannot be represented reliably by the current Disease fields.
- **Status:** all five records are represented. PHB/XPHB Concentration preserve different damage DC rounding/limits and automatic endings. PHB's environmental save is DM-adjudicated, but the schema has no optional/adjudication marker. Dependent spell/effect termination and effect-specific duration remain external. PHB Surprised records the turn restrictions; its Stealth-versus-passive-Perception assignment procedure is unsupported. Different creatures can carry different statuses, so the group example is treated as duplicate of per-creature status application.
- **Sense:** PHB Darkvision records darkness-as-dim-light and grayscale color, but dim-light-as-bright-light remains a genuine schema limitation: SIGHT has `darknessAs` and cannot encode a separate dim-light transformation without changing field meaning. MM/XPHB Tremorsense retain vibration-based nonvisual detection and contact limits; XPHB includes creatures and movement. The schemas have no pinpoint/location precision. Truesight retains its qualitative capabilities but cannot distinguish invisible objects from objects generally or express automatic illusion detection and saving-throw success.
- **Language:** PHB Common retains category, script, and the typed humanoid/human speaker filter. XPHB Common's origin “Sigil” has no established place identity in the approved reference targets. Gith retains its script and exact source-context creature speakers; its dialect identities and script layout/writing direction are not canonical Language structures. Thieves' Cant retains its secret category; speaker resolution and its comprehension/time-cost mechanics are unsupported by the approved Language schema.

## Source-entry accounting

The 34 selected records contain **74 source `entries` roots**, including nested content in 12 roots. The revised manifest classifies every root and relevant mixed portion:

| Disposition | Classification parts |
| --- | ---: |
| `INTERPRETED` | 51 |
| `DUPLICATE` | 11 |
| `OMITTED` | 50 |

These counts are classification-part counts, not unique root counts; 32 roots have multiple dispositions because supported facts can coexist with duplicate or omitted content. The 50 omitted classification parts carry an explicit cause (`SCHEMA_UNSUPPORTED`, `REFERENCE_UNRESOLVED`, `SOURCE_AMBIGUOUS`, `EXTERNAL_DEPENDENCY`, `NON_MECHANICAL`, or `OUT_OF_APPROVED_SCOPE`). Fourteen structured-field/reference issues are separately identity- and locator-qualified in the accounting manifest. There are 46 distinct source-entry roots with omitted portions; complete source entries with omitted or duplicate portions are regenerated into the model-specific `/doc/<model>/omitted.md` and `duplicate.md` registers. Nested paths and full original entry trees are retained. Those registers contain restricted source excerpts and are not public-distribution material.

`ruleType` values `C` and `O` remain unresolved in the selected Rule records; no full-word mapping was inferred. `V` and `VO` remain unresolved as well. Unqualified same-name reference tags are retained in accounting when the source target cannot be established exactly.

## Corrections proposed for Phase 2 examples

The approved fixtures remain unchanged. Source comparison identifies these review corrections:

1. The PHB Dodge fixture places the attacker in the `CAN_SEE` actor role. PHB and XPHB wording makes the affected creature the one who sees the attacker.
2. The PHB Shove fixture omits automatic success when the target is Incapacitated.
3. The Unarmed Strike fixture does not relate the Grapple save DC to escape attempts. The representative output demonstrates the existing typed Rule reference relationship.
4. The Critical Hit fixture's attack forms omit unarmed attacks even though the source applies to an attack roll generally and Unarmed Strike is an attack roll.
5. The PHB Acrobatics fixture lists page 175; the source record supplies page 176.
6. The PHB Blinded fixture omits the HftT page 48 source citation present in the original record.

These are proposals for review, not edits to the approved Phase 2 examples.

## Validation and interpretation limits

`npm run validate:core-rules` passed with zero failures:

- 9 Draft 2020-12 schemas compiled.
- 34 Phase 2 examples and 33 Phase 3 canonical records validated.
- 111 permanent validation checks passed, including source metadata and typed reference resolution, Action operation/choice integrity, Rule composition/applicability, complete entry/register verification, and positive/negative integrity cases.
- Selected semantic assertions verify the corrected Mark reaction requirement, Shove actor skill, variable timing alternatives, XPHB Sense movement/contact, source references, and other edition-specific mechanics.

The validator checks structure, identities, metadata, references, accounting, and explicit regression assertions. Manual source comparison supports only the selected interpretations described here; successful validation does not establish complete semantic correctness or coverage beyond the sample. Cackle Fever remains unsupported; 17 other records are partial for the documented reasons. The Crafting Magic Item procedure, attack-count/Extra Attack, grappled movement, Disease state progression, some Status consequences, Sense precision, and Language identity/communication patterns require reviewed contract decisions before broad normalization.

The obsolete semantic conversion command has been removed. `npm run sync:core-rules:phase3-registers` is limited to deterministic complete-entry register generation from the reviewed accounting manifest. Phase 3 remains awaiting review; Phase 4 validation/feedback, Phase 5 expansion, and Phase 6 integration have not started.

## Final semantic corrections — Darkvision and Sewer Plague

This targeted continuation leaves the approved schemas and all Phase 2 examples unchanged and modifies no unrelated canonical record. The selected-record totals remain 34 selected, 33 canonical records, 16 fully represented, 17 partial, and 1 unsupported.

### Darkvision (PHB)

At the Phase 3 checkpoint, the original source at `src/main/resources/data/senses.json $.sense[2]` was recorded as only partially represented: the approved Sense schema then had no separate dim-light transformation, and the omitted portion was recorded with `SCHEMA_UNSUPPORTED`. Phase 4 later adds the independent transformation and regenerates current omission registers; see the Phase 4 implementation report for the superseding canonical record and accounting.

Smallest reusable extension proposal for future approval: add an optional `dimLightAs` field to `SIGHT`, with only source-established outcomes (initially `bright-light`). This execution does not change the schema or canonical record.

### Sewer Plague (XDMG)

The original source at `src/main/resources/data/conditionsdiseases.json $.disease[18]` is fully accounted for across three entries and their nested list/procedure portions. The current canonical record accurately retains the magical-contagion classification, contact/wound routes, DC 11 Constitution infection save, and explicit XPHB Exhaustion condition identity. It does not assert an immediate symptom or assign a fixed incubation interval.

The complete entry accounting now separately records the unsupported Humanoid susceptibility and route-source restrictions; 1d4-day symptom onset; one-level Exhaustion increase; Hit Point Dice recovery reduction while Exhausted; Long Rest limitations while Exhausted; and daily-at-dawn saving throw with conditional Exhaustion increment/decrement and disease termination only at level zero. These mechanics are `SCHEMA_UNSUPPORTED`. The disease's stated origin and sewer/refuse ecology are separately excluded as `NON_MECHANICAL`; named creatures remain contextual carrier examples. The mechanical portions are source facts, not flavor. The contract lacks random-die interval representation, typed target applicability and route qualifiers, quantified condition-level symptom application, condition-gated recovery restrictions, and stateful progression branches linked to a condition-level threshold. The existing Disease progression outcomes cannot encode these changes; using `disease-ends` for an ordinary successful save would be wrong.

Reusable Disease extension proposal for future review: a typed random interval; typed target/source applicability; quantified condition-level symptoms with onset; condition-gated Hit Point Dice and Long Rest restrictions; and a Disease progression procedure that can specify cadence, a saving throw, separate success/failure condition-level changes, and a condition-level termination threshold. These should remain Disease-specific structures, not a general-purpose effects engine. No schema changes are made here.

### Accepted review decisions

- **Attack:** Extra Attack relationships may be modeled from the feature side in a future domain.
- **Cast a Spell:** the current typed timing alternatives are accepted.
- **Unarmed Strike:** the current composition and choice-before-resolution interpretation is accepted.
- **Magic-item crafting:** the current partial representation is accepted.

### Updated verification

The permanent validator now asserts the represented Darkvision restrictions and verifies the record-specific omission accounting for both targets, including Sewer Plague's source-supported Exhaustion link and the separately omitted conditional mechanics. The source-entry register synchronizer regenerated the complete-entry registers from the updated manifest. Validation passed: 9 Draft 2020-12 schemas compiled, 34 Phase 2 examples and 33 Phase 3 canonical records validated, and all 111 permanent validation checks passed. The accounting has 51 interpreted, 11 duplicate, and 50 omitted classification parts; register synchronization covered 46 omitted and 11 duplicate entry roots. `git diff --check` passed with line-ending normalization warnings only.

## Phase 4 contract refinement addendum

The foregoing validation counts and limitations are the historical Phase 3 baseline. The approved Phase 4 changes, including the Disease, Action, Rule, Status, and Sense refinements and six fixture corrections, are recorded in [the Phase 4 implementation report](../phase-4/CORE_RULES_FOUNDATION_PHASE_4_IMPLEMENTATION.md). Current accounting remains the same 34-record representative selection and is maintained in `CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json`; it now contains 34 canonical records, 25 fully represented, 9 partial, 0 unsupported, and 60 interpreted, 11 duplicate, and 34 omitted classification parts. The revised permanent workflow reports 121 passing checks. These revised results do not replace the historical Phase 3 validation snapshot above.

Phase 3 remains awaiting human review. Phase 4 has not started.

## Approval record reconciliation — 2026-10-29

The “Phase 4 has not started” statement above is a historical checkpoint and is superseded by the Phase 4 reports and later Phase 5 and Phase 6 work. Later reports describe the Phase 3 representatives as an approved baseline and preserve accepted interpretations, but the reviewed records do not identify an explicit approval of the Phase 3 gate itself. Under the roadmap's separate approval requirement, Phase 3 still requires explicit approval; execution and later reliance on its outputs are not treated here as that approval. Phase 2's whole-phase approval also remains outstanding as Action and Rule are still pending in the latest Phase 2 checkpoint.

## Explicit approval — 2026-10-29

The user explicitly approved the Phase 3 representative conversion baseline, including the nine documented partial representations. This accepts the recorded coverage, omissions, duplicates, dependencies, and unresolved limitations for the 34-record representative selection. This approval supersedes the preceding pending-approval statement; it does not claim full-catalog mechanical completeness.
