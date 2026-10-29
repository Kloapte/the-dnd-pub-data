# Step 0 — Dependency analysis and roadmap validation

Date: 2026-10-22. Status: discovery complete; recommendations proposed for review.

## Finding

The roadmap is viable as an incremental normalization sequence with explicit deferred references and recorded execution limitations. It is **not viable as a strict dependency order in which each domain's mechanics are fully executable before the next domain begins**. Actual references form cycles across the foundations and most major domains. Moving whole domains cannot eliminate those cycles.

The strongest changes to consider are a shared foundation spanning Steps 1–2, source metadata coverage beyond books, an early equipment foundation, and class optional features handled with classes. Spells can remain before monsters if summon and transformation execution is explicitly deferred until the required creature models are available. Missing external rule content and catalogs prevent claims of complete semantic coverage for some entries regardless of ordering.

These are recommendations only. The roadmap, source datasets, application, and contracts have not been changed. Step 1 has not begun.

## Scope, evidence, and reproducibility

The instructions are [AGENTS.md](../../AGENTS.md) and Step 0 of [docs/ROADMAP.md](../../ROADMAP.md). The roadmap resides under the existing `docs` directory; this analysis follows the requested `/discover` location without relocating existing files.

All 174 JSON files under `src/main/resources/data` were parsed and recursively scanned: 27,828,944 bytes and 15,083 top-level array members. This is a complete census of those files, not a sample. Array members include metadata, templates, name-table containers, and loot-table containers; this number is not a count of canonical game entities. Nested versions and generated variants are not expanded or added to that total. Representative mechanics and dependency paths were then inspected directly; no exhaustive semantic interpretation of every entry is claimed.

The inspected checkout was `cea1688e3e60f47c69bdfcb4be6d799e6185ace8`. The source corpus fingerprint is:

```text
df60cc4ad8599701aff178bed2826fb80d2d2cb1e862d67938d1873ec793b8c1
```

The fingerprint is SHA-256 of sorted records consisting of relative filename, NUL, that file's hexadecimal SHA-256, and LF. Individual source hashes are retained in the evidence. The README describes a pinned 5eTools version; an upstream revision was not independently established by this analysis. Git revision and local content hashes provide the observed provenance without assuming an upstream version.

Supporting discovery artifacts:

- [Dependency tables](dependency-tables.md): complete domain inventory, cross-domain matrix, roadmap-step matrix, all observed tag types, reference-check counts, copies, and selectors.
- [Machine-readable evidence](../../tools/evidence/step-0/dependency-evidence.json): per-file hashes and counts, source properties, metadata declarations, dependency counts, reference exceptions with source pointers, copy chains, strongly connected components, identity collisions, and selected structured-reference checks.
- [Read-only census script](../../tools/scripts/step-0/analyze-dependencies.cjs): run `node tools/scripts/step-0/analyze-dependencies.cjs` from the repository root. Node built-ins only; writes machine-readable evidence beneath `/tools/evidence/step-0` and report tables beneath `/reports/step-0`. It does not apply copy operations, resolve source defaults, create normalized entities, or alter sources.

Paths in the evidence use `file.json#/array/index/property` relative to `src/main/resources/data`. Array indexes are zero based. Discovery JSON is analytical evidence, not a normalized dataset, manifest contract, or JSON Schema.

## 1. Dependency inventory

### Planned domains

