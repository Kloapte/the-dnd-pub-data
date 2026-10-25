# Core Rules Foundation Phase 5 Disease Contract Review

## Executive summary

This review compares all 29 source records in `src/main/resources/data/conditionsdiseases.json` (`$.disease[0..28]`) with the approved Disease and related Core Rules contracts, the three Phase 3/4 Disease representatives, the 24 Phase 5 Disease outputs, and the accounting registers. The authorized baseline is 27 partial canonical records and 2 unsupported records. This review changes no implementation artifact.

The Disease contract has useful, domain-specific structures for infection saves, selected exposure patterns, onset intervals, condition changes, a bounded symptom set, some progression branches, DC tracks, and recovery. It has no accurate representation for several recurring procedures: Long Rest progression with separate outcomes; success counters; event-conditioned and non-psychic damage procedures; several exposure types; and Disease-instance suspension by Petrified. Small additive extensions are preferable to a universal effects model.

The full-catalog review also finds source-fidelity problems in current Phase 5 records. These are canonical interpretation corrections, not schema gaps: Bluerot's DC 12 recovery save is represented as an infection save; XDMG Cackle Fever uses the 2014 great-stress trigger; DMG Sight Rot has a saving-throw recovery absent from its source; XDMG Sight Rot references PHB rather than XPHB cures; and Shaking Plague and The Rusting use PHB Exhaustion references where their source explicitly uses XPHB Exhaustion. These are documented for a later authorized correction pass and have not been changed here.

### Recommendations at a glance

| Proposal | Priority | Summary |
|---|---|---|
| DIS-1 Long Rest and elapsed progression | `REQUIRED` | Add a Disease-owned typed trigger for Long Rest completion and bounded elapsed-time cadence, keeping onset, progression, and recovery distinct. |
| DIS-2 Outcome counters | `REQUIRED` | Add counters scoped to a named save procedure for success/failure thresholds, explicit consecutive/cumulative behavior, stated reset behavior, termination, and duration-specific immunity. Preserve unspecified behavior as unresolved. |
| DIS-3 Disease effects | `REQUIRED` | Extend the Disease-specific symptom/outcome vocabulary for recurring mechanical families; keep bounded structs and refer to Conditions, spells, Rules, and Actions where those domains own the procedure. |
| DIS-4 Exposure procedures | `REQUIRED` | Add typed, bounded exposure triggers for material ingestion/contact, infected-creature contact, area proximity, and environmental prerequisites, with target eligibility and source immunity scoped to the actual source. |
| DIS-5 Petrified suspension | `REQUIRED` | Add one optional Condition-side declaration for suspending active Diseases, separate from immunity and cure; do not decide scheduler behavior the source does not state. |
| DIS-6 Redface effect reuse | `RECOMMENDED` | Add a narrow Disease effect-profile reference for Redface to the exact DMG Sight Rot record, without generic object inheritance. |

Priorities indicate design importance, not authorization. The reviewer must approve proposals before implementation.

## Scope and source inventory

Source codes and record counts in the 29-record catalog: PHB 6, DMG 3, XDMG 3, ToA 3, FRAiF 4, GoS 2, and one each from CM, EGW, IDRotF, OoW, OotA, TftYP, VRGR, and WDMM. Same-name records with different source codes remain distinct identities. The representatives are PHB Blinding Sickness, DMG Cackle Fever, and XDMG Sewer Plague.

The source locator column uses exact JSON paths. For nested list entries, the path proceeds through the named `items[n].entry` or nested `entries[n]` fields shown in the source object. The matrix marks the proposals that could address the identified gap; it does not assert that every portion of a record will become fully representable.

## Complete 29-record coverage matrix

