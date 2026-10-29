# Step 2 — Core Rules Foundation: Phase 2 Design and Validation

**Status:** Revised canonical design is complete and awaiting review. It is not approved. Phase 3 representative conversion has not begun.

## Historical validation — original prose-oriented contracts

This section preserves the results for the superseded Phase 2 contract set. Those results do not validate the revised contracts below.

The original Draft 2020-12 contract set comprised 11 schema documents, including recursive rich-text and LanguageScript contracts, and 13 examples. Ajv 8.17.1 reported that all 11 schemas compiled and all 13 examples validated. A non-converting shape scan inspected 594 records across six source files and validated 1,231 nested `entries` arrays without unsupported rich-text structures. The source files were not modified and no canonical dataset was produced.

The original contracts preserved source prose and opaque `ruleType` values. They used source-qualified IDs that preserved source capitalization and names. These validation results establish only structural validity for the original prose-oriented design. They do not establish semantic correctness or revised schema validity.

## Permanent design-feedback record

The earlier contracts were rejected as the canonical design because they made source prose and presentation structures the primary data model. Recursive `entries` and rich-text trees moved source interpretation to runtime consumers, obscured mechanics, and conflicted with the canonical-data boundary that requires interpreted typed mechanics. The revised contracts instead encode supported domain mechanics and references, leave unsupported or ambiguous content outside canonical JSON, and avoid a universal effects engine.

### General feedback and decisions implemented for this revision (Phase 2 approval pending)

| Feedback | Revised decision |
| --- | --- |
| Canonical records must represent interpreted mechanics, not copied `entries` or rich text. | Removed `entries`, rich-text structures, the rich-text schema, and its example. Domain schemas reject unknown fields, so prose cannot be reintroduced as a fallback. |
| Reuse identity, source metadata, and typed references. | Shared schema defines canonical `id`/display `name`, source code, optional page, supplied flags, citations, reprint references, and typed target references. Relationship and mechanical operation are distinct reference attributes. |
| IDs must be lowercase, slugged, deterministic, and collision checked. | Contract pattern is `<type>:<lowercase-source>:<slug>`. Durable rules define NFKD normalization for the source component and name slug, combining marks, non-ASCII code points, punctuation, whitespace, empty values, and post-normalization collision reporting in [`doc/CORE_RULES.md`](). |
| Remove source presentation metadata and workflow status. | Removed `hasFluff`, `hasFluffImages`, font metadata, and LanguageScript. Canonical JSON has no review or interpretation status fields. |
| Preserve all source information through interpreted mechanics, references, or external registers. | Supported portions have typed fields; omission and duplicate accounting must record complete source entries and source-object identities outside canonical JSON when conversion is performed. No source entry has been classified as omitted or duplicate by this Phase 2 design revision. |

### Model-specific feedback and decisions

| Model | Revised contract decision | Unresolved design boundary |
| --- | --- | --- |
| Action | Explicit capabilities include melee, ranged, and barehand attacks; typed requirements/references and operations include `END_STATUS`. Timing supports fixed action/bonus/reaction and in-game units, `free`, evidenced `varies` options, and absent timing. | The finite capability and operation vocabulary will not cover every action. Unsupported portions require entry-level accounting and contract review; do not add guessed capabilities during conversion. |
| Condition | Typed effects represent sense removal, automatic sense-dependent check failure, roll modifiers, and conditional restrictions with actor/target/applicability fields. PHB and XPHB identities remain separate. | The examples cover Blinded and both complete Exhaustion level progressions. Novel effects outside these typed patterns still require reviewed contract design. |
| Disease | Disease-specific infection, transmission, incubation, symptoms, saving throws, progression, and recovery are separate typed concepts. Symptoms can reference conditions and represent supported roll/sense effects. | Complex transformation consequences, narrative flaws, immunity, variable dice, and disease-specific special cases may need additional typed structures. No free-text symptom or progression fallback is allowed. |
| Rule | One cohesive Rule domain has a discriminated set of typed rule structures for ability checks, attack rolls, saving throws, movement, and cover. It does not carry a universal prose/effect object. | Source repository evidence shows `ruleType` values/counts but no authoritative mapping for `C`, `O`, `V`, or `VO`. Their meanings remain unresolved and the codes are not expanded or placed into canonical classifications. The separate source `type` formatting field is also excluded from mechanics. |
| Language | Retains category, script name, origin and dialect references, and resolved typed typical-speaker creature references. Fonts and LanguageScript are removed. | A speaker filter that cannot be resolved to an explicit canonical target must be tracked externally; no raw tag or inferred same-name creature reference is accepted. |
| Sense | Qualitative sight, darkness, nonvisual/touch/vibration capabilities, restrictions, and condition interactions are structured. | Creature-specific ranges remain outside Sense. Novel interactions and conditions beyond the enumerated contract require review. |
| Skill | Keeps the explicit ability association and optional typed check references. | Descriptive examples are not a canonical property. A source description that adds mechanics beyond the ability association must be accounted for as a duplicate only if fully redundant, otherwise as an omission alongside any supported facts. |
| Status | State-specific mechanics represent thresholds, turn restrictions, maintenance saves, and ending conditions. All five records are included in the design examples: Bloodied (XPHB); Concentration (PHB and XPHB); Surprised (PHB and XPHB). | The source records have distinct edition semantics. Bloodied is a half-hit-point threshold; Concentration has edition-specific save formulas and ending triggers; PHB Surprised restricts movement/action/reaction on the first turn, while XPHB Surprised imposes initiative disadvantage. Do not merge by name. |

