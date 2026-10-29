# Step 2 Core Rules Foundation — Phase 5 Detailed Action Contract Review

## Executive summary

This review compares all 48 Action source records in `src/main/resources/data/actions.json` with the complete approved Action and shared v1 schemas, all 43 existing Action canonical records, examples, registers, and prior research. Source objects are identified as `$.action[0]` through `$.action[47]`; the catalog contains 8 DMG, 20 PHB, 18 XPHB, and 2 XGE records. The 6 Phase 3/4 records and 37 Phase 5 canonical records account for 43 outputs. Five records have no canonical output.

The current reported classifications remain a useful starting tally, but several accounting reasons and one canonical mapping need review. Reassessment leaves the record totals provisionally at **13 fully represented, 30 partial, and 5 unsupported**. That tally is a record-level judgment; it is not a claim that every omitted classification part is a schema gap.

Three source-backed findings stand out:

1. **Use an Object and Utilize are currently mapped to `ordinary-interaction`.** Their source entries expressly describe an action-cost use of an object. That operation conflates incidental object interaction with an item use that costs an Action. This is a correctness issue requiring a distinct typed Action operation.
2. **The movement model needs distinct creature-space operations.** Permission to pass through a hostile creature’s space, entering and moving within a creature’s space, and moving a target sideways are different mechanics. They cannot be represented as forced movement or as the existing Grapple carry operation.
3. **The Action check contracts need better linked procedure detail.** Several current omissions are real, while some are already representable: the existing check model can express fixed DCs, opposing abilities, selected abilities, Skill references, and success-linked Condition operations. The major gaps are ability/Skill option pairing and enforcement, an attack roll opposed by a check, source-specific predicates and DC formulas, and typed outcomes beyond the narrow existing set.

The recommendations are **ACT-1 through ACT-3 REQUIRED**, **ACT-4 RECOMMENDED**, and **ACT-5 and ACT-6 DEFER**. The conservative forecast is 29 full, 17 partial, and 2 unsupported after ACT-1–ACT-3; adding ACT-4 could bring two more records to full (31 full, 15 partial, 2 unsupported). These are design forecasts, not guaranteed conversion results. No schema, canonical JSON, fixture, validator, source data, register, or documentation index was changed for this review.

## Scope, evidence, and classification method

Reviewed the repository instructions and boundaries; Action/Rule research; Phase 3 representative report; Phase 4 contract review and implementation; Phase 5 expansion and source-accounting reports; Action, common, Rule, Condition, Status, Sense, and Disease schemas; all 43 canonical Action outputs; Action examples, registers, and validation assertions; and all 48 original Action objects including nested entries and structured properties.

Relevant current contract structures include Action capabilities, requirements, typed operations for attack resolution, modifiers, checks, saves, movement, objects, spells, recovery, Conditions, and Statuses; local operation IDs and applicability; Action choice groups; and shared attack rolls, D20 modifiers, checks, DCs, triggers, predicates, references, and timing. Related ownership includes Rule attack/check procedures, Condition and Status state changes, Sense visibility, Spell casting, and future Equipment and character-resource models.

The matrix distinguishes current source-faithful coverage from future coverage. `Full` means the Action source object’s mechanical content is represented or deliberately resolved to an identified domain boundary. `Partial` means supported content is present but a meaningful clause remains absent or external. `Unsupported` means there is no faithful canonical record. Forecast status assumes the listed proposals are approved and implemented, with source interpretation still required. References and future-domain dependencies do not by themselves excuse omission of a mechanic expressible now.

## Full-catalog coverage matrix

Source paths are exact object locators. The current labels were reassessed from source rather than copied automatically from the expansion report. A dagger marks a current source-accounting or canonical-fidelity issue described later.

