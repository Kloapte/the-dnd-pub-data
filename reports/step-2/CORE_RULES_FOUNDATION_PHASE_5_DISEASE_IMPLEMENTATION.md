# Step 2 Phase 5 Disease Contract Refinement and Implementation

## Status

Disease contract refinement and the authorized Disease catalog reinterpretation are implemented for human review. Phase 5 remains in progress and is not approved. Action, Language, and Rule expansion has not resumed; Phase 6 has not started.

## Summary

Implemented DIS-1 through DIS-6 as bounded Disease and Condition structures, migrated the 25 representable records in the Phase 5 Disease batch, retained the three Phase 3/4 Disease representatives, and left Super-Tetanus unsupported. The completed Disease inventory covers all 29 PHB/XPHB and supplemental Disease source records.

Across the 29 records, 25 are fully represented, 3 are partial, and 1 is unsupported. There are 28 canonical Disease records. The partial records retain all supported mechanics and document the remaining source portions. No Disease record was assigned a guessed save, default counter reset, or invented canonical target.

## Implemented contract changes

### DIS-1 — Long Rest and elapsed-time progression

The Disease schema extends existing progression branches with typed Long Rest completion, elapsed-time, repeating elapsed-time, symptom-onset, damage/Condition, and source event triggers. Long Rest is distinct from dawn. Success and failure outcomes remain branch-local, with condition-level and DC-track termination. The same Long Rest save is represented once: Ghoul Gut's recovery save now exists only on its progression branch.

### DIS-2 — Saving-throw outcome counters

Outcome counters attach to one progression or recovery procedure and record counted outcome, threshold, sequence, reset, threshold outcome, and post-recovery immunity. The validator checks that references resolve and prevents a source-unspecified sequence from acquiring an executable reset policy.

- DMG Cackle Fever counts three failures on its Long Rest recovery procedure; sequence and reset remain `UNSPECIFIED_BY_SOURCE`. The indefinite-madness result is attached only to that counter.
- XDMG Cackle Fever retains a separate three-success Long Rest recovery threshold and one-year Disease immunity. Its unspecific reset remains explicit.
- Lichen Plague preserves three successes that need not be consecutive and permanent Disease immunity.
- Mudpox preserves three consecutive dawn successes and resets on the opposite outcome.
- Arcane Blight retains the separate three-failure transformation counter and DC-zero cure/immunity behavior.

No stress, contagion, or symptom-ending save is counted in DMG Cackle Fever's recovery counter.

### DIS-3 — Bounded Disease effects

The typed Disease effect and outcome unions now cover observed attack/check modifiers, ability-score loss and recovery, damage vulnerability, speed changes, temporary Conditions, recovery restrictions, physical capabilities, timed behavior, Condition thresholds, transformations, and external procedure references. Recovery supports a bounded skill-check procedure and source-described disease-curing magic that removes only Exhaustion levels attributable to that Disease. This supports the Ghoul Gut Medicine care procedure and Grackle-Lung cure without duplicating the Disease's Long Rest save.

### DIS-4 — Exposure and transmission

Exposure structures distinguish material ingestion/contact, wounds by a carrier, contact with an infected creature, proximity, environmental exposure, Long Rest in a qualifying area, and qualifying damage followed by encounter end. They retain source creatures, target groups, exclusions, saving throws, immunities and their scope/duration, and separate infection routes. The Gnawing Plague's carrier list is an explicit enum; disease-immune automatic success and the Cackle Fever / Mudpox / Sight Rot source-relative immunity cases are represented distinctly.

### DIS-5 — PHB Petrified suspension

The Condition schema now permits `SUSPEND_ACTIVE_DISEASES`. PHB Petrified declares suspension while the Condition is active and preservation of the Disease instance. Scheduling and resumption timing remain explicitly unresolved. XPHB Petrified is unchanged; suspension is not represented as immunity or cure.

### DIS-6 — Disease effect-profile reuse

Redface references exactly `disease:dmg:sight-rot` and reuses only the explicitly identified symptoms, progression, and recovery components. Its local inhalation route and source identity remain separate. No XDMG reprint substitution or metadata inheritance is introduced. The source-supported component scope is represented without general object inheritance.

## Source-fidelity corrections and record migrations

The following ten approved corrections are implemented:

