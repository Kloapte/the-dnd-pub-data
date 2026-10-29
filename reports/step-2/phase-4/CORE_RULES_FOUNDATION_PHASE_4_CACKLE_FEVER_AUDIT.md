# Phase 4 Cackle Fever Source-Fidelity Audit

## Scope and source

This audit checks the selected 2014 DMG Cackle Fever record at `src/main/resources/data/conditionsdiseases.json $.disease[4]`, including all five source entries, against `datasets/phase-3/step-2/core-rules/v1/disease/dmg-cackle-fever.json`, the approved Disease schema, the Phase 4 implementation report, the source-accounting manifest, and the permanent validator's Cackle assertions.

The record has five plain-string source entries and no nested structured entry objects. The canonical Disease keeps the source-qualified `disease:dmg:cackle-fever` identity and its typed reprint reference. The source-accounting manifest assigns all five source-entry roots and contains no omitted mechanical portion for Cackle Fever.

## Verdicts

| Mechanic | Source locator and source-supported behavior | Current canonical representation | Verdict | Required correction |
| --- | --- | --- | --- | --- |
| Applicability and onset | `$.disease[4].entries[0–1]`: Humanoids are targets; gnomes are immune. Symptoms manifest 1d4 hours after infection. | `applicability.targetCreatureTypes: [humanoid]`, excluded `race:phb:gnome`, and `incubation: 1d4 hours`. | `CORRECT` | None. |
| Exhaustion restriction | `$.disease[4].entries[1]`: infection adds one Exhaustion level; it cannot be removed until the disease is cured. | `CONDITION_LEVEL_CHANGE` references PHB Exhaustion, amount 1, `cannotReduceUntilDiseaseEnds: true`. | `CORRECT` | None. |
| Stress trigger and save | `$.disease[4].entries[2]`: an event causing great stress (with combat, damage, fear, and nightmares as examples) triggers a DC 13 Constitution save. | `GREAT_STRESS` branch, DC 13 Constitution; the listed examples are retained. | `CORRECT` | None. |
| Failed stress save | `$.disease[4].entries[3]`: failure deals 5 (1d10) psychic damage and starts one minute of Incapacitated mad laughter. The source's printed 5 is the average of 1d10; canonical damage records the die. | Failure outcomes are 1d10 psychic damage and `START_SYMPTOM`; the temporary symptom applies PHB Incapacitated for one minute. | `CORRECT` | None. |
| End-of-turn saves | `$.disease[4].entries[3]`: the infected creature can repeat the stress save at the end of each turn; success ends mad laughter and Incapacitated. | The temporary symptom has a Constitution save at end of each turn, with `dcTrackId: cackle-fever-dc`. It repeats the stress-triggered save and references the same DC track. | `CORRECT` | None. |
| DC reduction | `$.disease[4].entries[4]`: after each Long Rest, a DC 13 Constitution save can reduce the DC for that recovery save and the save to avoid an attack of mad laughter by 1d6. The end-of-turn procedure explicitly repeats the stress save. | Long Rest recovery and stress-triggered/end-of-turn repeated saves share `cackle-fever-dc`; the recovery save has a 1d6 reduction. | `CORRECT` | None. |
| Recovery | `$.disease[4].entries[4]`: success on a Long Rest save reduces the linked DC; the creature recovers when the DC drops to 0. | Recovery is a Long Rest saving throw linked to the DC track and ends the disease at DC 0 or below. | `CORRECT` | None. |
| Three-failure consequence: save family and timing | `$.disease[4].entries[4]`: “these saving throws” refers to the immediately preceding Long Rest recovery saves; failure three times gives a randomly determined form of indefinite Madness. | `failureThreshold.count: 3` and the Madness Rule procedure are nested in the Long Rest recovery object. Stress, end-of-turn, and contagion saves do not carry this threshold. | `CORRECT` | None. |
| Three-failure consequence: consecutive/reset policy | `$.disease[4].entries[4]`: the source does not say that failures must be consecutive, nor does it say whether success resets or otherwise changes the failure count. | The Disease schema records a threshold of three failures on the Long Rest recovery procedure; it has no consecutive/reset field. | `AMBIGUOUS` | Do not infer a reset policy. Preserve this source ambiguity for any later mechanics execution decision. |
| Nearby infection event | `$.disease[4].entries[3]`: a Humanoid starting its turn within 10 feet of an infected creature actively in mad laughter must make a DC 10 Constitution save. Failure infects that Humanoid. | Secondary exposure requires the target to start its turn within 10 feet of the active `mad-laughter` symptom; it uses DC 10 Constitution and infects on failure. Disease applicability limits recipients to Humanoids and excludes gnomes. | `CORRECT` | None. |
| Contagion-save success and immunity | `$.disease[4].entries[3]`: success on the nearby infection save grants immunity to the mad laughter of that particular infected creature for 24 hours. It does not grant immunity to Cackle Fever. | The exposure's success outcome is `SOURCE_RELATIVE_IMMUNITY`, scoped to `mad-laughter`, explicitly relative to the `exposing-creature`, for 24 hours. It is separate from the failure outcome that infects the target. | `CORRECT` | None. |
| Madness application is not caused by unrelated failures | `$.disease[4].entries[4]`: the third failure language occurs in the Long Rest recovery paragraph. | The threshold and its `RULE_PROCEDURE_OUTCOME` are attached only to the Long Rest recovery procedure. | `CORRECT` | None. |

