# Edition distinctions

Status: comparisons of explicit source fields, now preserved and tested in the Stage B internal definitions. No automatic edition migration is included; see [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Identity and applicability

PHB and XPHB each supply 12 class definitions. TCE adds Artificer and three sidekicks; EFA adds another Artificer; UATheMysticClass supplies Mystic. All class roots declare `classic` or `one`; features do not. A subclass's source, its own declared edition, and its parent class source/edition must be retained separately.

Subclasses occur under PHB parents (120), XPHB parents (190), TCE Artificer (4), EFA Artificer (10), and Mystic (6). The 190 XPHB-parent roots include 120 explicit adaptations of older subclasses and 70 directly authored roots. EFA Artificer includes four TCE-parent copies and six directly authored subclasses, including five EFA-source definitions and one other-source definition. These are observed parent relationships, not a blanket compatibility rule for any older subclass.

130 subclasses declare `classic`, 70 declare `one`, and 130 have no root edition. Absence includes copy roots and other source records; do not assign edition from the publication code alone. Copy metadata policy is discussed in [INHERITANCE-ANALYSIS.md](INHERITANCE-ANALYSIS.md).

## Class subclass-selection milestones

| Class | PHB first flagged level | XPHB first flagged level |
| --- | ---: | ---: |
| Barbarian | 3 | 3 |
| Bard | 3 | 3 |
| Cleric | 1 | 3 |
| Druid | 2 | 3 |
| Fighter | 3 | 3 |
| Monk | 3 | 3 |
| Paladin | 3 | 3 |
| Ranger | 3 | 3 |
| Rogue | 3 | 3 |
| Sorcerer | 1 | 3 |
| Warlock | 1 | 3 |
| Wizard | 2 | 3 |

These levels come from `gainSubclassFeature` wrappers and feature UIDs. The full advancement schedules are in [DISCOVERY-TABLES.md](DISCOVERY-TABLES.md). PHB Cleric additionally flags level 2 and 8, whereas XPHB Cleric flags 3, 6, 17. Seventy-five copied feature roots implement selected earlier-level adaptations, and subclass list overrides deliberately retain some older feature UIDs. Do not impose the class schedule onto every referenced feature.

## Spellcasting

PHB Bard/Ranger/Sorcerer/Warlock known-count arrays become XPHB prepared-count arrays, with authored `level` or `restLong` change timing. Prepared formulas on PHB Cleric/Druid/Paladin/Wizard become explicit XPHB count progressions. XPHB Paladin/Ranger use `casterProgression:artificer` and have level-1 slots; PHB uses `1/2` and level-1 zeros. `pact` remains a distinct Warlock code in both.

Subclass changes include PHB known versus XPHB prepared progressions for Eldritch Knight and Arcane Trickster. Additional spell groups keep unqualified legacy spell names and explicit XPHB UIDs separately; do not rebind the former simply because an adapted parent is XPHB. Structured spell grants remain independent from complete spell-list availability.

## Equipment and proficiencies

Legacy equipment commonly uses several a/b choice groups plus rolled gold text. XPHB and EFA Artificer use larger A/B(/C) packages containing explicit copper values. Preserve every package and currency option. No starting gold is rolled and no package is chosen.

Modern classes add `primaryAbility` structures and class/feat progressions not present on their older counterparts. XPHB roots often omit numeric multiclass ability requirements while retaining proficiency grants or empty multiclass objects. Do not reuse PHB requirements to fill these absences. Weapons, armor, tools, optional firearm flags, and skill choices remain per-definition facts; display and typed proficiency fields must not be double-counted.

## Resources and class-specific scaling

The source tables distinguish PHB Monk's Ki Points and XPHB Focus Points and use different martial arts dice. PHB Barbarian's level-20 Rages cell is `Unlimited`; XPHB's is the string `6`. XPHB tables add resources such as Second Wind, Channel Divinity, Wild Shape, and weapon mastery columns. These differences are preserved as typed/presentation source cells; labels and numeric-looking strings do not become calculated resource rules.

Optional-feature versus feat-based fighting-style progressions differ between older and newer definitions. Keep the source's selector system. A shared public option shape may reference both catalogs, but it must preserve target kind and category semantics.

## Reprints and replacement limits

All 13 class and 77 subclass reprints resolve. They connect independently addressable definitions and do not authorize merging them. `isClassFeatureVariant` flags optional variants without structured replacement edges. Feature replacement semantics described only in text remain unsupported for mechanical execution.

The future public API should filter by source, parent class identity, and separately recorded edition information. Selecting XPHB class definitions must not silently hide authored legacy-parent definitions from the internal import, reassign their sources, or generate unlisted compatibility copies.
