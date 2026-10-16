# Spellcasting source analysis

Status: these structures are implemented internally and tested in Stage B. A Spell Catalog remains unavailable; spell IDs and unqualified sources are not invented. See [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Observed caster metadata

| Owner | `casterProgression` | Occurrences | Source evidence |
| --- | --- | ---: | --- |
| Class | `full` | 10 | PHB/XPHB Bard, Cleric, Druid, Sorcerer, Wizard |
| Class | `1/2` | 2 | PHB Paladin and Ranger |
| Class | `artificer` | 5 | TCE/EFA Artificer, XPHB Paladin/Ranger, TCE Spellcaster Sidekick |
| Class | `pact` | 2 | PHB/XPHB Warlock |
| Subclass | `1/3` | 5 | PHB/XPHB Eldritch Knight and Arcane Trickster, AU Warrior of the Mystic Arts |

These are source codes, not inferred class-name conventions. Preserve `artificer` even on Paladin/Ranger or a sidekick. Do not substitute a mathematically derived caster fraction or rounding rule. Eleven class roots and most subclasses have no caster progression; absence does not prove that all their features lack spell grants.

`spellcastingAbility` is present on 18 classes and nine subclasses. Values are source ability codes. Some subclass ability metadata occurs without `casterProgression` (for example, rituals or innate abilities). Spellcaster Sidekick has progression but no root ability; its specialization text is not a structured ability choice. Do not invent one.

## Numeric progressions and formulas

Cantrips, known spells, prepared spells, fixed additions, and fixed-by-level spells have separate fields; see [PROGRESSION-ANALYSIS.md](PROGRESSION-ANALYSIS.md). Five prepared-spell formulas use level/ability-modifier placeholders and arithmetic, including half-level plus an ability modifier. Preserve exact expressions internally; no rounding, minimum, or evaluation is supplied by this discovery. A future public symbolic expression must use a small validated typed expression structure, never expose executable source templates.

Thirteen classes and three subclasses declare `preparedSpellsChange`, observed as `level` or `restLong`. It is explicit preparation-change timing, not a general resource recharge rule. Nine classes and three subclasses have numeric prepared-count arrays. PHB Wizard lacks `preparedSpellsChange` even though it has a formula; do not fill timing from edition conventions.

## Slots and Pact Magic

Sixteen class and five subclass table groups use `rowsSpellProgression`. Preserve their numeric matrices, row levels, column positions, and source column metadata. When a column's structured filter explicitly supplies `level=N`, a typed spell-level association is supported; do not derive it solely from display text such as “1st”.

Pact Magic has the explicit `pact` code but uses ordinary `rows` tables for slot count and slot level, not `rowsSpellProgression`. PHB slot level cells contain spell filter markup; XPHB uses numbers. Preserve both, with only the numeric source form eligible as a direct numeric value. A normalized spellcasting model must allow missing standard slot matrices and a separate owned Pact presentation progression. Rest-based slot recharge is prose, not a root structured property.

Two Warlock roots carry fixed-by-level spell counts for Mystic Arcanum independently of slots. Do not combine those with regular known/prepared counts or claim that they form higher-level pact slots.

## Additional spell grants

Seven class roots and 133 subclass roots contain `additionalSpells`. Groups may have a `name`, `ability` (nine subclass groups), and `resourceName` (four). Observed grant categories are `known`, `prepared`, `expanded`, and `innate`. Numeric keys encode level thresholds; `s6`–`s9` also occur for spell-level gates in XPHB Bard's expanded list. Preserve the gate kind, number, and original key internally rather than collapsing `s6` into class level 6.

Payloads vary between arrays and objects at the same level. Observed nested usage branches include `_`, `daily`, `rest`, `ritual`, and `resource`. Usage keys include numeric counts, `1e`, and `int`. Keep these tokens typed as distinct usage specifications, including the per-spell qualifier and ability-derived count; do not assume every key is an integer. Direct arrays retain the grant category and level without invented usage limits.

Spell choices use `{choose: filterExpression, count?}` and expanded selections use `{all: filterExpression}`. There are **186 selector occurrences, 102 distinct expressions**, including an empty `choose` string. Empty means an authored selector with no listed restrictions, not a missing choice or an empty list of candidates. Preserve an unrestricted selector type in the public projection so `NON_EMPTY` cannot erase its meaning.

Filters include level sets, class names, school codes, and `source=EGW`. They describe selectors, not confirmed links to every possible matching spell. No fuzzy matching or candidate enumeration is performed. Additional spell UID suffixes such as `#c` carry configuration and must survive parsing separately from name/source.

There are **1,068 direct structured spell UID occurrences**: 446 explicitly source-qualified and 622 unqualified. Stage B also preserves three explicit `choose.from` candidates in FRHoF Scion of the Three, excluded by Stage A's direct-reference counter. Including those candidates gives 1,071 authored spell UIDs (449 qualified, 622 unqualified). All remain deferred for catalog binding; do not synthesize Spell IDs or stamp the owner's source onto unqualified names. A further 1,233 spell markup tags are descriptive links and are not structured grants. The original evidence retains its direct-reference counting scope.

## Rituals, focus, lists, and preparation

Ritual-only innate grants are explicitly represented for some subclasses. General class ritual-casting rules and spellcasting focus rules occur in feature entries, including equipment/rule markup, without dedicated root booleans. Omission of structured ritual/focus fields must not become `false`. Spell lists are primarily selectors in grants or presentation filters; no complete root spell-list catalog is embedded here.

`prepared` and `expanded` remain separate grant meanings. The data distinguishes prepared grants from ordinary class prepared-count arrays, but does not provide one universal always-prepared boolean on every grant. Retain the authored category and conditions; do not derive additional preparation exemptions from prose.

## PHB/XPHB differences

PHB Bard, Ranger, Sorcerer, and Warlock have known-count progressions; XPHB counterparts have prepared-count progressions with explicit change timing. PHB Cleric/Druid/Paladin/Wizard use formulas, while XPHB uses numeric prepared-count progressions. XPHB Paladin/Ranger have `artificer` progression and level-1 slot rows, unlike PHB `1/2`. PHB/XPHB caster definitions and their spell UIDs remain separate. PHB/XPHB Eldritch Knight and Arcane Trickster likewise distinguish known versus prepared counts.

## Normalization before a Spell Catalog

Supported now in the proposed model: caster code, ability, numeric progressions, slot matrices, source-authored typed table cells, grant categories, named option groups, usage/resource tokens, symbolic preparation expressions, UID name/source/configuration, and typed filter selectors. Deferred: binding spell IDs, enumerating lists, validating spell school/level facts against a catalog, computing character slots, or interpreting prose. These dependencies must not prevent retaining complete structured grant information.
