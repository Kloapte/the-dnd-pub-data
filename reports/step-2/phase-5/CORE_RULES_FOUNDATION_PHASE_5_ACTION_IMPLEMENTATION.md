# Step 2 Core Rules Foundation — Phase 5 Action Implementation

## Status

ACT-1 through ACT-4 are implemented under the existing Phase 5 authorization. Phase 5 remains in progress and is not approved. ACT-5 Equipment/character-resource work, ACT-6 Ready/natural-sleep work, and Phase 6 remain deferred.

## Implemented contracts

### ACT-1 — Movement and positioning

The Action schema now distinguishes check-gated `TRAVERSE_CREATURE_SPACE`, `ENTER_CREATURE_SPACE`, and `MOVE_TARGET_TO_SPACE_WITHIN_REACH`. Traversal is a temporary permission through a hostile creature's space; entry describes moving with a larger creature and the source-defined difficult-terrain, attack, and dislodging behavior; lateral movement describes Shove Aside's five-foot displacement into a valid space within reach. Traversal and entry outcomes explicitly apply only after their linked check succeeds. Existing forced movement and voluntary Grappled-target drag/carry remain separate operations.

Overrun and Tumble retain different opposed checks and share only their passage permission. Overrun preserves the relative-size Advantage/Disadvantage rule. Climb onto a Bigger Creature preserves its entry contest, DM-adjudicated positioning, movement with the target, Advantage against it, difficult-terrain treatment, and distinct dislodging contest. Its qualitative size-likelihood and supernatural-might statement remains omitted because the source establishes no deterministic threshold. PHB and XPHB Dash remain edition-specific; XPHB Dash can select a special Speed for each use. Opportunity Attacks preserve the PHB hostility and movement exceptions separately from XPHB's qualifying movement origins, with both attacks resolving immediately before departure.

### ACT-2 — Linked procedures

Check participants can select paired ability/Skill options. The permanent integrity validator checks Skill ability associations, including Influence choices and opposed checks. `ATTACK_CONTESTED_BY_CHECK` represents Disarm's attack roll against a defender-selected check, with no attack damage and the held-item drop outcome. Check and attack modifiers/outcomes reference local operation IDs. The finite DC structures support fixed value plus referenced Spell level and the greater of a fixed minimum and target ability score.

Bounded procedures now represent PHB/XPHB Help modes and their exclusive choice, XPHB Hide prerequisites and Invisible lifecycle, XPHB Influence's willingness branches, XGE Identify a Spell timing and check, PHB/XPHB Escape a Grapple pairings and endings, PHB Search's DM-selected ability, and XPHB Search/Study checks and advisory skill-task guidance. The XPHB Help record marks DM feasibility as a supplied decision. Task guidance remains advisory and does not resolve a check.

### ACT-3 — Object use

`OBJECT_USE` now distinguishes an item use that requires an Action from the incidental `ordinary-interaction`, shield operations, and magic-item activation. PHB Use an Object and XPHB Utilize use the Action-required prerequisite. PHB Other Activity records one free interaction per turn, its placement during movement or an Action, and the Action cost of an additional interaction. The PHB-specific count is not applied to XPHB Utilize.

### ACT-4 — Search and Study guidance

The XPHB Search and Study records preserve each source table row as typed Skill-to-task guidance, including Study's separate creature-group associations. These values are suggestions and do not select a required Skill or execute a D20 Test.

### Schema and compatibility changes

`src/main/resources/schemas/action/schema.json` adds the two distinct movement capabilities and operations, required success applicability for check-gated traversal/entry, per-use Speed selection, operation-linked checks/modifiers, paired check participants, opposed attack/check resolution, the bounded Help/Hide/Influence/Spell-identification procedures, advisory task-guidance rows, action-cost object use, and the PHB interaction allowance. `src/main/resources/schemas/common/schema.json` adds the finite DC formula shapes and source-specific Opportunity Attack and Hide event details used by these Action operations. These are additive typed branches in the approved v1 contracts; existing Action records retain their prior semantics. The integrity utility checks local operation references, Skill-to-ability consistency, choice membership/cardinality, and linked check compatibility.

## Canonical migration and coverage

