# Core Rules Foundation — Phase 4 Contract Review

## 1. Executive summary

**Status:** Contract review completed for human decisions. No schemas, Phase 2 examples, Phase 3 canonical records, source data, or accounting classifications were changed. Phase 5 has not started.

This review checked all eight approved v1 contracts against the 34 approved Phase 3 records, their 74 source-entry roots and 14 structured-field/reference issues, the complete omission and duplicate registers, the Phase 1/2 research, and the original source objects identified below. Phase 3 remains the baseline; this report identifies proposals and one interpretation concern for review rather than rewriting accepted interpretations.

The highest-value contract decisions are:

1. **REQUIRED — Disease:** add Disease-specific onset, target/exposure qualification, quantified Condition changes, condition-gated restrictions, and branching progression needed by Sewer Plague. This common lifecycle does **not** by itself model all Cackle Fever behaviors; do not force event-driven infection, per-source immunity, random DC reduction, and failure-count outcomes into it without a separate review.
2. **REQUIRED — Action:** distinguish moving while dragging/carrying a Grappled creature from forced movement, and represent the speed penalty and size exception.
3. **REQUIRED — Rule:** attach the five-foot reach requirement to the Grapple and Shove Unarmed Strike options, not only the Damage attack roll.
4. **REQUIRED — Rule:** model the source-defined conditions that make a space Difficult Terrain, including its explicit DM-adjudicated “similar” case.
5. **REQUIRED — Status:** mark the PHB environmental Concentration save as DM-optional. Current canonical data makes it appear automatic.
6. **REQUIRED — Sense:** represent the two Darkvision light transformations independently and retain grayscale restrictions.
7. **REQUIRED — Sense:** represent Tremorsense's pinpoint capability.
8. **REQUIRED — Sense:** represent Truesight's invisible-object and illusion interactions.

Four accepted decisions are preserved: Attack count/Extra Attack stays as approved; Cast a Spell keeps its timing alternatives and external spell dependency; Unarmed Strike keeps its choice-before-resolution composition; Magic-item crafting remains partial. Six Phase 2 fixture corrections are factual fixture edits supported by existing schema structures; none needs a new schema field.

## 2. Review scope and evidence

Reviewed `AGENTS.md`, `BOUNDARIES.md`, the Step 2 roadmap section, [`doc/CORE_RULES.md`](../../doc/CORE_RULES.md), the complete Phase 1 report, all nine v1 schemas and 34 Phase 2 examples, the Phase 2 validation and Action/Rule research reports, all 33 Phase 3 canonical records, the Phase 3 conversion report and accounting manifest, all eight model omission/duplicate registers, the permanent validation utility, and the original source objects cited in this report.

The Phase 3 baseline is 34 source records, 33 canonical records, 16 fully represented, 17 partial and 1 unsupported; 74 entry roots accounted for; 51 interpreted, 11 duplicate and 50 omitted classification parts; and 14 structured-field/reference issues. The previous report records 111 passing permanent checks. These figures describe the approved representative sample, not complete normalization.

Source evidence is cited by repository-relative file and JSONPath, for example `src/main/resources/data/actions.json $.action[18]`. A JSON fragment below is a proposal illustration, not an approved contract or a claim that validation has been run against it.

## 3. Domain-by-domain findings

### Action

**Attack — accepted, no change.** `actions.json $.action[1]` (PHB) describes the Attack action and its use of one attack, while Extra Attack comes from features outside this model. The Phase 3 `ATTACK_RESOLUTION` captures supported attack forms, but the Action schema has no attack-count or feature-grant model. This is an accepted boundary: do not add an Action attack-count property. Future feature records may reference or grant attacks when that domain is designed.

**Grapple — genuine movement gap.** `actions.json $.action[18].entries[2]` permits the grappler to move while dragging/carrying the Grappled target, halves the grappler's Speed, and waives that penalty when the target is at least two sizes smaller. Action `FORCED_MOVEMENT` represents an externally imposed drag by distance; it does not describe the grappler voluntarily moving with a condition-linked creature or a size-dependent Speed cost. The complete entry is in `doc/action/omitted.md`. This affects combat movement and is distinct from forced movement, Shove, and the grapple check.

