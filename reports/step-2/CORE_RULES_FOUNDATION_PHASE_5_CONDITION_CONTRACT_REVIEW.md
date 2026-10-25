# Core Rules Foundation Phase 5 Condition Contract Review

## Executive summary

The approved Condition schema accurately handles sense removal, sensory ability-check failure, unconditional attack modifiers, simple roll modifiers, several movement values, and Exhaustion's level ladder. It does not have a condition-active applicability model for ordinary conditions, conditional creature-to-creature predicates, Condition-to-Condition application/immunity, or several specific outcomes (automatic saving-throw failure, item drop, critical-hit conversion, and damage resistance/immunity).

The Phase 5 accounting currently classifies the 30 PHB/XPHB Condition records as **8 fully represented, 14 partial, and 8 unsupported**. This is a structural/accounting status, not proof of semantic correctness. This audit found a likely actor/target error in the approved XPHB Blinded example and representative canonical record. No fixture, schema, canonical record, accounting, or validation code was changed.

The smallest useful refinement is a set of Condition-specific capabilities: active restrictions; source-relative applicability; automatic saving-throw failure; condition application/immunity with explicit persistence; conditional movement and attack outcomes; and a bounded set of body-state/damage interactions. Concentration termination is already modeled by Status and should not be duplicated in Condition. Critical-hit conversion and condition-specific initiative modifiers need coordinated Rule-domain consideration.

## Scope and evidence

Inspected the 30 records in `src/main/resources/data/conditionsdiseases.json` at `$.condition[0]` through `$.condition[29]`, including nested XPHB entries and the PHB Exhaustion table. The records contain **50 top-level entry roots**. Four records are in the approved Phase 3 baseline; 26 are in the Phase 5 expansion manifest. All 30 records have been compared to the Condition v1 schema, canonical records or current unsupported classifications, and the Phase 2 Condition fixtures. The complete omission and duplicate registers were reviewed.

The audit also inspected shared actor/predicate/reference definitions and the relevant Action, Rule, Status, and Sense structures. Action has typed condition predicates and APPLY/END_CONDITION operations, but those describe an Action procedure, not the standing effects of a Condition. Status already ends Concentration on Incapacitation. Sense has a narrow Condition interaction for sight restrictions. Rule has attack/save procedures and critical-hit behavior, but no Condition-scoped attack outcome applicability. These structures are useful dependencies; none is a general substitute for Condition effects.

## Complete 30-record audit

Paths below are source locators. For XPHB records, the mechanics are under `entries[1].entries[*].entries[0]`; `entries[0]` is an introductory heading unless otherwise called out.

