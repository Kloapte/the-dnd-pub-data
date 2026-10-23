# Action source content omitted during Step 2 Phase 3 representative conversion

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
- OMITTED: Exactly one attack, the Making an Attack procedure, and its separately defined resolution — The approved Action schema has no attack-count field or complete attack procedure; the referenced book section has no exact standalone canonical identity in this selection.; cause: `SCHEMA_UNSUPPORTED`

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

### action:phb:grapple — entry 2

- Source file: `src/main/resources/data/actions.json`
- Source object: `$.action[18]`
- Source entry path: `$.action[18].entries[2]`
- Canonical output: `action/phb-grapple.json`
- OMITTED: complete source entry — Dragging or carrying the Grappled target while moving and the half-speed exception for a target at least two sizes smaller are not expressible by the approved Action movement operations.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "entries",
  "name": "Moving a Grappled Target",
  "entries": [
    "When you move, you can drag or carry the {@condition grappled} creature with you, but your speed is halved, unless the creature is two or more sizes smaller than you."
  ]
}
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