## Recovery counter interpretation

Only failed Long Rest recovery saves are in scope for the three-failure consequence. The surrounding source paragraph identifies those saves, and the canonical `failureThreshold` is nested under that recovery procedure. The stress-triggered save and its repeated end-of-turn rolls use the shared DC track but are not recovery saves. The DC 10 nearby infection save is a separate exposure procedure. None of those other failures contributes to the three-failure threshold.

The source states a count of three failed Long Rest saves but does not define whether the failures must be consecutive or whether a successful Long Rest save resets the failure count. The canonical field preserves the stated threshold without specifying either policy. A later runtime design must not silently select one; the source ambiguity requires an explicit rules interpretation if executable counter behavior is needed.

## Validation and accounting

No schema, canonical record, or example correction was necessary. The canonical record's existing shared DC track is source-supported because the end-of-turn save repeats the stress-triggered saving throw; Long Rest success reduces the DC of that save procedure as well as the recovery save. The failure threshold is correctly scoped to the Long Rest recovery procedure.

The manifest accounts for all five entries at `$.disease[4].entries[0]` through `$.disease[4].entries[4]`. Its classifications distinguish represented mechanics, the mad-laughter wording repeated by the detailed symptom procedure, and descriptive material such as fever/disorientation without specified mechanics and the disease nickname. No additional Cackle-specific entry accounting changes were required.

Ran `npm run validate:core-rules` successfully:

- 9 Draft 2020-12 schemas compiled.
- 36 examples validated.
- 34 representative canonical records validated.
- 121 permanent checks passed.

`git diff --check` passed with Git line-ending normalization warnings only. Structural validation does not independently establish semantic correctness; the verdicts above come from direct comparison with the pinned source wording.

## Recommendation

The Cackle Fever canonical record is source-faithful for the mechanics represented. Recommend Phase 4 approval with the consecutive/reset question explicitly retained as a source ambiguity for future execution semantics. This audit does not approve Phase 4; final approval remains with the human reviewer. Phase 5 and Phase 6 have not started.

## Approval record reconciliation — 2026-10-29

The statement that Phase 5 and Phase 6 had not started is a historical checkpoint superseded by their later reports. The Phase 5 closure report records explicit approval of Phase 5 only. No explicit approval of the Phase 4 gate was found; this audit's recommendation remains a recommendation, and Phase 4 still requires explicit human approval. The Cackle Fever consecutive/reset ambiguity remains unresolved as documented.

## Approval reconciliation — 2026-10-29

The user's explicit Phase 4 approval is recorded in the Phase 4 implementation report and accepts this audit's documented Cackle Fever ambiguity. The consecutive/reset behavior remains unresolved and unchanged; the prior statement that Phase 4 still requires approval is superseded.
