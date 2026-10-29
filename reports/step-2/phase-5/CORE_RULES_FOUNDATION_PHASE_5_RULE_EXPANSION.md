# Core Rules Foundation Phase 5 Rule Expansion

## Status

**Incomplete; Rule expansion remains in progress.** This report preserves the original three-record batch as historical evidence and adds the later D20/resolution batch below. At the time of this historical conversion checkpoint, twenty additional source records had been examined; 203 of the 231 Rule records were then unexamined after excluding five Phase 3/4 representatives and 23 Phase 5 examined records. These are conversion/accounting figures, not the current semantic-discovery status. This report does not claim full-catalog semantic coverage or Phase 5 approval. Phase 6 has not started.

## Source inventory

The immutable source is `src/main/resources/data/variantrules.json`, collection `$.variantrule`. It contains **231 records**, matching the Phase 1 inventory. Source-code counts are:

| Source | Records |
| --- | ---: |
| XPHB | 115 |
| DMG | 44 |
| XGE | 22 |
| PHB | 13 |
| AI | 10 |
| GoS | 9 |
| AAG | 4 |
| TCE | 3 |
| MM | 3 |
| VRGR | 3 |
| PSI | 1 |
| VGM | 1 |
| SCAG | 1 |
| CoS | 1 |
| ESK | 1 |
| **Total** | **231** |

The five existing Phase 3/4 representatives are `rule:xphb:d20-test` (`$.variantrule[44]`), `rule:xphb:critical-hit` (`[38]`), `rule:xphb:unarmed-strike` (`[225]`), `rule:xphb:difficult-terrain` (`[53]`), and `rule:dmg:downtime-activity-crafting-a-magic-item` (`[62]`). Their mechanical interpretations remain unchanged in the approved Phase 3 location; the later authorized `ruleType` metadata was added when source data supplied it. The first Phase 5 batch directly interpreted three additional XPHB definitions: Ability Check (`[0]`), Attack Roll (`[14]`), and Saving Throw (`[187]`). The initial 223-unexamined count is historical and is superseded below.

## Initial three-record batch (historical snapshot)

The following batch findings and counts describe the original execution before the later `ruleType` decision and D20/resolution expansion. They are retained as historical evidence; the current totals appear in **D20/Resolution Batch** below.

## First converted batch

| Source identity | Source locator | Canonical output | Classification | Interpretation |
| --- | --- | --- | --- | --- |
| `rule:xphb:ability-check` | `$.variantrule[0]` | `datasets/phase-5/step-2/core-rules/v1/rule/xphb-ability-check.json` | FULL | Ability-check D20 test with the six source-defined ability inputs; explicit reference to the XPHB D20 Test. The definition does not select a particular Skill or establish a DC. |
| `rule:xphb:attack-roll` | `$.variantrule[14]` | `datasets/phase-5/step-2/core-rules/v1/rule/xphb-attack-roll.json` | FULL | Attack-roll forms are weapon, Unarmed Strike, and spell; explicit reference to the XPHB D20 Test. |
| `rule:xphb:saving-throw` | `$.variantrule[187]` | `datasets/phase-5/step-2/core-rules/v1/rule/xphb-saving-throw.json` | FULL | Effect-selected ability and DC; a creature may choose to fail; automatic failure if it lacks the ability score used. |

These interpretations use existing approved Rule and shared definitions. The source `ruleType` value `C` is retained as an unresolved structured-field issue for each record. No meaning is inferred from that code. No source text is copied into canonical JSON.

## Catalog-wide pattern context and current contract fit

The Phase 2 Action/Rule mechanics research provides the prior title-led pattern census: 24 D20-test records, 35 movement/spatial records, 21 damage/health/state records, 16 spell/magic records, and 38 downtime records. These sets overlap and are not a replacement for the source-by-source semantic audit required for this expansion. That research also identified 101 records outside those five named groups, including social, creature/character, ship, campaign, and setting procedures.

