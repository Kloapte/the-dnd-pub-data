# Step 2 — Core Rules Foundation: Phase 1 Source and Domain Analysis

**Status:** Phase 1 analysis completed and approved. Phase 2 design is recorded separately and is awaiting review.

## Scope and method

Inspected the six authorized raw JSON files under `src/main/resources/data/`: `actions.json`, `variantrules.json`, `skills.json`, `conditionsdiseases.json`, `senses.json`, and `languages.json`. Counts and field frequencies below are a complete inventory of those files at this revision, not a sample. Recursive inspection included nested entry objects and embedded `{@...}` tags. No source data was modified, and no schema, normalized dataset, or Java implementation was created.

The primary `source` value on every inspected record matched one of the 181 codes in `sources/registry.json` (594/594). This verifies source-code coverage for the primary record identity; it does not assert that every secondary reference, reprint, or embedded tag has a resolvable target.

## 1. Source inventory and structural findings

| File / record array | Records | Distinct names | Source codes | Common properties and observed variations |
| --- | ---: | ---: | ---: | --- |
| `actions.json` / `action` | 48 | 34 | 4 | Every record has `name`, `source`, `page`, `entries`; 47 have `time`. Optional flags: `srd` 20, `srd52` 17, `basicRules` 21, `basicRules2024` 5; `reprintedAs` 19, `seeAlsoAction` 11, `fromVariant` 8. |
| `variantrules.json` / `variantrule` | 231 | 224 | 15 | All have `name`, `source`, `page`, `entries`; `ruleType` on 191, absent on 40; optional `type` on 45, `reprintedAs` on 13, `additionalSources` on 3. |
| `skills.json` / `skill` | 36 | 18 | 2 | All have `name`, `source`, `page`, `ability`, `entries`; `reprintedAs` on 18. |
| `conditionsdiseases.json` / `condition` | 30 | 15 | 2 | Two source records for each of 15 condition names. All have `name`, `source`, `page`, `entries`; reprint and source flags vary on 15 records. |
| `conditionsdiseases.json` / `disease` | 29 | 26 | 14 | All have `name`, `source`, `page`, `entries`; optional `type` on 7, reprint and SRD flags on 3. |
| `conditionsdiseases.json` / `status` | 5 | 3 | 2 | Records are Bloodied, Concentration (two sources), and Surprised (two sources); all have `name`, `source`, `page`, `entries`. |
| `senses.json` / `sense` | 8 | 4 | 3 | Four sense names occur in multiple sources; all have `name`, `source`, `page`, `entries`; reprint flags on 4. |
| `languages.json` / `language` | 201 | 111 | 24 | All have `name`, `source`, `page`; optional `typicalSpeakers` 159, `type` 105, `script` 99, `origin` 36, `entries` 40, `reprintedAs` 46, `hasFluffImages` 26, `additionalSources` 10, `dialects` 3, `otherSources` 2, and `fonts` 1. |
| `languages.json` / `languageScript` | 6 | 5 | 2 | Records have `name`, `source`, and `fonts`; Draconic has PHB and XPHB records. Font lists vary in length and are asset paths, not language rules. |

There are **594 records** total across **9 arrays**. All 594 have `name` and `source`; 588 also have `page`. The 427 records with an `entries` property do not share one flat text shape: nested entries include lists, sections, tables, insets, items, quotes, images, and other typed nodes. `languageScript` records have no `entries`. The source JSON already separates three concepts in `conditionsdiseases.json` and separates `languageScript` from `language`; retaining these distinctions avoids collapsing structurally different entities.

No array contains a repeated exact `(name, source)` pair. There are 154 additional records beyond per-array distinct-name counts, reflecting same-name records in different source contexts: actions 14, variant rules 7, skills 18, conditions 15, diseases 3, statuses 2, senses 4, languages 90, language scripts 1. Names alone therefore do not make stable identities. Explicit `reprintedAs` relationships and source-specific identity must be preserved; similar names must not be merged automatically.