### Repository investigation: `ruleType`

The Step 2 discovery census and checked-in source files provide the observed `ruleType` values and counts: `C` 115, `O` 43, `V` 17, `VO` 16, and absent on 40 of 231 variant-rule records. Local searches across the roadmap, boundaries, discovery reports, and source files found no authoritative full-word key. The source filename and examples such as Ability Check (`C`) and Action Options (`O`) do not establish reliable mappings for all four values. No semantics are inferred. The source field must be retained in external provenance during later processing, with mapping unresolved until authoritative repository evidence or separate review supplies it.

### Revised source-to-canonical mappings

| Source property or structure | Revised treatment |
| --- | --- |
| `name`, `source`, `page` | `name` remains display identity; `sourceMetadata.source` preserves the exact Source Registry code; `page` remains optional when supplied. Canonical IDs lowercase the source component and use the deterministic name slug. |
| `srd`, `srd52`, `basicRules`, `basicRules2024` | Present booleans map unchanged under `sourceMetadata.flags`; absent values stay absent. |
| `reprintedAs` | Convert explicit targets to typed references in `sourceMetadata.reprintedAs`; do not merge editions or infer a preferred edition. Unresolved target identity stays external. |
| `additionalSources`, `otherSources` | Map source/page pairs to `sourceMetadata.citations`. |
| Nested `entries` and inline tags | Interpret supported mechanics into domain fields; convert explicit resolved entity links into typed references. Exclude original prose and presentation trees from canonical JSON. Log unsupported portions and duplicate content with complete source identity and entry content in their model registers during conversion. |
| Action `time` | Map fixed, Free, and Varies forms to typed `timing`; source absence stays absent. Populate `varies.options` only from verified timing evidence. |
| Action `seeAlsoAction`, `fromVariant` | Convert only when the explicit relationship and canonical target identity are established; otherwise retain unresolved evidence outside canonical data. |
| Rule `ruleType` | No full-word interpretation is supported by checked-in evidence. Keep the code and unresolved mapping in external provenance; do not emit a canonical classification. |
| Rule `type` (`section`/`entries`) | Source rendering/organization detail; it does not classify rule mechanics and is not a canonical field. |
| Skill `ability` | Normalize the explicit ability code to its typed lowercase ability value. Descriptive examples do not populate canonical fields. |
| Condition, Disease, Status, Sense prose | Interpret into the corresponding typed effects, lifecycles, thresholds, restrictions, capabilities, or references. Unrepresented portions are externally dispositioned, not retained as text. |
| Language `type`, `script`, `origin`, `dialects` | Map to category, script name, and typed origin/dialect references where target identity is explicit. |
| Language `typicalSpeakers` | Parse verified creature filters into typed creature filters and explicit entity tags into typed references. Place non-entity descriptions or unresolvable filters in external accounting. |
| LanguageScript `fonts`, font asset paths, `hasFluff`, `hasFluffImages` | No canonical mapping; the LanguageScript model and font-only records are removed, as are presentation flags. |

### Status design inventory

All five status source records, not merely the three distinct names, are represented by examples:

| Source object | Canonical example | Distinguishing mechanics |
| --- | --- | --- |
| `status[0]`: Bloodied, XPHB p. 362 | `status-bloodied.json` | At or below one-half hit points. |
| `status[1]`: Concentration, PHB p. 203 | `status-concentration-phb.json` | Constitution save at DC 10 or half damage, separate save per damage source; ending on incapacitation or death. No unprovided maximum DC is invented. |
| `status[2]`: Concentration, XPHB p. 363 | `status-concentration-xphb.json` | Round damage fraction down, maximum DC 30; ends when another concentration effect begins, on incapacitation, or death. |
| `status[3]`: Surprised, PHB p. 189 | `status-surprised-phb.json` | Cannot move or act on the first turn; cannot react until that turn ends. |
| `status[4]`: Surprised, XPHB p. 376 | `status-surprised-xphb.json` | Disadvantage on initiative. |

These examples are contract fixtures illustrating supported mechanics; they are not a converted dataset or a declaration that every source entry is completely covered.

## Deviations from the original Phase 2 design

- Nine semantic domain models remain: Action, Rule, Skill, Condition, Disease, Status, Sense, and Language are the eight active schemas; LanguageScript has been removed by explicit instruction.
- Identity IDs use lowercase source components and deterministic slugs rather than preserving exact source casing in IDs. Display names and exact source codes remain in their own fields.
- Source fields are grouped under `sourceMetadata`; action timing, rule classification codes, structural entry type, and speaker descriptions are no longer represented as source-shaped fields.
- Reprint associations are typed canonical references rather than raw UID strings.
- No LanguageScript schema or example, rich-text schema or example, or source prose schema remains.
- Historical original-schema validation is retained above and is expressly not reused as validation evidence for this revision.