**Spell action — accepted, no change.** PHB Cast a Spell's action/reaction/variable-time alternatives remain accepted. The casting procedure and spell-defined timing remain dependencies for the Spell domain; the omission is not a defect in Action timing.

**Mark and other Action references.** Mark's reaction behavior is represented. Its `seeAlsoAction` token `attack` does not select PHB versus XPHB (`actions.json $.action[29]`), and unqualified Incapacitated/Shocking Grasp tags likewise do not establish exact targets. These are reference-resolution issues, not a reason to permit unresolved canonical IDs or change Action's reference shape. Preserve them outside canonical data until source context resolves them.

Mark's `fromVariant: "Action Options"` is editorial provenance with no approved metadata field, and its optional-rule purpose is nonmechanical; neither warrants a mechanics extension. These are accounted outside canonical JSON.

### Rule

**Unarmed Strike — reach is incomplete.** `variantrules.json $.variantrule[225].entries[0]` limits the Damage, Grapple, and Shove alternatives to a target within five feet. The current Rule schema's `reachFeet` is available on an attack roll, and Phase 3 records it on the Damage attack only. `UNARMED_STRIKE_OPTION` cannot state reach for its saving-throw procedures. Keep the accepted `OR` choice-before-resolution composition; extend the option component with the shared reach constraint.

**Difficult Terrain — effect is represented, applicability is not.** `variantrules.json $.variantrule[53].entries[0]` supports the extra foot per foot and non-cumulative result already recorded. Entries 1–2 define qualifying spaces: non-Tiny non-ally creatures, size-matched furniture, listed surfaces/materials, liquid depth, a relatively narrow opening, a slope threshold, and “something similar.” The current `MOVEMENT_SPATIAL / DIFFICULT_TERRAIN` component has no applicability/qualifying-space field. Without that, the cost is known but the rule for deciding when it applies is not.

**Magic-item crafting — accepted, no change.** `variantrules.json $.variantrule[62]` has a partial `DOWNTIME_PROCEDURE` representation. Its crafting formula and spell prerequisites; formula, item-rarity, availability, sale-cost and rarity-table relationships; variable cost/work; spell-slot/material-component consumption; group contributions; lifestyle exception; and DM discretion remain documented gaps. Per the approved decision, do not broaden this review into a full crafting model.

**Classification codes — no change.** Selected Rule records use `C` and `O`; the repository provides no authoritative mapping. `V` and `VO` remain unresolved as well. Keep the source values in external accounting; do not infer canonical classifications. This is an evidence gap, not a schema gap.

### Skill

All three Phase 3 Skills preserve the source ability association. Their omitted descriptions are task examples/DM guidance rather than procedures that the approved Skill contract should execute. The validator's source metadata checks do not alone prove each ability mapping; add a focused assertion when fixture corrections are implemented. No Skill schema extension is indicated.

### Condition

Both PHB/XPHB Blinded and Exhaustion records are represented separately. The Phase 3 source accounting classifies their nonmechanical or explanatory source entry content as excluded; no unrepresented mechanical Condition effect was found in this sample. PHB/XPHB Exhaustion differences are preserved. No Condition redesign is recommended.

### Disease

**Sewer Plague — multiple reusable lifecycle gaps.** `conditionsdiseases.json $.disease[18]` currently retains the contact/wound routes, DC 11 Constitution infection save, magical-contagion classification, and XPHB Exhaustion identity. The source additionally limits targets to Humanoids, qualifies infection sources, delays effects by 1d4 days, adds one Exhaustion level, halves Hit Point Dice recovery while Exhausted, prevents specified Long Rest recovery while Exhausted, and calls for a DC 11 Constitution save daily at dawn. Failure adds one Exhaustion level; success removes one; the disease ends only when Exhaustion reaches zero.