| Source identity / locator | Source mechanics and current approved representation | Remaining limitation or fidelity finding | Relevant proposal |
|---|---|---|---|
| `disease:idrotf:arcane-blight` — `$.disease[0].entries[0..3]` | Canonical record has Humanoid applicability, DC 15 infection save, Long Rest DC reduction by 1d6, and DC-zero recovery. | The 12-hour necropolis exposure is not expressed. Three failed Long Rest saves transform the victim into a Nothic; the Disease counter outcome cannot express that. Failure reset policy is not stated. Greater Restoration ends infection and removes symptoms; source also distinguishes the character flaw. Current recovery omits `immunityAfterRecovery`, which the schema already supports for immunity after DC-zero recovery. | DIS-1, DIS-2, DIS-3, DIS-4 |
| `disease:phb:blinding-sickness` — `$.disease[1].entries[0..1]` | Phase 3 record represents Wisdom-check and save Disadvantage, Blinded, and the Contagion reference. | Remaining pain and milky-eye wording is descriptive; no additional mechanical consequence is stated. Preserve the representative as approved. | `NO_CHANGE` |
| `disease:toa:blue-mist-fever` — `$.disease[2].entries[0..1]` | Canonical record represents Giant/Humanoid applicability, DC 13 Constitution save, 1d6-hour onset, and a daily recovery save. | Source says contact with a blue magical mist; canonical `inhalation` is a route mismatch. Hallucinations and their ending are absent. Dispel Magic destroys the mist in a 20-foot square; the encounter's 1d6 areas are an environmental-hazard procedure. Source says the repeated save ends “the effect”; whether that means the hallucination alone or the infection itself should be resolved before attaching Disease termination. | DIS-1, DIS-3, DIS-4 |
| `disease:gos:bluerot` — `$.disease[3].entries[0..1]` | Canonical record represents Humanoid eligibility, wound transmission, and 1d4-hour onset. | The canonical DC 12 infection save is unsupported: the source's DC 12 save is the end-of-Long-Rest recovery save, while no initial infection save is specified. Source mechanics omitted include Constitution/Charisma loss by 1d4 to minimum 3, recovery of one point of each score on success, 4d8 necrotic damage on failure, radiant vulnerability, underwater breathing, disease termination when all lost points return, and the no-hit-point-recovery restriction when this damage reduces HP to zero. | DIS-1, DIS-2, DIS-3, DIS-4 |
| `disease:dmg:cackle-fever` — `$.disease[4].entries[0..4]` | Approved Phase 3 record represents Humanoid/gnome eligibility, 1d4-hour onset, PHB Exhaustion, great-stress DC 13 Constitution saves, mad-laughter damage/Condition, source-relative spread immunity, Long Rest DC reduction, recovery at DC 0, and the indefinite-madness Rule outcome. | Preserve the Phase 4 audit: only failures on the Long Rest recovery procedure count for the three-failure outcome. The source does not say whether failures must be consecutive or whether a success resets the counter. Stress, end-of-turn, and contagion saves are separate procedures and do not count. No reinterpretation is proposed. | DIS-2 (preserve ambiguity only); otherwise `NO_CHANGE` |
| `disease:xdmg:cackle-fever` — `$.disease[5].entries[0]`, `entries[1].items[0].entry`, `entries[1].items[1].entry`, `entries[2].entries[0].entries[0]`, `entries[2].entries[1].entries[0]` | Canonical record represents Humanoid/gnome eligibility, 1d4-day onset, XPHB Exhaustion, a temporary Incapacitated symptom, a saving throw, and nearby exposure with 24-hour source-relative immunity. | Current output incorrectly uses the DMG `GREAT_STRESS` trigger. XDMG calls for a Constitution save each time the infected creature takes non-Psychic damage while Exhausted. It omits ingestion from tainted potions/elixirs, automatic success at the end of the one-minute laughter effect, three successful Long Rest saves to end the contagion, and one-year immunity after recovery. The source-relative immunity applies to the particular infected creature, not just a particular symptom. | DIS-1, DIS-2, DIS-3, DIS-4 |
| `disease:phb:filth-fever` — `$.disease[6].entries[0..1]` | Canonical record represents Disadvantage on Strength checks and Strength saves, plus the Contagion reference. | Disadvantage on attack rolls that use Strength is a supported but absent roll category. | DIS-3 |
| `disease:phb:flesh-rot` — `$.disease[7].entries[0..1]` | Canonical record represents Disadvantage on Charisma checks and the Contagion reference. | Vulnerability to all damage is absent. | DIS-3 |
| `disease:egw:frigid-woe` — `$.disease[8].entries[0..2]` | Canonical record represents DC 11 Constitution exposure save and 1d4-day onset. | Speed decreases by 5 feet while infected and by another 5 feet after each 10-day interval from symptom onset after a failed save; Speed 0 causes death and an ice statue transformation. Only the manufactured antidote cures it and can be drunk as an Action. “Cannot be cured by conventional treatment or magic” must be preserved. | DIS-1, DIS-3, DIS-4 |
| `disease:oow:ghoul-gut` — `$.disease[9].entries[0..1]` | Canonical record represents tainted-water ingestion with DC 12 Constitution save and a DC 12 Long Rest recovery save. | Symptoms begin at the first Long Rest or on later excitement, followed by 2d4 necrotic damage. No HP or Hit Dice are regained from nonmagical sources. Failed Long Rest saves deal another 2d4 damage; success cures. A DC 15 Wisdom (Medicine) check during a Long Rest can cure another creature, but the caregiver gains no rest benefit. The canonical recovery save currently omits its failure outcome and immediate symptom trigger. | DIS-1, DIS-3, DIS-4 |
| `disease:oota:grackle-lung` — `$.disease[10].entries[0..2]` | No canonical shell, correctly avoiding a misleading infection-save interpretation. | On each Long Rest completed by a living, breathing creature in Gracklstugh, DC 11 Constitution failure adds Exhaustion; level 6 causes death. At Exhaustion 1+, Constitution checks gate Dash and verbal spell casting at DC 10 + current level; duergar/derro have Advantage on the checks. Below level 1, effects end and one-week immunity begins. Any disease-curing effect cures it. This is a useful DIS-1 case but also needs bounded exposure and Action/Spell constraints. | DIS-1, DIS-3, DIS-4 |
| `disease:fraif:lichen-plague` — `$.disease[11].entries[0]`, `entries[1].items[0].entry`, `entries[1].items[1].entry`, `entries[2].entries[0].entries[0]`, `entries[2].entries[1].entries[0]` | Canonical record represents creature-type eligibility, contact, DC 14 infection save, and 1d4-day onset. | Long Rest save failure adds Exhaustion; three successful Long Rest saves end it, explicitly not necessarily consecutively, and grant permanent immunity. Humanoids that die while infected rise immediately as a Zombie. Skin contact with the lichen patch spreads the disease, with general 24-hour immunity after a successful save. | DIS-1, DIS-2, DIS-3, DIS-4 |
| `disease:phb:mindfire` — `$.disease[12].entries[0..1]` | Canonical record represents Intelligence-check/save Disadvantage and typed references to Contagion and Confusion. | “Behaves as if under Confusion during combat” is linked but not declared as a mechanical effect or applicability interval. Decide whether the exact Spell reference is sufficient or whether a Disease-owned typed spell-effect relation is required. | DIS-3; future Spell dependency |
| `disease:fraif:mudpox` — `$.disease[13].entries[0]`, `entries[1].items[0].entry`, `entries[2].entries[0].entries[0]`, `entries[2].entries[1].entries[0]` | Canonical record represents contact infection and the XPHB Heal and Lesser Restoration cures. | Beast/Humanoid target applicability is supported by the current schema but missing from the canonical record. The daily-at-dawn DC 12 branch is already supported by v1, but is absent from this record: failure causes vomiting of prior-day food and +1 Exhaustion. Three consecutive successes end the contagion. Skin-contact spread to Humanoids grants immunity from that particular infected creature for 24 hours after a successful save. Separate existing-schema interpretation gaps from the counter and exposure schema gaps. | DIS-1, DIS-2, DIS-3, DIS-4; supported applicability/progression needs canonical correction |
| `disease:gos:redface` — `$.disease[14].entries[0]` | Canonical record identifies inhalation and has a typed `same-effects-as` reference to DMG Sight Rot. | Source explicitly states that effects are identical to Sight Rot while exposure is polluted air rather than tainted water. The present reference does not cause effects to be inherited by the canonical contract. It is one clear test for bounded, source-qualified Disease mechanics reuse. | DIS-6 |
| `disease:cm:saprophytic-plague` — `$.disease[15].entries[0..4]` | Canonical record represents ingestion at DC 20, 1d4-hour onset, and an Exhaustion level change. | Creature-type applicability (Beast/Humanoid) is supported but absent. The 10-foot exposure is not ingestion; an infected creature can infect when another comes within range. Success grants 24-hour immunity to the plague, and disease-immune creatures automatically succeed. Further effects include another Exhaustion level every 24 hours, a zombie-like state after 24 hours, death into a magenta ooze, food-spore contamination after 2d12 hours, and disease-curing magic / Purify Food and Drink. `cannotReduceUntilDiseaseEnds` on the current level change is not stated by the source. | DIS-1, DIS-3, DIS-4 |
| `disease:phb:seizure` — `$.disease[16].entries[0..1]` | Canonical record represents Dexterity-check/save Disadvantage and Contagion reference. | Disadvantage on Dexterity-based attack rolls is absent. | DIS-3 |
| `disease:dmg:sewer-plague` — `$.disease[17].entries[0..4]` | Canonical record represents Humanoid eligibility, carrier wound/contaminated filth or offal exposures, DC 11 infection save, 1d4-day onset, initial PHB Exhaustion, half Hit Dice HP recovery, and no HP from a Long Rest. | At the end of each Long Rest, failure adds Exhaustion; success removes a level; disease ends below level 1. Disease v1 has a dawn trigger but no Long Rest-completion branch. This is a principal DIS-1 fixture. Keep it separate from the XDMG reprint. | DIS-1 |
| `disease:xdmg:sewer-plague` — `$.disease[18].entries[0]`, `entries[1].items[0..2].entry`, `entries[2].entries[0].entries[0]` | Approved Phase 3/4 record represents Humanoid eligibility, qualified exposures, DC 11 infection save, 1d4-day onset, XPHB Exhaustion, both rest restrictions, and daily-at-dawn progression/termination. | No mechanical gap was found in the interpreted disease procedure. The source's origin and ecology are descriptive; the Phase 3 source accounting already distinguishes that text from mechanics. Preserve XPHB Condition references and the separate identity. | `NO_CHANGE` |
| `disease:fraif:shaking-plague` — `$.disease[19].entries[0]`, `entries[1].items[0..2].entry`, `entries[2].entries[0].entries[0..1]` | Canonical record represents Humanoid eligibility, a contact/DC 11 infection save, an Exhaustion change, Dexterity check/save Disadvantage, and daily-at-dawn progression. | Source uses XPHB Exhaustion, but current canonical references PHB Exhaustion. `cannotReduceUntilDiseaseEnds` conflicts with the source's successful daily saves reducing Exhaustion before disease ends; the approved Long Rest restriction structure is a better fit for “Long Rest doesn't reduce.” The disease also has a DC 11 Constitution save when casting a Somatic spell while Exhausted, with Action/Bonus Action/Reaction and spell-slot outcomes, plus skin-contact spread and source-relative immunity. | DIS-1, DIS-3, DIS-4; correct edition and restriction using existing pattern |
| `disease:toa:shivering-sickness` — `$.disease[20].entries[0..3]` | Canonical record represents Giant/Humanoid applicability, a DC 11 Constitution save, and 2d6-hour onset. | Exposure has two alternatives: qualifying insect damage then exposure at encounter end; or no repellent since the previous Long Rest then exposure at Long Rest completion. Natural armor grants Advantage on the infection save. After onset, Hit Dice restore half HP, Long Rest restores no HP, all ability checks and attack rolls have Disadvantage, and a Long Rest save can end the disease. Generic `contact` does not encode these prerequisites; the current record omits the supported attack modifier and recovery. | DIS-1, DIS-3, DIS-4 |
| `disease:dmg:sight-rot` — `$.disease[21].entries[0..2]` | Canonical record represents tainted-water ingestion, DC 15 infection save, one-day onset, and sight impairment. | The canonical Long Rest recovery save is unsupported by the source. Instead, a numeric penalty to attack rolls and sight-dependent checks starts at -1 and worsens after each Long Rest; at -5, Blinded applies until magic restores sight. Ointment requires a named flower, proficient Herbalism Kit, one hour, pre-rest application, and three doses to cure; one dose prevents worsening after a rest. | DIS-1, DIS-3; canonical correction required for unsupported save |
| `disease:xdmg:sight-rot` — `$.disease[22].entries[0]`, `entries[1].entries[0].entries[0..1]` | Canonical record represents water ingestion, DC 15 save, XPHB Blinded, and two spell cures. | Current spell references use PHB identities although the source names XPHB spells. Skin-contact spread to Humanoids and source-relative 24-hour immunity are absent. Herbalism Kit ointment suppresses the Disease for 24 hours per application; a total of 72 suppressed hours across three doses ends it. This is a cumulative timed suppression, distinct from recovery or Petrified suspension. | DIS-2, DIS-3, DIS-4; correct spell source identity |
| `disease:phb:slimy-doom` — `$.disease[23].entries[0..1]` | Canonical record represents Constitution-check/save Disadvantage. | Taking any damage applies Stunned until the end of the creature's next turn. The trigger is an event; the Disease's state modifiers and temporary Condition have separate scopes. | DIS-3 |
| `disease:wdmm:spider-eggs` — `$.disease[24].entries[0..1]` | Canonical record represents Restrained and a Lesser Restoration reference. | Defeat/stabilization precedes awakening after 1d4 hours, cocooned 10 feet up; escape takes an Action and DC 15 Strength check. Eggs hatch after 2d12 hours and kill the host; Lesser Restoration or similar kills the eggs. Party state and escape Actions cross encounter/Action boundaries. | DIS-1, DIS-3; encounter/Action dependency |
| `disease:tftyp:super-tetanus` — `$.disease[25].entries[0]` | No canonical shell; remains unsupported. | Source specifies 2d10 damage at the start of each turn and a repeat save at the end of every minute after exposure; it does not specify the save ability, DC, or failure behavior. The omission is genuine source ambiguity plus unsupported typed damage/timing, not a license to guess. Keep unsupported unless authoritative evidence fills the save fields. | DIS-1, DIS-3; remain unsupported pending source evidence |
| `disease:vrgr:the-gnawing-plague` — `$.disease[26].entries[0]`, `entries[1..5].entries[0..1]` | Canonical record represents wound/contact routes, DC 10 Constitution save, 1d2-day onset, Exhaustion, half HP from Hit Dice, and no HP from a Long Rest. | Long Rest save failure adds Exhaustion; success removes it; recovery occurs below level 1. DC can vary with spread severity. All forms of rats and wererats are immune. Disease v1 can represent creature-reference exclusions, but the canonical record does not use them; the family-wide scope may need an approved creature-family reference rather than invented individual targets. | DIS-1, DIS-3, DIS-4; decide reference scope |
| `disease:fraif:the-rusting` — `$.disease[27].entries[0..3]`, especially `entries[2].items[0..2].entry` | Canonical record represents wound/area save, Exhaustion progression, and Remove Curse. | Source is XPHB, but current level changes and daily outcomes reference PHB Exhaustion. The “cannot reduce” flag conflicts with the daily successful save reducing Exhaustion; the source only bars reduction from finishing a Long Rest while cursed. Other mechanics include Constructs/Undead automatically succeeding on infection save, Speed -5, level-six nonfatal transformation to a Rusted Construct, immunities, curse ending, and remove-curse termination. | DIS-1, DIS-3, DIS-4; correct edition and Long Rest restriction using existing pattern |
| `disease:toa:throat-leeches` — `$.disease[28].entries[0..1]` | Canonical record represents Giant/Humanoid applicability, water ingestion/DC 12 save, 1d6-hour onset, and +1 PHB Exhaustion. | Long Rest failure adds Exhaustion; success removes one; it ends below level 1. “Can't be removed except as described below” means ordinary level reduction is restricted while the disease remains, but the listed Long Rest recovery branch can reduce it. Clean, boiled, or purified water avoids infection. Current canonical record omits the repeated save and termination branch. | DIS-1, DIS-3, DIS-4 |

