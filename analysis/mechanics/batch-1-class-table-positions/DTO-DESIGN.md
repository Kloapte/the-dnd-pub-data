# Batch 1 DTO and Compatibility Checkpoint

## Confirmed source defect

The independent `audit-authored-rows.mjs` scan reproduces 97 authored rows across six roots:
Artificer EFA 1; Barbarian PHB 20; Barbarian XPHB 20; Psi Warrior XPHB 18;
Soulknife XPHB 18; Warlock PHB 20. `authored-rows.json` records every row locator and original
retained column index, without copying withheld content. Original Step 7 evidence is unchanged.

Internal `ClassProgressionTable` and `ClassTableRow` preserve all source cells in order.
`ClassPublicMapper.safeTables()` filters text cells but supplies no original column index. The
PHB Barbarian first row loses its Rages string at column zero and retains the Rage Damage BONUS
value 2 as the first compact public cell. Compact list position cannot identify the authored
column.

## Chosen contract, recorded before implementation

Use an additive public projection:

- `ClassProgressionTableDto` preserves existing table property names and metadata, with public rows.
- `ClassProgressionTableRowDto` preserves `level` and the existing compact `cells` array exactly,
  and adds `positionedCells`.
- `ClassPositionedTableCellDto` contains zero-based `columnIndex`, existing safe `kind`, optional
  `number`, `dice` and `rollable`. It has no text field.
- `ClassSpellcastingDto` preserves existing spellcasting property names/values while using public
  progression table types for `slotTables` and `pactTables`. This avoids altering the shared
  internal `ClassSpellcasting` type or leaving nested progression tables without positions.

Only the three public table carriers and public spellcasting carrier are new; existing normalized
table, row, cell and spellcasting types and normalization algorithms stay intact. Public Class and
Subclass detail records change their Java component types to these projection records. REST JSON
changes only by adding `positionedCells` where a row has at least one retained safe value.

Enumerate the original unfiltered cells. For each safe cell, append the unchanged legacy value to
`cells` and append the positioned projection with the original enumeration index. Never enumerate
the filtered array to determine source positions. Preserve table/row/cell order and all existing
safe labels/metadata. Fully withheld and empty rows keep their level and omit both empty arrays.

All public records use `NON_EMPTY` and defensive collection copies. Index zero, numeric zero and
Boolean false remain present. No placeholder, withheld source string or inferred value is added.

## Compatibility and publication

Before:

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

Existing consumers retain the historical compact values; `cells` is not silently redefined as a
position-aligned array. Clients requiring column associations must use `positionedCells` and the
table's existing `columns`. No endpoint or identifier changes. Java clients recompiling against
the public DTO types need the new carrier types; the HTTP contract remains additive.

Only source indexes and values already approved by the current safe projection are added. The
withheld Rages string is not parsed or published. Feature-entry tables, source markup, importer
metadata, mechanics evaluation and other Step 7 batches remain outside scope.

## Verification plan

Capture pre-change canonical fingerprints for all 2,536 class-domain IDs, normalized class-domain
data, the feature graph, complete resolver results and legacy Class/Subclass public JSON. After
implementation, assert equality after removing only the new positioned-cell property from public
JSON. Test all authored/effective affected rows independently, every projected progression table
path, synthetic edge cases, public omission/immutability, existing endpoints and packaged loading.
