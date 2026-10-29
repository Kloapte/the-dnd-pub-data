# Step 3 — Equipment Foundation: Phase 1 Discovery

**Status:** Phase 1 discovery complete; awaiting explicit review and approval. No Phase 2 schema design or conversion has begun.

## Scope reviewed

This inventory follows the Step 3 scope and Phase 1 gate in `ROADMAP.md`, the research-only rule in `AGENTS.md`, and the identity, coverage, omission, dependency, and no-inference boundaries in `BOUNDARIES.md`. Original data remains unchanged. Counts below describe source records and field presence; they do not claim semantic completeness or define the final Phase 2 inclusion boundary.

The complete record-level inventory, root-field/type frequencies, JSON Pointers, and SHA-256 hashes are in [`equipment-source-inventory.json`](../../../discover/step-3/phase-1/equipment-source-inventory.json). It enumerates every row in the primary catalogs and adjacent dependency catalogs without copying source prose.

| Catalog | Arrays inventoried | Rows |
|---|---|---:|
| `items-base.json` | `baseitem` 230; `itemProperty` 27; `itemType` 67; `itemTypeAdditionalEntries` 2; `itemEntry` 13; `itemMastery` 8 | 347 |
| `items.json` | `item` 2,512; `itemGroup` 119 | 2,631 |
| `magicvariants.json` | `magicvariant` 230; top-level `linkedLootTables` map recorded in source metadata | 230 |
| **Primary catalog total** |  | **3,208** |

Adjacent catalogs were checked for boundaries and dependencies: `loot.json` has 100 rows across its seven arrays; `vehicles.json` has 39 vehicles and 31 upgrades; `objects.json` has 37 objects. They are not treated as equipment records to duplicate into Step 3 by this inventory.

## Identity, structured fields, and source variation

No duplicate identity keys were found within the inventoried arrays using each catalog's source-qualified identity (`name|source`, the item property/type abbreviation plus source, or the magic variant name plus its type/source discriminator). The 2,512 item records and 230 base items have distinct `(name, source)` pairs. Source and edition distinctions are material: the item catalog is led by XDMG (584), DMG (500), PHB (168), and XPHB (123); the base catalog includes 94 XPHB and 91 PHB records. Similar names across editions remain separate identities.

Structured source-field coverage is broad and heterogeneous:

| Record set | Common structured fields and counts (records containing field) |
|---|---|
| Base items (230) | `name`, `source`, `type`, `rarity`, `page`: 230 each; `edition`: 176; `weight`: 216; `value`: 196; `weapon`: 98; `weaponCategory`: 102; `property`: 95; `dmg1`/`dmgType`: 101 each; `dmg2`: 17; `range`: 52; `mastery`: 48; `ammoType`: 36; `armor`: 25; `ac`: 27; `strength`: 6; `stealth`: 15; `packContents`: 11. |
| Items (2,512) | `name`, `source`: 2,512 each; `page`: 2,444; `rarity`: 2,469; `entries`: 2,143; `type`: 1,521; `weight`: 870; `value`: 703; `lootTables`: 643; `baseItem`: 241; `_copy`: 67. |
| Equipment mechanics in `item` | `reqAttune`: 899; `reqAttuneTags`: 253; `dmg1`/`dmgType`: 241 each; `weaponCategory`: 233; `property`: 223; `bonusWeapon`: 200; `attachedSpells`: 432; `charges`: 287; `recharge`: 289; `bonusAc`: 85; `bonusSpellAttack`: 53; `bonusSpellSaveDc`: 32; `resist`: 141; `conditionImmune`: 17; `modifySpeed`: 45; `light`: 80; `containerCapacity`: 31. |

Additional structured fields include armor and vehicle statistics, damage thresholds, passenger/cargo/crew capacity, abilities, immunities, vulnerabilities, proficiency/language grants, spellcasting focus, charges, recharge, sentience, and curses. Important representation variations include 991 item records without a `type` field; ten distinct `rarity` values (including `none`, `unknown`, `unknown (magic)`, and `varies`); numeric or string charge values; both string and object forms for some fields; arrays or objects for `attachedSpells`; and a string or object member in `property`. These forms require evidence-led treatment; one field or rarity value alone is not a safe magical/mundane scope classifier.

Source examples reviewed across the variation include PHB/XPHB Longsword (`items-base.json $.baseitem[117]`, `$.baseitem[118]`), Longbow (`$.baseitem[114]`, `$.baseitem[115]`), Chain Mail (`$.baseitem[33]`, `$.baseitem[34]`), XPHB Rope (`items.json $.item[1868]`), PHB/XPHB Riding Horse (`$.item[1748]`, `$.item[1749]`), Potion of Healing (`$.item[1610]`, `$.item[1611]`), Ring of Protection (`$.item[1794]`, `$.item[1795]`), Staff of Power (`$.item[2177]`, `$.item[2178]`), and Carriage (`$.item[377]`, `$.item[378]`). The XPHB weapon records expose weapon mastery alongside source-specific weapon properties; they are not mechanically interchangeable with PHB records.

## Recurrent semantic patterns

The full item entry trees were structurally surveyed. Their typed entry blocks include 1,182 nested `entries`, 237 `list`, 191 `item`, and 151 `table` blocks, alongside inset, quote, and section blocks. Inline references also cross into other domains; tag incidence across item entries includes spell references in 537 records, DC tags in 540, dice in 470, condition references in 304, damage in 302, action references in 263, and creature references in 268. These are discovery signals, not a substitute for interpreting each selected record during conversion.

The catalog repeatedly expresses these mechanic families:

