# Step 1 — Source Registry: Phase 5 Dataset Expansion

**Status:** Phase 5 dataset expansion reviewed, completed, and approved. The canonical dataset is deployed as a runtime resource at `src/main/resources/normalized/sources/sources.json`. Phase 6 has not begun.

## Dataset and coverage

The expanded dataset is [sources.json](../../../src/main/resources/normalized/sources/sources.json). It contains one Source entity for every registry code and embeds all supplied book/adventure identity metadata. Provenance and parent-reference tracking remain outside runtime resources under `datasets/step-1/v1/`.

The runtime dataset SHA-256 is `d5b52e21935b3ef0b9f69f0a769ea572c3b044a768820886c1fff9c8e56c6a29`. Repeated generation produced byte-identical output at this path.

| Coverage measure | Result |
| --- | ---: |
| Registry Source identities converted | 181 / 181 (100%) |
| Sources with publication metadata | 165 |
| Code-only Sources preserved | 16 |
| Book publication references | 66 / 66 (100%) |
| Adventure publication references | 101 / 101 (100%) |
| Total embedded publication references | 167 / 167 (100%) |
| Sources with code aliases | 6 |
| Publication IDs differing from Source codes | 8 |
| Records omitted from the approved identity scope | 0 |

The complete conversion reuses the approved representative conversion helpers and conventions in [convert-source-registry-representative.cjs](../../../tools/scripts/step-1/convert-source-registry-representative.cjs), with `--full` selecting all registry identities. It preserves registry order, then book-array order and adventure-array order within each Source. Optional absent values and empty alias arrays are omitted. Publication references keep collection `kind`; supplied IDs that differ from their Source code are retained. The representative batch was compared with the full dataset and all eight approved Source values are unchanged.

The approved ten-value `group` enum remains closed. The converter rejects unapproved group values before producing the dataset, and schema validation independently rejects them. No schema changes were made.

## Validation and integrity results

Validation used Ajv 8.17.1 with Draft 2020-12 and `ajv-formats` 3.0.1. The detailed full-dataset checker is [validate-source-registry-expansion.cjs](../../../tools/scripts/step-1/validate-source-registry-expansion.cjs).

| Validation | Result |
| --- | --- |
| Schema Draft 2020-12 meta-validation and compilation | Passed |
| Source objects validated | 181 / 181 |
| Rejection of an unapproved group value | Passed |
| Unique exact and case-insensitive Source codes | 181; no collision |
| Code-alias count and collision checks | 6; no collisions |
| Publication identity keys `(code, kind, publication ID)` | 167; no collisions |
| Book/adventure source associations | 167 / 167; all registry codes exist |
| Source-derived field comparisons | Passed for all Source codes and publication records |
| Provenance Source pointers | 181 / 181 dereferenced |
| Provenance publication pointers | 167 / 167 dereferenced |
| Approved representative Sources preserved | 8 / 8 |
| Source file hashes versus approved Phase 1 evidence | All three match |

The full provenance manifest is [source-registry-provenance.json](../../../datasets/step-1/v1/source-registry-provenance.json). For each Source and each publication reference it records the original file, JSON Pointer, source-file hash, exact source record ID where applicable, the Phase 1 corpus digest, and the Source schema ID. Provenance remains outside canonical Source JSON.

The canonical data preserves all identity-scoped input fields represented by the approved contract: source code and code aliases; metadata classification; distinct publication ID; name; publication and revision dates; group; title aliases; and raw parent token. It contains no workflow statuses. The approved exclusions (publication contents, storylines, levels, authors, covers, credits, and `publishedOrder`) remain outside the Source identity dataset.

## Parent-reference tracking

All 54 raw `parentSource` values are retained in canonical publication metadata and have matching entries in the separate [parent-reference review file](../../../datasets/step-1/v1/source-registry-parent-reference-review.json):

| Disposition | Count | Treatment |
| --- | ---: | --- |
| Resolved to one Source identity under the documented target rule | 34 | Source code recorded as candidate; raw token preserved |
| Unresolved | 18 | Tokens do not match a registry code or supplied publication ID |
| Ambiguous | 2 | A Source code has multiple publication records, so the raw token does not establish target granularity |

The unresolved values are `SAiS` (3), `PAitM` (3), `AitFR` (5), and `NRH` (7). The ambiguous references are the `MOT-NSS` adventure's `MOT` token and the `FRAiF-TLLoL` adventure's `FRAiF` token. These dispositions are not asserted inside canonical JSON and must not be treated as resolved mechanical dependencies.

## Determinism, incremental updates, and preservation

The generalized converter requires the three source files to match their pinned Phase 1 hashes. This detects source drift before generation. Its stable source and record ordering, exact joins, fixed field projection, and pretty-printed JSON produce repeatable output. Re-running `--full` against existing artifacts refuses any differing artifact rather than silently replacing it. The full manifest stores input hashes and pointers so later comparisons can identify changed source records and affected canonical entries.

The approved representative values are explicitly compared during expansion. The full dataset can be regenerated from pinned source metadata, but changes to source files require a reviewed evidence update and a report of affected entries before replacing the approved expansion. The existing overwrite guard is a safety stop; it does not itself produce a migration or semantic-diff report.

## Outstanding issues and integration limitations

- The 18 unregistered parent tokens and two target-granularity ambiguities remain unresolved and are fully tracked. They do not prevent structural completion of the Source registry.
- Parent relationships are raw metadata; this domain does not establish executable mechanics, edition equivalence, or publication-content availability.
- The schema validates individual Source objects. Dataset uniqueness, cross-file membership, conditional publication-ID representation, provenance coverage, and parent-target dispositions rely on the accompanying integrity validator and manifest.
- Provenance files identify records by JSON Pointer and pinned file hash. Upstream reordering changes pointers even when record identity remains stable; a future source revision comparison should report such pointer movement separately from content changes.
- The full expansion does not create Java DTOs or application integration; those belong to Phase 6.

## Recommendation

The complete registry and all in-scope publication metadata are accounted for, validated, and traceable. No unsupported Source identity or untracked parent reference remains. The dataset, schema, and Phase 5 output are approved. Phase 6 requires separate authorization.