### Coverage matrix by proposal

| Proposal | Records with directly evidenced mechanics | Source-path families |
|---|---|---|
| DIS-1 | Arcane Blight, Blue Mist Fever, Bluerot, both Cackle Fever records, Ghoul Gut, Grackle-Lung, Lichen Plague, Mudpox, both Sewer Plague records, Shaking Plague, Shivering Sickness, both Sight Rot records, The Gnawing Plague, The Rusting, Throat Leeches, Super-Tetanus, Spider Eggs | See the exact row locators above; recurring procedures appear in `.entries[n]`, nested `items[n].entry`, and Fighting the Contagion nested entries. |
| DIS-2 | Arcane Blight, DMG Cackle Fever, XDMG Cackle Fever, Lichen Plague, Mudpox | Save thresholds, reset/consecutive wording, recovery counts, and post-recovery immunity. |
| DIS-3 | Blue Mist Fever, Bluerot, both Cackle Fever records, Filth Fever, Flesh Rot, Frigid Woe, Ghoul Gut, Grackle-Lung, Lichen Plague, Mindfire, Mudpox, Saprophytic Plague, Seizure, both Sewer Plague records, Shaking Plague, Shivering Sickness, both Sight Rot records, Slimy Doom, Spider Eggs, Super-Tetanus, The Gnawing Plague, The Rusting, Throat Leeches | Symptom paragraphs and nested disease-effect lists; the current representation/gaps are itemized per source path above. |
| DIS-4 | Arcane Blight, Blue Mist Fever, Bluerot, XDMG Cackle Fever, Ghoul Gut, Grackle-Lung, Lichen Plague, Mudpox, Saprophytic Plague, DMG Sewer Plague, Shaking Plague, Shivering Sickness, both Sight Rot records, The Gnawing Plague, The Rusting, Throat Leeches | Infection exposure paragraphs, contamination material, creature contact, vicinity, area, and location/creature prerequisites. |
| DIS-5 | PHB Petrified Condition `$.condition[18].entries[0].items[5]`; affects any active Disease instance | Condition-side cross-domain lifecycle declaration; not a Disease source record. |
| DIS-6 | Redface and DMG Sight Rot | Redface `$.disease[14].entries[0]` explicitly says identical effects to Sight Rot; bind only to `disease:dmg:sight-rot`. |

