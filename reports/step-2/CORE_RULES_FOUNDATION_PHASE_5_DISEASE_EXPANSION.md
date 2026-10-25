# Core Rules Foundation Phase 5 Disease Expansion

## Status and scope

**Disease batch complete; Phase 5 remains in progress and is not approved. Phase 6 has not started.**

This report covers all 29 records in `src/main/resources/data/conditionsdiseases.json`, `$.disease[0..28]`. The three reviewed Phase 3/4 representatives remain unchanged. The 26 remaining records were examined; 24 received partial canonical records, while Grackle-Lung and Super-Tetanus remain unsupported. No Disease schema, approved Phase 2 fixture, or PHB Petrified canonical Condition was changed.

The 29 source records by source code are: PHB 6, DMG 3, XDMG 3, ToA 3, FRAiF 4, GoS 2, and one each from CM, EGW, IDRotF, OoW, OotA, TftYP, VRGR, and WDMM.

## Inventory and coverage

Source-qualified names are retained as separate records, including reprints and edition variants.

| Source locator | Disease | Disposition | Canonical output / primary limitation |
|---|---|---|---|
| `$.disease[0]` IDRotF | Arcane Blight | Partial | `idrotf-arcane-blight.json`; exposure duration and transformation/failure counter are not representable |
| `$.disease[1]` PHB | Blinding Sickness | Phase 3 representative, partial | Existing Phase 3 record; reviewed classification retained |
| `$.disease[2]` ToA | Blue Mist Fever | Partial | `toa-blue-mist-fever.json`; hallucination and area hazard behavior omitted |
| `$.disease[3]` GoS | Bluerot | Partial | `gos-bluerot.json`; ability-score changes, damage, vulnerability, breathing, and recovery track omitted |
| `$.disease[4]` DMG | Cackle Fever | Phase 3 representative, partial | Existing approved record and unresolved failure-counter ambiguity retained |
| `$.disease[5]` XDMG | Cackle Fever | Partial | `xdmg-cackle-fever.json`; three-success cure and one-year immunity threshold are not expressible |
| `$.disease[6]` PHB | Filth Fever | Partial | `phb-filth-fever.json`; Strength-based attack disadvantage omitted |
| `$.disease[7]` PHB | Flesh Rot | Partial | `phb-flesh-rot.json`; damage vulnerability omitted |
| `$.disease[8]` EGW | Frigid Woe | Partial | `egw-frigid-woe.json`; Speed loss progression and fatal transformation omitted |
| `$.disease[9]` OoW | Ghoul Gut | Partial | `oow-ghoul-gut.json`; timed symptoms, damage, recovery restrictions, and caregiver procedure omitted |
| `$.disease[10]` OotA | Grackle-Lung | Unsupported | No canonical shell; its recurring Constitution save occurs on Long Rest completion and the approved Disease schema cannot tie that trigger to the Exhaustion outcome and dependent Dash/verbal spell checks |
| `$.disease[11]` FRAiF | Lichen Plague | Partial | `fraif-lichen-plague.json`; Long Rest progression, three-success cure, and death transformation omitted |
| `$.disease[12]` PHB | Mindfire | Partial | `phb-mindfire.json`; Confusion combat behavior remains an external Spell dependency |
| `$.disease[13]` FRAiF | Mudpox | Partial | `fraif-mudpox.json`; vomiting, consecutive successes, and skin-contact spread omitted |
| `$.disease[14]` GoS | Redface | Partial | `gos-redface.json`; source says it shares DMG Sight Rot effects, but schema references do not inherit another Disease's mechanics |
| `$.disease[15]` CM | Saprophytic Plague | Partial | `cm-saprophytic-plague.json`; proximity/food transmission, automatic immunity, timed behavior, and transformation omitted |
| `$.disease[16]` PHB | Seizure | Partial | `phb-seizure.json`; Dexterity-based attack disadvantage omitted |
| `$.disease[17]` DMG | Sewer Plague | Partial | `dmg-sewer-plague.json`; Long Rest progression and condition-level recovery outcome not representable |
| `$.disease[18]` XDMG | Sewer Plague | Phase 3 representative, partial | Existing approved record unchanged |
| `$.disease[19]` FRAiF | Shaking Plague | Partial | `fraif-shaking-plague.json`; spellcasting disruption and contact spread omitted |
| `$.disease[20]` ToA | Shivering Sickness | Partial | `toa-shivering-sickness.json`; exposure prerequisites, natural-armor benefit, and delayed recovery applicability omitted |
| `$.disease[21]` DMG | Sight Rot | Partial | `dmg-sight-rot.json`; cumulative attack/check penalties and ointment procedure omitted |
| `$.disease[22]` XDMG | Sight Rot | Partial | `xdmg-sight-rot.json`; temporary suppression, cumulative 72-hour cure, and skin-contact spread omitted |
| `$.disease[23]` PHB | Slimy Doom | Partial | `phb-slimy-doom.json`; damage-triggered Stunned condition omitted |
| `$.disease[24]` WDMM | Spider Eggs | Partial | `wdmm-spider-eggs.json`; capture trigger, escape procedure, hatching, and death omitted |
| `$.disease[25]` TftYP | Super-Tetanus | Unsupported | No canonical shell; source omits saving-throw ability, DC, and outcomes for the repeat save |
| `$.disease[26]` VRGR | The Gnawing Plague | Partial | `vrgr-the-gnawing-plague.json`; variable spread DC, creature immunity, and Long Rest progression omitted |
| `$.disease[27]` FRAiF | The Rusting | Partial | `fraif-the-rusting.json`; curse state, Speed change, transformation, and special immunities omitted |
| `$.disease[28]` ToA | Throat Leeches | Partial | `toa-throat-leeches.json`; Long Rest level progression and termination threshold omitted |

