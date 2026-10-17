# Trap and Hazard Reference Inventory

## Authored structured references

Neither `trap` nor `hazard` has a structured reference property. The only explicit source metadata relationships are `hazard[].otherSources` (2 source/page records) and `reprintedAs` strings (8 trap, 15 hazard values). These are provenance/reprint records, not mechanical target definitions. All referenced source codes were checked against the Source Registry.

## Tagged text

The nested `entries` trees contain tagged markup: `spell` 39, `item` 20, `skill` 86, `condition` 113, `creature` 7, `hazard` 2, `action` 16, `damage` 130, `dc` 186, `dice` 47, `variantrule` 44, `book` 18, `quickref` 7, `disease` 1, `status` 2, `sense` 3, `adventure` 1, and presentation tags `atk` 2, `hit` 4, `b` 7, `i` 1, `table` 3, `filter` 1. A tag occurrence can contain presentation data or an ordinary narrative reference; it is not automatically a mechanical relationship.

The extractor submits supported `spell`, `item`, `skill`, and `filter` markup to the existing resolver with catalog owner IDs and exact nested field locators. The source inventory contains 39 spell, 20 item, 86 skill, and one filter tag occurrence (146 total); filter remains a filter semantic, not a grant. Trap outcomes are 35 resolved, 57 ambiguous, 0 unresolved, and 3 deferred (95 total). Hazard outcomes are 25 resolved, 24 ambiguous, 0 unresolved, and 2 deferred (51 total). Only exact authoritative outcomes expose `targetId`; an unqualified token does not gain an inferred source. This preserves the pre-existing 4,370 resolver occurrences and their outcomes. All source entry text and markup remain available internally in typed `EntryDte` trees.

| Catalog | Resolver target kind | Resolved | Ambiguous | Unresolved | Deferred | Total |
|---|---|---:|---:|---:|---:|---:|
| Trap | Item (includes one filter) | 13 | 3 | 0 | 3 | 19 |
| Trap | Skill | 16 | 33 | 0 | 0 | 49 |
| Trap | Spell | 6 | 21 | 0 | 0 | 27 |
| Hazard | Item | 0 | 0 | 0 | 2 | 2 |
| Hazard | Skill | 15 | 22 | 0 | 0 | 37 |
| Hazard | Spell | 10 | 2 | 0 | 0 | 12 |

Creature tags/names are not turned into Monster IDs; condition mentions are not effects; and hazard tags are not treated as self-references. These target kinds have no adapter in the existing resolver.

## Unsupported or deferred targets

Markup target kinds for Creature/Monster, Condition, Action, Damage Type, Disease, Status, Sense, Adventure, Variant Rule, Hazard, and other non-adapted tags have no supported resolver target kind in this catalog. The `filter` tag is presentation/filter context, not a grant. Even markup using a generally supported spell/item/skill tag is contained in prose and is intentionally not promoted into a catalog relationship. A later rules/reference pass can review those fields with explicit semantics and publication approval.
