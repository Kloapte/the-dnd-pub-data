# Traps and Hazards

- `trapshazards.json` has 37 trap and 73 hazard definitions. They remain separate domains. IDs use `trap:<source>:<slug>` and `hazard:<source>:<slug>`; source/name pairs and generated IDs are unique. Duplicate display names are retained across authored sources, and name lookup returns all matches with optional source filtering.
- Trap classifications are `CMPX`, `HAUNT`, `MAG`, `MECH`, `SMPL`, and `TRP`. Hazard classifications are `ENV`, `EST`, `GEN`, `WLD`, and `WTH`; 33 hazards have no classification. Ratings are tier/threat arrays. Traps additionally author triggers, effects, countermeasures, durations, and complex-trap initiative/stage fields. None are executed.
- Typed strict DTEs retain full root mechanics, source/page, flags, reprints, secondary source references, and recursive typed descriptive entries, including table cells and tagged markup. All authored source codes validate against the registry; no registration changes were needed. No `_copy` or version declarations occur.
- Public APIs expose identity, source, authored type, rating metadata, trap initiative and duration types. Prose, raw markup, effects, triggers, instructions, and source media flags remain internal pending licensing review. No structured detection/disarming DCs, saves, damage blocks, character-level ranges, or reset instructions occur; prose-only mechanics are deferred to Step 7.
- Tagged markup occurs inside entry prose. No new resolver references or target IDs were added; creature names remain prose, not Monster links. The existing resolver snapshot of 4,370 occurrences is unchanged. The implementation, full field inventory, and reference audit are in `analysis/remaining-json/traps-and-hazards/`.
# Domain discoveries

## Vehicles and Vehicle Upgrades

- `vehicles.json` contains 39 Vehicles and 31 Vehicle Upgrades. They remain separate domains. IDs are
  `vehicle:<source>:<normalized-name-slug>` and `vehicle-upgrade:<source>:<normalized-name-slug>`; no source/name or
  generated ID collisions occur. Vehicle types are OBJECT, SHIP, SPELLJAMMER, INFWAR, ELEMENTAL_AIRSHIP, and CREATURE.
- Vehicle DTEs retain all 46 observed root properties and typed nested hull, control, movement, weapon, action, station,
  trait, reaction, threshold, provenance, and recursive entry structures. Heterogeneous size, AC, HP, speed, pace,
  action, and cargo-capacity source forms are explicit variants. Fractional cargo values 0.5 and 0.25 retain exact
  `BigDecimal` values. One DMG record reprints into XDMG; two `otherSources` references validate against the Source
  Registry. No copy/version expansion or registry edits were needed.
- Vehicle Upgrade records only author name, source, page, `upgradeType`, and descriptive entries. They do not author
  structured costs, prerequisites, compatibility restrictions, or benefits. Prose-only upgrade effects, component
  instructions, weapon descriptions, and action text remain internal for Step 7 review. Vehicle mechanics are data,
  not movement, combat, damage, repair, ownership, installation, or cargo execution.
- Public Vehicle summaries expose identity, source, classification, size, AC, HP, and speed. Details add safe
  structured terrain, capacities, dimensions, pace, hull, component statistics, weapon statistics/cost values, ability
  scores, action thresholds, weight, cost, and immunity metadata. Upgrade public DTOs expose identity, source, and
  authored category only. Descriptive entries, markup, notes, media/token flags, and importer metadata are withheld.
- Four explicitly marked-up cross-catalog occurrences are integrated: 2 Spell and 2 Skill references. Outcomes are
  1 RESOLVED, 2 AMBIGUOUS, 0 UNRESOLVED, and 1 DEFERRED. Only one source-qualified exact Skill reference exposes a
  target ID. The previous 4,516 occurrences and their 1,917/1,485/44/1,070 outcomes remain unchanged. Object, creature,
  feat, action, condition/status, variant-rule, and adventure tags remain source text because no supported exact
  adapter exists for these contexts; no relationship is inferred from prose or shared names.
- Full source path/type evidence, public exposure decisions, references, and verification are in
  `analysis/remaining-json/vehicles-and-upgrades/`.

## Items and Base Items

- The source supports one unified immutable Item Catalog: `items.json:item` contains 2,512 concrete item records and `items-base.json:baseitem` contains 230 separately usable equipment definitions. They have compatible `(name, source)` identities and no collisions with each other. Definitions retain an `ITEM` or `BASE_ITEM` kind.
- The 241 `baseItem` fields are exact `name|source` references to base-item identities; they are not merge instructions. The catalog validates those references without merging definitions.
- `itemGroup` (119 records) is a selector/group structure. Auxiliary `itemProperty`, `itemType`, `itemTypeAdditionalEntries`, `itemEntry`, and `itemMastery` collections supply source-authored classification, reference, and template data, not additional equipment records.
- Item type identifiers are open source codes, sometimes qualified by source/edition. `itemType.abbreviation` is an authoritative code-to-name mapping. Names and source codes are preserved without an enum.
- 67 item roots use `_copy`; 35 include `_mod`, with operations limited to entries append/insert/replace and property append-if-absent. Two auxiliary type records use `_copy`. `_versions` is absent. Copy-derived source identities remain independent catalog definitions.
- Explicit cross-entity mechanics include attached spell UIDs, base item UIDs, ammunition UIDs, weapon property/mastery UIDs, structured pack-content item UIDs, and type codes. These are exact source references; no fuzzy matching is used. Markup references in entries remain narrative source text and are not normalized into API relationships.
- Secondary and reprint source references retain `SourceReference` page locators or source-qualified UIDs and are Source Registry validated. Source definitions from PHB/XPHB and DMG/XDMG remain separate.
- Item data has overlapping needs with Feat and Optional Feature models for source references and ability-score labels, but their mechanical grants are not semantically shared. This pipeline uses separate item-specific DTOs.
- Future `magicvariants.json` integration can connect through explicit item/base-item references. It must not generate variants until that source's identity and override semantics are separately analyzed.

## Existing discoveries

Prior source and domain findings remain unchanged. New item relationships are implemented locally rather than introducing unrelated shared effect or equipment models.

## Magic Variants