| # | Source record and source path | Current state | Source mechanics and audit finding |
|---:|---|---|---|
| 0 | Blinded PHB — `$.condition[0]`, `entries[0].items[0..1]` | Fully represented | Cannot see; automatic failure of sight-dependent ability checks; attacks against the creature have Advantage; its attacks have Disadvantage. All are represented. |
| 1 | Blinded XPHB — `$.condition[1]`, `entries[1].entries[0..1].entries[0]` | Fully represented; role concern | Same four mechanics as PHB. Current outgoing modifier uses `target: "attacker"`; source says the affected creature's attack roll has Disadvantage, so the attacked creature is the attack's target. Review the role and likely use the generic `target` role, matching the PHB fixture. This is a data correction, not a schema gap. |
| 2 | Charmed PHB — `$.condition[2]`, `entries[0].items[0..1]` | Unsupported | The affected creature cannot attack or target its charmer with harmful abilities or magical effects; the charmer has Advantage on ability checks to interact socially with the affected creature. The contract lacks the dynamic charmer role, prohibited target operation, and social-check scope. |
| 3 | Charmed XPHB — `$.condition[3]`, `entries[1].entries[0..1].entries[0]` | Unsupported | Same relationship, but source wording limits abilities to damaging abilities and separately names magical effects. Preserve that distinction from PHB's “harmful” wording. The charmer has Advantage on social checks with the affected creature. |
| 4 | Deafened PHB — `$.condition[4]`, `entries[0].items[0]` | Fully represented | Cannot hear; automatically fails hearing-dependent ability checks. Both effects are represented. |
| 5 | Deafened XPHB — `$.condition[5]`, `entries[1].entries[0].entries[0]` | Fully represented | Same mechanical effects as PHB. The opening sentence is nonmechanical framing. |
| 6 | Exhaustion PHB — `$.condition[6]`, table at `entries[1].rows[0..5]`; other clauses at `entries[2..5]` | Fully represented | Six cumulative levels; effects at levels 1–6 (ability-check Disadvantage, half Speed, attack/save Disadvantage, half HP maximum, Speed 0, death); specified-level application; reduction below 1 ends effects; Long Rest removes one level only with food and drink; raised-from-dead removes one. The approved level and effect structures preserve this 2014 ladder. Introductory source prose is not an additional procedure. |
| 7 | Exhaustion XPHB — `$.condition[7]`, `entries[1].entries[0..3].entries[0]` | Fully represented | Cumulative one-level additions, death at level 6, D20 Test penalty of 2 per level, Speed reduction of 5 feet per level, and one level removed on Long Rest with the condition ending at zero. Correctly differs from PHB. |
| 8 | Frightened PHB — `$.condition[8]`, `entries[0].items[0..1]` | Unsupported | Disadvantage on checks and attacks only while the fear source is in line of sight; the affected creature cannot willingly move closer to that source. The current modifier has no source-relative visibility predicate, and movement has no directional/source relation. |
| 9 | Frightened XPHB — `$.condition[9]`, `entries[1].entries[0..1].entries[0]` | Unsupported | Same two mechanics as PHB. Preserve the explicit line-of-sight gate and willing-movement restriction. |
| 10 | Grappled PHB — `$.condition[10]`, `entries[0].items[0..2]` | Partial | Speed becomes 0; cannot benefit from Speed bonuses; ends if grappler is Incapacitated or the target is removed from the grappler/effect's reach. Only Speed 0 is represented. Action's PHB Grapple operation separately represents dragging/carrying at half Speed except when the target is at least two sizes smaller; that does not express the Condition's end predicates or bonus suppression. |
| 11 | Grappled XPHB — `$.condition[11]`, `entries[1].entries[0..2].entries[0]` | Partial | Speed 0 and cannot increase; Disadvantage on attacks against anyone except the grappler; grappler drags/carries with one extra foot of movement per foot, waived for Tiny or a target at least two sizes smaller. Only Speed 0 is represented. This differs from PHB's grappler half-Speed rule and Tiny exception. |
| 12 | Incapacitated PHB — `$.condition[12]`, `entries[0].items[0]` | Unsupported | Cannot take Actions or Reactions. `RESTRICTION` has related values but requires turn/level applicability that cannot state “while this Condition is active” for all action timings. |
| 13 | Incapacitated XPHB — `$.condition[13]`, `entries[1].entries[0..3].entries[0]` | Unsupported | Cannot take Actions, Bonus Actions, or Reactions; Concentration is broken; cannot speak; has Disadvantage on Initiative if rolling it while Incapacitated. The Concentration consequence is already represented in Status and should not be duplicated. Bonus Action, speech, and initiative applicability are absent from the Condition contract. |
| 14 | Invisible PHB — `$.condition[14]`, `entries[0].items[0..1]` | Unsupported | Cannot be seen without magic/special sense; treated as heavily obscured for hiding; noise or tracks can reveal location; attacks against it have Disadvantage and its attacks Advantage. No observer-specific visibility or concealment/location capability is available. |
| 15 | Invisible XPHB — `$.condition[15]`, `entries[1].entries[0..2].entries[0]` | Unsupported | Advantage on Initiative; effects requiring the target to be seen fail unless their creator can see it; carried/worn equipment is concealed; attack modifiers apply except against a creature that can see it. These are distinct from PHB's hiding/location wording and require conditional targeting and observer-relative applicability. |
| 16 | Paralyzed PHB — `$.condition[16]`, `entries[0].items[0..3]` | Partial | Incapacitated; cannot move or speak; automatically fails Strength and Dexterity saves; attacks against it have Advantage; a hit by an attacker within 5 feet is a Critical Hit. Speed 0 and incoming Advantage are represented; other effects are not. |
| 17 | Paralyzed XPHB — `$.condition[17]`, `entries[1].entries[0..4].entries[0]` | Partial | Has Incapacitated; Speed 0; automatically fails Strength/Dexterity saves; incoming attacks have Advantage; hits by an attacker within 5 feet are Critical Hits. Current output represents Speed 0 and incoming Advantage only. |
| 18 | Petrified PHB — `$.condition[18]`, `entries[0].items[0..5]` | Partial | Becomes inanimate substance with nonmagical worn/carried items; weight ×10; stops aging; Incapacitated, unable to move/speak and unaware; incoming attacks Advantage; automatic Strength/Dexterity save failure; resistance to all damage; immune to poison and disease, with existing poison/disease suspended. Only Speed 0 and incoming Advantage are represented. |
| 19 | Petrified XPHB — `$.condition[19]`, `entries[1].entries[0..6].entries[0]` | Partial | Inanimate transformation, weight ×10, stops aging; Incapacitated; Speed 0; incoming attacks Advantage; automatic Strength/Dexterity save failure; resistance to all damage; immunity to the Poisoned Condition. It does not state PHB's disease immunity/suspension, so do not copy that mechanic into XPHB. Current output represents Speed 0 and incoming Advantage. |
| 20 | Poisoned PHB — `$.condition[20]`, `entries[0].items[0]` | Fully represented | Disadvantage on attacks and ability checks. Current schema-valid representation uses `CHECK_ROLL_MODIFIER` with `roll: "attack"` for the attack component and a second check modifier. This is expressible; a later consistency decision could use the dedicated attack modifier form. |
| 21 | Poisoned XPHB — `$.condition[21]`, `entries[1].entries[0].entries[0]` | Fully represented | Same two modifiers as PHB. Opening source text is nonmechanical framing. Preserve separate source identity. |
| 22 | Prone PHB — `$.condition[22]`, `entries[0].items[0..2]` | Partial | Only movement option is crawling until standing ends the Condition; attacks by the creature have Disadvantage; incoming attacks have Advantage within 5 feet and Disadvantage otherwise. Only the creature's attack Disadvantage is represented. |
| 23 | Prone XPHB — `$.condition[23]`, `entries[1].entries[0..1].entries[0]` | Partial | Crawl or spend half Speed rounded down to right itself/end Condition; cannot right itself at Speed 0; own attacks Disadvantage; incoming attack modifier switches at 5 feet. Only own attack Disadvantage is represented. PHB does not specify the XPHB standing cost in its Condition entry. |
| 24 | Restrained PHB — `$.condition[24]`, `entries[0].items[0..2]` | Partial | Speed 0 and cannot benefit from Speed bonuses; incoming attacks Advantage; own attacks Disadvantage; Dexterity saves Disadvantage. All except Speed-bonus suppression are represented. |
| 25 | Restrained XPHB — `$.condition[25]`, `entries[1].entries[0..2].entries[0]` | Partial | Speed 0 and cannot increase; incoming attacks Advantage; own attacks Disadvantage; Dexterity saves Disadvantage. Current output represents the modifiers and zero Speed, but not the no-increase rule. |
| 26 | Stunned PHB — `$.condition[26]`, `entries[0].items[0..2]` | Partial | Incapacitated; cannot move; can speak only falteringly; automatically fails Strength/Dexterity saves; incoming attacks Advantage. Current output represents Speed 0 and incoming Advantage only. “Falteringly” is a source-specific speech limitation, not equivalent to fully unable to speak. |
| 27 | Stunned XPHB — `$.condition[27]`, `entries[1].entries[0..2].entries[0]` | Partial | Incapacitated; automatically fails Strength/Dexterity saves; incoming attacks Advantage. No Speed or speech restriction is stated in this edition's Condition entry. Current output represents incoming Advantage only. |
| 28 | Unconscious PHB — `$.condition[28]`, `entries[0].items[0..4]` | Partial | Incapacitated; cannot move/speak; unaware; drops held objects and falls Prone; automatically fails Strength/Dexterity saves; incoming attacks Advantage; hits within 5 feet are Critical Hits. Current output represents Speed 0 and incoming Advantage only. |
| 29 | Unconscious XPHB — `$.condition[29]`, `entries[1].entries[0..5].entries[0]` | Partial | Incapacitated and Prone; drops held objects; remains Prone when Unconscious ends; Speed 0 and cannot increase; incoming attacks Advantage; automatic Strength/Dexterity save failure; 5-foot hit becomes Critical; unaware. Current output represents Speed 0 and incoming Advantage. The explicit persistence of Prone is edition-specific. |

