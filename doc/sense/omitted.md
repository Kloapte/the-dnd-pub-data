# Sense source content omitted during Step 2 Core Rules conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### sense:phb:blindsight — entry 0

- Source file: `src/main/resources/data/senses.json`
- Source object: `$.sense[0]`
- Source entry path: `$.sense[0].entries[0]`
- Canonical output: `sense/phb-blindsight.json`
- OMITTED: Creature examples and descriptive explanation of who may have Blindsight — These examples do not add a separate capability or procedure beyond the defined nonvisual sense.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"A creature with blindsight can perceive its surroundings without relying on sight, within a specific radius. Creatures without eyes, such as oozes, and creatures with echolocation or heightened senses, such as bats and true dragons, have this sense."
```

### sense:xphb:blindsight — entry 0

- Source file: `src/main/resources/data/senses.json`
- Source object: `$.sense[1]`
- Source entry path: `$.sense[1].entries[0]`
- Canonical output: `sense/xphb-blindsight.json`
- OMITTED: The exclusion for targets behind Total Cover — The approved Sense contract has no typed cover obstruction applicability.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
"If you have Blindsight, you can see within a specific range without relying on physical sight. Within that range, you can see anything that isn't behind {@variantrule Cover|XPHB|Total Cover} even if you have the {@condition Blinded|XPHB} condition or are in {@variantrule Darkness|XPHB}. Moreover, in that range, you can see something that has the {@condition Invisible|XPHB} condition."
```

### sense:xphb:truesight — entry 0

- Source file: `src/main/resources/data/senses.json`
- Source object: `$.sense[7]`
- Source entry path: `$.sense[7].entries[0]`
- Canonical output: `sense/xphb-truesight.json`
- OMITTED: Introductory statement that Truesight enhances vision within a specified range — The introduction contains no distinct capability beyond the listed behaviors; the range is creature-specific and excluded from Sense.; cause: `NON_MECHANICAL`

Complete original source entry:

```json
"If you have Truesight, your vision is enhanced within a specified range. Within that range, your vision pierces through the following:"
```

### sense:xphb:truesight — entry 1

- Source file: `src/main/resources/data/senses.json`
- Source object: `$.sense[7]`
- Source entry path: `$.sense[7].entries[1]`
- Canonical output: `sense/xphb-truesight.json`
- OMITTED: Seeing in normal and magical Darkness — The approved Sense contract does not distinguish normal from magical Darkness and has no Darkness-penetration capability that preserves this distinction.; cause: `SCHEMA_UNSUPPORTED`

Complete original source entry:

```json
{
  "type": "list",
  "style": "list-hang-notitle",
  "items": [
    {
      "type": "item",
      "name": "Darkness",
      "entries": [
        "You can see in normal and magical {@variantrule Darkness|XPHB}."
      ]
    },
    {
      "type": "item",
      "name": "Invisibility",
      "entries": [
        "You see creatures and objects that have the {@condition Invisible|XPHB} condition."
      ]
    },
    {
      "type": "item",
      "name": "Visual Illusions",
      "entries": [
        "Visual illusions appear transparent to you, and you automatically succeed on {@variantrule Saving Throw|XPHB|saving throws} against them."
      ]
    },
    {
      "type": "item",
      "name": "Transformations",
      "entries": [
        "You discern the true form of any creature or object you see that has been transformed by magic."
      ]
    },
    {
      "type": "item",
      "name": "Ethereal Plane",
      "entries": [
        "You see into the Ethereal Plane."
      ]
    }
  ]
}
```
