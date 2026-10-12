# Spell DTE mapping

`com.thedndpub.data.dte.spell.SpellDte` is the raw 5eTools import representation. It is independent of Spring and contains no domain transformation or gameplay calculations. `com.thedndpub.data.dte.common.entry` contains reusable recursive entry and table-cell types intended for later collection mappings.

## Preserved fields

The DTE covers the schema's spell properties, including identity/source fields, casting time, range, components, duration, level and school, descriptions and higher-level descriptions, attacks, scaling, damage, conditions, saves, and spell tags/references. Source identifiers (`source`, `page`, additional/other/reference sources, and reprint identifiers) are retained to distinguish content and resolve links. Inline tags remain part of their original strings.

Entry strings and structured objects are represented by `TextEntryDte` and `StructuredEntryDte`, recursively. Structured entries retain known `type`, `name`, `by`, caption/style, labels/styles, nested entries/items, table rows, and source/page data. Table strings and roll cells have distinct types. Unknown entry or table-cell properties are rejected with path context instead of dropped.

## Deliberately excluded properties

Only these root spell flags are excluded as 5eTools catalog/presence indicators; they do not describe spell rules or identify a source entry:

- `spell[].basicRules`
- `spell[].basicRules2024`
- `spell[].hasFluff`
- `spell[].hasFluffImages`

They are explicitly listed in `@JsonIgnoreProperties` on `SpellDte`. No other unknown property is ignored. `srd` and `srd52` are retained as `SpellCatalogReferenceDte`: the boolean form records catalog inclusion, while the string form retains observed alternate names. `source`, `page`, source lists, and rule-bearing `meta.ritual` are also retained.

## Polymorphic representations

- `components.m`: `MaterialTextDte` for a string, `MaterialDetailsDte` for an object.
- `components.m.consume`: `ConsumeFlagDte` for boolean, `ConsumeNoteDte` for string.
- `scalingLevelDice`: object and array source forms normalize to `List<ScalingLevelDiceDte>`; level-keyed formulas remain `Map<String, String>`.
- Recursive entries and table cells use focused deserializers that validate each observed shape and reject unexpected JSON types/keys.
- Observed structured entry `type` values are `entries`, `inset`, `item`, `list`, `quote`, and `table`; new values fail validation for explicit review.

## Validation

`SpellDteTests.deserializesAllSpellsFromAllSourceFiles` streams all 18 files and checks all 969 spells. Other tests cover material and consume variants, structured and recursive entries, table roll cells, markup preservation, and rejection of new top-level or entry properties. Failures include the source filename, entity index, and Jackson path when available.
