# Schema Complexity and Source Findings

## Evidence scale

Complexity combines root counts, field-path diversity, nested and polymorphic forms, copy/version behavior, cross-catalog relationships, source coverage, and safe API value. Field-path counts below come from the complete recursive scan and include metadata and array element paths; they are evidence of shape breadth, not a count of Java properties needed. Counts are in `inventory-evidence.json`.

| Family | Root records | Unique recursive field paths | Copy / version observations | Complexity | Evidence and concern |
|---|---:|---:|---:|---|---|
| Adventures | 101 | 35 | 0 / 0 | Low | Publication records have `contents`, nested headers/ordinals and cover metadata. Not the adventure text itself. |
| Bastions / facilities | 70 | 67 | 0 / 0 | Medium | Facility levels, prerequisite structures, hirelings, space and cost; references may be names or markup. |
| Character creation options | 44 | 62 | 0 / 0 | Medium | `charoption` subtypes, options, prerequisites and source-specific source text require shape checks. |
| Cults and boons | 42 | 58 | 0 / 0 | Medium | Cult and boon are distinct definitions with creature-like mechanics and descriptive entries. |
| Deities | 563 | 97 | 6 / 0 | High | Pantheon/category are identity context; five repeated PHB name/source pairs prove `(name,source)` is insufficient. Rich prose and markup. |
| Encounters | 42 | 24 | 0 / 0 | Medium | Encounter tables and nested result structures; likely exact monster/item references mixed with prose. |
| Homecrafts | 20 | 116 | 0 / 0 | Medium | Actual `crochetPattern` schema is deep and measurement-rich; content is instruction prose with limited RPG API value. |
| Loot / treasure tables | 100 | 132 | 0 / 0 | High | Seven root collections have different roles: individual, hoard, magic-item, dragon, gems, art, and dragon mundane item data. Dice/table expressions and dynamic currency fields. |
| Names | 10 | 15 | 0 / 0 | Low | Name tables with gender/options and roll ranges; static lookup rather than a conventional entity catalog. |
| Objects | 37 | 89 | 0 / 0 | Medium | Implemented in Step 6. Object statistics/actions are typed; spell/item links use existing adapters; prose remains private. |
| Recipes | 316 | 69 | 0 / 0 | Medium | Many records and ingredient/instruction substructures; potential non-game or licensed prose. |
| Rewards | 278 | 90 | 0 / 0 | High | Broad reward mechanics, alternatives, prerequisites, and references to feats/items/other definitions. |
| Traps and hazards | 110 | 135 | 0 / 0 | High | Distinct `trap` and `hazard` roots; nested ratings, effects, countermeasures, tables, and prose mechanics. |
| Variant rules | 231 | 122 | 0 / 0 | High | Broad rules categories and structured rule types, mostly accompanied by descriptive rules text; avoid generic rules-engine modeling. |
| Vehicles and upgrades | 70 | 206 | 0 / 0 | High | Vehicle stat blocks, actions, movement/crew/capacity and upgrade definitions; related to but not identical with Items/Objects/Monsters. |

## Cross-corpus observations

- The scan found no `_versions`, `_abstract`, or `_implementations` declarations in any unimplemented family. Deities contain six `_copy` declarations; resolve them only after inspecting the actual operation forms and target identities.
- Other implemented source families already demonstrate that similarly named source constructs do not imply interchangeable semantics: item groups, magic variant applicability, monster templates, race versions, and class feature graphs each have dedicated resolvers.
- Nested `entries` frequently contain markup tags and rules text. Exact markup links can use the Step 5 shared resolver only after a typed owner extractor identifies a definition reference; generic scanning of strings must not turn prose mentions into grants.
- `loot.json` is a multi-schema family. It should not be forced into a generic `LootEntry` model or represented as concrete Items.
- Vehicle and monster records both resemble stat blocks, but their identities, movement/capacity models, and actions differ. Reuse narrow value objects only after inspecting exact overlap.

## Schema gate for each future implementation

Before DTE work, run the scanner and add family-specific path/type frequencies, null/optional counts, explicit empty values, tagged markup types, source distributions, duplicate identities with parent context, copy operations, and reference occurrence counts. The machine evidence is the initial reproducible denominator; it does not replace focused semantic analysis or production deserialization tests.
