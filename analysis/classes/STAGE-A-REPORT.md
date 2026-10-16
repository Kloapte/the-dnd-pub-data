# Classes and subclasses: Stage A review report

Status: historical Stage A source/architecture report. Stages B and C are implemented; see [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md) and [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md). The authored source counts below remain unchanged.

## Results

| Item | Result |
| --- | --- |
| Physical source files | 15 JSON files, recursively discovered; no manifest or index |
| Root definitions | 30 classes, 330 subclasses, 677 class features, 1,499 subclass features |
| Root metadata | 13 `_meta` objects, separate from 2,536 definitions |
| Field coverage | 729 exact array-normalized paths; all shapes, types, counts, missing/null/empty observations and source/edition distributions inventoried |
| Structural feature references | 2,339 occurrences; all resolve uniquely to all 2,176 features |
| Parents and reprints | All 2,506 parent edges and 90 class/subclass reprints resolve |
| Duplicate full identities / proposed IDs | 0 / 0 |
| Insufficient `(name,source)` identity | 296 collision groups: subclass 124, class feature 55, subclass feature 117 |
| Orphans / dependency cycles | 0 / 0, including explicit feature markup cycle checks |
| Copies | 124 subclasses, 75 subclass features; all bases resolve, no chains or cycles |
| Versions / template expansions | No declarations; zero extra candidates; no expansion performed |
| Owned level table groups | 59: 47 class, 12 subclass; all have 20 source rows |
| Structured spell UIDs | 1,068; 446 qualified, 622 unqualified, deferred without a Spell Catalog |
| Spell filter selectors | 186 occurrences, 102 distinct expressions |
| Source Registry | 26 canonical identities / 31 spellings, all covered after one confirmed code addition |
| Maven | 176 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS |

PHB/XPHB remain independent. Explicit subclass-selection schedules move Cleric, Druid, Sorcerer, Warlock, and Wizard to level 3 on XPHB class definitions. Copies preserve older subclass sources and selected older feature UIDs. Spell known/prepared fields, caster codes, equipment packages, primary abilities, proficiencies, and resource presentation differ; no automatic rules migration is proposed.

## Recommended implementation architecture

Use independently addressable Class, Subclass, Class Feature, and Subclass Feature definitions, with full parent/source/level-aware readable IDs. Keep feature references and option configurations distinct from reusable definitions. Tables, spellcasting metadata, and starting option groups are owned immutable values, with complete internal preservation and narrower explicit public projections.

Separate classpath loading, strict typed DTE mapping, class-specific copy application, graph/reference resolution, normalization, immutable catalog indexing, querying, projection, and HTTP binding. Reuse shared `SourceIdentity`/`SourceReference`; preserve original source codes. Keep import directives, provenance, descriptions and renderer metadata internal.

Recommend separate `/api/classes`, `/api/subclasses`, `/api/class-features`, and `/api/subclass-features` list/detail routes, plus class-parent subclass listing and exact class name/source lookup. Use paginated summaries, safe detail DTOs with `NON_EMPTY`, deterministic sorting, and typed unbound spell/group references. No character state or generic rules execution.

## Specific Stage B risks and decisions

1. **Copy metadata semantics:** the corpus provides preservation flags and explicit overrides, but not its upstream consumer. Settle and test inheritance of unlisted metadata (especially edition and licensing flags) without assuming another entity's copier is compatible.
2. **Entry model coverage:** existing common entries do not support the observed reference, options, ability declaration, and statblock types. Decide on class-specific DTEs versus bounded shared extensions. Preserve all unknown-field checks.
3. **Item group binding:** two Holy Symbol occurrences and one Druidic Focus reference match authored `itemGroup` records, not concrete Item Catalog targets. Retain a group choice/deferred target form; do not select a concrete item or invent its ID.
4. **Spell dependency:** retain all UIDs, gate/usage structures, configuration suffixes, and selectors without claiming Spell Catalog validation. Unqualified names and empty/unrestricted filter expressions require explicit typed representations.
5. **Public progression scope:** retain every table internally but approve safe columns/cells explicitly. Do not derive resource rules, recharge timing, spell slots, or numeric counts from presentation strings/prose.
6. **Identity and classpath behavior:** full parent context is required even for same-name/same-source subclasses; packaged loading must enumerate all 15 resources rather than assume a filesystem directory or missing manifest.
7. **Builder limits:** catalogs can provide authored choices and explicit mechanics; prose-only effects, variant replacement targets, generalized recharge, spell focus rules and some sidekick mechanics cannot be calculated from structured fields alone.

There is no missing class-feature source data blocking typed model design. The unresolved policy and cross-catalog bindings above must be handled explicitly during implementation and review.

## Documents and evidence

- [SOURCE-INVENTORY.md](SOURCE-INVENTORY.md), [normalized-field-inventory.json](normalized-field-inventory.json), and [DISCOVERY-TABLES.md](DISCOVERY-TABLES.md).
- [IDENTITY-ANALYSIS.md](IDENTITY-ANALYSIS.md) and [FEATURE-GRAPH-ANALYSIS.md](FEATURE-GRAPH-ANALYSIS.md).
- [PROGRESSION-ANALYSIS.md](PROGRESSION-ANALYSIS.md), [SPELLCASTING-ANALYSIS.md](SPELLCASTING-ANALYSIS.md), and [STARTING-OPTIONS-ANALYSIS.md](STARTING-OPTIONS-ANALYSIS.md).
- [INHERITANCE-ANALYSIS.md](INHERITANCE-ANALYSIS.md), [EDITION-ANALYSIS.md](EDITION-ANALYSIS.md), and [CROSS-CATALOG-ANALYSIS.md](CROSS-CATALOG-ANALYSIS.md).
- [NORMALIZED-MODEL-DESIGN.md](NORMALIZED-MODEL-DESIGN.md), [DTO-API-DESIGN.md](DTO-API-DESIGN.md), and skill entry point [DTO-DESIGN.md](DTO-DESIGN.md).
- [SOURCE-REGISTRY-COVERAGE.md](SOURCE-REGISTRY-COVERAGE.md), shared [DOMAIN-DISCOVERIES.md](../domain/DOMAIN-DISCOVERIES.md), and [VERIFICATION.md](VERIFICATION.md).

Machine-readable supporting evidence includes `source-evidence.json`, `feature-graph.json`, `inheritance-evidence.json`, `mechanics-evidence.json`, `cross-catalog-references.json`, `source-registry-coverage.json`, `discovery-summary.json`, and `summary-details.json`. Existing `schema-*.json` reports were retained unchanged. Discovery scripts are analysis utilities, not application pipeline implementation.
