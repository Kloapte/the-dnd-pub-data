# Core Rules Foundation Phase 5 Condition Contract Refinement and Implementation

## Result

The approved Condition review proposals CND-1 through CND-8 are implemented as bounded Condition mechanics. All 30 PHB/XPHB Condition source records now have canonical records: 29 are fully represented and PHB Petrified remains partial because Disease suspension has no supported suspension/resumption lifecycle. Phase 5 dataset expansion remains in progress; this task did not resume expansion in another domain or begin Phase 6.

The Phase 2 Condition fixture and the Phase 3/4 XPHB Blinded canonical record now point outgoing attack Disadvantage at the attack's target. The Poisoned records and their `CHECK_ROLL_MODIFIER` representation were left unchanged.

## Implemented capabilities

| Approved proposal | Contract change and source-backed use |
| --- | --- |
| CND-1 Activity restrictions | `ACTIVITY_RESTRICTION` identifies the actor and separately lists Action, Bonus Action, Reaction, speech, or movement restrictions while the Condition applies. PHB Incapacitated restricts Actions and Reactions; XPHB also restricts Bonus Actions and speech. XPHB Initiative Disadvantage is a roll modifier. Concentration termination remains in Status. |
| CND-2 Dynamic roles and targeting | Roles such as `charmer`, `fear-source`, `observer`, `effect-creator`, `attacker`, and `grappler` identify relationships without invented creature IDs. `RELATIONAL_RESTRICTION` distinguishes attack, PHB harmful ability/magical effect, XPHB damaging ability/magical effect, and sight-required targeting. |
| CND-3 Relational applicability | Typed applicability expresses active-Condition scope, fear-source line of sight, charmer social interaction, observer visibility/nonvisibility, creature distance, and an attack target's identity relative to a role. No general expression tree was added. |
| CND-4 Automatic save failure | `AUTOMATIC_SAVING_THROW_FAILURE` limits the affected abilities to the source-supported Strength and Dexterity saving throws. Existing sensory `AUTOMATIC_CHECK_FAILURE` remains specifically an ability-check mechanic. |
| CND-5 Condition interaction and persistence | `CONDITION_INTERACTION` distinguishes application from immunity and requires lifecycle behavior. Paralyzed, Petrified, Stunned, and Unconscious apply Incapacitated while active. Unconscious applies Prone; XPHB explicitly retains Prone when Unconscious ends. PHB Petrified's category immunity is not treated as suspension. |
| CND-6 Movement and ending | `MOVEMENT_CONSTRAINT` covers speed-increase suppression, crawling, movement prohibition, fear-relative movement, and the Speed-zero standing restriction. `CONDITION_ENDING` models Grappled ending when its grappler is Incapacitated or the target leaves reach, and Prone ending when the creature stands. XPHB Grappled assigns its extra movement cost to the grappler, with Tiny/two-size waiver. PHB's half-Speed voluntary drag/carry remains represented by the existing Action Grapple operation rather than being duplicated in Condition. |
| CND-7 Critical Hit | `CRITICAL_HIT_ON_HIT` requires a successful hit against the affected creature and an attacker within five feet before yielding a Critical Hit result. XPHB effects reference the existing XPHB Critical Hit Rule. Its `ON_CRITICAL_HIT` damage modifier remains responsible for the damage resolution. The Rule schema already supports that result-to-damage path, so no Rule schema change was necessary. PHB condition outcomes do not receive a fabricated PHB Rule reference. |
| CND-8 Physical state | `PHYSICAL_STATE`, `DAMAGE_MITIGATION`, `CATEGORY_IMMUNITY`, `AWARENESS_RESTRICTION`, and `DROP_HELD_OBJECTS` cover the approved source facts. PHB Petrified includes inanimate transformation, nonmagical worn/carried object transformation, tenfold weight, ceased aging, unawareness, damage resistance, and poison/Disease immunity. XPHB retains its own physical-state and Poisoned immunity wording without an unsupported unawareness effect. |

The Condition v1 schema is the only domain schema changed. Shared definitions and Rule schema remain unchanged. Critical-hit resolution uses the existing Rule contract. Source metadata, edition-specific IDs, and other approved-domain semantics remain intact.

