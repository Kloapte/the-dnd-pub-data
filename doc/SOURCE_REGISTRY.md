# Source Registry contract

**Scope:** Step 1, Source Registry contract and validation history.

**Phase 2 status:** Approved and closed after review of the representative conversion.

**Phase 3 status:** Representative conversion reviewed and approved. The batch remains a focused sample; it is not a full registry dataset.

**Phase 4 status:** Validation and feedback reviewed and approved.

**Current checkpoint:** Phase 5 is completed and approved. Phase 6 implementation is complete and awaits review.

The embedded `publications` list, per-record classification, distinct publication IDs, separate code and title aliases, and raw `parentSource` tokens are approved for representative conversion. The closed ten-value `group` enum is explicitly approved. Any unknown future group must trigger source analysis and an approved schema change; it must not be accepted automatically.

## Proposed model

Use one `Source` entity per stable source code. The code is the primary identifier. A Source may have no publication metadata, or it may contain one or more lightweight publication references supplied by the book and adventure metadata. Each reference carries its own `kind`, optional publication ID, and optional metadata.

This composition handles observed identity cardinality without separate Book and Adventure domain models. The `MOT` source code has both a book record (`MOT`) and an adventure record (`MOT-NSS`). `FRAiF` has the same pattern with a book (`FRAiF`) and an adventure (`FRAiF-TLLoL`). A single publication slot would overwrite one of these records; the `publications` list preserves both. The embedded values are source metadata references, not independent publication-management models.

### Properties

| Property | Required | Type | Meaning and constraints |
| --- | --- | --- | --- |
| `code` | Yes | non-empty string | Exact, case-preserved source registry code and primary Source identity. |
| `codeAliases` | No | unique non-empty string array | Aliases of the source code from `sources/registry.json`. Omit when there are none. |
| `publications` | No | non-empty array | Omit for code-only sources. Multiple metadata records may share the Source code. |
| `publications[].kind` | Yes when a publication reference exists | `book` or `adventure` | Which supplied metadata collection classified the record. |
| `publications[].publicationId` | No | non-empty string | Preserve the supplied `id` when it differs from `code`; include it to distinguish records when needed. Otherwise omit a redundant equal value. |
| `publications[].name` | No | non-empty string | Supplied publication/adventure name. |
| `publications[].published` | No | date string | Supplied publication date. |
| `publications[].revised` | No | date string | Supplied revision date. Observed on two book records. |
| `publications[].group` | No | supplied group enum | Preserve the source grouping label; it does not establish edition or rules equivalence. |
| `publications[].titleAliases` | No | unique non-empty string array | Human-readable title aliases, distinct from source-code aliases. |
| `publications[].parentSource` | No | non-empty string | Raw `parentSource` token from metadata. It is not by itself a resolved canonical parent reference. |

Unknown properties are rejected. Empty optional arrays are omitted. The schema requires only the stable code at the Source level and `kind` for each present publication reference; names, dates, IDs, aliases, groups, and parent values are not required.

The approved closed `group` enum values are `core`, `screen`, `setting`, `setting-alt`, `organized-play`, `supplement`, `supplement-alt`, `other`, `recipe`, and `homecraft`. A new source group requires analysis and approval of a versioned contract change rather than silent acceptance.

## Source-to-canonical mapping

| Source location/property | Canonical property | Mapping |
| --- | --- | --- |
| `sources/registry.json` entry `code` | `code` | Preserve exact code and case. |
| `sources/registry.json` entry `aliases` | `codeAliases` | Preserve non-empty source-code aliases; omit empty arrays. |
| `books.json` record `source` | `code` | Join to a registry code; retain the registry's code as primary identity. |
| `adventures.json` record `source` | `code` | Join to a registry code; retain the registry's code as primary identity. |
| Book/adventure collection | `publications[].kind` | Set `book` or `adventure` according to the record's collection. |
| Book/adventure `id` | `publications[].publicationId` | Preserve when different from `source`; use when required to distinguish multiple records. Do not confuse with a source-code alias. |
| Book/adventure `name` | `publications[].name` | Preserve the supplied title. |
| Book/adventure `published` | `publications[].published` | Preserve the supplied date as an ISO date. |
| Book `revised` | `publications[].revised` | Preserve the supplied revision date when present. |
| Book/adventure `group` | `publications[].group` | Preserve the observed group label without assigning further meaning. |
| Book/adventure `alias` | `publications[].titleAliases` | Preserve non-empty human-readable title aliases. |
| Book/adventure `parentSource` | `publications[].parentSource` | Preserve the raw token; resolve only after identity-role review. |

`contents`, `storyline`, adventure `level`, authors, and cover credits are excluded from this Source contract. `publishedOrder` is not an identity field. No rule or story text is consumed by the Source model.

## Aliases, identifiers, and relationships

