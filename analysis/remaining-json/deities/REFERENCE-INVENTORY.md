# Deities Reference Inventory

## Resolver occurrences added

The source has 33 explicitly tagged references to target kinds supported by the existing resolver.
All are found in typed deity `entries` strings and preserve their original owner locator.

| Target kind | Occurrences | RESOLVED | AMBIGUOUS | UNRESOLVED | DEFERRED |
|---|---:|---:|---:|---:|---:|
| SPELL | 17 | 17 | 0 | 0 | 0 |
| ITEM | 15 | 1 | 0 | 8 | 6 |
| SKILL | 1 | 1 | 0 | 0 | 0 |
| **Total** | **33** | **19** | **0** | **8** | **6** |

Only exact authoritative results have public `targetId`; ambiguous, unresolved, and deferred results
remain without a target ID. The public detail projection retains lightweight supported references,
including authored token, source qualification, field locator, and role. Resolver diagnostics and
candidate lists remain private.

## Other observed markup

The recursive entry scan also found:

| Markup tag | Occurrences | Treatment |
|---|---:|---|
| `@creature` | 86 | Monster target adapter is not supported by this resolver; retained in private entries. |
| `@reward` | 60 | Reward target adapter is not supported; retained in private entries. |
| `@book` | 44 | Publication/book citations in source text, not treated as deity catalog links. |
| `@condition` | 1 | Condition target adapter is not supported; retained in private entries. |
| `@i` | 7 | Formatting tag in private entries. |
| `@b` | 2 | Formatting tag in private entries. |

No `@deity` markup or explicit structured deity-to-deity relationship occurs. Names, domains,
pantheons, and prose mentions are not converted into relationships. We do not infer Class or
Subclass links from domain strings. No target adapters were added speculatively.

## Baseline stability

The Deity catalog adds 33 occurrences to the existing snapshot. The previous 4,881 occurrences
retain their existing outcomes: 2,020 RESOLVED, 1,701 AMBIGUOUS, 65 UNRESOLVED, and 1,095 DEFERRED.
The full new snapshot contains 4,914 occurrences. Deity outcomes are tested independently in
`DeityCatalogTests`.