1. **Blue Mist Fever** uses contact with magical mist. Daily saves end the hallucination symptom only; ending or dispersing the local mist is not modeled as curing an infected creature.
2. **Bluerot** has no invented infection save. DC 12 belongs to its Long Rest recovery procedure; ability-score loss/restoration, radiant vulnerability, underwater breathing, and the zero-HP restriction are typed.
3. **XDMG Cackle Fever** uses the source-defined non-Psychic damage trigger while XPHB Exhaustion is active; the DMG great-stress trigger remains edition-specific to the DMG record.
4. **DMG Sight Rot** has no invented recovery save. Long Rest worsening, the sight-dependent penalty, Blinded threshold, and Eyebright preparation/application are represented.
5. **XDMG Sight Rot** references `spell:xphb:heal` and `spell:xphb:lesser-restoration`.
6. **Shaking Plague** uses XPHB Exhaustion in every progression/termination reference and limits its Long Rest restriction to the source-defined level reduction.
7. **The Rusting** uses XPHB Exhaustion consistently, limits the Long Rest restriction, and represents Rusted immunities as typed poison-damage and XPHB Condition references.
8. **Saprophytic Plague** applies to Beasts and Humanoids, models automatic success for Disease-immune creatures, the infection-relative 24-hour behavioral effects, food contamination, and supported progression; no unsupported level-reduction ban is added.
9. **Mudpox** applies to Beasts and Humanoids, includes daily-at-dawn outcomes, food vomiting, the consecutive-success threshold, and source-relative 24-hour immunity on its secondary route.
10. **Arcane Blight** grants Disease immunity after the DC-zero recovery outcome.

Additional source checks corrected Shivering Sickness to halve hit points gained from spent Hit Dice and prevent hit points from Long Rests only after symptom onset; it no longer prohibits Hit Dice recovery. Ghoul Gut's duplicate Long Rest save was removed, and its distinct DC 15 Wisdom (Medicine) check during Long Rest care is represented as a typed skill-check recovery, including the caregiver's lost rest benefit.

## Canonical coverage of all 29 Disease records

| Canonical identity | Source locator | Result |
|---|---|---|
| `disease:phb:blinding-sickness` | `$.disease[1]` | Fully represented; Phase 3 representative |
| `disease:xdmg:sewer-plague` | `$.disease[18]` | Fully represented; Phase 3 representative |
| `disease:dmg:cackle-fever` | `$.disease[4]` | Fully represented; Phase 3 representative; counter ambiguity retained |
| `disease:idrotf:arcane-blight` | `$.disease[0]` | Partial: character flaw dependency and symptom prerequisite for transformation |
| `disease:toa:blue-mist-fever` | `$.disease[2]` | Partial: environmental mist dispersal/area geometry is outside Disease scope |
| `disease:gos:bluerot` | `$.disease[3]` | Fully represented |
| `disease:xdmg:cackle-fever` | `$.disease[5]` | Fully represented |
| `disease:phb:filth-fever` | `$.disease[6]` | Fully represented |
| `disease:phb:flesh-rot` | `$.disease[7]` | Fully represented |
| `disease:egw:frigid-woe` | `$.disease[8]` | Fully represented |
| `disease:oow:ghoul-gut` | `$.disease[9]` | Fully represented |
| `disease:oota:grackle-lung` | `$.disease[10]` | Fully represented |
| `disease:fraif:lichen-plague` | `$.disease[11]` | Fully represented |
| `disease:phb:mindfire` | `$.disease[12]` | Fully represented; Confusion remains an external referenced procedure |
| `disease:fraif:mudpox` | `$.disease[13]` | Fully represented |
| `disease:gos:redface` | `$.disease[14]` | Fully represented; references DMG Sight Rot effect profile |
| `disease:cm:saprophytic-plague` | `$.disease[15]` | Fully represented |
| `disease:phb:seizure` | `$.disease[16]` | Fully represented |
| `disease:dmg:sewer-plague` | `$.disease[17]` | Fully represented |
| `disease:fraif:shaking-plague` | `$.disease[19]` | Fully represented |
| `disease:toa:shivering-sickness` | `$.disease[20]` | Fully represented |
| `disease:dmg:sight-rot` | `$.disease[21]` | Fully represented |
| `disease:xdmg:sight-rot` | `$.disease[22]` | Fully represented |
| `disease:phb:slimy-doom` | `$.disease[23]` | Fully represented |
| `disease:wdmm:spider-eggs` | `$.disease[24]` | Fully represented |
| `disease:tftyp:super-tetanus` | `$.disease[25]` | Unsupported; no canonical shell |
| `disease:vrgr:the-gnawing-plague` | `$.disease[26]` | Partial: regional Cycle of the Plague DC schedule is an external dependency |
| `disease:fraif:the-rusting` | `$.disease[27]` | Fully represented |
| `disease:toa:throat-leeches` | `$.disease[28]` | Fully represented |

