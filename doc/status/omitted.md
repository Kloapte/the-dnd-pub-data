# Status source content omitted during Step 2 Phase 3 representative conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### status:phb:concentration — entry 0

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.status[1]`
- Source entry path: `$.status[1].entries[0]`
- Canonical output: `status/phb-concentration.json`
- OMITTED: The maintained spell itself ends when Concentration is lost. — The approved Status model records status loss but does not link the lost state to each dependent spell effect.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Some spells require you to maintain {@status concentration} in order to keep their magic active. If you lose {@status concentration}, such a spell ends."
```

### status:phb:concentration — entry 1

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.status[1]`
- Source entry path: `$.status[1].entries[1]`
- Canonical output: `status/phb-concentration.json`
- OMITTED: The maximum Concentration duration is specified by the individual spell. — The Status record has no general duration field; spell-specific duration remains on the referenced spell.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"If a spell must be maintained with {@status concentration}, that fact appears in its Duration entry, and the spell specifies how long you can concentrate on it. You can end {@status concentration} at any time (no action required)."
```

### status:phb:concentration — entry 2

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.status[1]`
- Source entry path: `$.status[1].entries[2]`
- Canonical output: `status/phb-concentration.json`
- OMITTED: complete source entry — Normal movement and attacks do not interrupt Concentration; the approved Status model does not contain continuing-activity permissions.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"Normal activity, such as moving and attacking, doesn't interfere with {@status concentration}. The following factors can break {@status concentration}:"
```

### status:xphb:concentration — entry 0

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.status[2]`
- Source entry path: `$.status[2].entries[0]`
- Canonical output: `status/xphb-concentration.json`
- OMITTED: The example list of possible maximum durations (one minute, one hour, or another duration). — Status contains no general duration field; durations are defined by the effect record.; cause: `EXTERNAL_DEPENDENCY`

Complete original source entry:

```json
"Some spells and other effects require Concentration to remain active, as specified in their descriptions. If the effect's creator loses Concentration, the effect ends. If the effect has a maximum duration, the effect's description specifies how long the creator can concentrate on it: up to 1 minute, 1 hour, or some other duration. The creator can end Concentration at any time (no action required). The following factors break Concentration."
```

### status:phb:surprised — entry 0

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.status[3]`
- Source entry path: `$.status[3].entries[0]`
- Canonical output: `status/phb-surprised.json`
- OMITTED: complete source entry — Narrative examples introduce surprise but do not define a separate mechanic.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"A band of adventurers sneaks up on a bandit camp, springing from the trees to attack them. A {@creature gelatinous cube} glides down a dungeon passage, unnoticed by the adventurers until the cube engulfs one of them. In these situations, one side of the battle gains surprise over the other."
```

### status:phb:surprised — entry 1

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.status[3]`
- Source entry path: `$.status[3].entries[1]`
- Canonical output: `status/phb-surprised.json`
- OMITTED: DM determination using hiding creaturesÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ Dexterity (Stealth) checks against each opposing creatureÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢s passive Wisdom (Perception). — The approved Status model represents resulting turn restrictions but not the perception/Stealth determination procedure.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"The DM determines who might be surprised. If neither side tries to be stealthy, they automatically notice each other. Otherwise, the DM compares the Dexterity ({@skill Stealth}) checks of anyone hiding with the passive Wisdom ({@skill Perception}) score of each creature on the opposing side. Any character or monster that doesn't notice a threat is {@status surprised} at the start of the encounter."
```