## Reusable mechanics taxonomy

| Capability family | Source evidence | Current representation | Boundary/reuse assessment |
|---|---|---|---|
| Active activity restrictions | Incapacitated PHB/XPHB; also Paralyzed, Stunned, Petrified, Unconscious | `RESTRICTION` contains some values but its applicability enum is turn/level-oriented; it cannot mean continuously while an ordinary Condition is active. | A Condition-specific activity restriction is justified. Keep Action, Bonus Action, Reaction, speech, and movement as explicit activities. Refer to Status for Concentration ending rather than duplicating it. |
| Creature-relative targeting and harm restrictions | Charmed PHB/XPHB; Invisible XPHB target-seen rule | No Condition effect can say “this creature, relative to its charmer/observer, may not target that creature/effect.” Action has predicates, but they are bound to Action operations and omit these source relationships. | Use typed dynamic roles and a small set of target operations/categories. Preserve `harmful` (PHB) versus `damaging` (XPHB). Do not add a generic expression tree. |
| Perception-gated applicability | Frightened line of sight; Invisible exceptions when the observer can see the affected creature | Sense has a narrow `CONDITION_INTERACTION` for blindness; Action has `CAN_SEE` but Condition effects cannot use it. | A Condition-specific `appliesWhen` relation is needed. References to a canonical Sense can be used only for a named capability; “magic or a special sense” does not identify a concrete Sense record. |
| Conditional roll modifiers | Frightened modifiers only while fear source is visible; Charmed social-check Advantage; Invisible attack exception; Prone 5-foot bands | `ATTACK_ROLL_MODIFIER` and `CHECK_ROLL_MODIFIER` have no general applicability field other than the attack branch's fixed two cases. Neither can express these predicates. | Add a narrow, discriminated Condition applicability structure. Keep roll kind, direction, actor, target, and trigger separate. Avoid an unrestricted predicate language. |
| Automatic failures | Strength/Dexterity saving throws under Paralyzed, Petrified, Stunned, Unconscious | `AUTOMATIC_CHECK_FAILURE` is restricted to sensory ability checks. | Add a typed saving-throw failure form with exact abilities and affected actor. Do not reuse Status maintenance saves as the Condition outcome. |
| Condition composition, immunity, and persistence | Incapacitated inclusion; Unconscious also applies Prone; Petrified immunity; XPHB Unconscious retains Prone after ending | `references` can name a Condition but cannot state that it is applied, immune, or persists independently. Action can apply/end a Condition as an Action outcome only. | Add Condition-specific typed interactions with explicit lifecycle (while source Condition holds versus independent/persistent). Avoid assuming every included Condition ends together. Preserve XPHB Unconscious's explicit Prone persistence. |
| Movement restrictions and outcomes | Grappled, Frightened, Prone, Restrained, Paralyzed, Petrified, Stunned, Unconscious | Speed 0/fraction exists; no increase suppression, crawl-only, stand-and-end, cannot approach, or source-relative speed cost. | Add typed movement constraints and termination outcomes. Grappler movement remains an Action/Rule responsibility where it is an operation by the grappler; do not misstate it as forced movement. Preserve PHB half-Speed versus XPHB extra-foot cost and size exceptions. |
| Attack outcome modification | Paralyzed and Unconscious convert hits within 5 feet to Critical Hits; Prone uses attacker distance for Advantage | Condition can modify attack rolls but not hit outcomes. Rule has critical-hit/damage mechanics, but Condition cannot add a conditional critical result. | Requires coordination with Rule's attack-resolution/critical-result contract. A Condition-specific rule should specify “hit” and the exact distance, and refer to—not duplicate—the Rule critical procedure. |
| Damage and physical state | Petrified resistance, immunity, transformation, weight, aging, awareness; Unconscious dropped objects and awareness | No Condition mechanics for damage resistance/immunity, suspended diseases, physical state, dropped equipment, or awareness. | Add only concrete domain-specific outcomes supported by these records. Disease suspension crosses the Disease lifecycle contract; item dropping is an equipment-state outcome and should be coordinated with Action/Item semantics. |