The approved Disease schema has fixed numeric `incubation`, a Condition reference without amount/onset, progression with one trigger and one closed outcome, and no condition-gated recovery restrictions. It cannot represent these state interactions without losing or changing mechanics. Disease-specific lifecycle structures are reusable across disease records and materially affect play; a general effects engine is unnecessary.

**Cackle Fever — not fully covered by the Sewer Plague correction.** `conditionsdiseases.json $.disease[4].entries[0..4]` includes Humanoid targeting and gnome immunity; 1d4-hour symptom onset; Exhaustion that cannot be removed until cure; a great-stress event save; damage and timed Incapacitated/laughter on failure; end-of-turn repeat saves; nearby-entity infection; 24-hour immunity to a particular infected creature's laughter; a Long Rest save that reduces DC by 1d6 until zero; and a three-failure indefinite-madness result. Current Disease fields cannot represent that combined event, infection, temporary effect, local immunity, and stateful DC/failure-count behavior.

Random intervals, scoped applicability/exposure, condition-level changes, and outcome branches would support common lifecycle mechanics in both records. They would only partially support Cackle Fever. The event-triggered contagion, source-relative immunity, damage/duration, random DC descent, and failure-count outcome need a further Disease-specific design review before attempting that record. Do not add generic expression trees or a universal progression engine to make it fit.

Blinding Sickness (`conditionsdiseases.json $.disease[1]`) has typed Wisdom roll Disadvantage and Blinded reference; the omitted pain/eye appearance is descriptive. No additional schema proposal is warranted from this record.

### Status

**Concentration — one PHB source-faithfulness concern.** PHB source `conditionsdiseases.json $.status[1].entries[3]` says the DM *might decide* that environmental phenomena require a DC 10 Constitution save. The Phase 3 PHB canonical record currently contains an `environmental-phenomenon` maintenance save without an optional/adjudicated marker, which reads as a generally required save. The Status schema needs a small trigger requirement/adjudication value for this case. PHB and XPHB damage DC formulas, separate damage saves, automatic ends, and voluntary ending are otherwise represented with edition differences intact. Spell/effect termination and maximum duration belong to the external Spell/effect record; the dependency should stay explicit rather than embedding spell mechanics into Status.

**PHB Surprised — procedure deferred.** `conditionsdiseases.json $.status[3].entries[1]` gives an encounter determination procedure: automatic mutual notice if neither side hides; otherwise compare each hiding creature's Stealth check to each opposing creature's passive Perception; apply Surprised per creature. The current Status model represents resulting turn restrictions, and Phase 3 correctly preserves individual application. It lacks encounter-side assignment logic. This procedure belongs with encounter/initiative rules and has limited reuse in the Core Rules sample; defer it until the relevant encounter model is designed rather than expanding Status with a combat setup engine now.

Concentration spell/effect termination remains an external dependency, not an omitted Status rule. XPHB duration examples defer to the duration of the specific spell/effect. Surprised's descriptive example is nonmechanical.

### Sense

**Darkvision.** `senses.json $.sense[2].entries[0]` has two separate transformations: dim light appears bright; darkness appears dim. Grayscale applies in darkness. `SIGHT.darknessAs` can represent only the latter. Reusing it for dim light would change field meaning. Add an independent optional `dimLightAs` with the same established light-outcome vocabulary; preserve existing `darknessAs` and `colorPerception`.

**Tremorsense.** MM `senses.json $.sense[4]` and XPHB `$.sense[5]` explicitly say “pinpoint”: the origin of vibrations in MM and locations of creatures/moving objects in XPHB. Existing nonvisual `detects` and contact/air restrictions do not encode location precision. Add a typed location-precision property, scoped to what the capability detects; retain source-edition differences and creature-specific range outside Sense.

**Truesight.** PHB `senses.json $.sense[6]` includes invisible creatures *and objects*, automatic visual-illusion detection, and automatic success against illusion saves. XPHB `$.sense[7]` distinguishes creatures and objects with Invisible, makes visual illusions transparent, and grants automatic success. The current enum can state `invisible-creatures`, `objects`, and `illusions`, but cannot say objects are invisible, how illusions appear, or how saves resolve. Typed invisible-object perception and a compact illusion interaction are reusable Sense capabilities; do not collapse the PHB and XPHB wording into an unsupported edition-equivalence claim.

