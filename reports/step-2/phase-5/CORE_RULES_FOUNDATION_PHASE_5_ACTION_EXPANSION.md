# Step 2 Core Rules Foundation — Phase 5 Action Coverage and Expansion

> Historical baseline: findings below describe the contracts before the authorized ACT-1–ACT-4 implementation. Current classifications, contracts, accounting, and validation are superseded by the [Action implementation report](CORE_RULES_FOUNDATION_PHASE_5_ACTION_IMPLEMENTATION.md).

## Status and scope

Phase 5 remains in progress and awaits human review. This report covers the complete 48-record Action source catalog. The six Phase 3/4 representatives and ten previously expanded Phase 5 records were retained; the 32 remaining records were newly interpreted. No schema, source file, or other domain record was changed.

Codex directly inspected the original Action objects in `src/main/resources/data/actions.json`, including nested entries and structured metadata, and compared them with the complete approved Action and shared schemas, examples, existing canonical records, and Phase 2 research. Inventory and validation tooling was used only for deterministic lookup, path/count bookkeeping, schema validation, reference checks, formatting, and register synchronization. No semantic converter was used.

The full source catalog contains 8 DMG, 20 PHB, 18 XPHB, and 2 XGE records. Phase 3/4 accounts for 6 records; Phase 5 now accounts for the other 42. The new batch examined 32 records and produced 27 canonical records: 2 fully represented and 25 partial. Five remain unsupported without canonical shells. Across all 48 Action records, 13 are fully represented, 30 partial, and 5 unsupported.

## Approved contract coverage

The Phase 2 research identified overlapping families, counted by records containing a family rather than as mutually exclusive categories:

| Observed family | Source occurrence count | Current approved representation | Main coverage limit |
|---|---:|---|---|
| Attack resolution and variants | 13 | Attack capabilities, `ATTACK_RESOLUTION`, shared `ATTACK_ROLL`, and typed reaction timing | Compound attack/check procedures, attack count, damage adjustments, and several variant outcomes are not encoded. |
| Checks and contests | 19 records across 15 activities | `CHECK_OR_CONTEST`, ability/skill references, DCs, opposing checks, and check-success applicability | Dynamic DCs, paired skill/ability options, task-specific outcomes, and some cross-operation dependencies remain unavailable. |
| Roll modifiers | 9 records; 16 directionally distinct outcomes | Reusable typed `ROLL_MODIFIER` with roll, direction, actor, target, trigger, duration, and usage restrictions | Help beneficiary roles, size predicates, and several conditional roll relationships do not match the finite trigger/predicate vocabulary. |
| Movement and positioning | 16 | Speed increase, movement permissions, forced movement, triggered movement, and Grappled-target drag/carry | Hostile-space traversal, creature-space positioning, lateral displacement, and interruption/order details remain unsupported. |
| Condition/Status transitions | 10 | Typed Condition application/ending and Status ending | Stability/death-save state, natural sleep, and several condition lifecycles belong to other or future state models. |
| Object/equipment interactions | 6 | Ordinary interaction, shield state, and magic-item activation operations | Held-item drops, weapon equipment state, interaction counts, and consumable/charge state are not represented. |
| Spell/effect activation | 3 | Cast Spell and 2024 Magic action alternatives with long-casting behavior | Spell/item-specific lifecycle, resource, and effect consequences remain external. |
| Healing/stabilization | 2 | Basic recovery and stabilization operations | Hit Die expenditure sequences and stable/death-save transitions are not represented. |

Occurrence counts overlap: one Action can use several families. They are source-record counts, except the 16 roll-modifier outcomes, which count distinct outcomes across nine records. The current contract supports reusable mechanics in each family but does not fully encode every observed procedure.

## Complete 48-record coverage matrix

`SUPPORTED` describes a mechanic represented by an existing typed structure. `PARTIAL` means supported portions were retained while listed mechanics remain outside the contract. `UNSUPPORTED` means no faithful canonical record was produced. These are external analysis classifications, not canonical JSON fields.