## Prioritized contract proposals

All examples below are illustrative proposal fragments, not approved schema shapes. They intentionally use candidate discriminators to show the needed distinctions; final enum names and exact shapes require human approval.

### CND-1 — Activity restrictions while a Condition is active (`REQUIRED`)

**Affected:** Incapacitated PHB/XPHB (`$.condition[12..13]`); related restrictions in Paralyzed, Stunned, Petrified, and Unconscious.

The existing `RESTRICTION` cannot express continuous, Condition-duration restrictions: its `appliesWhen`/`duration` contract is tied to turn/level states. An additive Condition-specific `ACTIVITY_RESTRICTION` could list established activity categories (`ACTION`, `BONUS_ACTION`, `REACTION`, `SPEECH`, and narrowly scoped movement) and apply while the owning Condition remains active. PHB Incapacitated has Action and Reaction only; XPHB adds Bonus Action and speech prohibition. XPHB's Initiative modifier remains a roll modifier, not an activity restriction. Concentration ending remains owned by Status.

```json
// Proposed Condition effect
{
  "kind": "ACTIVITY_RESTRICTION",
  "actor": "affected-creature",
  "activities": ["ACTION", "REACTION"],
  "appliesWhile": { "kind": "CONDITION_ACTIVE" }
}
```

Additive branch; existing records need no migration. Tests must prove PHB does not prohibit Bonus Actions by inference, while XPHB does.