### Structural outliers and source conventions

- Action `time` is heterogeneous: of the 50 supplied time values, 37 are action-unit objects, 3 reaction-unit objects, 4 bonus-unit objects, and 6 strings (`Varies` four times and `Free` twice). `Mark` from DMG has no `time`. It is unsafe to assume a required numeric duration or a single time object.
- `variantrule.ruleType` values are `C` (115), `O` (43), `V` (17), and `VO` (16), with 40 records lacking the field. The source does not provide a local key mapping those abbreviations to canonical semantics. Do not expand or infer them without reviewing the source convention. A separate optional `type` field has the literal values `section` (28) and `entries` (17); it is not the same field as `ruleType`.
- Skills are an even PHB/XPHB pair: 18 named skills in each. Each record has an explicit lowercase ability code, while text and page can differ by source. Keep each source-specific record and preserve the supplied SRD/basic-rules flags.
- Conditions are 15 PHB/XPHB pairs. Diseases are varied source material, not a condition subtype by formatting: seven carry `type: "Magical Contagion"`, while most rely on `entries` prose for their effects and procedures. Status contains a different, small set of game states.
- Languages mix a core/setting language catalog with speaker lists, type labels, script names, origins, dialects, optional explanatory entries, and reprint links. The 105 present `type` values are `standard` (68), `exotic` (16), `rare` (17), and `secret` (4); 96 records omit `type`. `script` has 25 distinct supplied strings. Six `languageScript` records provide font asset paths; these are not a complete catalog of all language `script` values. Language-script association must not be inferred as same-source identity: languages commonly cite a script under a different source, and `none` is also a supplied script value.
- Preserve SRD/SRD52, basicRules/basicRules2024, page, and reprint metadata as source facts. These flags do not imply that two records are equivalent or that one edition supersedes another in every rule context.

## 2. Semantic and mechanics analysis

### Actions

Actions identify commonly available activities and include timing metadata plus descriptive/nested rules text. Their concepts support more structured mechanics than arbitrary prose alone: action/reaction/bonus-action timing is often explicit, and bodies describe requirements, restrictions, exceptions, and outcomes. Still, time is not uniform, and much of the operational detail is in text. Some entries represent definitions of the standard turn actions; others describe procedures or variants (for example, Disarm, Overrun, Healing Surge, Activate an Item, and Utilize). Deterministic representation is plausible for explicitly stated timing and simple gates/effects after interpretation, but complete runtime execution should not be presumed for every action.

### Core and variant rules

The 231 `variantrule` records span core definitions, optional and variant procedures, setting/campaign material, and broad game concepts. `ruleType` is incomplete and its code meanings require evidence; its absence is common enough to be an ordinary variation, not automatically an error. Nested bodies include extensive explanatory text, tables, cross-domain references, and rule-specific exceptions. Some simple numerical or categorical statements can later be represented deterministically, but broad rules such as grappling, travel, disease, ship combat, or downtime require substantially different mechanics and/or future domains. Treat semantic enrichment as entry-by-entry: structured supported mechanics with retained explanatory text, and explicit deferred/unsupported/ambiguous interpretation outcomes where evidence or an execution model is insufficient.

`variantrules.json` is not exclusively optional rules. Classification should preserve source evidence and distinguish core, optional, and variant treatment only where the supplied field and source meaning support it. Do not use the presence of a file or `ruleType` alone to imply a universal toggle/execution policy.

### Skills

Each skill has an explicit ability association and description. The ability mapping is deterministic source data (e.g. Acrobatics → `dex`) and can be represented as a reusable reference/value. What a check does at runtime depends on ability modifiers, proficiency/expertise, advantage, DC, and task circumstances. A skill entry should not duplicate those mechanics or claim a check outcome from its description.

### Conditions, diseases, and statuses