## Condition record classifications

The original source file is `src/main/resources/data/conditionsdiseases.json`. PHB entries are at `$.condition[n].entries[0]`; for XPHB records, the mechanical entry is at `$.condition[n].entries[1]` (with the introductory heading in `entries[0]`). The table covers every one of the 30 source records; `full` means all source mechanics relevant to the approved contract are represented or explicitly accounted as duplicate. The remaining PHB Petrified disease-suspension mechanic is the only partial mechanical record.

| Source identity | Source locator | Classification | Canonical output |
| --- | --- | --- | --- |
| `condition:phb:blinded` | `$.condition[0]` | Full | `datasets/phase-3/step-2/core-rules/v1/condition/phb-blinded.json` |
| `condition:xphb:blinded` | `$.condition[1]` | Full; outgoing attack target corrected | `datasets/phase-3/step-2/core-rules/v1/condition/xphb-blinded.json` |
| `condition:phb:charmed` | `$.condition[2]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-charmed.json` |
| `condition:xphb:charmed` | `$.condition[3]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-charmed.json` |
| `condition:phb:deafened` | `$.condition[4]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-deafened.json` |
| `condition:xphb:deafened` | `$.condition[5]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-deafened.json` |
| `condition:phb:exhaustion` | `$.condition[6]` | Full; unchanged | `datasets/phase-3/step-2/core-rules/v1/condition/phb-exhaustion.json` |
| `condition:xphb:exhaustion` | `$.condition[7]` | Full; unchanged | `datasets/phase-3/step-2/core-rules/v1/condition/xphb-exhaustion.json` |
| `condition:phb:frightened` | `$.condition[8]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-frightened.json` |
| `condition:xphb:frightened` | `$.condition[9]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-frightened.json` |
| `condition:phb:grappled` | `$.condition[10]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-grappled.json` |
| `condition:xphb:grappled` | `$.condition[11]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-grappled.json` |
| `condition:phb:incapacitated` | `$.condition[12]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-incapacitated.json` |
| `condition:xphb:incapacitated` | `$.condition[13]` | Full; Concentration is accounted by Status | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-incapacitated.json` |
| `condition:phb:invisible` | `$.condition[14]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-invisible.json` |
| `condition:xphb:invisible` | `$.condition[15]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-invisible.json` |
| `condition:phb:paralyzed` | `$.condition[16]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-paralyzed.json` |
| `condition:xphb:paralyzed` | `$.condition[17]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-paralyzed.json` |
| `condition:phb:petrified` | `$.condition[18]` | Partial; existing poison/Disease suspension awaits Disease lifecycle support | `datasets/phase-5/step-2/core-rules/v1/condition/phb-petrified.json` |
| `condition:xphb:petrified` | `$.condition[19]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-petrified.json` |
| `condition:phb:poisoned` | `$.condition[20]` | Full; canonical representation unchanged | `datasets/phase-5/step-2/core-rules/v1/condition/phb-poisoned.json` |
| `condition:xphb:poisoned` | `$.condition[21]` | Full; canonical representation unchanged | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-poisoned.json` |
| `condition:phb:prone` | `$.condition[22]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-prone.json` |
| `condition:xphb:prone` | `$.condition[23]` | Full; half-Speed standing cost and Speed-zero prohibition | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-prone.json` |
| `condition:phb:restrained` | `$.condition[24]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-restrained.json` |
| `condition:xphb:restrained` | `$.condition[25]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-restrained.json` |
| `condition:phb:stunned` | `$.condition[26]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-stunned.json` |
| `condition:xphb:stunned` | `$.condition[27]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-stunned.json` |
| `condition:phb:unconscious` | `$.condition[28]` | Full | `datasets/phase-5/step-2/core-rules/v1/condition/phb-unconscious.json` |
| `condition:xphb:unconscious` | `$.condition[29]` | Full; Prone persists after Unconscious ends | `datasets/phase-5/step-2/core-rules/v1/condition/xphb-unconscious.json` |

## Contract examples

An active Condition can restrict different activities without implying unmentioned restrictions:

```json
{
  "kind": "ACTIVITY_RESTRICTION",
  "actor": "affected-creature",
  "activities": [
    "action",
    "reaction"
  ],
  "appliesWhen": {
    "kind": "WHILE_CONDITION_ACTIVE"
  }
}
```

The critical-hit result is gated by a successful hit and distance; the Condition does not convert a miss:

```json
{
  "kind": "CRITICAL_HIT_ON_HIT",
  "attacker": "attacker",
  "target": "affected-creature",
  "distanceFeet": 5,
  "requiresSuccessfulHit": true,
  "result": "critical-hit",
  "rule": {
    "targetType": "rule",
    "id": "rule:xphb:critical-hit"
  }
}
```

XPHB Unconscious applies Prone with an independent lifecycle:

```json
{
  "kind": "CONDITION_INTERACTION",
  "condition": {
    "targetType": "condition",
    "id": "condition:xphb:prone"
  },
  "interaction": "apply",
  "lifecycle": "persists-after-source-condition-ends"
}
```

## Source accounting and limitations

For the 26 Phase 5 Condition records, all 39 source-entry roots are accounted for in 54 classification parts: 39 INTERPRETED, 1 DUPLICATE, and 14 OMITTED. The omissions are the XPHB introductory headings plus the PHB Petrified existing-poison/Disease suspension mechanic. The duplicate is XPHB Incapacitated's Concentration consequence, already represented by the approved Concentration Status. The 4 Phase 3 Condition records account for 11 additional roots in 11 parts: 6 INTERPRETED, 2 DUPLICATE, and 3 OMITTED. The complete original entries remain in `doc/condition/omitted.md` and `doc/condition/duplicate.md` as applicable.

PHB Petrified immunity and suspension are different behaviors. `CATEGORY_IMMUNITY` represents immunity to poison and Disease while Petrified; the canonical record does not claim that an existing Disease is paused. Supporting suspension would require a reviewed Disease lifecycle that defines suspension and resumption without inventing progression, save cadence, or recovery behavior. That remains a cross-domain limitation, recorded as `EXTERNAL_DEPENDENCY`.

The XPHB Invisible rule about effects that require the target to be seen is typed in Condition. Spell-targeting execution remains an external Spell-domain dependency. Initiative modifiers are represented as Condition mechanics; encounter execution remains external. PHB Invisible uses the source's magic/special-sense wording and the source-described noise/track location detection; it does not name any concrete Sense entity.

## Validation

The permanent workflow validates the schemas, Phase 2 fixtures, Phase 3/4 records, and Phase 5 records; the Condition assertions cover the source-supported roles, edition differences, action restrictions, automatic save failures, condition persistence, movement costs, critical-hit hit prerequisite/range, and physical-state behavior. Negative schema checks reject an unsupported automatic-save ability and a non-source critical-hit distance. The XPHB Blinded assertion verifies outgoing Disadvantage is on attacks made by the affected creature against its target.

Latest executed `npm run validate:core-rules` result: 9 Draft 2020-12 schemas compiled; 37 examples validated; 34 Phase 3/4 canonical records validated; 73 Phase 5 canonical records validated; 203 permanent checks passed. `git diff --check` completed without whitespace errors; Git emitted line-ending normalization warnings for CRLF-tracked files. These structural and selected source-backed assertions do not prove all mechanics correct.

## Remaining review points

- PHB Petrified's suspension of existing poison/Disease requires Disease lifecycle design approval before that mechanic can be represented.
- PHB Paralyzed and Unconscious declare the Critical Hit result, but the current canonical baseline has no PHB Critical Hit Rule record to reference. Their result remains typed without an invented Rule identity; XPHB records reference the canonical XPHB Critical Hit Rule.
- Condition's typed restrictions and Condition interactions express rules but do not implement runtime Action or Spell targeting, encounter initiative, equipment inventory transitions, or Disease progression.
- No Rule schema change was made: the approved Critical Hit Rule already applies its damage modifier on a critical-hit result, and the new Condition outcome yields that result only after a hit. Human review should confirm that this integration boundary is sufficient.
- The report and Phase 5 manifest are updated, but Phase 5 remains in progress and awaits separate review; Phase 6 has not started.