- `magicvariants.json` contains 225 generic rules, five copy-derived named transformations, and 127 auxiliary loot-table links. It contains no concrete generated-item enumeration. The variant catalog is kept distinct from the 2,742-definition Item Catalog.
- Applicability is explicit in `requires`/`excludes`: required clauses preserve OR alternatives with AND fields, and list-valued name/property terms remain alternatives. Matching uses exact normalized item facts; it does not parse narrative entries or fuzzy-match names.
- All 56 structured source codes are covered by Source Registry. Secondary and reprint references are preserved. Explicit attached spell references are source UIDs; other spell mentions in entries are not normalized.
- The five AU transformations copy through generic XDMG rules but terminate at two item anchors absent from Item Catalog. They are exposed as transformations with unresolved name/source references, not complete item records.
- Against current Items, generic rules produce 12,663 candidate rule/item matches. There are 3,361 repeated candidate generated identities, 213,179 same-item rule-pair overlaps, zero distinct same-item/same-output conflicts, and zero candidate identity collisions with existing Items. The data does not specify rule stacking, so no output combinations are materialized.

## Backgrounds

- Backgrounds are source-specific definitions: 171 roots normalize to 171 catalog records; 26 roots inherit from other backgrounds and are independently addressable. PHB and XPHB Acolyte remain distinct. Ability distributions are weighted choice structures, and proficiency/feat packages preserve their source grouping and alternatives.
- Starting equipment explicitly references Items through source-qualified UIDs. Current resolution yields 718 matches and eight unresolved occurrences (three exact UIDs), with no ambiguous UIDs; 204 descriptors are special text or equipment type. No Item is synthesized. Package choices preserve `_`, A/B, a/b and other option keys.
- Feat grants use source-qualified feat UIDs and explicit selection labels where present; category choice remains a category choice. Additional spell schedules preserve source-authored spell strings without a Spell Catalog dependency. Skill/language grants are source names or choice lists without source identity, so no edition-specific catalog IDs are invented.
- Feature prose and roleplaying tables remain typed internal entries. Public output exposes feature names and table labels/counts only. Source `data.isFeature` entry metadata, named section IDs and numeric table cells are retained by the shared entry DTE.
- Campaign prerequisites, additional-source references, other-source references, reprints and copy lineage are separate concepts. Copy parents resolve by exact name/source; entry modifications are applied in source order. No character creation state or grant application is modeled.

## Races and Species

- `races.json` contains 160 race roots and 98 subrace records. Five PHB subrace records have no name and are auxiliary relationship data; 93 named subrace roots have explicit parent race/source. `lineage` is a source flag on 50 races, not a general selectable-kind indicator.
- The corpus includes PHB and XPHB identities, which must remain independent. Edition is explicitly present on only 18 race roots, so it cannot be safely inferred for all records from book publication dates.
- Race/subrace mechanics have source-level typed candidates for movement, size, creature type, abilities, proficiency grants, senses, protections, spells, and traits. Entry prose remains distinct from those structured fields. Parent references alone do not authorize copying all parent mechanics into a subrace.
- There are 16 race and one subrace `_copy` declarations. Version lists contain 41 race and 14 subrace declarations; four race and three subrace declarations use abstract templates with 25 and 30 implementation rows. The race-specific expander applies the observed `appendArr`, `replaceArr`, `removeArr`, `prependArr`, and `replaceTxt` operations and substitutes typed `_variables` without reusing unrelated entity expansion contracts.
- All 35 primary/secondary/additional/reprint source codes are present in the Source Registry. Reprint records use both UIDs and structured UID/tag objects. No source registry changes were needed.
- Stage A and B implementation: strict typed DTEs deserialize the complete corpus; the immutable normalizer resolves 17 copy declarations and expands 103 direct/template version outputs. The classpath-loaded Race Catalog contains 356 definitions (160 race roots, 93 named subrace roots, 103 expanded versions); five nameless auxiliary subrace records remain typed import records. Parent relationships and the two parent-distinguished name/source collisions are preserved. Public DTOs expose structured mechanics, grant choices, movement markers, spell schedules, source references, and safe trait metadata without prose or copy/version instructions. `GET /api/races`, `GET /api/races/{id}`, and `GET /api/races/by-name/{name}?source=` are implemented. Full Maven suite: 176 tests, zero failures/errors.

## Classes and Subclasses — Stage A discovery only

- The complete recursive `data/classes/` corpus is 15 files containing 30 classes, 330 subclasses, 677 class features, and 1,499 subclass features. Features are independently authored roots, with 2,339 structural feature references reaching all 2,176 features. There are no unresolved own-feature references, ambiguous identities, or orphaned features. No class pipeline production implementation is claimed.
- Parent source is essential identity context. There are 124 subclass `(name,source)` collision groups caused by explicit parent-context copies, plus 55 class feature and 117 subclass feature collision groups. Full parent/level-aware tuples and proposed readable IDs are collision-free. Subclass short names are alternate keys scoped by parent class and subclass source, not global names.
- `_copy` occurs on 124 subclasses and 75 subclass features; there are no versions, templates, or modification declarations. Copies retain original source identities while changing parent context and sometimes feature level. An empty subclass-feature UID source slot defaults to PHB independently of parent class source; borrowed feature UIDs must not be rewritten to the referencing parent's source. Copy metadata defaults remain a specific Stage B design question.
- Optional Features and class/subclass features are different catalogs. All 162 direct/required Optional Feature references and two structured Feat references resolve exactly. Optional `featureType` progression and Feat `category` progression are distinct selector systems. Forty nested option groups retain explicit or unspecified counts and required-option metadata.
- Classes introduce progression table cells with explicit bonus, dice, and speed-bonus structures alongside numbers and presentation strings. Preserve all 59 owned level table groups internally. Numeric-looking strings and column labels do not authorize deriving a resource rule. Feature consumption names/amounts do not define a complete rechargeable resource pool.
- Spellcasting must distinguish source caster codes, cantrip/known/prepared/fixed progressions, symbolic preparation formulas, slot matrices, Pact presentation tables, spell grants, typed class/spell-level gates, usage tokens, and selectors. There are 1,068 structured spell UID occurrences and 186 selectors. No Spell Catalog IDs or unqualified source identities are invented; PHB and XPHB remain separate.
- Three class equipment references name exact authored item groups (`holy symbol|xphb` twice, `druidic focus|xphb` once), which the concrete Item Catalog does not expose. Retain group choices rather than pick items. Class equipment also has an `equipmentTypes` array absent from existing Background entry DTEs. Unqualified skill/tool choices must retain names/counts without an invented edition source. `additionalFromBackground` is a procedure flag rather than a Background definition reference.
- The existing common entry DTE hierarchy does not cover feature reference nodes, options, ability DC/attack declarations, or statblocks. Reuse requires deliberate bounded extensions or class-specific typed entries; never disable strict unknown-field checks globally. No speculative shared abstractions were implemented.
- All 26 referenced source identities resolve after adding the confirmed `UATheMysticClass` code without publication metadata. Registry size is now 179, with 66 existing Book Catalog associations. Existing registry test counts were adjusted, and the complete Maven suite still passes all 176 tests.
- Proposed architecture: four independent immutable definition/catalog views, owner-specific progression/spellcasting/starting option values, internal copy/source provenance, dedicated safe public DTOs, and separate `/api/classes`, `/api/subclasses`, `/api/class-features`, and `/api/subclass-features` routes. Stage A documents are in `analysis/classes/`; implementation awaits review.

