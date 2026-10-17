# Rewards Reference Inventory

## Explicit supported references

The importer records 291 authored occurrences, preserving reward owner IDs, source field locators, original tokens, parsed source qualification, and semantic form. Breakdown by target kind and outcome:

| Target kind | Occurrences | RESOLVED | AMBIGUOUS | UNRESOLVED | DEFERRED |
|---|---:|---:|---:|---:|---:|
| SPELL | 257 | 56 | 201 | 0 | 0 |
| ITEM | 24 | 5 | 11 | 5 | 3 |
| SKILL | 10 | 0 | 10 | 0 | 0 |
| **Total** | **291** | **61** | **222** | **5** | **3** |

Spell occurrences comprise 113 explicit spell UID values under `additionalSpells` and 144 supported spell markup links in entries. Item occurrences comprise 23 `{@item ...}` links and one `{@filter ...}` selector. There are ten supported skill markup links. Reward spell grant context remains encoded in source field locators and typed structured spell data; choices are not converted into unconditional grants. Only resolver results that meet the resolver's authoritative exact-match rules expose a public `targetId`.

The resolver's prior 4,079 occurrences remain unchanged at RESOLVED 1,796, AMBIGUOUS 1,182, UNRESOLVED 39, and DEFERRED 1,062. The Rewards additions bring the complete snapshot to 4,370 occurrences: 1,857 RESOLVED, 1,404 AMBIGUOUS, 44 UNRESOLVED, and 1,065 DEFERRED.

## Preserved without a target adapter

There are 30 `seeAlsoFacility` UID strings on 29 definitions. Facility definitions do not have a shared resolver target kind, so these remain internal and receive no target IDs. Creature, condition, action, language, sense, book, adventure, variant-rule, and other unsupported inline tags remain in internal entries; they are not converted into links or inferred grants. Monster-shaped inline statblocks remain typed reward entry content. Reprint UIDs and `otherSources` are lineage/provenance rather than reward grants and are not resolved as cross-catalog links.

No creature name found in prose or inline statblock content is promoted to a Monster reference. No relationship is inferred from a matching name.

## Other observed markup

The corpus tag histogram includes action 13, adventure 4, book 27, condition 50, creature 17, damage 9, dc 16, dice 11, filter 1, i 61, language 5, note 11, quickref 2, sense 9, status 1, table 2, and variantrule 13 in addition to supported spell/item/skill tags. These tags are preserved in internal entries; they do not produce resolver occurrences without a supported typed adapter and explicit domain semantics. `{@filter}` is retained as a filter occurrence, not expanded into candidate definitions.
