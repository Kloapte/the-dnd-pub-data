# Recipes Domain and Publication Design Checkpoint

## Decision

Implement a narrow read-only Recipe metadata catalog. The source's purpose is clear and product
relevance is coherent: these are real-world food and cocktail recipes presented in D&D culinary
publications, and the source/book index groups them as recipes. They are content for discovery and
browsing, not D&D rules data, Item definitions, or a crafting system. A source-aware metadata index
is useful without exposing the recipe content itself.

This is not a redistribution authorization for the source instructions. The public contract is
limited to recipe identity and catalog metadata. Full ingredient text, quantities, sections,
equipment, preparation instructions, cook notes, aliases, and markup remain internal. This keeps
the endpoint useful for finding and filtering records while withholding the expressive cookbook
content that forms the source's main value.

## Domain boundary

`RecipeDefinition` is independent from Items, Base Items, Magic Variants, Rewards, Treasure Tables,
Spells, and character inventory. Ingredients are not Item references; recipe completion is not an
Item grant. No preparation, consumption, crafting, skill checks, yield scaling, inventory updates,
or effect application is implemented. Source text is preserved but never interpreted as mechanics.

## Identity and provenance

Use `recipe:<exact-source>:<normalized-name-slug>`. All records have name/source, the complete
corpus has no duplicate `(source,name)` keys, and normalized IDs are unique. Page is not a required
identity component because 10 HFDoMM records omit page data. The previously absent `HFDoMM` registry
identity is added with no invented alias/title/publication fields. All `otherSources` codes are validated;
copy/version/reprint lineage is absent.

## Typed internal model

Use strict Jackson DTEs for root definitions, serving ranges, ingredient leaves and groups,
equipment, instruction sections, cook-note sections, secondary sources, and `makes` string/number
variants. Ingredient quantity placeholders are numeric `amount1` through `amount5` fields, not
parsed from descriptive text. Ingredient/equipment/instruction entries have explicitly typed
string and structured variants. Preserve original strings, amount values, section order, tags,
booleans, aliases, sources, and pages. The immutable internal definition retains the complete
typed source record to prevent loss.

## Public DTOs

Summary and detail DTOs expose only:

- ID, name, exact primary source code, and optional page.
- Authored `type` and `dishTypes` labels, preserved as source strings.
- Ingredient/equipment/instruction/note counts and whether structured subsections occur.
- Lightweight references only if a supported resolver target is later evidenced; current count is zero.

The public response omits aliases, diet and allergen classifications, `makes`/`serves`, ingredient
and equipment values, quantities, instruction/cook-note text, `hasFluff*` flags, markup, and media.
This is a narrow metadata browse API and does not make food safety, allergy, nutrition, or
mechanical claims. The open `type` field is a source label, not an enum or game class reference.

## REST representation

- `GET /api/recipes?source=&type=&dishType=` returns deterministically sorted summaries. The fixed
  corpus is only 316 records, so pagination is not necessary.
- `GET /api/recipes/{id}` returns safe detail metadata.
- `GET /api/recipes/by-name/{name}?source=` returns all exact-name matches using established
  source-aware duplicate behavior.

No cooking, crafting, ingredient, inventory, or effect endpoints are appropriate.

## Relationships and licensing

There are eight explicitly authored recipe-to-recipe markup occurrences, one creature tag, one
external link, and unit-formatting markup. Recipe and creature target kinds are not supported by the
shared resolver. Keep all tokens internally; add zero resolver occurrences and expose zero
`targetId` values. No ingredient-name relationship is inferred.

The source contains complete, potentially protected cookbook instructions. Do not publish these
instructions, ingredient lists, notes, aliases, or their raw markup. Public fields are catalog
metadata only; publication of full recipes requires a separate rights decision.