## Classes and Subclasses — Stage B internal implementation

- Production typed DTEs now preserve all 15 complete JSON trees and 729 observed paths, proven by independent reconstruction tests. The immutable result contains 30 classes, 330 subclasses, 677 class features, 1,499 subclass features and 13 separate file metadata occurrences. No catalog beans, public DTOs or REST routes were introduced.
- All 124 subclass and 75 subclass-feature copies resolve with source-specific fill-missing semantics, atomic authored overrides and protected publication/source/edition/media metadata. Six copied features explicitly preserve `srd`, and three preserve `basicRules`; this corrects an incomplete Stage A prose summary while retaining its original complete inventory. `analysis/classes/COPY-RESOLUTION-SPEC.md` records the implemented policy and its evidence limits.
- The exact source/parent/level identity model is collision-free. All 2,339 authored structural feature references resolve; inheritance creates 386 more effective occurrences, giving 2,725 graph edges without adding definitions. No own-domain references are unresolved/ambiguous, and no cycles or orphaned features occur.
- The 1,068 direct structured spell UID occurrences are preserved, plus three explicit candidates in FRHoF Scion of the Three's `choose.from`. Stage A's direct-reference counter excluded those candidates; the complete authored spell UID count including choices is 1,071. Spell sources remain absent when unqualified. Typed class/spell-level gates, usage tokens, selectors, preparation expressions and caster codes do not evaluate character mechanics.
- Existing Items, Optional Features, Feats and Monsters provide exact reusable target IDs where supported. The three focus/symbol references remain typed deferred item groups discovered through existing typed Item source. Unqualified skills and deferred spells retain their source limitations instead of invented IDs. Optional Feature source defaults are applied only where corpus-supported UID grammar establishes them.
- Class-specific rich entry records avoid changing the shared entry deserializer or existing API behavior. Normalized content preserves feature choices, required options, variant flags, resource-consumption declarations, typed tables and internal descriptions. Root provenance retains renderer/copy metadata without exposing it through a public contract.
- Recursive classpath loading works from an isolated JAR, including nested files and duplicate resource rejection. Disabling resolver/URL caches prevents Windows JAR handle retention. No Stage B Source Registry edits were needed: all 26 identities resolve, including the unchanged identity-only `UATheMysticClass` registration.
- Stage C public catalogs, services, DTOs, and REST routes are implemented and summarized below. Spell/group catalog binding, prose-only replacements/recharge/focus rules, and public presentation limits remain explicit; no generic rules engine or character state was implemented.
- Final Stage B verification: the complete Maven suite passes **241 tests** (176 baseline plus 65 dedicated Stage B tests), with zero failures/errors/skips. Full source hashes remain unchanged, and independently reconstructed typed source trees match all original values/shapes.

## Classes and Subclasses — Stage C public catalogs and API

- One Spring-loaded `ClassDomainSnapshot` performs class normalization once; four immutable catalogs index 30 classes, 330 subclasses, 677 class features, and 1,499 subclass features. All retain Stage B IDs and exact parent contexts, so PHB/XPHB definitions remain distinct.
- Public APIs use separate summary/detail DTOs and thin read-only controllers for classes, subclasses, class features, and subclass features. Owners expose feature/subclass IDs with level/order metadata, while feature details remain independently addressable. Parent filters require the full class or subclass ID. Exact name routes preserve ambiguity and optionally filter on source.
- Structured starting choices, multiclass requirements, spellcasting schedules and choices, and typed table cells are projected without character calculations. Public serialization omits descriptive entry graphs, raw markup, import provenance, copy directives, and internal publication flags. Presentation-only table text and feature-entry tables are not projected; only typed class progression cells are exposed.
- All 2,725 effective feature reference occurrences resolve to the same IDs used by the feature catalogs. Features without direct progression order omit that order rather than deriving it from level or descriptive content.
- The 1,068 authored direct spell references and three explicit candidates remain qualified or unqualified as authored. Copy-derived subclass definitions yield 1,694 effective direct spell-reference occurrences in public detail responses. Three item-group references remain deferred choices without concrete item identities.
- Stage C adds no Spell Catalog, item-group expansion, character state, rules engine, or persistence. The full Maven suite passes **248 tests**, including seven dedicated Stage C tests; details are in `analysis/classes/STAGE-C-IMPLEMENTATION.md`.

## Spells — Stage A discovery

- Recursive `data/spells/` inventory: 18 JSON files with one `spell` root array each and 969 records. There are 148 recursive field paths and no explicit nulls, `_meta` roots, `_copy`, `_versions`, or template declarations. The reproducible scanner is `analysis/spells/discover-spells.mjs`; the field and source evidence is in `analysis/spells/`.
- Every spell root has a unique `(name, source)` pair. There are 386 repeated-name groups across sources, including 359 PHB/XPHB pairs. Keep all source variants separate; all 389 explicit `reprintedAs` UIDs resolve exactly to a spell root.
- Spell roots structure casting time, range geometry/distance, components/material variants, duration/concentration, ritual metadata, scaling formulas, and classification tags. Damage/healing amounts, DC calculations, and many full effects remain prose-only; do not derive them from entries.
- No canonical class spell-list membership or auxiliary list collection exists in the spell directory. Eligibility-like data lives in other owners' grant schedules/selectors; class Stage B records 1,068 direct UIDs plus three explicit candidates and 186 selector occurrences. Preserve selectors as filters and unqualified spell names without invented source identities.
- Across spell primary/secondary/reference/reprint source metadata, 106 distinct source identities are observed and all resolve in the existing 179-entry Source Registry. No registry update is indicated. Stage A added discovery documentation only; no spell production DTE, normalized model, DTO, catalog, service, or REST controller was implemented.

