# Scaling and higher-level casting analysis

`scalingLevelDice` occurs on 41 spell roots: 36 object-form and 5 array-form source values. The nested scale maps use level-like keys and formula strings; object and array forms require typed variants or a narrowly scoped deserializer. The map keys are source data, not a character-level calculation instruction. Values can contain formulas/placeholders, so they should remain strings unless Stage B explicitly models a validated source expression union.

Cantrip scaling is structurally represented on examples such as Acid Splash, Chill Touch, and Sapping Sting, with keys `1`, `5`, `11`, and `17`. Tasha-era cantrips also show a list form. Structured formula strings may combine dice and spellcasting placeholders; do not evaluate them.

`entriesHigherLevel` occurs on 354 records and contains descriptive entries such as “At Higher Levels” text. This is distinct from the source-authored structured scaling maps. Most spells do not have either structured scaling or a higher-level entry. Preserve both independently; do not parse higher-level prose into mechanics.

The source does not provide a universal, normalized numeric higher-slot scaling schedule. Do not infer higher-level damage, healing, summoned statistics, spell slot use, or character progression from prose or from formula keys.