The expanded batch has **0 fully represented, 24 partial, and 2 unsupported** records. Including the three representative records, the 29-record Disease inventory has **27 canonical outputs**, all partial under source-entry accounting, and two unsupported records. These labels do not measure how many individual mechanics were extracted.

## Approved patterns applied

The existing Disease v1 structures represented supported portions of:

- Creature-type applicability and excluded targets.
- Infection saving throws and supported transmission modes.
- Fixed or dice-based incubation/onset.
- Condition applications and level changes.
- Ability-check and saving-throw advantage/disadvantage.
- Sight impairment and condition-gated recovery restrictions.
- Daily-at-dawn progression where that schedule is source-supported.
- Recovery saves, spell cures, and source-relative secondary-exposure immunity.
- Typed references to spells, Conditions, races, Rules, and other Diseases where the source establishes the target.

No universal effect structure or source prose was added. A save schedule or outcome was left out when the approved schema could not express it without changing its meaning. In particular, `DAILY_AT/DAWN` was not used as a substitute for “at the end of a Long Rest,” and a success-count threshold was not modeled as an ordinary DC threshold.

| Pattern group | Source records | Use of current contract |
|---|---|---|
| Exposure save and onset interval | Blue Mist Fever, Bluerot, Frigid Woe, Mudpox, Shivering Sickness, Sight Rot variants, Throat Leeches, and others | Infection and incubation fields retain explicit save/DC and onset values; exposure prerequisites not covered by enums remain omitted |
| Condition-level disease | Cackle Fever variants, Sewer Plague variants, Lichen Plague, Shaking Plague, The Gnawing Plague, The Rusting, Throat Leeches | Typed Exhaustion changes and condition-gated recovery restrictions are retained when the source supports them; progression branches are used only when their trigger matches |
| Repeated checks with DC changes | Arcane Blight, DMG Cackle Fever | Recovery DC tracks and reductions retained; failure counting on DMG Cackle Fever remains ambiguous and unchanged |
| Nearby/source-relative contagion | DMG Cackle Fever, XDMG Cackle Fever | Existing nearby exposure pattern retains turn-start timing, distance, save, and 24-hour source-specific immunity |
| Passive Condition or roll consequence | PHB Filth Fever, Flesh Rot, Mindfire, Seizure, Slimy Doom | Ability check/save modifiers and referenced Conditions are retained; attack-roll and damage consequences outside the contract are omitted |
| Linked or exceptional procedures | Redface, Spider Eggs, Super-Tetanus | Exact references or supported Condition applications retained where meaningful; unsupported procedures remain partial or unsupported rather than being generalized |

