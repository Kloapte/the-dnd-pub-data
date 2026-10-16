# Items source inventory

The inventory was generated from both complete packaged JSON files. `normalized-field-inventory.json` records every discovered property path, observed JSON type, occurrence count, examples for scalar leaves, root collection count, identities, and sources. It intentionally keeps the two files separate.

## Root collections

| File | Collection | Records | Meaning in source | Catalog definition? |
|---|---|---:|---|---|
| `items.json` | `item` | 2,512 | Concrete equipment and magic-item definitions | Yes |
| `items.json` | `itemGroup` | 119 | Group headers and selectable named item members; members may be concrete item references | No separate item identity; group is retained as import data |
| `items-base.json` | `baseitem` | 230 | Reusable mundane/equipment base definitions, many referenced by `item.baseItem` | Yes, as `BASE_ITEM` definitions |
| `items-base.json` | `itemProperty` | 27 | Weapon-property reference definitions | Auxiliary reference data |
| `items-base.json` | `itemType` | 67 | Extensible item type/category definitions | Auxiliary reference data |
| `items-base.json` | `itemTypeAdditionalEntries` | 2 | Supplemental descriptions keyed by type and source | Auxiliary reference data |
| `items-base.json` | `itemEntry` | 13 | Entry templates used by item groups/variants | Auxiliary import templates |
| `items-base.json` | `itemMastery` | 8 | Weapon mastery reference definitions | Auxiliary reference data |

The item and baseitem records each have unique `(name, source)` identities internally. The 230 base items and 2,512 item records have no cross-file identity collisions. Fifteen group identities overlap auxiliary `itemEntry` or `itemType` records; this is not an item identity collision. Two `itemType` records (`Vehicle (Air)`) share the same name/source but differ by abbreviation and are source type records, not item definitions.

## Identity and source coverage

There are 2,742 definition roots (`item` + `baseitem`) and 2,978 total records across the eight entity/auxiliary collections. Primary, reference, additional, and reprint source references span 134 distinct source codes; 131 were already present in the Source Registry. `EET`, `HAT-LMI`, and `RoTOS` are observed in the source and were added as identity-only registry records without invented publication metadata.

Definition root source counts and every additional source occurrence are in the normalized inventory. Pages are integer locators and are optional. `referenceSources` are source codes without a page; `additionalSources`/`otherSources` are source-plus-optional-page objects; `reprintedAs` are UIDs, with some records carrying a tag. Reprint targets retain their original codes.

## Structured and descriptive fields

All root and nested paths are listed in `normalized-field-inventory.json`. Key item fields include `type`, `typeAlt`, `rarity`, `value`, `weight`, `baseItem`, `property`, `mastery`, weapon/armor statistics, `reqAttune` and its tags, charges/recharge, spell-grant schedules, vehicle/capacity data, proficiency/language flags, grouped pack contents, source references, and item-specific boolean tags. Several root properties are intentionally source-specific and irregular: `focus` is boolean or string list; `reqAttune` is boolean or prose string; `charges` is number or string; `rechargeAmount` is number or markup string; `value` may be null; and `attachedSpells` is array or a schedule object.

Descriptions use nested 5eTools entry structures and markup. They remain internal source content and are not projected through public APIs. `srd`, `srd52`, `basicRules`, `basicRules2024`, `hasFluff`, `hasFluffImages`, `hasRefs`, `itemsHidden`, and similar flags are import/source availability or presentation metadata, not item mechanics.

## Type and category representation

`type` is one source code (sometimes edition-qualified, e.g. `SCF|XPHB`); `typeAlt` is a separate alternate code rather than a multi-membership list. The auxiliary `itemType` collection has authoritative name/abbreviation/source records, including Ammunition, Artisan's Tools, Gaming Set, Heavy/Medium/Light Armor, Melee/Ranged Weapon, Mount, Potion, Shield, Spellcasting Focus, Tool, Tack and Harness, Vehicle (Air/Land/Water/Space), Treasure, Scroll, Wand, Rod, Ring, Explosive, and other source-specific types. These are open source codes and are not forced into a narrower enum. Magic-item rarity values include `none`, `unknown`, `unknown (magic)`, `varies`, and named rarities; these values are preserved exactly.

## Source-specific shapes

Classic and 2024 PHB definitions are separate source identities. XPHB introduces explicit weapon mastery data, renamed/qualified type codes, and `edition`. DMG/XDMG and setting/source supplements contribute firearm/futuristic gear, vehicle records, tattoos, magic item groups, and source-specific fields. Auxiliary base data includes distinct 5e/2024 weapon properties and mastery records. See the complete field inventory for all low-frequency shapes.
