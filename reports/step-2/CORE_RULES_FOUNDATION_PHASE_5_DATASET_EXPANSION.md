# Core Rules Foundation Phase 5 Dataset Expansion

## Closure status

**Phase 5 is complete and approved.** The initial status and intermediate checkpoint statements preserved below are historical snapshots and are superseded by the [Phase 5 closure report](CORE_RULES_FOUNDATION_PHASE_5_CLOSURE.md) and the final accounting manifest. The final checkpoint is 588 in-scope identities, 585 canonical outputs (551 Phase 5 plus 34 Phase 3 representatives), and three explicitly accepted unsupported alternatives without canonical shells. The six font-only LanguageScript records remain excluded by approved scope. Final validation passed 9 schemas, 89 examples, 34 Phase 3 records, 551 Phase 5 records, and 838 permanent checks.

The Phase 3/4 representative baseline remains separately identified from the 554 Phase 5 expansion identities. Phase 6 is not covered by this closure.

## Historical expansion snapshots

This section retains the source inventory and batch-level history. Intermediate totals and status statements below describe their original checkpoints and are superseded by the closure report.

## Source inventory reconciliation

The current eight-domain inventory contains 588 records, matching the Phase 1 total. Phase 1's 594 count also included six LanguageScript records; that canonical domain remains out of scope. Source variations and repeated names remain distinct source-qualified records.

| Domain | Source records | Phase 3 representatives | Remaining before expansion | Phase 5 examined | Remaining now |
|---|---:|---:|---:|---:|---:|
| Action | 48 | 6 | 42 | 42 | 0 |
| Rule | 231 | 5 | 226 | 0 | 226 |
| Skill | 36 | 3 | 33 | 33 | 0 |
| Condition | 30 | 4 | 26 | 26 | 0 |
| Disease | 29 | 3 | 26 | 26 | 0 |
| Status | 5 | 5 | 0 | 0 | 0 |
| Sense | 8 | 4 | 4 | 4 | 0 |
| Language | 201 | 4 | 197 | 197 | 0 |
| **Total** | **588** | **34** | **554** | **328** | **226** |

The 34 Phase 3 representative source records are excluded from Phase 5 conversion work. The domain-by-source counts are preserved in the source dataset and Phase 1 inventory; Action includes DMG 8, PHB 20, XPHB 18, XGE 2; Skill PHB 18/XPHB 18; Condition PHB 15/XPHB 15; Sense PHB 3/XPHB 4/MM 1. Full Rule, Disease, and Language source breakdowns were inventoried directly from their source files and retain source codes as given.

## Completed batches

| Domain / pattern batch | Examined | Canonical output | Fully represented | Partial | Unsupported | Entry roots |
|---|---:|---:|---:|---:|---:|---:|
| Skill | 33 | 33 | 33 | 0 | 0 | 35 |
| Condition | 26 | 26 | 25 | 1 | 0 | 39 |
| Sense | 4 | 4 | 1 | 3 | 0 | 5 |
| Action: full-catalog expansion and ACT-1–ACT-4 refinement | 42 | 40 | 27 | 13 | 2 | 84 |
| Disease | 26 | 25 | 22 | 3 | 1 | 73 |
| Language | 197 | 197 | 71 | 126 | 0 | 40 |
| **Total** | **328** | **325** | **179** | **146** | **3** | **276** |

The current Phase 5 accounting manifest records 204 interpreted, 24 duplicate, and 190 omitted classification parts across 276 source-entry roots. Omitted portions are preserved in their model-specific registers. The full, partial, and unsupported labels describe only records examined to date.

### Skill

All 33 non-representative PHB/XPHB Skill records were converted using their explicit source ability association. Descriptive task examples were not added as canonical mechanics. Repeated ability associations and excluded descriptive text are accounted for in the Skill registers.

### Condition

All 26 non-representative PHB/XPHB records were interpreted using the approved Phase 5 Condition refinements. Charmed, Frightened, Incapacitated, and Invisible in both editions now have typed canonical records. Condition mechanics include relational roles and predicates, active restrictions, saving-throw failures, Condition persistence, movement constraints, critical-hit outcomes, and physical-state consequences. The PHB/XPHB distinctions are preserved. PHB Petrified remains partial solely for suspending pre-existing poison and Disease progression; its immunity is represented separately. The four Phase 3 representatives remain in their frozen location; XPHB Blinded's outgoing attack target was corrected as explicitly authorized.

The initial Condition batch preserved self-directed attack modifiers using the existing generic target field. The subsequent authorized Condition refinement is recorded in the [Condition implementation report](CORE_RULES_FOUNDATION_PHASE_5_CONDITION_IMPLEMENTATION.md), including the Condition schema extensions and updated canonical records.

