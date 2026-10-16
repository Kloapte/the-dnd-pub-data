# Proficiencies and starting options

Status: source options implemented as immutable typed groups, choices, proficiencies and prerequisites in Stage B. All three item-group references remain typed deferred groups. No character equipment selection or generation is included; see [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Coverage

Twenty-seven classes define hit dice, saving throw proficiencies, starting proficiencies, and starting equipment. Three TCE sidekick classes omit all four; retain absence rather than invent standard class defaults. Twenty-six classes define `multiclassing`; some objects are empty. Thirteen define explicit ability requirements, and 21 define proficiency grants.

## Hit dice and ability requirements

`hd` has integer `number` and `faces`. Class `proficiency` is an array of saving throw ability codes, with 54 values across 27 classes. Thirteen class roots have `primaryAbility`: a list of ability-flag objects, including alternatives and multiple abilities in one object. Preserve grouping. Primary abilities are distinct from saving throws and multiclass prerequisites.

Multiclass requirements use ability thresholds and an `or` array. Fighter's `{or:[{str:13,dex:13}]}` groups alternative ability thresholds; Paladin's top-level `str` and `cha` are simultaneous requirements. Preserve the source grouping in a typed requirement representation. Stage B must cover this unusual OR-object convention rather than treating every object as an implicit conjunction. No prerequisite evaluation is part of the reference catalog.

## Starting and multiclass proficiencies

`startingProficiencies` carries `skills`, `weapons`, `armor`, `tools`, `armorProficiencies`, and `toolProficiencies` where present. Multiclass `proficienciesGained` uses the same conceptual groups, with a smaller observed field set. Keep starting and multiclass contexts separate.

Skill choices use `choose.from` lists plus `count`; unrestricted `any` skill counts also occur. There are 256 explicit skill names in structured `from` lists, all unqualified. Retain the source tokens and selection counts. The Skills Catalog contains edition-specific definitions; names alone cannot establish PHB versus XPHB. Do not assign a source merely because the owning class has one.

Weapon and armor display lists are polymorphic: strings or objects such as `{proficiency:"firearms",optional:true}` and `{proficiency:"shield",full:...}`. The optional firearm grant is explicit. PHB Druid's metal restriction is descriptive `full` text, not a new mechanical armor category. Typed `armorProficiencies` separately supplies boolean categories. Avoid double-counting display lists and structured fields as independent grants.

Tool structures combine fixed boolean keys (`thieves' tools`, `tinker's tools`, `herbalism kit`) and count selectors (`anyArtisansTool`, `anyMusicalInstrument`). Separate array packages can be alternatives, as with Monk's artisan's tool or musical instrument. Preserve these packages, counts, and raw unqualified names; tool labels do not establish Item IDs. Text lists and markup are retained as source presentation.

## Equipment package grammar

All 27 starting equipment objects have `additionalFromBackground` and `defaultData`. Fourteen use legacy `default` and `goldAlternative`; thirteen use `entries` for display prose. The 67 `defaultData` groups use `_`, lowercase `a/b/c`, or uppercase `A/B/C` branch keys. Preserve key spelling and branch order; `_` is an unconditional bundle, while lettered branches are alternatives within a group. Groups remain separate rather than flattening all alternatives into one list.

Observed equipment entry variants:

| Variant | Fields | Normalized proposal |
| --- | --- | --- |
| Direct UID string | name/source UID | Item reference occurrence |
| Item object | `item`, optional `quantity` | Item reference and explicit quantity |
| Equipment category | `equipmentType`, optional `quantity` | Category choice preserving code |
| Multiple categories | `equipmentTypes` array | Alternative category selector |
| Currency | numeric `value` | Copper-denominated source value |
| Special entry | `special` | Unbound named equipment option |

The `equipmentTypes` array is an actual class variation absent from the existing Background equipment entry record. Do not reuse that record unchanged or silently drop the array. Wizard's special `Spellbook` has no source UID and must not be converted into a guessed item identity.

Quantities remain explicit option configuration. When quantity is absent, retain that fact internally; a user interface may present a bundle without the importer materializing character inventory. Currency `value` follows the existing Background convention of copper units; XPHB package display text corroborates values such as 1600 for 16 GP and 15000 for 150 GP. Legacy gold alternatives are dice/markup strings, not structured rolled currency. Preserve them internally without rolling, parsing arbitrary prose, or exposing markup.

## Cross-catalog outcomes

Of 168 direct equipment UID occurrences, 165 resolve to concrete Item/Base Item definitions and three reference auxiliary item groups: `holy symbol|xphb` on Cleric and Paladin, and `druidic focus|xphb` on Druid. Each has one exact `itemGroup` match in the source, but no concrete definition in the existing Item Catalog. Model a retained group choice/deferred catalog target; do not choose a symbol/focus item automatically. One further structured item statblock reference elsewhere in features resolves, giving 169 structured item references overall.

`additionalFromBackground` explicitly links the class equipment procedure to background equipment without identifying a Background definition. No character background is selected by this catalog. Background package modeling supplies useful precedent, not a complete compatible grammar.

Languages have 11 markup links but no root structured language proficiency grant in this corpus. Feature text can mention proficiency grants; this stage does not interpret them. Preserve language links internally without manufacturing character language choices or source identities.

## Edition distinctions

Legacy classes use several independent equipment choice groups and optional rolled gold prose. XPHB classes and EFA Artificer use explicit A/B (occasionally C) packages with fixed currency. Starting tools, armor, and weapons differ between definitions. Keep each authored option tree and class source independent; do not apply modern packages to PHB or sidekick classes.