The source-referenced DMG Sewer Plague and XDMG Sewer Plague remain distinct source identities. PHB Conditions are referenced for the legacy records and XPHB Conditions for XDMG disease mechanics where the source uses the newer Exhaustion/Condition rules.

## PHB Petrified disease-suspension dependency

The source is `conditionsdiseases.json`, `$.condition[18].entries[0].items[5]` (PHB Petrified). It states that the creature is immune to poison and disease, while poison or disease already in its system is “suspended, not neutralized.” The current PHB Petrified canonical record models the separate poison-and-disease immunity but has no declaration suspending an active Disease.

The approved Disease schema is not sufficient to express the cross-domain lifecycle. Immunity prevents acquisition; suspension concerns an existing instance; neither is curing or terminating the Disease. “Not neutralized” supports treating the disease as retained rather than ended, but the source does not define timer or scheduler behavior: whether incubation or duration clocks pause, whether scheduled saves and recovery attempts are delayed, whether symptoms and accumulated Condition levels remain, or exactly when those procedures resume.

The smallest plausible extension is a typed effect on the Condition record, not a Disease-specific field on every disease:

```json
{
  "kind": "SUSPEND_ACTIVE_DISEASES",
  "applies": "while-originating-condition-is-active",
  "resume": "when-originating-condition-ends",
  "preserveDiseaseState": true
}
```

This is a proposal only: one optional typed effect on `condition.schema.json`, authored on PHB Petrified, with no Disease schema change. Validation should establish that the effect preserves an active Disease instance, remains separate from acquisition immunity, and does not imply termination; tests should not assert timer pausing, scheduled-save behavior, symptom-level persistence, or exact resumption timing until those semantics are approved. The source supports suspension while Petrified and retention rather than neutralization. Resumption when the Condition ends is a reasonable reading of “suspended,” but timer advancement and scheduled-procedure semantics remain unresolved and require human review before any contract change. No schema or PHB Petrified data was modified.

## Contract limitations and unresolved dependencies

| Recurring pattern | Affected records | Current result | Smallest future review question |
|---|---|---|---|
| Saving throws at the end of a Long Rest with separate success/failure outcomes | DMG Sewer Plague, Grackle-Lung, Lichen Plague, Mudpox, Shivering Sickness, The Gnawing Plague, Throat Leeches | Omitted where this would require substituting dawn or imply immediate recovery; Grackle-Lung has no remaining accurately representable Disease procedure | Should Disease progression triggers include a typed `LONG_REST_COMPLETED`, with separately typed success/failure outcomes and termination conditions? |
| Success-count cure thresholds | XDMG Cackle Fever, Lichen Plague, Mudpox | Omitted; the Cackle Fever Phase 4 ambiguity is unchanged | Should the Disease contract represent successful-save counters, with reset behavior present only when the source states it? |
| Disease-instance suspension by another Condition | Any Disease active when PHB Petrified applies | Cross-domain dependency remains omitted | Approve or reject a Condition-side suspension declaration and specify clock/resumption behavior |
| Disease effects outside current symptom vocabulary | Bluerot, Ghoul Gut, Frigid Woe, Sight Rot, Slimy Doom, Rusting, and others | Individual attack modifiers, ability-score changes, typed damage, Speed, and timed transformations omitted | Review reusable structures by mechanic family; do not implement a generic disease effect fallback |
| Secondary transmission beyond an infected creature starting a turn nearby | Mudpox, Sight Rot XDMG, The Gnawing Plague, Saprophytic Plague, Shaking Plague | Contact, food, area, or source eligibility details omitted | Determine whether multiple bounded exposure procedures are needed, preserving source-relative immunity only where stated |
| Inherited mechanics by reference | Redface | Source reference is retained, but effect inheritance is not implied | Decide whether canonical references should ever establish reusable mechanics inheritance |
| Underspecified saving throw | Super-Tetanus | Unsupported, with complete source entry registered | Revisit only if authoritative source evidence establishes the missing save inputs/outcomes |