### CND-2 — Dynamic creature roles and targeting restrictions (`REQUIRED`)

**Affected:** Charmed PHB/XPHB and Invisible XPHB; potentially Invisible PHB's observer relation. Current paths: `$.condition[2..3]` and `$.condition[14..15]`.

The source-defined charmer and observer are roles established by the Condition, not stable canonical creature IDs. A Condition-specific target restriction should express the affected actor, the relationship role, and exact prohibited target operation. Preserve PHB `harmful` versus XPHB `damaging`; do not normalize those terms to one broader category without rules evidence. XPHB Invisible also restricts target-required-to-be-seen effects unless that effect's creator can see the target. The relation must identify creator/observer separately from the affected creature.

```json
// Proposed PHB Charmed effect; XPHB uses a distinct DAMAGING category
{
  "kind": "TARGETING_RESTRICTION",
  "actor": "affected-creature",
  "targetRole": "charmer",
  "prohibitedOperations": ["ATTACK", "TARGET_HARMFUL_ABILITY", "TARGET_MAGICAL_EFFECT"]
}
```

Additive Condition-specific structure. No stable creature reference is invented. Regression tests must ensure the restriction is against the charmer only and that the two editions retain their source wording distinction. Spell-target enforcement itself remains a Spell-domain integration concern.

### CND-3 — Relational applicability for Condition roll modifiers (`REQUIRED`)

**Affected:** Frightened PHB/XPHB, Charmed social Advantage, Invisible XPHB attack exception, and Prone PHB/XPHB distance bands; current paths `$.condition[8..9]`, `[2..3]`, `[15]`, and `[22..23]`.

Current roll-modifier branches lack `appliesWhen`, except a fixed attack target/actor enum. A small Condition-owned discriminator can represent only observed relations: source in line of sight, observer can see the affected creature, social check by the charmer against the affected creature, and attacker within/over five feet. This is not a general boolean expression tree.

```json
// Proposed incoming attack modifier for PHB Prone
{
  "kind": "ATTACK_ROLL_MODIFIER",
  "roll": "attack",
  "direction": "advantage",
  "actor": "attacker",
  "target": "affected-creature",
  "appliesWhen": {
    "kind": "ACTOR_WITHIN_DISTANCE_OF_AFFECTED",
    "distance": { "amount": 5, "unit": "feet" }
  }
}
```

