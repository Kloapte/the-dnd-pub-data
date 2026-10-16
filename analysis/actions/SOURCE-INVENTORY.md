# Actions source inventory

Analyzed the complete packaged source `src/main/resources/data/actions.json`. It contains one root collection, `action`, with **48 records and 48 unique `(name, source)` identities**. The machine-readable [field inventory](normalized-field-inventory.json) lists every observed property path, type and count, entry form, markup tag, and source registry result.

## Sources and source relationships

Primary source counts: PHB 20, XPHB 18, DMG 8, XGE 2. All four source codes resolve in the shared Source Registry. Nineteen definitions have `reprintedAs`; every target source is XPHB. Most targets are strings in `Name|Source` form. Two are structured `{uid, tag}` references with tag `variantrule`, targeting `Unarmed Strike|XPHB`; preserve the tag rather than treating these as ordinary action targets. No secondary source property, copy directive, or version-expansion structure occurs. Eight records have `fromVariant` labels (such as `Action Options`, `Healing`, and `Spellcasting|XGE`); this is source lineage/presentation metadata, not an action-definition inheritance instruction. No source identities need to be added.

## Fields and optionality

All 48 records have `name`, `source`, `page`, and `entries`. `time` occurs on 47 records and is an array; one record (Mark, DMG) has no time. `srd` occurs 20 times, `basicRules` 21 times, `srd52` 17 times, and `basicRules2024` 5 times. `reprintedAs` occurs 19 times, `seeAlsoAction` 11 times, and `fromVariant` 8 times. No explicit nulls were observed. Availability flags and `fromVariant` are import metadata, not public definition fields.

Entry blocks reuse shared 5etools string and typed entry structures: `entries` (15), `table` (3), `inset` (1), and `list` (1), including nested `entries`, table labels/styles/rows/captions. The shared entry parser validates these structures and preserves inline markup in provenance.

## Action timing and mechanics

The source explicitly contains structured `time` alternatives. Object values have a numeric `number` (always 1 in this corpus) and a `unit` of `action`, `bonus`, or `reaction`. Six additional values are strings: `Free` (2), `Varies` (4). Some definitions have multiple alternatives (Identify a Spell, Overrun, and Tumble). The data therefore supports preserving source timing values as structured timing alternatives, but not a general action-economy engine or a speculative `ActionCost` enum. The `time` field is absent for Mark. No separate action, bonus-action, reaction, free-interaction, or out-of-combat root classifications exist.

Names and timing are the only directly structured action concepts. Prerequisites, ability checks, skill checks, saves, targeting, range, duration, and effects occur within entries and are not normalized as mechanics. `seeAlsoAction` is an explicit list of related-action strings, but its values mix unqualified names and source-qualified names; it is retained in import provenance without guessing missing source identities.

## Cross-entity markup and publication

Inline tags include `action` (16), `subclassFeature` (1), `status` (11), `book` (8), `variantrule` (30), `skill` (48), `quickref` (1), `condition` (13), `table` (1), `item` (4), `note` (1), `dc` (2), `spell` (1), and `dice` (1). These are embedded in descriptive entry strings, not independent typed references. No `sense`, `language`, or disease tags occur. Ordinary mentions cannot be resolved reliably from text. No markup parser or cross-catalog relationship is introduced.

Entries include potentially copyrighted rules prose and raw 5etools tags. They remain internal import provenance and are omitted from public DTOs pending publication approval. The source has no separate creature action availability or execution state. Monster feature/action records live in their own stat-block model and are not merged with these general action definitions.