## Remaining limitations and dependencies

- **Arcane Blight:** the source assigns and later removes a player-character flaw. This belongs to a future character/flaw contract. The source also conditions transformation on hallucinations and isolation/paranoia; the current counter can express the three failures but not the symptom prerequisite/order. Both portions are recorded outside canonical JSON.
- **Blue Mist Fever:** Dispel Magic destroys a 20-foot square of the environmental mist; typical encounter scale is 1d6 areas. Disease conversion does not model area-hazard geometry, and ending exposure is not equated with curing infection.
- **The Gnawing Plague:** the source points to the separate Cycle of the Plague severity procedure for a variable infection/recovery DC. The record retains that the DC varies with spread severity but does not invent a schedule.
- **Super-Tetanus:** remains unsupported. The source states recurring damage and a repeat save but omits the save ability, DC, and outcome relationship needed for reliable typed progression.
- **Cackle Fever:** DMG three-failure counter consecutiveness/reset semantics remain unresolved. XDMG counter reset semantics are also source-unspecified. Neither is assigned an execution default.
- **PHB Petrified:** timer advancement, scheduled saves, symptoms, recovery opportunities, and exact resumption scheduling remain unresolved by source.
- **External procedures:** Spell, Skill, encounter, and transformed-creature execution remains reference-level and does not add runtime behavior.

## Source accounting

The Phase 5 accounting manifest now records 26 non-representative Disease source records, 25 canonical outputs, 22 fully represented, 3 partial, and 1 unsupported; these contain 73 source-entry roots. Their classifications total 72 interpreted, 0 duplicate, and 68 omitted portions. Complete original omitted and duplicate entries are regenerated in the Disease registers.

Including the three Phase 3 representatives, the full 29-record Disease catalog has 83 source-entry roots: 25 fully represented, 3 partial, and 1 unsupported. The combined Disease disposition totals are 83 interpreted, 1 duplicate, and 72 omitted classification parts. Omitted parts are limited to the three partial records, Super-Tetanus, and descriptive/nonmechanical content. The Cackle Fever ambiguity remains represented as source-unspecified canonical data rather than an omission.

The overall Phase 5 accounting manifest reports 98 canonical records, 88 fully represented, 10 partial, 1 unsupported, 164 entry roots, and 126 interpreted / 18 duplicate / 123 omitted classification parts. The PHB Petrified entry accounting now classifies Disease suspension as interpreted.

## Fixtures and documentation

- Updated `schemas/core-rules/v1/examples/disease-cackle-fever.json` to match the approved DMG counter interpretation.
- Added `disease-sewer-plague-dmg.json` to demonstrate the Long Rest procedure separately from XDMG dawn progression.
- Added `condition-petrified-phb.json` to demonstrate active-Disease suspension.
- Updated `doc/CORE_RULES.md`, Disease omission/duplicate registers, `doc/INDEX.md`, and the Phase 5 roadmap status.
- Updated the Phase 5 dataset expansion report and accounting manifest; the separate Disease contract review remains historical.

## Validation

`npm run validate:core-rules` result:

- 9 Draft 2020-12 schemas compiled.
- 39 schema examples validated.
- 34 Phase 3 canonical records validated.
- 98 Phase 5 canonical records validated.
- 231 permanent checks passed, including source-backed Disease assertions and a negative unspecified-counter-reset check.
- Rule components checked: 14. Action records checked: 12.

These results establish structural validity and deterministic integrity. The source comparisons and record dispositions above are Codex's semantic interpretation and remain subject to human review; schema validation alone does not establish mechanical correctness.

`git diff --check` is recorded after the final implementation review.