## Revised contract validation

> Historical result for the first interpreted-contract revision. The consolidated revision and its current verification are recorded at the end of this report; these counts do not validate that revision.

The revised contract files are the nine JSON Schemas under `src/main/resources/schemas/` (shared definitions plus eight domain schemas). At this historical checkpoint, Ajv 8.17.1 using its Draft 2020-12 implementation compiled all **9 schemas** and validated all **18 examples**, with zero failures. The example set included all five Status source-record cases across Bloodied, Concentration, and Surprised. Those duplicate example files have since been removed; the current validator uses canonical records instead. The rich-text and LanguageScript contracts were absent by design.

Schema validation does not prove that mechanics are correct, that each source entry has been interpreted, or that references resolve. Those checks require later authorized source-entry conversion and validation. No normalized records, Java DTOs, or dataset expansion were produced.

## Omission, duplication, and unsupported patterns

This design revision has not classified source entries as interpreted, omitted, or duplicate and has not copied complete source content into model registers. That accounting belongs to entry-by-entry conversion, which is outside this Phase 2 revision. Known representation gaps that must be resolved or documented during later conversion include:

- Disease mechanisms requiring transformation state, variable dice, narrative character changes, complex exposure, or other behavior beyond the current typed lifecycle fields.
- Rule patterns outside the defined ability-check, attack, saving-throw, movement, and cover structures, including optional systems with multiple interdependent procedures.
- Action operations and conditional timing not covered by the explicit capability/operation vocabulary.
- Sense interactions not covered by sight, darkness, nonvisual perception, vibration, and condition interaction fields.
- Language `typicalSpeakers` and entity references whose exact target identity cannot be established.
- Any prose-only source part, which cannot be copied into canonical JSON and must be dispositioned with full source identity and complete source entry content in the model-specific omission or duplicate register when conversion occurs.

These are design gaps and work instructions, not claims that the affected source records have already been omitted. Phase 3 remains unauthorized until the revised contracts are reviewed and approved.

## Review decisions still needed

1. Approve or revise the domain schemas and enumerated mechanics, especially condition and disease outliers.
2. Supply authoritative meanings for `ruleType` codes if full-word classification is required; otherwise keep them unresolved outside canonical records.
3. Review whether source citations and reprint references under `sourceMetadata` meet provenance needs while preserving exact source codes.
4. Confirm any additional exact supported values for `varies` timing before representative conversion; absent timing remains absent.

**Approval checkpoint:** Phase 2 design is awaiting review. Do not begin Phase 3 until approval is explicit.

## Consolidated-feedback revision — current design record (Phase 2 approval pending)

This permanent section records the consolidated design feedback and the subsequent focused Action/Rule research. It supersedes the contract descriptions above for the **current proposal only**; prior validation and feedback remain historical evidence. This revision remains a proposal awaiting human review.

### Feedback and research findings incorporated

- The prose-oriented contracts were rejected because source `entries` and rich-text structures delegated interpretation to runtime consumers. The current contracts contain typed mechanics only; canonical schemas reject unknown prose fields and contain no workflow status.
- Action research inspected all 48 source records (`actions.json`, PHB/XPHB/DMG/XGE). It found eight overlapping operation families: attack resolution, checks/contests, roll modifiers, movement/positioning, Condition/Status transitions, object/equipment interaction, spell/effect activation, and healing/stabilization. The prior `GRANT_ATTACK_ADVANTAGE` represented only Mark. Mark, Dodge, Grapple, Shove, Magic, End Concentration, and Stabilize a Creature inform the revised operation structures and examples.
- Rule research inspected all 231 records (`variantrules.json`) and identified overlapping D20 test, movement/spatial, damage/health/state, spell/magic, and downtime families. It found records with multiple independent mechanics and verified that the old exclusive single-`rule` object could not hold them. The title-led research inventory is documented in [`CORE_RULES_ACTION_RULE_MECHANICS_RESEARCH.md`](CORE_RULES_ACTION_RULE_MECHANICS_RESEARCH.md).
- The remaining 101 Rule records include social rules, rest and progression procedures, creature/character definitions, hazards, equipment, vehicles and ships, optional rules, and setting-specific subsystems. Social, rest, selected vehicle procedures, and the cohesive Unarmed Strike attack/grapple/shove procedure are now represented. Many optional or interdependent systems remain out of scope for a generic structure and require targeted design. Definitional or organizational records are not presumed to be executable mechanics.
- Source `ruleType` values remain external unresolved facts (`C` 115, `O` 43, `V` 17, `VO` 16, absent 40); no full-word mapping is inferred.

### Current design decisions

