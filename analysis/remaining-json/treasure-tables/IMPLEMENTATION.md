# Treasure and Loot Tables Implementation

## Source and counts

The pinned input is `src/main/resources/data/loot.json`, SHA-256
`fb2df75593f6d04dfe795502042ac94f060f63a331156bedce1a26b9d512aea4`. It contains seven root
arrays and 100 source records. There are 75 named, source-attributed definitions; the 25
`dragonMundaneItems` rows are one shared supporting result table, not standalone definitions.

| Root collection | Source records / definitions | Primary source distribution | Authored role |
|---|---:|---|---|
| `individual` | 8 | DMG 4, XDMG 4 | CR-banded currency rows |
| `hoard` | 8 | DMG 4, XDMG 4 | CR-banded currency and treasure-category rows |
| `dragon` | 4 | FTD 4 | Age-group treasure structure |
| `gems` | 12 | DMG 6, XDMG 6 | Gem result lists by GP denomination |
| `artObjects` | 10 | DMG 5, XDMG 5 | Art-object result lists by GP denomination |
| `magicItems` | 33 | DMG 9, XDMG 24 | Typed magic-item result and choice tables |
| `dragonMundaneItems` | 25 rows | No source field; shared by the FTD Dragon definitions | Shared ordered result rows |

Primary-source distribution across the 75 definitions is DMG 28, XDMG 43, and FTD 4. All source
identities and all reprint-target sources validate against the Source Registry; none were added.
See `FIELD-COVERAGE.md` for all 131 root and nested field paths, JSON types, and field occurrence
counts.

## Domain and identity

One cohesive `/api/treasure-tables` API exposes six sealed definition types. The source collection
remains an explicit discriminator. Individual and hoard tables are separate types despite their
shared CR-band names. Gem and Art Object table shapes are similar but remain distinct. Dragon
mundane-item rows are retained once as supporting data and are referenced by Dragon tables through
their authored `dragonMundaneItems.amount` field.

IDs use `treasure-table:<exact-source>:<collection>:<normalized-name-slug>`. There are no duplicate
IDs within a collection. Eight `(source,name)` pairs repeat between `individual` and `hoard` for
the four CR bands in both DMG and XDMG. The collection segment distinguishes these records. Eleven
DMG Gem/Art Object definitions contain authored XDMG `reprintedAs` UIDs; the catalog validates
each exact same-collection target and retains the original lineage without merging the definitions.

## Typed source and normalized data

Strict Jackson records cover all seven roots, currency denominations, CR bands, row ranges,
category quantities, Dragon type tables, Dragon rarity/tier alternatives, magic-item rows,
nested magic-item results, choices, filters, reprint UIDs, and supporting Dragon mundane-item
rows. Currency denominations are typed `cp`, `sp`, `ep`, `gp`, and `pp` string fields; dice and
quantity expressions remain authored strings. `choose` has four explicit variants: `fromItems`,
`fromGeneric`, `fromGroup`, and `fromMatching.rarity`. Unknown choice keys, multiple variants,
unknown record properties, and unknown nested properties fail deserialization.

Immutable normalized definitions retain the complete typed root source record. This keeps result
strings, markup, every amount expression, row ordering, nested data, and reprint lineage available
internally without making the source DTE the public API model. All seven root arrays deserialize
strictly; exact source-to-DTE JSON tree round-trip and the 131-path inventory are tested.

## Table semantics preserved

The source contains 86 physical table structures and 1,354 ordered rows: 21 individual currency
rows, 108 hoard rows, 60 Dragon category-type rows, 25 shared Dragon mundane-item rows, 104 Gem
rows, 98 Art Object rows, and 938 Magic Item rows including 14 nested results. Numeric ranges are
preserved in authored order, with optional min/max fields left optional. Six nested magic-item
rows omit `max`. All checked numeric-range row groups are ordered with no observed overlaps or
gaps where both adjacent bounds are present. No range is repaired or sorted by computed values.

The source has no authored dice-expression field for table rolling; no `d100` expression is
inferred from 1–100 ranges. Dice formulas on coin amounts and category quantities remain strings.
There are no authored terminal-`00` ranges, media references, `_copy`, `_versions`, general
`otherSources`, or descriptive `entries` fields. Gem, Art Object, Magic Item, and Dragon mundane
result strings include authored names/markup or prose and remain internal.

## Public DTOs and REST API

Summary DTOs expose ID, name, source, page, collection type, authored category, table count, and
row count. Detail DTOs add CR bounds or denomination/table code where present and ordered range
metadata, including nested tables. Gem and Art Object detail responses expose result counts rather
than result labels. Dragon details expose the shared mundane row count and numeric ranges without
its authored results. Item relationships are projected as lightweight references through the
existing resolver.

Public JSON does not contain source row result text, gem/art descriptions, Dragon mundane-item
descriptions, raw markup, currency/dice expressions, item-filter strings, media paths, resolver
diagnostics, or importer metadata. The DTO boundary exposes only numeric ranges and safe table
metadata pending separate publication review of authored results.

Read-only endpoints:

- `GET /api/treasure-tables?source=&type=&category=`
- `GET /api/treasure-tables/{id}`
- `GET /api/treasure-tables/by-name/{name}?source=&type=`

The API returns at most 75 summaries and does not require pagination. Duplicate name lookup returns
all matches; source and collection filters can distinguish the intentionally repeated Challenge
bands. No rolling, item selection, generation, award, or inventory behavior is implemented.

## References and publication boundary

The seven root collections add 1,332 ITEM occurrences: 1,127 explicit `@item` tags, 190
`fromItems` candidates, and 15 rarity filters. Per-collection outcome counts and unsupported
selector decisions are documented in `REFERENCE-INVENTORY.md`. The historical 4,914 resolver
outcomes remain 2,039 RESOLVED, 1,701 AMBIGUOUS, 73 UNRESOLVED, and 1,101 DEFERRED.

Source structure and numeric mechanics are not presumed redistributable by default. Public APIs
withhold all authored result text and publish only identifiers, source metadata, counts, authored
category metadata, and numeric range structure. Full results remain internally available for a
future licensing decision; no Step 7 interpretation or prose enrichment is included.

## Verification

- Focused Treasure Tables tests: 9 passed.
- Full Maven suite: 389 tests passed, 0 failures, 0 errors (380 baseline + 9 new tests).
- Packaged JAR build passed; catalog loading from packaged classpath is covered by the tests.
- The historical resolver snapshot remains 4,914 occurrences: 2,039 RESOLVED, 1,701 AMBIGUOUS,
  73 UNRESOLVED, and 1,101 DEFERRED. Treasure adds 1,332 independently counted ITEM occurrences.
- `git diff --check` passed; Git reported line-ending conversion warnings for pre-existing and
  modified LF files on this Windows checkout.
- Tests cover all seven arrays, exact counts, source distributions, 131-path source/DTE
  round-trip, strict unknown-property rejection, choice variants, reprints, IDs, row counts and
  order, missing range maxima, source lookup, immutable data, public JSON, REST behavior, reference
  outcomes, baseline resolver stability, and JAR classpath loading.