| Current step | Complete source inventory | Dependency implications |
| --- | --- | --- |
| 1 | 66 books; 48 actions; 231 rule entries | Source identity and shared rules are foundational, but actions and rules already consume Step 2 concepts and later-domain information. |
| 2 | 36 skills; 30 conditions, 29 diseases, 5 statuses; 8 senses; 201 languages and 6 language scripts | Conditions, statuses, senses, skills, and core rules interact. Diseases often depend on spells and world effects; language examples reference creatures and species. |
| 3 | 969 spells in 18 files | Uses foundations extensively; also creatures, species, class capabilities, equipment, and other spells. |
| 4 | 4,560 monsters, 188 legendary groups, 66 monster templates, 1 legendary-group template in 113 files | Requires copy/template handling, spells, foundations, and equipment. Summoned creatures also consume spell or class context. |
| 5 | 30 classes, 330 subclasses, 677 class features, 1,499 subclass features in 15 files | Progression links require composite identities; optional features, feats, equipment, and spell selection reach outside this step. |
| 6 | 160 races, 98 subraces; 305 feats; 171 backgrounds; 221 optional features; 10 name-table containers | Grants, prerequisites, and choices depend on foundations, spells, class context, and equipment. Subraces and copied entries require parent interpretation. |
| 7, equipment | 2,512 items, 119 item groups; 230 base items, 27 properties, 67 types, 2 type supplements, 13 entry templates, 8 masteries; 230 magic variants | Base equipment and property rules are prerequisites for complete equipment-dependent mechanics in earlier domains. Magical variants and loot require an internal processing order. |
| 7, other world data | 100 loot array members; 278 rewards; 42 encounter-table containers; 316 recipes; 563 deities; 30 cults and 12 boons; 37 objects; 39 vehicles and 31 upgrades; 37 traps and 73 hazards | Several independent responsibilities. Encounter/loot expansion depends on target catalogs; vehicles, objects, traps, and creatures share combat concepts without necessarily sharing entity identity. |

All source arrays, including ancillary arrays inside planned domain files, appear in the linked inventory. File patterns must include monster templates and legendary groups, not just `bestiary-*` files. Conditions/diseases also contain statuses, and languages include independent script records.

### Present but not explicitly assigned to the main sequence

| Source | Count | Recommendation |
| --- | --- | --- |
| `sources/registry.json` | 181 source-code records | Evaluate with foundational metadata in Step 1; preserve the existing codes and aliases. |
| `adventures.json` | 101 adventure metadata records | Include source identity and explicit parent relationships with Step 1 metadata. Full adventure content is a separate scope decision. |
| `bastions.json` | 70 facilities | Assign explicitly. Facilities reference rewards, spells, items, monsters, rules, and actions; the reward/facility relationship crosses the current Step 7/Step Z boundary. |
| `homecrafts.json` | 20 crochet patterns | Reasonable Step Z candidate. `seeAlsoItem` and `seeAlsoCreature` provide relationships; similar names alone do not establish entity equivalence. |

Missing target catalogs are separately discussed below. A source absent from the current roadmap is not automatically excluded from accounting.

## 2. Reference forms and cross-domain matrix