| Area | Decision and rationale |
| --- | --- |
| Action operations | Operations are typed objects discriminated by `kind`. `ATTACK_RESOLUTION` is distinct from special check/save outcomes. `ROLL_MODIFIER` replaces Mark-only advantage and retains roll, direction, actor, target, trigger, expiry, usage limit, and reaction behavior. DC checks, contests, and saving throws, state transitions, movement increase, forced movement, movement permission, triggered movement, object/equipment operation, spell operation, healing, and stabilization use separate domain responsibilities where outcomes differ. Applying/ending a Condition and ending a Status carry separate target types. |
| Action timing | Shared timing values have consistent object shapes. Explicit alternatives use `kind: alternatives`; open-ended source variation uses `kind: varied_gametime`; specific time amounts use `fixed_gametime`. Cast a Spell no longer has an invented one-minute duration. Identify a Spell demonstrates action/reaction alternatives. Mark preserves absent timing and End Concentration preserves `free`. |
| Rule composition | A Rule has a required `mechanics` array. Each component uses `kind` for family and `subtype` for procedure, so one identity can carry distinct typed components. Critical Hit demonstrates attack-roll and damage components. This is a component list, not a universal effects engine. |
| Rule families | D20 Test has distinct TEST, ABILITY_CHECK, ATTACK_ROLL, SAVING_THROW, DEATH_SAVING_THROW, and TEST_MODIFIER subtypes. Movement/spatial, combat procedure, damage/health/state, spell/magic, downtime, social, rest, and vehicle procedure structures cover additional source-evidenced patterns. Separate component variants prevent one record from being forced into one family. |
| Edition identity | PHB/XPHB source identities remain separate. D20 subtype fields do not imply that edition-specific procedures are equivalent. Death Saving Throw models only the source record's supported start-of-turn-at-zero-hit-points trigger; unprovided success/failure thresholds are not inferred from general knowledge. |
| Condition | Blinded actor/target directions are explicit: attackers have advantage when attacking the affected creature; the affected creature has disadvantage on its own attacks. PHB Exhaustion retains cumulative six-level effects, half hit-point maximum from level 4 while level 4+ persists, death at level 6, and recovery by rest with food and drink or being raised. XPHB Exhaustion retains a per-level D20 Test penalty of 2 and Speed reduction of 5 feet while each level persists, with death at level 6 and one level removed by a Long Rest. |
| Concentration | The PHB example includes separate Constitution saves for each damage source, DC 10 or half damage (whichever is higher), incapacitation, death, another Concentration spell, voluntary termination without an action, and the explicitly sourced DM-adjudicated environmental DC 10 save. XPHB preserves round-down and maximum DC 30, ends on another Concentration effect, incapacitation, death, or voluntary termination. Automatic endings are distinct from save-based maintenance. No unsourced environmental rule is added to XPHB. |
| Disease | At least one typed infection, incubation, symptom, progression, or recovery component is required. A metadata-only shell cannot validate. Symptoms remain distinct from infection triggers, incubation, progression, and recovery. The Blinding Sickness example encodes its sourced Wisdom roll disadvantages and Blinded condition. |
| Other models | Language, Sense, and Skill remain unchanged apart from shared conventions. LanguageScript, rich text, font fields, and presentation flags remain absent. |

### Schema and example changes

| Contract | Changes and representative fixtures |
| --- | --- |
| `common.schema.json` | Restricts reference targets to known canonical model types and defines consistent timing objects for action, bonus action, reaction, free, fixed in-game duration, open-ended `varied_gametime`, and explicit alternatives. |
| `action.schema.json` | Replaces `GRANT_ATTACK_ADVANTAGE` with typed roll modifiers; adds typed attack resolution, DC/contested checks, saves, condition/status transitions, movement variants, object/equipment operations, spell activation, healing, and stabilization. Action fixtures include Attack, Mark, End Concentration, Cast a Spell, Identify a Spell, Improvising an Action, Dodge, Grapple, Shove, Escape a Grapple, Magic, and Stabilize a Creature. |
| `rule.schema.json` | Replaces the exclusive `rule` object with a multi-component `mechanics` array using family `kind` and procedure `subtype`. Fixtures cover D20 Test, ability checks, attack rolls, saving throws, death-save trigger, advantage, Critical Hit composition, Difficult Terrain, Spell Attack, and Unarmed Strike with damage/grapple/shove options. |
| `condition.schema.json` | Makes scaled Exhaustion effects carry actor, threshold applicability, and duration information; retains separate PHB/XPHB mechanics and precise Blinded actor/target direction. Fixtures cover PHB/XPHB Exhaustion and XPHB Blinded alongside the existing PHB Blinded example. |
| `disease.schema.json` | Requires at least one disease-specific typed component, preventing metadata-only records from validating. The Blinding Sickness fixture encodes its supported mechanical effects and explicit Contagion spell reference; it is a design fixture, not complete source-entry accounting. |
| `status.schema.json` | Separates save-based Concentration maintenance from automatic ending and action-free voluntary termination; represents distinct PHB/XPHB DC formulas and PHB environmental save evidence. All five Status source records remain represented by examples. |

Language, Sense, and Skill contracts and fixtures were not otherwise redesigned. The separate Action/Rule research report remains unchanged.

### Source-evidence references