## Reusable mechanics taxonomy and proposals

### DIS-1 — Long Rest and elapsed progression (`REQUIRED`)

**Current limitation.** Disease v1 already has `progression` with branches, saving throw inputs, success/failure outcomes, and termination conditions. Its trigger vocabulary is limited to daily-at-dawn, great stress, and end-of-turn while a symptom is active. The separate `recovery` object has a `frequency` but cannot represent distinct successful and failed outcomes. A 24-hour interval, dawn, Long Rest completion, symptom onset, and end of turn are therefore not interchangeable.

**Source behavior.** End-of-Long-Rest saving throws recur in DMG Sewer Plague (`$.disease[17].entries[3..4]`), Bluerot (`[3].entries[1]`), Ghoul Gut (`[9].entries[0]`), Grackle-Lung (`[10].entries[0]`), Lichen Plague (`[11].entries[1].items[0].entry` and `[11].entries[2]`), Shivering Sickness (`[20].entries[3]`), The Gnawing Plague (`[26].entries[4].entries[0]`), Throat Leeches (`[28].entries[0]`), and DMG Sight Rot's symptom worsening (`[21].entries[1]`). Daily-at-dawn is already represented and must remain distinct, including XDMG Sewer Plague, Mudpox, Shaking Plague, and The Rusting. DMG Cackle Fever has a Long Rest recovery DC procedure; do not re-scope its separate failure counter.

**Proposed extension.** Extend the existing Disease `SAVING_THROW_BRANCH.trigger` vocabulary with bounded timing objects such as `LONG_REST_COMPLETED` and, if needed, an elapsed interval anchored to infection or symptom onset. Retain `onSuccess`, `onFailure`, and `endsWhen`; do not add a second Long Rest branch model. Make the phase anchor explicit only when the source states it. Keep `recovery` for a source-defined direct cure or a recovery procedure that truly has no distinct outcomes; avoid representing the same save once under `recovery` and again under `progression`.

Before, the existing branch can express dawn but not Long Rest completion:

```json
{
  "kind": "SAVING_THROW_BRANCH",
  "branchId": "daily-save",
  "trigger": {
    "kind": "DAILY_AT",
    "time": "DAWN"
  },
  "savingThrow": {
    "ability": "constitution",
    "dc": 11
  },
  "onSuccess": [],
  "onFailure": []
}
```

Proposed Long Rest branch, for DMG Sewer Plague:

```json
{
  "kind": "SAVING_THROW_BRANCH",
  "branchId": "long-rest-exhaustion",
  "trigger": {
    "kind": "LONG_REST_COMPLETED",
    "phase": "AFTER_SYMPTOM_ONSET"
  },
  "savingThrow": {
    "ability": "constitution",
    "dc": 11
  },
  "onFailure": [
    {
      "kind": "CHANGE_CONDITION_LEVEL",
      "condition": {
        "targetType": "condition",
        "id": "condition:phb:exhaustion"
      },
      "amount": 1
    }
  ],
  "onSuccess": [
    {
      "kind": "CHANGE_CONDITION_LEVEL",
      "condition": {
        "targetType": "condition",
        "id": "condition:phb:exhaustion"
      },
      "amount": -1
    }
  ],
  "endsWhen": {
    "kind": "CONDITION_LEVEL_AT_OR_BELOW",
    "condition": {
      "targetType": "condition",
      "id": "condition:phb:exhaustion"
    },
    "level": 0
  }
}
```

The exact final names and whether the phase anchor belongs in `trigger` or another small field require approval. Long Rest mechanics for Grackle-Lung also need an environmental exposure condition and disease-linked check applicability; DIS-1 alone does not complete it.

