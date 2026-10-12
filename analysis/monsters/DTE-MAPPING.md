# Monster DTE mapping

This mapping covers only the `monster` collection. The schema manifest identifies 4,560 monster entities in 111 contributing bestiary files. The independent `legendaryGroup` collection is not imported here; monsters retain its `{name, source}` reference for later resolution.

## Record groups

`MonsterDte` groups the source's flat properties into typed identity, statistics, defenses, features, tags, supplemental display/gameplay data, copy instructions, and alternate versions.

- `MonsterIdentityDte` retains source/page, aliases, edition references, groups, legendary-group references, creature type, alignment, sizes, and summon identity.
- `MonsterStatisticsDte` retains ability scores, saving throws, skills, senses, passive Perception, hit points, challenge rating, speed, initiative, and AC.
- `MonsterDefensesDte` recursively represents plain and conditional resistance, immunity, vulnerability, and condition-immunity values, including notes, restrictions, and nested defense groups.
- `MonsterFeaturesDte` retains traits, actions, bonus actions, reactions, legendary/mythic actions, variants, headers, and spellcasting. `MonsterFeatureDte` stores names and recursive descriptions via the shared `EntryDte` for prose; monster spellcasting blocks in feature entries use a separate typed content variant.
- `MonsterSpellcastingDte` keeps `will`, `daily`, `rest`, `restLong`, `recharge`, `legendary`, and `charges` maps keyed by their original strings, and level-based `spells` keyed by level strings. Spell candidates support strings and `{entry, hidden}` forms. Headers and footers use shared typed entries.
- `MonsterCopyReferenceDte` and `MonsterVersionDte` preserve inheritance and alternate stat-block instructions. Their dynamic modifier property names remain typed map keys. Feature modifications, spell replacement instructions, skill overrides, templates, and version implementations are represented by dedicated record variants; modifiers are retained and not executed.
- `MonsterTagsDte` retains source classifiers for actions, damage, conditions, languages, senses, spellcasting, and traits. `MonsterSupplementalDte` retains gear, environments, treasure, token/art details, SRD flags, and related source annotations.

## Observed polymorphism

- AC values are integer or object forms with AC, source descriptions, conditions, braces, or special text. Hit points retain average, formula, and special rules. CR retains string or object forms including coven/lair values and XP variants.
- Speed modes accept integer or `{number, condition}` forms; alternate speeds and choices retain their own structure. Initiative is integer or proficiency/advantage-mode object. Passive Perception is integer or text.
- Creature type is string or structured type/choice with string or object tags. Alignment items are code strings or structured combinations with chance, special text, or notes. `shortName` is string or boolean.
- Resistances, immunities, vulnerabilities, and condition immunities are strings or nested conditional records. `skill` values are text bonuses, alternative choices, or numeric version/copy overrides.
- Spell-use keys remain distinct (`1`, `1e`, `2`, `2e`, and other observed keys). Spell levels preserve `slots`, `lower`, and the source spell references. Copy instructions may contain replacement pairs instead of spell references; these are modeled as a separate variant.
- Gear and source-reference values support the observed string/object forms. Feature descriptions use the existing recursive entry and table-cell mappings.

The custom root deserializer checks the exact allowed root-property set. Nested Jackson record mappings remain strict, and focused deserializers reject unsupported shapes with property context. No global unknown-property suppression is enabled.

## Boundaries and exclusions

The source transformation structures below are deliberately retained in typed records rather than applied during import:

- `monster[]._copy`: copy identity and modifier instructions.
- `monster[]._versions`: alternate stat-block values and modifier instructions.

Only these paths are intentionally omitted:

- `monster[].hasFluff` and `monster[].hasFluffImages`: presence flags only; the referenced fluff collection/assets are outside this collection mapping.
- `monster[].soundClip`: an external audio/media reference, not part of the playable stat block or monster rule descriptions.

All other discovered root properties are mapped, including source identity and references. The scanner's gameplay classifications are advisory; the strict complete-collection run exercised every present property, while the root allowlist is reviewed against the gameplay report.

## Validation

`MonsterDteTests` discovers the bestiary files dynamically, deserializes each monster without starting Spring, and reports failures with source filename, monster name/index, and Jackson property path. Current result: 111 files, 4,560 successes, zero failures. The test suite also exercises AC/speed/type/alignment/CR/defense variants, recursive descriptions, spellcasting maps, copy operations, source references, and unknown-property rejection.

The full project Spring application-context test has a known unrelated failure: `MonsterController` references the missing `MonstersService`. Import tests remain independent of that context.
