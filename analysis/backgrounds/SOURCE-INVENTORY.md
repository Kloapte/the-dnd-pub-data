# Background Source Inventory

Source: `src/main/resources/data/backgrounds.json`. Full nested path/type/frequency coverage is in `normalized-field-inventory.json` (285 observed paths).

## Root data

The file contains `_meta` and one root collection, `background`, with **171 records**. There are 27 primary source codes and all primary, secondary, reprint, and copy-parent source codes are covered by the Source Registry. There are 26 `_copy` declarations; `_versions` and root `_mod` are absent. The source sets `edition: "one"` on 70 records; the other 101 omit it. Source-specific identities remain independent.

Primary source codes: `AAG`, `ABH`, `AI`, `AU`, `BGDIA`, `BGG`, `BMT`, `DSotDQ`, `EFA`, `EGW`, `ERLW`, `FRHoF`, `GGR`, `GoS`, `LFL`, `MOT`, `PHB`, `PSA`, `PSI`, `RHW`, `SCAG`, `SCC`, `SatO`, `ToA`, `VRGR`, `WBtW`, and `XPHB`. No new Source Registry identities were needed.

Root field counts: `name` and `source` 171 each; `page` 169 (page values range from 6 to 204); `edition` 70; `ability` 70; `feats` 84; `skillProficiencies` 146; `toolProficiencies` 124; `languageProficiencies` 56; `startingEquipment` 144; `entries` 145; `additionalSpells` 15; `reprintedAs` 13; `additionalSources` 13; `fromFeature` 15; `prerequisite` 4; `skillToolLanguageProficiencies` 1. Source/presentation flags (`hasFluff`, `hasFluffImages`, `srd`, `srd52`, `basicRules`, `basicRules2024`) also occur. `otherSources` occurs once.

## Structured shapes

- `ability` is a list of alternatives, currently represented by `choose.weighted.from` ability codes and matching `weights`; the two observed distributions are `[2,1]` and `[1,1,1]`.
- Skill, tool, language and feat grants are lists of packages whose dynamic keys identify named grants, `choose.from` alternatives, or source-defined choice counts such as `anyStandard`, `anyArtisansTool`, and `anyFromCategory`. Skill entries are not all equivalent to fixed grants. The source also has one `skillToolLanguageProficiencies` package collection.
- `startingEquipment` is a list of packages. Each package uses `_` for a fixed package or keys such as `a`/`b`, `A`/`B`, and `a`/`b`/`c`/`d` for alternatives. Entries are item UID strings, typed objects (`item`, `special`, `equipmentType`, `value`) with optional display labels, quantities, contained value, or worth. Currency values are copper-piece integers in this data convention; monetary amounts are not granted to a character by this importer.
- XPHB equipment uses named A/B packages with explicit `value` alternatives. PHB packages commonly combine `_` fixed contents with a separate a/b choice group.
- `feats` keys commonly contain source-qualified feat UIDs. `anyFromCategory` is an explicit category choice, not a specific feat reference.
- `additionalSpells` contains structured `expanded` maps keyed by spell level (`s0`–`s5`) and lists of spell references. `fromFeature` flags source-structured feat provenance. `prerequisite` is a list of campaign-name objects.
- `entries` contains feature entries, roleplaying prose, lists, and roll tables. It is retained internally as typed Entry DTE structures; public DTOs expose feature/table metadata only, not source prose or table content.

There are 271 source-authored table objects with varied column labels, including personality traits, ideals, bonds and flaws. Feature-marked entries use typed `data.isFeature` metadata. The complete label/path frequencies are recorded in the normalized inventory.

All 26 copy records resolve to a parent in this corpus. Of those, 21 have entry modifications containing 23 operations: `insertArr` (13) or `replaceArr` (10); replacement targets can be array index or entry name. Some payloads are arrays, others single entries. Detailed copy shapes and expanded counts are in `INHERITANCE-ANALYSIS.md`.
