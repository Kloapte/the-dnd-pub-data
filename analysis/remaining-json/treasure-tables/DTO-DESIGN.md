# Treasure Tables DTO Design Checkpoint

This checkpoint precedes production models. It is based on the full seven-collection audit in
`source-audit.json` and the domain boundary decision in `DOMAIN-DESIGN.md`.

## Internal normalized types

Use a sealed `TreasureTableDefinition` interface with six immutable variants, one for each named
definition shape: Individual, Hoard, Dragon, Gems, Art Objects, and Magic Items. Each variant has
only its meaningful fields, plus a shared `TreasureTableId`, exact `SourceReference`, authored
name, collection discriminator, and typed source provenance. Use separate typed source DTEs for
all seven arrays. The Dragon mundane-item collection is one shared immutable list of typed rows,
not a record with 25 fabricated definitions.

Roll rows preserve numeric `min` and optional numeric `max` exactly. Nested magic-item rows are a
distinct DTE from parent rows because their shape and semantics differ. Currency amounts and
quantities are authored expression strings; no dice parser runs during import. `choose` remains a
typed optional item-choice structure with separate `fromItems`, `fromGeneric`, `fromGroup`, and
`fromMatching.rarity` forms. Exact nested choices are not flattened into automatic grants.

## Public projection

`TreasureTableSummaryDto` exposes:

- Stable ID, name, source identity, page, and collection kind.
- Authored CR bounds or denomination/table code only for variants that supply them.
- Table and row counts.

`TreasureTableDetailDto` adds only safe authored structure:

- Ordered table metadata groups with a semantic role, row count, and ordered min/max bounds.
- Nested range groups where the source has nested tables.
- Lightweight supported explicit item references, including their source field locator and choice
  role.

No result labels, item descriptions, gem/art strings, Dragon mundane-item text, raw markup,
filters, media, importer metadata, or resolver diagnostics are serialized. Detail DTOs do not
contain a generic optional-field bag or a complete source row. `NON_EMPTY` inclusion omits missing
bounds and empty collections while preserving zero bounds.

## Identity and lookup

ID form is `treasure-table:<source>:<collection-type>:<normalized-name-slug>`. The collection
segment prevents collisions between the intentionally repeated CR-band names in `individual` and
`hoard`. Name lookup is exact normalized name plus optional source and collection type and returns
all matches; it never arbitrarily chooses a table. No public identity is issued to supporting
Dragon mundane-item rows.

## API

- `GET /api/treasure-tables?source=&type=&category=` returns deterministic summaries.
- `GET /api/treasure-tables/{id}` returns safe detail metadata.
- `GET /api/treasure-tables/by-name/{name}?source=&type=` returns all matching summaries.

There are 75 named top-level table definitions plus one shared supporting table that is not
exposed as a definition. The 75-definition API is a bounded static catalog and does not need
pagination. It provides no rolling, generation, award, inventory, or filter-evaluation operations.

## Publication decision

Public range bounds, CR bands, denomination values, table codes, and aggregate row counts are
exposed as structured metadata. Authored result labels and prose remain internal. Cross-catalog
item reference tokens are published only through the existing lightweight reference projection;
only authoritative exact matches receive `targetId`. This is a deliberate narrow relationship
projection and does not publish complete row contents.
