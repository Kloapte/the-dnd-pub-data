# Trap and Hazard Field Coverage

Pinned input: `src/main/resources/data/trapshazards.json` (SHA-256 `17c1b0e10e8b6ad078c41324ccc8ed1e07805e90aa017d27a8e1ed4f5b25d244`). Root arrays `trap` (37) and `hazard` (73) are deserialized together. Optional fields are absent where not authored; the corpus uses no dynamic property-key maps.

Counts below are JSON node observations, not definition counts. Entries deliberately share a recursive path notation. No explicit `null`, empty string, empty array, `false`, or numeric zero occurs anywhere in either root corpus; the typed models still preserve nullable Boolean and numeric values if later source versions author them.

## Trap

| Field path | Present nodes | JSON types |
|---|---:|---|
| `trap` | 1 | array 1 |
| `trap[]` | 37 | object 37 |
| `trap[].name` | 37 | string 37 |
| `trap[].source` | 37 | string 37 |
| `trap[].page` | 37 | number 37 |
| `trap[].trapHazType` | 37 | string 37 |
| `trap[].rating` | 25 | array 25 |
| `trap[].rating[]` | 26 | object 26 |
| `trap[].rating[].tier` | 26 | number 26 |
| `trap[].rating[].threat` | 26 | string 26 |
| `trap[].entries` | 37 | array 37 |
| `trap[].entries[]` | 112 | object 42, string 70 |
| `trap[].entries[].type` | 42 | string 42 |
| `trap[].entries[].entries` | 42 | array 42 |
| `trap[].entries[].entries[]` | 60 | string 54, object 6 |
| `trap[].entries[].name` | 39 | string 39 |
| `trap[].hasFluffImages` | 5 | boolean 5 |
| `trap[].effect` | 10 | array 10 |
| `trap[].effect[]` | 10 | string 10 |
| `trap[].trigger` | 22 | array 22 |
| `trap[].trigger[]` | 22 | string 22 |
| `trap[].countermeasures` | 14 | array 14 |
| `trap[].countermeasures[]` | 18 | string 14, object 4 |
| `trap[].srd` | 8 | boolean 8 |
| `trap[].reprintedAs` | 7 | array 7 |
| `trap[].reprintedAs[]` | 8 | string 8 |
| `trap[].srd52` | 8 | boolean 8 |
| `trap[].duration` | 8 | array 8 |
| `trap[].duration[]` | 8 | object 8 |
| `trap[].duration[].type` | 8 | string 8 |
| `trap[].entries[].entries[].type` | 6 | string 6 |
| `trap[].entries[].entries[].colLabels` | 5 | array 5 |
| `trap[].entries[].entries[].colLabels[]` | 15 | string 15 |
| `trap[].entries[].entries[].colStyles` | 5 | array 5 |
| `trap[].entries[].entries[].colStyles[]` | 15 | string 15 |
| `trap[].entries[].entries[].rows` | 5 | array 5 |
| `trap[].entries[].entries[].rows[]` | 15 | array 15 |
| `trap[].entries[].entries[].rows[][]` | 45 | string 45 |
| `trap[].hauntBonus` | 4 | string 4 |
| `trap[].initiative` | 4 | number 4 |
| `trap[].eActive` | 4 | array 4 |
| `trap[].eActive[]` | 8 | string 4, object 4 |
| `trap[].eActive[].type` | 4 | string 4 |
| `trap[].eActive[].style` | 4 | string 4 |
| `trap[].eActive[].items` | 4 | array 4 |
| `trap[].eActive[].items[]` | 9 | object 9 |
| `trap[].eActive[].items[].type` | 9 | string 9 |
| `trap[].eActive[].items[].name` | 9 | string 9 |
| `trap[].eActive[].items[].entry` | 8 | string 8 |
| `trap[].eDynamic` | 4 | array 4 |
| `trap[].eDynamic[]` | 8 | string 4, object 4 |
| `trap[].eDynamic[].type` | 4 | string 4 |
| `trap[].eDynamic[].style` | 4 | string 4 |
| `trap[].eDynamic[].items` | 4 | array 4 |
| `trap[].eDynamic[].items[]` | 7 | object 7 |
| `trap[].eDynamic[].items[].type` | 7 | string 7 |
| `trap[].eDynamic[].items[].name` | 7 | string 7 |
| `trap[].eDynamic[].items[].entry` | 7 | string 7 |
| `trap[].eConstant` | 2 | array 2 |
| `trap[].eConstant[]` | 4 | string 2, object 2 |
| `trap[].eConstant[].type` | 2 | string 2 |
| `trap[].eConstant[].style` | 2 | string 2 |
| `trap[].eConstant[].items` | 2 | array 2 |
| `trap[].eConstant[].items[]` | 4 | object 4 |
| `trap[].eConstant[].items[].type` | 4 | string 4 |
| `trap[].eConstant[].items[].name` | 4 | string 4 |
| `trap[].eConstant[].items[].entry` | 4 | string 4 |
| `trap[].countermeasures[].type` | 4 | string 4 |
| `trap[].countermeasures[].style` | 4 | string 4 |
| `trap[].countermeasures[].items` | 4 | array 4 |
| `trap[].countermeasures[].items[]` | 17 | object 17 |
| `trap[].countermeasures[].items[].type` | 17 | string 17 |
| `trap[].countermeasures[].items[].name` | 17 | string 17 |
| `trap[].countermeasures[].items[].entry` | 16 | string 16 |
| `trap[].eActive[].items[].entries` | 1 | array 1 |
| `trap[].eActive[].items[].entries[]` | 2 | string 1, object 1 |
| `trap[].eActive[].items[].entries[].type` | 1 | string 1 |
| `trap[].eActive[].items[].entries[].caption` | 1 | string 1 |
| `trap[].eActive[].items[].entries[].colLabels` | 1 | array 1 |
| `trap[].eActive[].items[].entries[].colLabels[]` | 2 | string 2 |
| `trap[].eActive[].items[].entries[].colStyles` | 1 | array 1 |
| `trap[].eActive[].items[].entries[].colStyles[]` | 2 | string 2 |
| `trap[].eActive[].items[].entries[].rows` | 1 | array 1 |
| `trap[].eActive[].items[].entries[].rows[]` | 6 | array 6 |
| `trap[].eActive[].items[].entries[].rows[][]` | 12 | string 12 |
| `trap[].duration[].condition` | 1 | string 1 |
| `trap[].initiativeNote` | 1 | string 1 |
| `trap[].countermeasures[].items[].entries` | 1 | array 1 |
| `trap[].countermeasures[].items[].entries[]` | 3 | string 3 |
| `trap[].entries[].entries[].entries` | 1 | array 1 |
| `trap[].entries[].entries[].entries[]` | 2 | string 2 |

