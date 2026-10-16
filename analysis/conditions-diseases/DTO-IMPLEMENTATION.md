# Conditions and diseases implementation

The source file deserializes strictly into typed root collections for condition, disease, and status records. Nested prose uses the existing sealed `EntryDte` hierarchy and strict entry deserializer. No source field is dropped by the DTE model; the adjacent status records are type-checked and their source/reprint codes validated, but are intentionally outside the two requested domain catalogs.

`ConditionMapper` and `DiseaseMapper` produce immutable, separate normalized definitions and import provenance. IDs are deterministic and source-aware (`condition:phb:blinded`, `disease:fraif:the-rusting`). Duplicate IDs and case-insensitive `(name, source)` identities fail startup. Catalogs retain immutable indexes by ID and name and deterministic summary lists. Catalog initialization checks primary, secondary, and reprint sources against `SourceRegistry`; it does not require publication records. No copy resolution, version expansion, or name-based cross-source merging is performed.

Public detail DTOs expose stable ID, name, primary source/page, explicit reprint targets, and condition secondary source references. Disease detail also exposes the optional source `type` as `classification`. Summaries expose ID, name, and source. Entry prose/markup, publication availability flags, image flags, and import provenance are omitted. Optional fields serialize away under `NON_EMPTY`; page zero and booleans would remain meaningful if present.

Endpoints:

- `GET /api/conditions?name=&source=` — sorted summary list with optional case-insensitive substring name and exact source filters.
- `GET /api/conditions/{id}` — one detail definition.
- `GET /api/conditions/by-name/{name}?source=` — all exact case-insensitive name matches, optionally source-filtered.
- `GET /api/diseases?name=&source=` — sorted summary list with optional filters.
- `GET /api/diseases/{id}` — one detail definition.
- `GET /api/diseases/by-name/{name}?source=` — all exact case-insensitive name matches, optionally source-filtered.

These small fixed reference collections return a complete list without pagination, matching existing small catalog patterns. The packaged classpath resource is loaded at startup. Tests cover corpus deserialization and counts, model identity, source/reprint preservation, entry provenance, filtering, ambiguity, serialization exclusions, HTTP responses, and JAR resource loading. Full Maven validation passed: 92 tests, 0 failures, 0 errors (baseline 86; six tests added).