The matching Disadvantage branch must explicitly apply beyond five feet. A separate Fear-source-visible predicate governs Frightened modifiers. Additive to Condition effect definitions; a runtime resolver must preserve actor/target direction. Where a relation depends on a dynamic charmer/fear source, store that role, not an invented ID.

### CND-4 — Automatic saving-throw failure (`REQUIRED`)

**Affected:** Paralyzed, Petrified, Stunned, and Unconscious in both editions: eight records, all currently partial. Paths `$.condition[16..19]`, `[26..29]`.

`AUTOMATIC_CHECK_FAILURE` only permits ability checks for sensory requirements. It cannot express automatic failure on specific Strength and Dexterity saving throws. Add a distinct roll-failure structure or a carefully discriminated extension that keeps ability checks and saves unambiguous.

```json
{
  "kind": "AUTOMATIC_ROLL_FAILURE",
  "roll": "saving-throw",
  "abilities": ["strength", "dexterity"],
  "actor": "affected-creature"
}
```

Additive. Tests must reject applying this to unrelated saves, assert both abilities for each cited Condition, and preserve source edition presence/absence.

### CND-5 — Condition application, immunity, and persistence (`REQUIRED`)

**Affected:** Paralyzed, Petrified, Stunned, and Unconscious (eight partial records); especially XPHB Unconscious at `$.condition[29].entries[1].entries[0].entries[0]`.

Generic `references` identify a Condition but do not assert application or immunity. Add explicit Condition-specific interaction variants for applying another Condition, immunity to a Condition, and any narrowly justified suspension. Each application needs lifecycle semantics. XPHB Unconscious applies Incapacitated and Prone, but explicitly says Prone remains when Unconscious ends; a single blanket “while source Condition active” would be wrong.

```json
{
  "kind": "APPLY_CONDITION",
  "condition": { "targetType": "condition", "id": "condition:xphb:prone" },
  "lifecycle": "PERSISTS_AFTER_SOURCE_CONDITION_ENDS"
}
```

Additive Condition branch. Exact interactions need source-specific tests. PHB Petrified's poison/disease suspension is not a generic Condition immunity: disease suspension requires a Disease lifecycle decision. XPHB Petrified explicitly grants Poisoned-condition immunity and does not state PHB disease suspension.

### CND-6 — Conditional movement rules and ending outcomes (`RECOMMENDED`)

**Affected:** Grappled both editions, Prone both, Restrained both; plus Frightened via CND-3. Paths `$.condition[10..11]`, `[22..25]`.

Extend the existing movement effect family only with observed variants: Speed cannot increase; only crawl; spend a specified movement amount to end a Condition; cannot willingly move closer to a condition source; and relational drag/carry cost. A separate ending outcome should distinguish an externally ended Condition (grappler incapacitated or target moved out of reach) from the Prone creature's own stand-up choice.

```json
{
  "kind": "MOVEMENT_RESTRICTION",
  "actor": "affected-creature",
  "restriction": "CANNOT_INCREASE_SPEED",
  "appliesWhile": { "kind": "CONDITION_ACTIVE" }
}
```

Additive Condition proposal. XPHB Grappled's movement cost is incurred by the grappler and may require an Action/Rule-side operation reference; the Condition must not present it as forced movement. Tests must keep PHB half-speed/size exception distinct from XPHB +1 foot per foot/Tiny-or-two-sizes exception. PHB Prone does not state XPHB's half-Speed standing cost.

### CND-7 — Critical-hit outcome from a Condition (`REQUIRED`; cross-domain approval)

**Affected:** Paralyzed and Unconscious PHB/XPHB, four records. Paths `$.condition[16..17]`, `[28..29]`.

These entries make a hit a Critical Hit when the attacker is within five feet. Condition's current attack modifier cannot change a hit outcome. Rule already owns attack resolution and Critical Hit behavior; a condition-specific critical-hit applicability must connect to that procedure without duplicating damage definitions. This requires a coordinated Rule contract change (and likely Rule-integrity assertions), so it must not be implemented under a Condition-only approval.

