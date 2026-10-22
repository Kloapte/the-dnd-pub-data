# Step 1 — Source Registry: Phase 4 Validation and Feedback

**Status:** Phase 4 validation reviewed and approved. Phases 2 and 3 remain approved. Phase 5 dataset expansion has proceeded under the next checkpoint.

## Scope and method

The audit read `sources/registry.json`, `books.json`, and `adventures.json` from `src/main/resources/data/`. It used registry identity and publication identification metadata only. It did not interpret book contents, adventure storylines, level ranges, publication prose, or rules text.

The audit verified that all three source files match the hashes recorded in the Phase 1 evidence. It projected every registry entry into the approved Source shape in memory, associating book and adventure metadata by exact `source` code, then validated each projected entity against `schemas/source/v1/source.schema.json`. The projection output was discarded. The approved representative dataset and schema were not changed.

The executable audit is [analyze-source-registry-phase-4.cjs](../../discover/step-1/analyze-source-registry-phase-4.cjs). It uses Ajv 8.17.1 in Draft 2020-12 mode and `ajv-formats` 3.0.1. It verifies the schema meta-schema, strict compilation, source hashes, associations, identity keys, alias collisions, and the closed `group` enum.

## Coverage and structural findings

| Check | Result |
| --- | ---: |
| Registry Source entries | 181 |
| Unique registry codes (case-sensitive and case-insensitive) | 181 |
| Projected Source entries validated against schema | 181 / 181 |
| Sources with publication metadata | 165 |
| Code-only Sources | 16 |
| Sources with code aliases | 6 |
| Book metadata records | 66 |
| Adventure metadata records | 101 |
| Total embedded publication references | 167 |
| Metadata `source` values missing from registry | 0 |
| Duplicate `(Source code, kind, publication ID)` identities | 0 |
| Code alias collisions with codes or other aliases | 0 |

All 167 metadata records map to exactly one registry Source. Two Sources (`MOT` and `FRAiF`) each hold both a book and an adventure reference; the embedded list preserves both records. The schema accepts all 16 code-only Sources without fabricated publication fields.

All 167 supplied group values fall within the approved closed enum: `core` 6, `homecraft` 1, `organized-play` 1, `other` 2, `recipe` 4, `screen` 6, `setting` 15, `setting-alt` 8, `supplement` 70, and `supplement-alt` 54. A future value outside this set correctly fails schema validation and requires analysis and an approved schema change.

The source-ID differences are preserved by the `publicationId` convention in eight records: six Plane Shift books (`PS-A`, `PS-D`, `PS-I`, `PS-K`, `PS-X`, and `PS-Z`) and adventures `MOT-NSS` and `FRAiF-TLLoL`. The six registry source-code aliases remain separate from those publication IDs. Six metadata records supply title-alias arrays; these contain four distinct alias strings. `Planescape: Adventures in the Multiverse` occurs on three records and is therefore descriptive metadata, not a unique identity.

Metadata property frequency for the fields in the approved model:

| Canonical field | Supplied record count |
| --- | ---: |
| `name`, `published`, `group` | All 66 books and all 101 adventures |
| `revised` | 2 books; 0 adventures |
| `alias` / `titleAliases` | 4 books; 2 adventures |
| `parentSource` | 4 books; 50 adventures |
| Distinct source `id` from `source` | 6 books; 2 adventures |

The metadata files also contain fields outside this identity model: all 167 records have `contents`, `cover`, `group`, `id`, `name`, `published`, and `source`; all 101 adventures have `level` and `storyline`; 100 adventures and all 66 books have `author`; three books have `coverCredit`; and 26 adventures have `publishedOrder`. Those content and credit fields remain excluded under the approved scope. No conversion of those fields was attempted.

## Identity and reference integrity

The cross-source identity checks found no duplicate registry codes, case-folded code collisions, duplicate code aliases, code-to-alias collisions, missing registry association, or repeated publication identity key within a Source and kind. Exact supplied IDs are distinct from the Source identity where required. Where a publication ID equals the Source code, `publicationId` is omitted as approved; the original record remains identifiable through its source pointer in a conversion provenance manifest.