The complete [cross-domain matrix](dependency-tables.md#cross-domain-matrix) reports every observed source domain against target roadmap groups. Its [step aggregation](dependency-tables.md#matrix-aggregated-by-roadmap-step) provides a smaller view. Both count explicit entity/rule tags only; structured links and semantic dependencies are evaluated separately here. A zero does not establish independence.

The recursive scan found **92,262 tag occurrences across 73 tag types**. Of these, 46,829 are entity/rule/catalog reference tags counted in the matrix. Repeated references, nested tags, references in version descriptors, and text inside copy modifications are counted at their original locations; they do not represent distinct normalized edges or materialized entities.

The largest reference groups are spells (15,946), conditions (8,189), rule entries (5,544), items (4,962), creatures (4,637), skills (2,460), and actions (1,385). There are also 696 book and 72 adventure tags. Smaller groups, such as mastery, optional-feature, and facility references, remain consequential despite their lower counts.

| Reference form | Observed evidence | Required treatment in future phases |
| --- | --- | --- |
| Entity tags | `{@spell ...}`, `{@creature ...}`, `{@condition ...}`, `{@action ...}` | Preserve identity, source components, and display text separately; missing sources require reviewed source-format defaults. |
| Compound identities | Class/subclass feature tags and feature UIDs include class, class source, level, subclass, and feature source | A feature name alone is insufficient. Establish identity qualification before canonical references are emitted. |
| Book/adventure locations | Tags carry a publication identifier and sometimes chapter/header coordinates | Metadata presence does not imply the referenced rules text is bundled. Retain location information. |
| Candidate-set selectors | 848 `filter` tags: spells 476, items 178, bestiary 132, feats 24, optional features 21, psionics 12, rewards 5 | Preserve selection criteria. A filter is not one entity reference or evidence that a complete class spell list is available. |
| Page navigation | 153 `5etools` tags, including 151 feat-page links | Navigation can reveal a dependency but cannot supply a resolved feat identity. |
| Structured grants and progression | 488 `additionalSpells` properties; class spell progression; class/feat/optional-feature progression; prerequisites | Interpret level gates, resources, selections, and eligibility in the owning domain. Property occurrence counts are not numbers of distinct mechanics. |
| Structured equipment relations | 256 `baseItem` fields; attached items/spells, packs, attunement requirements, item groups | Resolve base identity and composition offline; distinguish category choices from individual items. |
| Content substitution | 122 `{#itemEntry ...}` directives and 13 entry-template records | Separate from `{@...}` tags. Entry templates and `{=...}` substitutions are real offline expansion dependencies. No expansion was performed. |
| Inheritance and variants | `_copy`, `_mod`, `_preserve`, `_templates`, `_versions`, `inherits`, `requires`, `excludes`, and file `_meta` declarations | Preserve operation order and explicit inheritance identity; references discovered in unapplied operations are not effective runtime mechanics. |
| Roll/mechanics markup | Damage, dice, DC, attacks, recharge, saving-throw outcome, trigger, and response tags | These require shared mechanics/value concepts. They are not all links to catalog entities. |

Structured key counts are retained as occurrences, including control paths. For example, a `legendaryGroup: true` flag under `_preserve` is a copy instruction, not a group reference. The reference checks exclude such flags.

## 3. Required predecessors versus deferred references

The following distinction is about what is needed for a particular operation. It does not require complete normalization of every referenced domain.

| Dependency | Needed before interpreting or executing | Can remain deferred |
| --- | --- | --- |
| Source identity and rules edition | Source-code identity, explicit aliases/parents, source-specific rule context | Publication prose and unrelated metadata enrichment. |
| Copied entity → source parent | Parent contents and ordered copy/override semantics before claiming complete child meaning | An entity placeholder can be recorded, but its inherited mechanics cannot be declared complete. |
| Action/rule → condition/skill/sense/status | Shared definitions needed by the mechanic being interpreted | Creature examples or class exceptions can remain references with a visible limitation. |
| Spell → creature form or species | Creature/species mechanics before executing that summon, transformation, or trait replacement | A spell can preserve a source reference and explicit unsupported execution dependency before the target domain is integrated. |
| Monster → spell | Spell effects before executing referenced spellcasting | The monster's ordinary statistics and independent attacks can be interpreted separately. |
| Class → optional features | Eligible feature pool and relevant effects before complete progression/choice execution | Specific feat choices and external feature references can be recorded pending their domain. |
| Character/class → equipment | Referenced weapon/armor/property rules before enforcing equipment-dependent effects and choices | Starting-equipment identity can be retained without embedding complete item models. |
| Language → example speaker; deity → reward | Usually descriptive identity links; particular granted effects require their mechanics | Example creatures, narrative associations, and cross-links generally do not require prior normalization. |
| Encounter/loot → catalog entries | Target availability before drawing complete executable encounter or loot results | Table structure, ranges, weights, and explicit unresolved results can be retained earlier. |
| Missing external rules/selector catalogs | Authoritative content or an approved limitation before the affected mechanic is complete | Citation and selection text can be preserved; missing rules cannot be reconstructed by guessing. |

Shared foundational concepts supported by the observed fields and text include ability checks, saves, attack rolls, advantage/disadvantage, proficiency, damage/healing, conditions/statuses, action timing and resources, movement/distance, targeting/areas, duration and termination, concentration, senses, prerequisites, choices, and progression. These are candidate reusable concepts for future domain analysis, not contracts designed or approved in Step 0.

Rules context must remain attached to interpretation. For example, the PHB and XPHB `Polymorph` entries describe materially different hit-point behavior. Shared names or `reprintedAs` links do not authorize merging mechanics or reassigning SRD/SRD52 status.

## 4. Significant dependency chains and cycles

Arrows below mean “references or needs information from,” not “must normalize the entire target first.”

### Foundations

`actions.json#/action/27` (`Influence|XPHB`) names several XPHB skills and leaves willingness and check selection to the DM. `Hide|PHB` (`action/22`) references Stealth and external PHB hiding/attack rules. Step 1 has 320 counted tags to Step 2 concepts; Step 2 has 79 back to Step 1. These cannot form a strict foundation-first ordering with a hard boundary between the two steps.

There is also a concrete cycle within the XPHB rules: `Ability Check` (`variantrule/0`) → `D20 Test` (`variantrule/44`) → `Ability Check`. A nine-member component in the explicitly sourced tag graph joins those rules with Attack Roll, Proficiency, Unarmed Strike, Advantage, Disadvantage, Grappled, and Prone. Identity can be established before behavior is fully interpreted; recursive embedding would be inappropriate.

`senses.json#/sense/1` (`Blindsight|XPHB`) references Cover, Darkness, Blinded, and Invisible. Conditions and visibility/perception rules therefore belong in a coordinated foundational analysis.

### Spells and creatures

`spells/spells-tce.json#/spell/9` (`Summon Beast|TCE`) → `Bestial Spirit|TCE` → spell-provided level/context and shared combat mechanics. Across spells there are 131 creature tags and 43 bestiary selectors. Monsters contain 11,552 spell tags, 27 `summonedBySpell` properties, and 15 `summonedByClass` properties. The latter counts identify source declarations, not all implied scaling relationships.

`Polymorph|PHB` (`spells/spells-phb.json#/spell/255`) selects beasts by type and challenge rating and replaces statistics; `Reincarnate|PHB` (`spell/276`) references species/subrace choices. Spell identity, casting, and independent effects are feasible before Steps 4 and 6, but complete execution of these effects requires later model capabilities or an explicit unresolved limitation. Reordering monsters ahead of spells would reverse the spellcasting dependency without solving the cycle.

### Classes, choices, and equipment

Class optional-feature progression → optional-feature pools → class/subclass level prerequisites and other optional features. The census found 16 class-domain `optionalfeatureProgression` properties, 161 `refOptionalfeature` structures in classes, and 21 optional-feature selectors across classes, feats, and optional features. This is a stronger completion dependency than a narrative cross-link.

Classes also reference equipment through 499 item/property/mastery tags, starting equipment, and proficiencies. Items point back to class features: 41 `classFeatures` properties occur in `items.json` and 12 in magic variants, including inherited and modification paths. Equipment and class mechanics cannot both be fully closed under the existing sequence without explicit deferred links.

### World domains

Encounter tables → creatures/vehicles/items; loot tables → items → base items/property rules/magic variants; cults/boons → granted spells and creatures; deity piety → rewards. The encounter domain alone contains 2,568 creature tags. Facilities → rewards (30 tags), while rewards contain a facility tag and additional structured facility links. That last relationship requires an explicit bastion scope decision.

### Graph-level interpretation

The domain graph built from counted tags has one 18-domain strongly connected component. The narrower entity graph using only uniquely matched, explicitly sourced tags has 46 cyclic strongly connected components: 35 contain multiple entries, and 11 are self-reference components. These are observed graph components, not a count of all possible cycles, runtime recursion, or mechanical errors. Omitted-source links, most structural dependencies, and untagged semantic relationships are outside that graph.

No cycle was found in the checked `_copy` graph. Reference cycles and inheritance cycles must be evaluated separately.

## 5. Inheritance, source identity, and reference exceptions

### Copy and metadata dependencies

There are **1,485 top-level `_copy` declarations**, all with exactly one available target when checked against the array type and supplied identity fields. Item variants can explicitly locate their source in `inherits.source`; a root-only source check would incorrectly report two missing targets. The longest observed copy chains have three edges. One is:

```text
Ascendant Dragon Vessel|FTD   items.json#/item/115
  → Wakened Dragon Vessel    items.json#/item/2388
  → Stirring Dragon Vessel   items.json#/item/2213
  → Slumbering Dragon Vessel items.json#/item/2069
```

The 103 files with `_meta` include 81 declared `(file, array type, dependency source)` requirements, all represented by source members in the corpus. `_meta.otherSources` relationships describe source-dependent inclusion and must not be mistaken for new identities or copy parents. Presence checks do not execute those inclusion rules.

All 187 top-level copy-template links and 21 explicit version-template links checked have targets. Monster references to legendary groups include a target declared inside a version: `Shadow Dragon (Bronze Dragon)|MM` from `monsters/bestiary-wdmm.json#/monster/94` is declared at `monsters/legendarygroups.json#/legendaryGroup/37/_versions/0`. Of 241 named structured group links, 240 match top-level groups and one matches that declaration. This is an expansion prerequisite, not evidence of absent source content.

All checked subclass parents (330), feature class parents (2,176), subrace parents (98, including a parent identity present in a copy declaration), and explicit-source base-item links (256) have unique targets. For structured feature UIDs, 286 fully explicit occurrences match unique targets; 754 class-feature and 1,463 subclass-feature occurrences remain deferred because source/default components are omitted. Some UID values appear both in a container and its nested reference field; these are occurrence checks, not counts of unique relationships.

No copy operations, version generation, subrace composition, or selector evaluation was performed. Successful target lookup does not prove the resulting inherited mechanics are complete or correct.

### Source coverage and identifier qualification

There are 180 distinct primary source codes across top-level members, including explicit variant `inherits.source` values. All are present in the existing 181-code registry. Books alone lack records for 114 of those codes; adding adventure metadata reduces that to 15. Those 15 still have registry identities:

```text
UATheMysticClass, TftYP, RoTOS, HAT-LMI, MCV2DC, EET, ESK, MCV3MC,
MFF, MisMV1, SADS, VD, EEPC, HFDoMM, DrDe
```

This measures primary source coverage, not every secondary citation or source string inside arbitrary prose. Preserve code-only metadata where that is all the source supplies. Do not fabricate publication details. Explicit identifiers can differ: six Plane Shift book IDs use `PS-*` while their `source` fields use forms such as `PSA`. Preserve that supplied relationship rather than deriving aliases by punctuation removal.

The unqualified `(type, source, name)` tuple has **303 collision groups**: subclasses 124, class features 55, subclass features 117, deities 5, and subraces 2. Adding observed identity context (class/source, subclass/source, level, pantheon, race/source as appropriate) removes collisions in this census. A future `<type>:<source>:<name>` convention must define a qualified name component for these domains. Step 0 does not assign canonical IDs or change the convention. Similar names and reprints must not silently merge distinct entities.

### Tag lookup results and unresolved cases

The conservative lookup accounts for all 46,829 counted reference-tag occurrences:

| Result | Occurrences | Meaning |
| --- | --- | --- |
| Unique explicit identity match | 18,888 | Target exists in a source array; behavior and publication chapter contents are not validated by this match. |
| Source omitted | 27,114 | Requires source-format/default review; these are not classified as broken links. |
| Compound components omitted | 8 | Remains deferred; no guessed class/subclass/source defaults. |
| No direct literal match | 52 | Requires generated-name, alias, or missing-content investigation. |
| Target catalog absent | 767 | Quickref 355, tables 274, cards 132, decks 6. |
| Multiple explicit literal matches | 0 | Only within the restricted matching procedure, not a guarantee of unambiguous canonical resolution. |

The 52 direct-match exceptions comprise:

- **37 item occurrences across 33 raw tag bodies.** Examples are `+1 Shield|XDMG`, named enchanted weapons, and barding. These are candidates for source-defined variant generation or other offline composition. They are not confirmed missing items, and this analysis does not create their resolved identities.
- **5 race occurrences across 4 raw tag bodies.** Examples include `Goblin (Ixalan)|PSX`, `Human (Keldon)|PSD`, and parent/subrace display names such as `Genasi (Water)|EEPC`. Parent composition or alias handling may account for them; exact-name similarity is insufficient to resolve them here.
- **10 occurrences of `weapon mastery properties|XPHB`.** No literal `variantrule` identity matches. Individual mastery records and book contents provide related evidence but do not establish equivalence to a normalized rule entity. Preserve the original reference for review.

Full raw bodies and representative locations are in `missingReferenceGroups` in the evidence. Formatting/display text differences can produce multiple raw bodies for one prospective identity.

### Missing content that order changes cannot supply

- **Quick-reference content:** 355 tags refer to concepts such as cover and difficult terrain without a bundled quick-reference catalog. Some concepts also appear elsewhere; a shared name is not permission to substitute a different edition's definition.
- **Table catalog:** 274 tags have no standalone table catalog. Inline tables do exist. They must be reviewed before deciding whether a target is locally embedded or genuinely unavailable; Step 0 does not claim that every table's content is missing.
- **Deck/card catalogs:** 138 combined tags reference decks/cards with no corresponding top-level catalogs. Related item prose and inline rows do not automatically supply all individual card mechanics.
- **Psionics:** 12 selectors in `classes/class-mystic.json` target psionics. No psionic discipline/talent catalog is bundled. Class progression is inspectable; complete discipline/talent choices and effects cannot be supplied from those selectors alone.
- **General class spell membership:** spell record fields do not contain class lists, and no general spell-to-class membership catalog was found among the inventoried files. Class-domain spell selectors include queries such as `class=Artificer`; their labels do not establish the eligible set. Explicit `additionalSpells` grants provide particular relationships, not evidence for a complete general class spell list.
- **Book/adventure prose:** All 696 book and 72 adventure tags match metadata IDs. The bundled records contain publication metadata and contents/header lists, not full chapter prose. For example, `Hide|PHB` cites rules that its local action entry does not reproduce. Metadata normalization will not fill that semantic gap.

These gaps block complete semantic enrichment of the affected mechanics, not all useful work in the owning domain. Preserving unsupported behavior requires explicit review in later phases; no reduced normalization strategy is approved or applied by this report.

## 6. Evaluation of the current order

| Existing step | Assessment | Proposed adjustment for review |
| --- | --- | --- |
| 1: books, actions, variant rules | Correct foundational intent; insufficient metadata scope and a strict Step 2 boundary conflict with observed dependencies. | Include registry/adventure source metadata; coordinate core rules/actions with conditions/statuses/skills/senses. Separate foundational rules from dependent subsystems within the plan. |
| 2: basic building blocks | Core concepts belong early. Diseases and narrative language associations do not have the same predecessor requirements as condition effects. | Treat conditions/statuses, skills, senses, and required language identity as the shared foundation. Analyze diseases separately where spell/world dependencies require it. |
| 3: spells | Useful early consumer of reusable mechanics. Creature/species effects prevent universal execution closure here. | Retain its relative position with explicit dependency and execution limitations for summon, transformation, and trait replacement effects. |
| 4: monsters | Spell-first order is supported by extensive spellcasting. Equipment and generated forms remain dependencies. | Keep after spells; analyze copies, templates, versions, and legendary groups before claiming complete monster coverage. Use early equipment rules or defer affected mechanics. |
| 5: classes | Depends materially on optional-feature pools currently in Step 6, and equipment currently in Step 7. | Bring class optional features into the same analysis and conversion scope as classes; retain their independent domain identity. Review feat selection separately. |
| 6: character building | Generally sensible after spells/classes. Subrace parent identities, grants, and equipment must be supported. | Keep species, feats, and backgrounds here; remove only the class-optional-feature scheduling conflict if approved. Naming tables can remain here or move to Z without an observed mechanics benefit. |
| 7: world building | Too broad to express a meaningful internal dependency order. Basic equipment is needed earlier. | Split off equipment foundations, then order magical items/variants before loot and result expansion; schedule other world domains by their actual dependencies. |
| Z: bonus data | Appropriate for supplementary data with explicit scope. | Consider homecrafts here; assign facilities and missing catalogs deliberately rather than treating every unlisted source as optional. |

`variantrules.json` specifically contains 115 entries with `ruleType: C`, 43 with `O`, 17 with `V`, 16 with `VO`, and 40 without a `ruleType`. `Ability Check`, `D20 Test`, `Attack Roll`, `Hit Points`, and `Cover` show that this file includes ordinary foundational rules. Treating all its contents as optional variants would change their meaning. The codes and absent values need domain review; the file name cannot define applicability. Complex optional subsystems must still receive semantic analysis and an approved disposition.

## 7. Recommended sequence and alternatives

Recommended planning changes, subject to approval:

1. **Source identity first:** registry, book/adventure metadata, explicit aliases and parent relationships; define reference qualification and unresolved-reference handling before conversion. This does not require importing unavailable publication prose.
2. **Coordinated foundations:** analyze rules, actions, conditions/statuses, skills, and senses as a dependency group, including language identities where needed. Keep distinguishable domain contracts and the six approval phases. Establish common mechanic concepts before dependent execution is claimed.
3. **Early equipment foundation:** base-item identities and weapon/armor/type/property/mastery rules, plus entry templates needed to interpret them. Item and property rules themselves reference foundations; the relationship must be reviewed rather than treated as a perfectly acyclic pipeline. Leave advanced magic, variants, loot, and most world entities for later.
4. **Spells, then creatures:** preserve the proposed order with clear deferred creature/species/context dependencies and explicit execution limits. Coordinate small representative summon/creature pairs only if separately authorized.
5. **Classes with their optional-feature pools:** analyze and schedule the interdependent choices together. Preserve references to distinct feat, species, background, equipment, and spell domains.
6. **Remaining character building, then world batches:** maintain the broad existing progression. Within world building, resolve base/variant/group item relationships before expanding loot results; resolve creature/vehicle targets before complete encounter execution. Decide facility/reward scope explicitly.
7. **Supplementary domains and unresolved content:** account for every unassigned source. Approve additional source acquisition, narrower mechanics coverage, or supported unresolved representations for missing content; none is automatic.

| Alternative | Advantages | Costs / limitations | Recommendation |
| --- | --- | --- | --- |
| Keep every existing step boundary and order | Minimal planning change; stable references can preserve many forward links. | Step 1 and later completion criteria must accept documented unresolved mechanics; equipment/class-choice execution waits longer. | Viable if those limitations are explicitly approved. |
| Coordinated foundations plus targeted splits above | Addresses concrete predecessor conflicts; retains most of the intended learning sequence and domain identities. | Requires scope decisions and grouped dependency review while maintaining separate phase approvals. | Preferred. |
| Move all Step 7 before classes/monsters, or monsters before spells | Makes selected targets available earlier. | World data already depends on spells, creatures, and classes; reversing spell/creature order does not remove their cycle. | Unsupported as a general solution. |
| Combine every cyclic domain into one milestone | Allows simultaneous closed-world modeling in principle. | The 18-domain component would create an unmanageably large conversion/review scope and defeat incremental approvals. | Do not adopt. |

These recommendations define scheduling boundaries, not a generic interpreter, runtime registry, model hierarchy, or schema design. Reusable components and separate referenced identities appear preferable to recursive embedding, but actual domain/submodel alternatives remain the responsibility of the authorized Phase 1 analyses.

## 8. Decisions and risks for review

The review should decide:

- Whether to keep the current sequence with explicit partial execution coverage, or approve the targeted coordination and splits above.
- Whether Step 1 source metadata includes the existing registry and adventure metadata, and how code-only sources remain represented without invented details.
- How rules editions and source defaults will be reviewed before emitting canonical references; reprints do not establish mechanical equivalence.
- How contextual identity becomes part of the name component for features, subclasses, deities, and subraces without changing existing stable identities unnecessarily.
- Whether class optional-feature pools move with classes, and when early equipment mechanics are scheduled.
- Whether facilities belong alongside rewards/world domains, and which genuinely supplementary sources belong to Z.
- Whether missing publication/catalog content will be supplied later or the affected behavior will remain explicitly unsupported under an approved contract.

Main limitations of this discovery are intentional: the census does not execute a source-format renderer or apply omitted-source defaults; no inheritance or variant generation is applied; no class list is derived from filter labels; no prose reference is guessed from a similar name; no exhaustive semantic coverage percentage is asserted. Top-level member property and recursive tag inventories are complete for the inspected files; structured dependency checks cover the documented fields, while semantic findings are representative analyses supported by source evidence. Unknowns remain visible rather than receiving default mechanics.

## 9. Completion and verification

Step 0's dependency inventory, cross-domain matrix, significant chains, circular/unresolved dependencies, order assessment, proposed adjustments, and review questions are supplied by this report and its linked evidence/tables. The analysis script parses the entire source corpus and performs the documented target-presence and graph checks. Source hashes support repeatable comparison; table totals account for every counted reference occurrence.

Verification is limited to discovery artifacts and source integrity. Application behavior and Java code were not changed, so application tests do not validate this report. No schema creation, source conversion, normalized-data modification, roadmap edit, or Step 1 work is included. Proceeding requires a new authorization after review.

All 174 source hashes were rechecked against the census, and the 46,829 counted references reconcile across result categories and the matrix. Regenerating the evidence and tables produced identical hashes. Local Markdown link targets exist. Whitespace checks passed for all four new files, and the final working-tree review showed additions only under `/discover`.