| Source identity | Evidence used in current design |
| --- | --- |
| `action[DMG:Mark]` p. 271 | Advantage on opportunity attacks against the marked target through end of next turn; reaction availability is required but the reaction is not consumed; one opportunity attack per turn. No Action timing is supplied. |
| `action[PHB:Dodge]` p. 192 | Attacks against the dodging creature have disadvantage when it can see the attacker; Dexterity saves have advantage; benefits end on incapacitation or Speed 0. |
| `action[PHB:Grapple]` p. 195; `action[PHB:Shove]` p. 195 | Contested ability checks and distinct grappled, push, or prone outcomes. The examples retain the check and resulting Condition/movement as distinct components. |
| `action[XPHB:Escape a Grapple]` p. 367; `variantrule[XPHB:Grappling]` p. 367 | Escaping uses an acting-creature-selected Strength or Dexterity check against the grapple's escape DC and ends the Grappled Condition on success. The Action example references the defining Rule rather than inventing a fixed DC. |
| `action[XPHB:Magic]` p. 371; `action[PHB:Cast a Spell]` p. 192 | 2024 Magic action can cast an action-time spell or activate a feature/item; long casting repeats the Magic action and requires Concentration, with failure and slot behavior on concentration loss. PHB source describes action/reaction/minutes/hours as possible casting times, not a fixed one-minute duration. |
| `action[XGE:Identify a Spell]` p. 85; `action[PHB:Other Activity]` p. 190; `action[PHB:Improvising an Action]` p. 193 | Explicit action/reaction alternatives; open-ended interactions and DM-adjudicated timing; supports distinct alternative and `varied_gametime` forms. |
| `variantrule[XPHB:D20 Test]` p. 364; `Ability Check` p. 360; `Attack Roll` p. 361; `Saving Throw` p. 373 | Establishes the D20 family and its three kinds without merging their procedures. |
| `variantrule[XPHB:Death Saving Throw]` p. 365; `Dead` p. 365 | Death Saving Throw source record establishes its trigger, not all detailed roll outcomes. Only the trigger is placed in its example. |
| `variantrule[XPHB:Critical Hit]` p. 364 | Natural 20 on an attack roll hits regardless of modifiers/AC and doubles all damage dice while adding relevant modifiers once. This is represented by separate D20 and damage components. |
| `variantrule[XPHB:Difficult Terrain]` p. 366 | Every foot in the space costs one extra foot; the effect is non-cumulative. |
| `variantrule[XPHB:Unarmed Strike]` p. 377 | One Rule contains the attack roll, damage, grapple option, and shove option. The latter options use target-chosen Strength or Dexterity saves against DC 8 + Strength modifier + Proficiency Bonus; grappling also requires a free hand. |
| `condition[PHB:Exhaustion]` p. 291; `condition[XPHB:Exhaustion]` p. 365; `condition[PHB:Blinded]` p. 290; `condition[XPHB:Blinded]` p. 361 | Supports distinct exhaustion progressions and the actor/target direction of Blinded's attack effects. |
| `status[PHB:Concentration]` p. 203; `status[XPHB:Concentration]` p. 363 | Supports separate source-specific maintenance formulas and end triggers, including PHB environmental phenomena and voluntary termination without an action. |
| `disease[PHB:Blinding Sickness]` p. 227 | The full entry supports Wisdom check/save disadvantage and Blinded; those effects are typed in the fixture. |

### Known limits and open design decisions

- Action check/contest outcomes and `requirements` remain a starter vocabulary. Records such as Hide, Influence, Search, Overrun, and item activation have additional conditions, choices, or outcomes not covered by the examples. Do not claim complete Action source coverage.
- `varied_gametime` intentionally carries no default duration. `Other Activity` and DM-adjudicated improvisation can combine free interaction, action-cost activity, and rulings; a single fixed value cannot encode all source context.
- The Rule contract covers common D20 procedures plus selected combat, spatial, damage, magic, downtime, social, rest, and vehicle patterns. It does not yet model all 231 records. Travel at Sea, Ships in Combat, Tool Proficiencies, Downtime Revisited, faction/franchise systems, character progression, hazards, and other optional rules need further submodel investigation. A source heading alone does not justify child identity or a generic component.
- Current Rule schemas include specific numeric/enum fields for representative patterns, not every edition-specific exception, subsystem dependency, resource process, or source-defined choice. Examples are design fixtures, not proof of source coverage.
- Disease source records such as Cackle Fever, Sight Rot, and The Rusting combine contagion, timed symptoms, repeated saving throws, conditional consequences, recovery, immunity, transformation, and exceptions. The current Disease schema does not represent all those interactions; later domain-specific review must document the full original entry for any unsupported portion and must not pass empty shells.
- The repository still does not explain `ruleType` C/O/V/VO. The source field remains unmapped pending authoritative evidence or review.
- Human review is required for whether the finite Rule family list is a sufficient Phase 2 starting contract and which unsupported Rule subdomains warrant dedicated schemas next.

### Current consolidated revision validation