The registry aliases `PS-A`, `PS-D`, `PS-I`, `PS-K`, `PS-X`, and `PS-Z` point to source codes `PSA`, `PSD`, `PSI`, `PSK`, `PSX`, and `PSZ`. The corresponding Plane Shift publication IDs also differ from the source codes. Keep both facts in their distinct fields: for example, `PSZ` is `code`, `PS-Z` is a `codeAliases` value, and `PS-Z` is also the supplied publication ID for that publication record.

Publication-title aliases are not code aliases. The human-readable alias “Planescape: Adventures in the Multiverse” is shared by two book records and one adventure record, so it is not a unique identity key.

`parentSource` remains a raw token on the publication reference. Eighteen of 54 observed parent tokens use values outside the registry: `SAiS`, `PAitM`, `AitFR`, and `NRH`. The data does not establish whether these are omitted source codes, publication IDs, or source-specific shorthand. Even when a parent token matches a registered code, the Source schema does not decide whether the intended target is the source context or a particular publication record. For example, the `MOT-NSS` adventure carries `parentSource: "MOT"`, while `MOT` also identifies its source and the associated book record. The model preserves this token but does not claim a resolved parent publication.

Do not add a placeholder Source for an unresolved parent token. A later reference-integrity pass must resolve targets by documented identity role and key, preserve unresolved tokens, and report competing candidates. No identity decision should rely on title similarity alone.

## Provenance and versioning

The Source object contains domain identity and publication facts only. Record provenance separately in a manifest or equivalent, including for each normalized Source and each embedded publication reference:

- Original file and JSON location.
- Pinned source revision or version where supplied.
- Content hash where available.
- Normalized Source code and publication ID where applicable.
- Schema identifier/version and the manifest's own version.

Do not put conversion, implementation, review, or completeness statuses in `Source` JSON. The schema uses a versioned path and ID (`schemas/source/v1/source.schema.json`, `urn:thedndpub:schemas:source:v1`). A future incompatible contract change must use a reviewed schema version and migration plan. Source updates should compare deterministic entry identities and hashes, preserving previously approved interpretations until changes are reviewed.

## Incomplete identity tracking

| Finding | Current treatment | Required follow-up |
| --- | --- | --- |
| 16 registry codes lack book/adventure identity metadata | Valid code-only Source entries; do not invent titles or dates. | Preserve all 16 codes and source references during conversion. |
| 18 `parentSource` values are outside the registry | Preserve raw tokens; canonical target is unresolved. | Review token roles and candidate identities before resolving a parent reference. |
| `MOT-NSS` parent token equals the shared source code `MOT` | Preserve on the adventure metadata reference; publication-level target is not asserted by schema. | Review target granularity before declaring the parent relationship complete. |
| `WGE` appears in one nested alternate-art `source` field | Outside Source Registry ownership; keep the source value traceable and unresolved. | Revisit during the relevant monster and alternate-art domain analysis. |
| Publication content/catalogs may be absent for later mechanics | Outside this Source contract. | Where required for an in-scope rule, record as unsupported using the roadmap's interpretation coverage rules. |

See the [Phase 1 discovery report](../discover/step-1/SOURCE_REGISTRY_PHASE_1.md) for exact code lists, counts, source locations, and census evidence. Incomplete references are documented here and in discovery artifacts, not represented by workflow fields in canonical JSON.

## Schema and examples

- [Draft 2020-12 Source schema](../schemas/source/v1/source.schema.json)
- [Complete metadata example](../schemas/source/v1/examples/source-phb.json)
- [Code-only example](../schemas/source/v1/examples/source-code-only-eepc.json)
- [Multiple publications and distinct ID example](../schemas/source/v1/examples/source-mot.json)
- [Code and publication aliases example](../schemas/source/v1/examples/source-psz-aliases.json)
- [Unresolved parent example](../schemas/source/v1/examples/source-aag-unresolved-parent.json)
- [Schema validation summary](../reports/step-1/SOURCE_REGISTRY_SCHEMA_VALIDATION.md)

These artifacts were approved for representative conversion and retained after Phase 3 review. The closed ten-value `group` enum remains part of the approved contract. A new upstream value requires analysis and an approved schema change. Full dataset expansion and application DTO implementation require their own later approval checkpoints.

## Representative conversion checkpoint

The approved focused batch contains Source codes `PHB`, `EEPC`, `MOT`, `PSZ`, `AAG`, `EFR`, `ERLW`, and `SAC`, with all six matching book records and both matching adventure records. It covers a code-only entry, a shared Source code with multiple publications, distinct publication IDs, both alias roles, a resolvable parent token, an unresolved parent token, and the supplied revision-date field.

