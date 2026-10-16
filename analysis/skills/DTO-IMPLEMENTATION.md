# Skills DTO implementation

## Imported corpus

- Imported and normalized definitions: **36**.
- Unique source-qualified identities: **36**.
- Sources: PHB and XPHB; both already existed in the Source Registry, so **0 new source identities** were added.
- Reprint references: **18** PHB-to-XPHB pairs, validated by source code and exact skill name/source identity.
- Registry coverage: all primary source codes and reprint targets resolve.
- Distinct explicit ability relationships: five observed values (STR, DEX, INT, WIS, CHA). No ability relationship is inferred.
- Unmapped source fields: **0**. Source prose, reprints, and availability flags are retained in internal import provenance; prose and flags are excluded from public DTOs.

## Types and mapping

- `SkillsFileDte` and `SkillDte` deserialize the single collection and its source records. Unknown root, skill, and supported nested-entry properties fail strict deserialization.
- `entries` uses the shared typed `EntryDte` hierarchy. The one list-shaped Athletics entry remains structured.
- `SkillMapper` validates required fields, maps explicit ability codes to `SkillAbility`, builds the stable `skill:<source-slug>:<name-slug>` ID, and creates a shared `SourceReference` with the original page.
- `SkillDefinition` contains only ID, name, source reference, and source-defined ability.
- `SkillImportProvenance` retains source file, `entries`, `reprintedAs`, and optional SRD/Basic Rules flags.
- `SkillDto` and `SkillSummaryDto` expose ID, name, source reference/page, and ability. Both use `NON_EMPTY` JSON inclusion. No raw prose, flags, or provenance are exposed.

## Catalog, service, and endpoints

`SkillCatalog` loads `data/skills.json` once from the packaged classpath. It rejects duplicate IDs and `(name, source)` identities, validates primary/reprint source codes against `SourceRegistry`, confirms reprint targets exist in the corpus, and stores immutable indexes and sorted summaries. It indexes source-qualified names, source-independent names, IDs, provenance, and source codes.

`SkillsService` supports case-insensitive source/name filtering, substring filters for collection queries, exact case-insensitive name matching for by-name lookups, ID lookup, and list-based ambiguity handling. The 36-entry catalog does not require pagination.

- `GET /api/skills?name=&source=` returns sorted summaries.
- `GET /api/skills/{id}` returns one skill detail.
- `GET /api/skills/by-name/{name}?source=` returns all exact-name matches, or source-filtered matches when `source` is provided. PHB and XPHB definitions are not merged.

Missing ID or by-name results use the shared 404 problem response. Invalid requests use the existing 400 response handling.

## Validation

Added 10 pipeline tests covering complete strict deserialization, full-corpus counts, all observed ability mappings, stable source-aware IDs, the unusual Athletics list entry, provenance, duplicate and unregistered source rejection, ordering and filtering, ambiguous names, public JSON omissions, REST status/response behavior, and JAR-classpath resource loading.

The full Maven suite passes: **80 tests, 0 failures, 0 errors, 0 skipped**. This includes the existing Language, Book, Source Registry, and Monster tests.

## Limits

No proficiency, expertise, modifier, or skill-check calculations are implemented because the Skills source does not define them. Ability values only represent the explicit source field. Publication rights for descriptive entries are undecided, so descriptions remain internal.
