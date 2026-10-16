# Class level progression

Status: authored progression discovery, implemented as immutable typed owned values in Stage B. Dense/sparse forms, source ordering, table variants and zero values are tested. No character-level calculation or generic rules engine is included; see [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Four distinct representations

| Representation | Observed source | Recommended treatment |
| --- | --- | --- |
| Explicit level-indexed mechanics | cantrip/known/prepared arrays, fixed spell maps, optional/feat progression | Typed owned progressions with explicit level associations |
| Feature unlocks | class/subclass feature UIDs, feature `level`, subclass advancement wrappers | Ordered references and unlock configuration |
| Presentation tables | `classTableGroups`, `subclassTableGroups`, embedded entry tables | Preserve cells and column identity; do not turn every column into a rule |
| Descriptive rules | feature entries and markup | Internal typed rich content; no inferred mechanical execution |

## Feature advancement

Classes contain 645 progression occurrences; subclasses explicitly contain 1,115. Levels come from UIDs and corresponding definition levels, never array position. Preserve list order even when several features share a level. `gainSubclassFeature` identifies 108 advancement occurrences. The first flagged class feature gives a source-supported selection milestone for that class; later flags indicate further subclass advancement. It does not prove that every inherited subclass's first feature has been moved to that level.

[DISCOVERY-TABLES.md](DISCOVERY-TABLES.md) lists all 30 class schedules. PHB Cleric starts at 1, Druid/Wizard at 2, Sorcerer/Warlock at 1; their XPHB class schedules start at 3. Bard, Barbarian, Fighter, Monk, Paladin, Ranger, and Rogue start at 3 in both. Mystic starts at 1. Sidekick classes have no subclass advancement flags.

## Owned tables

Twenty-seven class roots contain **47 table groups**: 31 `rows`, 16 `rowsSpellProgression`. Seven subclass roots contain **12 groups**: seven `rows`, five `rowsSpellProgression`. Every group has exactly 20 rows. The 21 spell matrices contain numeric slot counts; zero is meaningful. Check dimensions against column labels and preserve original order. Stage B should validate row length and numeric ranges rather than assume all future tables have 20 rows.

Across ordinary table groups, cells comprise 1,282 numbers, 158 strings, 40 typed `bonus` objects, 100 typed `dice` objects, and 40 typed `bonusSpeed` objects. Dice carry `toRoll` arrays of `{number,faces}` and `rollable`. Strings include numeric-looking counts, dashes, `Unlimited`, dice markup, and spell filter markup. None should be silently converted into numeric counts, infinity, dice mechanics, or slot levels.

Groups have `colLabels` and optional `title`. Subclass groups also carry explicit `subclasses` name/source selectors, scoped to the owning class. Preserve those selectors even when a table appears on one subclass root. Feature entries contain another 144 `table` objects, often roll tables; these are not class-level matrices simply because they contain rows.

## Structured progressions

- `cantripProgression`: 15 class and five subclass arrays, each with 20 numeric values.
- `spellsKnownProgression`: five class and two subclass arrays.
- `preparedSpellsProgression`: nine class and three subclass arrays.
- `spellsKnownProgressionFixed`: two class arrays, separate from ordinary known spells.
- `spellsKnownProgressionFixedByLevel`: two maps of class-level thresholds to spell-level/count maps (11→6, 13→7, 15→8, 17→9, each count 1).
- `spellsKnownProgressionFixedAllowLowerLevel`: two boolean flags; preserve independently.
- `preparedSpells`: five symbolic formula strings, with no evaluation in this stage.

Arrays author values for levels 1–20; source position 0 is level 1. The normalized representation should explicitly associate each value with its level and retain zeros. Do not replace absence with a synthesized zero progression.

## Optional feature and feat progression

Eight classes carry nine optional progression groups; eight subclasses carry eight groups. Three class groups use 20-element arrays. Six class groups and all eight subclass groups use sparse numeric-level objects. Entries carry `name`, `featureType` selectors, and `progression`; one subclass group also has level-indexed `required` UIDs.

Sparse values such as Battle Master 3→3, 7→5, 10→7, 15→9 represent the authored milestones. Retain sparse versus dense source form internally. Do not add the values as independent grants, fill absent levels, or invent replacement permissions. The public design can expose milestones with `count` and typed category selectors; character selection semantics remain future work.

Thirteen class roots have 16 feat progression groups; one subclass has one. Fields are `name`, `category` arrays, and sparse `progression`. These include level-19 Epic Boons and fighting-style categories. They select existing Feat categories rather than define new feats. Different category systems (`featureType` and feat `category`) must remain distinct.

## Resources and proficiency bonus

Resources/scaling visible in tables include rages, rage damage, Bardic dice, Channel Divinity, Wild Shape, Second Wind, weapon mastery counts, martial arts dice, Ki/Focus Points, unarmored movement, Psi Points/limits, Sorcery Points, invocations, and psionic dice. Some cells are fully typed; others only have presentation labels. Retain all table structure internally and project independently meaningful typed cells without assigning mechanics from English labels.

Feature `consumes` objects explicitly name resources and sometimes specify amounts/ranges. They do not define a complete resource pool or recharge schedule. Proficiency bonus references and recharge rules also occur in entries; there is no dedicated root proficiency-bonus progression or generalized recharge model. Do not mistake class `proficiency` for proficiency bonus: it contains saving throw ability codes.

No multiclass character progression, combined spell slots, ability modifier evaluation, resource calculation, or character-specific state belongs in these catalogs.
