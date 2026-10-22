# Step 1 — Source Registry: Phase 3 Representative Conversion

**Status:** Phase 3 representative batch reviewed and approved. Phase 2 is approved and closed. This report records the bounded sample; Phase 4 subsequently validated the approved model against the complete registry and publication metadata without expanding the normalized dataset.

## Source inputs and traceability

The repository stores the requested original source files under `src/main/resources/data/`:

- `sources/registry.json`
- `books.json`
- `adventures.json`

All three files are byte-identical to their Step 1 Phase 1 evidence hashes. The conversion used only registry and source-identification metadata fields; it did not inspect book contents, adventure storylines, or rules text.

| Input | SHA-256 |
| --- | --- |
| `sources/registry.json` | `f54414851b9470158ebabaf1c5c847cce988d41aaeb3f573dec9f32b85594955` |
| `books.json` | `8d216dd1aa9e948546d3cd6bc1d92a32438049c8e3c0d250619c7f7e1937129c` |
| `adventures.json` | `cba46dc96d2b74fdc5d30cbea5f16f1867cbc89f64a8245c1e200794603fa6b1` |

The pinned corpus digest from Phase 1 is `df60cc4ad8599701aff178bed2826fb80d2d2cb1e862d67938d1873ec793b8c1`.

## Representative selection and coverage

The batch selects 8 of 181 Source codes (4.4%). It contains all 6 book records and both adventure records whose `source` matches the selected codes, and one code-only Source.

| Source code | Selected case | Publication records included |
| --- | --- | ---: |
| `PHB` | Normal book metadata | 1 book |
| `EEPC` | Code-only identity; no book/adventure metadata | 0 |
| `MOT` | Multiple records share one Source code; distinct adventure ID | 1 book, 1 adventure (`MOT-NSS`) |
| `PSZ` | Source-code alias and distinct publication ID use the same token in separate roles | 1 book (`PS-Z`) |
| `AAG` | Publication-title alias and unresolved parent token `SAiS` | 1 book |
| `EFR` | Parent token `ERLW` resolves to a selected Source and one publication record | 1 adventure |
| `ERLW` | Resolved parent Source and book metadata for `EFR` | 1 book |
| `SAC` | Additional revision-date edge case | 1 book |

The `MOT` record preserves both metadata objects under one canonical Source. `FRAiF` has the same observed multiple-record pattern but was not needed in this focused batch.

## Mapping and preservation

The conversion script derives each canonical entry from the source records at run time:

- Registry `code` becomes the primary `code`; the exact spelling is preserved.
- Non-empty registry `aliases` become `codeAliases`; empty arrays are omitted.
- Every book/adventure record is assigned by its exact `source` value and appears under its matching Source's `publications` list.
- Record collection becomes `kind` (`book` or `adventure`).
- `id` becomes `publicationId` when it differs from the owning source code. The source ID is still recorded in the external provenance manifest when it equals the code.
- `name`, `published`, `revised`, and `group` are copied when present.
- Non-empty metadata `alias` arrays become `titleAliases` and remain separate from registry aliases.
- `parentSource` is copied verbatim, including unresolved or ambiguous tokens.
- Missing/null optional fields and empty alias arrays are omitted. No content, storyline, level, author, cover credit, or workflow-status fields appear in canonical JSON.

Source-to-publication comparisons against the original arrays passed for all eight selected codes. All eight matching publication metadata entries are included exactly once; no selected source association was missing or reassigned. The two distinct-ID cases are represented as `MOT-NSS` and `PS-Z`. `PS-Z` remains separately present as the code alias of `PSZ` and as the publication ID of its book record.

No source-derived field discrepancy was found. No source inconsistency was corrected or silently resolved.

## Parent references

| Owner | Raw token | Result |
| --- | --- | --- |
| `EFR` adventure | `ERLW` | Resolved at source-code level to the selected `ERLW` Source; the token also matches its single supplied book ID. |
| `AAG` book | `SAiS` | Unresolved; not a registry code or supplied book/adventure ID. Preserve raw value. |
| `MOT-NSS` adventure | `MOT` | Ambiguous target granularity; the token matches the Source code and book ID `MOT`. Preserve raw value without asserting which relationship role it means. |

Unresolved and ambiguous reference records are stored in [representative-parent-reference-review.json](../../normalized/source/v1/representative-parent-reference-review.json), outside canonical Source JSON.

## Provenance

[representative-provenance.json](../../normalized/source/v1/representative-provenance.json) contains one registry JSON Pointer for each of the 8 Source entities and one publication JSON Pointer for each of the 8 included book/adventure records. Each pointer is accompanied by the exact source-file SHA-256. The manifest also records the Phase 1 corpus digest and the Source schema ID. Validation dereferenced every pointer and confirmed its code or publication ID and source association.

The normalized batch is [representative-batch.json](../../normalized/source/v1/representative-batch.json). The conversion utility is [convert-source-registry-representative.cjs](../../discover/step-1/convert-source-registry-representative.cjs), and focused integrity validation is [validate-source-registry-representative.cjs](../../discover/step-1/validate-source-registry-representative.cjs).

## Validation results

The batch was validated against `schemas/source/v1/source.schema.json` using Ajv 8.17.1 in Draft 2020-12 mode and `ajv-formats` 3.0.1. The schema passed meta-validation and strict compilation. All 8 Source objects passed.

Focused integrity checks passed:

- 8 unique Source codes, each present exactly once in the registry.
- No source-code alias duplicates or collisions with registry codes across the full registry.
- 8 publication identity keys, with no collisions within their Source entities.
- 6 book and 2 adventure associations match the source `source` values exactly.
- Source-code aliases and publication-title aliases are preserved in separate canonical fields.
- All 8 registry provenance pointers and 8 publication pointers resolve to the expected source records and file hashes.
- 1 parent reference resolves to a Source code and unique publication record.
- 2 parent references are tracked outside canonical JSON: 1 unresolved and 1 ambiguous.
- Every canonical object omits empty optional arrays, null values, unknown properties, and workflow-status fields.

The schema validates individual Source objects; the representative dataset is a JSON array whose members were each validated against that schema. This batch-level integrity check is separate from the entity schema, as intended.

## Remaining limits and review

- The batch is representative only and does not claim coverage of the other 173 registry codes.
- The 16 code-only cases found in Phase 1 remain valid identities; only `EEPC` is sampled here.
- The closed ten-value `group` enum is approved. A future unrecognized group must stop conversion for analysis and an approved schema change.
- Parent target semantics remain incomplete for `SAiS` and `MOT-NSS`'s `MOT` token.
- The schema cannot enforce global code uniqueness, alias-to-code collision checks, publication identity keys, provenance completeness, or the conditional rule for including `publicationId`; those require the focused integrity checks and later full-dataset validation.
- The isolated `WGE` alternate-art source value remains outside this batch and is tracked for the relevant monster-domain analysis.

The converted values, mappings, provenance, and parent dispositions were reviewed and approved. Phase 4 findings are in the [Phase 4 validation report](SOURCE_REGISTRY_PHASE_4_VALIDATION.md). The subsequent [Phase 5 expansion report](SOURCE_REGISTRY_PHASE_5_EXPANSION.md) records the complete registry dataset.
