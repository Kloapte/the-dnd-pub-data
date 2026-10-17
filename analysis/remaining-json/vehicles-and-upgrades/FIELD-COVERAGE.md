# Vehicles and Vehicle Upgrades Field Coverage

The source file fingerprint and complete path/type inventory are in [`source-audit.json`](source-audit.json). Each row below is an observed path/type occurrence count across the indicated root. Arrays use `[]`; numeric keys in `actionThresholds` are fixed authored positions.

## Root collections

| Root | Count | Sources |
|---|---:|---|
| `vehicle` | 39 | DMG (1), XDMG (1), MTF (3), AI (2), AAG (16), BGDIA (4), GoS (6), EFA (3), DD (1), DitLCoT (1), VEoR (1) |
| `vehicleUpgrade` | 31 | BGDIA (9), GoS (22) |

## Observed paths and JSON types

### `vehicle`

| Path | JSON type | Occurrences |
|---|---|---:|
| `$.ac[].ac` | number | 1 |
| `$.ac[].from[]` | string | 1 |
| `$.ac[].from` | array | 1 |
| `$.ac[]` | object | 1 |
| `$.ac` | array | 1 |
| `$.ac` | number | 3 |
| `$.action[].entries[]` | string | 3 |
| `$.action[].entries` | array | 3 |
| `$.action[].items[].entry` | string | 19 |
| `$.action[].items[].name` | string | 19 |
| `$.action[].items[].type` | string | 19 |
| `$.action[].items[]` | object | 19 |
| `$.action[].items` | array | 8 |
| `$.action[].name` | string | 3 |
| `$.action[].style` | string | 8 |
| `$.action[].type` | string | 8 |
| `$.action[]` | object | 11 |
| `$.action[]` | string | 8 |
| `$.action` | array | 9 |
| `$.actionStation[].entries[]` | string | 13 |
| `$.actionStation[].entries` | array | 13 |
| `$.actionStation[].name` | string | 13 |
| `$.actionStation[]` | object | 13 |
| `$.actionStation` | array | 5 |
| `$.actionThresholds.0` | number | 8 |
| `$.actionThresholds.1` | number | 8 |
| `$.actionThresholds.2` | number | 5 |
| `$.actionThresholds.3` | number | 4 |
| `$.actionThresholds` | object | 8 |
| `$.capCargo` | number | 32 |
| `$.capCargo` | string | 1 |
| `$.capCreature` | number | 5 |
| `$.capCrew` | number | 32 |
| `$.capCrewNote` | string | 2 |
| `$.capPassenger` | number | 16 |
| `$.cha` | number | 14 |
| `$.con` | number | 14 |
| `$.conditionImmune[]` | string | 150 |
| `$.conditionImmune` | array | 14 |
| `$.control[].ac` | number | 8 |
| `$.control[].entries[]` | string | 8 |
| `$.control[].entries` | array | 8 |
| `$.control[].hp` | number | 8 |
| `$.control[].name` | string | 8 |
| `$.control[]` | object | 8 |
| `$.control` | array | 7 |
| `$.cost` | number | 18 |
| `$.dex` | number | 14 |
| `$.dimensions[]` | string | 56 |
| `$.dimensions` | array | 28 |
| `$.entries[].caption` | string | 2 |
| `$.entries[].colLabels[]` | string | 6 |
| `$.entries[].colLabels` | array | 2 |
| `$.entries[].colStyles[]` | string | 6 |
| `$.entries[].colStyles` | array | 2 |
| `$.entries[].rows[][]` | string | 60 |
| `$.entries[].rows[]` | array | 20 |
| `$.entries[].rows` | array | 2 |
| `$.entries[].type` | string | 2 |
| `$.entries[]` | object | 2 |
| `$.entries[]` | string | 10 |
| `$.entries` | array | 3 |
| `$.hasFluff` | boolean | 34 |
| `$.hasFluffImages` | boolean | 31 |
| `$.hasToken` | boolean | 39 |
| `$.hp.average` | number | 1 |
| `$.hp.dt` | number | 5 |
| `$.hp.formula` | string | 1 |
| `$.hp.hp` | number | 5 |
| `$.hp.mt` | number | 4 |
| `$.hp` | number | 2 |
| `$.hp` | object | 6 |
| `$.hull.ac` | number | 27 |
| `$.hull.acFrom[]` | string | 16 |
| `$.hull.acFrom` | array | 16 |
| `$.hull.dt` | number | 26 |
| `$.hull.hp` | number | 27 |
| `$.hull` | object | 27 |
| `$.immune[]` | string | 37 |
| `$.immune` | array | 16 |
| `$.int` | number | 14 |
| `$.movement[].ac` | number | 12 |
| `$.movement[].hp` | number | 12 |
| `$.movement[].hpNote` | string | 11 |
| `$.movement[].isControl` | boolean | 1 |
| `$.movement[].name` | string | 12 |
| `$.movement[].speed[].entries[]` | string | 13 |
| `$.movement[].speed[].entries` | array | 12 |
| `$.movement[].speed[].mode` | string | 12 |
| `$.movement[].speed[]` | object | 12 |
| `$.movement[].speed` | array | 12 |
| `$.movement[]` | object | 12 |
| `$.movement` | array | 8 |
| `$.name` | string | 39 |
| `$.otherSources[].source` | string | 2 |
| `$.otherSources[]` | object | 2 |
| `$.otherSources` | array | 2 |
| `$.pace.fly` | number | 10 |
| `$.pace.fly` | string | 9 |
| `$.pace.swim` | number | 1 |
| `$.pace.walk` | string | 1 |
| `$.pace` | number | 11 |
| `$.pace` | object | 19 |
| `$.page` | number | 39 |
| `$.reaction[].entries[]` | string | 2 |
| `$.reaction[].entries` | array | 2 |
| `$.reaction[].name` | string | 2 |
| `$.reaction[].type` | string | 2 |
| `$.reaction[]` | object | 2 |
| `$.reaction` | array | 2 |
| `$.reprintedAs[]` | string | 1 |
| `$.reprintedAs` | array | 1 |
| `$.size[]` | string | 4 |
| `$.size` | array | 4 |
| `$.size` | string | 16 |
| `$.source` | string | 39 |
| `$.speed.canHover` | boolean | 3 |
| `$.speed.fly.condition` | string | 3 |
| `$.speed.fly.number` | number | 3 |
| `$.speed.fly` | number | 16 |
| `$.speed.fly` | object | 3 |
| `$.speed.swim.condition` | string | 3 |
| `$.speed.swim.number` | number | 3 |
| `$.speed.swim` | number | 1 |
| `$.speed.swim` | object | 3 |
| `$.speed.walk` | number | 5 |
| `$.speed` | number | 5 |
| `$.speed` | object | 23 |
| `$.srd` | string | 2 |
| `$.station[].ac` | number | 4 |
| `$.station[].action[].entries[]` | string | 8 |
| `$.station[].action[].entries` | array | 8 |
| `$.station[].action[].name` | string | 8 |
| `$.station[].action[]` | object | 8 |
| `$.station[].action` | array | 4 |
| `$.station[].crew` | number | 4 |
| `$.station[].entries[].entries[].entries[]` | string | 4 |
| `$.station[].entries[].entries[].entries` | array | 4 |
| `$.station[].entries[].entries[].name` | string | 4 |
| `$.station[].entries[].entries[].type` | string | 4 |
| `$.station[].entries[].entries[]` | object | 4 |
| `$.station[].entries[].entries` | array | 4 |
| `$.station[].entries[].type` | string | 4 |
| `$.station[].entries[]` | object | 4 |
| `$.station[].entries[]` | string | 8 |
| `$.station[].entries` | array | 4 |
| `$.station[].hp` | number | 4 |
| `$.station[].name` | string | 4 |
| `$.station[].size[]` | string | 4 |
| `$.station[].size` | array | 4 |
| `$.station[]` | object | 4 |
| `$.station` | array | 3 |
| `$.str` | number | 14 |
| `$.terrain[]` | string | 52 |
| `$.terrain` | array | 39 |
| `$.tokenCredit` | string | 5 |
| `$.tokenCustom` | boolean | 5 |
| `$.trait[].entries[]` | string | 14 |
| `$.trait[].entries` | array | 14 |
| `$.trait[].name` | string | 14 |
| `$.trait[]` | object | 14 |
| `$.trait` | array | 5 |
| `$.type` | string | 1 |
| `$.vehicleType` | string | 39 |
| `$.weapon[].ac` | number | 41 |
| `$.weapon[].action[].entries[]` | string | 38 |
| `$.weapon[].action[].entries` | array | 38 |
| `$.weapon[].action[].name` | string | 38 |
| `$.weapon[].action[]` | object | 38 |
| `$.weapon[].action` | array | 36 |
| `$.weapon[].costs[].cost` | number | 41 |
| `$.weapon[].costs[].note` | string | 59 |
| `$.weapon[].costs[]` | object | 59 |
| `$.weapon[].costs` | array | 33 |
| `$.weapon[].count` | number | 22 |
| `$.weapon[].crew` | number | 32 |
| `$.weapon[].dt` | number | 2 |
| `$.weapon[].entries[].items[].entry` | string | 3 |
| `$.weapon[].entries[].items[].name` | string | 3 |
| `$.weapon[].entries[].items[].type` | string | 3 |
| `$.weapon[].entries[].items[]` | object | 3 |
| `$.weapon[].entries[].items` | array | 1 |
| `$.weapon[].entries[].style` | string | 1 |
| `$.weapon[].entries[].type` | string | 1 |
| `$.weapon[].entries[]` | object | 1 |
| `$.weapon[].entries[]` | string | 53 |
| `$.weapon[].entries` | array | 51 |
| `$.weapon[].hp` | number | 41 |
| `$.weapon[].name` | string | 51 |
| `$.weapon[].size[]` | string | 3 |
| `$.weapon[].size` | array | 3 |
| `$.weapon[]` | object | 51 |
| `$.weapon` | array | 27 |
| `$.weight` | number | 5 |
| `$.wis` | number | 14 |
| `$` | object | 39 |