### Sense

The four non-representative Sense records are PHB Blindsight, XPHB Blindsight, XPHB Darkvision, and XPHB Truesight. Darkvision's independent light transformations and grayscale color limitation are represented. XPHB Blindsight retains its invisible-target and Blinded interactions but omits Total Cover applicability, which has no typed Sense structure. PHB Blindsight is represented as nonvisual perception without a creature-specific range. XPHB Truesight retains its illusion, invisibility, transformation, and Ethereal Plane capabilities; the contract cannot distinguish normal from magical Darkness, so that portion is omitted. No Phase 3 record was changed in this batch.

### Action

All 48 Action source records have now been reviewed: six Phase 3/4 representatives and 42 Phase 5 records. After the authorized ACT-1–ACT-4 refinement, the Phase 5 Action batch has 40 canonical records: 27 fully represented and 13 partial; Healing Surge and Waking Someone remain unsupported. Across all 48 Actions, 31 are fully represented, 15 are partial, and 2 are unsupported. Climb onto a Bigger Creature is a new partial record; Overrun and Tumble are new fully represented records. The full source-qualified coverage matrix and evidence are in the [Action implementation report](CORE_RULES_FOUNDATION_PHASE_5_ACTION_IMPLEMENTATION.md).

The Action schema now distinguishes creature-space traversal, shared-space entry, lateral displacement, paired ability/Skill options, opposed attack/check resolution, bounded procedures and DC formulas, action-cost object use, and advisory Search/Study task guidance. Remaining partial Action mechanics include equipment and character-resource state, Ready lifecycle execution, PHB Hide's external Rule outcomes, qualitative Climb size likelihood, and cross-domain stabilization details.

### Disease

The Disease batch examined all 26 non-representative PHB and supplement records; the three representative records remain frozen in Phase 3/4. It produced 24 partial canonical records. Grackle-Lung and Super-Tetanus remain unsupported because their repeated-save procedures cannot be accurately expressed by Disease v1. The record-by-record inventory, contract fit, and PHB Petrified suspension analysis are in the [Disease expansion report](CORE_RULES_FOUNDATION_PHASE_5_DISEASE_EXPANSION.md).

The batch used only approved Disease v1 structures for infection, target applicability, onset, supported condition changes, condition-gated recovery restrictions, recovery checks, secondary exposure, and linked references. Long Rest triggered progression, success-count cures, suppression/resumption, non-psychic damage outcomes, ability-score changes, speed changes, and disease-specific transformations remain unsupported where the contract cannot represent them faithfully. The existing DMG Cackle Fever interpretation and its unresolved failure-counter behavior were preserved; the XDMG source variant remains partial and no consecutive/reset policy was inferred.

PHB Petrified's source at `conditionsdiseases.json $.condition[18].entries[0].items[5]` distinguishes disease immunity from suspension of an existing disease. The Disease contract has no typed suspension lifecycle. Timer advancement, scheduled saves, symptoms, and resumption timing are not specified by the source; the implementation report proposes a bounded Condition-side declaration for review and does not alter the approved schema or Petrified canonical record.

## Recurring limitations and remaining domains

All 201 Language records have now been reviewed: four Phase 3 representatives and 197 Phase 5 records. After approved LANG-1, LANG-3, and LANG-4 refinements, 197 Phase 5 Language records comprise 71 fully represented and 126 partial records. Ten exact-tag selector expressions, PHB Druidic and Thieves' Cant concealed-message mechanics, and Loross spoken intelligibility are now represented. Search selectors, population targets, origins, dialect identities, written-translation prerequisites, and Qualith inscription behavior remain deferred or unresolved. See the [Language expansion report](CORE_RULES_FOUNDATION_PHASE_5_LANGUAGE_EXPANSION.md) and [Language implementation report](CORE_RULES_FOUNDATION_PHASE_5_LANGUAGE_IMPLEMENTATION.md).

The only source records remaining unexamined in Phase 5 are 226 Rules. All 48 Actions are accounted for, and all five Status records are in the approved representative baseline. No Rule `ruleType` code mapping was inferred. The Cackle Fever failure-counter ambiguity remains as documented in the Phase 4 audit; this expansion has not introduced a consecutive/reset policy.

Potential contract questions must be source-backed and reviewed separately. The initial expansion batches did not change schemas; the later authorized Condition contract refinement is documented in its implementation report. No unsupported mechanic was placed in a generic fallback field.

## Validation and accounting

The permanent `npm run validate:core-rules` workflow has been extended for Phase 5 output and checks source identities, metadata, typed references, schemas, deterministic formatting, entry locators, nested paths, and register completeness. It includes focused assertions for expanded Condition, Sense, and Action patterns. These checks establish structural integrity and selected invariants; they do not prove that every interpretation is mechanically correct.