| ID | Source object | Main source mechanics and dependencies | Current | After REQUIRED | After RECOMMENDED | Decisions / remaining dependency |
|---|---|---|---|---|---|---|
| `action:dmg:activate-an-item` | `$.action[0]` | Item-defined, command-word, read-scroll, drink/apply activation; sound restriction; consumables, spell activation, charges, Identify/attunement | Partial | Partial | Partial | ACT-5; Item, Spell, resource state |
| `action:phb:attack` | `$.action[1]` | One melee or ranged attack resolution; features can grant more attacks; separate Making an Attack procedure | Partial | Partial | Partial | Accepted feature-side Extra Attack; Rule procedure dependency; accounting correction for the one represented attack |
| `action:xphb:attack` | `$.action[2]` | One weapon/Unarmed Strike attack; equip or unequip one weapon before/after; movement between feature-granted attacks | Partial | Partial | Partial | ACT-5; Equipment and feature/turn sequencing |
| `action:phb:cast-a-spell` | `$.action[3]` | Casting-time alternatives; spellcasting procedure is external | Partial | Partial | Partial | Accepted Spell-domain dependency |
| `action:dmg:climb-onto-a-bigger-creature` | `$.action[4]` | Positioning checks; contested check to enter target space; move with target; Advantage against target; movement within space as Difficult Terrain; target’s action to dislodge | Unsupported | Partial | Partial | ACT-1/2; DM-adjudicated entry positioning and spatial combat/Rule dependency |
| `action:phb:dash` | `$.action[5]` | Add current Speed after modifiers to current-turn movement | Full | Full | Full | Existing `MOVEMENT_INCREASE` represents the rule |
| `action:xphb:dash` | `$.action[6]` | Same increase; may choose a special Speed such as Fly or Swim each use | Partial | Full | Full | ACT-1 adds selected Speed basis |
| `action:dmg:disarm` | `$.action[7]` | Weapon attack roll opposed by target’s Str (Athletics) or Dex (Acrobatics); no damage; target drops held item; modifiers for two hands and relative size | Partial | Partial | Partial | ACT-2 resolves attack/check; ACT-5 defers held-item state to Equipment |
| `action:phb:disengage` | `$.action[8]` | Movement does not provoke Opportunity Attacks for rest of turn | Full | Full | Full | Existing `MOVEMENT_PERMISSION` |
| `action:xphb:disengage` | `$.action[9]` | Same permission through end of current turn | Full | Full | Full | Existing `MOVEMENT_PERMISSION` |
| `action:phb:dodge` | `$.action[10]` | Attack Disadvantage if affected creature sees attacker; Dexterity save Advantage; until next turn; ends on Incapacitated or Speed 0 | Full | Full | Full | Existing typed modifiers/predicates; reviewed against the approved actor direction |
| `action:xphb:dodge` | `$.action[11]` | Same effects, edition-specific loss wording | Full | Full | Full | Existing typed modifiers/predicates |
| `action:phb:don-or-doff-a-shield` | `$.action[12]` | Don or doff a shield as an Action; armor-category rule is contextual cross-reference | Full | Full | Full | Existing separate shield operations |
| `action:xphb:don-or-doff-a-shield` | `$.action[13]` | Don or doff a shield as an Action | Full | Full | Full | Existing separate shield operations |
| `action:phb:end-concentration` | `$.action[14]` | Voluntarily end Concentration at any time, no action required | Full | Full | Full | `END_CONCENTRATION` capability and Free timing; Status reference may be made explicit in future, but not needed to infer target identity |
| `action:xphb:end-concentration` | `$.action[15]` | Voluntarily end Concentration at any time, no action required | Full | Full | Full | Same, preserving XPHB wording |
| `action:phb:escape-a-grapple` | `$.action[16]` | Grappled creature’s Action; Strength (Athletics) or Dexterity (Acrobatics) opposed by grappler Strength (Athletics) | Partial | Full | Full | ACT-2 pairs each ability with its Skill and assigns each participant |
| `action:xphb:escape-a-grapple` | `$.action[17]` | Action; Str (Athletics) or Dex (Acrobatics) against escape DC; Condition also ends if grappler Incapacitated or distance exceeds grapple range | Partial | Full | Full | ACT-2 pairing; current condition-ending operations and approved escape-DC reference are reusable |
| `action:phb:grapple` | `$.action[18]` | Attack replacement; free hand, reach/size limit; contested checks; target-Incapacitated automatic success; Grappled application/release; drag/carry half Speed with size exception | Full | Full | Full | Existing Action check, Condition, and C2 movement operation |
| `action:dmg:healing-surge` | `$.action[19]` | Spend up to half Hit Dice incrementally; roll each plus Constitution; healing total; use limit and Short/Long Rest recovery | Unsupported | Unsupported | Unsupported | ACT-5; character level, Hit Dice, Constitution, and rest-resource owner needed |
| `action:phb:help` | `$.action[20]` | Either aid next task check before next turn, or aid friendly creature attacking enemy within 5 feet; first attack Advantage | Partial | Full | Full | ACT-2 adds beneficiary, task/target relation, event, and expiry |
| `action:xphb:help` | `$.action[21]` | Choose check assistance using proficiency and near enough; or attack assistance against enemy within 5 feet; Help may stabilize | Partial | Full | Full | ACT-2; stabilization consequence remains a Status/Rule dependency if not defined in this Action entry |
| `action:phb:hide` | `$.action[22]` | Dexterity (Stealth) check; on success use PHB hiding and unseen-attacker rules | Partial | Partial | Partial | ACT-2 can encode the check invocation, but complete success rules are external PHB Rule procedures without a resolved canonical section reference |
| `action:xphb:hide` | `$.action[23]` | DC 15 Stealth; obscuration/cover and enemy line-of-sight prerequisites; Invisible while hidden; check-total discovery DC; four ending events | Partial | Full | Full | ACT-2 typed Hide prerequisites, effect, and termination events; Sense supplies visibility semantics |
| `action:xge:identify-a-spell` | `$.action[24]` | Reaction while casting or Action after cast by effect; perception prerequisite; Int (Arcana), DC 15 + spell level; class-match Advantage | Partial | Full | Full | ACT-2 bounded procedure; Spell and Class references remain external identities |
| `action:phb:improvising-an-action` | `$.action[25]` | DM decides feasibility and check, if any; varied timing | Partial | Partial | Partial | Preserve open adjudication; no fixed procedure to invent |
| `action:xphb:improvising-an-action` | `$.action[26]` | DM decides feasibility and D20 Test, if any; varied timing | Partial | Partial | Partial | Existing D20 Test Rule reference helps; actual adjudication remains DM-side |
| `action:xphb:influence` | `$.action[27]` | Monster, communication and attitude; willing/unwilling/hesitant; no-check branches; selected check; DC max(15, Int); success compliance; failure retry delay | Partial | Full | Full | ACT-2 bounded Influence procedure; target attitude is an input, not a general predicate language |
| `action:xphb:magic` | `$.action[28]` | Cast spell or activate feature/item with Magic action; long casting repeats each turn; Concentration; failure if broken; slot not expended | Full | Full | Full | Existing spell operation and Status reference |
| `action:dmg:mark` | `$.action[29]` | Melee attack marks target; Advantage on qualifying opportunity attack until end of attacker’s next turn; no reaction spent, once/turn, reaction-blocking exceptions | Full | Full | Full | Existing typed modifier and trigger; edition-qualified references remain recorded as unresolved where not sourced |
| `action:phb:opportunity-attack` | `$.action[30]` | Hostile visible creature leaves reach; Reaction melee attack immediately before departure; Disengage, teleport, and forced-movement exceptions | Partial | Full | Full | ACT-1/2 trigger qualifiers, movement origin and interruption point |
| `action:xphb:opportunity-attack` | `$.action[31]` | Visible creature leaves reach using Action, Bonus Action, Reaction, or a Speed; Reaction weapon/Unarmed Strike attack before departure | Partial | Full | Full | ACT-1/2 source-qualified movement trigger; preserve different source list from PHB |
| `action:phb:other-activity` | `$.action[32]` | Brief communication; one free object interaction during move/action; second interaction or special obstacle may cost Action | Partial | Partial | Partial | ACT-3 interaction budget; communication and DM-adjudicated timing remain separate |
| `action:dmg:overrun` | `$.action[33]` | Action or Bonus Action; opposed Athletics; size Advantage/Disadvantage; successful one-time hostile-space traversal | Unsupported | Full | Full | ACT-1/2 |
| `action:phb:ready` | `$.action[34]` | Perceivable trigger; choose Action or move; take/ignore Reaction after trigger; expires next turn; readied spell and Concentration | Partial | Partial | Partial | ACT-6 defer to event/reaction and Spell/Status lifecycles |
| `action:xphb:ready` | `$.action[35]` | Same general trigger/response; Reaction; spell resources; Concentration through next turn; dissipates if broken | Partial | Partial | Partial | ACT-6; edition wording retained |
| `action:phb:search` | `$.action[36]` | DM may call for Wisdom (Perception) or Intelligence (Investigation) depending on search | Partial | Full | Full | ACT-2 ability/Skill alternatives; DC and result remain Rule/DM adjudication |
| `action:xphb:search` | `$.action[37]` | Wisdom check; suggested Skill-to-detection table | Partial | Partial | Full | ACT-2 check; ACT-4 retains typed advisory task mappings |
| `action:phb:shove` | `$.action[38]` | Attack replacement; size/reach; Str (Athletics) vs target’s chosen Str (Athletics)/Dex (Acrobatics); automatic success vs Incapacitated; choose push or Prone | Full | Full | Full | Existing operation/choice structures; Skills’ ability associations can disambiguate current source pairings, but validator should assert this |
| `action:dmg:shove-aside` | `$.action[39]` | PHB Shove procedure; Disadvantage on attacker Athletics; successful target moves 5 feet sideways to a space within reach | Partial | Full | Full | ACT-1/2: reuse procedure with explicit variant override; not generic forced movement |
| `action:phb:stabilize-a-creature` | `$.action[40]` | Unconscious target; DC 10 Wisdom (Medicine); success stabilizes; stable state suppresses death saves, damage ends it, 1 HP after 1d4 hours | Partial | Partial | Partial | Action check is already modeled; downstream stable/death-save state belongs to future encounter/character state, not an invented Status reference |
| `action:xphb:study` | `$.action[41]` | Intelligence check; Areas of Knowledge table maps Skills to source topics/creature groups | Partial | Partial | Full | ACT-2 check; ACT-4 typed advisory mappings |
| `action:dmg:tumble` | `$.action[42]` | Action or Bonus Action; opposed Acrobatics; successful one-time hostile-space traversal | Unsupported | Full | Full | ACT-1/2 |
| `action:phb:two-weapon-fighting` | `$.action[43]` | Bonus Action after Attack with held Light melee weapon; different Light weapon in other hand; damage modifier omitted unless negative; thrown alternative | Partial | Partial | Partial | ACT-5 Item properties, hand/equipment state, and a bounded damage adjustment |
| `action:xphb:two-weapon-fighting` | `$.action[44]` | On-turn Attack with Light weapon; later Bonus Action extra attack with different Light weapon; no nonnegative ability modifier to damage | Partial | Partial | Partial | ACT-5; feature/equipment state and damage-rule ownership |
| `action:phb:use-an-object` | `$.action[45]` | Action when item use costs an Action; useful for additional object interaction | Partial† | Full | Full | ACT-3 corrects current `ordinary-interaction` misclassification |
| `action:xphb:utilize` | `$.action[46]` | Action when item use costs an Action | Partial† | Full | Full | ACT-3 corrects current `ordinary-interaction` misclassification |
| `action:xge:waking-someone` | `$.action[47]` | Natural sleep only; damage, shake/slap, loud sound; whisper distance/DC; speech in quiet environment/DC | Unsupported | Unsupported | Unsupported | ACT-6 defer until natural-sleep state and environment/perception contracts exist |