### Language

**Already supported:** the Language contract retains category, script string, origin and dialect references, plus creature references or typed creature filters for typical speakers. It is not necessary to replace these with a generalized language/script model.

**Unresolved identities/dependencies:** XPHB Common's `Sigil` origin lacks an established canonical place record; Gith's Githzerai/Githyanki dialect names lack matching Language records in the source inventory; Thieves' Cant has an unqualified creature link and a class speaker (`Rogue`) not accepted by the current creature-only typical-speaker target. Phase 1 also identified language references to species and classes. These are identity/catalog or target-domain limitations. Do not add arbitrary strings or invent source-qualified references. Revisit target types after Place, Species/Class, and creature identities exist.

**Thieves' Cant communication behavior:** `languages.json $.language[173].entries[0]` says only a creature that knows the Cant understands the coded message and conveying it takes four times as long. This is mechanical communication behavior, not merely a typical-speaker association. A Language-specific typed covert-message profile could encode comprehension prerequisite and message-time multiplier, but the representative evidence is one record and no broader behavior family has been assessed. **DEFER** this extension until a complete Language source inventory identifies reuse; retain the source entry accounting meanwhile.

Gith's circular letter layout, word clustering, and writing directions are script/writing-system details with no approved executable use case; defer a Script model. Do not create a `LanguageScript` model solely for font assets.

## 4. Prioritized contract proposals

These changes are independent recommendations, not implementation authorization. “Before” shows the current gap; “after” sketches the smallest direction. The literal names are illustrative until the schemas are approved.

| ID | Recommendation | Domain/schema | Scope and rationale |
| --- | --- | --- | --- |
| C1 | `REQUIRED` | Disease | Typed onset interval, exposure applicability, level-qualified Condition changes, state-gated restrictions, and success/failure progression for Sewer Plague. Reuse only these Disease-specific mechanics; separately design Cackle Fever's event-driven spread/state. |
| C2 | `REQUIRED` | Action | A move-with-grappled-target operation, with drag/carry relation, half-Speed cost, and two-size exception. Forced movement is not equivalent. |
| C3 | `REQUIRED` | Rule | Reach on `UNARMED_STRIKE_OPTION`, so Grapple/Shove as well as Damage respect five feet. Keep current composition. |
| C4 | `REQUIRED` | Rule | Typed Difficult Terrain applicability for the listed space conditions and the DM-adjudicated similar case. |
| C5 | `REQUIRED` | Status | Mark PHB environmental Concentration saves as DM-optional. Prevent an unconditional canonical interpretation. |
| C6 | `REQUIRED` | Sense | Add `SIGHT.dimLightAs`, independently from `darknessAs`. |
| C7 | `REQUIRED` | Sense | Add typed Tremorsense location precision. |
| C8 | `REQUIRED` | Sense | Add Truesight invisible-object and illusion outcomes, preserving edition wording. |
| C9 | `DEFER` | Status/Rule | Surprise determination requires encounter-side resolution. Revisit with encounter/initiative rules, retaining current per-creature Surprised effects. |
| C10 | `DEFER` | Language | Type Thieves' Cant comprehension/time cost after auditing broader Language records; resolve speaker target types when referenced domains are available. |
| C11 | `DEFER` | Disease | Design Cackle Fever's stress trigger, contagion spread, per-source immunity, random DC reduction, and failure-count consequence in a dedicated Disease review after C1's core model is approved. |

### C1 — Disease progression example

Current Sewer Plague fragment:

```json
{
  "symptoms": [
    {
      "kind": "CONDITION",
      "condition": {
        "targetType": "condition",
        "id": "condition:xphb:exhaustion"
      }
    }
  ]
}
```

Illustrative future shape (not final property names):

