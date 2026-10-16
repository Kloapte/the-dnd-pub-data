# Actions DTO implementation

The complete `actions.json` resource is loaded through a strict `ActionsFileDte`. `ActionDte` models every observed record property. Typed `EntryDte` blocks reuse the existing strict entry hierarchy. Dedicated deserializers preserve the two observed polymorphic values: timing is either `{number, unit}` or a special string, and `reprintedAs` is either a UID string or `{uid, tag}`. Both reject malformed values and unknown object properties.

`ActionMapper` creates immutable `ActionDefinition` records with stable `action:<source>:<name>` IDs, `SourceReference` primary locators, source-exact timing alternatives, and tagged source-aware reprint references. `ActionImportRecord` retains typed entries, `seeAlsoAction`, `fromVariant`, and availability flags separately. No copy expansion or reprint merging occurs. Catalog startup validates all primary and reprint source codes, plus source suffixes embedded in related-action and variant-lineage strings, against the Source Registry. It rejects duplicate IDs or case-insensitive `(name, source)` identities. Catalog indexes by ID, name, and source; summary results are immutable and deterministically sorted.

Public `ActionDto` exposes ID, name, primary source/page, timing alternatives, and explicit reprint references. `ActionSummaryDto` exposes ID, name, and source. Both use `NON_EMPTY`. No prose, markup, availability fields, lineage, related-action strings, or provenance is returned.

Endpoints:

- `GET /api/actions?name=&source=` — sorted summaries, with optional case-insensitive substring name and exact source filters.
- `GET /api/actions/{id}` — one definition.
- `GET /api/actions/by-name/{name}?source=` — all exact case-insensitive matches, optionally filtered by source.

The collection has 48 records and is small enough to return without pagination. Tests validate full corpus deserialization, polymorphic timing/reprint forms, source validation and identity uniqueness, provenance retention, sorting/filtering, serialization exclusions, endpoints, not-found responses, and packaged JAR resource loading. Full Maven validation passed: 99 tests, 0 failures, 0 errors (baseline 92; seven tests added).