## Hazard

| Field path | Present nodes | JSON types |
|---|---:|---|
| `hazard` | 1 | array 1 |
| `hazard[]` | 73 | object 73 |
| `hazard[].name` | 73 | string 73 |
| `hazard[].source` | 73 | string 73 |
| `hazard[].rating` | 21 | array 21 |
| `hazard[].rating[]` | 26 | object 26 |
| `hazard[].rating[].tier` | 26 | number 26 |
| `hazard[].rating[].threat` | 26 | string 26 |
| `hazard[].entries` | 73 | array 73 |
| `hazard[].entries[]` | 187 | string 159, object 28 |
| `hazard[].page` | 68 | number 68 |
| `hazard[].trapHazType` | 40 | string 40 |
| `hazard[].entries[].type` | 28 | string 28 |
| `hazard[].entries[].name` | 19 | string 19 |
| `hazard[].entries[].entries` | 19 | array 19 |
| `hazard[].entries[].entries[]` | 26 | string 21, object 5 |
| `hazard[].reprintedAs` | 15 | array 15 |
| `hazard[].reprintedAs[]` | 15 | string 15 |
| `hazard[].srd52` | 14 | boolean 14 |
| `hazard[].entries[].caption` | 8 | string 8 |
| `hazard[].entries[].colLabels` | 8 | array 8 |
| `hazard[].entries[].colLabels[]` | 19 | string 19 |
| `hazard[].entries[].colStyles` | 8 | array 8 |
| `hazard[].entries[].colStyles[]` | 19 | string 19 |
| `hazard[].entries[].rows` | 8 | array 8 |
| `hazard[].entries[].rows[]` | 59 | array 59 |
| `hazard[].entries[].rows[][]` | 130 | string 130 |
| `hazard[].hasFluffImages` | 3 | boolean 3 |
| `hazard[].entries[].entries[].type` | 5 | string 5 |
| `hazard[].entries[].entries[].colLabels` | 5 | array 5 |
| `hazard[].entries[].entries[].colLabels[]` | 14 | string 14 |
| `hazard[].entries[].entries[].colStyles` | 5 | array 5 |
| `hazard[].entries[].entries[].colStyles[]` | 14 | string 14 |
| `hazard[].entries[].entries[].rows` | 5 | array 5 |
| `hazard[].entries[].entries[].rows[]` | 15 | array 15 |
| `hazard[].entries[].entries[].rows[][]` | 42 | string 42 |
| `hazard[].otherSources` | 2 | array 2 |
| `hazard[].otherSources[]` | 2 | object 2 |
| `hazard[].otherSources[].source` | 2 | string 2 |
| `hazard[].otherSources[].page` | 2 | number 2 |
| `hazard[].entries[].page` | 1 | number 1 |
| `hazard[].entries[].items` | 1 | array 1 |
| `hazard[].entries[].items[]` | 3 | string 3 |

