# Deities Field Coverage

Source file: `src/main/resources/data/deities.json` (SHA-256: `6666acc24818ed5c9fd963cdf1d3030b845dea5437be48dea54923dccaf7c5b8`).
Root metadata is typed separately from the `deity` records. The file contains one `deity` root collection with 563 definitions and a `_meta.internalCopies` string array containing `deity`.

## Root fields

| Field | JSON type | Records | Destination / treatment |
|---|---:|---:|---|
| `_copy` | object | 6 | Typed exact copy identity; resolved to parent by name/source/pantheon; retained. |
| `additionalSources` | array | 61 | Typed source/page references; source validated and retained. |
| `alignment` | array | 463 | Ordered open string-code list; null/absence retained; effective list copy-inherits missing values. |
| `altNames` | array | 85 | Ordered alternate names; retained and indexed for exact lookup. |
| `basicRules` | boolean | 37 | Optional boolean publication marker; retained internally; 37 true, no false. |
| `category` | string | 175 | Open string grouping/category label; retained and filterable. |
| `customExtensionOf` | string | 6 | Authored name|pantheon|source lineage UID; validated against _copy. |
| `domains` | array | 464 | Ordered open strings; copy-inherits when missing; never mapped to class/subclass. |
| `entries` | array | 216 | Typed recursive EntryDte list; retained internally; no prose exposed. |
| `name` | string | 563 | Required authored name and identity component. |
| `page` | number | 563 | Required integer page locator; public source reference. |
| `pantheon` | string | 563 | Required authored identity context and category filter. |
| `piety` | boolean | 15 | Optional boolean source flag; retained internally; 15 true, no false. |
| `plane` | string | 69 | Descriptive plane label; retained internally. |
| `province` | string | 153 | Descriptive province/portfolio text; retained internally. |
| `reprintAlias` | string | 22 | Authored alternate/reprint label; retained internally; no reprintedAs root field. |
| `source` | string | 563 | Required exact primary source code; source-registry validated. |
| `srd` | boolean | 49 | Optional boolean publication marker; retained internally; 49 true, no false. |
| `symbol` | string | 496 | Plain string with inline formatting on 12 ERLW values; retained internally and withheld publicly. |
| `symbolImg` | object | 158 | Typed media metadata (158); retained internally; media path/art withheld publicly. |
| `title` | string | 432 | Short authored title/epithet; retained internally and withheld as setting lore. |
| `worshipers` | string | 69 | Descriptive prose; retained internally and withheld publicly. |

## Recursive path inventory

The path inventory below includes every path observed recursively below each deity record. Counts are observed value occurrences by JSON type, including absent-on-other-record distinctions. Root `_meta.internalCopies` is listed above separately.