## Spells — Stage B typed import and normalization

- Reused the existing spell DTE hierarchy and added typed root metadata and a `SpellFileDte`. Strict classpath loading accounts for all 18 files and 969 spell roots; the recursively observed paths match all 148 Stage A inventory paths. Unsupported fields/shapes fail instead of being ignored.
- Added immutable source-aware `SpellDefinition` values and typed casting, range, component/material, duration, scaling, mechanics-tag, and source/reprint relationships. All 969 identities are unique; PHB and XPHB variants remain distinct. All 389 reprint edges resolve, and all 106 observed source identities pass the shared Source Registry.
- No class eligibility lists, numeric spell damage/healing effects, or copy/version expansions are present in the roots. Stage B preserves descriptive entries internally and structured tags as classifications, without deriving mechanics from prose. Class spell references remain unbound for Stage C; 620 unqualified occurrences remain ambiguous across multiple definitions.
- Stage B implementation and verification details: `analysis/spells/STAGE-B-IMPLEMENTATION.md`. No public DTOs, Spell Catalog, query services, or REST endpoints were added.

## Spells — Stage C public catalog and API

- Added public summary/detail DTOs, an explicit mapper, one startup-loaded immutable catalog, query service, and read-only REST routes. The catalog exposes all 969 normalized definitions with stable Stage B IDs and validates all 389 reprint targets.
- Public projections retain structured casting, range, component presence/cost, duration, classification, scaling, and source relationships. They omit entry prose, markup, import flags, and provenance. Because observed casting condition/note strings can contain 5eTools markup, the public projection exposes presence indicators only; material text and textual consume notes are omitted.
- No class/subclass eligibility filters or cross-catalog spell binding were added. The source roots lack authoritative full eligibility lists; ambiguous unqualified class spell references remain unresolved. Structured tags are classifications, not executable mechanics, and no numeric damage/healing calculations were added.
- Stage C implementation and test evidence: `analysis/spells/STAGE-C-IMPLEMENTATION.md`.

## Spells — Stage D mechanics investigation and school metadata

- The Stage D reproducible scan examined all 969 roots in 18 files, including nested `entries`, tables, tagged text, and higher-level entries. It found 642 `{@damage}` and 148 `{@scaledamage}` tags, zero dedicated healing tags, 126 generic `{@dice}` and 14 `{@scaledice}` tags, and no malformed markup. Full tag counts, argument shapes, examples, and paths are in `analysis/spells/mechanics-investigation-evidence.json`; the evidence omits descriptive entry prose.
- Formula syntax can be parsed into internal annotations with entry location and exact arguments, but tag presence does not establish semantic role. In particular, 223 spells have multiple damage formula annotations and 86 have multiple distinct formula arguments; conditional/branch associations, targets, repeat counts, and primary versus secondary effects need context and semantic verification. Of 354 spells with higher-level entries, 179 have neither the dedicated `scalingLevelDice` field nor an explicit scaling tag. Stage D does not extract formulas in production or expose prose.
- The public school is now a typed `{code,name}` value using a strict backend mapping for the eight observed codes: A Abjuration, C Conjuration, D Divination, E Enchantment, I Illusion, N Necromancy, T Transmutation, V Evocation. All 969 roots are projected and validated. School filters accept code or case-insensitive name. This is a deliberate breaking JSON property-type change; stable IDs and routes are retained.
- Area tags classify spells but do not supply radius/length dimensions. For all 643 area-tagged roots, no separate dimension property was found. Damage type classifications and typed scaling remain public; numeric formula annotations and prose-only effects remain excluded pending review of the proposed semantic boundary in `analysis/spells/MECHANICS-EXTRACTION-DESIGN.md`.

## Cross-Catalog References — Stage A discovery

- Existing catalogs use different identity formulas and semantic boundaries. The proposed reusable layer must call catalog-owned adapters and return existing stable IDs; it must not reconstruct IDs from names. Subclasses/features require parent/level context, items distinguish Item from Base Item, Magic Variants are transformation rules rather than generated items, and Monster IDs currently retain their Base64-derived legacy format.
- Class feature relations are already validated by `ClassReferenceResolver`: 2,339 authored feature edges and 2,725 effective edges resolve with no ambiguity, cycles, or orphan definitions. A shared cross-catalog layer should not duplicate that graph resolver. The class corpus independently reports 162 Optional Feature refs, 2 Feat refs, 166 concrete item refs, 3 item-group refs, 1 creature ref, 256 unqualified skill choices, and 1,068 direct spell refs.
- Of the class spell refs, 446 source-qualified occurrences match exactly. 620 unqualified references have multiple candidates; two `branding smite` references have one current candidate but remain unqualified. Three explicit choice candidates also exact-match but remain choices. Spell selector expressions are filters, not grants.
- Background equipment has 718 resolved item occurrences and 8 unresolved occurrences across three UIDs; the class data has three item-group choices. Magic Variants contain five transformations anchored to two absent XDMG items. None justify creating definitions or fuzzy binding.
- The architecture, result-state semantics, source inventory, and per-catalog evidence are recorded in `analysis/references/`. `scan-reference-evidence.mjs` reproducibly checks the class occurrence evidence. The scan does not modify production behavior or public API contracts.

## Cross-Catalog References — Stage B1 implementation

- Added an internal typed resolver, immutable target adapters/registry, and one startup `ResolutionSnapshot` for source-authored class spell, class/background equipment, and Item `baseItem` references. It uses Spell, Item, Base Item, and Item Group target categories and preserves target catalog IDs.
- The snapshot processes 2,392 selected occurrences: 1,570 resolved, 620 ambiguous, 8 unresolved, and 194 deferred. The 1,068 class/subclass direct spell references produce 446 exact qualified matches, 620 ambiguous names, and two unique candidates that remain deferred. Three choice candidates and 186 spell filters remain deferred.
- Background equipment remains 718 resolved plus 8 unresolved; class equipment has 165 exact concrete references and three deferred groups. Item source-authored `baseItem` declarations have 241 exact matches; inherited copy duplicates are excluded.
- Stage A's 169 class item total included one Psychic Blade feature statblock link in addition to 168 equipment UID occurrences. B1 handles equipment only; its 165 concrete matches plus that separate exact feature link reconcile the prior 166 concrete matches.
- Exact equipment lookup can resolve to a Base Item definition; `ResolutionResult.resolvedDefinitionKind` retains that distinction. The eight known Background misses remain warning diagnostics. Unknown explicit sources, malformed UIDs, duplicate target IDs, and mandatory exact-match failures reject startup.
- Existing `ClassReferenceResolver` continues to own all 2,725 effective feature edges. No public DTO or endpoint changed. Details and tests are in `analysis/references/STAGE-B1-IMPLEMENTATION.md`.

