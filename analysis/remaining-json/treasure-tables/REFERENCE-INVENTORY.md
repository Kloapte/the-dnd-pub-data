# Treasure Tables Reference Inventory

## Explicit Item relationships

The 1,127 authored `{@item ...}` tokens occur only in result values in `gems` (104), `artObjects`
(98), and `magicItems` (925, including 14 nested magic-item results). They are Item references,
not Item grants. Owner IDs use the table's stable ID. Locators preserve source structure, including
`table[index].markup[index]`, `table[index].item`, and
`table[index].table[index].item` for nested results.

Some markup carries more than the simple name/source shape understood by the current UID parser.
Those 150 occurrences retain their original argument token as `UNSUPPORTED` semantic forms; they
receive no target ID. The other 977 are recognized definition-form Item references. Unqualified
names do not acquire an inferred source identity.

`magicItems.table[].choose.fromItems[]` contributes 190 authored Item candidates. These are
represented as ITEM/CHOICE relationships, not grants. Even source-qualified candidates remain
deferred because they are conditional alternatives; the public projection does not expose target
IDs for them. `choose.fromMatching.rarity` contributes 15 ITEM/FILTER occurrences, all deferred;
the filter is retained without expanding candidate Items.

`choose.fromGeneric` has 103 string values across 100 choice objects, and `choose.fromGroup` has
six values. They identify generic selectors or Item groups rather than concrete definitions in a
currently supported target catalog. They remain typed in the source and normalized data; no
resolver target kind or target IDs are introduced for them. `reprintedAs` UIDs are provenance, not
table result relationships. No Spell, Feat, Optional Feature, Skill, or Language references occur.

## New resolver occurrences

All new occurrences target ITEM. Counts below include explicit item markup, choices, filters, and
extended-context item markup; they are separate from the historical 4,914 outcomes.

| Collection | Total | RESOLVED | AMBIGUOUS | UNRESOLVED | DEFERRED |
|---|---:|---:|---:|---:|---:|
| `gems` | 104 | 52 | 52 | 0 | 0 |
| `artObjects` | 98 | 50 | 32 | 0 | 16 |
| `magicItems` | 1,130 | 376 | 269 | 108 | 377 |
| **New total** | **1,332** | **478** | **353** | **108** | **393** |

The new total is 1,127 markup occurrences, 190 choices, and 15 filters. Outcomes are produced by
the existing immutable startup resolver and item adapters. Only an authoritative exact
source-qualified definition match can project `targetId`. Existing 4,914 occurrences retain
RESOLVED 2,039, AMBIGUOUS 1,701, UNRESOLVED 73, and DEFERRED 1,101.

## No inferred relationships

Currency expressions, category quantities, rarity selectors, collection names, and descriptive
result prose are not references. The implementation does not materialize `fromGeneric` or
`fromGroup`, expand rarity filters, infer Item ownership, or turn a selected treasure result into
an awarded Item.