The earlier executed results are retained as historical batch findings. The original Action expansion run compiled 9 Draft 2020-12 schemas, validated 39 examples, 34 Phase 3/4 records, and 125 Phase 5 canonical records, and passed 259 permanent checks. The completed ACT-1 to ACT-4 run compiled 9 schemas, validated 54 examples, 34 Phase 3/4 records, and 128 Phase 5 canonical records, and passed 283 permanent checks. After Language expansion, `npm run validate:core-rules` compiled 9 schemas, validated 54 examples, 34 Phase 3/4 records, and 325 Phase 5 records; 481 permanent checks passed. Structural validation and selected source-backed assertions do not prove dataset-wide source fidelity.

## Next expansion order

The Skill, Condition, Sense, Disease, Action, and Language batches have been examined and await review; Status has no records outside Phase 3. Remaining inventory is 226 Rules. Step 2 Phase 5 remains in progress and is not approved. Do not begin Rule expansion without separate authorization.



## Disease contract implementation update

The Disease contract refinement and full Disease catalog review are recorded in [the Phase 5 Disease implementation report](CORE_RULES_FOUNDATION_PHASE_5_DISEASE_IMPLEMENTATION.md). This update supersedes the earlier Disease batch counts and limitation statements in this report. All 29 Disease source records were reviewed: the three representative records remain the approved baseline; 25 Phase 5 records now have canonical outputs and one (Super-Tetanus) remains unsupported. Across the full Disease catalog, 25 records are fully represented, three are partial, and one is unsupported. The Disease batch accounts for 73 roots and 72 interpreted, 0 duplicate, and 68 omitted classification parts. After the Action reconciliation, the Phase 5 manifest contained 128 canonical records. Following Language expansion it contains 325 Phase 5 canonical records and remains in progress; this does not complete Phase 5 or authorize Phase 6.

The Language contract refinement update preserves the original expansion findings as historical evidence and supersedes its initial Language coverage/count statements. The current Phase 5 manifest contains 325 canonical records (179 full, 146 partial, 3 unsupported), with 204 interpreted, 24 duplicate, and 190 omitted source-entry classification parts. This update does not begin Rule expansion or authorize Phase 6.

Following LANG-1, LANG-3, and LANG-4 implementation, the latest `npm run validate:core-rules` run compiled 9 schemas, validated 58 examples, 34 Phase 3/4 records, and 325 Phase 5 canonical records, and passed 507 permanent checks. See the [Language implementation report](CORE_RULES_FOUNDATION_PHASE_5_LANGUAGE_IMPLEMENTATION.md) for structural, semantic-assertion, and source-fidelity limitations.

## Rule expansion execution update

The original Rule inventory was rechecked against `variantrules.json`: 231 records remain, including five Phase 3/4 representatives. A first Phase 5 Rule batch directly interpreted three non-representative XPHB records (Ability Check, Attack Roll, and Saving Throw), producing three canonical records. The current Phase 5 manifest therefore contains 328 canonical records, 182 full, 146 partial, and 3 unsupported; it accounts for 279 Phase 5 entry roots and 207 interpreted, 24 duplicate, and 190 omitted entry classification parts, plus 157 structured-field issues. The batch passes the permanent validator with 511 checks.

This is an intermediate update only. The Rule expansion report records **223 remaining Rule records that have not yet been semantically examined or accounted for**. The 231-record Rule catalog is not complete, and Step 2 Phase 5 remains open and unapproved.

## Rule D20/Resolution batch update

The later Rule batch examined 20 additional source records after the five Phase 3/4 representatives and three initial Phase 5 Rule conversions. The verified source inventory remains 231 Rule records; 203 remain unexamined. The [Rule expansion report](CORE_RULES_FOUNDATION_PHASE_5_RULE_EXPANSION.md) contains the source-qualified batch matrix and mechanics findings.

The approved `ruleType` mapping is now represented by the optional Rule enum: `C` maps to `CORE`, `O` to `OPTIONAL`, `V` to `VARIANT`, and `VO` to `VARIANT_OPTIONAL`. Only source-provided codes are emitted. The Phase 5 manifest now contains 334 canonical records: 186 FULL, 150 PARTIAL, and 15 UNSUPPORTED; it accounts for 314 source-entry roots and 213 INTERPRETED, 26 DUPLICATE, and 220 OMITTED classification parts. Structured-field issues total 154. These global totals preserve the other domains' existing entries and incorporate the newly reviewed Rule records.

