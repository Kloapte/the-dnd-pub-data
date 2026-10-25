# Action source content omitted during Step 2 Core Rules conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### action:phb:attack — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[1]`
- Source entry path: `$.action[1].entries[0]`
- Canonical output: `action/phb-attack.json`
- OMITTED: Action examples and description of common use — The sword, bow, and fist examples are illustrative use cases; typed attack capabilities represent the relevant forms.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"The most common action to take in combat is the Attack action, whether you are swinging a sword, firing an arrow from a bow, or brawling with your fists."
```

### action:phb:attack — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[1]`
- Source entry path: `$.action[1].entries[1]`
- Canonical output: `action/phb-attack.json`
- OMITTED: The separately defined Making an Attack procedure — The source directs attack resolution to a Rule procedure that is not represented by the Action entity.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"With this action, you make one melee or ranged attack. See the \"{@book Making an Attack|phb|9|making an attack}\" section for the rules that govern attacks."
```

### action:phb:attack — entry 2

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[1]`
- Source entry path: `$.action[1].entries[2]`
- Canonical output: `action/phb-attack.json`
- OMITTED: Additional attacks granted by features such as Extra Attack — The action record points to a feature-dependent count, but the Action schema has no composable attack-count modifier and the granting feature belongs to the Class domain.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Certain features, such as the Extra Attack feature of the fighter, allow you to make more than one attack with this action."
```

### action:phb:cast-a-spell — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[3]`
- Source entry path: `$.action[3].entries[0]`
- Canonical output: `action/phb-cast-a-spell.json`
- OMITTED: Introductory examples of spellcasters and the PHB Chapter 10 reference — The introductory statement is context; the chapter-level spellcasting procedure is outside this Action record and no canonical book-section target type exists.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Spellcasters such as wizards and clerics, as well as many monsters, have access to spells and can use them to great effect in combat. Each spell has a casting time, which specifies whether the caster must use an action, a reaction, minutes, or even hours to cast the spell. Casting a spell is, therefore, not necessarily an action. Most spells do have a casting time of 1 action, so a spellcaster often uses his or her action in combat to cast such a spell. See {@book chapter 10|phb|10|casting a spell} for the rules on spellcasting."
```

### action:dmg:mark — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[29]`
- Source entry path: `$.action[29].entries[0]`
- Canonical output: `action/dmg-mark.json`
- OMITTED: The optional-rule design purpose — This introductory sentence gives context and adds no independent mechanical rule.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"This option makes it easier for melee combatants to harry each other with opportunity attacks."
```

### action:dmg:mark — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[29]`
- Source entry path: `$.action[29].entries[1]`
- Canonical output: `action/dmg-mark.json`
- OMITTED: Canonical targets for the unqualified Incapacitated and Shocking Grasp tags — The mechanical restriction is represented as requiring reaction availability, but the source tags do not identify one PHB or XPHB target identity.; cause: `REFERENCE_UNRESOLVED`

Complete original source entry:

```json
"When a creature makes a melee attack, it can also mark its target. Until the end of the attacker's next turn, any opportunity attack it makes against the marked target has advantage. The opportunity attack doesn't expend the attacker's reaction, but the attacker can't make the attack if anything, such as the {@condition incapacitated} condition or the {@spell shocking grasp} spell, is preventing it from taking reactions. The attacker is limited to one opportunity attack per turn."
```