```json
{
  "onset": {
    "kind": "RANDOM_INTERVAL",
    "count": 1,
    "die": 4,
    "unit": "day"
  },
  "applicability": {
    "targetCreatureTypes": ["humanoid"]
  },
  "progression": [
    {
      "kind": "SAVING_THROW_PROGRESSION",
      "cadence": { "kind": "DAILY_AT", "time": "DAWN" },
      "savingThrow": { "ability": "constitution", "dc": 11 },
      "onFailure": [
        {
          "kind": "CHANGE_CONDITION_LEVEL",
          "condition": { "targetType": "condition", "id": "condition:xphb:exhaustion" },
          "amount": 1
        }
      ],
      "onSuccess": [
        {
          "kind": "CHANGE_CONDITION_LEVEL",
          "condition": { "targetType": "condition", "id": "condition:xphb:exhaustion" },
          "amount": -1
        }
      ],
      "endsWhen": {
        "condition": { "targetType": "condition", "id": "condition:xphb:exhaustion" },
        "level": 0
      }
    }
  ]
}
```

This fragment still omits source-qualified exposure sources and the conditional Hit Point Dice/Long Rest rules. A complete approved Disease design must include them. It must not make a save success end the disease unless the Exhaustion threshold is reached.

**Reuse:** high across diseases with incubation, Conditions, repeated saves, and recovery. **Migration:** additive optional Disease fields; Sewer Plague changes from partial to more fully represented after approval and source-accounting reconciliation. Cackle Fever remains partial/unsupported until its separate event and outcome needs are approved. **Risk:** a progression `onSuccess`/`onFailure` shape can become a generic effects engine if its outcomes are open-ended; constrain variants to Disease lifecycle mechanics and validate them against both source records before schema approval.

### C2 — Grapple movement example

Current `FORCED_MOVEMENT` requires a fixed distance and an operation trigger. It does not represent voluntary movement with a held creature.

```json
{
  "kind": "MOVE_WITH_GRAPPLED_TARGET",
  "target": "grappled-creature",
  "mode": "drag-or-carry",
  "speedCost": {
    "kind": "HALF_SPEED",
    "waivedWhenTargetIsAtLeastSizesSmaller": 2
  }
}
```

**Reuse:** the specific relationship is Grapple, but the typed speed-cost/size-comparison value can be reused only if later source evidence demonstrates equivalent mechanics. **Migration:** add one Action operation; PHB Grapple may become more complete. Validate its source identity, target Condition, half-Speed penalty, and size exception as separate semantic assertions. **Risk:** creature-size comparisons need a stable creature size contract later. Keep the source's relative-size rule explicit rather than assuming target size fields are already normalized.

### C3–C8 — focused fragments

**C3 Unarmed Strike reach:** attach the shared range to the option, not just attack roll:

```json
{
  "componentId": "unarmed-grapple-outcome",
  "kind": "COMBAT_PROCEDURE",
  "subtype": "UNARMED_STRIKE_OPTION",
  "option": "GRAPPLE",
  "reachFeet": 5
}
```

The same field applies to Shove. This preserves the accepted choice/composition and saves. Migration is additive on one component subtype; no child Rule identity is needed. The field is reusable across both option procedures; update the representative canonical options and fixtures only after approval, and assert all three options against the source's five-foot restriction. Keep option reach separate from attack-roll reach to avoid duplicating the attack component.

**C4 Difficult Terrain:** add typed `qualifyingSpaces` to the existing subtype. The variants should cover the source's distinct conditions: creature presence, furniture relative to creature size, surface/material examples, liquid depth, opening size, and slope degrees. Retain “similar” as an explicit `DM_ADJUDICATED_SIMILAR` case rather than an open text field. Example:

```json
{
  "qualifyingSpaces": [
    { "kind": "CONTAINS_CREATURE", "restriction": "not-tiny-and-not-ally" },
    { "kind": "SURFACE_MATERIAL", "materials": ["heavy-snow", "ice", "rubble", "undergrowth"] },
    { "kind": "SLOPE_AT_LEAST", "degrees": 20 },
    { "kind": "DM_ADJUDICATED_SIMILAR" }
  ]
}
```

This list is illustrative and omits furniture/liquid/opening variants for brevity; the approved schema should encode the full inspected list. The schema extension is additive and reusable for qualifying movement spaces. Avoid collapsing all qualifications into a string or a single generic predicate.