| Source-qualified identity | Original source object | Mechanic families | Contract coverage and record result |
|---|---|---|---|
| `action:dmg:activate-an-item` | `$.action[0]` | Object/item activation, spell activation, resources | `PARTIAL` — item activation as an Action; item-specific modes, charges, consumables, spell behavior, and the Use an Object distinction are omitted. |
| `action:phb:attack` | `$.action[1]` | Attack | `PARTIAL` — melee, ranged, and unarmed forms. Exact attack count and feature-granted Extra Attack remain deferred to feature-side modeling. |
| `action:xphb:attack` | `$.action[2]` | Attack, weapon state, feature-dependent movement | `PARTIAL` — weapon/Unarmed Strike attack forms. One-attack count, equipping/unequipping, and feature-dependent movement between attacks are not represented. |
| `action:phb:cast-a-spell` | `$.action[3]` | Spell activation, timing | `PARTIAL` — approved action/reaction/variable-time alternatives; spell procedure remains an external dependency. |
| `action:dmg:climb-onto-a-bigger-creature` | `$.action[4]` | Checks, positioning, modifiers | `UNSUPPORTED` — linked checks, creature-space occupancy/movement, attack Advantage, and dislodging cannot be represented under a matching capability. |
| `action:phb:dash` | `$.action[5]` | Movement increase | `SUPPORTED` — current-turn movement increase based on modified Speed. |
| `action:xphb:dash` | `$.action[6]` | Movement increase, speed modes | `PARTIAL` — current Speed increase; choosing a special Speed for each Dash is omitted. |
| `action:dmg:disarm` | `$.action[7]` | Attack/check, modifiers, object state | `PARTIAL` — melee weapon-attack capability and target/weapon requirements; contested attack/check, dropped item, and size/hand modifiers are omitted. |
| `action:phb:disengage` | `$.action[8]` | Movement permission | `SUPPORTED` — no opportunity attacks from movement for the current turn. |
| `action:xphb:disengage` | `$.action[9]` | Movement permission | `SUPPORTED` — no opportunity attacks from movement for the current turn. |
| `action:phb:dodge` | `$.action[10]` | Roll modifiers, conditions | `SUPPORTED` — attack Disadvantage when the affected creature sees the attacker; Dexterity-save Advantage; duration and early-ending conditions. |
| `action:xphb:dodge` | `$.action[11]` | Roll modifiers, conditions | `SUPPORTED` — approved representative; edition-specific reference and wording preserved. |
| `action:phb:don-or-doff-a-shield` | `$.action[12]` | Equipment state | `SUPPORTED` — separate don and doff operations. |
| `action:xphb:don-or-doff-a-shield` | `$.action[13]` | Equipment state | `SUPPORTED` — separate don and doff operations. |
| `action:phb:end-concentration` | `$.action[14]` | Status transition, timing | `SUPPORTED` — voluntary Concentration termination with Free timing. |
| `action:xphb:end-concentration` | `$.action[15]` | Status transition, timing | `SUPPORTED` — voluntary Concentration termination with Free timing. |
| `action:phb:escape-a-grapple` | `$.action[16]` | Contested check, Condition transition | `PARTIAL` — contested escape and successful Condition ending; the Athletics/Acrobatics-to-ability pairing is not expressible. |
| `action:xphb:escape-a-grapple` | `$.action[17]` | DC check, Condition transition | `PARTIAL` — escape DC reference, successful escape, Incapacitated-grappler ending, and range ending; skill/ability pairing is omitted. |
| `action:phb:grapple` | `$.action[18]` | Attack/check, Condition, movement | `SUPPORTED` — approved representative after the authorized drag/carry operation. |
| `action:dmg:healing-surge` | `$.action[19]` | Healing, Hit Dice, rest resources | `UNSUPPORTED` — no capability or operation models spending/rolling a bounded sequence of Hit Dice and recovering those resources. |
| `action:phb:help` | `$.action[20]` | Roll modifiers, ally assistance | `PARTIAL` — Help capability; check/attack advantage, aided ally, task/enemy, range, and expiry are not represented. |
| `action:xphb:help` | `$.action[21]` | Roll modifiers, stabilization | `PARTIAL` — Help and Stabilize capabilities; check/attack assistance choices and their predicates/outcomes are omitted. |
| `action:phb:hide` | `$.action[22]` | Check, condition/perception | `PARTIAL` — Hide capability and PHB Stealth reference; check/result depends on external PHB rules. |
| `action:xphb:hide` | `$.action[23]` | Check, visibility, Condition lifecycle | `PARTIAL` — DC 15 Dexterity (Stealth) check; concealment prerequisites, hidden/invisible lifecycle, search DC, and ending events are omitted. |
| `action:xge:identify-a-spell` | `$.action[24]` | Check, reaction, spell reference | `PARTIAL` — Action/reaction alternatives and observed-casting trigger; perception prerequisite, DC formula, class-based Advantage, and class/spell dependencies are omitted. |
| `action:phb:improvising-an-action` | `$.action[25]` | Open-ended activity, variable timing | `PARTIAL` — Improvised-action capability and Varies timing; DM-selected feasibility/check behavior is not represented. |
| `action:xphb:improvising-an-action` | `$.action[26]` | Open-ended activity, D20 Test reference | `PARTIAL` — capability, timing, and explicit XPHB D20 Test reference; DM-selected test procedure is omitted. |
| `action:xphb:influence` | `$.action[27]` | Social checks, conditional outcomes | `PARTIAL` — Influence capability and five explicit skill references; willingness/attitude branches, dynamic DC, compliance, and retry timing are omitted. |
| `action:xphb:magic` | `$.action[28]` | Spell/feature/item activation, Concentration | `SUPPORTED` — approved Magic action alternatives and long-casting rule. |
| `action:dmg:mark` | `$.action[29]` | Roll modifier, attack trigger | `SUPPORTED` — approved representative with trigger, marked target, duration, and per-turn use limit. |
| `action:phb:opportunity-attack` | `$.action[30]` | Reaction attack, movement trigger | `PARTIAL` — reach trigger and melee attack; hostility/visibility, timing interruption, and nonprovoking exceptions are omitted. |
| `action:xphb:opportunity-attack` | `$.action[31]` | Reaction attack, movement trigger | `PARTIAL` — reach trigger and weapon/Unarmed Strike attack; visibility, permitted movement sources, and exact interruption timing are omitted. |
| `action:phb:other-activity` | `$.action[32]` | Free activity, object interaction, speech | `PARTIAL` — variable timing and one free ordinary object interaction; communication, count, action-cost, and DM-adjudicated timing rules are omitted. |
| `action:dmg:overrun` | `$.action[33]` | Contested check, hostile-space movement | `UNSUPPORTED` — no capability or movement operation represents check-gated passage through a hostile creature’s space. |
| `action:phb:ready` | `$.action[34]` | Triggered action/movement, reaction, Concentration | `PARTIAL` — Ready capability and Action timing; arbitrary trigger/response, reaction lifecycle, and readied spell behavior are omitted. |
| `action:xphb:ready` | `$.action[35]` | Triggered action/movement, reaction, Concentration | `PARTIAL` — Ready capability and Action timing; the same source-defined response lifecycle remains unavailable. |
| `action:phb:search` | `$.action[36]` | Checks, Skill references | `PARTIAL` — Perception and Investigation references; optional DM-selected check and task association are omitted. |
| `action:xphb:search` | `$.action[37]` | Checks, Skill/task table | `PARTIAL` — four typed Skill references; unspecified DC/check result and skill-to-detection-task mapping are omitted. |
| `action:phb:shove` | `$.action[38]` | Contested check, movement/Condition choice | `SUPPORTED` — approved representative with automatic success and exclusive push/Prone choice. |
| `action:dmg:shove-aside` | `$.action[39]` | Check modifier, forced movement | `PARTIAL` — typed reference to PHB Shove; Disadvantage and lateral five-foot movement are omitted. |
| `action:phb:stabilize-a-creature` | `$.action[40]` | Check, stabilization, death-save state | `PARTIAL` — Unconscious prerequisite, DC 10 Wisdom (Medicine), and stabilization on success; stable/death-save and timed HP recovery consequences are omitted. |
| `action:xphb:study` | `$.action[41]` | Check, Skill/task table | `PARTIAL` — five typed Skill references; Intelligence-check result and skill-to-area mapping are omitted. |
| `action:dmg:tumble` | `$.action[42]` | Contested check, hostile-space movement | `UNSUPPORTED` — no capability or movement operation represents check-gated passage through a hostile creature’s space. |
| `action:phb:two-weapon-fighting` | `$.action[43]` | Bonus Action attack, weapon predicates, damage modifier | `PARTIAL` — bonus attack forms and Attack reference; weapon/hand predicates, damage adjustment, and conditional Thrown option are omitted. |
| `action:xphb:two-weapon-fighting` | `$.action[44]` | Bonus Action attack, weapon predicates, damage modifier | `PARTIAL` — bonus attack and Attack reference; turn/Light/different-weapon prerequisites and damage modifier are omitted. |
| `action:phb:use-an-object` | `$.action[45]` | Object interaction | `PARTIAL` — approved Phase 5 record; item-defined action requirement and interaction count are omitted. |
| `action:xphb:utilize` | `$.action[46]` | Object interaction | `PARTIAL` — approved Phase 5 record; item-defined action requirement is omitted. |
| `action:xge:waking-someone` | `$.action[47]` | Sleep state, events, Perception thresholds | `UNSUPPORTED` — no approved sleep state/capability or typed sound, damage, quiet, and passive Perception predicates. |