`Partial†` denotes a canonical field that currently assigns the wrong operation, not merely an omitted detail. The repair requires a contract change and record migration; it is not implemented here.

## Movement and positioning findings

The current contract already distinguishes speed increase, forced movement, movement permission, triggered movement, and Grappled-target drag/carry. Those boundaries should remain. The missing semantics are not another generic movement effect:

| Source pattern | Evidence | Distinct behavior required | Ownership |
|---|---|---|---|
| Traverse hostile creature space | DMG Overrun `$.action[33].entries[0]`; DMG Tumble `$.action[42].entries[0]` | One-time passage through a hostile creature’s occupied space after a contested check. Overrun uses Athletics vs Athletics and relative-size Advantage/Disadvantage; Tumble uses Acrobatics vs Acrobatics. This grants passage, not forced movement or occupation. | Action grants a traversal permission; Rule/encounter resolution applies the chosen check and space availability. |
| Enter and move within a creature’s space | DMG Climb `$.action[4].entries[0..2]` | The smaller creature enters and moves with the target; the target is treated as terrain; the climber has attack Advantage against it. A larger target may spend an Action to dislodge it by a separate opposed check. This is persistent shared-space position, not traversal. | Action defines climb/dislodge procedures; encounter spatial state and Rule terrain/attack handling are dependencies. |
| Move a target to a lateral space | DMG Shove Aside `$.action[39].entries[0]` | Reuses the PHB special Shove attack but applies check Disadvantage and moves the target five feet to a different space within reach. It is a source-defined positional outcome, not the PHB push-away result. | Action variant using the Shove procedure; encounter occupancy validates destination. |
| Choose a Dash Speed | XPHB Dash `$.action[6].entries[1]` | Each use may select Speed or a special Speed such as Fly or Swim. It changes the basis of the extra movement; it does not grant a new movement mode. | Action selects a Speed value owned by creature movement/character statistics. |
| Opportunity attack interrupt | PHB `$.action[30].entries[1..2]`; XPHB `$.action[31].entries[0]` | Resolve a reaction attack immediately before the triggering creature exits reach. PHB requires hostility and visibility and lists Disengage, teleport, and involuntary-movement exceptions. XPHB lists its own movement sources and visibility rule. | Action owns trigger qualifiers and attack response; encounter movement owns interruption ordering and movement-origin events. |