- **Weapon and armor facts:** damage forms/types, versatile damage, category, range, ammunition, reload, armor class, Strength requirements, Stealth disadvantage, and edition-specific properties/mastery. Examples: base Longsword, Longbow, and Chain Mail above; all eight `itemMastery` entries are XPHB records.
- **Value, carrying, and equipment composition:** price/value, weight, packs, grouped alternatives, container capacity, and mounts. Examples include the 11 base records with `packContents` and 31 item records with `containerCapacity`.
- **Equipment-granted capabilities:** bonuses to AC, weapon attacks/damage, spell attacks/DCs, and saves; resistance/immunity; ability changes; speed/light changes; proficiencies; languages; and focus eligibility.
- **Limited-use and spell-linked items:** charges, recharge schedules/amounts, attached spell lists and use limits, scroll level, and consumable item types. These describe the item contract; they do not authorize a runtime mechanics evaluator.
- **Conditional/exceptional items:** attunement constraints, curses, sentience, unique procedures, transformations, and rare technology/age encodings. Examples include the copied Dormant/Awakened/Exalted item families and modern/futuristic equipment (`items-base.json` `age` occurs 24 times; `items.json` 19 times).

These families show where Step 2's typed references, damage/check/DC structures, identities, and source metadata may provide useful precedent. There is no existing equipment schema; Phase 2 must determine which structures transfer directly and where equipment-specific semantics differ. Reuse must not turn into an execution engine or a universal effects model.

Rare structured cases requiring explicit pattern review include the Skyship's `crewMin`, `crewMax`, `shippingCost`, and `travelCost` (`items.json $.item[2058]`); Hazirawn's `reqAttuneAlt` (`$.item[964]`); Orb of Skoraeus' `bonusSavingThrowConcentration` (`$.item[1450]`); and Serpent Scale Armor's nullable `dexterityMax` (`$.item[1977]`). Other low-frequency fields and every field's exact coverage are retained in the inventory JSON. These are source observations, not proposed mechanics or omission decisions.

## Identity links and dependencies

- All 241 `items.item[].baseItem` references resolve exactly to a source-qualified base-item identity.
- All 67 `_copy` targets resolve to an exact item identity. Copy chains and source-specific modifications must preserve source identity and provenance rather than silently merge records.
- The base-item catalog has 106 `reprintedAs` values: 105 resolve to base items and one to an item. The item catalog has 695 such values: 684 resolve to item identities; 11 do not resolve as direct item identities and need their existing source/group context retained.
- `itemGroup` contains 669 member strings: 371 exact item identities, 81 exact base-item identities, 33 unique unqualified item names, and 184 entries that are not direct identities. Some groups describe generated or hidden alternatives; Phase 2 must decide whether/how to model group membership and generation without inventing item identities.
- `magicvariants.json` contains 230 variant definitions; 225 have `inherits` and 227 have `requires`, while five are copy records. Requirements use multiple selectors (including type, source, weapon/armor flags, category, and name); variants are transformations over qualifying equipment, not 230 ordinary standalone base items. Their integration with loot tables is recorded in the inventory.
- Cross-domain item fields include attached spells (432 records), class-feature links (40), optional-feature links (100), vehicle links (31), deck links (8), and language grants (25). `lootTables` occurs on 643 items. These links depend on the separate spell, class/feature, vehicle/deck, language, and loot catalogs; unresolved targets must remain explicit rather than guessed.
- Twelve item records use the `VEH` type and carry vehicle-like statistics; the separate vehicle catalog has 39 vehicle and 31 upgrade records. Objects have a separate 37-record catalog. The Phase 2 scope should avoid duplicating those domains while preserving equipment-side references and item facts.

## Scope boundary and exceptional cases for Phase 2

The complete source catalogs are inventoried, but `items.json` mixes foundational gear, mounts, magic items, technology, grouped options, and vehicle-related records. The roadmap explicitly assigns advanced equipment/magical variants to later Step 9 work and says not to expand advanced magical equipment merely to complete foundational references. A precise record-level rule for the boundary inside `items.json` is not established by the Phase 1 source fields alone. Phase 2 should approve that boundary and the disposition of item groups, equipment variants, vehicle-like items, copies/reprints, source-only presentation fields, and external dependencies before schema design is treated as complete. `magicvariants.json` is therefore inventoried as a dependency and later-step candidate; no variant expansion or canonical record is produced here.

Recommended representative batches for later approval: (1) PHB/XPHB mundane weapon and armor pairs with differing mechanics; (2) ammunition, reload, modern/futuristic age, and weapon mastery; (3) packs, tools, grouped gear, and containers; (4) a potion/scroll and a charged, spell-linked item; (5) an attunement-constrained, cursed or sentient item; and (6) one base-item-linked generic variant plus one vehicle-related item to test the approved boundary. These are candidate batches, not authorization to convert them.

Documented omission/deferment candidates are source-only display/fluff and navigation fields; advanced magical item procedures and generic variant expansion; unresolved item-group/generated references; and mechanics whose meaning depends on spells, creatures, classes, features, vehicles, or encounter state. No candidate has been silently classified as an approved omission or canonical limitation in Phase 1.

## Phase 1 gate

The source inventory and discovery findings are ready for explicit review of coverage and Step 3 scope. Approval is required before Phase 2 schema design. No schema, canonical dataset, normalized data, Java code, or test was changed or created. Original source files were only read; their hashes and record-level locators are retained in the inventory artifact.

## Approval record — 2026-10-29

The user explicitly approved Step 3 Phase 1 and authorized Phase 2. The Phase 1 findings above are the approved discovery baseline for the design recorded in the Phase 2 report.
