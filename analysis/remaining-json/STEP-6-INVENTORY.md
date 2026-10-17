# Step 6 â€” Remaining JSON Catalog Inventory

## Scope and method

This is the source inventory and implementation plan for Step 6. The scanned runtime corpus is `src/main/resources/data/`, the repository's pinned 5eTools v2.37.0 resource snapshot. The repository has no independent snapshot-version marker beside the project context; each file is therefore SHA-256 fingerprinted so the scan can be tied to the exact checked-in inputs. Catalog implementation status below reflects completed Step 6 work.

The 45 JSON files outside `src/main/resources/data/` are analysis/schema/evidence artifacts, not additional upstream source records. They are intentionally not counted in the 175-file source-corpus total.

Run `node analysis/remaining-json/scan-json-inventory.mjs` from the repository root. It recursively parses every JSON file, records sorted root collections and record counts, collects recursive field paths and observed JSON types, source properties, markup tags, UID-like strings, copy/version/template declarations, primary source values, file hashes, and candidate duplicate `(source, name)` keys. It writes `inventory-evidence.json` and `per-file-classification.json`. The scan found **175/175 files**, **0 parse failures**, and **32 family buckets** (including implementation families and the registry). The existing `analysis/coverage-manifest.json` covers 173 content files; the two additional files are `sources/registry.json` and `monsters/template.json`, neither previously counted as a domain-root contribution. No JSON file in the resource tree remains unclassified.

Physical paths divide into 28 root-level JSON files, 15 under `classes/`, 113 under `monsters/`, 18 under `spells/`, and one under `sources/`.

The evidence contains 58 root array collections and 15,125 array records across all families. This total includes overlapping support records, class feature records and other non-independent definitions; it is not a count of unique domain entities. Existing schema reports remain useful domain-specific evidence, but the new per-file report includes every source file, including support files omitted from the previous manifest.

## Coverage summary

| Classification | Families | Files / records |
|---|---|---|
| Existing primary source families | 29 | Actions, adventures, backgrounds, Bastion Facilities, books, character creation options, classes, conditions/diseases/statuses, cults and boons, Deities, encounters, feats, items/base items, languages, magic variants, monsters, name tables, Objects, optional features, races/subraces, rewards, senses, skills, spells, Treasure Tables, traps and hazards, Variant Rules, Vehicles and Vehicle Upgrades |
| Existing support family | 1 separate registry family | Source Registry (`sources` array; 181 records after confirmed PiP and HFDoMM identity registrations); monster templates and legendary groups are separately identified support collections inside the monster family |
| Unimplemented source families after Adventure, Name Table, Character Creation Option, Cult, Boon, Object, Bastion Facility, Encounter, Reward, Traps/Hazards, Vehicles/Vehicle Upgrades, Variant Rules, Deities, and Treasure Tables implementation | 2 | 2 individual root files; **336** root records |

The initial inventory found 2,034 unimplemented records. Adventure Index (101), Name Tables (10), Character Creation Options (44), Cults (30), Boons (12), Objects (37), Bastion Facilities (70), Encounters (42), Rewards (278), Traps (37), Hazards (73), Vehicles (39), Vehicle Upgrades (31), Variant Rules (231), Deities (563), Treasure Tables (100 root records), and Recipes (316, metadata-only public projection) are implemented. Homecraft's 20 crochet patterns are OUT OF SCOPE by product decision, leaving zero in-scope Step 6 source families pending.

At initial inventory, distinct primary `source` values were: adventures 101, bastions 5, character options 6, cults/boons 4, deities 13, encounters 10, homecrafts 1, loot 3, names 3, objects 12, recipes 5, rewards 26, traps/hazards 14, variant rules 15, and vehicles 11. Adventure source values are publication IDs/source tokens, not 101 distinct book publication source codes. The current evidence refresh reflects the added PiP registry identity and the implemented family classifications. Full per-record values and source occurrence counts remain in the evidence. Existing primary catalog source distribution is in the same evidence, by file and root collection.

