# Step 7 Batch 1: Class/Subclass Progression Column Positions

Implemented and verified on 2026-10-17 against the pinned 5eTools v2.37.0 corpus.

## Defect and evidence

The internal normalizer preserves ordered cells, including source strings classified as
`PRESENTATION_TEXT`. The previous `ClassPublicMapper.safeTables()` removed those cells from
public rows while keeping the original column labels. A retained value after any withheld cell
therefore occupied a compact array position different from its authored column.

PHB Barbarian is the required regression: `classes/class-barbarian.json`,
`$.class[0].classTableGroups[0].rows[0]`. Column zero is a withheld text-valued Rages cell;
column one is the typed Rage Damage BONUS value 2. That bonus previously appeared only at
`cells[0]`, without a way to associate it with column one.

The independent [authored audit](audit-authored-rows.mjs) reproduces the original analysis G1.
[authored-rows.json](authored-rows.json) records all 97 exact row locators, source widths,
retained column indexes and hashes of the 15 class source files. It contains no withheld cell
content. The original Step 7 scanner and evidence files are unchanged.

All paths below are relative to `src/main/resources/data/classes/`. Row indexes are zero-based;
the spans describe inclusive endpoints, not new row identities.

| Source owner | Source file / root | Table / affected row indexes | Authored rows | Effective rows |
|---|---|---|---:|---:|
| Artificer EFA | `class-artificer.json`, `$.class[1]` | `classTableGroups[0].rows[0]` | 1 | 1 |
| Barbarian PHB | `class-barbarian.json`, `$.class[0]` | `classTableGroups[0].rows[0]` through `rows[19]` | 20 | 20 |
| Barbarian XPHB | `class-barbarian.json`, `$.class[1]` | `classTableGroups[0].rows[0]` through `rows[19]` | 20 | 20 |
| Warlock PHB | `class-warlock.json`, `$.class[0]` | `classTableGroups[0].rows[0]` through `rows[19]` | 20 | 20 |
| Psi Warrior XPHB, Fighter XPHB | `class-fighter.json`, `$.subclass[23]` | `subclassTableGroups[0].rows[2]` through `rows[19]` | 18 | 18 |
| Soulknife XPHB, Rogue XPHB | `class-rogue.json`, `$.subclass[20]` | `subclassTableGroups[0].rows[2]` through `rows[19]` | 18 | 18 |
| **Total** | **Six roots** | | **97** | **97** |

Effective counts are distinct owner/table/row occurrences after copy resolution across all
30 Classes and 330 Subclasses. Copy expansion adds no affected owner or row. There are **117
affected public path occurrences**: the same 20 Warlock rows also appear in
`spellcasting.pactTables`. These are repeated projections of existing rows, not 20 new authored
rows. All slot and pact table paths are covered by the fix and corpus regression.

## DTO and compatibility decision

The [DTO design checkpoint](DTO-DESIGN.md) was recorded before production implementation.
The selected contract is additive:

- `ClassProgressionTableRowDto` keeps `level` and the existing compact `cells`, and adds
  `positionedCells`.
- `ClassPositionedTableCellDto` contains zero-based `columnIndex`, `kind`, and optional `number`,
  `dice`, `rollable`. It has no text component and rejects presentation-text kinds and negative
  indexes.
- `ClassProgressionTableDto` retains the existing `order`, `kind`, `title`, `columns`, `rows`
  and `subclasses` JSON fields, using public row carriers.
- `ClassSpellcastingDto` retains existing spellcasting JSON fields and uses the public table
  carrier for both `slotTables` and `pactTables`.

`safeRow()` enumerates original, unfiltered normalized cells. Each retained cell is appended
unchanged to `cells`; its safe value is also projected with the original enumeration index.
The filtered-array index is never used as the source column index. Table, row and retained-cell
order remain unchanged. Existing label sanitization, selectors and metadata remain unchanged.

All four public records use `NON_EMPTY` and defensive collection copies. Absent/empty optional
fields are omitted. Index zero, numeric zero and Boolean false remain serialized. Rows with
all cells withheld, and supported empty rows, preserve their level and omit both empty arrays.
There are no placeholders, reconstructed values or calculations.

### Public JSON example

Before, PHB Barbarian's first row:

```json
{"level":1,"cells":[{"kind":"BONUS","number":2}]}
```

After:

```json
{
  "level": 1,
  "cells": [{"kind":"BONUS","number":2}],
  "positionedCells": [{"columnIndex":1,"kind":"BONUS","number":2}]
}
```

The existing table columns remain `["Rages","Rage Damage"]`. Consumers requiring column
associations use `positionedCells[].columnIndex` with `columns`. The legacy `cells` array
remains compact and has exactly its prior meaning and values. No existing JSON property is
removed or reinterpreted; no route, query parameter, summary, identity or feature DTO changes.
Consumers that reject every unknown JSON field need to accept the additive field.

