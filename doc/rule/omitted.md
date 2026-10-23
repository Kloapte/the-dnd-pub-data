# Rule source content omitted during Step 2 Phase 3 representative conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### rule:xphb:unarmed-strike — entry 0

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[225]`
- Source entry path: `$.variantrule[225].entries[0]`
- Canonical output: `rule/xphb-unarmed-strike.json`
- OMITTED: The five-foot range also applies to the Grapple and Shove options — ATTACK_ROLL.reachFeet can express range for the attack/damage branch, but the approved UNARMED_STRIKE_OPTION structure has no range field for its saving-throw options.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Instead of using a weapon to make a melee attack, you can use a punch, kick, head-butt, or similar forceful blow. In game terms, this is an Unarmed Strike—a melee attack that involves you using your body to damage, grapple, or shove a target within 5 feet of you."
```

### rule:xphb:difficult-terrain — entry 2

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[53]`
- Source entry path: `$.variantrule[53].entries[2]`
- Canonical output: `rule/xphb-difficult-terrain.json`
- OMITTED: Listed space contents and the open-ended similar-feature adjudication — The approved movement component cannot express the qualifying terrain conditions or the DM-adjudicated similarity test.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "list",
  "items": [
    "A creature that isn't Tiny or your ally",
    "Furniture that is sized for creatures of your size or larger",
    "Heavy snow, ice, rubble, or undergrowth",
    "Liquid that's between shin- and waist-deep",
    "A narrow opening sized for a creature one size smaller than you",
    "A slope of 20 degrees or more"
  ]
}
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 0

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[0]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Magic items are the DM's purview, so you decide how they fall into the party's possession. As an option, you can allow player characters to craft magic items."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 1

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[1]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"The creation of a magic item is a lengthy, expensive task. To start, a character must have a formula that describes the construction of the item. The character must also be a spellcaster with spell slots and must be able to cast any spells that the item can produce. Moreover, the character must meet a level minimum determined by the item's rarity, as shown in the Crafting Magic Items table. For example, a 3rd-level character could create a {@item wand of magic missiles} (an uncommon item), as long as the character has spell slots and can cast {@spell magic missile}. That same character could make a {@item +1 weapon} (another uncommon item), no particular spell required."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 2

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[2]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"You can decide that certain items also require special materials or locations to be created. For example, a character might need {@item alchemist's supplies|phb} to brew a particular potion, or the formula for a {@item flame tongue} might require that the weapon be forged with lava."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 3

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[3]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "table",
  "caption": "Crafting Magic Items",
  "colLabels": [
    "Item Rarity",
    "Creation Cost",
    "Minimum Level"
  ],
  "colStyles": [
    "col-4",
    "col-4 text-right",
    "col-4 text-center"
  ],
  "rows": [
    [
      "Common",
      "100 gp",
      "3rd"
    ],
    [
      "Uncommon",
      "500 gp",
      "3rd"
    ],
    [
      "Rare",
      "5,000 gp",
      "6th"
    ],
    [
      "Very rare",
      "50,000 gp",
      "11th"
    ],
    [
      "Legendary",
      "500,000 gp",
      "17th"
    ]
  ]
}
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 4

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[4]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"An item has a creation cost specified in the Crafting Magic Items table. A character engaged in the crafting of a magic item makes progress in 25 gp increments, spending that amount for each day of work until the total cost is paid. The character is assumed to work for 8 hours each of those days. Thus, creating an uncommon magic item takes 20 days and 500 gp. You are free to adjust the costs to better suit your campaign."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 5

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[5]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"If a spell will be produced by the item being created, the creator must expend one spell slot of the spell's level for each day of the creation process. The spell's material components must also be at hand throughout the process. If the spell normally consumes those components, they are consumed by the creation process. If the item will be able to produce the spell only once, as with a {@item spell scroll}, the components are consumed only once by the process. Otherwise, the components are consumed once each day of the item's creation."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 6

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[6]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Multiple characters can combine their efforts to create a magic item if each of them meets the level prerequisite. Each character can contribute spells, spell slots and components, as long as everyone participates during the entire crafting process. Each character can contribute 25 gp worth of effort for each day spent helping to craft the item."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 7

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[7]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Normally, a character who undertakes this activity creates a magic item described in chapter 7, \"Treasure.\" At your discretion, you can allow players to design their own magic items, using the guidelines in chapter 9, \"Dungeon Master's Workshop.\""
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 8

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[8]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Source-specific procedure details for this entry — The approved Rule schema cannot model the precise optionality, prerequisites, rarity tables, cost/work formula, spell-slot/material expenditure, cooperative contributions, campaign choices, or lifestyle exception without loss.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"While crafting a magic item, a character can maintain modest lifestyle without having to pay the 1 gp per day, or a comfortable lifestyle at half the normal cost (see chapter 5, \"{@book Equipment|PHB|5|Lifestyle Expenses},\" of the {@book Player's Handbook|PHB})."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 9

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[9]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- OMITTED: Formula rarity, item-rarity relationship, availability, and sale costs — The approved downtime component has no formula model or rarity/cost table.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "inset",
  "name": "Magic Item Formulas",
  "source": "DMG",
  "page": 141,
  "entries": [
    "A magic item formula explains how to make a particular magic item. Such a formula can be an excellent reward if you allow player characters to craft magic items.",
    "You can award a formula in place of a magic item. Usually written in a book or on a scroll, a formula is one step rarer than the item it allows a character to create. For example, the formula for a common magic item is uncommon. No formulas exist for legendary items.",
    "If the creation of magic items is commonplace in your campaign, a formula can have a rarity that matches the rarity of the item it allows a character to create. Formulas for common and uncommon items might even be for sale, each with a cost double that of its magic item."
  ]
}
```