**Impact and validation.** Additive union variants preserve current examples. Validate that `LONG_REST_COMPLETED` does not alias `DAILY_AT/DAWN`; all referenced Condition levels exist; each save's success/failure outcomes are explicit; termination is attached to the correct branch; and no same procedure is duplicated in `recovery` and `progression`. Migrate only records whose source trigger is Long Rest. Expected to address missing progression in at least nine records; other mechanics may keep those records partial.

**Dependencies.** DIS-2 for count thresholds; DIS-3 for unsupported outcomes; DIS-4 for qualifying exposure context. Independent of DIS-5 and DIS-6.

### DIS-2 — Saving-throw outcome counters (`REQUIRED`)

**Current limitation.** `recovery.failureThreshold` supports a failure count, but it is attached to a recovery object, only describes an outcome, has no named save-branch identity, cannot count successes, cannot state consecutive versus cumulative behavior, cannot preserve reset policy, and only supports the existing bounded failure outcome vocabulary. `immunityAfterRecovery` is Boolean and cannot carry the XDMG Cackle Fever one-year duration.

**Source distinctions.**

- DMG Cackle Fever (`$.disease[4].entries[4]`): three failed Long Rest recovery saves cause indefinite Madness. Keep the existing Phase 4 decision: no consecutive/reset rule is given.
- Arcane Blight (`$.disease[0].entries[2]`): three failed repeated Long Rest saves cause Nothic transformation. The source does not specify a reset rule.
- XDMG Cackle Fever (`$.disease[5].entries[2].entries[0].entries[0]`): three successful Long Rest saves end the disease and grant one-year immunity. It does not explicitly specify a reset rule.
- Lichen Plague (`$.disease[11].entries[2].entries[0].entries[0]`): three successful Long Rest saves end the disease and grant permanent immunity; the source explicitly says they need not be consecutive.
- Mudpox (`$.disease[13].entries[1].items[0].entry`): three consecutive daily-at-dawn successes end the disease; consecutiveness makes a failed save reset the success sequence.

**Proposed extension.** Add a counter attached to a `branchId`, not to the Disease globally. Counted outcome, threshold, sequence behavior, reset behavior, threshold result, and post-recovery immunity are explicit. Do not assign a runtime reset default when the source is silent.

Current bounded failure counter:

```json
{
  "failureThreshold": {
    "count": 3,
    "outcome": {
      "kind": "RULE_PROCEDURE_OUTCOME",
      "reference": {
        "targetType": "rule",
        "id": "rule:dmg:madness"
      },
      "procedure": "indefinite-madness"
    }
  }
}
```

Proposed branch-scoped counter shape (illustrative; unspecified reset remains explicit):

```json
{
  "counter": {
    "kind": "SAVING_THROW_OUTCOME_COUNTER",
    "branchId": "long-rest-recovery",
    "countedOutcome": "SUCCESS",
    "threshold": 3,
    "sequence": "NOT_REQUIRED",
    "resetOnOppositeOutcome": "SOURCE_UNSPECIFIED",
    "onThreshold": {
      "kind": "END_DISEASE"
    },
    "immunityAfterRecovery": {
      "duration": {
        "amount": 1,
        "unit": "year"
      }
    }
  }
}
```

This proposal would need a new duration unit `year` or a Disease-specific recovery duration definition: current Disease `interval` supports minute/hour/day/week only. For Mudpox, the explicit consecutive form would set `sequence: "CONSECUTIVE"` and reset on a failed save. For Lichen, successes need not be consecutive, so the counter is cumulative and failures retain the success count. For XDMG Cackle Fever, the source specifies three successes but no explicit reset behavior; do not infer one. For DMG Cackle Fever, reset behavior remains `SOURCE_UNSPECIFIED`, and its counter remains scoped solely to Long Rest recovery saves. Do not convert either unresolved value into an executable policy until the human reviewer decides how to handle source ambiguity.

**Impact and validation.** Additive branch counter property; old `failureThreshold` can remain accepted for compatibility and be migrated only when an approved meaning-preserving mapping exists. Check branch resolution, counter outcome type, duration, reset/sequence compatibility, threshold result, disease termination, immunity duration, and that non-counted stress/contagion/turn saves never contribute. Expected affected records: Arcane Blight, DMG and XDMG Cackle Fever, Lichen Plague, and Mudpox; several remain partial for other reasons.

**Dependencies.** Depends on DIS-1 to identify the save branch. DIS-3 for transformation/Madness outcomes; DIS-4 for spread immunity distinctions. DMG Cackle's reset question remains independently unresolved.

### DIS-3 — Bounded Disease effects and symptoms (`REQUIRED`)

**Current limitation.** `symptoms` currently supports Condition application, one ability check/save modifier, sense impairment, Condition-level change, and one triggered temporary Condition. Progression damage is restricted to psychic damage. There is no attack-roll modifier, ability-score track, vulnerability, Speed change, damage outcome, damage-trigger predicate, resource restriction by recovery source, spell-component limitation, capability change, or typed transformation outcome. Several outcomes can reference Conditions, but the Condition model should remain responsible for what that Condition means.

**Source-backed families and proposed bounded additions.**

| Family | Evidence | Proposed Disease-owned structure / boundary |
|---|---|---|
| Roll modifiers | Filth Fever, Seizure, Shivering Sickness, DMG Sight Rot | `ATTACK_ROLL_MODIFIER` plus numeric modifier variant scoped by ability/sense and source lifecycle; share small roll-target concepts only, not the full Action/Rule operations. |
| Ability-score loss/recovery | Bluerot | `ABILITY_SCORE_CHANGE` with ability, rolled amount/bounds, active loss, and a recovery track tied to restoring lost points. It must not let unrelated score bonuses cure the disease. |
| Damage and vulnerability | Bluerot, Ghoul Gut, both Cackle Fever records, Super-Tetanus, Flesh Rot | Typed damage outcomes with observed damage types/dice and event trigger. Damage vulnerability should identify damage category (`all`, `radiant`) and lifecycle; preserve the special zero-HP recovery restriction. Do not expand Rule damage resolution wholesale. |
| Speed changes and thresholds | Frigid Woe, The Rusting | Typed flat Speed change, interval/progression relationship, threshold outcome, and source-qualified transformation. Keep speed change distinct from movement permission. |
| Event-triggered Condition | XDMG Cackle Fever, Slimy Doom | Disease-specific trigger references (e.g. non-Psychic damage while Exhausted; any damage) and a typed Condition duration/end procedure. Reuse Condition references; do not turn these into generic event expressions. |
| Recovery restrictions | DMG Sewer Plague, Ghoul Gut, Shivering Sickness, The Gnawing Plague | Extend the existing `conditionGatedRestrictions` vocabulary for no recovery from nonmagical sources, no Hit Dice recovery, and exact Half/None resource outcomes. Preserve Condition threshold and Long Rest gates. |
| Capabilities and spell limitations | Bluerot, Saprophytic Plague, Grackle-Lung, Shaking Plague | Small values for breathing underwater, awareness/behavior states only when they have mechanical consequences, and spell component / Action check requirements. Spell execution remains Spell-owned; Action availability remains Action-owned. |
| Disease outcomes/transformation | Arcane Blight, Frigid Woe, Lichen Plague, Saprophytic Plague, Spider Eggs, The Rusting | Typed outcome families for death, Condition application, or transformation to an identified creature/state. Reference a target only when source identity exists; don't embed a creature model or invent a Chapter 9 entity. |
| External procedure links | Mindfire, Ghoul Gut, Frigid Woe, Spider Eggs, The Rusting | Typed references to Confusion, Medicine check, antidote/item, Lesser Restoration, Remove Curse, and other established source targets, with Disease-specific relation and trigger. Do not make an opaque reference imply procedure details absent from the target record. |

