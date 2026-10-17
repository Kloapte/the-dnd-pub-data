# Treasure and Loot Table Domain Design

## Source checkpoint

The pinned input is `src/main/resources/data/loot.json` (SHA-256
`fb2df75593f6d04dfe795502042ac94f060f63a331156bedce1a26b9d512aea4`). It contains seven arrays
and 100 array records. Six arrays contain 75 named, source-attributed definitions; the
`dragonMundaneItems` array contains 25 ordered result rows with no name, source, or page. See
`source-audit.json` and `FIELD-COVERAGE.md` for the complete 124-path audit.

## Boundaries

The API is one cohesive Treasure Tables catalog because every named root definition describes a
source-authored table or table group used to describe treasure. The source collection is retained
as a discriminator; it is not flattened into one record with optional fields for every table shape.

The normalized catalog has six sealed definition variants:

- `IndividualTreasureDefinition`: challenge range and ordered currency rows.
- `TreasureHoardDefinition`: challenge range, currency expressions, and ordered category/count rows.
- `DragonTreasureDefinition`: age category, currency expressions, and separately nested gem, art,
  and magic-item type tables. The named Dragon definitions refer to the shared
  `dragonMundaneItems` row list by its source field.
- `GemTableDefinition` and `ArtObjectTableDefinition`: denomination and ordered authored result
  strings, retained privately.
- `MagicItemTableDefinition`: table code, ordered roll rows, explicit item UIDs, choices, spell
  level metadata, and nested result rows.

`dragonMundaneItems` is imported as one immutable supporting table containing 25 ordered typed
rows. Its records are not independent table definitions and receive no fabricated names or row
IDs. The table's existence is represented by the source collection and its use is represented by
each Dragon definition's `dragonMundaneItems.amount` field. Its source context is FTD because all
four authored Dragon definitions that use it are explicitly sourced to FTD; the row records
themselves contain no source field.

These tables are not Items, Item Groups, Magic Variants, Rewards, or inventories. Table rows and
filters remain table data. No rolling, result selection, item generation, reward assignment, or
inventory mutation is implemented.

## Identity checkpoint

Named definitions use
`treasure-table:<exact-source>:<collection-type>:<normalized-name-slug>`. The type segment is
required: `individual` and `hoard` intentionally both use names such as `Challenge 0-4`; gems and
art tables also use denomination-oriented names. The complete `(collection, source, name)` and
generated-ID sets contain no collisions. DMG and XDMG versions remain separate. Eleven authored
DMG gem/art reprint UIDs pointing to XDMG are retained as provenance and do not merge definitions.

The supporting Dragon mundane-item rows are not independently addressable, so no unstable
array-index-based public IDs are created.

## Source structures and mechanics

`individual` (8) and `hoard` (8) are named CR-banded definitions with numeric row ranges. Currency
denominations are fixed authored keys `cp`, `sp`, `ep`, `gp`, and `pp`; their values are strings
such as dice expressions and remain strings. Hoard rows have structured gem/art amounts and lists
of magic-item type/amount selections.

`dragon` (4) has no generic `table` field. It contains currency expressions, gem and art type
tables, magic-item type tables with authored `typeAltChoose` rarity/tier context, and a mundane-item
amount. `gems` (12) and `artObjects` (10) are value-denominated result lists whose rows are strings
with explicit item tags and descriptive parenthetical text. `magicItems` (33) has ordered numeric
roll ranges, item result tokens, optional spell levels, typed choice alternatives, and two nested
subtables. Six nested magic-item rows omit `max`; omission is preserved, not repaired to a guessed
singleton or open-ended range. `dragonMundaneItems` rows have min/max plus protected result text.

All observed range bounds are numeric when present. No source-authored dice field describes a
table's roll expression; the implementation does not infer `d100` or other dice from ranges.
Currency and quantity dice formulas are retained as authored expressions. Row order, repeated
rows, absent bounds, and source-authored category variants are retained exactly.

## References and publication boundary

Explicit `{@item ...}` markup is present in gem, art-object, and magic-item result values. These
are item-result references, not item grants. `magicItems.table[].choose.fromItems` adds explicit
item choice candidates, including source-qualified values. `fromGeneric` and `fromGroup` are
authored selector categories, not UIDs in a currently supported target catalog. The
`fromMatching.rarity` property is an item filter; no candidates are expanded. Reprint UIDs are
provenance, not table-result links.

Internal normalized definitions retain complete typed source rows and text. Public DTOs expose
stable identity, source/page, collection kind, authored CR/denomination/type metadata, counts, and
ordered roll-range structure without result text. Supported exact-reference projections are
lightweight and do not turn candidates into grants. Gem, art-object, magic-item result text,
Dragon mundane-item descriptions, filter expressions, media, and markup remain private pending
publication review. Structured numeric information is treated separately from descriptive output;
the public range projection contains only authored bounds and counts.