## Cross-Catalog References — Stage B2 implementation

- Added Feat, Optional Feature, Skill, and Language target adapters to the B1 immutable adapter registry. Each uses catalog-owned IDs and validates indexed target sources through the shared Source Registry.
- The shared startup snapshot now processes 3,875 occurrences: the original B1-selected 2,392 occurrences retain their outcome counts, and B2 adds 1,483 typed references. B2 targets include 160 Feat, 210 Optional Feature, 830 Skill, 273 Language, and 10 combined proficiency selector occurrences.
- B2 contributes 127 RESOLVED, 488 AMBIGUOUS, 22 UNRESOLVED, and 846 DEFERRED outcomes. Qualified references resolve only against their authored source. Unqualified names keep their candidate lists without source inference; filters, choices, prerequisites, and combined proficiency groups remain non-grant outcomes.
- Source-field extraction now covers Background and Race/Subrace grants, class and feature external identities, Feat proficiency and prerequisite references, and Optional Feature prerequisites/progression categories. Category tokens such as `any` are preserved as filters. Combined skill/tool/language choices are represented as adapterless `PROFICIENCY` references rather than guessed into one catalog.
- The class feature graph remains with `ClassReferenceResolver`; B2 adds no public fields or endpoints. Full outcome and validation details are in `analysis/references/STAGE-B2-IMPLEMENTATION.md`.

## Cross-Catalog References — Step 5 public integration

- Added the optional `crossCatalogReferences` property to existing detail DTOs for Classes, Subclasses, Class Features, Subclass Features, Backgrounds, Races/Subraces, Feats, Optional Features, and Items/Base Items. Summary DTOs and API routes are unchanged.
- Each lightweight reference includes a source-field locator, target kind, semantic role, authored token/name/source, and a stable `targetId` only when the startup resolver snapshot contains an authoritative exact match. Candidate lists, internal outcome/diagnostic values, descriptions, and import metadata remain private.
- One immutable projection index is constructed from `CrossCatalogResolutionCoordinator.snapshot()`. It preserves repeated occurrences and deterministic locator order; services perform owner-index reads without re-resolving or scanning target catalogs.
- All 3,875 B1/B2 occurrences are available on existing owner detail responses. The eight unresolved Background item references, unqualified skill/language references, the unique-but-unqualified Branding Smite references, and three class item groups remain without fabricated target IDs. Choice, prerequisite, filter, and group roles are retained.
- The existing ClassReferenceResolver feature graph remains domain-owned. Monster spellcasting, Magic Variant applicability, and markup-derived links remain outside the current snapshot and were not added to public APIs. See `analysis/references/STEP-5-PUBLIC-API-INTEGRATION.md` for the DTO audit, occurrence breakdown, compatibility analysis, and verification.

## Remaining JSON Catalogs — Step 6 inventory

- A recursive scan of `src/main/resources/data/` parsed and classified all 175 JSON files into 32 evidence families, with no parse failures or unclassified files. The previous `analysis/coverage-manifest.json` accounts for 173 domain files; the two additional support paths are `monsters/template.json` and `sources/registry.json`.
- The initial scan found 15 unimplemented source families and 2,034 top-level root records. After the requested Step 6 catalogs, 27 primary source families are implemented and 4 families (999 root records) remain: deities, crochet patterns, treasure tables, and recipes. Existing primary catalog coverage and all per-file field paths are captured in `analysis/remaining-json/`.
- Several user-facing family labels need narrower boundaries: `adventures.json` contains publication/index metadata and contents headers rather than chapter bodies; `bastions.json` contains `facility`; `homecrafts.json` contains crochet patterns; `loot.json` contains seven heterogeneous table collections; `trapshazards.json` contains separate `trap` and `hazard` arrays; and vehicle upgrades are distinct from vehicles.
- Five deity pairs share the same PHB source/name but differ by authored pantheon. Deity identity therefore needs pantheon context. The scanned source properties include three values not found in the current Source Registry snapshot (`HFDoMM`, `PiP`, `WGE`); this is a validation follow-up, not permission to invent metadata or modify the registry during this inventory.
- The repository MIT license and source SRD flags do not themselves establish publication rights for all embedded descriptive prose, recipes, crochet instructions, images, or source-specific materials. Future DTOs should default to structured, source-attributed fields and exclude prose/media pending review.
- Reproducible evidence and implementation planning are in `analysis/remaining-json/STEP-6-INVENTORY.md`, `SOURCE-COVERAGE-MATRIX.md`, `SCHEMA-COMPLEXITY.md`, `CATALOG-BOUNDARIES.md`, `DEPENDENCY-MATRIX.md`, `LICENSING-BOUNDARIES.md`, `IMPLEMENTATION-ORDER.md`, and `PERSISTENCE-READINESS.md`.

## Step 6 — Adventure Index and Name Tables

- Added independent typed Adventure Index and Name Tables pipelines for all 101 Adventure records and all 10 Name Table roots. Adventure IDs use `adventure:<primary-source>`; source codes are unique across the corpus, while the two authored `id` values that differ from their `source` values remain retained as internal authored identity metadata.
- Adventure publication metadata is available from `/api/adventures` and `/api/adventures/{id}` with source/group filtering and ambiguity-preserving name lookup. Chapters, navigation content, covers, and media paths remain internal. The typed navigation corpus contains one UID (`Tree Blight|CoS`); it is retained and source-validated but not linked because the current resolver does not support Monster targets.
- Name Table IDs use `name-table:<source>:<name>`. The catalog retains all 61 option tables and 2,822 authored rows, including rows authored with the `min: 99, max: 0` terminal sentinel. Public endpoints expose only source metadata and table/row counts; names, dice tables, and results are withheld pending publication-rights review. No random name generation is provided.
- The Adventure corpus confirms `PiP` as a primary publication source identity. Registered it without aliases or publication metadata; Source Registry count rises from 179 to 180 while known publication metadata remains unchanged. Adventure `parentSource` values are series/group context and are not treated as source-registry identities.
- Both loaders use classpath resources and strict typed deserialization. Details, field accounting, limitations, and verification are in `analysis/remaining-json/adventures/` and `analysis/remaining-json/names/`.

