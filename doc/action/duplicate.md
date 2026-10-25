# Action source content duplicate during Step 2 Core Rules conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### action:dmg:mark — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[29]`
- Source entry path: `$.action[29].entries[1]`
- Canonical output: `action/dmg-mark.json`
- DUPLICATE: Incapacitated and Shocking Grasp examples of being unable to take reactions — The reaction availability requirement represents the general restriction; the named examples add no additional behavior to this Action operation.

Complete original source entry:

```json
"When a creature makes a melee attack, it can also mark its target. Until the end of the attacker's next turn, any opportunity attack it makes against the marked target has advantage. The opportunity attack doesn't expend the attacker's reaction, but the attacker can't make the attack if anything, such as the {@condition incapacitated} condition or the {@spell shocking grasp} spell, is preventing it from taking reactions. The attacker is limited to one opportunity attack per turn."
```

### action:phb:dash — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[5]`
- Source entry path: `$.action[5].entries[1]`
- Canonical output: `action/phb-dash.json`
- DUPLICATE: Numeric examples showing how Speed increases and decreases affect extra movement — The examples restate the already represented current-Speed calculation.

Complete original source entry:

```json
"Any increase or decrease to your speed changes this additional movement by the same amount. If your speed of 30 feet is reduced to 15 feet, for instance, you can move up to 30 feet this turn if you dash."
```

### action:phb:improvising-an-action — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[25]`
- Source entry path: `$.action[25].entries[0]`
- Canonical output: `action/phb-improvising-an-action.json`
- DUPLICATE: Illustrative examples of possible improvised activities — The examples add no fixed procedure beyond the represented improvised-action capability.

Complete original source entry:

```json
"Your character can do things not covered by the actions in this chapter, such as breaking down doors, intimidating enemies, sensing weaknesses in magical defenses, or calling for a parley with a foe. The only limits to the actions you can attempt are your imagination and your character's ability scores. See the descriptions of the ability scores in {@book chapter 7|phb|7|Ability Scores and Modifiers} for inspiration as you improvise."
```

### action:phb:opportunity-attack — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[30]`
- Source entry path: `$.action[30].entries[0]`
- Canonical output: `action/phb-opportunity-attack.json`
- DUPLICATE: Introductory combat description — The sentence is contextual and adds no procedure beyond the following rules.

Complete original source entry:

```json
"In a fight, everyone is constantly watching for enemies to drop their guard. You can rarely move heedlessly past your foes without putting yourself in danger; doing so provokes an opportunity attack."
```

### action:phb:other-activity — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[32]`
- Source entry path: `$.action[32].entries[0]`
- Canonical output: `action/phb-other-activity.json`
- DUPLICATE: Introductory flourish description — This is explanatory context for the following turn activities.

Complete original source entry:

```json
"Your turn can include a variety of flourishes that require neither your action nor your move."
```

### action:phb:other-activity — entry 5

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[32]`
- Source entry path: `$.action[32].entries[5]`
- Canonical output: `action/phb-other-activity.json`
- DUPLICATE: Examples of incidental object interactions — The examples add no distinct mechanic beyond the represented ordinary object interaction.

Complete original source entry:

```json
{
  "type": "inset",
  "name": "Interacting with Objects Around You",
  "entries": [
    "Here are a few examples of the sorts of thing you can do in tandem with your movement and action:",
    {
      "type": "list",
      "items": [
        "draw or sheathe a sword",
        "open or close a door",
        "withdraw a potion from your backpack",
        "pick up a dropped axe",
        "take a bauble from a table",
        "remove a ring from your finger",
        "stuff some food into your mouth",
        "plant a banner in the ground",
        "fish a few coins from your belt pouch",
        "drink all the ale in a flagon",
        "throw a lever or a switch",
        "pull a torch from a sconce",
        "take a book from a shelf you can reach",
        "extinguish a small flame",
        "don a mask",
        "pull the hood of your cloak up and over your head",
        "put your ear to a door",
        "kick a small stone",
        "turn a key in a lock",
        "tap the floor with a 10-foot pole",
        "hand an item to another character"
      ]
    }
  ]
}
```