The new canonical outputs are `action:dmg:climb-onto-a-bigger-creature` (partial), `action:dmg:overrun` (fully represented), and `action:dmg:tumble` (fully represented). Existing records were migrated where the new approved structures recovered source-supported mechanics. No record was added for Healing Surge or Waking Someone. Existing accepted records and interpretations were retained.

| Catalog portion | Fully represented | Partial | Unsupported | Canonical outputs |
|---|---:|---:|---:|---:|
| Phase 3/4 representatives (6) | 4 | 2 | 0 | 6 |
| Phase 5 Action batch (42) | 27 | 13 | 2 | 40 |
| **All Action sources (48)** | **31** | **15** | **2** | **46** |

The 31/15/2 result matches the earlier conservative review forecast. This is a coverage classification, not a claim that all Actions are executable end-to-end. The partial records are listed below; full details and source paths are in the manifests and this report's source evidence.

| Source-qualified identity | Source path | Result | Main mechanic / remaining issue |
|---|---|---|---|
| `action:dmg:activate-an-item` | `$.action[0]` | Partial | Generic item-defined activation and distinction from Use an Object are represented; item-specific modes/effects and feature interaction remain deferred. |
| `action:phb:attack` | `$.action[1]` | Partial | One attack is represented; feature-granted Extra Attack and general attack procedure remain external. |
| `action:xphb:attack` | `$.action[2]` | Partial | One attack is represented; Equipment/Class sequencing remains external. |
| `action:phb:cast-a-spell` | `$.action[3]` | Partial | Approved timing alternatives; Spell lifecycle deferred. |
| `action:dmg:climb-onto-a-bigger-creature` | `$.action[4]` | Partial | Shared-space entry and dislodging; qualitative size likelihood omitted. |
| `action:phb:dash` | `$.action[5]` | Full | Current-Speed movement increase. |
| `action:xphb:dash` | `$.action[6]` | Full | Per-use selection of an eligible special Speed. |
| `action:dmg:disarm` | `$.action[7]` | Full | Attack roll opposed by defender-selected check; no damage; held-item drop outcome. |
| `action:phb:disengage` | `$.action[8]` | Full | Current-turn movement permission. |
| `action:xphb:disengage` | `$.action[9]` | Full | Current-turn movement permission. |
| `action:phb:dodge` | `$.action[10]` | Full | Source-correct attack roles, Dexterity-save Advantage, duration and ending. |
| `action:xphb:dodge` | `$.action[11]` | Full | Approved edition-specific Dodge interpretation. |
| `action:phb:don-or-doff-a-shield` | `$.action[12]` | Full | Separate don/doff operations. |
| `action:xphb:don-or-doff-a-shield` | `$.action[13]` | Full | Separate don/doff operations. |
| `action:phb:end-concentration` | `$.action[14]` | Full | Voluntary Status termination. |
| `action:xphb:end-concentration` | `$.action[15]` | Full | Voluntary Status termination. |
| `action:phb:escape-a-grapple` | `$.action[16]` | Full | Paired Strength/Athletics or Dexterity/Acrobatics contest and escape outcome. |
| `action:xphb:escape-a-grapple` | `$.action[17]` | Full | Paired check options, DC reference, and source-defined Condition-ending cases. |
| `action:phb:grapple` | `$.action[18]` | Full | Approved check, Condition, release, and voluntary drag/carry mechanics. |
| `action:dmg:healing-surge` | `$.action[19]` | Unsupported | Character Hit Die spending/recovery procedure. |
| `action:phb:help` | `$.action[20]` | Full | Exactly-one check or attack assistance mode, beneficiary and expiry. |
| `action:xphb:help` | `$.action[21]` | Partial | Help choices represented; stabilization procedure depends on death-save state. |
| `action:phb:hide` | `$.action[22]` | Partial | Check represented; outcome depends on PHB hiding and unseen-target Rules. |
| `action:xphb:hide` | `$.action[23]` | Full | Concealment/visibility prerequisites, Invisible Condition, discovery DC and ending events. |
| `action:xge:identify-a-spell` | `$.action[24]` | Full | Reaction/action modes, perception, fixed-plus-level DC and class-based Advantage input. |
| `action:phb:improvising-an-action` | `$.action[25]` | Partial | DM-defined feasibility and outcome. |
| `action:xphb:improvising-an-action` | `$.action[26]` | Partial | DM-defined D20 Test and outcome. |
| `action:xphb:influence` | `$.action[27]` | Full | Willingness branches, check choices, DC, outcomes and retry delay. |
| `action:xphb:magic` | `$.action[28]` | Full | Distinct 2024 Magic action alternatives and long-casting behavior. |
| `action:dmg:mark` | `$.action[29]` | Full | Approved reusable roll-modifier interpretation. |
| `action:phb:opportunity-attack` | `$.action[30]` | Full | Hostility, visibility, movement exclusions and pre-departure timing. |
| `action:xphb:opportunity-attack` | `$.action[31]` | Full | Edition-specific qualifying movement origins and pre-departure timing. |
| `action:phb:other-activity` | `$.action[32]` | Partial | Free object-interaction count and placement; unusual Action adjudication remains external. |
| `action:dmg:overrun` | `$.action[33]` | Full | Contested Athletics and size modifier gate one hostile-space passage. |
| `action:phb:ready` | `$.action[34]` | Partial | Response scheduling, Reaction, expiry and Spell lifecycle deferred. |
| `action:xphb:ready` | `$.action[35]` | Partial | Response scheduling and Reaction lifecycle deferred. |
| `action:phb:search` | `$.action[36]` | Full | DM-selected Perception or Investigation check. |
| `action:xphb:search` | `$.action[37]` | Full | Wisdom check and advisory Skill/task mapping. |
| `action:phb:shove` | `$.action[38]` | Full | Approved check, automatic-success condition and exclusive outcomes. |
| `action:dmg:shove-aside` | `$.action[39]` | Full | Shove check with Disadvantage and lateral five-foot movement. |
| `action:phb:stabilize-a-creature` | `$.action[40]` | Partial | Check/stabilization represented; stable duration and later state transitions remain external. |
| `action:xphb:study` | `$.action[41]` | Full | Intelligence check and advisory Skill/task/creature-group mappings. |
| `action:dmg:tumble` | `$.action[42]` | Full | Contested Acrobatics gates one hostile-space passage. |
| `action:phb:two-weapon-fighting` | `$.action[43]` | Partial | Equipment predicates, attack damage adjustment and conditional Thrown form deferred. |
| `action:xphb:two-weapon-fighting` | `$.action[44]` | Partial | Light/different weapon predicates and damage adjustment deferred. |
| `action:phb:use-an-object` | `$.action[45]` | Full | Item-defined Action use, including the PHB additional-interaction use case. |
| `action:xphb:utilize` | `$.action[46]` | Full | Action required by object; PHB interaction-count rule is not applied. |
| `action:xge:waking-someone` | `$.action[47]` | Unsupported | Natural-sleep state and wake-event/perception procedure. |