Condition entries often state discrete effects such as sight restrictions, attack-roll advantage/disadvantage, movement restrictions, or automatic check outcomes. These are promising candidates for shared effect concepts, but wording and exceptions must be interpreted against source and edition. Exhaustion is a high-impact edition difference: PHB and XPHB are separate records and their rules differ; it must not be merged by name.

Diseases describe infection, incubation, saving throws, symptoms, progression, and remedies in prose with varying completeness. They may cause or reference conditions and effects, but the current file does not establish a uniform executable disease lifecycle. Preserve disease identity separately and classify individual mechanics as supported, deferred, unsupported, or ambiguous during later phases. Status records include concentration, surprised, and bloodied; they overlap conceptually with conditions but have their own source array and some refer to other rules. Model these categories separately unless Phase 2 evidence justifies a narrowly shared effect component.

### Senses

The four sense names—Blindsight, Darkvision, Tremorsense, Truesight—are described in source prose rather than a uniform structured range/limitations object. Entries refer to cover, darkness, blindness, invisibility, and perception. Numeric sense ranges occur on creature records elsewhere and are outside these files. A sense definition can be identified here, but executable perception needs its relationship to creature-provided range and conditions; defer that integration without embedding creature records.

### Languages and scripts

Language records identify a language and may describe category, script, origin, speakers, dialects, and text. Those fields are useful identity/lore metadata; they do not by themselves implement a communication rule. `typicalSpeakers` are tagged creature references in many records and are descriptive source associations, not necessarily exhaustive eligibility rules. The six `languageScript` records mostly provide font assets. These can be kept separate from language identities; a later contract decision should determine whether scripts are references to a source-aware script catalog or preserved string values plus independent font metadata.

## 3. Dependencies and identity

### Embedded references observed

`{@...}` tags in nested text include explicit references to both Step 2 domains and later domains. Selected complete scan counts by tag type:

| Domain | Step 2 internal references | Later-domain references and examples |
| --- | --- | --- |
| Actions | `skill` 48, `variantrule` 30, `condition` 13, `status` 11, `action` 16 | `item` 4, `spell` 1, `subclassFeature` 1. |
| Variant rules | `variantrule` 204, `skill` 144, `condition` 97, `action` 24, `status` 3, `sense` 4 | `creature` 289, `item` 129, `spell` 80, `class` 48, `vehicle` 12, plus hazards, traps, adventures and other domains. |
| Conditions/diseases/statuses | `variantrule` 45, `condition` 13, `action` 7, `status` 1 | `spell` 1. Disease outcomes and prerequisites can also depend on later world/creature sources without explicit tags. |
| Senses | `variantrule` 9, `condition` 3 | Creature sense values and creature applications are future Step 5 dependencies. |
| Skills | No `{@...}` tags found | Rules text names ability checks; progression/proficiency and class features are outside these records. |
| Languages | `language` 3 | `creature` 116, `race` 19, `class` 6. Character species and creature use are later roadmap domains. |

The tag syntax is explicit source evidence, but its target may be absent from this six-file scope. Keep a stable typed reference when a target is normalized; preserve raw/unresolved evidence when it is not. Do not recursively embed the target model. Rule text also refers to concepts without tags, so tag counts are a lower bound on conceptual dependencies, not a full semantic dependency count.

### Dependencies and ordering

- **Internal cycles:** actions, rules, skills, conditions/statuses, and senses refer to one another. Analyze and normalize them as a coordinated foundation, with stable IDs and references, rather than requiring a strict order among all six subdomains.
- **Step 3 Equipment Foundation:** action activation and item-use rules, weapon rules, and variant-rule item mechanics refer to equipment. Preserve those links for later integration; do not normalize equipment here.
- **Step 4 Spells:** actions and rules reference spells and spellcasting procedures; conditions/disease entries may also mention spells. Keep spell dependencies as references and defer spell mechanics.
- **Step 5 Creatures / Monsters:** variant rules and languages contain many creature references; senses need creature-specific ranges and use. These are important downstream dependencies, not a reason to inline creature data now.
- **Step 7 Classes / Optional Features and Step 8 Character Building:** actions and rules refer to class features; languages reference race/species concepts; skill application can depend on character construction. Preserve these references for those steps.
- **Source Registry:** every primary record `source` in this scope maps to an existing canonical Source code. Reuse that registry for source references/provenance. `reprintedAs`, `seeAlsoAction`, `otherSources`, `additionalSources`, and inline tags have their own roles; they are not aliases for Source identity and must be inspected before mapping to canonical entity references.

