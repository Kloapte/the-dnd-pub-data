# Step 3 — Equipment Foundation: Phase 2 Schema Design

**Status:** Phase 2 schema revision approved on 2026-10-29. Phase 3 execution and its approval gate are recorded in the Phase 3 report.

## Revision audit

This revision removes the separate Base Item schema and `recordKind` discriminator. Base items and catalog items now share one `item` schema, one `item:<source>:<slug>` identity form, and `targetType: item` for all item/base-item/content references. The Phase 1 collision audit found no `(name, source)` collisions between the base and item arrays.

Category validation now requires a weapon component for `WEAPON`, an armor component with AC for `ARMOR` and `SHIELD`, and a mount component for `MOUNT`; those components are rejected for incompatible categories. The source audit found no category cross-component exceptions. Weapon damage is mandatory except the source-supported PHB Net with its restraint procedure.

The Item contract now reuses common `sourceMetadata`, canonical `reference`, and `damageRoll`. Weapon property and mastery links remain locally typed because the shared reference enum does not include those target types. Property code reprints are represented as typed property links outside common source metadata because their source values are abbreviations rather than entity IDs. Mastery definitions were checked against all eight XPHB source descriptions: the schema now records Cleave's second-target reach, Nick's Light-property trigger, Push's direction, and Topple's dynamic attack-ability DC accurately. Topple reuses the common Condition reference; the dynamic DC stays local because it is not expressible by the common DC schema.

## Approval basis and boundary decision

This design uses the approved Phase 1 report and its complete record inventory. The Step 3/Step 9 boundary is now defined by source semantics, not by a single source flag:

- **Step 3:** the 230 `items-base.json $.baseitem` identities; source definitions needed to model ordinary equipment categories and weapon properties/masteries; and `items.json $.item` identities whose source-supported function is ordinary equipment and whose operative mechanics do not depend on magic, supernatural item-specific powers, or generated magic variants. This includes mundane weapons, armor, shields, ammunition, tools, gear, containers, mounts, ordinary consumables, and purchase-only equipment facts. Records retain separate source-qualified identity across editions.
- **Step 9:** all 230 `magicvariants.json $.magicvariant` definitions and its `linkedLootTables`; magical/advanced item records from `items.json` whose operation depends on supernatural powers, attunement, spell effects, magical charges, curses/sentience, evolving/vestige states, or generic magic-item inheritance; and the advanced item-specific effects of those records. This follows Step 9's roadmap assignment of advanced equipment and magical variants. Vehicle and object stat-block systems remain outside Step 3; their eventual assignment follows the roadmap domain scope when those steps are authorized and is not reassigned here.
- **Rarity is evidence, not a selector.** `rarity: none` includes exceptional powered or spell-linked records, while `unknown` includes ordinary gear and some magic items lack a rarity field. `rarity`, `wondrous`, and type codes therefore cannot assign scope on their own. During later conversion, Codex records each identity's disposition from its exact source entry in the accounting manifest. Ambiguous magic/advanced cases are deferred to Step 9 with source locators rather than guessed into Step 3.
- `items.json` entries that are ordinary land transport may retain their purchasable equipment identity and simple item facts. Vehicle and object stat blocks and operation remain outside this step; do not duplicate them or assign them to a roadmap step beyond existing approved scope.
- `itemGroup` is a source grouping/index, not a canonical game entity. Pack composition is represented by typed item contents; meaningful choices are represented on the owning mechanic where evidenced. Generated or hidden group labels do not create fictional item identities.
- `itemEntry` templates, item-type descriptions, fluff, display, and loot-table selection are source interpretation aids or presentation/generation data. Their mechanical meaning is normalized into the owning typed contract or an existing supported Rule/Action/Skill reference; the original template/prose is not copied into canonical records. The `Evolving Item` and `Vestige` properties are Step 9 material.

This rule keeps equipment foundations in Step 3 while assigning advanced magic-item expansion to the existing Step 9 scope. It does not add a new domain or expand the roadmap sequence. The future record-level manifest remains necessary because source rarity/type metadata is not sufficient to decide every item.

## Proposed canonical model

