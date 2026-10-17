# Encounters Catalog Implementation

## Scope and domain boundary

The catalog imports 42 authored encounter-table definitions from the pinned 5eTools v2.37.0 `encounters.json` resource. Each root definition contains one or more ordered tables and rows (93 tables, 2,299 rows). Rows remain part of their parent definition; they do not receive independent catalog IDs. The catalog is a read-only reference catalog, not an encounter generator or rules engine.

## Identity and provenance

IDs use `encounter:<exact-source>:<normalized-name-slug>`, for example `encounter:BGG:cloud-giant`. No `(source,name)` duplicate or ID collision was found across the full corpus. The source is included because `Urban` occurs under both DMG and XGE. All 10 exact source codes validate against Source Registry. Source and page provenance are preserved. Copy/version/reprint fields are absent in the source.

## Typed import and normalized model

`EncounterFileDte`, `EncounterDte`, `EncounterTableDte`, and `EncounterRowDte` strictly type all observed source properties. Resource loading explicitly enables Jackson unknown-field failures and validates every definition. The immutable normalized model retains row order, numeric ranges, expression strings, row results, attitudes, level bounds, table captions, footnotes, page locators, and roll-attitude flags. It does not parse dice expressions or infer quantities/mechanics from prose.

## Public DTO and licensing

Collection summaries expose ID, name, source, page, table count, and row count. Detail exposes these identity fields plus each table's authored dice expression, level bounds, row count, roll ranges, page, and roll-attitude flag. Captions, footnotes, result strings, result attitudes, markup, and media are omitted. The outcome rows contain creative, potentially protected encounter content; source-authored structured data is not assumed automatically redistributable. No raw markup or descriptive prose crosses the public projection.

## API

- `GET /api/encounters?source=` returns deterministic summaries, optionally source filtered.
- `GET /api/encounters/{id}` returns safe detail metadata or 404.
- `GET /api/encounters/by-name/{name}?source=` returns all matching summaries; source disambiguates duplicate names.

There are no roll, generation, balancing, or combat routes. No existing API or resolver configuration was changed.

## References and deferred mechanics

Encounter result markup was inventoried separately: 4,319 tokens across 17 markup tags (see [REFERENCE-INVENTORY.md](REFERENCE-INVENTORY.md)). Fifteen explicit spell/item/skill references use existing resolver adapters and public lightweight projections. New outcomes are 3 resolved, 4 ambiguous, 3 unresolved, and 5 deferred. The pre-existing 4,064 resolver occurrences retain their outcomes. Creature tags and mentions remain internal; no Monster target adapter exists and no IDs are inferred. Encounter generation, dice execution, result interpretation, quantity parsing, balancing, difficulty, and combat are deferred to future application/rules-engine work (Step 7 or later).

Authored creature quantities, attitudes, environmental instructions, and other mechanical details often appear only in the result prose or inline tags. They remain preserved as source strings internally and are not extracted into inferred mechanics; structured interpretation is deferred to Step 7.

## Verification

The `EncounterCatalogTests` validate full-corpus deserialization/counts, registered sources, stable identity, source-aware duplicate lookup, row preservation, immutable collections, strict unknown-field rejection, zero/false preservation, safe JSON projection, explicit reference projection, endpoint filters, and 404 behavior. Full Maven verification passed **343 tests** with no failures or errors. The packaged executable JAR started successfully and returned 11 XGE encounter summaries from `/api/encounters?source=XGE`; `git diff --check` passed.