The 20 new records produced two FULL canonical outputs, four PARTIAL outputs, twelve UNSUPPORTED records, and two duplicate source records represented by existing Rule identities. Six new canonical files were produced. Initiative was changed from a tempting but misleading passive-ability-check representation to UNSUPPORTED after checking that the source's Initiative score (10 + Dexterity modifier, with a five-point Advantage/Disadvantage adjustment) is a distinct formula not represented by the approved Rule schema. The batchResults entry combines these with the three earlier Rule conversions, yielding 23 examined source records, nine Phase 5 Rule outputs, seven FULL, four PARTIAL, and twelve UNSUPPORTED. This update supersedes the earlier statement that 223 non-representative Rule records remained unexamined. Phase 5 remains open and unapproved; no other Rule category was expanded and Phase 6 has not started.

## Rule catalog inventory checkpoint

All 231 Rule source identities now appear in `ruleCatalogInventory` in the Phase 5 manifest and in the [Rule gap inventory report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAP_INVENTORY.md). This is structural inventory coverage only: 28 records are reviewed and accounted; 203 remain explicitly PENDING semantic review. The reviewed subset has 11 FULL, 5 PARTIAL, and 12 UNSUPPORTED records. The gap families documented so far are provisional and do not constitute a full Rule-domain analysis. No new Rule schema capabilities were implemented, no pending source record received a canonical output, and Phase 5 remains open.

## Rule Gaps Batch 2 update

After the first Rule gap implementation batch, the approved Batch 2 adds source-backed interpretations for Death Saving Throw eligibility, DMG Flanking, XPHB standard Cover, and PHB Skills with Different Abilities, and partially represents the DMG Ability Check Proficiency variant. See the [Rule Batch 2 report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAPS_BATCH_2.md). Hitting Cover remains unsupported; an identity-only canonical Rule makes it referenceable without claiming mechanical coverage.

The Phase 5 manifest now contains 346 canonical outputs (197 FULL, 150 PARTIAL, 7 UNSUPPORTED) across 327 source-entry roots, with 229 INTERPRETED, 27 DUPLICATE, and 218 OMITTED classification parts. Rule conversion accounting covers 31 identities including five representatives (22 FULL, 5 PARTIAL, 4 UNSUPPORTED), with 26 canonical Rule outputs. Two hundred Rule records remain unaccounted for conversion.

Validation passes: 9 schemas, 73 examples, 34 Phase 3/4 records, 346 Phase 5 records, 569 permanent checks, and 30 Rule components checked. Phase 5 remains in progress; Rule Batch 3, Phase 6, and Step 3 have not started.

## Rule Gaps Batch 3 update

The approved Batch 3 implementation is documented in [the Rule Batch 3 report](CORE_RULES_FOUNDATION_PHASE_5_RULE_GAPS_BATCH_3.md). It advances four authorized gap families: Initiative score/side ordering, carrying capacity and optional encumbrance, rest/recovery, and Damage Resistance/Vulnerability. Fourteen affected Rule records are represented in Phase 5 canonicals, including the formerly unsupported XPHB Initiative record; DMG Planar Effects remains a reference-only unsupported Rule because its rest-triggered outcomes are inseparable from out-of-scope planar mechanics in this batch.

The current Phase 5 manifest contains 360 canonical records: 204 FULL, 156 PARTIAL, and 7 UNSUPPORTED. It accounts for 388 source-entry roots and 256 interpreted, 27 duplicate, and 254 omitted classification parts. Rule conversion accounts for 44 identities including five representatives (29 FULL, 11 PARTIAL, 4 UNSUPPORTED); 187 Rule records remain pending conversion accounting. Validation passes with 9 schemas, 79 examples, 34 Phase 3/4 records, 360 Phase 5 records, and 596 permanent checks. Phase 5 remains open and unapproved; no Batch 4, Phase 6, or Step 3 work has started.

## Rule Gaps Batch 4 update

Batch 4 implements only R-GAP-15, R-GAP-36, R-GAP-41, and R-GAP-45. It adds 23 Rule identities (20 FULL, 3 PARTIAL), bringing Rule conversion accounting to 67 identities including five representatives (49 FULL, 14 PARTIAL, 4 UNSUPPORTED). Phase 5 now has 383 canonical outputs, with 224 FULL, 159 PARTIAL, and 7 UNSUPPORTED source records. Source accounting reconciles to 428 roots and 294 INTERPRETED, 28 DUPLICATE, and 258 OMITTED classifications. The two absent reprint targets remain unresolved source-field issues.

Validation passes with 9 schemas, 89 examples, 34 Phase 3/4 records, 383 Phase 5 records, and 638 permanent checks. This update does not begin bulk Rule conversion or any later roadmap phase; Phase 5 remains open and unapproved.