The generated data is under [`normalized/source/v1`](../normalized/source/v1/representative-batch.json), with provenance and parent-reference tracking in sibling files. The [Phase 3 conversion report](../reports/step-1/SOURCE_REGISTRY_REPRESENTATIVE_CONVERSION.md) records mappings and validation results. The batch is approved as a representative conversion, not as a complete dataset. `SAiS` remains unresolved, and `MOT-NSS`'s `MOT` parent token still has source-versus-publication target ambiguity.

## Phase 4 validation and feedback

The approved schema and mapping were checked in memory against all 181 registry entries, 66 book metadata records, and 101 adventure metadata records. All 181 projected Source objects passed the schema. All 167 publication metadata records mapped to a registered source code, and no full normalized dataset was written. The [Phase 4 validation report](../reports/step-1/SOURCE_REGISTRY_PHASE_4_VALIDATION.md) documents coverage, identity checks, maintainability, intentional omissions, and limitations.

The review found no blocking contract defect. The current composition remains small and maintainable for source identification. Global uniqueness, conditional publication-ID rules, parent-target integrity, and provenance completeness require a dataset-level checker or manifest because the item schema cannot enforce them. Raw parent tokens remain unresolved where their target identity is unclear. These checks are required for expansion, but no schema change was proposed by Phase 4.

## Phase 5 dataset expansion

The complete dataset contains 181 Sources and 167 embedded publication references. Every Source passed the approved schema; all book and adventure `source` associations and provenance pointers were verified. The [Phase 5 expansion report](../reports/step-1/SOURCE_REGISTRY_PHASE_5_EXPANSION.md) records full coverage, validation, identity outcomes, tracked parent references, and update safeguards. The [representative dataset](../normalized/source/v1/representative-batch.json) remains as the approved sample. The canonical full dataset is the runtime resource [`src/main/resources/normalized/sources/sources.json`](../src/main/resources/normalized/sources/sources.json); provenance and parent-reference tracking remain under `normalized/source/v1/`.

## Phase 6 backend integration

The Spring Boot backend loads the approved dataset from `classpath:normalized/sources/sources.json` during application startup. It deserializes the JSON into immutable typed records and builds an in-memory index by exact canonical Source code and declared code alias. Startup fails with an `IllegalStateException` if the resource is missing, malformed, or violates required identity invariants. The service performs no source-specific parsing, normalization, persistence, or parent-reference resolution.

The read-only API exposes `GET /api/sources` to return the dataset in canonical order and `GET /api/sources/{code}` to find a Source by canonical code or exact declared alias. Alias requests return the same entity with its canonical code. Unknown values return HTTP 404.

The Source records map the approved `Source` and publication-reference shapes, including the closed publication kind and group enums, date values, aliases, and raw parent token. Empty optional arrays remain absent from HTTP JSON responses. No implementation or review status is part of the domain records.

For a future approved normalized Source update, replace the runtime resource only after checking its schema version, preserving the external provenance and incomplete-reference reports, and updating the integration tests and documentation. A schema change requires its own approved contract version and integration review; the runtime service must continue to consume canonical data without normalizing it.

The data retains 18 unresolved and 2 ambiguous parent references, described in the [Phase 5 expansion report](../reports/step-1/SOURCE_REGISTRY_PHASE_5_EXPANSION.md). The API returns their raw values and does not resolve them. Phase 6 implementation details and test results are recorded in the [backend integration report](../reports/step-1/SOURCE_REGISTRY_PHASE_6_BACKEND_INTEGRATION.md).

## Phase 6 backend integration

The Spring Boot backend loads the approved dataset from `classpath:normalized/sources/sources.json` during application startup. It deserializes the JSON into immutable typed records and builds an in-memory index by exact canonical Source code and declared code alias. Startup fails with an `IllegalStateException` if the resource is missing, malformed, or violates required identity invariants. The service performs no source-specific parsing, normalization, persistence, or parent-reference resolution.

The read-only API exposes `GET /api/sources` to return the dataset in canonical order and `GET /api/sources/{code}` to find a Source by canonical code or exact declared alias. Alias requests return the same entity with its canonical code. Unknown values return HTTP 404.

The Source records map the approved `Source` and publication-reference shapes, including the closed publication kind and group enums, date values, aliases, and raw parent token. Empty optional arrays remain absent from HTTP JSON responses. No implementation or review status is part of the domain records.

For a future approved normalized Source update, replace the runtime resource only after checking its schema version, preserving the external provenance and incomplete-reference reports, and updating the integration tests and documentation. A schema change requires its own approved contract version and integration review; the runtime service must continue to consume canonical data without normalizing it.

The data retains 18 unresolved and 2 ambiguous parent references, described in the [Phase 5 expansion report](../reports/step-1/SOURCE_REGISTRY_PHASE_5_EXPANSION.md). The API returns their raw values and does not resolve them. Phase 6 implementation details and test results are recorded in the [backend integration report](../reports/step-1/SOURCE_REGISTRY_PHASE_6_BACKEND_INTEGRATION.md).
