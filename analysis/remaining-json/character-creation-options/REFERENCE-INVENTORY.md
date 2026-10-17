# Character Creation Options reference inventory

The reproducible scan reports source-authored structured references separately from prose tags. Counts below are corpus occurrences, not distinct target definitions.

| Field/category | Occurrences | Authored target | Classification | Resolution handling |
|---|---:|---|---|---|
| `prerequisite[].race[].name` | 6 | Unqualified race names (`human`, `half-elf`, `half-orc`) | Prerequisite; not a grant | Preserved in prerequisite groups; `RACE` target kind is unsupported; no target IDs |
| `reprintedAs[]` | 2 | `Mist Wanderer|RHW`, `Spirit Medium|RHW`, tag `background` | Explicit source-qualified reprint relationship | Preserved with authored name, source, tag, and UID; `BACKGROUND` target kind is unsupported; no target IDs |
| `otherSources[]` | 2 | `ToD`, page 176 | Secondary publication/source relationship | Preserved and Source Registry validated; not a definition reference |

The combined scanner reference list contains 10 structured occurrences (6 race prerequisite names, 2 reprints, and 2 source-provenance references). Of these, **8 are definition relationships** and remain deferred/unbound because their target kinds are not supported by the existing resolver. `otherSources` are registry source metadata and are not counted as target-catalog resolution attempts. No supported target-kind references are present, so this catalog contributes zero occurrences to the immutable resolver snapshot.

Inline tags inside descriptive entries are not resolver input. The scanner counted: `book` 3, `condition` 21, `creature` 1, `damage` 6, `dc` 5, `deck` 1, `dice` 11, `i` 7, `sense` 1, `skill` 21, `spell` 20, and `status` 1. The source-bearing `book`/`deck` tags name `MOT` (2), `VRGR` (1), and `CoS` (1), all registered. Spell and skill tags are not treated as grants, and prose is not searched for additional relationships.

No references to Items, Feats, Optional Features, Skills, or Languages occur in structurally typed fields. No resolver adapters, owner extractors, or public `crossCatalogReferences` projection were added. This preserves the established 3,875 resolver occurrences and their RESOLVED 1,697 / AMBIGUOUS 1,108 / UNRESOLVED 30 / DEFERRED 1,040 outcome counts.
