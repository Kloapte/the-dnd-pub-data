# Source Coverage Matrix

Every file below is discovered recursively under `src/main/resources/data/`. Per-file record counts, collection keys, source lists, source/name duplicate candidates, SHA-256 hashes, recursive field paths, and type observations are in [per-file-classification.json](per-file-classification.json). This matrix groups the complete 175-file set without collapsing auxiliary files into definition files.

| Source family / path | Files | Root collections and record counts | Classification / current coverage |
|---|---:|---|---|
| `actions.json` | 1 | `action` 48 | Implemented Action catalog |
| `adventures.json` | 1 | `adventure` 101 | Implemented Adventure Index; contents/header metadata retained internally, no chapter bodies |
| `backgrounds.json` | 1 | `background` 171 | Implemented Background catalog |
| `bastions.json` | 1 | `facility` 70 | [Implemented Bastion Facility catalog](bastion-facilities/IMPLEMENTATION.md); authored facility definitions only, not the Bastion rules engine |
| `books.json` | 1 | `book` 66 | Implemented publication index/catalog; not book body content |
| `charcreationoptions.json` | 1 | `charoption` 44 | Implemented Character Creation Options catalog; descriptive entries retained internally |
| `classes/class-*.json` | 15 | `class` 30; `subclass` 330; `classFeature` 677; `subclassFeature` 1,499; `_meta` appears in 13 files | Implemented; includes sidekick and Mystic source records and metadata |
| `conditionsdiseases.json` | 1 | `condition` 30; `disease` 29; `status` 5 | Implemented all three record kinds |
| `cultsboons.json` | 1 | `cult` 30; `boon` 12 | Implemented as independent Cult and Boon catalogs; prose remains internal |
| `deities.json` | 1 | `deity` 563 | [Implemented Deities catalog](deities/IMPLEMENTATION.md); pantheon is part of stable identity |
| `encounters.json` | 1 | `encounter` 42 | [Implemented Encounters catalog](encounters/IMPLEMENTATION.md); table outcomes remain internal pending publication review |
| `feats.json` | 1 | `feat` 305 | Implemented Feat catalog |
| `homecrafts.json` | 1 | `crochetPattern` 20 | OUT OF SCOPE by product decision; not a technical implementation gap |
| `items.json` | 1 | `item` 2,512; `itemGroup` 119 | Implemented; groups are selection support, not concrete items |
| `items-base.json` | 1 | `baseitem` 230; `itemEntry` 13; `itemMastery` 8; `itemProperty` 27; `itemType` 67; `itemTypeAdditionalEntries` 2 | Implemented item and auxiliary type/property structures |
| `languages.json` | 1 | `language` 201; `languageScript` 6 | Implemented |
| `loot.json` | 1 | `artObjects` 10; `dragon` 4; `dragonMundaneItems` 25; `gems` 12; `hoard` 8; `individual` 8; `magicItems` 33 | [Implemented Treasure Tables](treasure-tables/IMPLEMENTATION.md); six named table variants plus shared Dragon row data |
| `magicvariants.json` | 1 | `magicvariant` 230; `linkedLootTables` is a dynamic object | Implemented variant catalog; linked loot tables are auxiliary |
| `monsters/bestiary-*.json` | 111 | `monster` 4,560 across bestiaries; `_meta` occurs in 83 files | Implemented monster roots; broad source corpus and copy/version processing |
| `monsters/legendarygroups.json` | 1 | `legendaryGroup` 188 | Batch 7 internal support catalog: all bodies/copies/versions preserved; 209 expanded support groups, outside Monster definition counts |
| `monsters/template.json` | 1 | `monsterTemplate` 66; `legendaryGroupTemplate` 1 | Existing Monster templates plus Batch 7 dedicated legendary-group template preservation/application; omitted from prior 173-file coverage manifest |
| `names.json` | 1 | `name` 10 | Implemented internal Name Tables catalog; metadata-only public projection pending publication review |
| `objects.json` | 1 | `object` 37 | Implemented Object catalog; source identity is `(source, name)` |
| `optionalfeatures.json` | 1 | `optionalfeature` 221 | Implemented |
| `races.json` | 1 | `race` 160; `subrace` 98 | Implemented; copy/version behavior included |
| `recipes.json` | 1 | `recipe` 316 | [Implemented metadata-only Recipes catalog](recipes/IMPLEMENTATION.md); complete recipe content remains internal |
| `rewards.json` | 1 | `reward` 278 | [Implemented Rewards catalog](rewards/IMPLEMENTATION.md); authored rewards and structured spell benefits only |
| `senses.json` | 1 | `sense` 8 | Implemented |
| `skills.json` | 1 | `skill` 36 | Implemented |
| `sources/registry.json` | 1 | `sources` 181 | Implemented shared registry; PiP and HFDoMM added as identity-only source codes |
| `spells/spells-*.json` | 18 | `spell` 969 | Implemented; PHB/XPHB are independent definitions |
| `trapshazards.json` | 1 | `trap` 37; `hazard` 73 | [Implemented as separate Trap and Hazard catalogs](traps-and-hazards/IMPLEMENTATION.md); descriptive entries remain internal |
| `variantrules.json` | 1 | `variantrule` 231 | [Implemented Variant Rules catalog](variant-rules/IMPLEMENTATION.md); nested rules and tables remain internal |
| `vehicles.json` | 1 | `vehicle` 39; `vehicleUpgrade` 31 | [Implemented separate Vehicle and Vehicle Upgrade catalogs](vehicles-and-upgrades/IMPLEMENTATION.md) |