### ACT-1 — Distinct creature-space movement and selected Speed (**REQUIRED**)

**Problem/evidence:** the four movement patterns above and XPHB Dash have no faithful current operation. Existing `FORCED_MOVEMENT` changes another creature’s position; `MOVE_WITH_GRAPPLED_TARGET` applies only to an existing Grappled relationship; `MOVEMENT_PERMISSION` only removes Opportunity Attacks. Reusing any of these would change meaning.

**Proposal:** add bounded discriminated Action operations for `TRAVERSE_CREATURE_SPACE`, `ENTER_CREATURE_SPACE`, and `MOVE_TARGET_TO_SPACE_WITHIN_REACH`; add a `speedBasis` choice to `MOVEMENT_INCREASE`. Keep each operation’s movement semantics separate. The operation should reference its check/outcome ID rather than contain a free-form procedure. Overrun/Tumble remain distinguishable by check configuration, and Climb uses a separate enter-and-move-with-target operation. Shove Aside references the PHB Shove procedure and states its override, not an implicit inheritance of every operation.

Example shape, illustrative only:

```json
{
  "kind": "TRAVERSE_CREATURE_SPACE",
  "operationId": "overrun-passage",
  "target": "hostile-creature",
  "checkId": "overrun-contest",
  "permission": "once-this-turn",
  "appliesWhen": {
    "kind": "CHECK_SUCCEEDED",
    "operationId": "overrun-contest"
  }
}
```

`ENTER_CREATURE_SPACE` must separately preserve shared movement/position and any source-defined attack modifier. `MOVE_TARGET_TO_SPACE_WITHIN_REACH` must not be encoded as a push vector. Dash should express `current-speed` versus `chosen-special-speed`, with no guessed speed identity.

**Impact:** additive operation variants and one optional movement basis; existing canonical values remain valid. Requires new negative tests preventing traversal from being represented as forced movement and preventing space occupancy from being inferred from a traversal permission. Affected records: Climb, Dash XPHB, Shove Aside, Overrun, Tumble; plus the Opportunity Attack event qualification in ACT-2. Expected: Overrun, Tumble, Shove Aside, and XPHB Dash can become full; Climb becomes at least partial until its adjudicated setup/spatial dependencies are resolved.

**Alternatives:** fold into `FORCED_MOVEMENT` or `TRIGGERED_MOVEMENT`—rejected because neither represents permission to traverse or occupy space. One broad `CREATURE_MOVEMENT` operation—rejected because it would conflate three distinct outcomes.

**Risk/open point:** define how a traversal permission interacts with occupied spaces and how the encounter resolver reports an unavailable destination. Those are execution interfaces, not new source mechanics. `REQUIRED` because currently unsupported DMG actions and the Shove Aside mapping cannot be represented faithfully otherwise.

## Linked checks, attack outcomes, and conditional procedures

The existing Action `CHECK_OR_CONTEST` is useful: it distinguishes checks against DC from contests, captures actor, ability, a fixed or referenced DC, opposing abilities, target choice, reach/size limits, and automatic success. Common `D20_ROLL_MODIFIER` represents Advantage, Disadvantage, and numeric modifiers. `operationApplicability` can link a check-success result to another operation; `choiceGroups` can enforce a bounded choice after success. Those structures are already suitable for the PHB Grapple/Shove resolutions and should be reused.

They do not currently guarantee all source detail:

- Ability and Skill choices are stored in separate arrays. A Skill record’s ability association can disambiguate a listed Skill, but the Action schema does not enforce the pairing. A bounded `checkOptions` shape with each ability and optional Skill reference together would remove ambiguity. For Grapple/Shove, current canonical values appear interpretable using approved Skill ability associations; this is primarily a validation assertion, not a reason to invent a new mechanical meaning.
- `CHECK_OR_CONTEST` cannot pair an attack roll by one participant against an ability check by another. DMG Disarm is not a normal attack contest or a check-vs-check contest.
- Current `ROLL_MODIFIER.appliesWhen` accepts an Action trigger, not the specific check operation being modified. Conditional size/two-handed modifiers and Shove Aside’s disadvantage therefore cannot be reliably bound to their check.
- Fixed DC and ability-plus-proficiency values exist, but the source-driven formulas `15 + spell level` and `max(15, target Intelligence)` are absent.
- Action operations can represent a few successful or failed outcomes, but not an Action-specific branch such as Influence’s willing/unwilling/hesitant response, Identify Spell’s check context, or XPHB Hide’s visibility prerequisites and end events.
- `ROLL_MODIFIER.target` lacks an ally/beneficiary role and cannot constrain a Help benefit to a specific task, opponent, first attack, or next check before expiry.
- `actionTrigger` supports observed spell casting, melee attack, Opportunity Attack, affected-creature attack/save, leaves-reach, a perceivable circumstance, and Grappler releases. It does not encode sound level, discovery, verbal components, movement source, or the post-casting window.

### ACT-2 — Bounded check procedures, applicability, and outcomes (**REQUIRED**)

**Affected records:** Disarm, both Escape a Grapple records, both Help records, both Hide records, Identify a Spell, Influence, PHB Search, Overrun, Tumble, Shove Aside, Opportunity Attack PHB/XPHB, and Climb. Existing Shove/Grapple tests should be checked against the same invariants.

**Proposal:** extend the existing Action procedure vocabulary only with observed, finite shapes:

1. Pair `{ability, skillReference}` as a unit for participant choice; preserve which participant chooses.
2. Add a distinct `ATTACK_CONTESTED_BY_CHECK` resolution containing one ordinary attack roll, the defender’s explicit check options, and a typed outcome. It must state `damageOnSuccess: false` where the source requires it. The dropped-item state itself remains an Equipment outcome dependency under ACT-5.
3. Permit a roll modifier to be bound to a named Action check and its actor/target/trigger. Keep `D20_ROLL_MODIFIER` as the reusable modifier value object.
4. Add only the needed bounded source predicates/outcomes: visible/hostile movement trigger and interrupt point; Hide’s obscure-or-cover and out-of-enemy-line-of-sight prerequisites, Invisible application, check-total discovery DC, and the four listed termination events; Help beneficiary/task/first-attack lifetimes; Influence’s three attitude states and defined no-check/check/retry branches; and the spell-identification check’s observation state and matching-class advantage.
5. Add the two non-composable DC formulas actually observed: fixed plus referenced Spell level, and the greater of a fixed minimum and target ability score. Do not create nested expressions or arbitrary formulas.

Illustrative pairing and DC shape:

```json
{
  "kind": "CHECK_OR_CONTEST",
  "resolution": "contested-check",
  "participants": [
    {
      "role": "acting-creature",
      "options": [
        {
          "ability": "strength",
          "skill": { "targetType": "skill", "id": "skill:phb:athletics" }
        }
      ]
    },
    {
      "role": "target",
      "selection": "participant-chooses",
      "options": [
        {
          "ability": "strength",
          "skill": { "targetType": "skill", "id": "skill:phb:athletics" }
        },
        {
          "ability": "dexterity",
          "skill": { "targetType": "skill", "id": "skill:phb:acrobatics" }
        }
      ]
    }
  ]
}
```

For existing Grapple/Shove canonicals, the two Skill references already point to PHB Skills whose ability associations distinguish these pairs. Preserve that representation unless validation or source comparison exposes ambiguity; add an assertion tying each Skill’s ability to the listed option.

Illustrative bounded DC, not a general expression tree:

```json
"dc": {
  "kind": "GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE",
  "minimum": 15,
  "ability": "intelligence"
}
```

**Ownership:** Action owns the named action procedure and branch; Rule owns base D20/attack resolution; Condition/Status owns state definitions and transitions; Sense supplies perception facts; Spell/Class provide future typed identities and properties. Equipment owns the dropped item. Do not duplicate these models in Action.

**Compatibility/migration:** additive variants and optional operation fields are possible, but the check union and operation applicability changes affect validation and all existing Action checks. Migration is needed for Shove/Grapple pair validation and Action records newly promoted from partial/unsupported. Existing records remain readable if old variants are preserved.

**Impact/forecast:** together with ACT-1 and ACT-3, supports Overrun/Tumble; completes both Escape, both Help, XPHB Hide, Identify Spell, Influence, PHB Search, both Opportunity Attack records, and Shove Aside. Climb remains partial due source-adjudicated setup/spatial targeting. Disarm remains partial until Equipment can represent the dropped held object. Alternatives were a universal action workflow or expression tree—rejected as broader and less interpretable. `REQUIRED` because multiple observed Actions cannot preserve target role, prerequisite, modifier, or outcome with current fields.

**Risks/questions:** for Influence, willingness and attitude are creature inputs set by the DM; the schema must not claim to calculate them. For Identify Spell, the class/spell matching relation can be typed before those domains exist, but identity resolution is later. For Hide, “enemy finds you” and “sound louder than a whisper” are source-defined events but need stable bounded event names, not opaque sentences. Approval should confirm the finite list before implementation.

### ACT-3 — Distinguish action-cost object use from incidental interaction (**REQUIRED**)

**Evidence:** PHB Use an Object `$.action[45].entries[0]` says the action is used when an object requires the user’s Action and is also useful for more than one object interaction. XPHB Utilize `$.action[46].entries[0]` says an object requiring an Action uses Utilize. Current canonical `operations[0].operation` is `ordinary-interaction` in both records. `OBJECT_OPERATION` defines ordinary interaction, shield state changes, and magic-item activation, but no action-cost item-use operation or item-defined Action prerequisite.

**Proposal:** add a distinct `OBJECT_USE` operation with an explicit `ACTION_REQUIRED_BY_OBJECT` prerequisite; retain `ordinary-interaction` for the free incidental interaction and `activate-magic-item` for magic-item activation. Add a bounded optional interaction count/placement only where the PHB source states it. The object’s actual effect and properties remain Equipment/Item-owned.

```json
{
  "kind": "OBJECT_USE",
  "activationRequirement": "ACTION_REQUIRED_BY_OBJECT",
  "permitsMultipleObjectInteractions": true
}
```

`permitsMultipleObjectInteractions` is present only for the PHB record; the XPHB source does not state this additional rule.

**Impact:** additive enum/operation, followed by a required migration of both existing records. It corrects a false operation assignment and should restore faithful representations of two records; it does not make the contents of an arbitrary item canonical Action data. Validate that `ordinary-interaction` does not satisfy Use Object/Utilize and that `use-item` is not treated as magic-item activation. `REQUIRED` because the current representation changes the source action’s action-economy meaning.

### ACT-4 — Preserve XPHB Search/Study advisory task tables (**RECOMMENDED**)

**Evidence:** XPHB Search `$.action[37].entries[1]` maps four Skills to creature state, ailment/cause of death, concealed creature/object, and tracks/food. XPHB Study `$.action[41].entries[2]` maps five Skills to stated areas and creature groups. The current canonical records preserve references to the Skills but discard the table relationships. These tables are guidance, not a mandatory skill-check resolver.

**Proposal:** a small `SKILL_TASK_GUIDANCE` structure with a Skill reference and a finite task/area identifier; Study can include only the source’s named groupings and creature types. Keep it descriptive typed guidance, not an automatic check selection. Example:

