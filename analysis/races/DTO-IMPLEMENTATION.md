# Race and Species Pipeline Implementation

## Normalization

`RacesFileDte` deserializes the packaged race corpus with strict unknown-property handling. Race and subrace structures have dedicated DTEs, including numeric or object speed values, mixed lineage values, polymorphic reprint and entry modifications, typed version templates, implementation variables, and proficiency choice forms. Shared entry DTEs handle observed `itemSpell` entries and `data.overwrite` metadata.

`RaceDefinitionNormalizer` resolves copy parents by exact source-qualified identity, checks cycles and missing parents, applies observed entry operations in declaration order, and expands direct versions and abstract implementations. Template substitutions apply to typed mechanics and modification operands; unresolved placeholders fail. Source references are checked against the Source Registry.

The source contains 160 race roots and 98 subrace records. Five nameless subrace records remain typed import records without fabricated identities. The normalized catalog contains 356 definitions: 160 race definitions, 93 named subrace roots, and 103 expanded definitions. All 17 copy declarations resolve. Version expansions comprise 37 direct race versions, 11 direct subrace versions, 25 race template implementations, and 30 subrace template implementations.

Subrace definitions retain local mechanics and source-qualified parent references. Parent mechanics are not implicitly merged. Parent-aware subrace IDs keep duplicate name/source pairs (`Ixalan|PSX` and `Variant; Mark of Finding|ERLW`) distinct. PHB and XPHB definitions remain independent.

## Public projection

`RaceMapper` maps normalized definitions to `RaceSummaryDto` and `RaceDto`. The summary exposes ID, name, primary source/page, definition kind, parent reference, structured size, scalar walking speed, and lineage metadata. Detail DTOs expose edition, creature types, size and movement, abilities and choices, grouped proficiency grants, senses, resistances and protections, spell grants, trait names/tags, aliases, lineage metadata, age/height/weight data, explicit overwrite fields, secondary/additional sources, and reprint references.

Movement preserves the corpus's integer and boolean representations separately. A source `true` movement marker is returned as `sourceFlag: true`; the API does not assign additional meaning to that marker. Spell grants reuse the existing typed Feat spell DTO hierarchy. Grant choices preserve counts, alternatives, categories, and filters. No spell catalog is required.

Public projection omits descriptive entries, 5eTools markup, media paths, availability flags, copy/version instructions, template variables, and import provenance. A serialization test checks all 356 definitions for raw `{@...}` markup and internal source fields. Meaningful `false` and `0` values are retained.

## Catalog, service, and API

`RaceCatalog` loads `data/races.json` from the classpath with strict deserialization, invokes Stage A normalization once, validates source references and parent IDs, rejects duplicate IDs/identities, and builds immutable ID/name/source/kind indexes. The five nameless auxiliary records are not catalog entries. All 103 expanded definitions are addressable by ID.

`RacesService` supports case-insensitive name, source, and definition-kind filtering, exact name lookup that returns all ambiguous matches, and ID lookup. The controller exposes:

- `GET /api/races?name=&source=&definitionKind=`
- `GET /api/races/{id}`
- `GET /api/races/by-name/{name}?source=`

Unknown IDs/names return 404; invalid definition kinds return 400. Collection output is deterministic. Pagination is not implemented for this fixed 356-definition catalog.

## Validation and limitations

The complete Maven suite passes: **176 tests, 0 failures, 0 errors, 0 skipped** (169-test baseline, seven additional Race/Species tests). Race tests cover corpus counts, expansion retrieval, parent and edition identity, movement flags, choices, spell/source structures, public serialization, sorting/filtering, immutability, and REST behavior.

No character state, automatic parent/subrace combination, spellcasting calculations, trait effect execution, or selection logic is implemented. Named trait output is limited to names and source tags; full descriptive entries remain internal. Tool/proficiency keys and spell strings remain source-authored values where no reliable catalog identity is present.