The approved Rule schema currently has typed structures for D20 tests and modifiers, movement/spatial mechanics, damage/health/death, spell/magic, downtime, social, rest, vehicles, and Unarmed Strike. This batch demonstrates direct fit for three D20 definitions. The catalog-wide audit has not yet established which remaining records can be represented by those structures or where a source-specific gap is genuine.

The source inventory contains broad multi-procedure records documented in the earlier research, including Action Options, Initiative Variants, Travel at Sea, Ships in Combat, Tool Proficiencies, and Downtime Revisited. Their components must be interpreted separately where appropriate; headings alone do not justify child Rule identities. Their source-specific coverage remains unaudited in this execution.

## Initial accounting and coverage (historical snapshot)

The Phase 5 manifest now includes `variantrules.json` and a Rule batch for the three interpreted records. Their three source entry roots are classified INTERPRETED. The three unresolved `ruleType` fields are separately recorded as `SOURCE_AMBIGUOUS`. The Rule omission and duplicate registers were not changed because none of these three entries required either disposition.

| Measure | Before Rule batch | Current manifest |
| --- | ---: | ---: |
| Phase 5 canonical records | 325 | 328 |
| Phase 5 FULL | 179 | 182 |
| Phase 5 PARTIAL | 146 | 146 |
| Phase 5 UNSUPPORTED | 3 | 3 |
| Phase 5 source-entry roots | 276 | 279 |
| Phase 5 entry dispositions: INTERPRETED / DUPLICATE / OMITTED | 204 / 24 / 190 | 207 / 24 / 190 |
| Phase 5 structured-field issues | 154 | 157 |

These are current Phase 5 manifest totals, not totals for the whole Step 2 Rule catalog. The Rule batch has 3 FULL, 0 PARTIAL, and 0 UNSUPPORTED records. The five representatives remain separately accounted for in Phase 3/4. No full/partial/unsupported classification is claimed for the 223 remaining source records.

## Contract-gap candidates requiring source-by-source confirmation

Prior Phase 2 research identifies likely areas where the approved contract may be insufficient or too coarse, including complex downtime and campaign procedures, large multi-part maritime procedures, object durability, broad character or creature rules, and setting-specific systems. These are not yet accepted gaps for the remaining catalog: each requires exact source-path review, confirmation that an existing structure cannot express it, affected-record counts, and a bounded proposal. No schema change is implemented here.

The meanings of source `ruleType` codes `C`, `O`, `V`, and `VO` remain unresolved. This batch encounters `C` on the three new records and records the ambiguity without mapping it to a full-word classification.

## Initial validation (historical snapshot)

`npm run validate:core-rules` passed after adding the first batch:

- 9 Draft 2020-12 schemas compiled.
- 58 examples validated.
- 34 Phase 3/4 canonical records validated.
- 328 Phase 5 canonical records validated.
- 511 permanent checks passed.
- The new assertions verify the three Rule source identities, the ability list, attack-roll forms, saving-throw choice/failure semantics, and unresolved `ruleType` accounting.

This establishes structural validity and selected source-backed assertions only. It does not establish semantic correctness or complete Rule conversion. `git diff --check` is recorded after the final report updates.

## Initial remaining work (historical snapshot)

The initial execution's full-catalog Rule expansion was **not complete**: at that point 223 of 226 non-representative records remained unexamined. The D20/resolution batch below supersedes that count. Step 2 Phase 5 remains open and unapproved; do not begin another domain, Phase 6, or Step 3.

## D20/Resolution Batch

### `ruleType` source verification and contract

