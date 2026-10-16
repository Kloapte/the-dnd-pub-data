# Race and Species Source Inventory

## Corpus

`data/races.json` has three root properties: `_meta` (source metadata), `race` (160 records), and `subrace` (98 records). The normalized field inventory lists every observed nested path, JSON type, count, and representative values.

Race roots use 32 primary source codes; subraces use 15. The combined set of primary, secondary, additional, and reprint source codes contains 35 codes, all present in the Source Registry. Source codes include PHB and XPHB independently. There are no duplicate `(name, source)` identities among named race roots. Five PHB subrace entries omit `name`; they are auxiliary subrace relationship records and cannot form independently addressable definitions without inheriting an identity.

## Root collections and counts

| Collection | Count | Meaning |
| --- | ---: | --- |
| `race` | 160 | Source-specific race/species records |
| `subrace` | 98 | Parent-linked subraces, lineage options, variants, and auxiliary relationship records |

The five nameless PHB subrace records carry parent/source metadata but no independent identity. All other subrace records have names and explicit `raceName`/`raceSource` parent identity.

## Root field coverage

Race fields and counts: `name` 160, `source` 160, `page` 153, `size` 145, `speed` 144, `ability` 74, `traitTags` 85, `languageProficiencies` 75, `soundClip` 102, `entries` 144, `otherSources` 15, `reprintedAs` 51, `age` 66, `hasFluff` 136, `hasFluffImages` 137, `lineage` 50, `additionalSpells` 30, `darkvision` 77, `resist` 38, `_versions` 17, `heightAndWeight` 35, `edition` 18, `creatureTypes` 42, `sizeEntry` 21, `skillProficiencies` 60, `creatureTypeTags` 9, `toolProficiencies` 10, `conditionImmune` 7, `_copy` 16, `feats` 2, `srd` 9, `srd52` 9, `basicRules2024` 9, `basicRules` 4, `weaponProficiencies` 5, `additionalSources` 1, `blindsight` 1, `immune` 5, `armorProficiencies` 1, `alias` 1, `vulnerable` 1.

Subrace fields and counts: `source` 98, `page` 98, `raceName` 97, `raceSource` 97, `name` 93, `hasFluff` 78, `hasFluffImages` 73, `reprintedAs` 46, `ability` 78, `entries` 90, `skillProficiencies` 20, `srd` 9, `_versions` 7, `darkvision` 9, `resist` 11, `overwrite` 22, `otherSources` 14, `traitTags` 25, `languageProficiencies` 10, `additionalSpells` 51, `basicRules` 8, `heightAndWeight` 11, `armorProficiencies` 2, `alias` 1, `weaponProficiencies` 10, `speed` 12, `skillToolLanguageProficiencies` 3, `age` 3, `toolProficiencies` 4, `soundClip` 1, `feats` 1, `_copy` 1.

## Structured and descriptive content

Structured fields include size codes, movement objects (walk/fly/swim/climb observed), ability score arrays with fixed scores and `choose` groups, proficiencies represented as arrays of maps with fixed grants and choices, size/creature type codes, senses, damage/condition protections, age/height/weight formulas, feat grant categories, and `additionalSpells` structures with known, innate, prepared, expanded spell lists, ability choice, level keys, and rest/daily usage. `entries` contains nested named sections, lists, and tables and is descriptive source content; it may contain mechanics and 5eTools markup but is not machine-interpreted by this pipeline.

Trait replacement is represented by subrace `overwrite` keys. `lineage` appears as a boolean on race records. Media paths and `hasFluff*`, SRD/basic-rules, edition, and trait-tag fields are source metadata rather than public mechanics. The inventory JSON records nested shapes, including copy/version modification payloads.

## Source references

`otherSources` and `additionalSources` carry `{source,page}` references. `reprintedAs` uses both string UIDs and structured `{uid,tag}` values. Page values are integers when present. All observed codes resolve in the existing Source Registry; no registry edits were needed.

## Limitations surfaced by discovery

Subrace records do not all form selectable definitions: nameless records lack their own identity. Source entries may include copyrighted descriptive text and markup and are excluded from public projections. Version templates use `_variables` substitution and a mixture of abstract templates and implementation payloads; these require explicit expansion rather than provenance-only retention.