Circular references among the core models can be handled with IDs and references, without inheritance or recursive embedding. Later-domain references should remain valid even when their mechanics are not yet executable.

## 4. Domain model and submodel alternatives

| Candidate boundary | Recommendation for Phase 2 design | Alternative and trade-off |
| --- | --- | --- |
| Action vs. rule | Keep `Action` and `Rule` as separate domain models. Actions have an activity identity and timing; rules include core definitions, options, variants, procedures, and setting material. | One generic rule model reduces top-level types, but mixes timing/activity fields with broad rule text and encourages unrelated optional fields. |
| Core / optional / variant rule | Retain one cohesive rule domain with explicit supported classification and source-specific metadata; do not split into files/models solely by `ruleType`, which is missing on 40 records and has unverified codes. | Separate models could clarify genuine behavior groups after review, but premature splitting risks reading the field codes as complete semantics. |
| Skill | Independent `Skill` entity with source-qualified identity and ability reference/value. | Treating a skill as a rule fragment loses its stable identity and explicit ability association. Avoid merging PHB and XPHB entries by name. |
| Condition / disease / status | Three distinct models under a coordinated rules foundation. Consider a reusable, composition-based effect/condition reference only after representative analysis. | One `ConditionLike` hierarchy may share effects, but diseases have lifecycle prose and statuses have separate state semantics; inheritance based on the shared JSON file would be artificial. |
| Sense | Independent sense-definition entity; model creature ranges/assignments only in Step 5. | A fully executable sense model now would need creature-specific numeric ranges and rule interactions not present here. |
| Language / language script | Keep `Language` and `LanguageScript` identities distinct; decide whether `script` values refer to catalog records or remain text pending source-aware mapping. | Fold font catalogs into Language records, but this duplicates shared script assets and conflates language with writing system. Source/version mismatches need review before references are asserted. |
| Entries and cross-domain references | Investigate one reusable structured rich-text/entry representation and a typed reference value shared by these models. Preserve semantic mechanics as a distinct concept from explanatory entries. | Keep each entry payload fully source-specific: simpler initially, but duplicates nested text handling and makes shared references harder to validate. A generic `Object`/untyped blob would weaken application contracts and is not recommended. |
| Source/provenance | Reference the approved Source registry using exact source codes; keep provenance and workflow/review states outside domain JSON. | Copy publication metadata into each rule entity: unnecessary duplication and inconsistent updates. |

Any shared mechanic/effect structures should be introduced only for recurring, semantically equivalent mechanics with distinct responsibilities. The scan justifies investigating such reuse; it does not establish an approved mechanics schema.

## 5. Coverage, outliers, and feasibility