| Schema | Responsibility |
|---|---|
| [`item/schema.json`](../../../src/main/resources/schemas/item/schema.json) | The sole Item contract for both `items-base.json` base-item records and in-scope `items.json` records. Both use `item:<source>:<slug>` identities and `targetType: item`; base-item links point to this same Item model. Common fields include source metadata, category, price, weight, and aliases; typed components hold weapon, armor/shield, mount, pack/container, focus, and poison facts. The Phase 1 identity audit found no current `(name, source)` collision across the two source arrays, so an artificial source-kind discriminator is unnecessary. |
| [`item-property/schema.json`](../../../src/main/resources/schemas/item-property/schema.json) | Source-qualified, typed semantics for the ordinary weapon properties, including source-specific PHB/XPHB/DMG/XDMG distinctions. Property references link to these definitions instead of embedding copied entry prose. |
| [`item-mastery/schema.json`](../../../src/main/resources/schemas/item-mastery/schema.json) | The eight XPHB weapon masteries as bounded semantic variants. Weapons reference these records by stable ID. |
The separate Base Item schema has been removed. Existing common `sourceMetadata`, canonical `reference`, and `damageRoll` definitions are reused directly where their target types and semantics fit. Property and mastery references retain narrow typed contracts because their target types are not in the shared reference vocabulary. No shared schema was changed. The model introduces no universal effects object, database-driven indirection, or runtime rules execution.

### Representative schema examples (illustrative, not canonical data)

```json
{
  "id": "item:phb:longsword",
  "name": "Longsword",
  "sourceMetadata": {"source": "PHB", "page": 149},
  "category": "WEAPON",
  "sourceTypeCode": "M",
  "edition": "classic",
  "price": {"amount": 1500, "unit": "copper-piece"},
  "weight": {"amount": 3, "unit": "pound"},
  "weapon": {
    "category": "martial",
    "damage": {"kind": "DAMAGE_ROLL", "damageTypes": ["slashing"], "base": {"kind": "DICE", "count": 1, "die": "d8"}},
    "properties": [{"targetType": "item-property", "id": "item-property:phb:versatile"}]
  }
}
```

The matching XPHB item remains a distinct source-qualified identity and can carry its own mastery reference. A catalog item’s `baseItem` reference points to that base item using the same Item identity type, without embedding it. A representative Topple mastery records the Constitution save, its source-defined DC formula, and the Prone Condition reference as data; it does not resolve the save or apply the Condition at runtime. The dynamic attack-ability DC remains a bounded equipment-specific structure because the shared DC contract does not express that source formula.

### Category and component constraints

| Item category | Required component | Incompatible components |
|---|---|---|
| `WEAPON` | `weapon` | `armor`, `mount` |
| `ARMOR` | `armor` with `light`, `medium`, or `heavy` subtype and source AC | `weapon`, `mount`, shield subtype |
| `SHIELD` | `armor` with `shield` subtype and source AC | `weapon`, `mount`, armor subtype |
| `MOUNT` | `mount` | `weapon`, `armor` |
| Other categories | None of those three components | `weapon`, `armor`, `mount` |

No cross-component exception appeared in the Phase 1 source audit. The source-supported exception is within the weapon contract: the PHB Net has no damage field and instead has its restraint procedure, so the schema permits missing weapon damage only when `NET_RESTRAINT` is present. No other weapon source candidate is exempted.

## Structured-field coverage decisions

Every source field and its observed frequency is enumerated in the approved Phase 1 inventory. The field disposition is:

| Source field families | Phase 2 destination or disposition |
|---|---|
| `name`, `source`, `page`, `srd`, `srd52`, `basicRules`, `basicRules2024`, `additionalSources`, `referenceSources`, `reprintedAs` | Stable `item:<source>:<slug>` identity and the shared `sourceMetadata` contract (exact source code, page, source flags, citations, and typed reprint links). Reprint targets with insufficient direct identity evidence remain in external accounting. Property code reprints are accounted separately as typed property links because source values are abbreviations, not entity IDs. |
| `type`, `typeAlt`, `edition`, `age`, `rarity`, `alias`, `group`, `weapon`, `armor`, `weaponCategory`, `scfType` | Canonical equipment category, edition, weapon-era/focus facts, source-supported aliases, and source-type code where mechanically useful. Boolean type markers are consolidated into typed category/weapon/armor data rather than copied as flags. `rarity` is retained only for in-scope nonmagical records as `none` or source `unknown`; it does not classify scope. Source grouping/filter tags are excluded. |
| `value`, `valueRarity`, `weight`, `weightNote`, `barDimensions` | Numeric price in copper pieces (fractional copper values retained), source-provided market-value rarity where no numeric price is supplied, weight in pounds (with a condition qualifier such as “full” retained), and physical dimensions in inches. No price conversion or rounding is performed. `valueRarity` is distinct from item magic rarity; it appears on price-by-rarity firearms/explosives as well as other advanced items. |
| `dmg1`, `dmg2`, `dmgType`, `range`, `ammoType`, `reload`, `property`, `mastery`, `ammo` flags | Typed weapon damage using the existing common `damageRoll`; normal/long or single-distance range; typed ammunition and property references; shots-before-reload; and weapon-mastery references. Source damage codes map to the approved damage vocabulary. Nonstandard scalar ranges retain their distinct `SINGLE_DISTANCE` form rather than inventing a normal/long split. |
| `ac`, `strength`, `stealth`, `dexterityMax` | Armor class, minimum Strength, and Stealth disadvantage for in-scope armor. `dexterityMax` occurs on an advanced magical armor record and is Step 9, not a Step 3 schema field. |
| `packContents`, `atomicPackContents`, `containerCapacity`, `carryingCapacity`, `light`, mount `speed` | Typed item-content references and quantities, atomic packaged contents (Ball Bearings and Caltrops), container limits, light radii/durations, and mount carrying/speed facts. Vehicle stat-block fields (`vehAc`, `vehHp`, `vehSpeed`, damage thresholds, crew/cargo/passenger, ship dimensions and movement) remain outside this equipment contract; do not duplicate them as equipment facts. |
| `poison`, `poisonTypes` | Preserve the poison designation and all observed administration forms (`ingested`, `inhaled`, `injury`, `contact`) as typed item data. Save, damage, condition, onset, and duration effects embedded in entries still require manual interpretation and source accounting; the schema does not execute them. |
| Weapon property `Special` and its four mundane examples | Store bounded typed mechanics on the owning weapon for the Double-Bladed Scimitar follow-up damage, Hoopak alternate ranged damage/ammunition and melee exception, Lance close-target disadvantage and unmounted hands requirement, and Net restraint, escape, damage-to-net, immunity limits, and one-attack limit. Lance reach and ordinary weapon facts remain in their existing typed fields. `Special` is not represented as an executable generic effect or as a redundant standalone item-specific property entity. |
| `itemProperty` | The 25 standard source-qualified weapon-property definitions map to `item-property` records. Their source differences are explicit variants; `Evo` and `Vst` are deferred with advanced magical items. |
| `itemMastery` | All eight definitions map to `item-mastery` records with bounded typed mechanics and Condition references. |
| `itemType`, `itemTypeAdditionalEntries`, `itemEntry` | Type codes inform the closed equipment-category vocabulary. Tool-proficiency and related statements refer to existing Rule/Skill concepts where an exact target exists; display/explanatory prose and substitution templates are not canonical fields. Mechanical portions not already represented must be source-accounted, not copied. |
| `entries`, `additionalEntries`, `detail1`, `detail2` | Offline interpretation inputs only. Supported facts go into typed equipment fields or bounded mechanics; descriptive material is classified externally as nonmechanical. No raw text or template field is allowed in canonical JSON. |
| `itemGroup`, `miscTags`, `hasFluff`, `hasFluffImages`, `hasRefs`, `lootTables`, `lootTables`/`linkedLootTables` | Group/filter/display and loot-generation structures are not equipment mechanics. Group relations are represented only when the source establishes a real bundle/choice; generic variants and loot generation are Step 9. Fluff/navigation flags are excluded. |
| Magical-effect fields in `items.item` (`ability`, `attachedSpells`, `bonus*`, `charges`, `recharge*`, `reqAttune*`, `resist`, `immune`, `conditionImmune`, `curse`, `sentient`, `tier`, `wondrous`, `optionalfeatures`, magical `classFeatures`/`light`/speed changes, etc.) | Belong to the record-level Step 9 magical/advanced boundary and are not properties of the Step 3 `item` schema. Their source identities and links remain in the Phase 1 inventory until that step; no target or mechanic is fabricated. |
| `magicvariants.json` `requires`, `inherits`, `excludes`, `linkedLootTables` | Entirely Step 9. This source encodes template-like transformations and selection logic; Step 3 neither expands it nor builds an automatic interpreter. |
| `_copy` | Offline source interpretation only. Canonical records preserve distinct source identities and manually interpreted differences; no runtime inheritance or copy engine is introduced. |