### action:dmg:activate-an-item — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[0]`
- Source entry path: `$.action[0].entries[0]`
- Canonical output: `action/dmg-activate-an-item.json`
- OMITTED: Specific command-word, read-scroll, and drink-potion methods tied to individual items — The generic action identifies item-defined activation; the exact method is specified by each Item record, which is outside the current expanded domains.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Activating some magic items requires a user to do something in particular, such as holding the item and uttering a command word, reading the item if it is a scroll, or drinking it if it is a potion. The description of each item category or individual item details how an item is activated. Certain items use one or more of the following rules related to their activation."
```

### action:dmg:activate-an-item — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[0]`
- Source entry path: `$.action[0].entries[1]`
- Canonical output: `action/dmg-activate-an-item.json`
- OMITTED: The limitation on a specific Rogue Fast Hands feature — The referenced subclass feature belongs to the future Class/feature domain and has no approved canonical target here.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"If an item requires an action to activate, that action isn't a function of the {@action Use an Object} action, so a feature such as the rogue's {@subclassFeature Fast Hands|Rogue||Thief||3} can't be used to activate the item."
```

### action:dmg:activate-an-item — entry 2

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[0]`
- Source entry path: `$.action[0].entries[2]`
- Canonical output: `action/dmg-activate-an-item.json`
- OMITTED: Command-word speech and sound-prevention restriction — The approved Action contract has no typed structure for this behavior.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "entries",
  "name": "Command Word",
  "entries": [
    "A command word is a word or phrase that must be spoken audibly for the item to operate. A magic item that requires the user to speak a command word can't be activated in the area of any effect that prevents sound, such as the area created by the silence spell."
  ]
}
```

### action:dmg:activate-an-item — entry 3

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[0]`
- Source entry path: `$.action[0].entries[3]`
- Canonical output: `action/dmg-activate-an-item.json`
- OMITTED: Consumable use, application or swallowing, and depletion after activation — The effects and item state transitions belong to item-specific mechanics outside the Action contract.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
{
  "type": "entries",
  "name": "Consumables",
  "entries": [
    "Some items are used up when they are activated. A potion or elixir must be swallowed, or an oil applied to the body. The writing vanishes from a scroll when it is read. Once used, a consumable item loses its magic and no longer functions."
  ]
}
```

### action:dmg:activate-an-item — entry 4

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[0]`
- Source entry path: `$.action[0].entries[4]`
- Canonical output: `action/dmg-activate-an-item.json`
- OMITTED: Item spell level, caster level, slot/component rules, concentration, and spell/item effect distinction — These behaviors depend on Spell and Item contracts and cannot be represented by this Action operation.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
{
  "type": "entries",
  "name": "Spells",
  "entries": [
    "Some magic items allow the user to cast a spell from the item, often by expending charges from it. The spell is cast at the lowest possible spell and caster level, doesn't expend any of the user's spell slots, and requires no components unless the item's description says otherwise. The spell uses its normal casting time, range, and duration, and the user of the item must concentrate if the spell requires {@status concentration}. Certain items make exceptions to these rules, changing the casting time, duration, or other parts of a spell.",
    "Many items, such as potions, bypass the casting of the spell and confer the spell's effects. Such an item still uses the spell's duration unless the item's description says otherwise.",
    "A magic item, such as certain staffs, may require you to use your own spellcasting ability when you cast a spell from the item. If you have more than one spellcasting ability, you choose which one to use with the item. If you don't have a spellcasting ability—perhaps you're a rogue with the Use Magic Device feature—your spellcasting ability modifier is +0 for the item, and your proficiency bonus does apply."
  ]
}
```

### action:dmg:activate-an-item — entry 5

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[0]`
- Source entry path: `$.action[0].entries[5]`
- Canonical output: `action/dmg-activate-an-item.json`
- OMITTED: Charge expenditure and identification/attunement disclosure — Charge state and Identify effects belong to Item and Spell domains.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
{
  "type": "entries",
  "name": "Charges",
  "entries": [
    "Some magic items have charges that you expend to activate its properties. The number of charges an item has remaining is revealed when an identify spell is cast on the item, or when a creature attunes to the item. Additionally, when an item regains charges, the creature attuned to that item learns how many charges it regained."
  ]
}
```

### action:xphb:attack — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[2]`
- Source entry path: `$.action[2].entries[1]`
- Canonical output: `action/xphb-attack.json`
- OMITTED: Equipping or unequipping one weapon before or after an attack — Weapon state changes belong to the future Equipment contract.; cause: `EXTERNAL_DEPENDENCY`
- OMITTED: Moving between attacks granted by features — Feature-granted attack sequencing belongs to future Class/feature and encounter execution models.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
{
  "type": "entries",
  "entries": [
    {
      "type": "entries",
      "name": "Equipping and Unequipping Weapons",
      "entries": [
        "You can either equip or unequip one weapon when you make an attack as part of this action. You do so either before or after the attack. If you equip a weapon before an attack, you don't need to use it for that attack. Equipping a weapon includes drawing it from a sheath or picking it up. Unequipping a weapon includes sheathing, stowing, or dropping it."
      ]
    },
    {
      "type": "entries",
      "name": "Moving Between Attacks",
      "entries": [
        "If you move on your turn and have a feature, such as Extra Attack, that gives you more than one attack as part of the Attack action, you can use some or all of that movement to move between those attacks."
      ]
    }
  ]
}
```

### action:dmg:climb-onto-a-bigger-creature — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[4]`
- Source entry path: `$.action[4].entries[0]`
- Canonical output: `action/climb-onto-a-bigger-creature.json`
- OMITTED: Qualitative size likelihood and the supernatural-might exception — The source gives qualitative likelihood rather than a deterministic size threshold or modifier; encoding it as a mechanical threshold would invent precision.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"If one creature wants to jump onto another creature, it can do so by {@action grapple||grappling}. A small or Medium creature has little chance of making a successful grapple against a Huge or Gargantuan creature, however, unless magic has granted the grappler supernatural might."
```

### action:dmg:healing-surge — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[19]`
- Source entry path: `$.action[19].entries[0]`
- Canonical output: `none`
- OMITTED: Complete source entry — Hit Die spending, rolling, healing, eligibility, and rest recovery require the deferred character-resource contract.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"As an action, a character can use a healing surge and spend up to half his or her Hit Dice. For each Hit Die spent in this way, the player rolls the die and adds the character's Constitution modifier. The character regains hit points equal to the total. The player can decide to spend an additional Hit Die after each roll."
```

### action:dmg:healing-surge — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[19]`
- Source entry path: `$.action[19].entries[1]`
- Canonical output: `none`
- OMITTED: Complete source entry — Hit Die spending, rolling, healing, eligibility, and rest recovery require the deferred character-resource contract.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"A character who uses a healing surge can't do so again until he or she finishes a short or long rest. Under this optional rule, a character regains all spent Hit Dice at the end of a long rest. With a short rest, a character regains Hit Dice equal to his or her level divided by four (minimum of one die)."
```

### action:xphb:help — entry 2

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[21]`
- Source entry path: `$.action[21].entries[2]`
- Canonical output: `action/xphb-help.json`
- OMITTED: The linked XPHB stabilization procedure and its death-save consequences — The XPHB dying-creature procedure depends on separately modeled death-save and stabilization mechanics.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"{@note Additionally, the Help action may be used to {@book stabilize a creature|XPHB|1|Stabilizing a Character}.}"
```

### action:phb:hide — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[22]`
- Source entry path: `$.action[22].entries[0]`
- Canonical output: `action/phb-hide.json`
- OMITTED: Success-dependent benefits defined by the PHB hiding and unseen-target procedures — Those Rule procedures are outside the approved Action contract and have no exact canonical target in the current normalized Rule set.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When you take the Hide action, you make a Dexterity ({@skill Stealth}) check in an attempt to hide, following the rules in {@book chapter 7|phb|7|hiding} for hiding. If you succeed, you gain certain benefits, as described in the \"{@book Unseen Attackers and Targets|PHB|9|unseen attackers and targets}\" section in the Player's Handbook."
```

### action:phb:improvising-an-action — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[25]`
- Source entry path: `$.action[25].entries[1]`
- Canonical output: `action/phb-improvising-an-action.json`
- OMITTED: DM decides feasibility and what roll, if any, determines success — This open-ended adjudication cannot be represented as a typed check procedure.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"When you describe an action not detailed elsewhere in the rules, the DM tells you whether that action is possible and what kind of roll you need to make, if any, to determine success or failure."
```

### action:xphb:improvising-an-action — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[26]`
- Source entry path: `$.action[26].entries[0]`
- Canonical output: `action/xphb-improvising-an-action.json`
- OMITTED: DM determines feasibility and which D20 Test, if any, is required — The Action contract cannot encode an open-ended adjudicated check.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Player characters and monsters can also do things not covered by other actions. Many class features and other abilities provide additional action options, and you can improvise other actions. When you describe an action not detailed elsewhere in the rules, the Dungeon Master tells you whether that action is possible and what kind of {@variantrule D20 Test|XPHB} you need to make, if any."
```

### action:phb:other-activity — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[32]`
- Source entry path: `$.action[32].entries[1]`
- Canonical output: `action/phb-other-activity.json`
- OMITTED: Brief speech and gestures during the turn — Brief communication is not an independently executable Action mechanic in the approved contract.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"You can communicate however you are able, through brief utterances and gestures, as you take your turn."
```

### action:phb:other-activity — entry 4

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[32]`
- Source entry path: `$.action[32].entries[4]`
- Canonical output: `action/phb-other-activity.json`
- OMITTED: DM may require an action for care or unusual obstacles — The source leaves Action requirements for unusual care or obstacles to DM adjudication; no fixed procedure is established.; cause: `OUT_OF_APPROVED_SCOPE`