```json
{
  "kind": "CRITICAL_HIT_ON_HIT",
  "actor": "attacker",
  "target": "affected-creature",
  "when": { "kind": "WITHIN_DISTANCE", "amount": 5, "unit": "feet" }
}
```

The attack must first hit; distance is measured between attacker and affected creature. Regression tests must ensure a miss is never upgraded to a hit or critical hit.

### CND-8 — Petrified and Unconscious physical-state consequences (`RECOMMENDED`)

**Affected:** Petrified both editions, Unconscious both editions. Paths `$.condition[18..19]`, `[28..29]`.

Potential typed outcomes include inanimate transformation, weight multiplier, aging cessation, awareness restriction, held-object drop, damage resistance, and immunity. Keep distinct source details: PHB Petrified has poison and disease immunity plus suspension; XPHB specifies Poisoned-condition immunity only. This set has lower reuse than CND-1–7 and should be implemented only as bounded Condition outcomes, not a universal effects list. Damage mitigation could require coordination with Rule/Damage semantics; held-object changes may require Action/Item semantics. Disease suspension requires Disease contract approval.

### CND-9 — Preserve approved Exhaustion and basic sensory mechanics (`NO_CHANGE`)

**Affected:** both Exhaustion records, both Deafened records, and most Blinded mechanics. Existing level ladders and effect direction are supported. No schema expansion is warranted for these mechanics. The XPHB Blinded target role is a source-fidelity correction candidate, not a contract gap; see review decision below.

## Expected coverage impact

| Proposal set | Records affected | Conditional expected result if approved and implemented |
|---|---:|---|
| CND-1 through CND-5 | 6 unsupported (Charmed, Incapacitated, Invisible) plus 8 partial (Paralyzed, Petrified, Stunned, Unconscious) | All eight unsupported records gain supported typed mechanics; their remaining source portions determine whether they become full or stay partial. The eight save-failure records gain their missing save outcomes. |
| CND-3, CND-5, CND-6 | Frightened 2 plus Grappled 2, Prone 2, Restrained 2; overlaps CND-1 | The two Frightened records become representable. Up to eight currently partial movement/relationship records can gain their missing conditional behavior; exact full conversion depends on their ending and cross-domain clauses. |
| CND-7 | 4 partial | Adds the missing source-defined critical-hit outcome only if Rule approves a coordinated contract change. |
| CND-8 plus Disease/Action/Item/Rule coordination | 4 partial | Could represent additional physical-state, mitigation, and drop behavior; PHB disease suspension remains dependent on Disease lifecycle support. |

The conservative combined expectation is **0 unsupported records after CND-1–5**, with all eight converting to at least partial records. Approximately **6–12 of the current 14 partial records could become fully represented** if the movement, Condition interaction, critical-hit, and state proposals are approved; the remaining **2–8 would remain partial** where Disease/Spell/Item/initiative dependencies or source wording need separate modeling. These are conditional design estimates, not measured coverage. No increase is counted merely because a record could receive a canonical shell.

The current baseline remains 8 full / 14 partial / 8 unsupported until human approval and actual conversion. The XPHB Blinded role concern means source-fidelity review is warranted independently of those totals.

## Cross-domain dependencies and deferrals

- **Status:** Concentration already ends on Incapacitated. Do not add a duplicate end-status operation to Incapacitated.
- **Action:** Action's `CAN_SEE`, condition predicates, and APPLY/END_CONDITION operations describe Action operation applicability/outcomes. They do not define a Condition's ongoing effect. The PHB Grapple operation's half-Speed drag is not equivalent to the XPHB Condition's extra movement cost.
- **Rule:** Critical-hit-on-hit requires changing Rule attack resolution to reference a Condition-gated result. Rule's current critical damage modifier is not sufficient by itself.
- **Sense:** Sense can model the narrow “unaffected by Blinded” interaction. It does not resolve the generic PHB Invisible exception for magic/special senses or reveal creature location from sound/tracks. Do not invent a named Sense reference.
- **Spell:** XPHB Invisible's seen-target restriction depends on the effect creator's perception. Condition can state the typed rule; Spell targeting must consume it in a later approved design.
- **Disease:** PHB Petrified suspends a poison or disease already in the creature. Disease progression needs a suspend/resume lifecycle tied to Condition state; it is not equivalent to immunity. This requires Disease schema review.
- **Item/equipment:** Unconscious drops held objects. A Condition outcome can declare the transition, but equipment state ownership must be agreed with Action/Item modeling.
- **Initiative/encounter:** XPHB Incapacitated and Invisible grant Initiative Disadvantage/Advantage respectively. A Condition-local Initiative roll modifier can encode the fact, while encounter execution remains a future domain dependency. Do not move encounter setup into Condition.
- **Source references:** The explicit Condition links in these entries refer to one of the 30 source records and can use source-qualified Condition IDs. The dynamic “charmer,” “fear source,” and “observer” are roles, not canonical entities.