There are 54 `parentSource` occurrences across 16 distinct raw tokens. Thirty-six occurrences equal a registered Source code; 18 occurrences use tokens outside the registry (`SAiS`, `PAitM`, `AitFR`, and `NRH`). Code-level membership does not prove target identity: some tokens also match publication IDs, and the raw field does not tell whether a parent means a Source or a particular publication. These tokens remain raw and are not represented as resolved references. The pre-existing representative review records the sampled unresolved `SAiS` token and ambiguous `MOT-NSS` → `MOT` token.

## Preservation, provenance, and deterministic updates

The in-memory mapping copies the approved source fields without deriving titles, dates, classifications, editions, or parent targets. It preserves exact code spelling, aliases, IDs that differ from the code, names, date strings, group values, and raw parent tokens. Empty optional arrays and absent values are omitted. Intentional exclusions are listed above and in the approved contract. No identity-scoped field loss was detected in this projection.

Each projected Source came from exactly one registry row. Each embedded reference came from one record in the matching books or adventures array, with collection kind retained. The in-memory audit used source array position and pinned source-file hashes to retain traceability while validating associations. It did not create a new full-corpus provenance manifest. The representative batch's existing manifest was previously checked for every one of its eight Source and eight publication pointers. A full conversion must produce the same per-entity and per-publication manifest coverage before it can be approved.

The conversion rule is deterministic when it uses exact source-code joins, the stated field mapping, registry order, then book-array and adventure-array order, and omits only absent values and empty optional arrays. The current representative converter is focused and hard-codes its selection; it demonstrates the mapping but is not a full-dataset incremental updater. For expansion, compare stable Source codes and publication keys `(Source code, kind, publicationId-or-Source-code)`, record source hashes and JSON pointers, and report added, changed, or removed metadata for review. Do not silently replace previously approved interpretations when upstream records change.

## Maintainability and model feedback

The approved model remains a small composition: one Source identity, optional code aliases, and an optional list of publication metadata values. It faithfully represents the observed one-to-many cases without Book/Adventure inheritance or separate publication datasets. Code-only cases need no special subtype. The closed group enum is explicit and its maintenance rule is clear. The schema is concise and rejects unapproved fields, which makes source drift visible.

No blocking contract defect was found. The following are limitations or optional improvements for the later expansion work:

1. **Dataset-wide constraints are external.** The item schema cannot enforce unique Source codes, alias collision rules, publication identity keys, or the conditional requirement to include `publicationId` when it differs from `code`. Keep these in a focused batch-integrity validator; this is a required validation companion, not a reason to add artificial schema complexity.
2. **Parent target identity remains unresolved.** Keep raw tokens and the external incomplete-reference tracker. Do not convert membership in the registry into a resolved relationship.
3. **Ordering is procedural.** The schema does not define ordering for `publications`. Use deterministic source-file order in conversion and document it, or propose a canonical sort before full expansion if stable diffs require it. No contract change is currently needed.
4. **Provenance is a separate artifact.** Keep the manifest separate from canonical JSON and require a pointer and file hash for each Source and each publication record in every expanded batch.
5. **Excluded publication metadata is intentionally outside scope.** Authors, credits, covers, contents, storylines, levels, and `publishedOrder` are not preserved in this Source model. If a later domain requires one of them, establish that requirement in the relevant analysis rather than broadening Source speculatively.

No recurring source variation requires another submodel. No inheritance or additional abstraction is recommended. No schema modification or migration is proposed by this validation phase.

## Validation outcome and recommendation

The complete registry and publication metadata fit the approved Source contract under the requested source-identification scope. Structural validation passed for all 181 Source projections, and all cross-file association and identity checks passed. Parent relationships remain the known incomplete reference case. Source hashes match the Phase 1 evidence.

**Recommendation (approved):** retain the approved Source model and schema unchanged. Apply the required full-dataset integrity and provenance checks during Phase 5. The representative dataset remained unchanged during Phase 4.
