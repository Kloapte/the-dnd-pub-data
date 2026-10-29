# Step 3 — Equipment Foundation: Phase 3 Representative Conversion

**Status:** Representative fixtures and validation findings are submitted at the Phase 3 approval gate. Phase 4 has not started.

## Authorization and scope

The user approved the revised Phase 2 contracts and authorized Phase 3 on 2026-10-29. These fixtures use the approved single Item schema and the approved Step 3/Step 9 boundary. They cover the complete base-item identities only by representative examples; the selected `items.json` records are ordinary equipment whose operation does not depend on advanced magic or generic magic variants. No Step 9 item or `magicvariant` was converted.

## Representative artifacts

Files are under [`datasets/step-3/equipment/v1/`](../../../datasets/step-3/equipment/v1/):

| Model | Records | Coverage |
|---|---:|---|
| Item | 30 | Melee and ranged weapons, modern firearm, ammunition, armor, shield, adventuring gear, food, tool, spellcasting focus, mount, container capacity, ordinary pack contents, atomic packaged contents, poison identity, and the Net's no-damage exception. |
| Item Property | 13 | PHB/XPHB property differences, Ammunition and firearm ammunition, Burst Fire, Reload, Finesse, Light, Reach, Special, Two-Handed, and Thrown. |
| Item Mastery | 8 | Every XPHB weapon mastery definition. |

The item records include PHB/XPHB Longswords, an XPHB Dagger and Spear, PHB Net, Double-Bladed Scimitar (ERLW), Hoopak (DSotDQ), Lance, Chain Mail, Leather Armor, Shield, arrows and firearm ammunition, an XDMG Automatic Rifle, Amulet, Thieves' Tools, Riding Horse, Backpack, Torch, Basic Poison, Ball Bearings, Waterskin, and the Explorer's Pack with its eight source-listed contents.

## Interpretation and source accounting

- Source identities use `item:<source>:<slug>` for both base items and catalog items. Item-to-item links use `targetType: item`. The PHB Longsword's reprint link resolves to the included XPHB Longsword.
- Source prices are recorded in copper pieces without rounding; source weights are in pounds. PHB Waterskin's `(full)` qualifier is retained. The Backpack maps its structured 30-pound limit and source-stated one-cubic-foot limit. The Riding Horse maps its 60-foot speed and 480-pound carrying capacity.
- Weapon damage, versatile damage on the items, normal/long ranges, armor class, minimum Strength, Stealth disadvantage, light radii/duration, weapon era, shots before reload, and the sourced XDMG market-value rarity are represented as typed values.
- The four ordinary `Special` cases are individually interpreted on their owning weapons: Double-Bladed Scimitar's bonus-action follow-up damage; Hoopak's alternate ranged damage and melee ammunition exception; Lance's close-target disadvantage and unmounted hand requirement; and Net restraint, escape, net durability, target exceptions, and attack limit. The Net's missing base damage is permitted only with its `NET_RESTRAINT` component.
- All eight Mastery records were checked against their XPHB source definitions. Dagger/Longsword/Spear/Automatic Rifle examples reference Nick, Sap, and Slow. Topple links to `condition:phb:prone`; the PHB Light property links to the existing `action:phb:two-weapon-fighting` record.
- The Explorer's Pack has eight typed contents with quantities. Ball Bearings is represented as 1,000 atomic packaged pieces. References to the included item contents and firearm/sling ammunition resolve within this fixture.
- Source locations for the main exceptional examples are `items-base.json $.baseitem[48]` (PHB Dagger), `[52]` (Double-Bladed Scimitar), `[90]` (Hoopak), `[99]` (Lance), `[117–118]` (Longswords), `[137]` (Net), and `[194]` (XPHB Spear); and `items.json $.item[135]` (Backpack), `[156]` (Ball Bearings bag), `[169]` (Basic Poison), `[730]` (Explorer's Pack), `[1748]` (Riding Horse), `[2299]` (Thieves' Tools), `[2330]` (Torch), and `[2440]` (Waterskin). The Phase 1 inventory retains full file paths, hashes, and field evidence.