## Step 6 — Character Creation Options

- Added a strict typed importer and immutable catalog for all 44 `charoption` records in `charcreationoptions.json`. The complete source contains 62 recursive paths; descriptive entry objects reuse the existing strict Entry DTE hierarchy.
- IDs use `char-option:<source-slug>:<option-type-slug>:<name-slug>`. Authored `optionType` is present exactly once on each root and has codes `CS`, `DG`, `RF:B`, and `SG`; the full `(source,type,name)` identity has no collisions. Source spelling and page are retained.
- Prerequisite groups retain six unqualified race names, and two explicit `reprintedAs` records retain their `Name|RHW` UID and `background` tag. Race and Background targets are unsupported in the existing shared resolver, so no target ID is inferred and no resolver occurrence is added. Two `otherSources` values (`ToD`) remain publication provenance. Markup mentions are kept within internal entry content and are not converted to grants.
- All primary sources (`EGW`, `HotDQ`, `IDRotF`, `MOT`, `OotA`, `VRGR`), secondary/reprint sources (`ToD`, `RHW`), and observed source-bearing book/deck tags are registered. The public DTO excludes entry text/tables, prerequisite notes, and `hasFluffImages`; it exposes source/category metadata and structured prerequisite/reprint identities only.
- Routes are `/api/character-creation-options`, `/api/character-creation-options/{id}`, and `/api/character-creation-options/by-name/{name}?source=`. The metadata list also supports `name`, `source`, and `optionType` filters. No character eligibility or option application behavior is implemented.
- Full inventory, field type evidence, references, tests, and limitations are documented under `analysis/remaining-json/character-creation-options/`. The resolver remains at its established 3,875 occurrences and outcome counts.

## Step 6 — Cults and Boons

- `cultsboons.json` contains two distinct source collections: 30 Cult definitions and 12 Boon definitions. Their shared identity fields (`name`, `source`, `page`, `type`) do not make their schemas interchangeable. Cult-specific structured fields are `goal.entry`, `cultists.entry`, `signatureSpells.entry`, and `otherSources`; Boons have `abilityEntry` and `signatureSpells.entry`. Both retain rich `entries` internally.
- Source-aware IDs are `cult:<source-slug>:<name-slug>` and `boon:<source-slug>:<name-slug>`. The complete source/name identities are unique within each collection. The source roots contain no `_copy`, `_versions`, template, prerequisite, grant, choice, or numeric mechanic fields. Mechanics are in descriptive entry text and were not inferred.
- All ten `reprintedAs` values retain their authored UIDs. Nine tagged records use the `boon` tag, although each UID name/source also matches a Cult root in the MPMM collection. Those tag/collection semantics are not reconciled into a Cult-to-Boon relationship, and no target IDs are fabricated. Cult Geryon's untagged reprint UID matches a Cult root, but remains source provenance rather than a resolver edge.
- Explicit markup links in prose are scanned only for supported catalog targets: spell (65), item (5), skill (4), plus one item filter. These are catalog references, not grants. The shared startup resolver adds those 75 occurrences while retaining the historical 3,875-result subset and its original outcome counts. Creature/deity/condition tags remain preserved in internal entries because those targets are unsupported by the shared resolver.
- Public Cult and Boon detail DTOs expose identity, source/page, authored type, secondary sources, reprint identity, and exact linked spell/item/skill references. Descriptive entries, Cult goal/cultist descriptions, spell schedules, Boon ability narrative, raw markup, and importer provenance are omitted. No cult membership, boon application, eligibility, or rules execution is implemented.
- Implementation, complete field paths, reference inventory, licensing boundary, and test evidence are documented in `analysis/remaining-json/cults-and-boons/`.

## Step 6 — Objects

- The pinned Objects corpus contains 37 standalone world-object definitions across 12 registered source identities. `(source, name)` identities are unique. IDs use `object:<source>:<normalized-name-slug>`; DMG/XDMG reprints remain distinct definitions connected only by authored lineage.
- Objects have three authored categories (`U`, `SW`, `GEN`) and selectively include AC, HP, size, speed, abilities, senses, damage defenses, and condition immunities. AC and HP may be numeric or authored special text. Speed includes a scalar or typed walk/burrow values. These mechanics do not make Objects creatures or equipment; keep ObjectDefinition, MonsterDefinition, and ItemDefinition separate.
- Object actions include formatted text, typed attack structures, and nested list content. They remain internal and are not interpreted as combat execution. Descriptive entries, markup, senses, token attribution, and media references are not included in public API projections. No dimensions, damage thresholds, copy declarations, or numeric rules derived from prose were found.
- Two explicit `@spell` and two `@item` markup references use existing resolver adapters. `@action`, `@creature`, `@variantrule`, `@condition`, and other observed tags lack target adapters and remain source-preserved. No name-based references are inferred.
- Detailed source paths, reference evidence, identity, public DTO boundary, and verification appear in `analysis/remaining-json/objects/`.

## Step 6 — Bastion Facilities

- The pinned `bastions.json` corpus contains 70 facility definitions: AU 9, EFA 10, FRHoF 8, RHW 8, and XDMG 35. The only authored classifications are `basic` (6) and `special` (64). IDs use `facility:<source>:<normalized-name-slug>`; the full corpus has no `(source,name)` or ID collisions, and all source identities validate against the registry.
- Strict DTEs preserve facility identity/page, level, space labels, hireling exact/minimum counts, order labels, five prerequisite forms, and typed recursive entries. Spellcasting focus is explicitly polymorphic (boolean or string array); meaningful true/false is retained. There are no structured costs, combat statistics, movement, numeric benefits, or durations. No mechanics are derived from descriptive prose.
- Public summaries expose identity/source, type, level, and space. Details add safe structured prerequisites, hirelings, orders, and supported lightweight references. Descriptive entries and `otherSummary` strings, raw markup, fluff flags, and media are excluded. Copyrighted descriptive text remains internal pending publication decisions.
- Inline facility markup contributes 110 supported resolver occurrences (89 RESOLVED, 0 AMBIGUOUS, 6 UNRESOLVED, 15 DEFERRED). The previous 3,954 results preserve the baseline 1,704/1,178/30/1,042 outcome counts. Other markup target types remain source-preserved without speculative adapters. Bastion ownership, construction, turns, order execution, production, hireling management, upgrades, events, and eligibility are outside this data catalog.
- Field coverage, references, implementation decisions, public contract, licensing considerations, and verification are recorded under `analysis/remaining-json/bastion-facilities/`.

