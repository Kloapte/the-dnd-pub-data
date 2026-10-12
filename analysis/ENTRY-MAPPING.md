# Shared recursive entry mapping

The shared entry records model recursive prose and formatted tables used by spells and monster features. They do not model monster stat blocks or spellcasting mechanics.

## Observed entry nodes

Source traversal covered every spell's `entries` and `entriesHigherLevel`, and each monster's `trait`, `action`, `bonus`, `reaction`, `legendary`, `mythic`, and `variant` entry arrays, recursively including `entries`, `items`, and structured table-cell contents.

| Source type | Typed representation | Notes |
| --- | --- | --- |
| String | `TextEntryDte` | Preserves inline tags verbatim. |
| `entries` | `NamedEntryDte` | Recursive child entries; optional name, legacy singular `entry`, source, and page are preserved. |
| `itemSub`, `variantSub`, `variantInner` | `NamedEntryDte` | Same named recursive prose semantics; source type is retained. |
| `list` | `ListEntryDte` | Preserves style and typed items. |
| `item` | `ListItemEntryDte` | Preserves its name and either singular entry text or recursive entries. |
| `table` | `TableEntryDte` | Preserves caption, column labels/styles, typed rows, source, and page. |
| `quote` | `QuoteEntryDte` | Preserves attribution and typed content. |
| `inset` | `InsetEntryDte` | Preserves title, recursive content, source, and page. |

Spell source traversal observed 733 structured entry objects: `entries` (587), `item` (58), `list` (56), `table` (28), `quote` (3), and `inset` (1). Monster feature traversal observed 1,627: `item` (786), `entries` (458), `list` (239), `itemSub` (113), plus `variantSub` (3) and `variantInner` (3). Monster traversal also encountered 12 nested `spellcasting`-typed structures inside variant prose.

## Table cells

`EntryTableCellDte` supports plain strings (`TextTableCellDte`), `cell` objects with typed `TableRollDte` (`RollTableCellDte`), and structured `cell` or `entries` objects with name and recursive entries (`StructuredTableCellDte`). Roll exact/min/max/pad values are retained as provided; no roll interpretation is performed.

The spell data has 28 tables with 322 string cells and 29 roll cells. The monster feature traversal has 310 cells: 292 strings and 18 structured `entries` cells. Structured cells retain their nested inline-marked prose rather than flattening it.

## Deliberate domain boundary

Monster `spellcasting` objects are excluded from `EntryDte`. Their `headerEntries` and `footerEntries` are prose attached to spellcasting rules, while `will`, `daily`, `spells`, slots, ability, and related fields describe spell availability and mechanics. Those properties need a typed monster spellcasting model and are not generic entry properties. They must be mapped with the future monster parent DTE; this refactor does not deserialize them into the shared hierarchy. Full spellcasting-object coverage is therefore not claimed.

## Validation

`SpellDteTests` deserializes all 969 spells from 18 source files. `EntryDteTests` validates recursive entry content across 4,560 monsters from 111 bestiary files, reporting source file, entity, index, and entry path for any failures. This verifies shared entry structures only, not full monster records.