## Validation coverage review

The permanent workflow currently validates the schemas, examples, source identities/metadata, typed references, entry locators, nested paths, and omission/duplicate register presence. Its Condition assertions check a subset of Deafened, Grappled, Paralyzed, Restrained, and unsupported Frightened/Invisible outputs. Phase 2 fixture-correction checks verify selected source strings and citations. Current validation result observed during this review: 9 schemas compiled, 36 examples, 34 Phase 3/4 records, 65 Phase 5 records, and 190 permanent checks passed.

Structural validation does **not** establish that every source clause is represented. In particular it does not catch the XPHB Blinded target-role concern, omitted mechanics in the 14 partial records, missing-condition composition, automatic save failures, conditional distance/visibility modifiers, or source-edition overgeneralization.

Recommended source-backed assertions after any approved implementation:

1. For every Condition, assert that all source entries and nested clauses have exactly one or more manifest classifications and every schema-unsupported mechanic is linked to its complete source entry.
2. Assert each modifier's actor and target roles; specifically distinguish “attacks against affected creature” from “affected creature attacks a target.” Add the XPHB Blinded case.
3. Assert PHB/XPHB versions independently: Charmed harmful/damaging wording, Exhaustion ladders, Grappled movement rules, Incapacitated activity lists, Prone standing cost, Petrified poison/disease consequences, and XPHB Unconscious Prone persistence.
4. Assert condition-gated applicability for fear-source line of sight, charmer/social checks, observer visibility, and Prone's within/over-five-foot branches.
5. Assert automatic Strength/Dexterity saving-throw failure only for the cited conditions; do not apply it to all saving throws.
6. Assert explicit applied Condition references and lifecycle; verify XPHB Unconscious leaves Prone after Unconscious ends.
7. Assert the five-foot Critical Hit only follows a hit and uses the source-defined attacker distance.
8. Keep Disease suspension, Concentration termination, spell target visibility, and item dropping in their owning-domain integration tests, while checking Condition references at the boundary.

These are semantic regression assertions. Schema validation should separately reject malformed discriminators, roles, ranges, Condition references, and incompatible operations.

## Human decisions required before implementation

1. Approve or reject CND-1 through CND-8 individually; CND-7 requires Rule schema approval, and CND-8 has Disease/Item/Rule dependencies.
2. Decide whether condition-source roles (`charmer`, `fear-source`) are modeled as a compact typed role reference or as explicit per-mechanic fields. Do not give dynamic roles canonical IDs.
3. Decide how to represent PHB Invisible's “magic or special sense” and noise/track location detection without claiming a specific Sense or Perception procedure.
4. Decide whether PHB Petrified disease suspension is in scope for the Condition contract or requires an approved Disease lifecycle extension.
5. Review the XPHB Blinded modifier target: current fixture and canonical record use `target: "attacker"` for the affected creature's outgoing attack Disadvantage. The source wording points to the creature it attacks; the PHB counterpart uses `target: "target"`. Decide whether to authorize this minimal data correction in the next implementation task. No change was made here.
6. Decide whether Poisoned's `CHECK_ROLL_MODIFIER` with `roll: "attack"` remains accepted or should be migrated to the dedicated `ATTACK_ROLL_MODIFIER` branch for consistency. Existing schema accepts the current representation; this is not a coverage blocker.

No schema or canonical changes are implemented by this report. Phase 5 expansion remains in progress and Phase 6 remains unauthorized.
