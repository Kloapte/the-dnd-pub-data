# Magic Variants Source Inventory

Source: `src/main/resources/data/magicvariants.json`. The complete path-level inventory is in `normalized-field-inventory.json`.

## Root collections

| Collection | Count | Meaning |
|---|---:|---|
| `magicvariant` | 230 | 225 generic variant rules and five source-specific copy transformations |
| `linkedLootTables` | 1 source group / 127 entries | Auxiliary source-to-loot-table lookup, not item definitions |
| `_meta` | 1 | Import metadata (`internalCopies: [magicvariant]`) |

There are 181 distinct names and 230 unique source-aware identities. Primary source codes are DMG, XDMG, and AU (the latter five records are transformations). The file uses 56 structured source codes across primary, secondary, reprint, condition, spell, and copy-related references; all are present in Source Registry. Generic rule identity includes the source qualifier in `type` (`GV|DMG` or `GV|XDMG`), while the five AU records use their root source. Source-specific editions remain separate.

## Record shapes and fields

Generic root fields observed: `name`, `referenceSources`, `edition`, `type`, `requires`, `ammo`, `inherits`, `entries`, `excludes`, `source`, `hasFluffImages`. Five AU roots additionally have `_copy`; these have no `type`. `page` is carried in `inherits`, not as a root property.

`requires` is an array of alternative clauses. Fields within a clause are jointly required. `name` and `property` each accept either one string or a list. `excludes` is a single exclusion clause whose supplied fields and alternatives disqualify a candidate. The full nested property paths and observed variants are enumerated in the JSON inventory.

`inherits` carries item overrides and benefits: names, type, rarity, attunement, weapon/armor bonuses, charges/recharge, spell schedules, resistances, immunities, proficiency/language grants, movement, traits, weight/value formulas, references, source/page/reprint data, and other typed properties. `attachedSpells` is structured in 12 records. The `ammo` field and requirement marker fields are modeled explicitly. Four records contain meaningful `stealth: false`. Formula strings may contain dice markup; the DTO projection removes the markup wrapper without evaluating formulas. `entries` and loot-table metadata are retained for import provenance and are not published as prose.

`referenceSources` and inherited `otherSources`/`reprintedAs` are preserved and validated. `linkedLootTables` is auxiliary lookup data and is retained by the catalog, but it does not create item definitions. Rarity values include `common`, `uncommon`, `rare`, `very rare`, `legendary`, `none`, `unknown`, and `unknown (magic)`; values remain open strings.

## Source-specific structures

The 225 generic rules have `inherits` and use structured applicability predicates. Five AU records are named copy-derived transformations. Their copy chains point to two XDMG anchors; neither anchor is a concrete definition in the existing Item Catalog. This is recorded as an unresolved item relationship, not silently matched by name. No `_versions` or root `_mod` records occur. Inheritance and operation counts are detailed in `INHERITANCE-ANALYSIS.md`.

Entries contain 5eTools markup and descriptive text. No mechanics are inferred from entries. Fields and path-level variants not summarized above are accounted for in `normalized-field-inventory.json` (456 observed paths).