```json
{
  "kind": "SKILL_TASK_GUIDANCE",
  "skill": { "targetType": "skill", "id": "skill:xphb:perception" },
  "task": "detect-concealed-creature-or-object"
}
```

**Impact:** additive; 2 XPHB records become fully represented once the check procedure is represented by ACT-2. `RECOMMENDED` because these tables guide task/Skill selection but do not independently resolve outcomes. Validation should compare every source row to one typed mapping and reject mappings not present in source. Risk: task vocabularies could become overlong; keep only observed rows and distinguish the Skills from their task labels.

## Equipment, resources, and damage ownership

| Behavior | Action’s responsibility | Recommended owner / disposition |
|---|---|---|
| Attack count and Extra Attack | One Attack action definition and its attack form | Keep the accepted Action operation; Extra Attack count is granted by Class/Feature (Step 7). Do not add an Action attack-count model solely for this accepted dependency. |
| XPHB weapon equip/unequip | State when one weapon can be equipped/unequipped before/after an attack | Action-side timing/reference may be needed; weapon identity/equippable properties and hand state belong to Equipment (Step 3) and later character inventory. Defer exact state model under ACT-5. |
| Two-Weapon Fighting | Bonus Action attack, relation to same-turn Attack, different weapon, damage modifier exception, PHB Thrown option | Action owns the bonus attack procedure and trigger; Equipment (Step 3) owns Light/Thrown properties and weapon identity; Rule/attack damage owns modifier application. Do not duplicate these properties in Action. |
| Disarm | Attack/check contest and conditional outcome | Action owns the opposed resolution; Equipment/inventory owns the held item drop and hand state. Keep partial until item-state identity exists. |
| Activate an Item | Whether an activation consumes an Action | Action owns an explicit action-cost activation reference. Equipment/Item owns command words, consumption, charges, item modes, and individual exceptions; Spell owns cast/effect behavior. |
| Use an Object/Utilize | Whether the item-use operation costs an Action; distinguish it from ordinary interaction | ACT-3 Action operation; Item describes what the item use does. |
| Shield don/doff | Action-cost shield state transition | Current Action operations represent this; actual shield/equipment identity belongs to Step 3. |
| Healing Surge | Action’s stepwise spend-and-heal procedure | Character resources own level, Hit Dice pool, and Constitution; Rest rules own recovery schedule; Action should later reference these. Defer full mechanics until these contracts exist. |
| Damage adjustment | Action-specific rule excluding nonnegative ability modifier from bonus attack damage | Rule/attack damage value owns arithmetic; Action supplies the circumstance. Preserve as typed Action applicability/reference when Rule/Equipment are ready; do not put damage formulas in Equipment. |
| Object interaction count and placement | PHB free interaction, second-interaction Action cost, and timing during move/action | Turn/action economy belongs with Action/encounter resolution; implement only the explicit one-free/second-cost policy, not general inventory execution. ACT-3 can correct Use Object and optionally expose the bounded count. |

### ACT-5 — Equipment and character-resource dependencies (**DEFER**)

Do not widen Action into inventory or a general resource model. Step 3 Equipment should establish source-backed weapon properties, item identity, and item activation data. Character/feature domains should establish proficiency, hand/equipment state, Hit Dice, ability modifiers, and Extra Attack grants. After those identities are available, define the Action-side references needed for Attack, Two-Weapon Fighting, Disarm, magic items, and Healing Surge. The exact source records are `$.action[0]`, `[1]`, `[2]`, `[7]`, `[19]`, `[43]`, `[44]`, `[45]`, and `[46]`. Keep ACT-3’s action-cost distinction independent. No Action schema change is recommended now for item internals or Hit Dice sequencing.

Healing Surge should remain unsupported rather than use an empty or misleading shell. A later bounded representation would need up to half Hit Dice spent iteratively, each roll plus Constitution modifier, a cumulative healing total, a per-character reuse restriction, and separate short/long rest recovery. Source evidence does not justify a generic `spend-resource` engine.

## Ready and natural sleep boundary

PHB Ready `$.action[34].entries[0..4]` and XPHB Ready `$.action[35].entries[0..3]` share the broad pattern: choose a perceivable trigger, choose an Action or movement response, take the Reaction after the trigger finishes or ignore it, and expire at the beginning of the next turn. The PHB additionally states one Reaction per round and readied spell casting/Concentration replacement; XPHB explicitly says spell resources are expended when readied and that Concentration can be maintained only until the next turn. Those differences should not be normalized away.

A minimum representation would still need a pending response state, a user-selected arbitrary event and action, whether the response was ignored or consumed, Reaction availability, a precise event ordering, expiry, and a Spell/Concentration sub-lifecycle. `PERCEIVABLE_CIRCUMSTANCE` is only a trigger category; it does not encode the response or an event scheduler. **ACT-6 defers Ready** to encounter/action execution and Spell/Status lifecycle review. Preserve the two editions as separate records; do not add generic “if event then action” execution.

XGE Waking Someone `$.action[47].entries[0..1]` is specifically about naturally sleeping creatures and excludes magical or chemical sleep. Damage, shaking/slapping, sudden loud sound, whispers within 10 feet against passive Perception 20, and normal-volume speech in an otherwise silent environment against passive Perception 15 are distinct stimuli. It cannot reference Unconscious: natural sleep is not that Condition. No approved canonical sleep state or sound/environment model exists. **ACT-6 also defers waking** until a sleep state and relevant environmental/perception inputs are defined. Preserve the explicit DCs/distances in research/source accounting; do not make a generic `WAKE` Condition or infer sleep behavior.

## Cross-domain ownership matrix

