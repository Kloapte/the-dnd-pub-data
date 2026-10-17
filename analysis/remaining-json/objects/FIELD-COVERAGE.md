# Objects Field Coverage

Source: pinned `src/main/resources/data/objects.json`, root `object`, 37 records. The audit recursively examined all
values and all typed arrays; there were no null-valued fields.

| Root field | Observed shape / nested paths | Destination |
|---|---|---|
| `name`, `source`, `page` | string, string, integer on all 37 | stable identity / `SourceReference` |
| `isNpc` | optional boolean, 3 | internal and public detail |
| `size` | string array on all 37; codes S, M, L, H, G, T, V | internal and public |
| `objectType` | string on all 37; U=10, SW=26, GEN=1 | internal and public as category |
| `ac`, `hp` | integer or `{special: string}`; AC 36 numeric/1 special, HP 32 numeric/5 special | typed statistic value, public |
| `speed` | integer (3) or `{walk, burrow}` (1) | typed speed value, internal and public |
| `str`, `dex`, `con`, `int`, `wis`, `cha` | optional integer ability values | internal and public detail; JSON `int` maps to Java `intValue` |
| `senses` | string array, 2 records | internal only (free text) |
| `immune` | string array or `{special}` element; 21 records | typed damage defenses, internal and public |
| `resist`, `vulnerable` | string arrays; 1 and 2 records | typed damage defenses, internal and public |
| `conditionImmune` | string array; 3 records | internal and public |
| `actionEntries` | 29 arrays; each object has `type`, `name`, and `entries`; children are text, typed `attack` (`attackType`, `attackEntries`, `hitEntries`), or typed `list` (`page`, `style`, `items[]` with typed entry content) | typed source DTE / internal normalized definition; omitted publicly |
| `entries` | 34 arrays of recursive text and `entries`, `table`, `list`, and `item` forms; table cells include strings and nested arrays | existing strict `EntryDte` hierarchy; internal only |
| `reprintedAs` | 7 string arrays of `name|source` UIDs | preserved lineage, source-validated |
| `token` | 3 `{name, source}` objects | importer provenance; omitted publicly |
| `altArt` | 6 arrays of `{name, source}` | importer provenance; omitted publicly |
| `tokenCredit` | optional string, 16 | importer provenance; omitted publicly |
| `tokenCustom`, `hasToken`, `hasFluffImages`, `srd` | optional booleans (16, 34, 10, 1) | provenance flags except `srd`, exposed in detail |

The complete recursive action and descriptive-entry subpaths observed are enumerated below. No other root or nested
field appears in the corpus.

- `actionEntries[]`, `.type`, `.name`, `.entries[]`; `.entries[]` is text or object; attack objects use
  `.type`, `.attackType`, `.attackEntries[]`, `.hitEntries[]`; list objects use `.type`, `.page`, `.style`,
  `.items[]`; list items use `.type`, `.name`, `.entries[]`.
- `entries[]`, nested `.type`, `.name`, `.entries[]`, `.items[]`, `.page`, `.style`, `.caption`, `.colLabels[]`,
  `.colStyles[]`, `.rows[][]`; all observed table rows are arrays of strings.
- `speed.walk` and `speed.burrow`; statistic special values use `.special`; defense special values use `.special`.
- `token.name`, `token.source`, `altArt[].name`, `altArt[].source`.

The corpus has no `otherSources`, dimensions, damage threshold, copy metadata, template metadata, or reprint target
expansion directives. No source field was silently ignored: authored mechanics are typed; importer-only presentation
metadata remains on the retained source DTE; prose and media are deliberately excluded from public projection.