## Step 6 — Encounters

- `encounters.json` contains 42 encounter definitions, 93 ordered nested tables, and 2,299 ordered rows across 10 registered source codes. The root definition—not each row—is the catalog entity. Identity is `encounter:<exact-source>:<normalized-name-slug>`; no collisions remain, and duplicate `Urban` names stay distinct by source.
- DTEs and immutable definitions retain every authored field, including expressions, numeric roll ranges, all tagged outcome strings, optional attitude values, captions, footnotes, page provenance, and level bounds. `d100` rows with authored terminal ranges such as `97–00` are retained without interpreting the sentinel as an error.
- Public summaries/details expose source/name/page, table and row counts, dice expressions, level bounds, roll range bounds, and roll-attitude flags. Authored outcome strings, their creature/item/spell names and markup, captions, and notes remain private due to publication/licensing review. No mechanics are inferred from outcome text.
- The 4,319 markup tags inside result strings are inventoried separately. Fifteen explicit spell/item/skill tags use existing resolver adapters (3 resolved, 4 ambiguous, 3 unresolved, 5 deferred); only exact matches expose target IDs. Creature names/tags are never inferred as Monster links because a Monster target adapter is unsupported. Existing 4,064 resolver outcomes remain unchanged. The catalog does not roll or generate encounters, balance difficulty, or execute outcomes.

## Step 6 — Rewards

- `rewards.json` contains 278 independent Reward records from 26 registered sources. Types are Blessing, Boon, Charm, Curse, Draconic Gift, Fragment of Suffering, Inhabitation, Other, and Piety Trait. Source-aware IDs are `reward:<source>:<normalized-name-slug>`; `(source,name)` and ID collisions were not found. Rewards remain separate from Feats, Optional Features, Cults/Boons, Items, and Objects.
- The only structured reward benefit family is `additionalSpells` (89 groups; 113 spell UID occurrences). Typed variants retain ability choices, known spell lists, innate lists, daily/limited/rest buckets, rituals, and a source-defined resource label. No root prerequisite, charge, recharge, duration, score, proficiency, or defense structures exist. Descriptive entries are preserved internally and never interpreted into mechanics.
- The source also contains 30 `seeAlsoFacility` tokens across 29 rewards. Since FACILITY is not a supported resolver target, these receive no target IDs. Three typed inline object statblocks remain nested reward content; they are not Monsters. No source prose, markup, statblock payload, or media is exposed publicly.
- Supported explicit spell/item/skill references add 291 resolver occurrences: SPELL 257 (56 RESOLVED, 201 AMBIGUOUS), ITEM 24 (5 RESOLVED, 11 AMBIGUOUS, 5 UNRESOLVED, 3 DEFERRED), SKILL 10 (10 AMBIGUOUS). Overall this is 61 RESOLVED, 222 AMBIGUOUS, 5 UNRESOLVED, and 3 DEFERRED. The prior 4,079 occurrences retain outcomes 1,796/1,182/39/1,062. Full evidence is in `analysis/remaining-json/rewards/`.

## Step 6 — Variant Rules

- `variantrules.json` contains 231 top-level Variant Rule definitions across 15 primary source codes. IDs use `variant-rule:<source>:<normalized-name-slug>`; the complete corpus has no `(source,name)` duplicates or generated-ID collisions. Seven display names repeat across source books and remain separate source-aware definitions.
- Authored `ruleType` codes C, O, V, and VO are retained as metadata; 40 records have no classification. The root `type` is absent on 186 records and otherwise marks `entries` or `section`. There is no authored root parent/child edge. Nested section headings remain internal to their top-level definition.
- Strict DTEs retain all 119 discovered recursive field paths, 188 tables with 1,138 rows, nested sections, inline/link/image variants, typed hrefs, reprint UID variants, and five additional-source citations. No `_copy`, `_versions`, prerequisite, modifier, choice, or structured effect fields occur. Dice, DC, damage, and other mechanical language is in descriptive entries and is never parsed into rules.
- The public catalog exposes identity, source/page, authored classification, root entry kind, and section/table counts. Authored prose, examples, table contents, media, markup, and importer flags remain internal. Campaign toggles, execution, calculations, eligibility, and rule conflict handling are outside this catalog.
- Supported Spell, Item, Skill, and Item-filter markup adds 361 resolver occurrences: 102 RESOLVED, 214 AMBIGUOUS, 21 UNRESOLVED, and 24 DEFERRED. Only exact resolutions expose target IDs. Variant Rule, Action, Condition, Monster, Hazard, Trap, Vehicle, and other unsupported tags remain internal with no inferred targets. The previous 4,520 occurrences retain outcomes 1,918/1,487/44/1,071. Source and field evidence is in `analysis/remaining-json/variant-rules/`.

## Step 6 — Deities

- `deities.json` contains 563 Deity definitions across 13 registered primary sources and 23 pantheon values. Stable IDs use `deity:<source>:<normalized-pantheon>:<normalized-name>`. Pantheon is present on every record and produces no ID collisions. Five PHB `(name, source)` duplicate pairs (Oghma, Silvanus, Surtur, Thrym, and Tyr) remain independent definitions; pantheon disambiguates each pair.
- Six VGM records explicitly copy SCAG Orc-pantheon deities. Exact `(name, source, pantheon)` parents exist. The importer inherits only missing structured alignment, domains, and symbol values; it retains child title/entries, copy declarations, `customExtensionOf`, and separate VGM identities. No inferred deity relationships are made from names, domains, or pantheons.
- Alignment and domain arrays remain authored ordered strings, not enums or Class/Subclass relationships. Category is an authored grouping label. Titles, symbol text/artwork, worshiper descriptions, plane/province lore, and recursive entries remain internal; public summaries expose identity, pantheon, category, alignment, and domains, while details add alternate names, secondary sources, and lightweight supported references.
- Supported Spell, Item, and Skill markup adds 33 resolver occurrences: 19 RESOLVED, 0 AMBIGUOUS, 8 UNRESOLVED, and 6 DEFERRED. Unsupported creature/reward/condition tags and publication citations remain source content. Only exact authoritative targets receive public IDs. The previous 4,881 occurrences preserve outcomes 2,020/1,701/65/1,095. Field paths, five identity collisions, reference locators, public boundaries, and tests are documented under `analysis/remaining-json/deities/`.

