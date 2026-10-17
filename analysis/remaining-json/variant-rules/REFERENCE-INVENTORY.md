# Variant Rules Reference Inventory

## Authored reference and lineage fields

The root records contain no typed rule dependency or child-rule UID. `reprintedAs` occurs on 13 definitions (11 string UIDs and 2 `{tag, uid}` objects); the two tagged UIDs identify hazards. `additionalSources` contributes 5 source/page citations across 3 definitions. All primary, secondary, and qualified reprint source codes are registered. No `_copy` or `_versions` declarations occur.

Markup inside recursive entries is inventoried by tag occurrence. The supported target adapters used by this catalog are Spell, Item, and Skill; the existing `filter` tag is retained as an Item filter reference. Filters remain choices, not grants. The exact new resolver outcomes are:

| Target kind | RESOLVED | AMBIGUOUS | UNRESOLVED | DEFERRED | Total |
|---|---:|---:|---:|---:|---:|
| ITEM (includes 8 authored `filter` occurrences) | 83 | 10 | 21 | 23 | 137 |
| SKILL | 12 | 132 | 0 | 0 | 144 |
| SPELL | 7 | 72 | 0 | 1 | 80 |
| **Total** | **102** | **214** | **21** | **24** | **361** |

The detail projection retains original tokens, optional source qualification, owner ID, exact nested entry locator, and reference role. Only RESOLVED outcomes expose `targetId`; ambiguous candidates and diagnostics are not public. New occurrences are owned by `variant-rule` and do not change the prior 4,520 resolver occurrences or their 1,918/1,487/44/1,071 outcome distribution.

## Complete markup tag inventory

These counts include tags nested in prose and table cells. They are authored markup occurrences, not grants or executable relationships.

| Tag | Occurrences | Treatment |
|---|---:|---|
| `variantrule` | 204 | No Variant Rule target adapter; preserve token in internal entries |
| `action` | 24 | Action target adapter not supported by the resolver |
| `skill` | 144 | Extracted to existing Skill adapter |
| `quickref` | 8 | Presentation/reference markup retained internally |
| `condition` | 97 | Condition target adapter not supported by this resolver |
| `spell` | 80 | Extracted to existing Spell adapter |
| `book` | 94 | Publication link markup retained internally |
| `item` | 129 | Extracted to existing Item adapter |
| `filter` | 8 | Extracted as non-grant Item filter references |
| `dice` | 292 | Dice expressions inside authored text; not parsed into mechanics |
| `dc` | 62 | DC notation inside authored text; not extracted as structured checks |
| `damage` | 35 | Damage notation inside authored text; not extracted as structured damage |
| `sense` | 4 | Unsupported target kind |
| `table` | 18 | Presentation markup retained internally |
| `chance` | 10 | Formatting/content markup retained internally |
| `i` | 6 | Formatting markup retained internally |
| `creature` | 289 | Monster target adapter not supported; no creature ID inferred |
| `status` | 3 | Condition/status target adapter not supported |
| `adventure` | 26 | Adventure target adapter not supported |
| `note` | 3 | Formatting markup retained internally |
| `link` | 3 | Formatting markup retained internally |
| `5etools` | 3 | Formatting markup retained internally |
| `trap` | 4 | Trap target adapter not supported |
| `bold` | 16 | Formatting markup retained internally |
| `b` | 32 | Formatting markup retained internally |
| `class` | 48 | Class target adapter not supported |
| `hazard` | 1 | Hazard target adapter not supported |
| `hit` | 1 | Attack notation retained as authored text |
| `deck` | 1 | Unsupported target type |
| `card` | 14 | Unsupported target type |
| `vehicle` | 12 | Vehicle target adapter not supported |
| `vehupgrade` | 23 | Vehicle Upgrade target adapter not supported |

The remaining tags, including class, creature, action, condition, hazard, trap, vehicle, and Variant Rule tokens, stay within the typed entry hierarchy. Ordinary name mentions and prose are never promoted to links. No new resolver target kinds were added.

## Source qualification

Primary `source` values use 15 codes. Across primary sources, `additionalSources`, and reprint UIDs, the 17 distinct codes are AAG, AI, CoS, DMG, ESK, GoS, MM, PHB, PSI, SCAG, TCE, VGM, VRGR, WDMM, XDMG, XGE, and XPHB. Every code validates against the shared Source Registry. Source-qualified markup retains the source token exactly; unqualified names are not assigned an inferred source.
