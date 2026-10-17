# Licensing and Publication Boundaries

This document records publication-risk boundaries for implementation planning; it is not legal advice or a publication authorization. The root project `LICENSE` is MIT for the application repository. That license alone does not establish redistribution rights for all imported third-party text, art, media paths, or data embedded in `src/main/resources/data`.

| Family | Structured content that may support an API | Content requiring explicit publication review |
|---|---|---|
| Adventures index | Source code, title, group, storyline, level range, contents headings and publication metadata | Covers/media paths, author/publisher content, detailed chapter descriptions if added later; actual chapter bodies are absent here |
| Bastion facilities | Facility type, level, space, prerequisite structure, hireling/cost fields | Entries, flavor descriptions, named source-specific content |
| Character options | Option type, level/prerequisite structure, structured choice values | Descriptive entries and source markup |
| Cults and boons | Type/category, explicit structured grants or prerequisites | Cult narratives, creature-like feature prose and deity references |
| Deities | Pantheon, domains, alignment and other structured identity fields | Personality, lore, symbols/art, entries and third-party setting descriptions |
| Encounters | Table mechanics, ranges, structured participants/results | Adventure-specific encounter descriptions and narrative text |
| Recipes | Stable identity, source/page, authored catalog type and dish-type labels, aggregate section counts | Ingredients, quantities, yields, equipment, instructions, cook notes, aliases, diet/allergen labels, markup, media |
| Homecraft | None; excluded by product decision | Crochet patterns are OUT OF SCOPE and have no public projection |
| Homecrafts | Measurements, material lists, structured sizing data | Full crochet instructions, patterns, designer attribution, photographs/asset paths |
| Loot | Dice ranges, structured currency and category outputs | Table prose, specific item text, associated media; structured tables still need source-specific publication review |
| Names | Table keys, dice intervals, name strings | Names and lists are source-authored content; assess redistribution rights, especially when paired with culture/setting text |
| Objects | Source-attributed category, size, AC/HP values, speed, abilities and defenses | Descriptive entries/actions, special-stat prose, imagery, token credits and source markup; see the Objects public projection |
| Recipes | Ingredient quantities, yields and timing metadata | Instructions and recipe text; cookbook-derived content may have separate rights |
| Rewards | Type/category, numeric values, structured prerequisites/grants | Descriptions, narrative flavor and source markup |
| Traps and hazards | Ratings, triggers, structured DC/damage where explicitly represented | Long descriptions, room text, encounter narrative, maps/media |
| Variant rules | Type/category, source/page, typed references | Rules prose and explanatory examples, even when represented as `entries` |
| Vehicles/upgrades | Numeric statistics, capacities, types and structured compatibility | Descriptive entries, setting-specific names, illustrations/media paths |

## Safe publication policy for the next stages

1. Keep DTE/import retention separate from public DTOs. Preserve source descriptions internally if required for completeness, but default to omitting them from public projections.
2. A structured mechanic is not automatically licensed merely because it is numeric. Preserve provenance and source attribution and review the exact field/source before publication.
3. Do not expose `entries`, `entriesHigherLevel`, raw tagged strings, image paths, token paths, cover paths, or importer metadata through a public DTO without a separate content decision.
4. Existing SRD flags, `basicRules` markers, and Source Registry records are metadata, not a blanket permission signal. Verify the applicable license and content scope for each future release.
5. `homecrafts.json` and `recipes.json` are especially likely to contain complete expressive instructions. A useful public API may be limited to metadata or deferred entirely pending review.

The inventory did not alter publication metadata, source registration, or public APIs.
