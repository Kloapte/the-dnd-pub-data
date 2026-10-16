# Stage A verification

Historical Stage A verification is retained below. Current Stage B results are recorded in the final section and [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Scope

Discovery completed on 2026-10-16. No class/subclass production Java, source corpus edits, copy/version expansion, or REST implementation was performed. The only application resource edit is the confirmed missing `UATheMysticClass` Source Registry identity, authorized by the requested skill and Phase A13. Three existing Source Registry test count expectations were adjusted from 178/112/178 to 179/113/179.

## Discovery checks

The recursive scanner reads every physical file under `src/main/resources/data/classes/`, parses every root and nested field, and records per-file hashes. Its complete definition/root/path totals are 15 files / 2,536 definitions / 729 paths. No source JSON record was skipped, and malformed JSON would terminate the scan rather than report success.

Full kind-specific tuple indexes and readable proposed-ID collision checks find no duplicate identities. Complete structural feature UID parsing and parent/copy/reprint checks resolve all applicable own-domain references. Structural reachability covers all 2,176 features; no orphan is discarded. Cycle checks cover both structural dependency edges and those edges plus feature markup links. Registry identity and alias indexes report no conflicts; every observed source code resolves.

All source transformation fields, including unfamiliar descendants, are represented in the path inventory. The only observed declarations are copies; no expansion was executed. Missing and optional values are counted against observed parent objects, while array elements have no object-property missing denominator. Counts are complete; examples are deliberately bounded.

Reproduction commands:

```sh
node analysis/classes/discover-classes.mjs
node analysis/classes/write-discovery-tables.mjs
```

Generated outputs are deterministic for unchanged sources/registry and contain no wall-clock timestamps. Source SHA-256 values permit comparison with later snapshots.

Final artifact review checked every recorded source hash against its file, reconciled every path's JSON-type/source/edition/object-shape totals, validated graph endpoints and Markdown links, and found no inconsistencies. Repeating both discovery commands produced byte-identical JSON evidence and discovery tables. The final Git diff contains no class corpus changes, no production Java changes, and no unrelated pipeline edits; `git diff --check` passes. No formatter is configured in the Maven POM.

## Maven suite

Command: `mvn test`.

The first sandboxed attempt stopped before compiling or running tests because Maven could not create its local repository at `C:\.m2\repository`. The requested suite was rerun with the existing approved Maven permission and completed successfully using Java 21 / Spring Boot 4.0.5.

Result: **BUILD SUCCESS — 176 tests, 0 failures, 0 errors, 0 skipped**. Maven reported 24.145 seconds and completion at 2026-10-16 11:35:46 +02:00. All existing catalog/Spring context tests and all five Source Registry tests passed. There are no pre-existing test failures to report. No new tests were added and no production class pipeline behavior was tested or claimed.

## Review boundary

All requested A1–A13 analyses and design deliverables are present. Phase A14 checks cover source accounting, graph/identity results, progression/spell/equipment/inheritance/edition findings, cross-catalog/source coverage, normalized/public architecture, and existing Maven regressions. Stage B remains pending review, with explicit risks recorded in [STAGE-A-REPORT.md](STAGE-A-REPORT.md).

## Stage B final verification

Stage B now implements strict source loading, class-specific copy application, exact graph/source resolution and immutable internal definitions. Public DTOs, catalogs, services and routes remain Stage C work.

Final command: `mvn test`, completed 2026-10-16 at **12:57:53 +02:00** in **43.843 seconds** using Java 21 / Spring Boot 4.0.5. Result: **BUILD SUCCESS — 241 tests, 0 failures, 0 errors, 0 skipped**. This includes all **176 baseline tests** and **65 dedicated Stage B tests** across six test classes. There are no unrelated pre-existing failures.

The full-corpus source test independently reconstructs every original JSON tree from the typed DTEs, asserts all 15 files are equal, and accounts for all 729 property paths. All 2,536 roots and 13 file metadata records are retained. Copy tests account for 124 subclasses and 75 features, including six explicit SRD and three basic-rules preservation flags. Graph tests verify 2,339 authored references and 2,725 effective occurrences, unique full-context IDs, exact parents and reprints, missing/ambiguous identities, cycles and zero actual orphans.

Focused tests cover dense/sparse progressions, source table cell variants, known/prepared/fixed/Pact casting, class/spell-level gates, usage tokens, explicit and unrestricted choices, all 1,068 direct spell UIDs plus three explicit choice candidates, proficiency/equipment alternatives, currency, three deferred item groups, unknown-source rejection, unsupported copy operations/paths, explicit null/false/empty overrides, defensive copies and deterministic normalization. Existing shared entry consumers retain their previous behavior.

Isolated JAR tests load the complete corpus, discover nested files without a manifest, reject duplicate resources and empty discovery, and successfully release JAR handles for Windows cleanup. No production class catalog or startup bean was added.

Final review confirms readable Java records/types in separate files, immutable nested collections, no untyped model fields, no changes to existing production Java or API contracts, and no Stage B source corpus/registry edits. The 15 source SHA-256 values match Stage A. `git diff --check` passes. No formatter is configured in the project. Transient JSON trees/reflection arguments are limited to import/parsing utilities; known source and normalized model components remain typed.
