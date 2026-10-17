# Normalization and Public Projection Gaps

## Method and confidence

Findings below combine parsed source predicates in `audit-candidates.mjs` with direct inspection
of DTEs, normalized records, mappers and public DTO construction. Counts describe authored roots;
the scanner does not expand copies/versions or measure serialized runtime responses. All affected
root identities and sample field locators are in `candidate-evidence.json`. These are findings
for future work, with no production edits in this delivery.

## G1: Class table column positions are lost in the public projection

**Confirmed correctness issue; highest implementation priority.**

`ClassMechanicsNormalizer.cell()` labels string cells `PRESENTATION_TEXT` and keeps numeric/bonus/
dice/speed cells typed. `ClassPublicMapper.safeTables()` removes presentation cells from each row
but retains the original columns. `ClassTableCell` and `ClassTableRow` contain no column index.
Thus any retained cell after a removed cell shifts to a different implied column.

Reproduce PHB Barbarian in `classes/class-barbarian.json`:

- Owner: `Barbarian | PHB`, `$.class[0].classTableGroups[0]`.
- Columns: `Rages`, `Rage Damage`.
- Source row zero: string `"2"`, then `{type:"bonus", value:2}`.
- Current public row has one BONUS cell at list index zero while both column labels remain.
  The source Rages string is deliberately withheld; the Rage Damage bonus has lost its position.

The query identifies **97 authored rows across six roots**: Artificer EFA, Barbarian PHB,
Barbarian XPHB, Warlock PHB, Psi Warrior XPHB and Soulknife XPHB. Class rows account for 61,
subclass rows for 36. Copy-effective impact must be measured in the future fix.

The correction should use explicit source column positions or a reviewed placeholder representation.
It must not interpret numeric-looking strings or expose withheld presentation text merely to fill
holes. The public contract change needs an explicit compatibility decision; silently retaining the
current ambiguous array is not an acceptance criterion.

Code evidence:
[normalizer](../../src/main/java/com/thedndpub/data/importer/classes/ClassMechanicsNormalizer.java),
[public mapper](../../src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java),
[cell](../../src/main/java/com/thedndpub/data/dto/classes/ClassTableCell.java),
[row](../../src/main/java/com/thedndpub/data/dto/classes/ClassTableRow.java).

## G2: Item focus array values do not reach the normalized definition

**Confirmed normalization omission.** `ItemStringListDeserializer` models an array as
`values=[...]`, `enabled=null`; a Boolean populates `enabled`. `ItemMapper.focusEnabled()`
returns only `enabled`. `ItemDefinition`/`ItemDto` expose only `focusEnabled` for this field.

**52 Item roots** use arrays; two other roots use Booleans. Example:
`+1 All-Purpose Tool | TCE`, `items.json`, `$.item[0].focus = ["Artificer"]`.
The original array remains in import provenance, so source data is retained but normalized
mechanics omit an explicit restriction. Do not turn the class-name label into a Class target ID
or infer a class source.

Propose an internal typed focus value preserving Boolean and ordered label-array variants. A
future public addition requires field-level publication review and compatibility with the existing
flag. This does not evaluate eligibility or modify the Item's base statistics.

Code: [DTE](../../src/main/java/com/thedndpub/data/dte/item/ItemStringListDte.java),
[deserializer](../../src/main/java/com/thedndpub/data/dte/item/ItemStringListDeserializer.java),
[mapper](../../src/main/java/com/thedndpub/data/dto/item/ItemMapper.java).

## G3: Feat source mechanics remain import-only

**Confirmed domain coverage gap, not a source parsing failure.** `FeatDte` types all fields below;
`FeatMapper.map()` and `FeatDefinition` omit them. `FeatCatalog` retains the root DTE in
`FeatImportRecord`. The existing shared reference extractor can preserve reference occurrences
without making those mechanics part of the normalized definition.

