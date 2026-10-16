# Background DTO Implementation

The strict typed Jackson pipeline loads all **171** records and normalizes **171** source-aware definitions. It resolves all **26** copies by exact parent identity, applies 23 ordered entry operations (13 insertions, 10 replacements), and records copy lineage outside public DTOs. There are no version declarations. The catalog validates primary, additional, other, reprint, copy-parent, feat, equipment and spell source references against Source Registry.

Ability distributions preserve weighted choices. Proficiency packages retain fixed keys, `choose.from` alternatives, counts and category choices. Feat UIDs resolve through Feat Catalog by source-aware identity, including explicit Magic Initiate selection labels. Starting equipment preserves fixed/alternative packages and typed entry metadata. Against Item Catalog, equipment references classify as 718 resolved, 8 unresolved, 0 ambiguous and 204 text/equipment-type descriptors. Skill and language grants stay source-authored names/choice groups because those grants do not carry source identity and the catalogs contain source-specific versions; no edition inference is used.

The model preserves 15 additional-spell grant blocks, four campaign prerequisite records, 13 `reprintedAs` relationships, 13 `additionalSources` declarations, and the one `otherSources` declaration. `entries` remain internal typed data; public responses expose feature names/types and characteristic table labels/counts only. The shared Entry DTE now retains `data.isFeature`, named section/id metadata, and integer table cells required by observed background entries.

REST API:

- `GET /api/backgrounds` with optional `name` and `source` filters.
- `GET /api/backgrounds/{id}` for structured details.
- `GET /api/backgrounds/by-name/{name}?source=` for source-specific matches or all same-name records.

The source corpus is a small static set of 171 roots, so the list endpoint returns filtered summaries without pagination.

`BackgroundPipelineTests` covers full strict corpus loading, copy expansion and cycles, source identity separation, ability/proficiency/feat choices, equipment alternatives and reference status counts, roleplaying table metadata, serialization, source validation, filtering, REST behavior, malformed records, and classpath JAR loading. The focused Background suite passed **13 tests**. The full Maven suite passed **165 tests, 0 failures, 0 errors, 0 skipped**.
