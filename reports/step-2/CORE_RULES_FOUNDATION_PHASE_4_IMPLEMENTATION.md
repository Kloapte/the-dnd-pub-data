# Step 2 Core Rules Foundation — Phase 4 Contract Refinement and Implementation

## Outcome

The approved Phase 4 proposals C1–C8 and C11 were implemented in the v1 contracts, examples, selected Phase 3 canonical records, and permanent validation. C9 and C10 remain deferred as authorized. The six approved Phase 2 fixture corrections were applied. The representative selection remains frozen at 34 source records; no Phase 5 expansion or Phase 6 backend work was started.

Phase 4 is **awaiting human review**. This report does not approve the phase.

## Implemented contract changes

| Decision | Contract and behavior | Evidence and implementation |
| --- | --- | --- |
| C1, C11 — Disease lifecycle | `disease.schema.json` supports creature-type applicability and typed exclusions, qualified exposures, fixed or dice-rolled incubation, condition-level changes, condition-gated rest restrictions, repeated temporary-condition saves, saving-throw branches with separate success/failure outcomes, linked DC tracks, condition/DC termination thresholds, and failure-count outcomes. Disease records still require at least one extracted mechanic. | Sewer Plague `conditionsdiseases.json $.disease[18]` and Cackle Fever `$.disease[4]`; new representative fixtures `disease-sewer-plague.json` and `disease-cackle-fever.json`. These are bounded Disease procedures, not a general effects engine. |
| C2 — Grapple movement | Action adds `MOVE_WITH_GRAPPLED_TARGET`, with the Grappled Condition reference, voluntary drag/carry modes, half-Speed cost, and waiver when the target is at least two sizes smaller. It is separate from forced movement. | PHB Grapple `actions.json $.action[18].entries[2]`; migrated `action/phb-grapple.json`. |
| C3 — Unarmed Strike reach | Rule Unarmed Strike option components accept typed `reachFeet`; Grapple and Shove each state five-foot reach. Existing OR composition, choice-before-resolution convention, shared saving throw, and escape-DC reference remain. | XPHB Unarmed Strike `variantrules.json $.variantrule[225]`; migrated Phase 3 record and Phase 2 fixture. |
| C4 — Difficult Terrain | Movement mechanics accept typed qualifying-space predicates for creature presence and ally/size restrictions, furniture size, listed surface materials, liquid depth, narrow openings, slope threshold, and DM-adjudicated similar cases. Existing extra movement cost and non-cumulative behavior remain. | XPHB Difficult Terrain `variantrules.json $.variantrule[53]`; migrated `rule/xphb-difficult-terrain.json`. |
| C5 — Concentration optionality | PHB environmental maintenance saves can state `requirement: dm-may-require`; schema constraints limit this marker to environmental-phenomenon saves. Damage saves remain mandatory and PHB/XPHB procedures remain distinct. | PHB Status Concentration `conditionsdiseases.json $.status[1].entries[4]`; migrated Phase 3 record and Phase 2 fixture. Spell-specific termination remains a Spell dependency. |
| C6 — Darkvision | `SIGHT.dimLightAs` independently expresses dim light as bright light. Existing `darknessAs: dim-light` and grayscale perception in darkness are preserved. | PHB Darkvision `senses.json $.sense[2]`; migrated Phase 3 record and fixture. |
| C7 — Tremorsense | `NONVISUAL_PERCEPTION.locationPrecision` distinguishes `VIBRATION_ORIGIN` from `DETECTED_TARGETS`, retaining each edition's detectability and medium restrictions. | MM `senses.json $.sense[4]` and XPHB `$.sense[5]`; both Phase 3 records migrated. |
| C8 — Truesight | `SIGHT` can state invisible-object perception and typed illusion interactions for automatic detection, transparency, and automatic saving-throw success. Only PHB-evidenced interactions were added to the PHB record; no PHB transparency claim was inferred. | PHB Truesight `senses.json $.sense[6]`; migrated `sense/phb-truesight.json`. XPHB language remains edition-specific. |
| C9 — deferred | No change to PHB Surprised. Resolving Stealth against passive Perception remains encounter/initiative modeling. Existing accounting remains. | Approved deferral. |
| C10 — deferred | No change to Thieves' Cant communication time or general Language speaker predicates. | Approved deferral. |

