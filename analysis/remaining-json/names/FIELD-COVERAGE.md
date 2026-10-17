# Name Table field coverage

Source: `src/main/resources/data/names.json`, root collection `name`, 10 records. The 15 observed recursive field paths are all represented in typed DTEs and normalized internal definitions.

The exact paths are `$`, `$.name`, `$.name[]`, then `$.name[].name`, `.page`, `.source`, `.tables`, `.tables[]`, `.tables[].diceExpression`, `.tables[].option`, `.tables[].table`, `.tables[].table[]`, `.tables[].table[].max`, `.tables[].table[].min`, and `.tables[].table[].result`.

| Field path | Observed type/shape | Coverage and publication handling |
|---|---|---|
| `name` | string | Required; definition name and stable ID context |
| `source` | string | Required; original source code retained and registry-validated |
| `page` | integer | Required positive integer; included as source reference page |
| `tables[]` | array of objects | Preserved as immutable `NameTableOption` list; not public |
| `tables[].option` | string | Preserved as option/category label; not public pending licensing review |
| `tables[].diceExpression` | string | Preserved verbatim; no random generation; not public |
| `tables[].table[]` | array of row objects | Preserved as immutable ordered rows; not public pending review |
| `tables[].table[].min` | integer | Source-authored lower bound; preserved exactly, including sentinel rows |
| `tables[].table[].max` | integer | Source-authored upper bound; preserved exactly, including `0` sentinel values |
| `tables[].table[].result` | string | Name-table content; retained internally, not redistributed in public DTO |

Each root contains all four root fields; each option has all three fields, and each row has all three fields. All 61 options and 2,822 rows have complete non-null values. The option dice-expression shapes are 55 `d100`, 4 `d10`, 1 `d12`, and 1 `d20`. There are 2,822 rows. No nested table objects, nulls, empty variants, markup tags, copy/version declarations, or other reference fields were observed.

The 55 final source rows use `{min: 99, max: 0}`. The pair is an authored sentinel in this dataset; no attempt is made to reinterpret it as a numeric interval. Public metadata exposes only option-table and row counts, not row values or table content.