Complete original source entry:

```json
"The DM might require you to use an action for any of these activities when it needs special care or when it presents an unusual obstacle. For instance, the DM could reasonably expect you to use an action to open a stuck door or turn a crank to lower a drawbridge."
```

### action:phb:ready — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[34]`
- Source entry path: `$.action[34].entries[0]`
- Canonical output: `action/phb-ready.json`
- OMITTED: Reaction response availability through the start of the next turn — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Sometimes you want to get the jump on a foe or wait for a particular circumstance before you act. To do so, you can take the Ready action on your turn, which lets you act using your reaction before the start of your next turn."
```

### action:phb:ready — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[34]`
- Source entry path: `$.action[34].entries[1]`
- Canonical output: `action/phb-ready.json`
- OMITTED: Perceivable trigger and selected action-or-movement response — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"First, you decide what perceivable circumstance will trigger your reaction. Then, you choose the action you will take in response to that trigger, or you choose to move up to your speed in response to it. Examples include \"If the cultist steps on the trapdoor, I'll pull the lever that opens it,\" and \"If the goblin steps next to me, I move away.\""
```

### action:phb:ready — entry 2

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[34]`
- Source entry path: `$.action[34].entries[2]`
- Canonical output: `action/phb-ready.json`
- OMITTED: Taking the reaction after the trigger or ignoring it; one reaction per round — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When the trigger occurs, you can either take your reaction right after the trigger finishes or ignore the trigger. Remember that you can take only one reaction per round."
```

### action:phb:ready — entry 3

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[34]`
- Source entry path: `$.action[34].entries[3]`
- Canonical output: `action/phb-ready.json`
- OMITTED: Readied spell casting, casting-time restriction, Concentration replacement/maintenance, failure, and spell dissipation — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When you ready a spell, you cast it as normal but hold its energy, which you release with your reaction when the trigger occurs. To be readied, a spell must have a casting time of 1 action, and holding onto the spell's magic requires {@status concentration} (explained in {@book chapter 10|phb|10|concentration}). If your {@status concentration} is broken, the spell dissipates without taking effect. For example, if you are {@status concentration||concentrating} on the web spell and ready magic missile, your web spell ends, and if you take damage before you release magic missile with your reaction, your {@status concentration} might be broken."
```

### action:phb:ready — entry 4

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[34]`
- Source entry path: `$.action[34].entries[4]`
- Canonical output: `action/phb-ready.json`
- OMITTED: Readied action expiration at the start of the next turn — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"You have until the start of your next turn to use a readied action."
```

### action:xphb:ready — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[35]`
- Source entry path: `$.action[35].entries[0]`
- Canonical output: `action/xphb-ready.json`
- OMITTED: Response uses Reaction before the next turn begins — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"You take the Ready action to wait for a particular circumstance before you act. To do so, you take this action on your turn, which lets you act by taking a {@variantrule Reaction|XPHB} before the start of your next turn."
```

### action:xphb:ready — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[35]`
- Source entry path: `$.action[35].entries[1]`
- Canonical output: `action/xphb-ready.json`
- OMITTED: Perceivable trigger and choice of action or movement response — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"First, you decide what perceivable circumstance will trigger your {@variantrule Reaction|XPHB}. Then, you choose the action you will take in response to that trigger, or you choose to move up to your {@variantrule Speed|XPHB} in response to it. Examples include \"If the cultist steps on the trapdoor, I'll pull the lever that opens it,\" and \"If the zombie steps next to me, I move away.\""
```

### action:xphb:ready — entry 2

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[35]`
- Source entry path: `$.action[35].entries[2]`
- Canonical output: `action/xphb-ready.json`
- OMITTED: Take the Reaction immediately after the trigger or ignore it — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When the trigger occurs, you can either take your {@variantrule Reaction|XPHB} right after the trigger finishes or ignore the trigger."
```

### action:xphb:ready — entry 3

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[35]`
- Source entry path: `$.action[35].entries[3]`
- Canonical output: `action/xphb-ready.json`
- OMITTED: Readied spell resource expenditure, Concentration duration, and dissipation if Concentration ends — Readied response selection, trigger timing, reaction execution, expiry, and spell lifecycle require deferred Ready/encounter and Spell procedures.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When you Ready a spell, you cast it as normal (expending any resources used to cast it) but hold its energy, which you release with your {@variantrule Reaction|XPHB} when the trigger occurs. To be readied, a spell must have a casting time of an action, and holding on to the spell's magic requires {@status Concentration|XPHB}, which you can maintain up to the start of your next turn. If your {@status Concentration|XPHB} is broken, the spell dissipates without taking effect."
```

### action:phb:stabilize-a-creature — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[40]`
- Source entry path: `$.action[40].entries[1]`
- Canonical output: `action/phb-stabilize-a-creature.json`
- OMITTED: Stable target stops death saves but remains Unconscious; damage ends stability; unhealed target regains 1 HP after 1d4 hours — Stable-state/death-save interactions and timed recovery are outside the Action contract.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"A stable creature doesn't make death saving throws, even though it has 0 hit points, but it does remain {@condition unconscious}. The creature stops being stable, and must start making death saving throws again, if it takes any damage. A stable creature that isn't healed regains 1 hit point after {@dice 1d4} hours."
```

### action:phb:two-weapon-fighting — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[43]`
- Source entry path: `$.action[43].entries[0]`
- Canonical output: `action/phb-two-weapon-fighting.json`
- OMITTED: Two different Light melee weapons held in different hands and omission of a nonnegative ability modifier from damage — Weapon properties, hand/equipment state, and attack damage adjustments depend on the deferred Equipment and attack-damage contracts.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When you take the {@action Attack} action and attack with a light melee weapon that you're holding in one hand, you can use a bonus action to attack with a different light melee weapon that you're holding in the other hand. You don't add your ability modifier to the damage of the bonus attack, unless that modifier is negative."
```

### action:phb:two-weapon-fighting — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[43]`
- Source entry path: `$.action[43].entries[1]`
- Canonical output: `action/phb-two-weapon-fighting.json`
- OMITTED: Conditional thrown option based on the weapon property — Weapon properties, hand/equipment state, and attack damage adjustments depend on the deferred Equipment and attack-damage contracts.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"If either weapon has the thrown property, you can throw the weapon, instead of making a melee attack with it."
```

### action:xphb:two-weapon-fighting — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[44]`
- Source entry path: `$.action[44].entries[0]`
- Canonical output: `action/xphb-two-weapon-fighting.json`
- OMITTED: On-turn prerequisite, Light weapon requirement, different weapon requirement, and the damage-ability modifier exception — Weapon properties, hand/equipment state, and attack damage adjustments depend on the deferred Equipment and attack-damage contracts.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"When you take the {@action Attack|XPHB} action on your turn and attack with a Light weapon, you can make one extra attack as a {@variantrule Bonus Action|XPHB} later on the same turn. That extra attack must be made with a different Light weapon, and you don't add your ability modifier to the extra attack's damage unless that modifier is negative. For example, you can attack with a {@item Shortsword|XPHB} in one hand and a {@item Dagger|XPHB} in the other using the {@action Attack|XPHB} action and a {@variantrule Bonus Action|XPHB}, but you don't add your Strength or Dexterity modifier to the damage roll of the {@variantrule Bonus Action|XPHB} unless that modifier is negative."
```

### action:xge:waking-someone — entry 0

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[47]`
- Source entry path: `$.action[47].entries[0]`
- Canonical output: `none`
- OMITTED: Complete source entry — Natural sleep, noise, damage, and passive-Perception wake conditions require a future sleep and encounter-state contract.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"A creature that is naturally sleeping, as opposed to being in a magically or chemically induced sleep, wakes up if it takes any damage or if someone else uses an action to shake or slap the creature awake. A sudden loud noise—such as yelling, thunder, or a ringing bell—also awakens someone that is sleeping naturally."
```

### action:xge:waking-someone — entry 1

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[47]`
- Source entry path: `$.action[47].entries[1]`
- Canonical output: `none`
- OMITTED: Complete source entry — Natural sleep, noise, damage, and passive-Perception wake conditions require a future sleep and encounter-state contract.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Whispers don't disturb sleep, unless a sleeper's passive Wisdom ({@skill Perception}) score is 20 or higher and the whispers are within 10 feet of the sleeper. Speech at a normal volume awakens a sleeper if the environment is otherwise silent (no wind, birdsong, crickets, street sounds, or the like) and the sleeper has a passive Wisdom ({@skill Perception}) score of 15 or higher."
```
