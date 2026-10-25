# Condition source content duplicate during Step 2 Core Rules conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### condition:phb:exhaustion — entry 3

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.condition[6]`
- Source entry path: `$.condition[6].entries[3]`
- Canonical output: `condition/phb-exhaustion.json`
- DUPLICATE: complete source entry — The current level includes all lower levels, already represented by cumulative level mechanics and the threshold effects.

Complete original source entry:

```json
"A creature suffers the effect of its current level of exhaustion as well as all lower levels. For example, a creature suffering level 2 exhaustion has its speed halved and has disadvantage on ability checks."
```

### condition:phb:exhaustion — entry 4

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.condition[6]`
- Source entry path: `$.condition[6].entries[4]`
- Canonical output: `condition/phb-exhaustion.json`
- DUPLICATE: complete source entry — All effects end below level 1, already represented by endsAtLevel=0 and each effect threshold.

Complete original source entry:

```json
"An effect that removes exhaustion reduces its level as specified in the effect's description, with all exhaustion effects ending if a creature's exhaustion level is reduced below 1."
```

### condition:xphb:incapacitated — entry 1

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.condition[13]`
- Source entry path: `$.condition[13].entries[1]`
- Canonical output: `condition/xphb-incapacitated.json`
- DUPLICATE: Concentration ends while Incapacitated. — The approved Concentration Status already ends on Incapacitation; the Condition does not duplicate that lifecycle operation.

Complete original source entry:

```json
{
  "type": "entries",
  "entries": [
    {
      "type": "entries",
      "name": "Inactive",
      "entries": [
        "You can't take any {@variantrule Action|XPHB|action}, {@variantrule Bonus Action|XPHB}, or {@variantrule Reaction|XPHB}."
      ]
    },
    {
      "type": "entries",
      "name": "No Concentration",
      "entries": [
        "Your {@status Concentration|XPHB} is broken."
      ]
    },
    {
      "type": "entries",
      "name": "Speechless",
      "entries": [
        "You can't speak."
      ]
    },
    {
      "type": "entries",
      "name": "Surprised",
      "entries": [
        "If you're Incapacitated when you roll {@variantrule Initiative|XPHB}, you have {@variantrule Disadvantage|XPHB} on the roll."
      ]
    }
  ]
}
```