| Source-qualified identity | Source path | Result | Remaining limitation or dependency |
|---|---|---|---|
| `action:dmg:activate-an-item` | `$.action[0]` | Partial | Specific activation modes, item state/effects, and feature interaction remain Item/Spell/Class dependencies. |
| `action:phb:attack` | `$.action[1]` | Partial | Extra Attack remains a feature-side dependency; the referenced general attack procedure belongs to Rule modeling. |
| `action:xphb:attack` | `$.action[2]` | Partial | Weapon state changes and feature-granted movement between attacks remain Equipment/Class/encounter dependencies. One attack is represented by one attack-resolution operation. |
| `action:phb:cast-a-spell` | `$.action[3]` | Partial | Approved timing alternatives are preserved; casting lifecycle and effects remain Spell-domain dependencies. |
| `action:dmg:climb-onto-a-bigger-creature` | `$.action[4]` | Partial | Qualitative size likelihood and supernatural-might exception have no deterministic source threshold. |
| `action:xphb:help` | `$.action[21]` | Partial | The Help task/attack choice is represented; the linked XPHB dying-creature stabilization procedure remains an external state/death-save dependency. |
| `action:phb:hide` | `$.action[22]` | Partial | PHB check is represented; success benefits are defined by external PHB hiding/unseen-target procedures. |
| `action:phb:improvising-an-action` | `$.action[25]` | Partial | The source leaves feasibility and any success procedure to DM adjudication. |
| `action:xphb:improvising-an-action` | `$.action[26]` | Partial | The source leaves the required D20 Test and outcome to DM adjudication. |
| `action:phb:other-activity` | `$.action[32]` | Partial | The object-interaction limit is represented; communication and unusual-obstacle Action adjudication remain non-procedural guidance. |
| `action:phb:ready` | `$.action[34]` | Partial | Readied response, trigger scheduling, Reaction execution, expiry, and spell lifecycle remain deferred under ACT-6 and Spell/encounter models. |
| `action:xphb:ready` | `$.action[35]` | Partial | Readied response and trigger lifecycle remain deferred under ACT-6 and encounter execution. |
| `action:phb:stabilize-a-creature` | `$.action[40]` | Partial | The check and stabilization transition are represented; stable duration, death-save resumption, and timed HP recovery require additional state procedures. |
| `action:phb:two-weapon-fighting` | `$.action[43]` | Partial | Weapon/hand predicates and damage modifier await Equipment and attack-damage ownership. |
| `action:xphb:two-weapon-fighting` | `$.action[44]` | Partial | Light/different-weapon predicates and damage modifier await Equipment and attack-damage ownership. |
| `action:dmg:healing-surge` | `$.action[19]` | Unsupported | Hit Die spending, rolling, healing, eligibility, and rest recovery require the deferred character-resource model. |
| `action:xge:waking-someone` | `$.action[47]` | Unsupported | Natural sleep, noise, damage, and passive-Perception wake conditions require a sleep/encounter-state contract. |