Example: current symptom model can state check/save Disadvantage, but not Filth Fever's Strength-based attack Disadvantage:

```json
{
  "kind": "ABILITY_ROLL_MODIFIER",
  "roll": "ability-check",
  "direction": "disadvantage",
  "ability": "strength"
}
```

Proposed distinct attack modifier:

```json
{
  "kind": "ATTACK_ROLL_MODIFIER",
  "actor": "affected-creature",
  "direction": "DISADVANTAGE",
  "ability": "strength",
  "lifecycle": "WHILE_DISEASE_ACTIVE"
}
```

Do not settle on that shape until the same review also examines Shivering Sickness's all-attack disadvantage and Sight Rot's numeric, sight-dependent penalty. A separate numeric modifier may be needed; changing attack disadvantage into an integer modifier would conflate distinct behavior.

**Impact and validation.** Mostly additive variants under Disease; several categories need more than one small object and should be delivered incrementally. Existing schemas/records remain compatible. Validation must cover direction, actor, lifecycle, units, lower/upper limits, legal target references, condition type, and trigger compatibility. Regression assertions should include each named source mechanic and detect duplicate representation. Records with concentrated missing effects include Bluerot, Frigid Woe, Ghoul Gut, both new-edition Cackle Fever mechanics, Shivering Sickness, both Sight Rot records, Slimy Doom, Saprophytic Plague, and The Rusting.

**Dependencies.** DIS-1 for scheduled effects, DIS-2 for threshold outcomes, DIS-4 for exposure triggers. Condition, Rule, Action, and Spell stay owners of their own procedures.

### DIS-4 — Exposure and transmission procedures (`REQUIRED`)

**Current limitation.** `infection.transmission` has broad route enums; `exposures` only describes carrier wounds or contact with filth/offal; `secondaryExposures` only describes a symptomatic creature, target starts its turn within a distance, and immunity tied to that symptom and exposing creature. This cannot express contact with an infected creature, contact with a particular infected lesion, a target entering/coming within a radius, contaminated water/food/mud, repeated environmental exposure, Long Rest or encounter prerequisites, or different immunity scopes.

**Source distinctions.** Skin contact with an infected person appears in Mudpox, Shaking Plague, and XDMG Sight Rot; skin contact with a particular lesion appears in Lichen Plague. Cackle Fever uses a target starting its turn within 10 feet, but DMG requires the source to be in mad laughter; XDMG requires an emanation from any infected creature. Saprophytic Plague triggers when a Beast or Humanoid comes within 10 feet and grants 24-hour disease immunity after success, not immunity only to one symptom. Contaminated ingestion includes Ghoul Gut, both Sight Rot sources, and Throat Leeches; contaminated mud, Sewer Plague material, tainted potions, and spore-contaminated food are not interchangeable materials. Environmental triggers include Arcane Blight's 12 hours in a necropolis, Grackle-Lung's Long Rest in Gracklstugh, Rusting's one hour in an affected area, and Shivering Sickness's damage/repellent alternatives.

**Proposed extension.** Keep the initial infection save separate from secondary transmission. Extend Disease exposure unions with only source-observed variants, including `INGEST_CONTAMINATED_MATERIAL`, `SKIN_CONTACT_WITH_INFECTED_CREATURE`, `CONTACT_WITH_INFECTED_SYMPTOM`, `ENTER_OR_APPROACH_INFECTED_AREA`, and explicit source prerequisites such as `FINISH_LONG_REST_IN_AREA` or `TAKE_QUALIFYING_DAMAGE_FROM_CREATURE`. Each exposure carries its own source, target eligibility, saving throw, failure result, and source-supported success immunity. A disease may have multiple alternative exposures; do not assume every exposure is secondary or uses a nearby-symptomatic trigger.

Current secondary exposure is restricted to a symptomatic source and start-turn event:

```json
{
  "kind": "NEARBY_SYMPTOMATIC_CREATURE",
  "trigger": "target-starts-turn-within-distance",
  "symptomId": "mad-laughter",
  "distance": 10,
  "unit": "feet"
}
```

Proposed explicit difference for XDMG Cackle Fever (illustrative):

```json
{
  "kind": "INFECTED_CREATURE_EMANATION",
  "trigger": "target-starts-turn-within-area",
  "area": {
    "kind": "EMANATION",
    "radiusFeet": 10
  },
  "sourceState": "DISEASE_INFECTED",
  "targetCreatureTypes": [
    "humanoid"
  ],
  "excludedTargets": [
    {
      "targetType": "race",
      "id": "race:phb:gnome"
    }
  ],
  "savingThrow": {
    "ability": "constitution",
    "dc": 10
  },
  "onFailure": {
    "kind": "INFECT_TARGET"
  },
  "onSuccess": {
    "kind": "SOURCE_RELATIVE_DISEASE_IMMUNITY",
    "source": "exposing-creature",
    "duration": {
      "amount": 24,
      "unit": "hour"
    }
  }
}
```

**Impact and validation.** Additive exposure variants. Keep eligibility/route relationships explicit and validate success immunity scope (disease, symptom, source creature, or global disease), duration, and separate exposure IDs. Reject unsourced exposure assumptions. Expected to affect at least 15 records, though some route values already fit the current schema; new work is mostly trigger/prerequisite precision.

**Dependencies.** DIS-1 for location/Long Rest triggers; DIS-2 for immunity-after-recovery; DIS-3 for infection consequences. No Creature/region entities may be invented to make an exposure fit.