### Before and after examples

Random incubation previously had no representation for a rolled interval. It is now explicitly typed:

```json
"incubation": {
  "kind": "DICE_ROLL",
  "count": 1,
  "die": 4,
  "unit": "day"
}
```

The prior Disease model could list a Condition symptom but not its level change or linked recovery restrictions. Sewer Plague now expresses its dawn branch and distinct outcomes:

```json
{
  "kind": "SAVING_THROW_BRANCH",
  "branchId": "daily-dawn-save",
  "trigger": {
    "kind": "DAILY_AT",
    "time": "DAWN"
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
        "id": "condition:xphb:exhaustion"
      },
      "amount": 1
    }
  ],
  "onSuccess": [
    {
      "kind": "CHANGE_CONDITION_LEVEL",
      "condition": {
        "targetType": "condition",
        "id": "condition:xphb:exhaustion"
      },
      "amount": -1
    }
  ],
  "endsWhen": {
    "kind": "CONDITION_LEVEL_AT_OR_BELOW",
    "condition": {
      "targetType": "condition",
      "id": "condition:xphb:exhaustion"
    },
    "level": 0
  }
}
```

The environmental PHB Concentration save is explicitly optional at DM discretion, while its damage save is not:

```json
{
  "kind": "MAINTENANCE_SAVE",
  "trigger": "environmental-phenomenon",
  "requirement": "dm-may-require"
}
```

## Disease design decisions

Sewer Plague preserves its Humanoid applicability, wound-by-contagion-carrier and contaminated filth/offal exposure, DC 11 Constitution infection save, 1d4-day symptom onset, one-level XPHB Exhaustion increase, Hit Point Dice and Long Rest restrictions while Exhaustion remains, daily dawn save, opposite Exhaustion changes on failure and success, and termination only at level zero.

Cackle Fever is now represented as a full mechanical Disease record rather than the former unsupported shell-free result. It expresses Humanoid applicability and PHB Gnome immunity; 1d4-hour onset; PHB Exhaustion that cannot be reduced before disease termination; great-stress DC 13 Constitution saves; failure damage and timed Incapacitated mad laughter; end-of-turn saves; nearby-Humanoid infection checks and immunity to that symptom from the particular source creature for 24 hours; Long Rest recovery saves with 1d6 DC reduction; recovery at DC 0; and the indefinite-madness Rule procedure after three failed recovery saves. In the 2014 DMG context, its unqualified Condition tags resolve to PHB Condition identities; this is recorded in the source-entry interpretation reasons and validated against pinned source records. Fever, disorientation, and the nickname are accounted as nonmechanical descriptions because the source gives them no separate mechanical outcomes. The XDMG reprint is not a second converted record; the frozen Phase 3 selection is unchanged.

The Disease schema uses local symptom, exposure, progression-branch, and DC-track identifiers where linked procedures require them. It does not claim that all disease progressions share one sequence. More unusual disease procedures remain subject to evidence and further review.

## Canonical record migrations and accounting

Changed Phase 3 records:

- Action: PHB Grapple.
- Rule: XPHB Unarmed Strike and Difficult Terrain.
- Status: PHB Concentration.
- Disease: XDMG Sewer Plague; PHB DMG Cackle Fever added as the previously selected but unsupported record.
- Sense: PHB Darkvision, MM Tremorsense, XPHB Tremorsense, and PHB Truesight.

No other representative identities were changed. Source metadata remains derived from the exact source objects. Typed Condition, Race, Rule, and reprint references resolve against pinned source data.

| Measure | Approved Phase 3 baseline | Phase 4 revised accounting |
| --- | ---: | ---: |
| Selected source records | 34 | 34 |
| Canonical records | 33 | 34 |
| Fully represented | 16 | 25 |
| Partially represented | 17 | 9 |
| Unsupported | 1 | 0 |
| Source-entry roots | 74 | 74 |
| INTERPRETED classification parts | 51 | 60 |
| DUPLICATE classification parts | 11 | 11 |
| OMITTED classification parts | 50 | 34 |
| Structured source-field/reference issues | 14 | 12 |