| Contract/domain | Owns | Action dependency |
|---|---|---|
| Action | Action timing, action-cost category, selected procedure, actor/target roles, action-specific checks, choices and outcomes | Canonical identity and source timing are stable here |
| Rule | General attack/D20 resolution, saving/check rules, difficult terrain and general movement consequences | Action references or instantiates resolved procedures; does not duplicate general Rule mechanics |
| Condition | Grappled/Prone/Incapacitated/Invisible state, application and removal | Use source-qualified references and Action event applicability |
| Status | Concentration and other duration/state lifecycle | End Concentration can name the status; Ready spell dependence stays external |
| Sense | Sight/visibility capability | Hide, Help, Identify Spell, and Opportunity Attack query visibility; do not duplicate vision rules |
| Equipment (Step 3) | Weapon/item identity, Light/Thrown properties, item activation, charges, held/equipped state | Action references eligibility and action-cost use; Disarm’s drop and Attack equipment changes wait for inventory state |
| Spell (Step 4) | Spell casting time, spell identity/effects/components and spell class-origin data | Cast a Spell, Magic, Ready spell, Identify Spell, and item-cast spell dependencies |
| Class/feature (Step 7) | Extra Attack and feature-given attack/resource grants | Keep feature-side attack counts; no Action-owned feature system |
| Character building/resources (Step 8 or approved core-resource model) | Ability scores, Hit Dice pool and character class membership | Healing Surge and ability-modifier damage effects require stable character inputs |
| Encounter/turn execution | Occupied-space checks, movement interruption, Reaction expenditure, initiative order and natural sleep runtime state | Action declares source-defined triggers/permissions; execution ordering remains external |

## Source-fidelity and accounting findings

No canonical files or accounting were modified. The following should be corrected or reviewed in an authorized implementation:

| Finding | Evidence and current representation | Assessment / required treatment |
|---|---|---|
| Use Object / Utilize operation conflation | PHB `$.action[45].entries[0]`, XPHB `$.action[46].entries[0]`; canonical files `action:phb:use-an-object` and `action:xphb:utilize` each say `OBJECT_OPERATION/ordinary-interaction` | **Incorrect mapping.** These actions consume an Action when the item requires one. ACT-3 must add a separate action-cost item-use operation and migrate both. |
| PHB Attack one-attack accounting | PHB `$.action[1].entries[1]`; canonical `action:phb:attack` contains one `ATTACK_RESOLUTION`. The manifest currently marks “Exactly one attack” as `SCHEMA_UNSUPPORTED` together with the separate book procedure. | **Accounting overstates the gap.** One operation is one attack resolution and represents the default one attack. The distinct Making an Attack section and feature-granted Extra Attack remain Rule/Class dependencies. Split the source portion disposition in a future accounting update; no count field is warranted by the approved decision. |
| XPHB Attack single attack accounting | XPHB `$.action[2].entries[0]`; canonical contains one `ATTACK_RESOLUTION`, with separate equipment/movement clauses omitted | **Accounting overstates the gap** for the represented single attack. Keep weapon state and feature movement partial; do not add attack-count model. |
| Shove/Grapple ability–Skill pairing | PHB Shove `$.action[38].entries[1]`, Grapple `$.action[18].entries[1]`; canonical lists the correct paired PHB Skill IDs and ability values separately | **Mechanics appear recoverable from existing Skill ability association.** Retain interpretation and add a validator assertion that source pairing equals referenced Skill ability. Do not count this as a genuine schema gap absent a failing resolution. |
| Escape pairing | PHB/XPHB `$.action[16].entries[0]`, `$.action[17].entries[0]` | Current accounting calls ability/Skill pairs unrepresentable. Pairing may be resolved via typed Skill references as above, but the current check schema does not require this invariant; ACT-2 should add paired options/validation, then reclassify the supported portion. |
| PHB/XPHB Dodge actor direction | PHB/XPHB `$.action[10]`, `$.action[11]`; corresponding canonical modifiers use the affected creature as target and opposing attacker as roll actor | **No new issue found.** Existing direction is source-correct. |
| Shove automatic success | PHB `$.action[38].entries[1]`; PHB canonical has `automaticSuccessWhen` target has PHB Incapacitated | **No new issue found.** Existing typed predicate is source-correct and distinct from the post-success push/Prone choice. |
| PHB Grapple movement | PHB `$.action[18].entries[2]`; canonical `MOVE_WITH_GRAPPLED_TARGET` actor, target, 0.5 Speed, two-size waiver | **No new issue found.** This is voluntary movement by grappler, not forced movement. |
| XPHB Dash speed | XPHB `$.action[6].entries[1]`; canonical uses current-Speed basis only | **Incomplete, correctly classified partial.** ACT-1 should add a per-use selected special Speed basis. |
| Opportunity Attack trigger details | PHB `$.action[30].entries[1..2]`; XPHB `$.action[31].entries[0]`; canonical timing says creature leaves reach | **Incomplete, correctly classified partial.** Existing trigger represents the event but misses source qualification, exception list and immediate-before-departure order. ACT-1/2. |
| XPHB Help stabilization note | XPHB `$.action[21].entries[2]`; canonical has `HELP` and `STABILIZE` capabilities but no stabilization operation | **Partial, not a second independent Action.** The note says Help may be used to stabilize; the PHB Stabilize procedure is not repeated by the note. Avoid duplicate procedure; resolve to an approved XPHB stabilization Rule/Action reference when identified, otherwise document the external dependency. |
| XPHB Hide | XPHB `$.action[23].entries[0..2]`; canonical represents DC 15 Dexterity (Stealth) only | **Incomplete, correctly partial.** Source-supported prerequisites, Invisible application, check-total discovery DC and termination events need ACT-2. |
| Source `fromVariant` metadata | DMG Climb, Disarm, Mark, Overrun, Shove Aside, Tumble and Healing Surge; XGE Identify a Spell carry source grouping metadata | Preserve the grouping in external provenance/accounting. It is editorial/source organization, not itself an Action mechanic; do not add a canonical field unless an approved use case requires it. |