### DIS-5 — Active Disease suspension from PHB Petrified (`REQUIRED`)

**Source and current limitation.** `conditionsdiseases.json $.condition[18].entries[0].items[5]` says Petrified creatures are immune to poison and disease, but an existing poison or disease is suspended, not neutralized. Condition v1 currently has `CATEGORY_IMMUNITY` for poison/disease, which models acquiring immunity; Disease v1 has no external suspension lifecycle. These are distinct effects.

**What the source establishes.** The Disease instance remains present and not neutralized while Petrified. It does not say whether incubation, durations, or Long Rest/daily saves elapse; whether progression or recovery attempts occur; whether existing symptoms/Condition levels remain active, pause, or are removed; or what exact procedure resumes when Petrification ends. Resumption after the suspending Condition ends is a plausible reading, not a fully specified scheduler rule.

**Proposed Condition-side field.** Add one optional typed effect to Condition, not a field on every Disease:

Before, the current effect only addresses immunity:

```json
{
  "kind": "CATEGORY_IMMUNITY",
  "category": "poison-and-disease",
  "lifecycle": "while-condition-active"
}
```

Proposed additional declaration:

```json
{
  "kind": "SUSPEND_ACTIVE_DISEASES",
  "applies": {
    "kind": "WHILE_CONDITION_ACTIVE"
  },
  "preservesInstance": true,
  "resumeWhen": {
    "kind": "SUSPENDING_CONDITION_ENDS"
  }
}
```

The declaration should not specify clock behavior or which Disease subeffects are suspended until the source or a separate approved execution decision establishes them. The canonical PHB Petrified record remains unchanged pending approval.

**Impact and validation.** Additive Condition schema effect, no Disease schema migration required. Validate that it does not replace acquisition immunity, does not terminate a Disease, and appears only where source evidence supports it. Tests must not invent clock pausing, save timing, symptom removal, or resume timing. It affects PHB Petrified and any future Condition with an explicit suspension source; it does not make any of the 29 Disease records intrinsically complete.

**Dependencies.** Independent schema-wise, but cross-domain with Disease scheduling (DIS-1) and Disease effects (DIS-3). Runtime semantics remain an approval question.

### DIS-6 — Bounded reuse of another Disease effect profile (`RECOMMENDED`)

**Source and current limitation.** GoS Redface (`$.disease[14].entries[0]`) says its effects are identical to Sight Rot in the DMG, but its cause is airborne pollutants rather than tainted drinking water. Current Redface retains a `same-effects-as` Disease reference but the schema does not say that symptoms, progression, or recovery are reused. Similar names/reprints alone are insufficient evidence; this case has explicit source wording.

**Proposed structure.** Add a Disease-specific `effectProfile` relation naming the exact source-qualified Disease whose mechanical profile is reused. Local infection/exposure remains owned by Redface. The relation should not inherit identity, source metadata, exposure, or unrelated applicability automatically. Validate that the exact source-qualified target matches the explicit source reference, that references are acyclic, and that the source explicitly supports reuse. Do not create general object inheritance.

Before, a normal relationship reference does not identify which mechanics are reused:

```json
{
  "targetType": "disease",
  "id": "disease:dmg:sight-rot",
  "relationship": "same-effects-as"
}
```

Proposed narrowed form:

```json
{
  "kind": "REUSE_DISEASE_EFFECT_PROFILE",
  "sourceDisease": {
    "targetType": "disease",
    "id": "disease:dmg:sight-rot"
  },
  "components": [
    "symptoms",
    "progression",
    "recovery"
  ]
}
```

The component list requires human review: “effects identical” may include or exclude target applicability, and the referenced DMG Sight Rot currently has incomplete/incorrect recovery representation. This relation must not inherit those errors by implication. Correct the source effect profile first under separate authorization.

**Impact and validation.** Additive Disease property; existing references and records remain valid. Validate acyclicity, exact source-qualified target identity, component availability, local override conflicts, and that a reference does not silently substitute infection mechanics. This is an explicit GoS-to-DMG relationship; validation must not resolve it to the XDMG reprint by matching the name. One known record benefits directly (Redface); the same pattern should not be generalized to records without explicit effect-reuse wording.

**Dependencies.** Depends on source-correct DMG Sight Rot and DIS-3 effect definitions. Not required for DIS-1 through DIS-5.

## Source-fidelity corrections to review separately

These findings concern current canonical content and are not a request to change it in this investigation. If approved for correction, make the smallest source-backed data/accounting/validator update; no new schema field is necessarily required for several entries.

| Canonical record | Finding against source | Classification |
|---|---|---|
| `disease:toa:blue-mist-fever` | `$.disease[2].entries[0..1]` says contact with mist, not inhalation. Its daily save ends the effect; verify that canonical Disease recovery does not overstate full infection termination. | Data interpretation correction / possibly DIS-3 symptom lifecycle |
| `disease:gos:bluerot` | `$.disease[3].entries[1]` places DC 12 on repeated recovery saves; entry 0 gives no infection save. Current `infection.savingThrow` incorrectly relocates that DC. | Data interpretation correction; DIS-1 needed for recovery outcome |
| `disease:xdmg:cackle-fever` | `$.disease[5]` specifies a damage-other-than-Psychic trigger while Exhausted, not DMG great stress; it also has tainted potion ingestion, automatic success after one minute, a three-success Long Rest threshold, and 1-year immunity. | Source/edition conflation plus DIS-1/2/3/4 gaps |
| `disease:dmg:sight-rot` | `$.disease[21].entries[1..2]` has Long Rest worsening and Eyebright ointment recovery, not a Constitution recovery save. | Unsupported canonical recovery field; DIS-1/3 |
| `disease:xdmg:sight-rot` | `$.disease[22].entries[1].entries[0].entries[0]` names Heal and Lesser Restoration as XPHB spells; canonical references identify PHB spells. | Source identity correction; not a schema gap |
| `disease:fraif:shaking-plague` | Source `$.disease[19].entries[1].items[0..2].entry` uses XPHB Exhaustion. Canonical uses PHB Exhaustion, and `cannotReduceUntilDiseaseEnds` contradicts successful dawn saves reducing levels before disease ends. | Source edition / incorrect existing restriction; daily trigger already supported |
| `disease:fraif:the-rusting` | Source `$.disease[27].entries[2].items[0].entry` uses XPHB Exhaustion and only prohibits Long Rest reduction while cursed; its dawn save can reduce levels before disease ends. Canonical PHB references and global cannot-reduce flag are wrong. | Source edition / incorrect existing restriction |
| `disease:cm:saprophytic-plague` | Source `$.disease[15].entries[0..1]` supports Beast/Humanoid applicability; canonical omits it. `cannotReduceUntilDiseaseEnds` is not stated, and a one-time level change does not represent automatic +1 every 24 hours. | Existing-schema applicability omission plus DIS-1/3/4 |
| `disease:fraif:mudpox` | Source `$.disease[13].entries[0]` supports Beast/Humanoid applicability; `entries[1].items[0].entry` is a daily-at-dawn branch, which the current schema already supports. | Existing-schema conversion omission; DIS-2 only for consecutive counter, DIS-4 for spread |
| `disease:idrotf:arcane-blight` | Source `$.disease[0].entries[2]` says DC 0 recovery grants immunity to further exposure; Disease v1 already has `recovery.immunityAfterRecovery`. Current output omits that supported field. | Existing-schema conversion omission |