**C5 PHB Concentration:** add a constrained save requirement for the environmental trigger:

```json
{
  "kind": "MAINTENANCE_SAVE",
  "trigger": "environmental-phenomenon",
  "requirement": "DM_MAY_REQUIRE",
  "ability": "constitution",
  "dc": { "formula": "fixed", "base": 10 },
  "failure": "status-ends"
}
```

The source does not make the trigger universally required. Migration is one optional field on the PHB environmental save; XPHB has no equivalent environmental save and remains unchanged. Validate that this value is allowed only for environmental phenomena. Reuse is limited to another explicitly DM-optional Status trigger if source evidence appears. Risk is low; do not generalize DM discretion to every mechanic.

**C6 Darkvision:**

```json
{
  "kind": "SIGHT",
  "darknessAs": "dim-light",
  "dimLightAs": "bright-light",
  "colorPerception": "grayscale"
}
```

This is additive, independently scoped, and reuses established light outcomes. It changes no existing darkness or grayscale semantics. After approval, migrate PHB Darkvision and add source-backed assertions for each light condition and grayscale-in-darkness; leave XPHB unchanged absent its own source evidence. Schema descriptions must prevent either field from being applied to the other light level.

**C7 Tremorsense:** attach pinpoint precision to nonvisual perception:

```json
{
  "kind": "NONVISUAL_PERCEPTION",
  "medium": "vibration",
  "detects": ["creatures", "movement"],
  "locationPrecision": "pinpoint"
}
```

**C7 reuse/migration/risk:** add location precision to both PHB MM and XPHB Tremorsense, but retain their distinct detectable targets and contact limitations. Assert pinpointing against each source and keep creature ranges outside Sense. Risk: MM “origin of vibrations” and XPHB “creatures and moving objects” may need distinct location-basis enum values.

**C8 Truesight** needs a separate contract decision for invisible objects and a typed illusion interaction. PHB/XPHB wording differs: PHB says automatically detect visual illusions and succeed on saves; XPHB says illusions appear transparent and saves automatically succeed. A possible structure is:

```json
{
  "kind": "SIGHT",
  "perceives": ["invisible-creatures", "invisible-objects", "illusions"],
  "illusionInteraction": {
    "appearance": "transparent",
    "detection": "automatic",
    "savingThrowOutcome": "automatic-success"
  }
}
```

A final typed union must allow the source-supported aspects separately; PHB need not assert transparency if only XPHB says that. The proposal is additive to Sense and reusable by both editions. Migration should test invisible objects, transparency/detection, and saving-throw outcomes separately. Risk: a single `illusions` enum or `automatic` flag conflates visibility, detection, and save behavior.

## 5. Phase 2 fixture correction proposals

All six points below are source-backed fixture corrections, not schema redesign. The examples remain unchanged pending approval.

| Fixture | Source evidence and current issue | Proposed correction | Schema/migration impact | Recommendation |
| --- | --- | --- | --- | --- |
| PHB Dodge (`action-dodge.json`) | `actions.json $.action[10]`: the affected creature receives attack disadvantage when **it can see the attacker**. Fixture's `CAN_SEE` actor is `attacker`, target `affected-creature`. | In `when`, set actor `affected-creature`, target `attacker`; preserve `ATTACK_AGAINST_AFFECTED` roll roles. | Existing typed `CAN_SEE` supports both roles. Example-only correction. | `REQUIRED` |
| PHB Shove (`action-shove.json`) | `actions.json $.action[38]` says the target automatically loses the contest if Incapacitated. Fixture has no `automaticSuccessWhen`. | Add a typed target `HAS_CONDITION` predicate referencing PHB Incapacitated. | Existing Action condition predicate supports it. Example-only correction. | `REQUIRED` |
| Unarmed Strike (`rule-unarmed-strike.json`) | `variantrules.json $.variantrule[225]` says the Grapple saving throw DC also governs escape attempts. Fixture does not link those uses. | Add the typed relationship from `condition:xphb:grappled` to the shared save DC. | Existing Rule reference relationship supports it; preserves one save component. Example-only correction. | `REQUIRED` |
| Critical Hit (`rule.json`) | `variantrules.json $.variantrule[38]` applies to an attack roll generally; PHB/XPHB Unarmed Strike is an attack roll. Fixture's attack forms omit `unarmed`. | Include `unarmed` in attack forms. | Existing attack form enum accepts it. Example-only correction. | `REQUIRED` |
| PHB Acrobatics (`skill.json`) | `skills.json $.skill[0].page` is 176; fixture uses 175. | Correct page to 176. | Schema accepts page; source-fidelity correction only. | `REQUIRED` |
| PHB Blinded (`condition.json`) | `conditionsdiseases.json $.condition[0].otherSources` includes HftT page 48; fixture includes RMR page 62 only. | Add `{ "source": "HftT", "page": 48 }` under `sourceMetadata.citations`. | Existing shared metadata `citations` supports source/page. Example-only correction. | `REQUIRED` |