## Schema gaps and unrepresented source portions

The following source portions remain visible and are not reinterpreted as absent rules. They are not added to the canonical records because the approved Phase 2 schemas cannot faithfully represent them. A schema change requires explicit review and approval; none was made in this phase.

| Source locator | Finding and current disposition |
|---|---|
| `items-base.json $.itemProperty[24–25]`; `$.baseitem[117–118].property` and `$.baseitem[194].property` | Versatile damage is item-specific, and the Item schema correctly stores it on each weapon as `versatileDamage`. The Item Property schema incorrectly requires `twoHandedDamage` on the shared property entity. No Versatile property records were authored; the three item property references remain unresolved in this fixture pending a contract correction. |
| `items-base.json $.itemProperty[22]`; `$.baseitem[137].property[1]` | PHB Thrown does not specify that drawing the weapon is part of the attack. The Item Property schema requires `drawAsPartOfAttack`, so this fact cannot be represented without inventing a value. The Net's Thrown property link is omitted from the fixture and recorded as `SCHEMA_UNSUPPORTED`. The XPHB Thrown definition is representable and is included. |
| `items.json $.item[169].entries[0]` | Basic Poison's bonus-action application, eligible damage types, extra poison damage, and one-minute/first-hit duration have no typed item-use/effect contract. The Item retains its identity, price, and `poison` marker; the use and effect are `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[2330].entries[0]` | Torch light is represented, but its one-point fire damage on a hit with a burning torch has no supported item-attack component. That effect is `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[156].entries[0]` | The Ball Bearings bag's action, area, Dexterity save, Prone result, and half-speed exception are not represented. Package contents and atomicity are retained; the use procedure is `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[2299].entries[0]` and `$.item[2299].additionalEntries` | Thieves' Tools' check proficiency and additional trap-related uses lack a supported tool-use/check component. The Item identity and equipment facts are retained; unsupported portions are `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[2440].entries[0]` | Waterskin capacity is given as four pints, while Item capacity accepts pounds or cubic feet only. Its identity, full-weight qualifier, price, and weight are retained; the capacity is `SCHEMA_UNSUPPORTED` rather than converted to a different unit. |
| `items.json $.item[2312].entries[0]`; `$.item[990].entries[0]` | Tinderbox ignition timing and hempen rope hit points/burst check are not represented by the approved Item components. These portions are `SCHEMA_UNSUPPORTED`. |

Seventeen retained `sourceMetadata.reprintedAs` links point to counterpart Items outside this small fixture. Their target IDs are preserved from source reprint evidence; the counterpart records are not duplicated merely to close the example batch. Three `weapon.properties` links to Versatile are the only unresolved in-fixture property references. Other property, mastery, ammunition, content, and included Step 2 references validate or resolve within the available approved data.

## Validation

- Ajv Draft 2020-12 strict mode compiled the shared, Item, Item Property, and Item Mastery schemas and validated all 51 fixture records: 30 Items, 13 properties, and 8 masteries.
- Representative positive and negative schema cases passed: ordinary weapon, armor/shield subtype matching, Net's no-damage exception, and rejection of incompatible weapon/armor components, missing non-exempt weapon damage, missing armor AC, and mismatched armor/shield subtype.
- All four `Special` patterns and all eight mastery records were validated against their schemas. Item contents and ammunition links resolve within the fixture. Cross-reference checking found only the three disclosed Versatile property links unresolved in the fixture.
- Source review confirmed 102 base-item weapon candidates (the PHB Net is the sole source candidate without damage) and 27 base-item armor/shield records with AC. Among 236 item-catalog weapon candidates all have damage. Of 78 armor/shield candidates, only Powered Armor (QftIS) lacks AC; it is advanced equipment within the approved Step 9 boundary, not this Step 3 batch.
- No production code, approved schema, normalized dataset, or test file was changed. The source datasets remain untouched.

## Phase 3 gate

The representative conversion, source interpretations, unresolved fixture references, and schema gaps are submitted for explicit Phase 3 review. Phase 4 conversion conventions and full-catalog work have not started.
