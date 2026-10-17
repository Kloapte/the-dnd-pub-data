# Bastion Facilities Field Coverage

Corpus: `src/main/resources/data/bastions.json`, root `facility`, 70 definitions. This inventory records observed fields and types. Optional fields absent from individual records are omitted; the corpus contains no explicit null values.

## Root fields

| JSON path | Type | Definition count | Normalization |
|---|---|---:|---|
| `name` | string | 70 | Required definition name |
| `source` | string | 70 | Required exact source code |
| `page` | integer | 70 | Retained in internal source provenance and source reference |
| `facilityType` | string (`basic`, `special`) | 70 | Required classification; public `facilityType` |
| `level` | integer | 64 | Public optional level requirement |
| `prerequisite` | array of objects | 34 | Typed alternatives, retained internally and safe structured subset projected |
| `space` | array of strings | 70 | Public authored space labels, no conversion to dimensions |
| `hirelings` | array of objects | 64 | Typed exact/minimum requirement and optional space label |
| `orders` | array of strings | 64 | Public authored order labels, no execution |
| `entries` | array of strings/typed objects | 70 | Preserved internally; never projected as prose |
| `hasFluffImages` | boolean | 15 | Retained internally; media is not exposed |
| `hasFluff` | boolean | 1 | Retained internally |

All primary sources validate against the Source Registry. Counts by source: AU 9, EFA 10, FRHoF 8, RHW 8, XDMG 35. There are no copy/version/reprint fields, record-level media paths, `otherSources`, explicit-null variants, numeric zero values, or authored false values. The boolean focus DTE still preserves false as a supported typed synthetic edge case.

## Nested fields

### Prerequisites

Observed prerequisite keys and occurrence counts: `membership` 17, `spellcastingFocus` 12, `otherSummary` 5, `expertise` 1, and `proficiency` 1.

- `membership[]`: strings naming authored membership requirements.
- `spellcastingFocus`: boolean (true occurs; false is a supported typed source variant) or string array. Observed values include `arcane`, `holy`, `druid`, `tool`, and `artisansTool`.
- `otherSummary.entry`: descriptive string retained internally and excluded publicly.
- `otherSummary.entrySummary`: descriptive summary string retained internally and excluded publicly.
- `expertise[]`: object with boolean `skill`.
- `proficiency[]`: object with string-array `skill`.

### Hirelings

- `hirelings[]`: object.
- `hirelings[].exact`: integer, observed where a fixed number is authored.
- `hirelings[].min`: integer, observed where a minimum number is authored.
- `hirelings[].space`: string label.

No other hireling keys are present. No numeric value is inferred from entries.

### Entry tree

Top-level facility entries include 79 string entries, 112 objects of `type: "entries"`, and one table object. The recursively observed typed entry paths are:

- `entries[].type`, `entries[].name`, `entries[].entries`, `entries[].entry`, `entries[].caption`, `entries[].colLabels[]`, `entries[].colStyles[]`, `entries[].rows[][]`.
- Nested `entries[].entries[]` has the same typed entry alternatives and may include `columns`, `footnotes[]`, `items[]`, and `style`.
- Entry `items[]` contains either a string or a typed object with `type`, `name`, and `entry`.
- Table values preserve rows as arrays of strings, labels/styles, captions, column count, and footnotes.

Entries use the existing strict `EntryDte` sealed hierarchy and its explicit entry/table-cell deserializers. The corpus has no facility-specific action, cost, currency, damage, AC/HP, size, movement, ability score, duration, or benefit field. Entries are not parsed for mechanics.

## Markup tags in entries

Counts in authored entry text: `book` 173, `variantrule` 48, `item` 48, `creature` 43, `reward` 30, `skill` 29, `spell` 26, `dice` 24, `filter` 7, `action` 7, `chance` 3, `damage` 3, `table` 2, `condition` 2, `sense` 2, `facility` 1, `status` 1, `dc` 1, and `b` 1 (451 total). Four additional `book` tags occur in prerequisite `otherSummary.entry` values, for 455 markup occurrences across the complete record tree. Their `EFA` source qualification is validated against the Source Registry. Raw markup stays internal. The established resolver handles spell/item/skill links and item filters only. Other tags are retained as entry text and documented as unsupported or descriptive, not upgraded into inferred relationships.
