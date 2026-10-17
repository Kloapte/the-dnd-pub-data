# Recipes Reference Inventory

## Embedded tags

Complete scan of the 316 `recipe` records:

| Tag | Count | Semantics and handling |
|---|---:|---|
| `{@unit ...}` | 96 | Unit/number formatting within ingredient or instruction strings; retained internally, not a catalog relationship |
| `{@recipe ...}` | 8 | Authored recipe-to-recipe links; RECIPE is not a supported resolver target kind, so tokens remain internal |
| `{@creature ...}` | 1 | Creature-name markup; MONSTER is not a supported resolver target for this catalog, so token remains internal |
| `{@link ...}` | 1 | External link markup; not a supported catalog target and not exposed publicly |

The eight Recipe links include repeated source tokens and links to records in this same corpus. They
are preserved as source text but not converted to IDs because the shared resolver does not support
RECIPE. The single creature token is not resolved. Ingredient names and equipment labels are plain
recipe content, not authored Item UIDs. No SPELL, ITEM, BASE_ITEM, FEAT, OPTIONAL_FEATURE, SKILL, or
LANGUAGE references occur.

## Resolver impact

New shared resolver occurrences: **0**. The existing resolver remains at 6,246 occurrences with
2,517 RESOLVED, 2,054 AMBIGUOUS, 181 UNRESOLVED, and 1,494 DEFERRED. No supported adapter was added
and no `targetId` was inferred.

## Source lineage

`otherSources` contains 46 authored source citations: HFDoMM 40, HFLotT five (with pages 56, 75,
143, 162, and 186), and HFStCM one (no page). Seven nested cook-note entries carry source codes;
these are validated source citations, not entity references. No `reprintedAs`, `_copy`, or
`_versions` relationships occur.
