# Encounter Reference Inventory

A complete scan of all 2,299 authored `result` strings found tagged markup below. Encounter rows are not public. Explicit `spell`, `item`, and `skill` tags are inventoried with their exact result locator and passed to existing resolver adapters; only exact authoritative matches receive `targetId`. Other tags remain retained in the private authored result. Existing 4,064 occurrences/outcomes are unchanged; 15 encounter occurrences are separately added.

| Markup tag | Occurrences | Relationship handling |
|---|---:|---|
| `creature` | 2,568 | Retained in authored result strings. Monster target adapter is not supported by the shared resolver; no Monster IDs inferred. |
| `dice` | 1,581 | Authored inline expressions remain in result strings; no rolling or quantity extraction. |
| `book` | 93 | Encounter/outcome navigation token; not exposed. |
| `vehicle` | 20 | Vehicle catalog target not implemented in this step. |
| `item` | 13 | Retained in outcome strings; no result-content relationship exposed. |
| `chance` | 6 | Markup preserved verbatim; not executed. |
| `variantrule` | 9 | Retained in outcome strings; not exposed. |
| `b` | 7 | Formatting markup retained internally. |
| `table` | 5 | Embedded table markup retained as part of authored result. |
| `italic` | 4 | Formatting markup retained internally. |
| `i` | 4 | Formatting markup retained internally. |
| `dc` | 3 | DC text embedded in outcome; not normalized into a check. |
| `condition` | 3 | Retained in outcome strings; no public relationship. |
| `disease` | 1 | Retained in outcome strings; no public relationship. |
| `spell` | 1 | Retained in outcome strings; no public relationship. |
| `skill` | 1 | Retained in outcome strings; no public relationship. |
| **Total** | **4,319** | Raw authored markup tags in withheld outcomes, not resolver occurrences. |

## Added resolver occurrences

The 15 supported explicit tags are one Spell, 13 Item, and one Skill reference. Their startup resolution outcomes are:

| Outcome | Count |
|---|---:|
| RESOLVED | 3 |
| AMBIGUOUS | 4 |
| UNRESOLVED | 3 |
| DEFERRED | 5 |

The existing baseline remains 4,064 with outcomes 1,793 resolved, 1,178 ambiguous, 36 unresolved, and 1,057 deferred. Including Encounters, the snapshot contains 4,079 occurrences: 1,796 resolved, 1,182 ambiguous, 39 unresolved, and 1,062 deferred.

Names inside result prose, including creature names, do not establish authoritative links. Creature and Vehicle tags are not supported target kinds and no Monster or Vehicle target IDs are inferred. No explicit choice/filter groups are present. The public projection exposes only the lightweight supported references and their locators, never the containing result text. A future resolver adapter is required before Monster, Vehicle, Book, or other unsupported target kinds can become authoritative API references.