## Source-entry accounting

The Action catalog has 97 source-entry roots and 45 nested paths. Across all 48 records, the accounting contains 54 `INTERPRETED`, 6 `DUPLICATE`, and 75 `OMITTED` classification parts. The new 32-record batch contributes 72 roots and 34 interpreted, 4 duplicate, and 66 omitted parts. Omitted and duplicate complete entries are regenerated in `doc/action/omitted.md` and `doc/action/duplicate.md`; nested paths and source object identities are retained in the manifest.

The Phase 5 Action batch contains 42 nonrepresentative records (the ten earlier records plus this batch), 37 canonical outputs, 9 fully represented, 28 partial, and 5 unsupported records. The new batch alone contains 27 canonical outputs, 2 fully represented, 25 partial, and 5 unsupported. Seven structured source-field issues are recorded for editorial `fromVariant` organization and the unrepresentable Overrun-to-Tumble source link. No original source prose was added to canonical JSON.

## Contract gaps requiring human review

No schema extension was authorized or implemented. These gaps recur across source records and should be considered before Action expansion is treated as complete:

1. **Position through occupied/hostile spaces and creature positioning — REQUIRED review.** Climb onto a Bigger Creature, Overrun, and Tumble require different check-gated movement/position outcomes; Shove Aside adds lateral displacement. Existing forced movement and triggered movement do not mean permission to traverse another creature’s space or remain in its space. Consider distinct typed movement operations, preserving the distinct procedures rather than making one generic movement effect. Three records currently have no canonical shell due the missing capability/procedure fit.
2. **Linked check/attack outcomes and modifiers — RECOMMENDED review.** Disarm, Help, Hide, Influence, Ready, Search, Study, and Stabilize combine checks or attack events with sourced conditions, choices, outcomes, dynamic DCs, or role relationships. Existing check IDs and applicability can express some branches, but not opposed attack/check resolution, item dropping, assistance roles, open-ended DM-selected checks, or all success/failure consequences. Review a small Action-specific outcome vocabulary and applicability additions; do not generalize into an effects engine.
3. **Equipment, resource, and damage adjustments — RECOMMENDED review.** Attack weapon handling, Two-Weapon Fighting prerequisites/damage, Healing Surge Hit Dice, and item charge/consumable rules lack exact Action/Item operations. Separate equipment-state, resource-cost, and damage-modification ownership before adding fields.
4. **Readied response and natural sleep — DEFER.** Ready needs an arbitrary perceivable event, chosen response, reaction lifecycle, and possible Spell/Concentration interaction. Waking Someone depends on a defined sleep state and source-specific event/perception predicates. These require further domain-boundary review; no fallback strings or guessed references were used.

