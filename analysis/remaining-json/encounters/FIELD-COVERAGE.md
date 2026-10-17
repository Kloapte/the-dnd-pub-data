# Encounter Field Coverage

Source: `src/main/resources/data/encounters.json`, root `encounter`; 42 records. Paths below are exhaustive for the complete corpus. Every listed property is non-null when present. There are no empty arrays/strings, explicit nulls, or dynamic object keys.

| Field path | JSON type | Presence / values | Mapping and treatment |
|---|---|---|---|
| `encounter` | array | 42 | `EncounterFileDte.encounter`, all loaded |
| `encounter[].name` | string | 42 | Required; normalized as display name |
| `encounter[].source` | string | 42 | Required; exact Source Registry code retained |
| `encounter[].page` | number | 40 | Optional source page retained |
| `encounter[].tables` | array | 42; 93 total nested tables | Ordered typed table list |
| `encounter[].tables[].minlvl` | number | 48 tables | Optional inclusive authored level lower bound |
| `encounter[].tables[].maxlvl` | number | 48 tables | Optional inclusive authored level upper bound |
| `encounter[].tables[].diceExpression` | string | 93 | Authored expression preserved verbatim; not parsed or rolled |
| `encounter[].tables[].table` | array | 93; 2,299 total rows | Ordered rows retained internally |
| `encounter[].tables[].table[].min` | number | 2,299 | Authored lower roll bound |
| `encounter[].tables[].table[].max` | number | 2,299 | Authored upper roll bound |
| `encounter[].tables[].table[].result` | string | 2,299 | Full authored result and markup retained internally; omitted publicly |
| `encounter[].tables[].table[].resultAttitude` | string | 59 | Optional authored attitude retained internally; omitted with row content |
| `encounter[].tables[].caption` | string | 7 | Optional authored caption retained internally; omitted publicly |
| `encounter[].tables[].captionPrefix` | string | 5 | Optional authored caption fragment retained internally; omitted publicly |
| `encounter[].tables[].captionSuffix` | string | 7 | Optional authored caption fragment retained internally; omitted publicly |
| `encounter[].tables[].footnotes` | array | 7; 10 strings | Ordered authored notes retained internally; omitted publicly |
| `encounter[].tables[].page` | number | 7 | Optional table page retained internally |
| `encounter[].tables[].rollAttitude` | boolean | 3; all `true` | Nullable Boolean preserves explicit false if future data supplies it |

### Confirmed absences

No source definition has category/environment fields, explicit creature quantity fields, nested/group table structures, structured result variants, party-size/difficulty metadata, choice/filter fields, explicit source-reference fields, `_copy`, `_versions`, reprint metadata, or media fields. These are not synthesized. Creature names, counts, attitudes, items, and special instructions occur only in result strings and are not normalized into mechanics.

### Source distribution

BGG 18; XGE 11; DMG 4; BAM 3; EFA 1; FRAiF 1; AATM 1; GoS 1; MPP 1; SatO 1. All ten authored codes resolve in the Source Registry; exact spelling and case are preserved.