All 48 source identities are recorded across the Phase 3/4 and Phase 5 manifests. The Phase 5 Action batch contains 84 source-entry roots and 44 nested paths: 85 interpreted, 5 duplicate, and 32 omitted classification parts. Across all currently expanded Phase 5 domains, the manifest contains 128 canonical outputs, 108 fully represented, 20 partial, and 3 unsupported records, with 201 interpreted, 22 duplicate, and 152 omitted classification parts.

The previous 125-versus-128 discrepancy was a stale manifest count. Three outputs had been authored for the previously unsupported Climb, Overrun, and Tumble records while the manifest still counted the original 37 Action outputs. The manifest now maps all 40 Action outputs, preserves the source records' 84 roots, and accounts for all entry roots including the still-unsupported Healing Surge and Waking Someone.

## Source accounting and source fidelity

The Phase 5 manifest and Action registers were regenerated from the original source objects. The Action batch accounts for 84 source-entry roots and 44 nested paths: 85 `INTERPRETED`, 5 `DUPLICATE`, and 32 `OMITTED` classification parts. Overrun's explicit `seeAlsoAction` relationship to Tumble is now an interpreted typed reference, not an unresolved omission. The Attack records now account for a single attack through their one `ATTACK_RESOLUTION`; feature-granted extra attacks and equipment/feature sequencing remain external dependencies. Escape a Grapple, Grapple, Shove, and Disarm ability/Skill pairings are represented and checked against canonical Skill abilities.

The complete original entries for omitted and duplicate portions are preserved in `doc/action/omitted.md` and `doc/action/duplicate.md`. Omitted portions that are already represented are no longer classified `SCHEMA_UNSUPPORTED`. The two unsupported records retain complete entry-level accounting without canonical shells.

## Verification

The executed `npm run validate:core-rules` run reported:

- 9 Draft 2020-12 schemas compiled.
- 54 examples validated, including 15 focused ACT-1–ACT-4 fixtures or migrated fixtures.
- 34 Phase 3/4 canonical records validated.
- 128 Phase 5 canonical records validated.
- 283 permanent checks passed; Action operation integrity covered 27 Action fixtures.

The workflow checks schema shape, deterministic formatting, source identity and metadata, typed references, Action choice and linked-operation integrity, source-entry locators/nested paths, register completeness, and source-backed regression assertions. These checks do not establish complete mechanical correctness for every record; the partial and unsupported mechanics above remain explicit.

`git diff --check` passed after the final documentation and formatting changes. Phase 5 remains in progress. No Action/Language/Rule expansion beyond this authorized Action refinement, ACT-5/ACT-6 implementation, Phase 6 backend integration, or later roadmap step has started.
