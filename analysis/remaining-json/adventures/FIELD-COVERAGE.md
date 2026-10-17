# Adventure field coverage

Source: `src/main/resources/data/adventures.json`, root collection `adventure`, 101 records. Paths below are relative to each root unless noted. Every discovered Adventure root field is mapped into a typed source DTE and has a normalized or provenance destination.

The scanner records 35 paths including document/collection/array nodes. In addition to the structural nodes `$`, `$.adventure`, and `$.adventure[]`, the exact field paths are `alias`, `alias[]`, `author`, `contents`, `contents[]`, `contents[].headers`, `contents[].headers[]`, header `depth/header/index/source/statblock/tag/uid`, `contents[].name`, `contents[].ordinal`, `contents[].ordinal.identifier`, `contents[].ordinal.type`, `cover`, `cover.path`, `cover.type`, `group`, `id`, `level`, `level.end`, `level.start`, `name`, `parentSource`, `published`, `publishedOrder`, `source`, and `storyline` (all rooted at `$.adventure[]`).

| Field path | Observed type/shape | Coverage and publication handling |
|---|---|---|
| `name`, `id`, `source` | string | Required; normalized name/source and retained authored ID |
| `group` | string | Required, exposed as safe publication classification |
| `published` | ISO date string | Required; parsed to `LocalDate` |
| `author` | string, absent on one record | Optional internal normalized metadata; public detail only |
| `alias[]` | string array, absent on 99 records | Optional, immutable; public detail only |
| `parentSource` | string, absent on 51 records | Series/group context, not a registry source; retained and exposed as source-authored metadata |
| `publishedOrder` | integer, absent on 75 records | Optional authored ordering metadata |
| `storyline` | string | Optional grouping metadata |
| `level.start`, `level.end` | integer object | Typed inclusive authored range, validated as nonnegative and ordered |
| `cover.path`, `cover.type` | object with strings | Reused `BookCoverDte`; retained in internal provenance, never public |
| `contents[].name` | string | Reused `BookContentDte`; internal navigation metadata |
| `contents[].ordinal.type` | string | Reused Book ordinal model |
| `contents[].ordinal.identifier` | number or string | Explicit existing Book ordinal identifier deserializer retained |
| `contents[].headers[]` | string or object | Reused strict Book header polymorphism |
| `contents[].headers[].header` | string, required for object form | Internal navigation label |
| `contents[].headers[].depth`, `.index` | integer | Internal navigation structure |
| `contents[].headers[].source` | source-code string, observed once | Validated against Source Registry; internal only |
| `contents[].headers[].statblock` | boolean | Internal navigation metadata; explicit `false` retained |
| `contents[].headers[].tag`, `.uid` | strings, observed in one header | Internal typed navigation reference; UID source validated |

There are 755 content sections, 391 ordinals (238 numeric identifiers and 153 string identifiers), and 10,708 header values: 3,244 strings and 7,464 objects. The object shape distribution is `{header,index}` twice; `{depth,header}` 7,430 times; `{depth,header,statblock}` 30 times; `{depth,header,statblock,tag,uid}` once; and `{depth,header,source,statblock}` once. The `depth` field is absent from two objects; `statblock` occurs 32 times with boolean values. The `header` field occurs 7,464 times. These nested paths are covered by the existing strict Book DTE hierarchy; an unknown property in a header object is rejected.

Root presence counts: `alias` 2/101, `author` 100/101, `parentSource` 50/101, and `publishedOrder` 26/101. All other root properties listed above occur on all 101 roots. `contents` and `cover` occur on all roots; no explicit null or empty variants occur. Within contents, 517 sections have headers and 238 do not. Source field paths are non-null and retain the exact authored values.

No root `_copy`, `_versions`, template, or reprint properties were observed. Root optional properties were absent rather than explicitly null/empty. The only media path remains internal. The only explicit UID is `Tree Blight|CoS`; it is retained but not bound to a catalog.