Disposition counts are classification parts, not source-entry roots; one entry may contain multiple parts. The two removed structured reference issues were the contextual PHB Condition references in Cackle Fever, now represented as typed PHB references. The remaining issues continue to be tracked in the manifest. Omitted and duplicate registers were regenerated from the revised manifest and preserve complete original entries, identity, classification, and reason. Sewer Plague's descriptive origin/ecology and Cackle Fever's descriptive presentation remain outside canonical JSON. The eight completed partial records promoted to full are Grapple, Unarmed Strike, Difficult Terrain, Darkvision, both Tremorsenses, Truesight, and Sewer Plague; Cackle Fever replaces the former unsupported classification with a complete mechanical record.

The nine partial records remaining are Attack, Cast a Spell, magic-item crafting, PHB and XPHB Concentration, PHB Surprised, Common, Gith, and Thieves' Cant. They retain the previously documented external dependencies, accepted partial scope, communication gaps, or encounter-side procedures; Phase 4 did not expand these accepted domains.

## Six approved Phase 2 fixture corrections

1. PHB Dodge now requires the affected creature to see the attacker (`actions.json $.action[10]`).
2. PHB Shove represents automatic success when the target is Incapacitated (`actions.json $.action[38]`).
3. Unarmed Strike's Grapple save DC is explicitly linked to escape attempts (`variantrules.json $.variantrule[225]`).
4. Critical Hit includes unarmed attack forms (`variantrules.json $.variantrule[38]`).
5. PHB Acrobatics cites page 176 (`skills.json $.skill[0]`).
6. PHB Blinded includes the HftT page 48 citation (`conditionsdiseases.json $.condition` record Blinded, `otherSources`).

These fixture corrections do not create new canonical entities or change the Phase 3 representative selection.

## Validation and source fidelity

`npm run validate:core-rules` passed:

- 9 Draft 2020-12 schemas compiled.
- 36 examples validated: 34 existing Phase 2 examples plus two new focused Disease fixtures.
- 34 Phase 3 canonical records validated.
- 121 permanent checks passed, including positive and negative tests for Disease-local symptom/DC references, metadata-only Disease rejection, optional Concentration saves, action/choice integrity, Rule composition, cross-source references, source metadata fidelity, formatting, and the six corrected fixtures.
- 14 Rule components and 12 Action records received integrity checks.

Structural validation and deterministic integrity assertions do not prove semantic correctness. Source-backed assertions inspect the relevant pinned records for the revised Disease lifecycle, Grapple, Difficult Terrain, Concentration, Sense capabilities, and all six fixture corrections. Human source review remains required. The validation baseline was refreshed only for explicitly authorized Phase 4 changes to Disease, Sense, Status, and the corrected Condition, Sense, Skill, and Status fixtures; other protected approved-domain files remain hash-checked.

Schema and example JSON formatting uses two-space indentation and expanded objects and arrays. Canonical records are also checked for deterministic formatting, metadata, IDs, typed-reference resolution, omission/duplicate consistency, and absence of source prose or fallback fields.

## Remaining limitations and review points

- Disease procedures with behavior outside the introduced bounded patterns may need further design. Cackle Fever is now represented without merging its stress, temporary symptom, infection, recovery, and failure-count procedures into Sewer Plague's linear condition progression.
- PHB Concentration's dependent spell termination and spell-specific duration remain Spell-domain relationships. The environmental save is optional; damage saves remain mandatory.
- PHB Surprised assignment remains encounter-side Stealth versus passive Perception (C9). Thieves' Cant communication mechanics and broad Language speaker predicates remain deferred (C10).
- The 12 remaining structured source-field/reference issues remain in the accounting manifest; this implementation does not invent missing targets.
- Source `ruleType` codes `C`, `O`, `V`, and `VO` remain unresolved; no classifications were inferred.
- This is still representative conversion, not proof of coverage for the full source datasets.

## Phase gate

The work is limited to Phase 4 contract refinement and implementation. Phase 5 dataset expansion and Phase 6 backend integration have not started. Phase 4 remains awaiting human review.