## Cross-domain ownership and dependencies

- **Condition:** Disease applies or changes Conditions but should not duplicate Condition meanings. PHB Petrified suspension is a Condition effect on existing Disease state.
- **Action:** Action-taking constraints (e.g. antidote use, escape attempts) should be typed in Disease as prerequisites/outcomes only where the Disease contract needs them, then refer to the Action operation. Do not embed general Action execution.
- **Rule:** Attack/save/damage procedures and Madness outcomes may reference canonical Rules when exact identity exists. A Disease's trigger/threshold and progression remain owned by Disease.
- **Status:** Disease-caused Exhaustion level changes are Condition references. Disease progression owns when changes occur; Status defines what Exhaustion levels do. Do not copy level tables into Disease.
- **Sense:** Sight impairment can reference or apply a Condition; do not duplicate Darkvision/Blinded perception mechanics into Disease.
- **Spell/Item/Creature/Encounter:** Spell cures, Confusion, Herbalism Kit ointment, antidote items, creature transformations, region conditions, party defeat, and creature traits are external dependencies. Resolve only exact, source-established canonical identities. A Disease contract must be able to represent its own relationship to a procedure without embedding that entire future domain.

## Conservative coverage forecast

Starting coverage is **0 fully represented, 27 partial, 2 unsupported**. The forecast assumes DIS-1 through DIS-6 are approved and implemented, existing canonical source-fidelity corrections are separately authorized, and all 29 records are reinterpreted. It does not count improved schema expressiveness as automatic conversion.

| Forecast category | Records | Reason |
|---|---|---|
| Likely to become fully representable | PHB Blinding Sickness, PHB Filth Fever, PHB Flesh Rot, PHB Seizure, PHB Slimy Doom, DMG Sewer Plague, XDMG Sewer Plague, ToA Throat Leeches | Their listed mechanical procedures are bounded: basic roll modifiers/Condition effects, a damage-triggered Condition, or source-specific Long Rest progression. Blinding Sickness and XDMG Sewer Plague already have their substantive mechanics represented; final classification still requires entry-accounting review. |
| Likely to remain partial | Arcane Blight, Blue Mist Fever, Bluerot, both Cackle Fever records, Frigid Woe, Ghoul Gut, Lichen Plague, Mindfire, Mudpox, Redface, Saprophytic Plague, Shaking Plague, Shivering Sickness, both Sight Rot records, The Gnawing Plague, The Rusting, Spider Eggs | They combine mechanics from more than one proposal, unresolved counters, setting/creature/item/spell dependencies, event-specific outcomes, or transformation/effect reuse requiring separate source targets. Some may become fully representable after additional cross-domain contracts, but that is not assumed here. |
| Remain unsupported | Super-Tetanus; Grackle-Lung until DIS-1 and required bounded checks/exposure are approved and modeled | Super-Tetanus still lacks save ability/DC/failure outcomes. Grackle-Lung could move to partial with Long Rest progression and typed constraints, but the forecast does not mark it canonical until the source's exposure and all linked checks are represented. |

This conservative forecast is **8 likely full, 19 partial, 2 unsupported** after the proposed Disease changes. It is a planning estimate, not a coverage claim. The likely-full set includes the two records whose source mechanics are already substantially represented; the remaining six require the corresponding proposal and subsequent record migration. If human review decides that Redface requires broader source effects, or that Disease cannot own the listed external dependencies, the full count should be lower.

## Validation requirements

Structural tests should compile all Draft 2020-12 schemas, validate examples and migrated records, resolve typed Disease/Condition/Spell/Rule/Creature references, reject cyclic effect-profile reuse, bind counters to existing save branches, and reject incompatible trigger/outcome combinations. Preserve old Phase 2/3/4 fixtures while adding targeted Disease fixtures.

Source-backed semantic assertions should verify at least:

- Long Rest versus dawn, elapsed-time, and turn-end timing for the named diseases.
- Infection save and recovery save are not interchanged (Bluerot).
- Outcome counters count only the specified save family; DMG Cackle ambiguity remains unresolved; Lichen is not consecutive; Mudpox is consecutive; XDMG Cackle has a success threshold and one-year immunity.
- XDMG Cackle's non-Psychic-damage trigger and infected-source emanation differ from the DMG great-stress/mad-laughter trigger.
- Exposure event, material, target eligibility, and source-relative/global immunity match each source.
- PHB versus XPHB Exhaustion references and progression values remain separate.
- DMG Sight Rot uses ointment and progression, not an invented recovery save; XDMG cures reference XPHB spells.
- Petrified suspension remains distinct from immunity, cure, and termination, without asserting unsourced clock behavior.
- Redface reuses only source-supported DMG Sight Rot components and cannot resolve through an XDMG reprint by name similarity.
- Every root/nested entry remains accounted for and complete omitted/duplicate source content remains in the registers.

Schema validation proves structural shape only. These source-specific assertions, direct review, and human approval are required to establish mechanical fidelity.

## Human decisions required

1. Approve or revise DIS-1 through DIS-6 independently before any implementation.
2. Decide how a source-unspecified counter reset should remain represented and whether it may be executed; preserve the approved DMG Cackle Fever ambiguity.
3. Decide whether disease immunity following recovery can carry a duration without changing existing Boolean semantics.
4. Confirm the spell, item, creature-family, and encounter ownership boundaries for effects that span future domains.
5. Review the source-fidelity correction table and authorize a separate correction pass if accepted.
6. Define how nonmechanical omissions affect the “fully represented” record classification when all mechanical clauses are represented.

No schemas, canonical records, fixtures, manifests, validators, or other domain implementations were modified for this investigation. Phase 5 remains in progress; Phase 6 has not started.