Spell cures and spell-defined behavior remain typed external references where exact source identity is known. Creature/race-category immunity and special regional procedures that lack established canonical identities remain external or omitted; no references were invented.

If approved later, Long Rest progression would affect at least seven disease records. It could make Grackle-Lung partially representable, not fully represented, because its Dash and verbal-spell checks remain outside Disease v1. A typed successful-save threshold could recover some progression details in three records (XDMG Cackle Fever, Lichen Plague, and Mudpox), but their other unsupported mechanics would remain. These are estimates of affected records based on this complete 29-record inventory, not conversion promises.

## Cackle Fever preservation

The approved DMG Cackle Fever Phase 3 record remains unchanged. Its Phase 4 audit's uncertainty about which failures count toward the three-failure result—and whether successes reset that count—remains unresolved. This expansion does not add a consecutive/reset rule.

The XDMG Cackle Fever source record is separately represented as partial. Its Humanoid applicability and gnome exclusion, dice-based onset, stress save, psychic damage, temporary Incapacitated effect, nearby exposure, and source-relative 24-hour immunity are represented where the existing schema supports them. The three-success Long Rest cure and one-year immunity threshold are omitted because the approved recovery model supports a failure threshold, not a success threshold. No unsupported counter behavior was inferred.

## Source accounting

For the expanded Disease batch, the accounting manifest records 26 source records examined, 3 representatives excluded, 24 canonical outputs, 0 fully represented, 24 partial, and 2 unsupported. It accounts for 73 source-entry roots and 40 nested source paths. Its classification-part counts are 69 interpreted, 0 duplicate, and 135 omitted; complete original entries for omitted portions are preserved in `doc/disease/omitted.md`. No Disease duplicate entry was identified. Grackle-Lung is omitted for `SCHEMA_UNSUPPORTED`; Super-Tetanus's full source entry is preserved with cause `SOURCE_AMBIGUOUS` because its saving-throw inputs/outcomes are not stated in the source entry.

Together, the Phase 3/4 representative Disease entries and this batch account for all 29 Disease source records, 83 source-entry roots, and 45 nested source paths; 27 records have canonical outputs, all partial, and two remain unsupported. The Phase 3 representative manifest remains separate from the Phase 5 expansion manifest.

Across the current Phase 5 expansion, the manifest now accounts for 99 examined source records, 97 canonical outputs, 66 fully represented, 31 partial, and 2 unsupported. It accounts for 164 entry roots and records 122 interpreted, 18 duplicate, and 191 omitted classification parts. There are no new Disease structured-field/reference issues.

The entry classifications preserve complete source entry objects in the restricted register for review. They are bookkeeping and traceability records; they do not establish semantic correctness by themselves.

## Validation

The executed `npm run validate:core-rules` result was:

- 9 Draft 2020-12 schemas compiled.
- 37 examples validated.
- 34 Phase 3/4 canonical records validated.
- 97 Phase 5 canonical records validated, including 24 Disease outputs.
- 228 permanent checks passed, including Disease schema/identity/source metadata and pinned typed-reference checks, lifecycle reference integrity, batch accounting, supported-pattern assertions, and the preserved Cackle Fever regression checks.

`git diff --check` completed with exit code 0. Git emitted line-ending normalization warnings for existing LF files; no whitespace errors were reported.

Structural validation and selected semantic assertions passed. They do not establish complete source fidelity for all Disease records. The omission list above and the source-accounting registers remain subject to human review.

## Human review required

1. Review the 24 partial Disease interpretations and the unsupported Grackle-Lung and Super-Tetanus dispositions.
2. Decide whether to authorize a future Condition-side active-Disease suspension declaration for PHB Petrified, including clock and resumption semantics.
3. Decide whether a future Disease contract proposal should support Long Rest-triggered progression and successful-save thresholds.
4. Review cross-domain transmission and effect gaps before converting further records that depend on those patterns.

Phase 5 remains in progress; this Disease batch does not authorize expansion into another domain or backend integration.