Existing accepted limitations remain: Extra Attack may be modeled from the feature side; Cast a Spell’s timing alternatives remain; Magic-item crafting remains outside this Action batch; no Action schema changed. No source ambiguity was used to hide a schema gap. The specific Action `fromVariant` labels are editorial organization and remain external to canonical mechanics.

## Validation and source-fidelity limits

`npm run validate:core-rules` passed after the Action additions:

- 9 Draft 2020-12 schemas compiled.
- 39 examples validated.
- 34 Phase 3/4 canonical records validated.
- 125 Phase 5 canonical records validated.
- 259 permanent checks passed, including Phase 5 batch accounting, complete entry/nested-path coverage, source metadata, references, Action operation integrity, and new Action-specific semantic assertions.

Structural validation confirms schema shape, identities, metadata, references, accounting, and asserted invariants. It does not prove every Action mechanic is correct. Source fidelity remains a human review task; the five unsupported records and partial mechanics are stated above rather than represented through misleading fields.

## Human decisions requested

- Review whether the recurring Action contract gaps warrant a targeted, separately approved refinement before Phase 5 continues to another domain.
- Review the five unsupported records and the omissions listed in the coverage matrix.
- Confirm acceptance of the present Action coverage counts (13 full, 30 partial, 5 unsupported) without interpreting structural validation as mechanical approval.

Phase 5 remains in progress and is not approved. Phase 6 has not started.

## ACT-1–ACT-4 implementation update

The authorized Action contract refinement changed the baseline above. The current 42-record Phase 5 Action batch contains 40 canonical records: 27 fully represented, 13 partial, and 2 unsupported. Combined with the six Phase 3/4 representatives, the full 48-record catalog contains 31 fully represented, 15 partial, and 2 unsupported records. The three movement Actions previously lacking outputs now have canonical records: Overrun and Tumble are fully represented, while Climb onto a Bigger Creature remains partial for its qualitative size-likelihood guidance. The 128 canonical records, 236 source-entry roots, and 201 interpreted / 22 duplicate / 152 omitted classification parts in the Phase 5 manifest reconcile with the files and registers. See the implementation report for source-qualified coverage, schema changes, and current validation results.
