# Sense DTO Implementation

The typed source records are `SenseDte` and `SensesFileDte`. Their `entries` use the existing sealed `EntryDte` model, preserving strings and structured 5eTools variants without `Object` or generic maps. Strict Jackson deserialization rejects unrecognized fields.

`SenseMapper` creates immutable `SenseDefinition` records with deterministic `sense:<source>:<name>` IDs and shared `SourceReference` values. `SenseImportProvenance` retains markup, reprint targets, source flags, and import path separately. The catalog rejects duplicate IDs and case-insensitive `(name, source)` identities, validates primary and reprint source identities against `SourceRegistry`, checks reprint targets, indexes by ID/name, and publishes immutable sorted summaries.

The 8 source definitions normalize successfully. Distinct source coverage is 3/3 (`MM`, `PHB`, `XPHB`), and all three have publication metadata via the existing Book Catalog. No registry or book-catalog entries were added or changed. The API exposes only ID, name, and source/page. No explicit structured mechanical properties are available.

Endpoints are `GET /api/senses` (optional `name` and `source` filters), `GET /api/senses/{id}`, and `GET /api/senses/by-name/{name}` (optional `source`). Unknown results follow the existing 404 problem response convention.

Validation: the full Maven suite passes with 86 tests, 0 failures, 0 errors, and 0 skipped (baseline 80; 6 Sense tests added). Coverage includes strict complete-corpus deserialization, corpus count and source publication coverage, source-aware IDs and preserved reprint provenance, duplicate and unknown-source rejection, public serialization, case-insensitive query behavior, REST responses, and loading the corpus from a JAR classpath resource. Existing Language, Book, Source Registry, Skills, and Monster suites also pass.