## Step 6 — Treasure and Loot Tables

- `loot.json` has seven arrays and 100 source records. Six arrays define 75 named tables; `dragonMundaneItems` is a shared 25-row result table referenced by the four FTD Dragon definitions. The API uses one Treasure Tables catalog with six sealed definition shapes and keeps that result table as shared internal data.
- IDs use `treasure-table:<source>:<collection>:<normalized-name-slug>`. The collection segment is required because eight `(source,name)` pairs repeat between `individual` and `hoard` CR bands. DMG/XDMG Gem and Art Object tables remain distinct; 11 authored `reprintedAs` UIDs validate to XDMG targets.
- Currency fields use five authored denomination keys and string amount expressions. CR values, numeric roll ranges, and categories remain typed; six nested magic-item rows omit `max`, which is preserved. The importer does not infer dice from ranges, repair boundaries, execute choices, or generate treasure.
- Supported references come only from explicit Item markup, typed `fromItems` choice candidates, and typed rarity filters. The shared Item resolver handles links and preserves choice/filter semantics; `fromGeneric` and `fromGroup` selectors are retained without guessed target IDs. Result names and descriptions remain internal. Public detail DTOs expose table/range metadata and safe source identity only, never full table rows.
- Source audit, 131-path field coverage, identity/domain design, reference outcomes, licensing boundary, and tests are recorded under `analysis/remaining-json/treasure-tables/`.

## Step 6 — Recipes and Homecraft scope decision

- `recipes.json` contains 316 food and cocktail recipes across HF (79), HFFotM (76), HFLotT (75), PaF (76), and HFDoMM (10). The records are culinary content, not D&D crafting rules. All 316 have ingredients and instructions; none has structured game checks, costs, outputs, durations, or effects. The 73 recursive field/type paths and the typed variants are recorded in `analysis/remaining-json/recipes/FIELD-COVERAGE.md`.
- Stable IDs are `recipe:<exact-source>:<normalized-name-slug>`; all 316 `(source,name)` keys and IDs are unique. `HFDoMM` is registered as an identity-only source code because the source uses it on recipe definitions and secondary citations; no publication metadata was invented.
- The public API is a read-only metadata index: ID, name, source, optional page, authored type and dish-type labels, and aggregate content counts. Ingredients, quantities, yields, equipment, instructions, notes, aliases, diet/allergen labels, markup, and media remain internal pending publication authorization. No culinary instructions are converted into game mechanics.
- The source has eight recipe tags, one creature tag, one external link, and unit-formatting tags. No tags map to supported resolver targets; Recipes add zero occurrences and expose zero target IDs. The prior resolver totals remain 6,246 (2,517 RESOLVED, 2,054 AMBIGUOUS, 181 UNRESOLVED, 1,494 DEFERRED).
- `homecrafts.json` contains 20 crochet-pattern records. Homecraft is OUT OF SCOPE by explicit product decision, not a parsing failure or pending technical catalog. It has no catalog or API.
- The source-purpose and licensing checkpoint, field audit, domain design, reference inventory, implementation, and tests are documented under `analysis/remaining-json/recipes/`.

## Step 7 Batch 3A: Structured Feat mechanics

- Fourteen existing typed Feat field families now reach internal immutable mechanics: 74 authored
  and effective field occurrences on 59 owners, among 314 definitions. The nine existing versions
  have no such fields in their parents; synthetic inheritance verifies that expansion preserves
  all fourteen. No source, identity, reference or public DTO behavior changes.
- Existing `RaceGrantGroupDto`, `RaceGrantDto`, `RaceGrantChoiceDto` and `RaceResistanceDto` are
  reusable value shapes without a Race owner. Owning fields still distinguish skill proficiency,
  expertise, category/count selectors, choices and damage resistance. Their names do not justify
  flattening choices into grants or inferring target identities.
- Combined Feat choices retain the source choice filter field, which the narrower Race combined
  choice DTO lacks. Sense-key maps and literal `*` progression maps use small Feat values; closed
  sense records and integer Class milestones would lose source distinctions. Progression type
  labels remain unqualified labels, not selected Optional Feature IDs.
- Public exposure remains excluded. See
  [the Batch 3A report](../mechanics/batch-3a-feat-structured-mechanics/IMPLEMENTATION.md) for the
  reuse assessment, field inventory, exact preservation checks and publication boundary.

## Step 7 Batch 7: Legendary-group support preservation

- Legendary groups are a separate internal support domain: 188 authored roots, 16 copies and
  21 explicit versions produce 209 support groups, without adding Monster definitions. Their
  lair/regional/mythic bodies use shared typed EntryDte content, not Monster action/effect models.
- The one `legendaryGroupTemplate` is distinct from the 66 `monsterTemplate` records. Its observed
  append/prepend body-array dialect uses a dedicated strict resolver. Monster templates can also
  change a Monster's group reference; that existing reference remains authoritative. A shared
  Shadow Dragon name does not establish a dragon-specific group-version relationship.
- Every inherited/added body entry retains its original group/copy/template pointer and source
  hash. Existing shared syntax text/evidence structures preserve nested context without assigning
  copied content to a new authored owner. Copy lineage and version/template lineage stay distinct.
- All 32 applicable source codes are already registered. Group source occurrences use shared
  SourceIdentity/SourceReference; nested source qualifiers are preserved and validated separately.
- All 307 effective Monster references resolve by exact authored name/source to 182 groups;
  27 additional support groups remain retained. Missing exact associations remain unresolved and
  duplicate identities fail loading. No name-only fallback or shared resolver adapter is added.
- Body prose, table results, raw tags and evidence remain internal. No gameplay interpretation,
  public field, Monster identity or existing resolver occurrence changes. See the
  [Batch 7 report](../mechanics/batch-7-legendary-groups/IMPLEMENTATION.md).
