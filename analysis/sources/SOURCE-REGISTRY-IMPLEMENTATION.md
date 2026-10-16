# Source Registry implementation

## Changes

- Added immutable `SourceIdentity`, shared `SourceReference`, optional `SourceMetadata`, and `SourceRecord` model types.
- Added `data/sources/registry.json` with 175 unique source identities and six explicit publication-ID aliases.
- Added a startup-loaded, immutable, case-insensitive `SourceRegistry`. It resolves publication associations through the existing `PublicationCatalog`, represents absent publication data explicitly with `PublicationMetadataStatus.UNAVAILABLE`, and rejects duplicate identities and alias conflicts.
- Reused the Book Catalog as-is for publication records and relationships; its records, IDs, DTOs, and resources were not changed.
- Updated normalized language source identity and monster source identity/reference models to use the shared types. Language and monster public source-code values and entity IDs remain unchanged. Secondary source locators retain page values.
- No database, new entity type, or publication records were introduced.

## Coverage

- Distinct source identities: **175**.
- Identities associated with existing publication metadata: **66**.
- Identities without publication metadata: **109**.
- Resolved publication aliases: **6** (`PS-Z`, `PS-I`, `PS-K`, `PS-A`, `PS-X`, `PS-D`).
- Publication records added/changed: **0**.
- Source types inferred for unresolved codes: **0**.

## API impact

Language `source` remains a string. Monster `identity.source.code` remains a string. Primary source page values, secondary/reference source codes and pages, reprint references, filters, name/source lookups, and IDs are preserved. The shared reference value serializes as the existing source string plus an optional page. No publication title, date, author, or internal metadata is added to entity responses.

## Validation

Added tests for all 175 discovered source identities, known publication relationships and the six ID aliases, explicit unavailable publication metadata, case-insensitive lookup, alias conflicts, source-reference serialization and page preservation, existing REST/API behavior, and registry resource loading from a JAR classpath. Language and monster catalog startup validates primary, secondary, and reprint source references across the complete imported corpora; existing tests cover copy/version processing and normalized entity counts.

Full Maven test suite: **70 tests passed, 0 failures, 0 errors, 0 skipped**. Maven used a workspace-local repository because the configured default repository path (C:\.m2\repository) was not writable. The missing dependencies were fetched after enabling Maven Central access.

## Known limitations

The registry stores only identities and confirmed aliases. Existing `books.json` is the sole publication metadata registry. Unsupported or ambiguous source classifications and unresolved reprint relationships are documented in `SOURCE-INVENTORY.md`; no values were fabricated.



