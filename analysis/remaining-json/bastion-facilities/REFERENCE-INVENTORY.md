# Bastion Facility Reference Inventory

## Discovery

The 70 facility definitions contain 455 inline markup tag occurrences across their full recursive record trees (451 in entries and four book citations in prerequisite summaries). Of these, the established cross-catalog layer supports explicit `spell`, `item`, and `skill` links and `filter` selectors in entries. It adds 110 resolver occurrences:

| Target kind | Semantic role | RESOLVED | AMBIGUOUS | UNRESOLVED | DEFERRED | Total |
|---|---|---:|---:|---:|---:|---:|
| Spell | Definition link | 26 | 0 | 0 | 0 | 26 |
| Skill | Definition link | 29 | 0 | 0 | 0 | 29 |
| Item | Definition link | 34 | 0 | 6 | 8 | 48 |
| Item | Filter | 0 | 0 | 0 | 7 | 7 |
| **Total** | | **89** | **0** | **6** | **15** | **110** |

The seven source `filter` tags are kept as selector references using established source/item filter parsing. They remain deferred selectors rather than item definitions. All 26 spell and 29 skill markup links resolve. Of 48 explicit item links, 34 resolve, six have no authoritative current target, and eight remain deferred. Exact source-qualified resolution is required for a public target ID; unqualified occurrences do not gain one merely because a unique candidate happens to exist.

## Metadata retained

Each supported occurrence is owned by catalog kind `facility` and its stable facility ID. The source extractor preserves original markup token, source qualification, semantic role (definition or filter), and exact recursive `entries[...]` locator. The existing immutable resolver snapshot and public lightweight reference projection perform resolution once at startup. No candidate lists or diagnostics are published.

Facility source entries also contain `action` (7), `creature` (43), `reward` (30), `facility` (1), `condition` (2), `sense` (2), `status` (1), `variantrule` (48), `table` (2), `book` (173), `dice` (24), `chance` (3), `damage` (3), `dc` (1), and `b` (1) tags. These types do not have compatible adapters in the existing resolver. Their authored markup remains preserved internally; no new resolver targets are guessed or registered. Prose mentions are not extracted as relationships.

The prior 3,954 occurrences retain their baseline outcomes: 1,704 RESOLVED, 1,178 AMBIGUOUS, 30 UNRESOLVED, and 1,042 DEFERRED. Bastion Facility references are additive and independently counted above, for 4,064 total occurrences after this catalog.