All names in the requested remaining-family list correspond to files, but several names overstate or misdescribe the actual data: `adventures.json` is a publication/index and contents-header dataset, not adventure chapter text; `bastions.json` contains `facility` records, not a complete bastion system; `homecrafts.json` contains crochet patterns; `loot.json` contains seven distinct table/support collections rather than a single item catalog; and `charcreationoptions.json` is the source filename for `charoption` records. The file `trapshazards.json` separates `trap` and `hazard` arrays. The repository contains no separate fluff JSON files in its resource snapshot; narrative entries are embedded within several definition families.

## Source, transformation, and identity evidence

The initial recursive scan saw 179 source codes in the registry and 182 distinct string values in all `source` properties. Three values were absent at that time: `HFDoMM`, `PiP`, and `WGE`. The Adventure implementation confirmed `PiP` as the primary source identity for *Peril in Pinebrook* and registered it without aliases or publication metadata. The recipe source audit confirmed `HFDoMM` as a primary identity used by 10 recipe definitions and as a secondary citation; it is now registered without aliases or publication metadata. The registry now contains 181 identities. `WGE` (Eberron bestiary source property) remains outside the implemented source-identity scope. Primary root-record source properties should be checked separately from nested source references; all current values and counts are retained in the refreshed evidence JSON.

The recursive pass records **1,485 `_copy` objects, 372 entries under `_versions`, 8 `_abstract` markers, 60 `_implementations` entries, 2,657 `reprintedAs` targets, and 812 `otherSources` entries** across this source tree. These are syntactic occurrence counts, not expanded definitions or resolved edges; they are broken down per file and family in the machine report. The already implemented classes, backgrounds, deities, feats, items, base items, magic variants, races, monsters, and their support files contain transformations. Among unimplemented families, only `deities.json` has `_copy` declarations (6); no unimplemented family has `_versions`, `_abstract`, or `_implementations`. Existing identity checks reveal five repeated deity `(source,name)` keys in PHB (`Oghma`, `Silvanus`, `Surtur`, `Thrym`, `Tyr`). Their `pantheon` and sometimes other authored values distinguish the records, so a deity ID must include a source-authored pantheon/identity context. This is a demonstrated collision, not a hypothetical one.

## Files and evidence index

- [Per-file source coverage matrix](SOURCE-COVERAGE-MATRIX.md)
- [Schema and complexity assessment](SCHEMA-COMPLEXITY.md)
- [Catalog boundaries](CATALOG-BOUNDARIES.md)
- [Cross-catalog dependencies](DEPENDENCY-MATRIX.md)
- [Licensing boundaries](LICENSING-BOUNDARIES.md)
- [Recommended implementation order](IMPLEMENTATION-ORDER.md)
- [Persistence readiness](PERSISTENCE-READINESS.md)
- [Machine-readable full inventory](inventory-evidence.json)
- [Machine-readable per-file classifications and hashes](per-file-classification.json)
- [Reproducible scanner](scan-json-inventory.mjs)

## Limitations

The scanner is schema discovery evidence, not an importer or license decision. It reports full JSON paths and observed types, but semantic classification of every prose field, markup link, dynamic tag, or target reference remains part of each catalog's Stage A. Candidate `(source,name)` duplicates are a warning signal; parent context, category, pantheon, edition, and source copy lineage determine whether they are real identity conflicts. The scanner does not execute `_copy` semantics or bind markup references.

## Validation

- Inventory scanner rerun produced identical SHA-256 hashes for both generated evidence files.
- All 175 source files parsed and classified. Implemented families retain their own typed field coverage, boundary, and resolver evidence; Bastion Facility details are in `bastion-facilities/`.
- `mvn -q test`: **335 tests, 0 failures, 0 errors, 0 skipped** after Bastion Facilities implementation.