## Completeness classifications

There are 30 implemented primary source families and one supporting source-registry family. Homecraft is explicitly excluded by product decision (20 crochet-pattern records), leaving zero in-scope Step 6 source families pending. Recipes have a metadata-only catalog; complete instructions and ingredient content remain internal. Deities use source and pantheon context in stable identities; copy-derived VGM entries retain separate IDs and source-defined inherited metadata. Bastion Facilities are imported from the `facility` collection only; this does not implement Bastion management or rules execution. Encounters import authored table definitions; they do not implement generation or encounter execution. `cultsboons.json` produces two independent catalog kinds from the `cult` and `boon` collections. `trapshazards.json` produces separate Trap and Hazard catalogs; it does not implement environmental execution. `vehicles.json` produces separate Vehicle and Vehicle Upgrade catalogs from the two roots; it does not model ownership or install upgrades. `loot.json` produces six named typed table definitions and retains a seventh root collection as shared Dragon result rows; it does not roll, award, or generate treasure. Variant Rules preserve their authored nested sections and tables internally without interpreting or enabling them. A source file can contain both public definitions and auxiliary collections: item groups, item properties/types/masteries, linked loot tables, monster legendary groups/templates, class `_meta`, and race/item/background metadata must be explicitly included in each catalog's corpus contract.

The 173-file `coverage-manifest.json` accurately accounts for the prior schema-discovery files but is not the complete runtime resource-file count: the recursive scan adds `monsters/template.json` and `sources/registry.json`. Those two paths are included in the new inventory and are not mislabeled as missing domain entities.

The root arrays of `adventures.json` and `books.json` function as publication indexes; no separate `index.json` or source manifest files are present. `sources/registry.json` is the shared identity registry, while `monsters/template.json` is actual transformation support rather than an index. No standalone fluff files are present; embedded descriptions remain part of root records.

The 45 repository JSON files outside this resource root are analysis inventories and schema/evidence reports. They are generated or documented evidence, not part of the pinned source corpus, and are not counted as source files.

## Not present in this runtime snapshot

No separate `fluff-*` files, source index manifests, standalone adventure chapter JSON, or generated output trees occur under `src/main/resources/data/`. Fluff-like entry fields and media paths may still occur embedded in root records. This is a repository-snapshot fact, not a claim about upstream 5eTools.


