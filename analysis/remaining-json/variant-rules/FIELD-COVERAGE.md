# Variant Rules Field Coverage

Source: `src/main/resources/data/variantrules.json`; SHA-256 `fbbaed4bb8002076fac0e38a59ed65bac34e261ac753602f3a02b0bdfb1a6485`. The `variantrule` root contains 231 definitions. The audit includes 119 distinct recursive property paths and aggregates occurrences by JSON type.

## Root collections and distributions

| Root | JSON type | Definitions |
|---|---|---:|
| `variantrule` | array of objects | 231 |

| Authored `ruleType` | Count |
|---|---:|
| `C` | 115 |
| `O` | 43 |
| `V` | 17 |
| `VO` | 16 |
| `[absent]` | 40 |

| Root entry `type` | Count |
|---|---:|
| `[absent]` | 186 |
| `entries` | 17 |
| `section` | 28 |
 
All 231 definitions have `name`, `source`, `page`, and `entries`. `ruleType` is absent on 40 records; root `type` is absent on 186. No parent, child-reference, category, subcategory, prerequisite, choice/filter, or structured modifier property occurs at the root.

## Nested entry node variants

Counts below include recursive nodes under entries but exclude the root's own optional `type` marker. `internal` is the observed `href.type` value and is modeled as a typed href discriminator, not an entry-node variant.

| Authored node type | Count | Observed fields besides `type` |
|---|---:|---|
| `entries` | 650 | entries; entries,name; entries,name,page; entries,name,page,ruleType,source; entries,name,page,source |
| `image` | 11 | credit,height,href,title,width; credit,height,href,width; href |
| `inline` | 1 | entries |
| `inset` | 4 | entries,name; entries,name,page,source |
| `internal` | 12 | hash,path; path |
| `item` | 48 | entries,name; entry,name |
| `link` | 1 | entry,href |
| `list` | 46 | columns,items; columns,items,style; items; items,name; items,style |
| `quote` | 3 | by,entries |
| `section` | 33 | entries,name; entries,name,page,reprintedAs,ruleType,source; entries,name,page,ruleType,source; entries,name,page,source; entries,name,page,source,srd |
| `table` | 188 | caption,colLabels,colStyles,data,rows; caption,colLabels,colStyles,footnotes,rows; caption,colLabels,colStyles,isNameGenerator,rows; caption,colLabels,colStyles,rows; colLabels,colStyles,rows |

The 188 tables contain 1,138 authored rows. Two nested tables have `isNameGenerator: true`. There are 33 nested `section` nodes plus 28 root records whose root entry type is `section`.

## Complete field path/type inventory

Each path row reports observed JSON value counts. `Definitions present` counts root records containing the path. No explicit null or empty values were observed. Arrays are traversed recursively; `[]` marks array elements. Table row indices and their values are positional source data, not dynamic object keys.