| Field | Authored roots |
|---|---:|
| `skillProficiencies` | 12 |
| `expertise` | 7 |
| `languageProficiencies` | 7 |
| `toolProficiencies` | 10 |
| `weaponProficiencies` | 4 |
| `armorProficiencies` | 6 |
| `savingThrowProficiencies` | 2 |
| `skillToolLanguageProficiencies` | 2 |
| `conditionImmune` | 2 |
| `immune` | 2 |
| `resist` | 11 |
| `senses` | 4 |
| `bonusSenses` | 1 |
| `optionalfeatureProgression` | 4 |

The union is **59 roots**, with **74 field occurrences**; sums are not distinct-record counts.
`Aberrant Anatomy | RHW`, `feats.json`, `$.feat[0]` has fixed Perception proficiency,
Perception expertise and blindsight 15 in separate fields. `Artificer Initiate | TCE`,
`$.feat[22].toolProficiencies` demonstrates that choice structures must also be preserved.

Normalize these domain values before proposing prose extraction. Reuse Race/Background/Optional
Feature primitives only after exact representation and meaning comparisons. Preserve choice
counts, alternatives, category filters and versions; assess all 314 effective Feats. Do not infer
proficiency sources, spell grants or unconditional benefits from filters.

Code: [source record](../../src/main/java/com/thedndpub/data/dte/feat/FeatDte.java),
[definition](../../src/main/java/com/thedndpub/data/dto/feat/FeatDefinition.java),
[mapper](../../src/main/java/com/thedndpub/data/dto/feat/FeatMapper.java),
[catalog](../../src/main/java/com/thedndpub/data/services/FeatCatalog.java).

## G4: Feat prerequisite proficiency categories are flattened

**Confirmed structural loss in normalized prerequisites.** On **12 roots**, the source
`prerequisite[].proficiency[]` object has typed `armor`, `weapon` or `weaponGroup` fields.
`FeatMapper.mapPrerequisites()` joins their values with `;` and returns `List<String>`, losing
the field/category identity. Example: `Fighting Initiate | TCE`, `feats.json`,
`$.feat[114].prerequisite[0].proficiency[0]`.

Keep source AND/OR grouping and typed category keys. Do not interpret the joined strings back
into categories; normalize from the source DTE. Prerequisite display text intentionally withheld
for publication is a separate issue. The hypothesis that race/background prerequisite *source
codes* were dropped was checked and found **zero authored occurrences**; those records do not
provide source fields, so no such defect is asserted.

## G5: Existing Item charge JSON contains raw source markup

**Confirmed publication-contract discrepancy; not a newly approved field.** There are **220
authored Item roots** with `{@...}` in root `charges` or `rechargeAmount`. The mapper forwards
these strings to `ItemChargeDto.countText`/`rechargeAmountText`, and `ItemDto.from()` forwards
the DTO. Example: `Abracadabrus | IDRotF`, `items.json`,
`$.item[43].rechargeAmount = "{@dice 1d20}"`.

This contradicts an unqualified statement that Item JSON never exposes raw markup. It does not
mean every charge field is unsafe or every Item requires suppression. A future batch should
retain exact expressions internally, replace only reviewed public fields with typed syntax if
authorized, and define compatibility explicitly. Do not strip braces and claim effect semantics.

Code: [charge DTO](../../src/main/java/com/thedndpub/data/dto/item/ItemChargeDto.java),
[mapper](../../src/main/java/com/thedndpub/data/dto/item/ItemMapper.java),
[public DTO](../../src/main/java/com/thedndpub/data/dto/item/ItemDto.java).

## G6: Optional Feature public prerequisites carry authored text

**Confirmed current exposure needing publication review.** Seven roots have **14 text fields**
under `prerequisite[].spell[].entry/entrySummary` or
`prerequisite[].otherSummary.entry/entrySummary`. These pass through
`OptionalFeatureMapper` into public nested records, then `OptionalFeatureDto.from()` without a
dedicated safe projection. Example: `Agonizing Blast | XPHB`, `optionalfeatures.json`,
`$.optionalfeature[1].prerequisite[0].spell[0].entry`.

