# Core Rules Foundation Phase 5 — Rule Gaps Batch 3

**Status:** Implemented for human review. Step 2 Phase 5 remains open and unapproved.

## Scope and checkpoint

This batch implements only R-GAP-09 (Initiative), R-GAP-17 (carrying capacity and encumbrance), R-GAP-18 (rest and recovery), and R-GAP-20 (Damage Resistance and Vulnerability). It does not begin Batch 4, Phase 6, or another domain.

Before implementation, the Batch 2 checkpoint was verified: 31 Rule identities were accounted including five representatives; 22 FULL, 5 PARTIAL, and 4 UNSUPPORTED; 26 total canonical Rule outputs; 346 Phase 5 canonical outputs; and 569 permanent validation checks. The XPHB Initiative record was among the unsupported records. It now has a partial canonical interpretation because its score rule is representable; ordinary initiative rolling and turn scheduling remain an external combat dependency.

## Implemented contracts and source decisions

| Gap | Source identity and source locator | Canonical result | Coverage |
|---|---|---|---|
| R-GAP-09 | `rule:xphb:initiative`, `$.variantrule[136].entries[0..1]`; `rule:dmg:initiative-variants`, `$.variantrule[137].entries[1..3]` | Added `INITIATIVE_PROCEDURE`. XPHB score is 10 + Dexterity modifier, with +5/−5 for Advantage/Disadvantage. DMG variants retain passive Dexterity score and the distinct side procedure: unmodified side d20, high roll first, reroll ties, members choose their order, and multiple sides order by their rolls. | XPHB PARTIAL; DMG PARTIAL |
| R-GAP-17 | `rule:xphb:carrying-capacity`, `$.variantrule[24].entries[0..2]`; `rule:phb:encumbrance`, `$.variantrule[99].entries[0..2]` | Added `CARRYING_CAPACITY_RULE`. XPHB’s full size table and Strength multipliers are represented, along with the 5-foot Speed cap while dragging/lifting/pushing beyond carry capacity. PHB encumbrance records strict `>` thresholds at 5× and 10× Strength, Speed reductions, and the three affected abilities for checks, attacks, and saves. | Carrying Capacity FULL; Encumbrance PARTIAL |
| R-GAP-18 | Recuperating `$.variantrule[70].entries[0..2]`; Relaxation `$.variantrule[71].entries[1..2]`; Healing `$.variantrule[120].entries[1..3]`; Long Rest `$.variantrule[147].entries[0..2]`; Planar Effects `$.variantrule[168].entries[0..21]`; Rest Variants `$.variantrule[183].entries[1..2]`; Short Rest `$.variantrule[195].entries[0..5]`; Sleep `$.variantrule[204].entries[0..3]` | Extended `REST_PROCEDURE`, `DOWNTIME_PROCEDURE`, and a bounded recovery-outcome union. Added rest durations, start requirements, permitted activity, interruption triggers/outcomes, resumption, recovery benefits, downtime recovery, sleep-deprivation save cadence, and rest variants. Fractional Hit Die recovery values state the quarter/level-divided-by-four fraction, minimum one, and round down. | Recuperating FULL; Relaxation FULL; Healing PARTIAL; Long Rest FULL; Planar Effects UNSUPPORTED; Rest Variants PARTIAL; Short Rest FULL; Sleep PARTIAL |
| R-GAP-20 | `rule:xphb:resistance`, `$.variantrule[182].entries[0]`; `rule:xphb:vulnerability`, `$.variantrule[227].entries[0]` | Extended the existing `DAMAGE_HEALTH_STATE`/`DAMAGE` component family with `RESISTANCE` and `VULNERABILITY` subtypes and bounded resolution modifiers. Resistance halves and rounds down; Vulnerability doubles; each applies once per damage instance and the same operation does not stack. | Both FULL |

### Deliberate source boundaries

