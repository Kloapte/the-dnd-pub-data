# Background Feature and Grant Analysis

`feats` occurs on 84 roots and contains 61 distinct explicit source-qualified feat UIDs, including three Magic Initiate options whose Cleric/Druid/Wizard selection label is retained while the base feat is resolved. Category-choice declarations preserve their category alternatives and are not converted to a specific feat. The `fromFeature` field contains `feats` (15 roots) and `additionalSpells` (5 roots); these are typed source flags, not references to Optional Feature Catalog. No prose-derived feat grants or Optional Feature links are created.

Feature narratives are `entries` with `data.isFeature` metadata. They remain internal typed entries; public definitions expose feature names/metadata without the prose. Roleplaying characteristics are nested tables in entries. Their table category, labels, roll labels, and row count are retained internally; table text is not included in public DTOs and no random selection is implemented.

The source contains 89 distinct feature-marked entry names and 271 table objects across the 171 roots. The table headers include the common roleplaying categories and other background-specific tables. Numeric roll cells are retained as typed integers rather than coerced to prose.

Structured proficiency grants preserve fixed keys, choice groups, source choice counts, and alternatives. Skill and language names are resolved only when unambiguous against their catalogs; tool names remain structured names because no Tool Catalog exists. Spell lists in `additionalSpells` remain source-qualified or unresolved references; no Spell Catalog dependency is introduced.