Ajv 8.17.1 with its Draft 2020-12 implementation compiled all **9 schemas** (the shared definitions and eight active domain models) and validated all **34 examples**, with zero failures. Strict compilation completed without schema warnings. An integrity scan checked all 34 example identities for uniqueness and canonical ID shape, all 24 typed reference target types against their referenced ID prefixes, and confirmed no example contains source `entries`, rich text, fonts, presentation flags, or workflow-status fields. A negative validation check confirmed that a metadata-only Disease shell fails its schema. `git diff --check` completed without whitespace errors.

These checks establish structural validity of the current fixtures and core identity/reference conventions; they do not prove complete source coverage or mechanical correctness. In particular, no normalized dataset or entry-by-entry omission/duplicate register was generated in this Phase 2 revision.

**Approval checkpoint:** Consolidated Phase 2 design is awaiting explicit review. Phase 3 conversion and backend integration have not started.

## Final Action and Rule contract refinement — review pending

This revision implements the remaining Action and Rule design refinements. It retains the earlier feedback, research, and validation sections above as historical evidence. The Condition, Disease, Language, Sense, Skill, and Status schemas and examples were only reformatted as JSON; their parsed content and approved behavior were not changed.

### Changes and design rationale

- Action triggers are typed objects with a `kind` discriminator. Source-evidenced variants cover observed spell casting, spell-defined reactions, a melee attack being made, opportunity attacks, attacks against an affected creature when the actor can see it, saves by the affected creature, creatures leaving reach, and perceivable circumstances. The trigger vocabulary has no free-text fallback. Reaction timing uses the same typed trigger contract.
- Action roll modifiers now reuse `D20_ROLL_MODIFIER` and separately retain actor, target, applicable roll and ability, establishing event, applicability trigger, duration, reaction availability/consumption, and usage limits. Mark distinguishes the melee-attack event that establishes its benefit from the opportunity attack on which the modifier applies. Dodge preserves the correct attack and Dexterity-saving-throw actors and targets.
- Action `CHECK_OR_CONTEST` components have local `operationId` values. Condition application/end and Status ending are distinct typed operations, with target type constrained to Condition or Status respectively and the recipient creature represented separately. Their `appliesWhen` values refer to successful checks, a named choice group, or an evidenced trigger. Action check outcome strings were removed. Stabilize a Creature now separates the Wisdom (Medicine) DC 10 check from the resulting stable state. Grapple and Shove retain their check requirements and source-supported attack replacement; Grapple also carries the source-defined voluntary release trigger.
- Rule mechanics now have stable local `componentId` values. An optional nested `composition` uses `AND` and `OR` groups that reference those components; component definitions are not copied into the composition. Separate `appliesWhen` values express hit, failed-save, and critical-hit applicability. Array order does not create a dependency.
- Rule mechanics share small value objects for attack rolls, saving throws, DCs, damage rolls, and D20 roll modifiers. The general saving throw fixture uses an effect-defined DC rather than inventing a number. Death Saving Throw remains a distinct procedure. Damage resolution remains separate from the critical-hit damage modifier.
- Unarmed Strike now has an `OR` composition: the Damage path combines an attack roll with damage on hit; the Grapple and Shove paths share one target-selected Strength or Dexterity saving-throw component, then select the corresponding outcome on `ON_FAILED_SAVE`. Shove preserves its separate choice between a five-foot push and Prone. Reusing one component avoids duplicate definitions of the same save procedure. Spell Attack reuses the shared attack-roll value without referring to a second Rule identity for the same procedure.

### Source evidence and updated examples

| Source record | Evidence represented |
| --- | --- |
| `action[DMG:Mark]`, p. 271 | A melee attack establishes Mark; an opportunity attack against the marked target gets Advantage through the attacker's next turn; reaction availability and one opportunity attack per turn are retained. |
| `action[PHB:Dodge]`, p. 192; `action[XPHB:Dodge]`, p. 366 | Attacks against the affected creature have Disadvantage when the attacker can see it; the affected creature has Advantage on Dexterity saves; incapacitation or Speed 0 ends the benefit. |
| `action[XGE:Identify a Spell]`, p. 85; `action[PHB:Cast a Spell]`, p. 192 | Identify a Spell supports action/reaction alternatives and casting observation. Cast a Spell preserves a spell-defined reaction and open-ended variable game time, not a fixed minute. |
| `action[PHB:Ready]`, p. 193; `action[PHB:Opportunity Attack]`, p. 195; `action[XPHB:Opportunity Attack]`, p. 371 | Ready identifies a perceivable circumstance as its trigger; opportunity attacks occur as a creature leaves reach, with source-specific visibility, hostility, movement, and reaction rules. The trigger vocabulary includes the evidenced event concepts; it does not encode every OA condition yet. |
| `action[PHB:Grapple]`, p. 195; `action[PHB:Shove]`, p. 195 | The checks, size/reach constraints, automatic success when the target is incapacitated, grapple application, Shove's push/Prone choice, and attack replacement are represented separately. Grapple release is represented as a distinct typed ending trigger. |
| `action[XPHB:Escape a Grapple]`, p. 367; `variantrule[XPHB:Grappling]`, p. 367 | The check uses an actor-selected Strength or Dexterity ability against the referenced escape DC; success, grappler incapacitation, and exceeding grapple range each end Grappled. |
| `action[PHB:Stabilize a Creature]`, p. 197 | A Wisdom (Medicine) check against DC 10 precedes stabilization; no duplicated generic outcome string remains. |
| `variantrule[XPHB:Critical Hit]`, p. 364 | A natural 20 on an attack roll is a critical hit that hits regardless of AC/modifiers; attack damage dice are rolled twice and relevant modifiers apply once. The damage modifier refers to the critical attack component. |
| `variantrule[XPHB:Unarmed Strike]`, p. 377 | The source-defined Damage, Grapple, and Shove choices retain their separate resolution requirements and outcomes under one Rule identity. |
| `variantrule[XPHB:Spell Attack]`, p. 374; `variantrule[XPHB:Saving Throw]`, p. 373; `variantrule[XPHB:Advantage]`, p. 360 | Spell attacks share attack-roll resolution; ordinary saves can be effect-defined, can be voluntarily failed, and automatically fail if the target lacks the used ability score; Advantage shares the typed D20 modifier value. |

