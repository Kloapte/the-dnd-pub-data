# Name Tables implementation

## Scope and identity

The catalog imports all 10 roots from `src/main/resources/data/names.json`. A root represents one source-authored name table, not a generated character or an individual name. All `(source, name)` combinations are unique, so IDs use `name-table:<source-slug>:<name-slug>`.

## Implementation

- `NameTableDte`, `NameTableOptionDte`, `NameTableRowDte`, and `NameTablesFileDte` provide typed Jackson mappings under strict unknown-field handling.
- `NameTableMapper` builds immutable `NameTableDefinition`, `NameTableOption`, and `NameTableRow` values. It retains source-authored dice expressions and row bounds exactly; it does not roll dice or generate names.
- `NameTableCatalog` validates each primary source against the Source Registry, rejects duplicate stable IDs, and builds immutable ID/name indexes and deterministic summary order.
- `NameTablesService` supports source filtering and ambiguity-preserving name lookup. Routes are `/api/name-tables`, `/api/name-tables/{id}`, and `/api/name-tables/by-name/{name}?source=`.
- Public DTOs are metadata-only: ID, table name, primary source/page, number of option tables, and row count. Table options, dice expressions, and results are intentionally omitted pending a specific publication-rights decision. No generated-name endpoint exists.

The corpus contains 61 option tables and 2,822 result rows. Some terminal rows use `min: 99, max: 0`; this is preserved exactly as source-authored sentinel data and is not normalized into a guessed range.

No copies, versions, templates, reprints, inline markup, or cross-catalog references were found. Sources `XGE`, `MTF`, and `GoS` already exist in the registry. No registry update was needed for this family.

## Validation

Tests verify all roots/rows load, nested fields are immutable, strict unknown-field rejection, source validation, public content exclusion, source filtering, exact ID detail lookup, deterministic ordering, and packaged-JAR classpath loading. See `FIELD-COVERAGE.md` for the full source shape inventory.

Verification: full `mvn -q test` passed with 292 tests, 0 failures, and 0 errors. This includes the existing cross-catalog resolver and catalog tests.