The source dataset uses only `C`, `O`, `V`, and `VO` for populated `ruleType` values, plus records where the property is absent. The human reviewer supplied the mappings below; the 5etools Variant Rules listing exposes the corresponding Type classifications ([5etools Variant Rules](https://5e.tools/variantrules.html)). Repository source data confirms the codes and their locations, but does not itself define the abbreviations. No additional code was inferred.

| Source code | Verified source classification | Canonical enum |
| --- | --- | --- |
| `C` | Core | `CORE` |
| `O` | Optional | `OPTIONAL` |
| `V` | Variant | `VARIANT` |
| `VO` | Variant Optional | `VARIANT_OPTIONAL` |

The Rule schema now has an optional bounded `ruleType` property with exactly these four values. The property is populated on existing Phase 3/4 and Phase 5 Rule records and Rule examples only when the source supplies a code. The crafting representative is `OPTIONAL`; the other four Phase 3/4 Rule representatives are `CORE`. Unknown codes are rejected. The prior `SOURCE_AMBIGUOUS` accounting for populated `ruleType` fields was removed, while absence remains absence. Canonical identities, edition metadata, and mechanical interpretations were otherwise preserved.

### Candidate selection and source inventory

The immutable source remains `src/main/resources/data/variantrules.json`, collection `$.variantrule`, with 231 records. Source distribution is unchanged from the verified inventory above. Five Phase 3/4 records and three earlier Phase 5 records were excluded from the new-record count. The 20 entries below were selected after checking their source objects; the selection covers D20 inputs and outcomes, modifiers, checks, skills/proficiency, initiative, and attack-resolution references without padding the batch with unrelated Rule families.

| Source-qualified identity | Exact source locator | Batch result | Canonical result or reason |
| --- | --- | --- | --- |
| `rule:xphb:ability-score-and-modifier` | `$.variantrule[1]` | UNSUPPORTED | Ability-score/modifier conversion is not represented by a Rule mechanic; no derived calculation fallback was added. |
| `rule:xphb:advantage` | `$.variantrule[5]` | FULL | New canonical `xphb-advantage.json`; two-d20 selection, cancellation, and non-stacking are typed. |
| `rule:xphb:armor-class` | `$.variantrule[12]` | UNSUPPORTED | AC derivation depends on armor, shield, ability, and character-state data outside this batch's approved structures. |
| `rule:dmg:automatic-success` | `$.variantrule[17]` | UNSUPPORTED | The optional procedure's conditions/outcomes do not fit an existing Rule mechanic without a new contract. |
| `rule:xphb:death-saving-throw` | `$.variantrule[51]` | PARTIAL | New canonical `xphb-death-saving-throw.json` preserves the turn/zero-HP procedure; the source's Player Character restriction is not typed. |
| `rule:xphb:difficulty-class` | `$.variantrule[54]` | UNSUPPORTED | DC formula forms are not expressible by the current Rule contract. |
| `rule:xphb:disadvantage` | `$.variantrule[56]` | FULL | New canonical `xphb-disadvantage.json`; two-d20 selection, cancellation, and non-stacking are typed. |
| `rule:xphb:expertise` | `$.variantrule[103]` | UNSUPPORTED | Proficiency scaling and character acquisition are not represented by this Rule contract. |
| `rule:dmg:flanking` | `$.variantrule[113]` | UNSUPPORTED | Optional positional procedure depends on encounter geometry and attacker/ally state. |
| `rule:dmg:hitting-cover` | `$.variantrule[128]` | UNSUPPORTED | The redirect from a missed creature attack to cover, cover AC, and object damage is not covered by existing Rule structures. |
| `rule:xphb:initiative` | `$.variantrule[136]` | UNSUPPORTED | No canonical output: its turn-order dependency and distinct Initiative score formula cannot be represented accurately by the current Rule schema. The tempting passive-ability-check encoding would be misleading. |
| `rule:xphb:passive-perception` | `$.variantrule[166]` | PARTIAL | New canonical `xphb-passive-perception.json` preserves Wisdom (Perception) and modifier references; the score formula remains unsupported. |
| `rule:xphb:proficiency` | `$.variantrule[175]` | UNSUPPORTED | Application and scaling depend on character proficiency state and multiple task domains. |
| `rule:dmg:proficiency-dice` | `$.variantrule[176]` | UNSUPPORTED | Optional replacement of proficiency bonus affects character progression and many roll procedures. |
| `rule:xphb:save` | `$.variantrule[186]` | FULL, DUPLICATE | Source entry is accounted as duplicate of `rule:xphb:saving-throw`; no second canonical definition was created. |
| `rule:xphb:skill` | `$.variantrule[201]` | UNSUPPORTED | The general skill-to-ability framework overlaps Skill and character task resolution and is not a separate represented Rule procedure. |
| `rule:dmg:skill-variants` | `$.variantrule[202]` | UNSUPPORTED | The optional subsystem modifies broad skill use and depends on the unmodeled character/check framework. |
| `rule:phb:skills-with-different-abilities` | `$.variantrule[203]` | PARTIAL | New canonical `phb-skills-with-different-abilities.json` preserves explicit Athletics and Intimidation pairings; open-ended DM pairings are omitted. |
| `rule:xphb:spell-attack` | `$.variantrule[207]` | PARTIAL | New canonical `xphb-spell-attack.json` represents attack resolution; its Magical Effect dependency has no exact canonical target in the reviewed set. |
| `rule:xphb:weapon-attack` | `$.variantrule[230]` | FULL, DUPLICATE | Source definition is accounted as duplicate of `rule:xphb:attack-roll`; no second canonical definition was created. |

The other five Phase 3/4 identities and the three earlier Phase 5 identities remain excluded from the new 20-record count. Together with these 20, 28 of 231 Rule source records have been reviewed; **203 remain unexamined**, not presumed unrelated. `Initiative Variants` (`rule:dmg:initiative-variants`, `$.variantrule[137]`) remains a directly related candidate for a later batch: it combines initiative score, side initiative, speed factor, modifiers, rolling, and turns, so it requires a dedicated multi-procedure review. Other catalog records remain unclassified until inspected.

### Coverage and accounting

| Measure | Before this D20 batch | Current Phase 5 manifest |
| --- | ---: | ---: |
| Phase 5 canonical records | 328 | 334 |
| Phase 5 FULL | 182 | 186 |
| Phase 5 PARTIAL | 146 | 150 |
| Phase 5 UNSUPPORTED | 3 | 15 |
| Phase 5 source-entry roots | 279 | 314 |
| Entry classification parts: INTERPRETED / DUPLICATE / OMITTED | 207 / 24 / 190 | 213 / 26 / 220 |
| Structured-field issues | 157 | 154 |

The 20 newly examined records yielded 2 new canonical FULL records, 4 PARTIAL canonical records, 12 UNSUPPORTED records, and 2 fully duplicated records that correctly reuse existing canonical identities. Six new canonical files were produced. Their 35 source-entry roots contain 41 independently classified parts: 9 INTERPRETED, 2 DUPLICATE, and 30 OMITTED. The batchResults entry includes the three earlier Rule conversions, hence its totals are 23 examined records, 9 Phase 5 Rule outputs, 7 FULL, 4 PARTIAL, and 12 UNSUPPORTED; its 38 total roots and 9/2/30 classification parts reconcile to the Phase 5 Rule entries.

At that historical conversion checkpoint, the five Phase 3/4 representatives and 23 Phase 5 examined Rule records had 11 FULL, 5 PARTIAL, and 12 UNSUPPORTED conversion classifications; then-unexamined records were excluded from those status totals. Phase 3 has five Rule canonical records; Phase 5 has nine. No unexamined source record received a conversion classification.

### Contract limitations and ownership

Existing D20 test, ability-check, attack-roll, saving-throw, and roll-modifier definitions supported the three earlier records plus Advantage and Disadvantage. The current structures also support useful portions of Passive Perception, Death Saving Throw, paired skill/ability examples, and Spell Attack, but not every source clause. Initiative was left unsupported after source review showed that the passive ability-check representation would conflate two different score procedures. The omissions identify bounded future review candidates rather than approved schema changes:

- A finite DC representation for fixed values plus Spell level and “greater of a fixed minimum and target ability score.”
- Character progression/proficiency and ability-score/modifier procedures, owned with Character Building and Skill boundaries in view.
- Armor Class derivation and equipment dependencies, for a future Equipment/character-state review.
- Death Saving Throw target applicability, Initiative turn order and its separate score formula/modifiers, and passive-score formulas.
- Positional Flanking and Hitting Cover's attack redirection/object-damage procedure, with encounter geometry and object durability dependencies.
- Automatic Success and Skill Variants, which need source-specific contract review before assigning a reusable Rule mechanic.
- Spell Attack's exact Magical Effect relationship; no unresolved target was fabricated.

`Initiative Variants` and broad `Skill`, `Skill Variants`, proficiency, campaign, and equipment procedures must not be collapsed into a generic resolution engine. These are candidates for subsequent review, not this batch's schema authorization.

### Validation

The current executed `npm run validate:core-rules` result is:

- 9 Draft 2020-12 schemas compiled.
- 58 examples validated.
- 34 Phase 3/4 canonical records validated.
- 334 Phase 5 canonical records validated.
- 520 permanent checks passed.
- 14 Rule components and 27 Action records checked by integrity validation.

New assertions verify all four `ruleType` mappings appear in the source-backed mapping set, canonical values match source values, unknown enums fail schema validation, and source records cannot be assigned an incorrect mapped value. The Rule assertions also verify the batch identities, key D20 mechanics, linked/duplicate entries, and all 231 structural inventory identities and nested text paths. These results establish structure and selected assertions, not complete semantic correctness. `git diff --check` is run after report edits.

### Current stop point

This execution covers only the `ruleType` metadata decision and the bounded D20/resolution batch. No other Rule category was expanded. Phase 5 remains open and unapproved; 203 Rule records remain unexamined. Do not automatically start another batch, Phase 6, or Step 3.

## Historical semantic-discovery checkpoint (superseded)

At this historical checkpoint the separate semantic-discovery inventory contained 200 REVIEWED and 31 PENDING Rule identities. The following status is superseded by the final discovery checkpoint below. These findings never altered Rule canonical conversion totals or source-accounting dispositions.

Since the initial 28 reviewed interpretations, Codex directly examined 172 additional source objects, including nested procedures, and recorded mechanics, inputs, conditions, outcomes, exceptions, and dependencies with exact JSONPaths. The companion JSON inventory is authoritative for record-level findings. At that historical checkpoint the pattern catalog contained 19 patterns and the gap inventory had 26 families. The notable newer interpretations include Initiative Variants, DMG Healing, Inspiration, Honor/Sanity, DMG Madness, DMG Injuries, and the self-contained Prophet?s Gambit card game. The latter two discovery findings are tracked as R-GAP-23 and R-GAP-22 respectively.

The newly examined multi-procedure examples were not flattened: Honor/Sanity separates optional score creation, fallback ability use, Honor and Sanity checks/saves, and score changes; Madness separates resistance, severity/duration, three result tables, and recovery; Inspiration separates the Advantage benefit from award criteria, token limits, award timing, and optional player/DM variants. Their remaining limitations and dependencies are recorded in the semantic inventory and gap families.

Within the reviewed records, the most prevalent gap family is detailed downtime resolution (27 records), followed by rest/recovery procedures (7) and proficiency/ability scaling (4). These counts represent records associated with a gap, not individual unsupported clauses. Cross-domain dependencies repeatedly involve Character Building and character state (ability scores, proficiency, resources), Equipment and inventory (costs, weights, objects), Encounter state (position, initiative, timing), and Spell/Disease/Condition contracts (external effects and recovery).

**Remaining semantic discovery:** 31 records are still PENDING. The source catalog is therefore not yet fully semantically reviewed; this checkpoint does not authorize conversion, schema changes, Phase 6, or Phase 5 approval.


### Exact remaining PENDING identities

These records have structural inventory only and have not received semantic classifications:

- rule:ai:downtime-and-franchise-activity-explore-territory
- rule:ai:downtime-and-franchise-activity-franchise-restructuring
- rule:ai:downtime-and-franchise-activity-headquarters-modification
- rule:ai:downtime-and-franchise-activity-marketeering
- rule:ai:downtime-and-franchise-activity-philanthropic-enterprise
- rule:ai:downtime-and-franchise-activity-running-a-franchise
- rule:ai:downtime-and-franchise-activity-schmoozing
- rule:ai:downtime-and-franchise-activity-scrutineering
- rule:ai:downtime-and-franchise-activity-shady-business-practice
- rule:ai:downtime-and-franchise-activity-team-building
- rule:xge:downtime-revisited
- rule:psi:emrakul-s-madness
- rule:gos:encounters-at-sea
- rule:vrgr:haunted-traps
- rule:phb:multiclassing
- rule:gos:mysterious-islands
- rule:gos:ocean-environs
- rule:gos:officers-and-crew
- rule:dmg:planar-effects
- rule:gos:random-ships
- rule:xge:shared-campaign-variant-rules
- rule:gos:ship-stat-blocks
- rule:aag:ship-to-ship-combat
- rule:gos:ships-in-combat
- rule:esk:sidekicks
- rule:tce:sidekicks
- rule:xge:spellcasting
- rule:gos:superior-ship-upgrades
- rule:vrgr:survivors
- rule:xge:tool-proficiencies
- rule:gos:travel-at-sea

## Final catalog-wide semantic discovery checkpoint

Codex directly examined the remaining 31 source objects and reconciled the full 231-record inventory. The semantic inventory now contains **231 REVIEWED, 0 PENDING**, with fit **60 SUPPORTED, 156 PARTIAL, 15 UNSUPPORTED, 0 NOT_ASSESSED**. The separate pattern catalog contains **21 patterns**; the gap inventory contains **46 families**. These are semantic discovery totals only. They do not change the historical 28-record canonical conversion results, Rule conversion accounting, or source dispositions.

The final records add multi-check Acquisitions Incorporated franchise procedures and XGE downtime timing/rivals; score-banded Emrakul corruption; daily voyage encounters and linked table-driven island/ship generation; haunted-trap perception and disarming; multiclass level/feature/spell-slot rules; extensive plane-specific procedures; maritime environmental hazards, crew-state group checks and ship components; shared-campaign checkpoint/treasure-point economy; ESK/TCE sidekick progression; XGE spell observability, invalid targets and grid-area placement; temporary Survivor advancement/talents; and XGE tool/Skill guidance. The semantic inventory preserves individual procedures and exact paths for multi-procedure records.

Final coverage emphasizes the detail and ownership boundaries rather than turning these clusters into generic mechanics. R-GAP-13 applies to **38 records** (27 previously reviewed downtime procedures, 10 Acquisitions Incorporated activities, and XGE Downtime Revisited). R-GAP-31 maritime operations apply to **12 records**; R-GAP-41 special movement procedures apply to **9 records**; R-GAP-06 proficiency/scaling applies to 6; table-driven campaign/encounter generation R-GAP-28 applies to 4. Spatial area/origin procedures R-GAP-36 apply to 3; grid distance and diagonal movement R-GAP-45 apply to 2; the distinct Hero Point resource procedure R-GAP-46 applies to 1. Counts are distinct records and overlap across families. New source-specific or cross-domain findings include haunted trap disarm (Trap/Hazard), creature/class progression (multiclass, sidekicks, survivors), planar state effects, vehicle/crew procedures, invalid spell targets, and object durability/cleaving/morale procedures.

Pattern assignments remain record-level: the catalog includes the exact inspected observation paths for each affected record, while each observation description supplies the mechanic-to-pattern evidence. A per-observation pattern-ID field was not added; this is a traceability limitation for later contract design, not an unreviewed source record.

Fifty-four PARTIAL records have no gap-family reference; each has a record-level, source-specific fitRationale naming the uncovered behavior and responsible domain or adjudication boundary. Eight records previously lacking a reusable gap reference were associated with the reviewed families R-GAP-31, R-GAP-36, R-GAP-45, and R-GAP-46. Exact duplicate mechanic observations (same path and description) were removed, while different mechanics sharing a path were retained. The discovery validator checks this condition, partial-without-gap rationales, source identity/path correspondence, and all pattern/gap references.

Verification: the discovery inventory validator passes for 231 unique identities (231 REVIEWED, 0 PENDING), source paths, pattern/gap references, and reconciled catalog tables. npm run validate:core-rules passes with 9 schemas compiled, 58 examples, 34 Phase 3/4 canonical records, 334 Phase 5 canonical records, and 520 permanent checks. git diff --check exited 0; Git emitted LF-to-CRLF working-tree warnings. These checks establish structure and references, not complete semantic correctness. **Canonical Rule conversion remains at its prior checkpoint and is not complete. Phase 5 remains open and unapproved.**

## Rule Gap Implementation — Batch 1

The approved Batch 1 implementation adds support for R-GAP-01 ability-score modifier mapping, R-GAP-05 finite Difficulty Class values, R-GAP-06 proficiency/scaling, and R-GAP-10 passive-score construction. Details and source-level decisions are recorded in [the Batch 1 implementation report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAPS_BATCH_1.md).

Eight Rule outputs were added and `rule:xphb:passive-perception` was extended. The Phase 5 Rule batch now accounts for 25 records (14 FULL, 5 PARTIAL, 6 UNSUPPORTED), with 17 canonical outputs. Two FULL aliases reuse existing records and do not create duplicate outputs. Batch accounting covers 50 entry roots: 22 INTERPRETED, 2 DUPLICATE, and 31 OMITTED classifications. The Rule catalog checkpoint including five Phase 3/4 representatives is 18 FULL, 6 PARTIAL, and 6 UNSUPPORTED, with 22 canonical Rule outputs.

The Phase 5 manifest now reports 342 canonical outputs across examined domains; source-record classifications total 193 FULL, 151 PARTIAL, and 9 UNSUPPORTED. Validation passes with 9 schemas, 68 examples, 34 Phase 3/4 canonical records, 342 Phase 5 canonical records, 552 permanent checks, and 24 Rule components checked. This is a Batch 1 checkpoint only; no other Rule gap family has been implemented and Phase 5 remains open and unapproved.

## Rule Gaps Batch 2 update

The later approved Batch 2 implements R-GAP-04, R-GAP-07, the standard-Cover scope of R-GAP-08, and R-GAP-11. See the [Batch 2 implementation report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAPS_BATCH_2.md). The original R-GAP-08 inventory described optional DMG Hitting Cover; the approved scope was standard XPHB Cover. Hitting Cover remains mechanically unsupported and now has an identity-only canonical Rule record.

The Phase 5 Rule batch accounts for 26 source records (18 FULL, 4 PARTIAL, 4 UNSUPPORTED), with 21 canonical outputs and 51 source-entry roots. Its dispositions are 25 INTERPRETED, 3 DUPLICATE, and 28 OMITTED. Including five Phase 3/4 representatives, 31 Rule identities are accounted (22 FULL, 5 PARTIAL, 4 UNSUPPORTED) and 26 canonical Rule outputs exist. Two hundred Rule records remain pending conversion accounting.

Across the Phase 5 manifest, 346 canonical outputs are present; source-record classifications are 197 FULL, 150 PARTIAL, and 7 UNSUPPORTED. Accounting covers 327 source-entry roots and 229 INTERPRETED, 27 DUPLICATE, and 218 OMITTED classification parts. Validation passes with 9 schemas, 73 examples, 34 Phase 3/4 records, 346 Phase 5 records, 569 permanent checks, and 30 Rule components checked. These results are structural validation plus selected source-backed assertions, not proof of complete mechanical correctness. Phase 5 remains open and unapproved; Batch 3 has not started.

## Rule Gaps Batch 3 update

The approved third Rule batch implements R-GAP-09, R-GAP-17, R-GAP-18, and R-GAP-20. See the [Batch 3 implementation report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAPS_BATCH_3.md). It adds finite Initiative score/side-initiative procedures, Strength/size carrying multipliers and optional PHB encumbrance thresholds, bounded rest/recovery outcomes, and XPHB Resistance/Vulnerability calculations. DMG Speed Factor, planar effects, Action/resource portions of Healing Surge, natural-sleep execution, spell-slot recovery, and Equipment dependencies remain explicitly omitted or deferred.

The Rule batch now accounts for 39 non-representative records (five representatives excluded): 25 FULL, 10 PARTIAL, and 4 UNSUPPORTED, with 35 Phase 5 canonical Rule outputs and 112 roots. Across the full Rule catalog, 44 records including five representatives are accounted (29 FULL, 11 PARTIAL, 4 UNSUPPORTED); 187 remain pending conversion accounting. The Phase 5 manifest now contains 360 canonical outputs (204 FULL, 156 PARTIAL, 7 UNSUPPORTED), 388 roots, and 256 INTERPRETED, 27 DUPLICATE, and 254 OMITTED classification parts. Structured-field/reference issues total 155.

Validation passes with 9 schemas, 79 examples, 34 Phase 3/4 records, 360 Phase 5 records, and 596 permanent checks. Phase 5 remains open and unapproved; Batch 4 and Phase 6 have not started.

## Rule Gaps Batch 4 update

The approved Batch 4 implements R-GAP-15, R-GAP-36, R-GAP-41, and R-GAP-45. See [the Batch 4 implementation report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAPS_BATCH_4.md). It adds source-bounded falling damage and flight-fall conditions; all six XPHB area shapes, origin rules, and line-of-effect behavior; movement procedures for burrowing, climbing, crawling, flying, hovering, jumping, swimming, and special Speed selection; and bounded grid movement costs.

The source review found four directly related XPHB movement records not associated with R-GAP-41 in the provisional gap list: Climb Speed, Crawling, Fly Speed, and Jumping. These were added because their source entries define the approved movement-cost exception or a reusable movement procedure. Batch 4 therefore adds 23 Rule identities, 20 FULL and 3 PARTIAL. The four optional/special clauses intentionally omitted are XGE Rate of Falling, its optional flying-speed reduction and later-turn halt, DMG diagonal range measurement, and PHB grid corner/range procedures. XGE Spellcasting's grid template/token methods remain outside this batch's geometry scope and the multi-procedure Rule remains pending conversion accounting.

The Rule conversion checkpoint is now 67 source identities including five Phase 3/4 representatives: 49 FULL, 14 PARTIAL, and 4 UNSUPPORTED. There are 63 canonical Rule outputs: 58 Phase 5 outputs and 5 representatives. Four identities have no unique Phase 5 output because two reuse represented Rule identities and two unsupported records remain without a canonical shell. Of the 187 previously pending identities, 164 remain pending conversion accounting.

The Phase 5 manifest now reports 383 canonical outputs, 224 FULL, 159 PARTIAL, and 7 UNSUPPORTED. It accounts for 428 source-entry roots and 294 INTERPRETED, 28 DUPLICATE, and 258 OMITTED classification parts. The two absent reprint targets on DMG Diagonals and XGE Falling remain recorded as `REFERENCE_UNRESOLVED` source-field issues; no identities were invented.

Validation passes with 9 Draft 2020-12 schemas, 89 examples, 34 Phase 3/4 canonical records, 383 Phase 5 canonical records, 638 permanent checks, 49 Rule example components checked, and 27 Action records checked. Batch 4 closes the currently approved Rule gap implementation batches only. Rule catalog expansion remains incomplete, Phase 5 remains open and unapproved, and Phase 6 has not started.

## RULE-CONV-01 — Glossary and Definitions

The first conversion-plan batch converted only its 12 assigned XPHB glossary identities. See the [RULE-CONV-01 implementation report](CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_01.md). All 12 have reference-only canonical outputs using the existing Rule v1 schema. Eight are FULL, three PARTIAL, and one UNSUPPORTED. No schema change or new mechanic was introduced.

The Rule conversion checkpoint is now 79 of 231 identities accounted (57 FULL, 17 PARTIAL, 5 UNSUPPORTED), with 152 pending conversion accounting. There are 75 canonical Rule outputs including five Phase 3/4 representatives, and 395 Phase 5 canonical outputs overall. The Phase 5 source accounting now covers 443 entry roots, with 312 INTERPRETED, 28 DUPLICATE, and 291 OMITTED portions.

Validation passes with 9 schemas, 89 examples, 34 Phase 3/4 records, 395 Phase 5 records, and 652 permanent checks. The legacy `ruleCatalogSummary.pendingSemanticReviewCount` remains unchanged at 164 by instruction; it is inconsistent with the complete semantic discovery inventory and current conversion-pending count of 152. That field correction remains separately pending. No subsequent conversion batch has started; Phase 5 remains open and unapproved.