Updated fixtures include Attack, Cast a Spell, Identify a Spell, Mark, Dodge, Grapple, Shove, Escape a Grapple, End Concentration, Stabilize a Creature, Critical Hit, Unarmed Strike, Spell Attack, Saving Throw, and Advantage. These remain contract examples rather than converted production records.

### Remaining limitations and review decisions

- JSON Schema validates the shape of local IDs and references but cannot ensure that every composition or applicability reference resolves to a compatible component. A separate integrity check is required. A future runtime contract must define the operational meaning of nested composition and named choice groups before execution; this Phase 2 model does not specify execution order.
- The Action trigger vocabulary covers observed source patterns and is intentionally finite. More complex triggers, conditional checks, object interactions, and action procedures still need evidence-led modeling. No failed-save or area-entry trigger was added without a supported Action fixture.
- Unarmed Strike's typed option object captures its source choice, but the Rule taxonomy remains a contract proposal for a broader 231-record source set. Complex procedures and optional subsystems still need separate design; no full Rule coverage is claimed.
- The critical-hit modifier expresses dice multiplication and one application of relevant modifiers, but does not calculate an attack's composed damage. Damage types and source-specific damage exceptions remain in the relevant Rule/effect contracts.
- Rule `ruleType` values C, O, V, and VO remain unresolved. No mapping was added.
- Human review remains required for the Action trigger/choice vocabulary and the Rule composition contract, especially nested groups and how a consumer executes them. These refinements do not approve Action or Rule and do not change the approval of the other six domains.

### Validation results for this revision

Ajv 8.17.1 Draft 2020-12 compiled all **9 schemas** with strict checks enabled and the `strictRequired` lint disabled for branch-local required properties in the existing union contracts; it validated all **34 examples** with zero schema failures after the final edits. A separate integrity pass checked 15 unique Rule component IDs, 10 composition references, 7 applicability references, 5 Action operation references, unique Action operation IDs, 28 typed references, 2 uses of the named Shove choice group, canonical and unique example IDs, Condition/Status target types, absence of free-text Action triggers/outcomes, and unchanged parsed contents for the six approved domains. Negative checks rejected an unknown composition component, an `ON_HIT` reference to a non-attack component, and a malformed typed trigger. At that time the nine schemas and 34 JSON examples matched two-space pretty formatting. The redundant example files have since been removed, and the current validator checks schema formatting plus canonical record coverage. `git diff --check` completed without whitespace errors.

Structural validation does not prove that a source interpretation is mechanically complete. Source comparisons above verify the represented mechanics of these fixtures only; no dataset conversion, source-entry accounting, or Phase 3 work was performed.

**Approval checkpoint:** Action and Rule remain awaiting human review. Phase 2 as a whole is not approved; Phase 3 conversion and backend integration have not started.

## Final Action and Rule consistency refinement — review pending

This targeted revision adds evaluable Action predicates, explicit Action choice groups, and a checked-in Rule/Action integrity validator. It retains the preceding feedback, research, and validation sections as history. The six approved domains were protected by a canonical-content hash baseline over their six schemas and thirteen examples; the regression check passed without a semantic change.

### Typed Action state and event conditions

- The shared Action contract now distinguishes event objects (`actionTrigger`) from state predicates (`conditionPredicate`). State predicates include `HAS_CONDITION` with a typed Condition reference, `SPEED_IS_ZERO`, `CAN_SEE`, and the grapple-specific `DISTANCE_EXCEEDS_RANGE`. Action applicability separates `CONDITION_MET` state from `TRIGGER_OCCURRED` events.
- Grapple's automatic success now checks the target's PHB Incapacitated Condition by canonical reference. Its voluntary release is the typed `GRAPPLER_RELEASES` event. Dodge uses a `CAN_SEE` predicate for its attack modifier and typed Condition/Speed predicates for ending its benefit. Escape a Grapple models the XPHB grappler Incapacitated state separately from the grapple-range predicate.
- Other expression-like Action strings were replaced with objects: movement after speed modifiers, casting time of at least one minute, and condition-valued modifier expiry. Timing triggers remain typed objects. The new predicates do not include a prose fallback.
- Action operation IDs use local slug IDs. Trigger, check-result, and choice-selection references are validated against operations in their owning Action. Condition IDs used by Action and Rule canonical records are checked against the original condition source records, so a syntactically valid but nonexistent Condition reference fails.

