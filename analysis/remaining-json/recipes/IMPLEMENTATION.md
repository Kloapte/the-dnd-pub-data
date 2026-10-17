# Recipes Catalog Implementation

## Scope and decision

The v2.37.0 `recipes.json` root contains 316 authored recipe records. They are food and cocktail
recipes from D&D-themed culinary publications, not gameplay crafting recipes. A read-only metadata
catalog is useful for discovering those records. It does not execute cooking/crafting or expose
the source instructions.

The complete schema and domain/publication decision are documented in [SOURCE-AUDIT.md](SOURCE-AUDIT.md)
and [DOMAIN-DESIGN.md](DOMAIN-DESIGN.md). The pinned resource fingerprint is
`49ae4462f5da4bcfb6d6324e78cccac0f232fc36f9f67934bb9cabb1c3c8b935`; the audit records 73
recursive field/type paths.

## Source mapping and normalization

`RecipesFileDte` and `RecipeDte` strictly deserialize all 316 records. Dedicated typed unions
preserve string and structured ingredient entries, ingredient groups, equipment entries,
instruction sections, cook-note sections, and string/number `makes` variants. Serving exact/range
metadata, numeric ingredient placeholders `amount1`–`amount5`, source citations, aliases, diet and
allergen labels, notes, tags, and Boolean source flags remain typed internally. No known source
field is ignored. Unknown root and nested fields fail deserialization.

`RecipeDefinition` is immutable and source-aware; its internal `RecipeSourceContent` retains the
typed authored content. IDs are `recipe:<exact-source>:<normalized-name-slug>`. The 316
`(source,name)` identities and normalized IDs are unique. HFDoMM is preserved as a registered
source identity with no fabricated publication metadata. Ten HFDoMM records have no page; pages
are not used as identity components.

## API and publication boundary

Summary and detail DTOs expose ID, name, primary source code, optional page, authored `type` and
`dishTypes`, and aggregate ingredient/equipment/instruction/note/section counts. They omit aliases,
diet and allergen labels, serving/yield strings, ingredient names and quantities, equipment text,
instructions, cook notes, raw markup, fluff flags, and media. The metadata API does not make
nutrition, allergy-safety, or game-mechanics claims.

Read-only endpoints:

- `GET /api/recipes?source=&type=&dishType=`
- `GET /api/recipes/{id}`
- `GET /api/recipes/by-name/{name}?source=`

All 316 results fit in a bounded static catalog, so pagination is unnecessary. Name lookup returns
all source-matching exact-name results; it does not arbitrarily select duplicate titles.

## References and licensing

There are eight authored recipe links, one creature tag, one external link, and 96 unit-formatting
tags. None target a currently supported resolver kind. Recipe/creature tokens remain in internal
typed source strings, and Recipes add zero resolver occurrences. No ingredient-name Item links or
recipe-result grants are inferred. See [REFERENCE-INVENTORY.md](REFERENCE-INVENTORY.md).

Complete cooking instructions and ingredient lists are source-authored cookbook content and remain
private pending explicit publication authorization. Structured representation alone is not treated
as redistribution permission. The public index exposes only narrowly scoped identity and
classification metadata.

## Verification

The dedicated tests cover the full-corpus typed round-trip, all 316 identities, registry and
secondary-source validation, strict unknown-field rejection, heterogeneous entries, grouped
ingredients/instructions/cook notes, missing page variants, meaningful numeric zero and Boolean
false, safe serialization, endpoints/filters, zero new resolver occurrences, unchanged 6,246
outcomes, and packaged-JAR resource loading. The complete Maven suite passes: 399 tests, with zero
failures or errors. `mvn -DskipTests package` succeeds, and `git diff --check` passes.