- **Complete coverage inspected:** 6 files, 9 arrays, 594 records. Every primary record has a source code present in the 181-code registry.
- **Common identity pattern:** 594/594 (100%) have `name` and `source`; 588/594 (99.0%) have `page`; exact `(name, source)` collisions within an array: 0.
- **Rich entry content:** 427/594 (71.9%) records carry `entries` (actions, rules, skills, conditions, diseases, statuses, senses, and 40 languages); their nested structures are mixed and cannot be treated as plain strings. All 231 variant rules have entries.
- **Repeated names by source:** 154 extra records beyond distinct names, concentrated in languages (90), skills (18), conditions (15), and actions (14); source-qualified identity is required.
- **Classification/metadata gaps:** 40/231 (17.3%) variant rules lack `ruleType`; 1/48 (2.1%) actions lacks `time`; 96/201 (47.8%) languages lack `type`; 102/201 (50.7%) lack `script`; 42/201 (20.9%) lack `typicalSpeakers`; and all six script records have a source and fonts but no page or entry text.
- **Distinct structural submodels:** the 3 conditionsdiseases arrays (30 conditions, 29 diseases, 5 statuses) and the separate 6 languageScript records should be accounted for independently. Source file co-location is not evidence of one domain model.
- **Ambiguity/unsupported counts:** no malformed JSON or primary source-code miss was found. The inventory does not determine semantic completeness of prose. The 40 uncoded rule classifications, disease procedures, time exceptions, script targets, edition-specific mechanics, and references to later domains need interpretation or may remain deferred/unsupported. These are analysis cases, not presumed source defects.

**Estimated coverage:** a structural identity/reference model can cover all 594 records while preserving their source-qualified identities and meaningful nested text. Executable mechanics coverage cannot be estimated reliably from structure alone. It requires Phase 2/3 representative cases and entry-level adjudication; do not claim that all prose can be compiled into deterministic behavior.

## 6. Normalization feasibility and recommended approach

Semantic enrichment is feasible for explicit, repeated rule patterns—e.g. skill-to-ability mappings, action timing, simple condition effects, and stable references—provided the descriptive text and source evidence are retained. It is not safe to force every variant rule, disease, sense interaction, or language description into executable mechanics. Use per-interpretation outcomes that distinguish complete, deferred, unsupported, and ambiguous cases, with completeness/review tracking outside canonical domain structures.

Recommended Phase 2 direction for review:

1. Preserve source-qualified identity (`type`, exact source code, name) and explicit edition/reprint relationships. Do not merge records by name or treat flags as equivalence.
2. Keep cohesive Action, Rule, Skill, Condition, Disease, Status, Sense, Language, and LanguageScript responsibilities; allow a narrowly shared structured-entry representation and typed cross-reference only where it genuinely reduces duplication.
3. Keep raw descriptive entries distinct from executable mechanics. Add reusable mechanical components only when evidence shows the same mechanics pattern across multiple entries.
4. Preserve action timing variants, missing optional fields, language/script/source distinctions, and all source provenance. Reuse the approved Source registry by stable code.
5. Use explicit references to later domains, and track dependency interpretation outside canonical entities. No requirement to execute a future-domain mechanic immediately.
6. Batch representative conversion across source versions and model boundaries before expansion; include different rule classifications, action time shapes, an old/new condition, a disease with prose-based progression, a sense interaction, and language/script examples.

## 7. Unresolved questions for review

1. Confirm that Phase 2 should retain separate canonical models for Condition, Disease, and Status, and for Language and LanguageScript.
2. Decide how `variantrule.ruleType` codes and the separate `type` field will be interpreted. Are the source-code meanings available in repository documentation/tooling, or should the contract preserve raw codes until interpretation is approved?
3. Decide how source-version variants are presented and related: retain every edition-specific entity and use explicit `reprintedAs` references, with no automatic “latest” selection?
4. Decide whether action timing strings (`Varies`, `Free`) and absent time remain explicit alternatives/absence or receive carefully interpreted canonical timing variants in Phase 2.
5. Decide whether the language `script` property is a string, an identity reference to `languageScript`, or a source-aware reference. Current data contains 25 script strings but only 5 unique script names in 6 font-catalog records, often from different sources.
6. Agree on how much of rich-text `entries` should be structurally represented versus preserved as descriptive content, and what threshold warrants deterministic mechanics extraction.
7. Confirm that Phase 2 keeps all later-domain references stable and explicit while leaving mechanics execution deferred until those domains are available.

**Approval checkpoint:** This report completes Step 2, Phase 1 only. Review the analysis and modeling recommendations before Step 2, Phase 2 schema design begins.