### Action choice semantics

`choiceGroups` now declare a local group ID, minimum and maximum selected operations, a trigger, and member operation IDs. Shove has `minimumSelected: 1` and `maximumSelected: 1`, triggered by success of its named check. Its push and Prone operations apply only when selected from that group. The validator checks unique group IDs and members, valid cardinality, resolvable operation IDs, single-group membership, and agreement between the group trigger and each member's `CHOICE_SELECTED` applicability. Cardinality is declared per group; no default cardinality is imposed on other Actions.

### Permanent Rule and Action integrity checks

Added `tools/scripts/step-2/validate-core-rules.cjs`, exposed as `npm run validate:core-rules`, with Ajv 8.17.1 pinned for Draft 2020-12 validation. It compiles every schema, validates every example, checks canonical identities and typed reference prefixes, confirms the approved-domain semantic baseline and two-space formatting, and runs positive and negative integrity cases.

Rule validation enforces unique local component IDs; resolved, nonduplicated composition references; and ID-only composition leaves. The inline composition tree is acyclic by construction; the utility also rejects a cyclic in-memory group graph and cycles in component applicability dependencies. It checks `ON_HIT` and `ON_CRITICAL_HIT` attack-roll targets, requires an explicit critical result for the latter, and requires an ordinary `SAVING_THROW` component for `ON_FAILED_SAVE`. It also rejects applicability that points across mutually exclusive OR branches. Common components should be factored above alternatives when each branch depends on them.

### Source evidence

| Source record | Evidence represented |
| --- | --- |
| `action[PHB:Grapple]`, p. 195 | The target being Incapacitated makes the grapple check automatically succeed; the grappler may release the target without an action. These are represented as a target state predicate and a separate release event. |
| `action[PHB:Dodge]`, p. 192; `action[XPHB:Dodge]`, p. 366 | Seeing the attacker gates attack Disadvantage; Incapacitated or Speed 0 ends the benefit. These conditions now have separate typed predicates. |
| `action[XPHB:Escape a Grapple]`, p. 367 | The Grappled Condition ends on a successful check, when the grappler has Incapacitated, or when distance exceeds grapple range. The last two conditions use distinct predicates rather than event-name strings. |
| `action[PHB:Shove]`, p. 195 | A successful check selects either a five-foot push or Prone; the two results are mutually exclusive and represented by an exactly-one group. |

### Validation and remaining limitations

`npm run validate:core-rules` passed **75 checks**: 9 schema compilation checks, 34 example validation checks, 8 positive integrity/regression checks, and 24 negative checks. The negative cases include malformed typed predicates/events, unknown source Condition IDs, missing and duplicate Action operation/group references, invalid cardinality, incompatible group membership, unresolved/duplicate Rule component references, duplicate mechanical definitions, cyclic composition and applicability, wrong dependency component types, contradictory OR/applicability, and inline component definitions. No checks failed. The validator also checks 14 Rule components across the fixtures and all 12 Action examples. `git diff --check` passed.

The validator confirms structural and cross-reference consistency, not full mechanical correctness or source coverage. Source evidence verifies the mechanics modeled in these examples. The Action vocabulary does not yet represent every opportunity-attack eligibility condition or every Action's conditional behavior. The Rule contracts remain an example-backed subset of the 231 source records. Composition gives explicit logical membership but does not define runtime execution order, and no Java execution behavior is added here.

The sandbox's offline npm cache lacked a required Ajv tarball, so `npm ci` could not bootstrap the pinned dev dependency in this run. The committed validation command was run with Ajv 8.17.1 already available in the environment; no network install was performed.

**Approval checkpoint:** Action and Rule remain awaiting explicit human approval. Condition, Disease, Language, Sense, Skill, and Status remain approved. Phase 2 is not approved as a whole; Phase 3 has not started.

## Approval record reconciliation — 2026-10-29

The preceding checkpoint accurately records the approval state of the revised Phase 2 design: Condition, Disease, Language, Sense, Skill, and Status have approved contracts; Action and Rule still require explicit human approval, so Phase 2 as a whole is not approved. Later Phase 3–6 work does not itself satisfy the roadmap's per-phase approval gates. The statement that Phase 3 had not started is superseded by the Phase 3 representative-conversion report and subsequent Phase 4–6 reports; this correction records chronology only and does not imply approval of Phase 2 or Phase 3.

## Explicit approval — 2026-10-29

The user explicitly approved the Action and Rule schemas and Phase 2 as a whole, accepting the documented limitations. This approval accepts the finite Action vocabulary, the example-backed Rule taxonomy, unresolved `ruleType` codes, the documented composition/execution boundary, and the remaining evidence-led refinements without changing those limitations or authorizing schema changes. This approval supersedes the preceding pending-approval checkpoints.