### `vehicleUpgrade`

| Path | JSON type | Occurrences |
|---|---|---:|
| `$.entries[].caption` | string | 1 |
| `$.entries[].colLabels[]` | string | 2 |
| `$.entries[].colLabels` | array | 1 |
| `$.entries[].colStyles[]` | string | 2 |
| `$.entries[].colStyles` | array | 1 |
| `$.entries[].entries[]` | string | 4 |
| `$.entries[].entries` | array | 4 |
| `$.entries[].name` | string | 4 |
| `$.entries[].rows[][]` | string | 20 |
| `$.entries[].rows[]` | array | 10 |
| `$.entries[].rows` | array | 1 |
| `$.entries[].type` | string | 5 |
| `$.entries[]` | object | 5 |
| `$.entries[]` | string | 34 |
| `$.entries` | array | 31 |
| `$.name` | string | 31 |
| `$.page` | number | 31 |
| `$.source` | string | 31 |
| `$.upgradeType[]` | string | 31 |
| `$.upgradeType` | array | 31 |
| `$` | object | 31 |

## Optional and heterogeneous fields

- All 70 definitions have `name`, `source`, and `page`; no explicit nulls or empty strings, arrays, or objects occur. Optional fields are absent from some records.
- Vehicle `size` is a string (16) or string array (4); `ac` is an integer (3) or array (1); `hp` is an integer (2) or object (6); `speed` is integer (5) or object (23); `pace` is integer (11) or object (19); `capCargo` is numeric (32) or string (1). Four numeric cargo capacities are fractional: three `0.5` and one `0.25`; they are retained as `BigDecimal`.
- `action` entries include 8 strings and 11 objects. Entry arrays and nested action/component entries use strings or typed objects; common entry discriminators observed are `entries`, `list`, `item`, and `table`.
- Upgrade `upgradeType` is always an array of strings. The observed codes are `IWM:W`, `SHP:W`, `SHP:O`, `IWM:A`, `SHP:H`, `SHP:M`, `SHP:F`, and `IWM:G`. Vehicle classifications are `OBJECT`, `SHIP`, `SPELLJAMMER`, `INFWAR`, `ELEMENTAL_AIRSHIP`, and `CREATURE`; terrain labels are source strings (`land`, `sea`, `air`, `space`).
- Vehicle `actionThresholds` is an object with numeric keys `0` through `3`; 0 and 1 are present in all 8 such objects, 2 in 5, and 3 in 4. It is modeled as a fixed typed four-value structure. Vehicle `srd` is a string in two records. `size`, AC, HP, speed, pace, cargo capacity, and action variants use explicit typed alternatives.
- No `_copy` or `_versions` declaration occurs. One vehicle has `reprintedAs`; two vehicles have `otherSources`. Upgrades have neither. Media-related root flags (`hasToken`, `hasFluff`, `hasFluffImages`, `tokenCredit`, `tokenCustom`) are retained internally and are not public. No explicit false boolean values occur in the corpus.
- `vehicleType` values are `OBJECT`, `SHIP`, `SPELLJAMMER`, `INFWAR`, `ELEMENTAL_AIRSHIP`, and `CREATURE`. Terrain values are `land`, `sea`, `air`, and `space`. Upgrade type values are `IWM:W`, `SHP:W`, `SHP:O`, `IWM:A`, `SHP:H`, `SHP:M`, `SHP:F`, and `IWM:G`.
- The only authored defense fields are `immune` and `conditionImmune`; no vulnerability or resistance field occurs. `cost` is numeric without a currency property. No separate vehicle-to-upgrade compatibility property occurs.
- Observed embedded markup tag counts and exact resolver references are recorded in the reference inventory. Descriptive entry content is typed and retained internally but not included in public DTOs.