These corrections do not alter the approved Phase 3 interpretation baseline. The Dodge correction is distinct from the correct XPHB Phase 3 actor direction, and the Shove/Unarmed/Critical Hit corrections are reflected in the source-backed Phase 3 records where applicable.

## 6. Accepted limitations and no-change findings

- **Attack count/Extra Attack:** keep accepted representation; a future feature-side relationship may supply additional attacks. No Action count field.
- **Cast a Spell:** accepted timing alternatives; spell-specific cast times and broader chapter mechanics belong to the Spell domain.
- **Unarmed Strike composition:** retain the accepted OR/choice-before-resolution contract while adding only the missing reach property if approved.
- **Magic-item crafting:** retain the current partial downtime procedure.
- **Skill descriptions:** no mechanical examples field; retain ability mappings and omit task guidance.
- **Condition editions:** preserve PHB/XPHB records and mechanics; no new gap identified in the sampled mechanics.
- **Disease ecology and symptom descriptions:** keep flavor out of canonical data; account it as nonmechanical.
- **Concentration's spell/effect consequences:** keep referenced effect lifecycle in the future Spell/effect model, not duplicated on Status.
- **Language origin/dialect unresolved targets:** no arbitrary strings or target identities. Current typed references are sufficient once exact targets exist.
- **Rule codes:** leave `C`, `O`, `V`, and `VO` unresolved until authoritative repository/source evidence establishes their meaning.
- **Script layout/font assets:** no LanguageScript model or font metadata. A writing-system contract needs a separately justified use case.

## 7. Reference and external dependency issues

The 14 manifest field/reference issues are not all schema defects:

- Mark's `seeAlsoAction: attack` collides with PHB and XPHB Attack identities; Incapacitated and Shocking Grasp tags omit edition/source qualification. Resolve only from explicit source context.
- Gith dialect names are absent as Language entities; XPHB Common's place origin Sigil needs a place identity. Thieves' Cant includes a class speaker outside the current creature-only speaker target and an unqualified creature tag.
- Cackle Fever's unqualified Exhaustion tag does not choose PHB/XPHB; Incapacitated lacks an explicit edition. Preserve those as ambiguity/unresolved identity, not guesses.
- Selected `ruleType` values C/O remain unresolved; V/VO are likewise not expanded.
- Attack/Extra Attack, Spell casting/effect lifecycles, creature size/type, class/species speakers, and place identity depend on later domains. Stable typed references can be added once identities are source-qualified and approved; embedding those models in Core Rules is unnecessary.

The schema's reference shape is adequate for resolved references. The current validation checks that generated targets exist and that their target types match; it cannot resolve source ambiguity on behalf of interpretation.

## 8. Validation workflow review

The permanent command is `npm run validate:core-rules` (`tools/validate-core-rules.cjs`). The approved Phase 3 report records 9 Draft 2020-12 schemas compiled, 34 examples and 33 canonical records validated, and 111 permanent checks passed. The utility includes source metadata checks; ID uniqueness; typed-reference resolution; Action operation/choice integrity; Rule component/composition/applicability checks; manifest/register consistency; and positive/negative structural cases.