| Field path | Observed JSON types and counts | Definitions present | Explicit nulls | Empty values |
|---|---|---:|---:|---:|
| `$.additionalSources` | array (3) | 3 | 0 | 0 |
| `$.additionalSources[]` | object (5) | 3 | 0 | 0 |
| `$.additionalSources[].page` | number (5) | 3 | 0 | 0 |
| `$.additionalSources[].source` | string (5) | 3 | 0 | 0 |
| `$.basicRules2024` | boolean (115) | 115 | 0 | 0 |
| `$.entries` | array (231) | 231 | 0 | 0 |
| `$.entries[]` | string (377), object (271) | 231 | 0 | 0 |
| `$.entries[].caption` | string (24) | 20 | 0 | 0 |
| `$.entries[].colLabels` | array (24) | 20 | 0 | 0 |
| `$.entries[].colLabels[]` | string (57) | 20 | 0 | 0 |
| `$.entries[].colStyles` | array (24) | 20 | 0 | 0 |
| `$.entries[].colStyles[]` | string (57) | 20 | 0 | 0 |
| `$.entries[].columns` | number (5) | 5 | 0 | 0 |
| `$.entries[].entries` | array (232) | 80 | 0 | 0 |
| `$.entries[].entries[]` | string (407), object (345) | 80 | 0 | 0 |
| `$.entries[].entries[].caption` | string (62) | 27 | 0 | 0 |
| `$.entries[].entries[].colLabels` | array (64) | 27 | 0 | 0 |
| `$.entries[].entries[].colLabels[]` | string (138) | 27 | 0 | 0 |
| `$.entries[].entries[].colStyles` | array (64) | 27 | 0 | 0 |
| `$.entries[].entries[].colStyles[]` | string (140) | 27 | 0 | 0 |
| `$.entries[].entries[].columns` | number (7) | 3 | 0 | 0 |
| `$.entries[].entries[].credit` | string (3) | 3 | 0 | 0 |
| `$.entries[].entries[].data` | object (2) | 1 | 0 | 0 |
| `$.entries[].entries[].data.genTables` | object (2) | 1 | 0 | 0 |
| `$.entries[].entries[].data.genTables.tableInclude` | boolean (2) | 1 | 0 | 0 |
| `$.entries[].entries[].entries` | array (255) | 45 | 0 | 0 |
| `$.entries[].entries[].entries[]` | string (360), object (256) | 45 | 0 | 0 |
| `$.entries[].entries[].entries[].by` | string (3) | 3 | 0 | 0 |
| `$.entries[].entries[].entries[].caption` | string (100) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].colLabels` | array (100) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].colLabels[]` | string (214) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].colStyles` | array (100) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].colStyles[]` | string (214) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].credit` | string (5) | 4 | 0 | 0 |
| `$.entries[].entries[].entries[].entries` | array (142) | 8 | 0 | 0 |
| `$.entries[].entries[].entries[].entries[]` | string (156), object (3) | 8 | 0 | 0 |
| `$.entries[].entries[].entries[].entries[].href` | object (3) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].entries[].href.path` | string (3) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].entries[].href.type` | string (3) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].entries[].type` | string (3) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].footnotes` | array (11) | 10 | 0 | 0 |
| `$.entries[].entries[].entries[].footnotes[]` | string (11) | 10 | 0 | 0 |
| `$.entries[].entries[].entries[].height` | number (5) | 4 | 0 | 0 |
| `$.entries[].entries[].entries[].href` | object (5) | 4 | 0 | 0 |
| `$.entries[].entries[].entries[].href.path` | string (5) | 4 | 0 | 0 |
| `$.entries[].entries[].entries[].href.type` | string (5) | 4 | 0 | 0 |
| `$.entries[].entries[].entries[].isNameGenerator` | boolean (1) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].items` | array (9) | 5 | 0 | 0 |
| `$.entries[].entries[].entries[].items[]` | object (7), string (26) | 5 | 0 | 0 |
| `$.entries[].entries[].entries[].items[].entry` | string (7) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].items[].name` | string (7) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].items[].type` | string (7) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].name` | string (139) | 5 | 0 | 0 |
| `$.entries[].entries[].entries[].rows` | array (100) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].rows[]` | array (498) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].rows[][]` | string (1097), number (108) | 21 | 0 | 0 |
| `$.entries[].entries[].entries[].style` | string (1) | 1 | 0 | 0 |
| `$.entries[].entries[].entries[].title` | string (3) | 2 | 0 | 0 |
| `$.entries[].entries[].entries[].type` | string (256) | 25 | 0 | 0 |
| `$.entries[].entries[].entries[].width` | number (5) | 4 | 0 | 0 |
| `$.entries[].entries[].entry` | string (1) | 1 | 0 | 0 |
| `$.entries[].entries[].footnotes` | array (16) | 12 | 0 | 0 |
| `$.entries[].entries[].footnotes[]` | string (16) | 12 | 0 | 0 |
| `$.entries[].entries[].height` | number (3) | 3 | 0 | 0 |
| `$.entries[].entries[].href` | object (4) | 4 | 0 | 0 |
| `$.entries[].entries[].href.hash` | string (1) | 1 | 0 | 0 |
| `$.entries[].entries[].href.path` | string (4) | 4 | 0 | 0 |
| `$.entries[].entries[].href.type` | string (4) | 4 | 0 | 0 |
| `$.entries[].entries[].isNameGenerator` | boolean (1) | 1 | 0 | 0 |
| `$.entries[].entries[].items` | array (22) | 10 | 0 | 0 |
| `$.entries[].entries[].items[]` | string (87), object (22) | 10 | 0 | 0 |
| `$.entries[].entries[].items[].entries` | array (22) | 3 | 0 | 0 |
| `$.entries[].entries[].items[].entries[]` | string (22) | 3 | 0 | 0 |
| `$.entries[].entries[].items[].name` | string (22) | 3 | 0 | 0 |
| `$.entries[].entries[].items[].type` | string (22) | 3 | 0 | 0 |
| `$.entries[].entries[].name` | string (257) | 46 | 0 | 0 |
| `$.entries[].entries[].page` | number (42) | 11 | 0 | 0 |
| `$.entries[].entries[].rows` | array (64) | 27 | 0 | 0 |
| `$.entries[].entries[].rows[]` | array (476) | 27 | 0 | 0 |
| `$.entries[].entries[].rows[][]` | string (1003), object (15) | 27 | 0 | 0 |
| `$.entries[].entries[].rows[][].entries` | array (15) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].entries[]` | string (14), object (2) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].entries[].entries` | array (2) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].entries[].entries[]` | string (2) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].entries[].name` | string (2) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].entries[].type` | string (2) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].name` | string (14) | 1 | 0 | 0 |
| `$.entries[].entries[].rows[][].type` | string (15) | 1 | 0 | 0 |
| `$.entries[].entries[].source` | string (3) | 1 | 0 | 0 |
| `$.entries[].entries[].style` | string (3) | 3 | 0 | 0 |
| `$.entries[].entries[].title` | string (1) | 1 | 0 | 0 |
| `$.entries[].entries[].type` | string (345) | 65 | 0 | 0 |
| `$.entries[].entries[].width` | number (3) | 3 | 0 | 0 |
| `$.entries[].footnotes` | array (2) | 2 | 0 | 0 |
| `$.entries[].footnotes[]` | string (2) | 2 | 0 | 0 |
| `$.entries[].items` | array (15) | 13 | 0 | 0 |
| `$.entries[].items[]` | string (80), object (19) | 13 | 0 | 0 |
| `$.entries[].items[].entry` | string (19) | 1 | 0 | 0 |
| `$.entries[].items[].name` | string (19) | 1 | 0 | 0 |
| `$.entries[].items[].type` | string (19) | 1 | 0 | 0 |
| `$.entries[].name` | string (205) | 57 | 0 | 0 |
| `$.entries[].page` | number (2) | 2 | 0 | 0 |
| `$.entries[].rows` | array (24) | 20 | 0 | 0 |
| `$.entries[].rows[]` | array (164) | 20 | 0 | 0 |
| `$.entries[].rows[][]` | string (343), number (45) | 20 | 0 | 0 |
| `$.entries[].source` | string (2) | 2 | 0 | 0 |
| `$.entries[].style` | string (5) | 5 | 0 | 0 |
| `$.entries[].type` | string (271) | 105 | 0 | 0 |
| `$.name` | string (231) | 231 | 0 | 0 |
| `$.page` | number (231) | 231 | 0 | 0 |
| `$.reprintedAs` | array (13) | 13 | 0 | 0 |
| `$.reprintedAs[]` | string (11), object (2) | 13 | 0 | 0 |
| `$.reprintedAs[].tag` | string (2) | 2 | 0 | 0 |
| `$.reprintedAs[].uid` | string (2) | 2 | 0 | 0 |
| `$.ruleType` | string (191) | 191 | 0 | 0 |
| `$.source` | string (231) | 231 | 0 | 0 |
| `$.srd` | boolean (1) | 1 | 0 | 0 |
| `$.srd52` | boolean (115) | 115 | 0 | 0 |
| `$.type` | string (45) | 45 | 0 | 0 |

## Optional values, references, and media

Present root metadata counts: page 231; ruleType 191; rootType 45; srd 1; srd52 115; basicRules2024 115; reprintedAs 13; additionalSources 5; copyDeclarations 0; versionDefinitions 0. The only structured source provenance properties are `reprintedAs` and `additionalSources`; no `_copy` or `_versions` structures occur.

No dynamic property-name maps occur in the root or entry schema. Table rows are heterogeneous arrays whose cells are strings, numbers, or explicitly typed objects. The entry corpus includes 11 image nodes, one link node, and internal href paths; media and href metadata remain internal and are not exposed by the API.

Observed markup-tag counts and their relationship policy are recorded in [`REFERENCE-INVENTORY.md`](REFERENCE-INVENTORY.md).