- **Initiative:** the XPHB source delegates ordinary rolls and turn ordering to the combat rules, so this is recorded as an external dependency. DMG Speed Factor is omitted as `SCHEMA_UNSUPPORTED`: it combines per-round action declarations, action/size modifiers, secret descending results, and an action constraint. It was not forced into the initiative-score or side-initiative structure.
- **Carrying/encumbrance:** the XPHB table is fully represented without inventory aggregation. PHB Encumbrance remains partial because ignoring the Strength column of the Armor table depends on Equipment. No current weight or character state is calculated.
- **Rest:** the XPHB Long Rest preserves the exact eight-hour minimum, six-hour sleep minimum, two-hour light-activity maximum, one-HP start requirement, 16-hour interval between starts, interruptions, one-hour threshold, short-rest interruption benefit, and one-hour-per-interruption resumption. XPHB Short Rest preserves one hour, start eligibility, permitted activity, interruption, and no-benefit outcome. Completion recovers the listed HP/Hit Dice/score changes and reduces XPHB Exhaustion by one; feature recharge remains feature-defined.
- PHB Recuperating models the three-day procedure, DC 15 Constitution save, and success choice. XGE Relaxation models the one-week duration, Modest lifestyle, ongoing disease/poison recovery advantage, end-of-week choice, and ongoing-magic exclusion. Their activity-specific effects are typed, not collapsed into ordinary rests.
- DMG Healing preserves rest-based Hit Dice recovery and limited-use reset, plus Slow Natural Healing. Healer’s Kit Dependency remains an Equipment/inventory dependency; Healing Surge’s Action timing, expenditure cap, roll sequencing, and Bonus Action option are outside this batch. DMG Rest Variants retain Epic Heroism and Gritty Realism rest durations; optional spell-slot changes remain a Spell/resource dependency.
- XGE Sleep represents armor-dependent Hit Dice recovery and no Exhaustion reduction, and the 24-hour sleep-deprivation save with +5 DC per consecutive interval and reset on Long Rest. Natural sleep state and waking by damage, interaction, sound, or speech remain deferred to the existing ACT-6 boundary. Medium/heavy Armor classification remains an Equipment dependency.
- DMG Planar Effects has 22 source-entry roots. Its planar-triggered Long Rest procedures are intertwined with alignment, Conditions, spells, transformation, and other outcomes owned by other gaps/domains. A standalone rest trigger would be misleading, so the Rule remains reference-only and UNSUPPORTED; the complete entries are preserved in the omission register.
- XPHB Resistance and Vulnerability are represented exactly as their records state. Those entries define one application per damage instance, but do not define a cross-operation ordering/cancellation rule when both apply. No such order was inferred.

The DMG Planar Effects source metadata points to an XDMG Hazard reprint. The approved shared reference enum has no `hazard` target type, and no canonical Hazard identity is authorized here. The Rule identity omits that reprint from `sourceMetadata`; its exact source value and unresolved reason are retained as a structured-field issue in the manifest. No Rule identity was invented.

## Schema and canonical changes

Only `src/main/resources/schemas/rule/schema.json` changed. It now adds `INITIATIVE_PROCEDURE` and `CARRYING_CAPACITY_RULE`; extends the existing `REST_PROCEDURE` and `DOWNTIME_PROCEDURE` with bounded duration, activity, interruption, recovery, and limitation fields; adds finite recovery outcome variants; and extends the existing damage component with Resistance/Vulnerability modifiers. No common/other domain schema or Java file changed.

Fourteen Phase 5 Rule files are now present for the affected identities: thirteen new source records and the previously unsupported XPHB Initiative record. The thirteen newly created outputs are:

- `rule:dmg:initiative-variants`
- `rule:xphb:carrying-capacity`
- `rule:phb:encumbrance`
- `rule:phb:downtime-activity-recuperating`
- `rule:xge:downtime-activity-relaxation`
- `rule:dmg:healing`
- `rule:xphb:long-rest`
- `rule:dmg:planar-effects` (reference-only)
- `rule:dmg:rest-variants`
- `rule:xphb:short-rest`
- `rule:xge:sleep`
- `rule:xphb:resistance`
- `rule:xphb:vulnerability`

The XPHB Initiative output reuses its existing source identity and metadata. Six positive examples were added for Initiative, carrying capacity, Long Rest, Resistance, and Vulnerability.

## Coverage and accounting

The Phase 5 Rule batch now accounts for 39 non-representative Rule records, excluding five representatives: **25 FULL, 10 PARTIAL, 4 UNSUPPORTED**, with **35 Phase 5 Rule outputs** and 112 source-entry roots. Its source-entry dispositions are **52 INTERPRETED, 3 DUPLICATE, and 64 OMITTED**. The previously omitted XPHB Initiative score clause is now INTERPRETED; its ordinary turn-order clause remains an external dependency.

Across the 231-record Rule catalog, 44 identities are accounted including the five representatives: **29 FULL, 11 PARTIAL, and 4 UNSUPPORTED**; 187 remain pending conversion accounting. There are 40 Rule canonical outputs including the five Phase 3/4 representatives. This is conversion coverage, separate from the completed 231-record semantic discovery inventory.

The Phase 5 manifest now records **360 canonical outputs**, **204 FULL, 156 PARTIAL, and 7 UNSUPPORTED** source-record classifications, across **388 source-entry roots** with **256 INTERPRETED, 27 DUPLICATE, and 254 OMITTED** classification parts. Structured-field/reference issues total **155**, including the unresolved DMG Planar Effects → XDMG Hazard reprint target.

## Validation

`npm run validate:core-rules` passes: **9** Draft 2020-12 schemas compiled, **79** examples validated, **34** Phase 3/4 records validated, **360** Phase 5 records validated, and **596** permanent checks passed. The output reports 38 Rule components checked and 27 Action records checked. Permanent assertions cover source-specific Initiative, exact size and encumbrance values, rest durations/interruption/recovery boundaries, and Resistance/Vulnerability calculations. Negative checks reject altered Initiative adjustments, carrying multipliers, encumbrance penalties, Long Rest intervals, and Resistance rounding.

`git diff --check` is run after report and documentation updates. These checks establish structural validity and selected invariants; they do not prove complete semantic correctness. Step 2 Phase 5 remains open and unapproved. Batch 4 has not started.
