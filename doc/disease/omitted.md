# Disease source content omitted during Step 2 Phase 3 representative conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### disease:phb:blinding-sickness — entry 0

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[1]`
- Source entry path: `$.disease[1].entries[0]`
- Canonical output: `disease/phb-blinding-sickness.json`
- OMITTED: Pain and the appearance of milky-white eyes. — The condition and roll modifiers are represented; descriptive symptoms have no approved Disease field.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"Pain grips the creature's mind, and its eyes turn milky white. The creature has disadvantage on Wisdom checks and Wisdom saving throws and is {@condition blinded}."
```

### disease:xdmg:sewer-plague — entry 0

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[18]`
- Source entry path: `$.disease[18].entries[0]`
- Canonical output: `disease/xdmg-sewer-plague.json`
- OMITTED: Humanoid-only target, contaminated source qualification, environmental origin, and 1d4-day incubation — The approved Disease schema has no target-susceptibility predicate or random-die incubation interval.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Fouled potions and alchemical waste can give rise to Sewer Plague, which incubates in sewers and refuse heaps and is sometimes transmitted by creatures that dwell in such areas, including otyughs and rats. Any Humanoid that is wounded by a creature that carries the contagion or that comes into contact with contaminated filth or offal must succeed on a {@dc 11} Constitution saving throw or become infected with Sewer Plague. A creature suffers the following effects {@dice 1d4} days after infection:"
```

### disease:xdmg:sewer-plague — entry 1

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[18]`
- Source entry path: `$.disease[18].entries[1]`
- Canonical output: `disease/xdmg-sewer-plague.json`
- OMITTED: Exactly one level after incubation, Hit Point Dice recovery reduction, and Long Rest restrictions while Exhaustion is present — The Disease schema cannot express condition-level application, symptom timing, or these condition-dependent recovery restrictions.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "list",
  "style": "list-hang-notitle",
  "items": [
    {
      "type": "item",
      "name": "Fatigue",
      "entry": "The creature gains 1 {@condition Exhaustion|XPHB} level."
    },
    {
      "type": "item",
      "name": "Weakness",
      "entry": "While the creature has any Exhaustion levels, it regains only half the normal number of Hit Points from spending Hit Point Dice."
    },
    {
      "type": "item",
      "name": "Restlessness",
      "entry": "While the creature has any Exhaustion levels, finishing a Long Rest neither restores lost Hit Points nor reduces the creature's Exhaustion level."
    }
  ]
}
```

### disease:xdmg:sewer-plague — entry 2

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[18]`
- Source entry path: `$.disease[18].entries[2]`
- Canonical output: `disease/xdmg-sewer-plague.json`
- OMITTED: Daily dawn Constitution saves, conditional Exhaustion increase/decrease, and disease ending only at Exhaustion level zero — Progression has no condition-level change or conditional threshold outcome; mapping success directly to disease termination would be inaccurate.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "entries",
  "entries": [
    {
      "type": "entries",
      "name": "Fighting the Contagion",
      "entries": [
        "Daily at dawn, an infected creature makes a {@dc 11} Constitution saving throw. On a failed save, the creature gains 1 {@condition Exhaustion|XPHB} level as its fatigue worsens. On a successful save, the creature's Exhaustion level decreases by 1. If the creature's Exhaustion level is reduced to 0, the contagion ends on the creature."
      ]
    }
  ]
}
```

### disease:dmg:cackle-fever — entry 0

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[4]`
- Source entry path: `$.disease[4].entries[0]`
- Canonical output: `none`
- OMITTED: complete source entry — Humanoid susceptibility, gnome immunity, laughter description, and nickname are not represented by Disease fields.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"This disease targets humanoids, although gnomes are strangely immune. While in the grips of this disease, victims frequently succumb to fits of mad laughter, giving the disease its common name and its morbid nickname: \"the shrieks.\""
```

### disease:dmg:cackle-fever — entry 1

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[4]`
- Source entry path: `$.disease[4].entries[1]`
- Canonical output: `none`
- OMITTED: complete source entry — Random 1d4-hour incubation, fever/disorientation, and Exhaustion that cannot be removed until cure are not expressible together in the approved schema.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Symptoms manifest {@dice 1d4} hours after infection and include fever and disorientation. The infected creature gains one level of {@condition exhaustion} that can't be removed until the disease is cured."
```

### disease:dmg:cackle-fever — entry 2

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[4]`
- Source entry path: `$.disease[4].entries[2]`
- Canonical output: `none`
- OMITTED: complete source entry — Stress-triggered DC 13 Constitution saving throw is unsupported as a Disease trigger.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Any event that causes the infected creature great stress—including entering combat, taking damage, experiencing fear, or having a nightmare—forces the creature to make a {@dc 13} Constitution saving throw."
```

### disease:dmg:cackle-fever — entry 3

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[4]`
- Source entry path: `$.disease[4].entries[3]`
- Canonical output: `none`
- OMITTED: complete source entry — Save failure damage, timed incapacitation/laughter, repeated end-of-turn saves, secondary infection, and 24-hour source-specific immunity are unsupported.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"On a failed save, the creature takes 5 ({@dice 1d10}) psychic damage and becomes {@condition incapacitated} with mad laughter for 1 minute. The creature can repeat the saving throw at the end of each of its turns, ending the mad laughter and the {@condition incapacitated} condition on a success. Any humanoid creature that starts its turn within 10 feet of an infected creature in the throes of mad laughter must succeed on a {@dc 10} Constitution saving throw or also become infected with the disease. Once a creature succeeds on this save, it is immune to the mad laughter of that particular infected creature for 24 hours."
```

### disease:dmg:cackle-fever — entry 4

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.disease[4]`
- Source entry path: `$.disease[4].entries[4]`
- Canonical output: `none`
- OMITTED: complete source entry — Long-rest save, random DC reduction, recovery threshold, and indefinite-madness consequence after three failures are unsupported.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"At the end of each long rest, an infected creature can make a {@dc 13} Constitution saving throw. On a successful save, the DC for this save and for the save to avoid an attack of mad laughter drops by {@dice 1d6}. When the saving throw DC drops to 0, the creature recovers from the disease. A creature that fails three of these saving throws gains a randomly determined form of {@variantrule Madness|DMG|indefinite madness}."
```
