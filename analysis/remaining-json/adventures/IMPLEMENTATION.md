# Adventure Index implementation

## Scope and evidence

The catalog imports the `adventure` collection in `src/main/resources/data/adventures.json` (101 roots). These records are publication/index metadata, not adventure chapter content. The source has 35 recursive field paths, 101 distinct `source` values, no `_copy`, `_versions`, template, or reprint declarations, and no tagged markup in the inspected corpus.

The Adventure source code is the stable identity because it is unique across all 101 roots. IDs are `adventure:<lowercase-source-slug>`. The authored `id` is retained internally: it differs from `source` on two records (`MOT-NSS`/`MOT` and `FRAiF-TLLoL`/`FRAiF`). No source or group identity is fabricated.

## Implementation

- `AdventureDte`, `AdventureLevelDte`, and `AdventureFileDte` strictly map the roots. Existing typed Book DTEs are reused for the observed polymorphic `contents` navigation records and cover shape.
- `AdventureMapper` creates immutable `AdventureDefinition` records and internal `AdventureImportRecord` provenance. The latter retains `contents` and cover data; these are not public fields.
- `AdventureCatalog` validates each root source against the Source Registry, rejects duplicate IDs/sources, validates structured navigation source/UID source codes, and creates immutable source, ID, name, and summary indexes.
- `AdventuresService` provides deterministic filtering and ambiguity-preserving name lookup. Routes are `/api/adventures`, `/api/adventures/{id}`, and `/api/adventures/by-name/{name}?source=`. The collection supports `source` and source-authored `group` filters.
- Public detail/summary fields are the ID, name, source, group, published date, author/aliases where present, parent-source group context, published order, storyline, and authored level range. Embedded chapter prose, navigation headers, covers, and media paths are excluded.

The authored `parentSource` values (`AitFR`, `NRH`, `SAiS`, `PAitM`) represent grouping/series context in this corpus, not primary publication source identities. They are preserved but not validated as Source Registry entries.

There is one structured navigation UID (`Tree Blight|CoS`, tagged as a creature) and one navigation `source` field. They are preserved internally and validated; no cross-catalog resolution is attempted because the shared resolver currently has no Monster target adapter. No other applicable cross-catalog relationship was found. Existing resolver outcomes remain unchanged.

## Source Registry

`PiP` is used as the primary source for *Peril in Pinebrook*. It was absent from the registry, so an identity-only source record with no aliases or invented publication metadata was added. `parentSource` values were not registered as publications.

## Validation

Tests cover strict full-corpus mapping, 101 unique definitions, source validation, authored-ID differences, nested Book header string/object polymorphism, immutable indexes, public serialization exclusions, filters and lookup, missing IDs, and classpath/JAR loading. See `FIELD-COVERAGE.md` for path coverage and observed nested variants.

Verification: full `mvn -q test` passed with 292 tests, 0 failures, and 0 errors. This includes the existing cross-catalog resolver and catalog tests.