## Validation and interpretation rules

- Draft 2020-12 schemas reject unknown fields, raw source entries, invalid source-qualified IDs, out-of-range measures, invalid categories, and incompatible weapon/armor/mount components. Weapon damage is required except for the source-supported Net pattern; armor and shield AC is required.
- Cross-record validation checks uniqueness across the unified Item identities, item/property/mastery links, source-qualified reprints, and item content references. An unresolved/out-of-scope target remains an explicit external disposition until its target step; it is not converted to a guessed ID.
- `value` is represented in copper-piece units, retaining fractional values such as sling-bullet pricing. `weight` is in pounds. Weapon range strings with two values become normal and long range; a one-value range is preserved as the source-supported single-distance case.
- Item prose is manually interpreted. Automation may enumerate, format, and validate records only. Tests must not simulate gameplay; the API exposes declarations rather than executing properties, masteries, item uses, or equipment effects.

### Phase 1 weapon and armor source audit

The complete source arrays were checked against the structured `type`, `weaponCategory`, `dmg1`, `dmgType`, and `ac` fields. The base-item catalog has 102 weapon candidates: 101 provide damage fields and the PHB Net has no damage field, with its exceptional procedure represented by the `Special` entry. It has 27 armor/shield records, all with source AC. The full item catalog has 236 weapon candidates, all with damage fields, and 78 armor/shield candidates; the only missing AC is Powered Armor (QftIS), an advanced magical record assigned to Step 9 by the approved boundary. Thus every in-scope weapon has damage except the documented Net exception, and every in-scope armor/shield has AC. Counts include source candidates beyond Step 3 where noted; the record-level boundary remains semantic and is tracked during later accounting.

Ajv Draft 2020-12 strict compilation passed for the shared schema and all three revised equipment schemas. Validation accepted ordinary weapon, item-to-item base reference, shield AC, the Net exception, all four `Special` patterns, property metadata/reprint link, and Topple's DC/reference example. It rejected weapon/armor component conflicts, an unexcepted weapon without damage, and armor without AC. The Phase 1 source-array check found no weapon/armor overlap. These were schema validation examples and source inventory checks, not gameplay tests; no project tests or canonical records were added.

## Limitations and Phase 2 review points

- The exact Step 3 `items.json` membership is semantic and will be recorded per identity during later conversion. The source catalog has ambiguous rarity/absent-type cases; no automatic rarity-based classification is permitted. Advanced magic, magic variants, and magical item-specific effects are explicitly Step 9 work.
- `itemGroup` member strings include generated/unqualified forms; a group is not made canonical solely to preserve source navigation. Any source-supported bundle or choice meaning must be attached to a canonical owning item with precise accounting.
- Poison effects, consumable use procedures, exact source distinctions in descriptive type entries, and external class/spell/vehicle/loot references need representative Phase 3 source review. The four ordinary weapon `Special` patterns are now represented, but their canonical interpretations still require that source review. They are not silently omitted. A schema defect found there requires review and approval before altering an approved contract. Existing Step 2 Rule/Action/Skill contracts are reusable only when meanings match.
- The eight mastery definitions remain separate because their source effects differ. Their source metadata, Condition references, and damage values reuse common contracts where compatible; Topple keeps a small local dynamic DC shape because the existing common DC vocabulary cannot express the attack ability modifier used in its formula. No generic weapon-mechanics framework is proposed.
- No item data examples have been authored as canonical records; the examples above illustrate schema shape only. Full semantic and source-accounting verification belongs to later authorized conversion phases.

## Phase 2 gate

The proposed schemas, Step 3/Step 9 boundary, coverage dispositions, and limitations were submitted at the explicit Phase 2 approval gate. No canonical datasets, normalized data, production code, or tests were changed for this phase.

## Approval record — 2026-10-29

The user explicitly approved the revised Phase 2 schemas and authorized Step 3 Phase 3. This approval establishes the contracts used by the Phase 3 representative fixtures; any further schema revision remains subject to review and approval.
