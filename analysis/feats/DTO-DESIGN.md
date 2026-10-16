# Feat DTO design

## Definition and identity

Use a source-specific `FeatDefinition` and immutable Feat Catalog. A reusable definition is separate from a character's feat selection, the choice made while selecting it, and the character-specific effects after acquisition. IDs use `feat:<source-slug>:<name-slug>`. Identically named definitions from different sources remain distinct; explicit `reprintedAs` values are references, never merge instructions.

## Catalog architecture decision

Choose separate Feat definitions and catalog, with shared import infrastructure and shared source identity/reference values. A unified mixed catalog would combine unrelated entity semantics and would make prerequisite and grant-specific query behavior ambiguous. A single generic catalog model would hide the substantial feat-specific structures. The application therefore uses a feat-specific catalog, mapper, service, and API.

## Structured model

- `FeatDte` models every observed root source field. Shared `EntryDte` handles prose blocks. Nested prerequisite, ability-increase, proficiency, sense/resistance, additional-spell, additional-source, and version types remain strongly typed.
- `FeatPrerequisiteDte` preserves the source array of requirement groups as an ordered list of complete records. Within each record, fields are simultaneous requirements; array alternatives are retained without flattening. `level` is a typed union of a simple level and the observed class-qualified level form. Prerequisites are not evaluated.
- `FeatAbilityIncreaseDte` captures fixed ability increments, choices and their `from`/`amount`/`count`/`entry` metadata, maximum, and hidden metadata. No character-specific ability choices are made.
- Named proficiency grants preserve dynamic source keys with a sealed value type for booleans, counts, and structured choices. Separate lists remain separate by domain (skill, language, tools, weapons, armor, expertise, saves). Sense distances and optional-feature progressions preserve their typed numeric values.
- `additionalSpells` is a structured source grant, not a resolved Spell reference: filter query strings and dynamic schedule keys are preserved. Version-specific grants remain on their version source model. No Spell catalog lookup is required.
- `_versions` are retained as explicit named/source-qualified variants with typed modification instructions and provenance. They are not expanded into independent, fully rewritten definitions because that would require interpreting source modifications and changing the identity/count model. The API exposes variant identities while provenance retains their typed modifications.

## Public DTO

`FeatDto` and `FeatSummaryDto` are separate public projections. The detail DTO exposes ID, name, `SourceReference` (including page), opaque source `category`, explicit reprint references, safe prerequisite group properties, ability increases, and repeatability. Source grant trees, version identities, and display metadata remain available in the typed internal import record; no references are resolved by fuzzy name matching. `NON_EMPTY` omits absent/empty fields while preserving meaningful `false`, zero, and source strings.

Entries, raw 5eTools text/markup, prerequisite display prose, publication flags, image flags, diagnostics, and import lineage are excluded. Skill, language, class, race, feat, spell, and optional-feature names remain source values where no reliable source-aware target identity is present; the mapper does not fuzzy-link other catalogs. Structured spell grants are preserved internally as grant configurations with unresolved names/queries rather than requiring a Spell Catalog.

## Boundaries

No character progression, feat selection, choice resolution, prerequisite evaluation, effects engine, or source-name merging is part of this model. Descriptive text may mention additional mechanics; only dedicated source fields are normalized as mechanical properties. Publication review is separate from import correctness.