Public Java `ClassDto` and `SubclassDto` component types now use dedicated public table and
spellcasting carriers. Direct Java consumers must account for those new carrier types when
recompiling; this is an HTTP JSON compatibility guarantee, not Java binary compatibility.
The one existing test adaptation changes only its declared public spellcasting type.

## Scope and publication boundary

Production changes are limited to the Class/Subclass public mapper, detail carrier component
types, and four new public DTOs. Internal normalized tables, rows, cells, spellcasting,
normalization, source loading, services, controllers and reference resolution are unchanged.

Only original column indexes and copies of already published safe typed values are added.
Previously withheld text stays internal, including numeric-looking strings. No raw markup,
feature-entry table, prose, importer metadata or additional mechanical interpretation is
published. This scoped correction does not establish broader publication permission for source
content or resolve other Step 7 licensing questions.

## Regression coverage

Ten new tests in `ClassProgressionTablePositionsTests` cover:

1. Pre-change fingerprints for every Class-domain identity, complete normalized snapshot,
   feature graph, complete resolver results and prior Class/Subclass mapper JSON.
2. PHB Barbarian's exact first-row JSON and source column one.
3. All 97 affected authored DTE rows and all six roots, comparing indexes and safe values
   directly with the original typed source cells.
4. Every effective Class/Subclass table path, including nested slot/pact projections, row/table
   ordering, levels, widths, selectors, compact-cell parity and typed values.
5. Copy-effective Eldritch Knight and Arcane Trickster tables under XPHB parents.
6. Multiple leading withheld cells, intervening and trailing withheld cells, safe labels and
   a withheld-content sentinel that cannot appear in public JSON.
7. Rows without withheld cells, fully withheld rows, empty rows, different widths, multiple
   tables, omission behavior and absent optional fields.
8. Every safe kind (`NUMBER`, `BONUS`, `DICE`, `SPEED_BONUS`), negative/zero numbers, zero
   indexes, dice and explicit false rollability.
9. Defensive copies and immutable collections on all new DTO carriers, plus invalid indexes
   and presentation kinds.
10. Existing detail endpoints for all six affected owners and source-qualified by-name lookup.

The existing seven public API tests retain their assertions. The complete public mapper JSON
fingerprint for all 360 Class/Subclass definitions matches the pre-change output after removing
only `positionedCells` and canonicalizing object keys. No preexisting array order is changed.
Unordered presence sets are canonicalized only for the internal snapshot fingerprint.

## Verification results

| Verification | Result |
|---|---|
| Focused mapper and existing Class API tests | 17 passed |
| Full Maven suite | **409 tests; 0 failures, 0 errors, 0 skipped** (399 baseline + 10) |
| Stable identities | All **2,536** IDs match the pre-change SHA-256 fingerprint |
| Normalized values/provenance | Complete snapshot matches the pre-change fingerprint |
| Feature graph | All **2,725** occurrences match the pre-change fingerprint |
| Resolver | All **6,246** complete results match the pre-change fingerprint |
| Resolver outcomes | RESOLVED **2,517**; AMBIGUOUS **2,054**; UNRESOLVED **181**; DEFERRED **1,494** |
| Legacy public mapper fields | Complete pre-change JSON fingerprint matches |
| Authored audit | Deterministic rerun, **97** rows / **six** roots / **15** source files |
| Protected-file comparison | Only the three intended existing public Java files and one test declaration changed |
| Executable JAR | Package succeeds; classpath startup and HTTP verification pass |
| Whitespace | `git diff --check` and checks of new files pass |

The protected-file comparison uses the original analysis manifest without modifying it. All
source JSON, DTEs, internal normalized models, importer/normalizer code, resolver code and
original analysis evidence retain their hashes. New source files are precisely the four public
DTOs and the regression test class.

`verify-packaged.ps1` starts the executable JAR from `target`, on loopback with a temporary port,
and stops only the process it launched. It verifies all six affected detail endpoints, Barbarian's
legacy value and positioned column, nested Warlock pact tables, a copy-effective Subclass,
by-name behavior, and counts **30 / 330 / 677 / 1,499**. The runtime result is written to
`target/batch-1-packaged-verification.json`; process logs remain under `target`.

Reproduction from the repository root (using the configured Maven repository where required):

```powershell
node analysis/mechanics/batch-1-class-table-positions/audit-authored-rows.mjs
mvn test
mvn -DskipTests package
& ./analysis/mechanics/batch-1-class-table-positions/verify-packaged.ps1
git diff --check
```

## Remaining limitations

Legacy `cells` alone still cannot establish source column associations where text was withheld;
that compatibility behavior is deliberate. Use `positionedCells` for positional fidelity.
Withheld values remain unavailable publicly. Existing label sanitization remains in force.
No general table framework, progression calculation, content publication expansion, feature
graph enrichment, other Step 7 batch or Step 8 integration is included.
