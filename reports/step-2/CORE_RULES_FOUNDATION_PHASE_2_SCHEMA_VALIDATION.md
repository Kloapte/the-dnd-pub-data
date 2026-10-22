# Step 2 — Core Rules Foundation: Phase 2 Schema Validation

**Status:** Canonical model and schema design completed and awaiting review. Phase 3 has not begun.

## Contracts produced

Draft 2020-12 contracts are versioned under `schemas/core-rules/v1/`:

- Shared definitions for recursive rich text, source flags/citations, typed references, and supplied reprint-link variants.
- One cohesive schema each for Action, Rule, Skill, Condition, Disease, Status, Sense, Language, and LanguageScript.
- A small rich-text helper schema for validating the reusable content component; it is not a tenth domain model.

The contracts require source-qualified identity (`id`, exact `source`, and exact `name`) for each entity. Page is required where it exists in the source record; LanguageScript has no invented page. Optional source properties remain optional and empty values/arrays are not used to stand in for absent data. `sourceFlags` contains only supplied SRD/basic-rules booleans.

`Action.timing` uses a typed array for fixed action/reaction/bonus timing, `free`, and `varies`; it is absent when source timing is absent. `Rule.ruleType` preserves only `C`, `O`, `V`, or `VO`; `Rule.type` is an independent optional `section`/`entries` value. No code meanings were added. `Language.script` remains text. Reprint links preserve either source string values or the observed `{uid, tag}` form. `ModelReference` supports an explicit target type/name and optional source-qualified ID without requiring future-domain targets to be available.

Rich text is recursively typed for all observed node forms. Inline tags stay literal within text; explicit entity-reference extraction is separately represented and does not claim target resolution. No general mechanics/effect engine was added. Typed source facts such as timing and skill ability remain separate from descriptive entries; semantic effects require later evidence and review.

Representative examples are in `schemas/core-rules/v1/examples/`, including each domain, all action timing states, and a rich-text fixture covering each supported node type.

## Validation

Validated with Ajv 8.17.1 using its Draft 2020-12 implementation:

- **11 schema documents** passed schema meta-validation and compilation.
- **13 JSON examples** validated against their corresponding schemas: zero failures.
- A non-converting source-shape audit checked the nested rich-text arrays in all six raw files: **594 source records** inspected and **1,231 `entries` arrays** validated, with zero unsupported rich-text structures.
- Source-data validation did not create canonical records, a representative batch, or a normalized dataset. No source files were changed.

The complete rich-text scan confirmed the table-cell schema must accept strings, numbers, and nested typed rich-text nodes. The approved schema includes all three.

## Mapping and coverage notes

The mapping and model rationale are documented in [Core Rules Foundation contracts](../../doc/CORE_RULES.md). All source-level properties identified in the approved Phase 1 census have explicit mappings: identity and page, edition/SRD flags, nested entries, action timing, skill ability, rule classification fields, reprint relationships, additional-source citations, language metadata, and language-script font paths. Unknown properties are rejected so future source variations require analysis.

The source census remains 594 entities across nine source arrays. The analysis identified no duplicate `(name, source)` identity pairs. Schema validation does not establish cross-document uniqueness, resolve every reference, verify mechanics, or prove source-to-canonical semantic correctness; those checks belong to later conversion and validation phases. The approved Source Registry is referenced by exact source code and was not changed.

## Limitations and review decisions

- `ruleType` values remain opaque source codes; the schema intentionally does not explain them.
- Reprint links retain source representation, including one typed `hazard` target. Review whether later conversion should supplement these with canonical ModelReference objects while keeping provenance.
- The `references` field is optional because not all source text contains entity references. Conversion conventions should define which explicit tags become references and how source-less references remain unqualified.
- `Language.script` is not resolved through the incomplete LanguageScript/font catalog.
- Rich-text validation is structural. It does not interpret embedded tags, resolve navigation, or extract executable mechanics.
- Conditions, diseases, statuses, senses, and complex rule prose remain descriptive in v1. Later semantic findings may justify narrowly scoped contract revisions, but must not be hidden in an untyped mechanic map.

The complete records have not been normalized or integrated. Phase 2 is awaiting review; explicit approval is required before Phase 3 representative conversion.