## Root-level field presence

| Catalog | Field | Definitions with field |
|---|---|---|
| trap | `name` | 37 |
| trap | `source` | 37 |
| trap | `page` | 37 |
| trap | `trapHazType` | 37 |
| trap | `rating` | 25 |
| trap | `entries` | 37 |
| trap | `hasFluffImages` | 5 |
| trap | `effect` | 10 |
| trap | `trigger` | 22 |
| trap | `countermeasures` | 14 |
| trap | `srd` | 8 |
| trap | `reprintedAs` | 7 |
| trap | `srd52` | 8 |
| trap | `duration` | 8 |
| trap | `hauntBonus` | 4 |
| trap | `initiative` | 4 |
| trap | `eActive` | 4 |
| trap | `eDynamic` | 4 |
| trap | `eConstant` | 2 |
| trap | `initiativeNote` | 1 |
| hazard | `name` | 73 |
| hazard | `source` | 73 |
| hazard | `rating` | 21 |
| hazard | `entries` | 73 |
| hazard | `page` | 68 |
| hazard | `trapHazType` | 40 |
| hazard | `reprintedAs` | 15 |
| hazard | `srd52` | 14 |
| hazard | `hasFluffImages` | 3 |
| hazard | `otherSources` | 2 |

## Confirmed absences

- Neither root contains `_copy`, `_versions`, or version implementation data.
- Neither root has structured character-level ranges, detection/disarming checks, saving throws, damage blocks, reset instructions, or explicit mechanical reference arrays.
- Traps have no `otherSources`; hazards have it on two definitions.
- All 37 traps have an authored `trapHazType`; 33 of 73 hazards omit it.
- Every root definition has `name` and `source`; hazards omit `page` on five records.
- All observed `hasFluffImages`, `srd`, and `srd52` values are booleans; these flags are retained internally and never projected publicly.

## Typed interpretation

- `rating` is an array of `{tier: integer, threat: string}` records, absent on 12 traps and 52 hazards.
- `entries` uses strings and typed entry objects, with recursive `entries`, list items, and tables. Table rows preserve authored string cells and labels.
- Trap `effect` and `trigger` are arrays of strings. `countermeasures`, `eActive`, `eDynamic`, and `eConstant` are arrays mixing text strings and objects with `type`, optional `style`, and typed `items`. Stage items have a `type`, optional name, and either an entry string or recursive typed entries.
- Trap `duration` is an array of `{type, condition?}` objects. `initiative` is integer, `hauntBonus` and `initiativeNote` are strings.
- Hazard `otherSources` is an array of `{source, page}` records. `reprintedAs` values are strings in both catalogs.
- Neither root contains media paths or embedded media objects; only `hasFluffImages` flags occur, and those remain internal.
