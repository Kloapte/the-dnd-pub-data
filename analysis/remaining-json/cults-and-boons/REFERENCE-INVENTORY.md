# Cults and Boons reference inventory

The inventory is reproducible with `node analysis/remaining-json/cults-and-boons/discover-cults-boons.mjs`. Counts are source occurrences, not unique target definitions.

## Structured source relationships

| Field | Occurrences | Meaning | Treatment |
|---|---:|---|---|
| `cult[].otherSources[]` | 4 | Secondary source/page references, all `MPMM` | Preserved and validated with Source Registry; not definition edges |
| `cult[].reprintedAs[]` | 2 | One plain UID and one `{tag, uid}` record | Preserved as authored name/source/tag; no target ID |
| `boon[].reprintedAs[]` | 8 | Tagged `{tag, uid}` records | Preserved as authored name/source/tag; no target ID |

All ten reprint UIDs point at source `MPMM`. The untagged Cult Geryon UID has an exact Cult name/source match. The other nine records carry the authored `boon` tag, while the same name/source keys occur in the MPMM `cult` collection and no such targets occur in the `boon` collection. This source/category mismatch is retained without turning similar names into Cult-to-Boon relationships. Cult/Boon are not resolver target kinds, so no target IDs are fabricated.

## Explicit markup references

Only explicit 5eTools tags are parsed as candidate links. Plain prose is never scanned for names.

| Markup | Occurrences | Authored identity shape | Resolver handling |
|---|---:|---|---|
| `{@spell ...}` | 65 | All unqualified names | 65 AMBIGUOUS results; no edition/source is inferred and no link is treated as a grant |
| `{@item ...}` | 5 | All `Name|PotA` | 5 RESOLVED exact ITEM IDs |
| `{@skill ...}` | 4 | All unqualified names | 4 AMBIGUOUS results; no source is inferred |
| `{@filter ...}` | 1 | Item selector with `source=phb` among its filter parameters | 1 DEFERRED ITEM filter; candidates are not expanded |

These 75 occurrences are added to the shared startup snapshot separately from its pre-existing 3,875 occurrences. New outcomes are RESOLVED 5, AMBIGUOUS 69, UNRESOLVED 0, DEFERRED 1. The historical subset remains RESOLVED 1,697, AMBIGUOUS 1,108, UNRESOLVED 30, DEFERRED 1,040. The combined snapshot has 3,950 occurrences and outcomes RESOLVED 1,702, AMBIGUOUS 1,177, UNRESOLVED 30, DEFERRED 1,041.

Inline source values encountered are `PotA` (five item links), `scag` (one deity link), `vgm` (one creature link), and `phb` (one item-filter source parameter); all validate against the existing registry (case-insensitive source-code/alias lookup). Their authored spelling is retained. These inline identities are preserved as markup in internal entries except for the supported Spell, Item, Skill, and Item-filter references represented in the shared resolver.

## Preserved but unsupported markup targets

| Markup | Occurrences | Reason excluded from shared resolution |
|---|---:|---|
| `{@creature ...}` | 95 | MONSTER target kind is not in the shared resolver |
| `{@deity ...}` | 1 | DEITY target kind is not in the shared resolver |
| `{@condition ...}` | 21 | Condition tags are presentation references, and no CONDITION target kind exists |

Other observed tags (`dc`, `damage`, `recharge`, `dice`, `quickref`, `atk`, `i`) are structured inline presentation/mechanics markers inside descriptive entries. They are retained internally and are not interpreted as standalone grants or effects. Cult and Boon roots contain no explicit typed prerequisite, grant, choice, or filter fields beyond the one markup filter above.