| Field path | Observed JSON type counts |
|---|---|
| `deity` | object: 563 |
| `deity._copy` | object: 6 |
| `deity._copy.name` | string: 6 |
| `deity._copy.pantheon` | string: 6 |
| `deity._copy.source` | string: 6 |
| `deity.additionalSources` | array: 61 |
| `deity.additionalSources[]` | object: 65 |
| `deity.additionalSources[].page` | number: 65 |
| `deity.additionalSources[].source` | string: 65 |
| `deity.alignment` | array: 463 |
| `deity.alignment[]` | string: 856 |
| `deity.altNames` | array: 85 |
| `deity.altNames[]` | string: 224 |
| `deity.basicRules` | boolean: 37 |
| `deity.category` | string: 175 |
| `deity.customExtensionOf` | string: 6 |
| `deity.domains` | array: 464 |
| `deity.domains[]` | string: 770 |
| `deity.entries` | array: 216 |
| `deity.entries[]` | string: 607, object: 111 |
| `deity.entries[].by` | string: 1 |
| `deity.entries[].entries` | array: 110 |
| `deity.entries[].entries[]` | object: 118, string: 179 |
| `deity.entries[].entries[].entries` | array: 97 |
| `deity.entries[].entries[].entries[]` | string: 133, object: 136 |
| `deity.entries[].entries[].entries[].credit` | string: 15 |
| `deity.entries[].entries[].entries[].entries` | array: 121 |
| `deity.entries[].entries[].entries[].entries[]` | string: 173, object: 154 |
| `deity.entries[].entries[].entries[].entries[].caption` | string: 30 |
| `deity.entries[].entries[].entries[].entries[].colLabels` | array: 30 |
| `deity.entries[].entries[].entries[].entries[].colLabels[]` | string: 60 |
| `deity.entries[].entries[].entries[].entries[].colStyles` | array: 30 |
| `deity.entries[].entries[].entries[].entries[].colStyles[]` | string: 60 |
| `deity.entries[].entries[].entries[].entries[].columns` | number: 15 |
| `deity.entries[].entries[].entries[].entries[].entries` | array: 64 |
| `deity.entries[].entries[].entries[].entries[].entries[]` | string: 79, object: 3 |
| `deity.entries[].entries[].entries[].entries[].entries[].items` | array: 3 |
| `deity.entries[].entries[].entries[].entries[].entries[].items[]` | object: 8 |
| `deity.entries[].entries[].entries[].entries[].entries[].items[].entry` | string: 8 |
| `deity.entries[].entries[].entries[].entries[].entries[].items[].name` | string: 8 |
| `deity.entries[].entries[].entries[].entries[].entries[].items[].type` | string: 8 |
| `deity.entries[].entries[].entries[].entries[].entries[].style` | string: 3 |
| `deity.entries[].entries[].entries[].entries[].entries[].type` | string: 3 |
| `deity.entries[].entries[].entries[].entries[].items` | array: 60 |
| `deity.entries[].entries[].entries[].entries[].items[]` | object: 50, string: 166 |
| `deity.entries[].entries[].entries[].entries[].items[].entry` | string: 50 |
| `deity.entries[].entries[].entries[].entries[].items[].name` | string: 50 |
| `deity.entries[].entries[].entries[].entries[].items[].type` | string: 50 |
| `deity.entries[].entries[].entries[].entries[].name` | string: 64 |
| `deity.entries[].entries[].entries[].entries[].rows` | array: 30 |
| `deity.entries[].entries[].entries[].entries[].rows[]` | array: 180 |
| `deity.entries[].entries[].entries[].entries[].rows[][]` | string: 360 |
| `deity.entries[].entries[].entries[].entries[].style` | string: 15 |
| `deity.entries[].entries[].entries[].entries[].type` | string: 154 |
| `deity.entries[].entries[].entries[].href` | object: 15 |
| `deity.entries[].entries[].entries[].href.path` | string: 15 |
| `deity.entries[].entries[].entries[].href.type` | string: 15 |
| `deity.entries[].entries[].entries[].name` | string: 106 |
| `deity.entries[].entries[].entries[].type` | string: 136 |
| `deity.entries[].entries[].items` | array: 21 |
| `deity.entries[].entries[].items[]` | string: 63 |
| `deity.entries[].entries[].name` | string: 97 |
| `deity.entries[].entries[].type` | string: 118 |
| `deity.entries[].items` | array: 1 |
| `deity.entries[].items[]` | object: 5 |
| `deity.entries[].items[].entry` | string: 5 |
| `deity.entries[].items[].name` | string: 5 |
| `deity.entries[].items[].type` | string: 5 |
| `deity.entries[].name` | string: 63 |
| `deity.entries[].style` | string: 1 |
| `deity.entries[].type` | string: 111 |
| `deity.name` | string: 563 |
| `deity.page` | number: 563 |
| `deity.pantheon` | string: 563 |
| `deity.piety` | boolean: 15 |
| `deity.plane` | string: 69 |
| `deity.province` | string: 153 |
| `deity.reprintAlias` | string: 22 |
| `deity.source` | string: 563 |
| `deity.srd` | boolean: 49 |
| `deity.symbol` | string: 496 |
| `deity.symbolImg` | object: 158 |
| `deity.symbolImg.credit` | string: 84 |
| `deity.symbolImg.height` | number: 42 |
| `deity.symbolImg.href` | object: 158 |
| `deity.symbolImg.href.path` | string: 158 |
| `deity.symbolImg.href.type` | string: 158 |
| `deity.symbolImg.title` | string: 36 |
| `deity.symbolImg.type` | string: 158 |
| `deity.symbolImg.width` | number: 42 |
| `deity.title` | string: 432 |
| `deity.worshipers` | string: 69 |

## Nested structure findings

- 92 recursive paths occur under deity records; there are no explicit null or empty values. Missing optional fields remain absent in typed DTEs.
- `alignment` occurs on 463 records as arrays of strings; `domains` on 464 records as arrays of strings. The values are open strings, not enums.
- `additionalSources` occurs on 61 records and contains 65 `{source,page}` references. All are source-registry valid.
- `symbolImg` occurs on 158 records. Its `type`, `href.type`, and `href.path` are strings; optional width/height are integer numbers, and optional title/credit are strings. These are internal media references.
- Entry nodes are typed and recursive: `entries` 354, `list` 85, `item` 63, `section` 15, `image` 15, `insetReadaloud` 15, `table` 30, `inset` 7, `quote` 1. Tables have 180 rows and 360 string cells. Source hierarchy and array order are preserved.
- Six `_copy` records point to SCAG definitions by exact `(name,source,pantheon)`; all six targets exist. They contain no authored alignment/domain/symbol values and inherit those three fields only. Their page, title, entries, source identity, `_copy`, and `customExtensionOf` remain child-authored.
- Root `reprintedAs`, `otherSources`, standalone alias, spell/domain-grant, prerequisite, ability-score, and deity-relationship properties are absent. `altNames` is the authored alias array (85 records, 224 values); `reprintAlias` occurs on 22 records.
- `srd`, `basicRules`, and `piety` occur as true booleans only (49, 37, and 15 records respectively); false and zero source values are absent. The typed boolean mapping preserves false if a later source revision supplies it.
- Entry `@spell` (17), `@item` (15), and `@skill` (1) tags are the only supported cross-catalog target occurrences. Additional markup is inventoried separately.