Coverage is strong for **structure and internal consistency** but limited for **source-mechanics fidelity**:

- Schema validation does not prove a source mechanic was encoded correctly. The fixed semantic assertions cover selected Dodge/Shove, Exhaustion, Concentration, Unarmed Strike, Mark, timing, and several references, not all mechanics in all 34 source objects.
- Action choice and Rule composition checks validate IDs, branch references, cardinality, and compatible applicability. They do not execute gameplay or establish that branch outcomes match source text.
- Disease assertions check the current infection save, classification, and Exhaustion reference, not disease cadence, condition-level state, or recovery effects.
- Status assertions check selected Concentration DC and environmental behavior, not optionality or every ending condition.
- Sense assertions check XPHB Tremorsense detection/restrictions, but not Darkvision's two transformations, pinpointing, or Truesight outcomes.
- Language assertions resolve selected creature speaker IDs; they do not test origin/dialect fidelity or the Thieves' Cant communication rule.
- Skill ability values and source pages should have direct source-backed regression assertions, especially when correcting fixtures.
- Accounting checks verify locator/register completeness and recorded dispositions; only human source comparison can establish that each disposition is semantically sound.

For approved implementation, add focused assertions tied to exact source locators for each approved change: Disease 1d4 onset, Humanoid applicability, Exhaustion level changes and threshold, recovery gates; Grapple size/speed behavior; terrain qualifiers; optional PHB environmental save; Sense transformations/precision/illusion outcomes; and all six fixture corrections. Keep structural checks and source-backed semantic assertions reported separately. No new validation code is implemented in this review.

## 9. Proposed implementation order

1. Human approves or declines each independent C1–C11 proposal and the six fixture corrections.
2. Design the bounded Disease lifecycle and test its concept against both Sewer Plague and Cackle Fever before finalizing C1/C11. If Cackle requires distinct episodic/spread mechanics, keep those as a separate optional Disease substructure.
3. Implement shared Sense additions (C6–C8) and validate edition distinctions against the PHB/XPHB source records.
4. Implement Action Grapple carry behavior and Rule reach/terrain applicability (C2–C4).
5. Correct PHB Concentration optional adjudication and the six Phase 2 fixtures after the schema decision is settled.
6. Add source-backed assertions, compile schemas, validate fixtures and representative canonical records, check accounting, and report migration effects. Any changed approved contract or canonical interpretation must return for review before further conversion.

This order is a proposal only. Phase 5 expansion and backend integration remain unauthorized by this report.

## 10. Decisions requiring human approval

1. Approve `SIGHT.dimLightAs` independently of `darknessAs`.
2. Approve the Disease lifecycle scope for randomized intervals, typed exposure/target restrictions, condition-level changes, gated recovery restrictions, and save-branch progression; decide whether Cackle Fever's event/spread/DC/failure-counter mechanics join the same design or remain a later submodel review.
3. Approve an Action operation for moving with a Grappled target and its size-based Speed cost.
4. Approve five-foot reach on Unarmed Strike Grapple/Shove option components.
5. Approve typed qualifying-space conditions for Difficult Terrain, including DM-adjudicated similar cases.
6. Approve a PHB-only DM-optional marker for environmental Concentration saves.
7. Approve Sense location precision for Tremorsense.
8. Approve Truesight invisible-object/illusion mechanics while preserving PHB/XPHB distinctions.
9. Decide whether PHB Surprised assignment is deferred to encounter/initiative modeling (recommended) or needs a Status/Rule structure sooner.
10. Defer Thieves' Cant communication mechanics and broader non-creature typical-speaker targets until Language/related-domain inventory (recommended), or authorize their design now.
11. Confirm Cackle Fever's event/spread/DC/failure-count mechanics remain deferred for a dedicated Disease design, or authorize that design as part of the Disease refinement.
12. Approve the six Phase 2 fixture corrections listed in section 5.

No proposal has been implemented. Phase 4 contract review is complete and awaiting human decisions; Phase 5 has not started.