The Phase 5 Action accounting currently has 54 `INTERPRETED`, 6 `DUPLICATE`, and 75 `OMITTED` classification parts across 97 entry roots (45 nested paths). The omitted parts include 67 `SCHEMA_UNSUPPORTED`, 5 `EXTERNAL_DEPENDENCY`, 1 `REFERENCE_UNRESOLVED`, and 2 `NON_MECHANICAL`. This review finds that some `SCHEMA_UNSUPPORTED` portions need reclassification: the default single attack resolution and PHB Shove/Grapple’s ability-to-Skill relationship are already represented or derivable from approved Skill records. Conversely, the two Use Object/Utilize canonical fields are not just incomplete; they encode the wrong operation. Any future accounting update should split mixed source entries rather than shift whole entries between dispositions.

## Coverage forecast

Current reassessed baseline: **13 full, 30 partial, 5 unsupported**. The matrix forecast assumes human approval and complete migration of each affected record, not merely schema acceptance.

| State | Full | Partial | Unsupported | Main promotions |
|---|---:|---:|---:|---|
| Current | 13 | 30 | 5 | None |
| After REQUIRED ACT-1–ACT-3 | 29 | 17 | 2 | Overrun, Tumble, Shove Aside, XPHB Dash, both Opportunity Attacks, both Escape a Grapple, both Help, XPHB Hide, Identify a Spell, Influence, PHB Search, Use an Object, Utilize; Climb becomes partial |
| After REQUIRED and RECOMMENDED ACT-4 | 31 | 15 | 2 | XPHB Search and Study add typed source task tables |

Records expected to remain partial after the proposed work: Activate an Item; PHB/XPHB Attack; Cast a Spell; Climb onto a Bigger Creature; Disarm; PHB Hide; both Improvising an Action records; Other Activity; both Ready records; Stabilize a Creature; both Two-Weapon Fighting records. Equipment, Spell, Rule, character-resource, encounter, or source-specific DM-adjudication dependencies remain. The two expected unsupported records are Healing Surge and Waking Someone; Healing Surge may become partial when a reviewed character-resource contract exists, and Waking Someone requires a natural-sleep/environment model. Neither should receive a metadata-only canonical shell.

The 75 current omitted classification parts are not 75 mechanically recoverable facts: the accounting has grouped nested clauses unevenly and includes external dependencies. The REQUIRED proposals affect about 20 source records with omitted mechanics; ACT-1/2 are expected to recover traversal, selected-Speed, paired-check, action-trigger, Help/Hide/Influence, and Action-outcome portions. ACT-3 repairs two wrongly mapped operations and distinguishes bounded object interaction. ACT-4 addresses the two XPHB task-table records. A reliable exact number of recoverable nested portions requires redoing the entry-level manifest after approval. Conservative estimate: **at least 20 distinct source clauses** could be represented or correctly linked by ACT-1–ACT-4; this counts clauses, not entry roots or records, and should be verified during implementation. Do not treat record-count increases as evidence of semantic correctness.

## Validation recommendations

Keep structural schema validation and source-mechanics tests distinct. For an approved implementation, add focused positive/negative assertions:

- Overrun and Tumble use different ability/Skill pairs; size Advantage/Disadvantage applies to Overrun only; both grant a single successful traversal this turn, not forced movement or shared-space occupancy.
- Climb enters and moves with the larger creature; Shove Aside moves laterally within reach and uses the Shove check with Disadvantage, not the PHB push outcome.
- XPHB Dash can select a special Speed per use; PHB Dash remains based on current Speed after modifiers.
- Opportunity Attack PHB requires hostile visible mover and preserves teleport/Disengage/involuntary movement exceptions; XPHB uses its own source list. Both resolve immediately before departure.
- Each opposed ability/Skill option is paired; referenced Skill ability matches the declared ability. Wrong pairings and missing option selections fail.
- Disarm models attack-versus-check rather than check-versus-check, no damage on win, and Equipment-state handoff.
- Help has exactly one selected option and correct ally, task/enemy, range, next-check/first-attack, and expiry roles.
- Hide prerequisites, check result, Invisible state, discovery DC, and each supported ending event match XPHB source; PHB Hide remains tied to its external rule reference.
- Identify Spell action/reaction windows, perception prerequisite, DC formula, ability, and conditional Advantage are distinct; Influence willing/unwilling branches do not request checks, and only hesitant branch does.
- Use Object/Utilize use `use-item`, never `ordinary-interaction`; the latter remains for free incidental interaction.
- Source-entry accounting splits represented clauses from unresolved external dependencies; no entire entry is classified omitted when a supported part is canonical.
- Preserve uniqueness, source metadata, edition-qualified references, composition/choice integrity, and no source prose in canonical JSON.

Current permanent validation checks structure and selected Action assertions; they do not establish source fidelity for all 48 records. Approval of any proposal should require new assertions tied to exact source JSONPaths and review of updated omission registers.

## Decisions requiring human review

1. Approve or decline ACT-1’s distinct creature-space operations and selected-speed field; confirm Action versus encounter/Rule ownership for occupancy and interruption.
2. Approve or decline ACT-2’s paired check options, attack-versus-check resolution, bounded action predicates/outcomes, and two finite DC formula types. Confirm the precise bounded Hide and Influence branch vocabulary.
3. Approve ACT-3’s correction distinguishing action-cost item use from ordinary interaction, and whether the PHB one-free/second-object interaction count belongs in the same contract change.
4. Approve or defer ACT-4’s task-guidance representation for XPHB Search and Study; it is advisory source information rather than a check resolver.
5. Confirm ACT-5 equipment/resource handoffs and ACT-6 Ready/natural-sleep deferrals; neither is implemented here.
6. Accept the current 13/30/5 baseline provisionally, and authorize correction of the Action source-accounting portions that overstate the Attack and Skill/ability gaps during a later implementation task.

No schema or canonical change is proposed as already approved. Step 2 Phase 5 remains in progress and awaiting human review; Phase 6 has not started.