This is narrower than a claim that full Optional Feature descriptions are public: main `entries`
remain internal. The actual text may be concise metadata, but that status is not established by
its Java type. Review each field/source and document authorization or withholding. Do not turn
prerequisite text into a filter or omit an eligibility restriction without a presence marker.

Code: [mapper](../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureMapper.java),
[text record](../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureOtherRequirementDto.java),
[public DTO](../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureDto.java).

## G7: Monster public/internal content boundaries differ from later catalogs

**Confirmed existing API design; authorization is an open question.** `MonsterDto` is both the
normalized application model and public detail response. It contains `Features`, each `Feature`
contains rich `entries`, and `PlainText.text` serializes source text. Spellcasting headers,
footers, original reference strings and typed source-property extensions are also public members.
The catalog contract covers **4,851 effective definitions**; this analysis does not claim that
each one has every exposed content field or count serialized violations from source roots.

Example source: `Fiendish Icon | ABH`, `monsters/bestiary-abh.json`,
`$.monster[0].action[2].entries[0]` contains save/branch/damage markup preserved in Feature entries.
Existing Monster tests intentionally cover retained content. The later catalog rule of withholding
descriptive entries cannot be presumed to apply to this API already.

Review publication scope before expanding Monster exposure. A dedicated public projection could
eventually separate stats from retained content, but this is a deliberate contract/migration
decision and must not be smuggled into an annotation parser change.

Code: [MonsterDto](../../src/main/java/com/thedndpub/data/dto/monster/MonsterDto.java),
[Feature](../../src/main/java/com/thedndpub/data/dto/monster/features/Feature.java),
[PlainText](../../src/main/java/com/thedndpub/data/dto/monster/content/PlainText.java),
[spellcasting](../../src/main/java/com/thedndpub/data/dto/monster/spellcasting/SpellcastingBlock.java).

## G8: Vehicle top-level statistics differ between summary and detail

**Confirmed projection consistency opportunity.** `VehicleSummaryDto.from()` projects top-level
AC and HP using `VehicleDtoProjection`; `VehicleDetailDto` has no corresponding top-level
components. Four AC fields and eight HP fields occur on **eight roots** (12 field occurrences),
and they are already normalized in `VehicleMechanics`. This is not lost import data and does not
require prose extraction. Consider a separately approved additive detail projection using the
same existing types. Do not confuse hull/component HP with top-level HP, and do not infer values
for the remaining vehicles.

Code: [summary](../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleSummaryDto.java),
[detail](../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleDetailDto.java),
[mechanics](../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleMechanics.java).

## Preserved but intentionally withheld: not defects

- Spell casting-condition/material/consume prose is internal; public presence, numeric cost and
  Boolean consumption metadata avoid publishing the text. All 41 structured scaling records and
  78 explicit ranged geometries are already normalized/public.
- Objects retain seven explicit attack entry structures but publish only safe statistics.
  A special HP string is intentionally represented as a presence marker, not a guessed number.
- Vehicle component movement/control/action descriptors are retained internally; public component
  statistics are deliberately narrower. Component speed records contain mode plus entries, not
  numeric speed leaves; do not propose extracting a component speed from those strings.
- Conditions/Diseases, Facility benefits, Reward non-spell effects, Upgrades and Variant Rules
  retain descriptive mechanics internally. The absence of public effects is not lost source data.
- Class feature choices remain explicit, even where flat reference convenience lists also contain
  option IDs. Those lists must not be interpreted as guaranteed grants; this review does not
  assert that the original choice trees are lost.
- Source DTEs in later normalized records preserve full authored structures for fidelity. Some
  duplicate identity/mechanics data is intentional provenance; a broad retention refactor is not
  prioritized without measurements and catalog-specific requirements.
