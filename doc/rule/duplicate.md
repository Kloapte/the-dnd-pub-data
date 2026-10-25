# Rule source content duplicate during Step 2 Core Rules conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.

### rule:xphb:unarmed-strike — entry 2

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[225]`
- Source entry path: `$.variantrule[225].entries[2]`
- Canonical output: `rule/xphb-unarmed-strike.json`
- DUPLICATE: The Grapple DC also governs escape attempts — A typed relation on the Rule references the XPHB Grappled condition and states that escape attempts use the same DC.

Complete original source entry:

```json
{
  "type": "entries",
  "entries": [
    {
      "type": "entries",
      "name": "Damage",
      "entries": [
        "You make an attack roll against the target. Your bonus to the roll equals your Strength modifier plus your {@variantrule Proficiency|XPHB|Proficiency Bonus}. On a hit, the target takes Bludgeoning damage equal to 1 plus your Strength modifier."
      ]
    },
    {
      "type": "entries",
      "name": "Grapple",
      "entries": [
        "The target must succeed on a Strength or Dexterity {@variantrule Saving Throw|XPHB|saving throw} (it chooses which), or it has the {@condition Grappled|XPHB} condition. The DC for the {@variantrule Saving Throw|XPHB|saving throw} and any escape attempts equals 8 plus your Strength modifier and {@variantrule Proficiency|XPHB|Proficiency Bonus}. This grapple is possible only if the target is no more than one size larger than you and if you have a hand free to grab it."
      ]
    },
    {
      "type": "entries",
      "name": "Shove",
      "entries": [
        "The target must succeed on a Strength or Dexterity {@variantrule Saving Throw|XPHB|saving throw} (it chooses which), or you either push it 5 feet away or cause it to have the {@condition Prone|XPHB} condition. The DC for the {@variantrule Saving Throw|XPHB|saving throw} equals 8 plus your Strength modifier and {@variantrule Proficiency|XPHB|Proficiency Bonus}. This shove is possible only if the target is no more than one size larger than you."
      ]
    }
  ]
}
```

### rule:xphb:difficult-terrain — entry 0

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[53]`
- Source entry path: `$.variantrule[53].entries[0]`
- Canonical output: `rule/xphb-difficult-terrain.json`
- DUPLICATE: complete source entry — The example is fully implied by the represented one extra foot of movement cost per foot through difficult terrain.

Complete original source entry:

```json
"If a space is Difficult Terrain, every foot of movement in that space costs 1 extra foot. For example, moving 5 feet through Difficult Terrain costs 10 feet of movement. Difficult Terrain isn't cumulative; either a space is Difficult Terrain or it isn't."
```

### rule:dmg:downtime-activity-crafting-a-magic-item — entry 9

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[62]`
- Source entry path: `$.variantrule[62].entries[9]`
- Canonical output: `rule/dmg-downtime-activity-crafting-a-magic-item.json`
- DUPLICATE: Magic-item formula as a prerequisite — The source already states that a formula is required to begin crafting; the inset explains the same prerequisite.

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

### rule:xphb:save — entry 0

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[186]`
- Source entry path: `$.variantrule[186].entries[0]`
- Canonical output: `none`
- DUPLICATE: ?Save? is another name for the Saving Throw procedure. — No independent mechanic is added; the source definition is represented by rule:xphb:saving-throw.

Complete original source entry:

```json
"Save is another name for a {@variantrule Saving Throw|XPHB|saving throw}."
```

### rule:xphb:weapon-attack — entry 0

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[230]`
- Source entry path: `$.variantrule[230].entries[0]`
- Canonical output: `none`
- DUPLICATE: Weapon Attack is the weapon form already represented by the XPHB Attack Roll Rule. — No independent mechanic is added; the source definition is represented by rule:xphb:attack-roll.

Complete original source entry:

```json
"A weapon attack is an attack roll made with a weapon."
```

### rule:dmg:skill-variants — entry 0

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[202]`
- Source entry path: `$.variantrule[202].entries[0]`
- Canonical output: `rule/dmg-skill-variants.json`
- DUPLICATE: The normal relationship between skills and their associated ability checks. — The source Skill records preserve each Skill identity and associated ability.

Complete original source entry:

```json
"A skill dictates the circumstances under which a character can add his or her proficiency bonus to an ability check. Skills define those circumstances by referring to different aspects of the six ability scores. For example, {@skill Acrobatics} and {@skill Stealth} are two different aspects of Dexterity, and a character can specialize in either or both."
```

### rule:dmg:diagonals ? entry 52

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[52]`
- Source entry path: `undefined`
- Canonical output: `rule/dmg-diagonals.json`
- DUPLICATE: Worked example of the alternating movement cost ? The example repeats the cost sequence represented by entry 1.

Complete original source entry:

```json
undefined
```

### rule:dmg:diagonals ? entry 2

- Source file: `src/main/resources/data/variantrules.json`
- Source object: `$.variantrule[52]`
- Source entry path: `$.variantrule[52].entries[2]`
- Canonical output: `rule/dmg-diagonals.json`
- DUPLICATE: Worked example of the alternating movement cost ? The example repeats the cost sequence represented by entry 1.

Complete original source entry:

```json
"For example, a character might move one square diagonally (5 feet), then three squares straight (15 feet), and then another square diagonally (10 feet) for a total movement of 30 feet."
```
