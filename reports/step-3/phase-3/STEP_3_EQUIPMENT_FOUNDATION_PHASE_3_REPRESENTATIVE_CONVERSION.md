# Step 3 â€” Equipment Foundation: Phase 3 Representative Conversion

**Status:** Phase 3 representative conversion was explicitly approved and closed by the user on 2026-10-30. The approval accepts the representative conversion and its documented limitations as the Phase 4 baseline.

## Authorization and scope

The user approved the revised Phase 2 contracts and authorized Phase 3 on 2026-10-29. These fixtures use the approved single Item schema and the approved Step 3/Step 9 boundary. They cover the complete base-item identities only by representative examples; the selected `items.json` records are ordinary equipment whose operation does not depend on advanced magic or generic magic variants. No Step 9 item or `magicvariant` was converted.

## Initial representative artifacts before authorized revision (historical)

Files are under [`datasets/step-3/equipment/v1/`](../../../datasets/step-3/equipment/v1/):

| Model | Records | Coverage |
|---|---:|---|
| Item | 30 | Melee and ranged weapons, modern firearm, ammunition, armor, shield, adventuring gear, food, tool, spellcasting focus, mount, container capacity, ordinary pack contents, atomic packaged contents, poison identity, and the Net's no-damage exception. |
| Item Property | 13 | PHB/XPHB property differences, Ammunition and firearm ammunition, Burst Fire, Reload, Finesse, Light, Reach, Special, Two-Handed, and Thrown. |
| Item Mastery | 8 | Every XPHB weapon mastery definition. |

The item records include PHB/XPHB Longswords, an XPHB Dagger and Spear, PHB Net, Double-Bladed Scimitar (ERLW), Hoopak (DSotDQ), Lance, Chain Mail, Leather Armor, Shield, arrows and firearm ammunition, an XDMG Automatic Rifle, Amulet, Thieves' Tools, Riding Horse, Backpack, Torch, Basic Poison, Ball Bearings, Waterskin, and the Explorer's Pack with its eight source-listed contents.

## Initial interpretation and source accounting (historical)

- Source identities use `item:<source>:<slug>` for both base items and catalog items. Item-to-item links use `targetType: item`. The PHB Longsword's reprint link resolves to the included XPHB Longsword.
- Source prices are recorded in copper pieces without rounding; source weights are in pounds. PHB Waterskin's `(full)` qualifier is retained. The Backpack maps its structured 30-pound limit and source-stated one-cubic-foot limit. The Riding Horse maps its 60-foot speed and 480-pound carrying capacity.
- Weapon damage, versatile damage on the items, normal/long ranges, armor class, minimum Strength, Stealth disadvantage, light radii/duration, weapon era, shots before reload, and the sourced XDMG market-value rarity are represented as typed values.
- The four ordinary `Special` cases are individually interpreted on their owning weapons: Double-Bladed Scimitar's bonus-action follow-up damage; Hoopak's alternate ranged damage and melee ammunition exception; Lance's close-target disadvantage and unmounted hand requirement; and Net restraint, escape, net durability, target exceptions, and attack limit. The Net's missing base damage is permitted only with its `NET_RESTRAINT` component.
- All eight Mastery records were checked against their XPHB source definitions. Dagger/Longsword/Spear/Automatic Rifle examples reference Nick, Sap, and Slow. Topple links to `condition:phb:prone`; the PHB Light property links to the existing `action:phb:two-weapon-fighting` record.
- The Explorer's Pack has eight typed contents with quantities. Ball Bearings is represented as 1,000 atomic packaged pieces. References to the included item contents and firearm/sling ammunition resolve within this fixture.
- Source locations for the main exceptional examples are `items-base.json $.baseitem[48]` (PHB Dagger), `[52]` (Double-Bladed Scimitar), `[90]` (Hoopak), `[99]` (Lance), `[117â€“118]` (Longswords), `[137]` (Net), and `[194]` (XPHB Spear); and `items.json $.item[135]` (Backpack), `[156]` (Ball Bearings bag), `[169]` (Basic Poison), `[730]` (Explorer's Pack), `[1748]` (Riding Horse), `[2299]` (Thieves' Tools), `[2330]` (Torch), and `[2440]` (Waterskin). The Phase 1 inventory retains full file paths, hashes, and field evidence.

## Initial schema gaps before authorized revision (historical)

The following source portions remain visible and are not reinterpreted as absent rules. They are not added to the canonical records because the approved Phase 2 schemas cannot faithfully represent them. A schema change requires explicit review and approval; none was made in this phase.

| Source locator | Finding and current disposition |
|---|---|
| `items-base.json $.itemProperty[24â€“25]`; `$.baseitem[117â€“118].property` and `$.baseitem[194].property` | Versatile damage is item-specific, and the Item schema correctly stores it on each weapon as `versatileDamage`. The Item Property schema incorrectly requires `twoHandedDamage` on the shared property entity. No Versatile property records were authored; the three item property references remain unresolved in this fixture pending a contract correction. |
| `items-base.json $.itemProperty[22]`; `$.baseitem[137].property[1]` | PHB Thrown does not specify that drawing the weapon is part of the attack. The Item Property schema requires `drawAsPartOfAttack`, so this fact cannot be represented without inventing a value. The Net's Thrown property link is omitted from the fixture and recorded as `SCHEMA_UNSUPPORTED`. The XPHB Thrown definition is representable and is included. |
| `items.json $.item[169].entries[0]` | Basic Poison's bonus-action application, eligible damage types, extra poison damage, and one-minute/first-hit duration have no typed item-use/effect contract. The Item retains its identity, price, and `poison` marker; the use and effect are `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[2330].entries[0]` | Torch light is represented, but its one-point fire damage on a hit with a burning torch has no supported item-attack component. That effect is `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[156].entries[0]` | The Ball Bearings bag's action, area, Dexterity save, Prone result, and half-speed exception are not represented. Package contents and atomicity are retained; the use procedure is `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[2299].entries[0]` and `$.item[2299].additionalEntries` | Thieves' Tools' check proficiency and additional trap-related uses lack a supported tool-use/check component. The Item identity and equipment facts are retained; unsupported portions are `SCHEMA_UNSUPPORTED`. |
| `items.json $.item[2440].entries[0]` | Waterskin capacity is given as four pints, while Item capacity accepts pounds or cubic feet only. Its identity, full-weight qualifier, price, and weight are retained; the capacity is `SCHEMA_UNSUPPORTED` rather than converted to a different unit. |
| `items.json $.item[2312].entries[0]`; `$.item[990].entries[0]` | Tinderbox ignition timing and hempen rope hit points/burst check are not represented by the approved Item components. These portions are `SCHEMA_UNSUPPORTED`. |

Seventeen retained `sourceMetadata.reprintedAs` links point to counterpart Items outside this small fixture. Their target IDs are preserved from source reprint evidence; the counterpart records are not duplicated merely to close the example batch. Three `weapon.properties` links to Versatile are the only unresolved in-fixture property references. Other property, mastery, ammunition, content, and included Step 2 references validate or resolve within the available approved data.

## Initial validation before authorized revision (historical)

- Ajv Draft 2020-12 strict mode compiled the shared, Item, Item Property, and Item Mastery schemas and validated all 51 fixture records: 30 Items, 13 properties, and 8 masteries.
- Representative positive and negative schema cases passed: ordinary weapon, armor/shield subtype matching, Net's no-damage exception, and rejection of incompatible weapon/armor components, missing non-exempt weapon damage, missing armor AC, and mismatched armor/shield subtype.
- All four `Special` patterns and all eight mastery records were validated against their schemas. Item contents and ammunition links resolve within the fixture. Cross-reference checking found only the three disclosed Versatile property links unresolved in the fixture.
- Source review confirmed 102 base-item weapon candidates (the PHB Net is the sole source candidate without damage) and 27 base-item armor/shield records with AC. Among 236 item-catalog weapon candidates all have damage. Of 78 armor/shield candidates, only Powered Armor (QftIS) lacks AC; it is advanced equipment within the approved Step 9 boundary, not this Step 3 batch.
- No production code, approved schema, normalized dataset, or test file was changed. The source datasets remain untouched.

## Initial Phase 3 gate submission (historical; revised below)

The representative conversion, source interpretations, unresolved fixture references, and schema gaps are submitted for explicit Phase 3 review. Phase 4 conversion conventions and full-catalog work have not started.

## Authorized Phase 3 revision — 2026-10-30

The user authorized source investigation, necessary schema revisions, fixture corrections, and validation while Phase 3 remains open. This revision supersedes the earlier “schema gaps and unrepresented source portions” statements where noted below; it does not approve Phase 3 or start Phase 4.

### Source evidence reviewed

- The full base-item inventory contains 102 weapon candidates: 101 records have structured damage and the PHB Net is the no-damage exception. Seventeen records have a second structured damage value (`items-base.json $.baseitem[*].dmg2`), used by Versatile weapons. PHB and XPHB Longswords are `$.baseitem[117]` and `[118]`; XPHB Spear is `[194]`. The PHB/XPHB property definitions are `$.itemProperty[22–25]`.
- Source damage also has distinct modes: the thrown property (`itemProperty[22–23]`) changes an eligible melee weapon to a ranged attack; the Hoopak (`baseitem[90]`) has melee and alternate ranged damage; the Double-Bladed Scimitar (`baseitem[52]`) has a bonus-action follow-up damage value; and the Longsword/Spear versatile values are two-handed melee damage. Damage usage is now recorded on each damage entry. No item-level `versatileDamage` remains; the Versatile property states that its two-handed profile applies to a melee attack.
- The PHB Thrown definition (`itemProperty[22]`) specifies the same ability modifier for a melee weapon’s ranged attack and damage roll, but says nothing about drawing timing. XPHB (`itemProperty[23]`) explicitly adds drawing the weapon as part of the attack. The schema now requires the PHB form to omit drawing timing and the XPHB form to include it. The PHB Net (`baseitem[137]`) again links to the PHB Thrown property.
- A focused catalog survey covered the PHB/XPHB ordinary gear and tool records, including repeated action/check/save/damage, durability, ignition, light, capacity, and liquid-container descriptions. Evidence includes PHB/XPHB Basic Poison (`items.json $.item[170]`/`[169]`), Ball Bearings (`[156]`)/XPHB Ball Bearings (`[155]`), Thieves’ Tools (`[2299]`/`[2300]`), Hempen Rope (`[990]`)/Rope (`[1868]`), Tinderbox (`[2312]`/`[2313]`), Torch (`[2330]`/`[2331]`), and Waterskin (`[2440]`/`[2441]`). Caltrops, Acid, Alchemist’s Fire, Holy Water, Oil, and other tools confirm that item-use procedures recur beyond the selected fixtures.
- Liquid-container evidence includes pints (Waterskins, Flask, Oil), gallons (Jug, Barrel, Iron Pot), and ounces (Vial). Source cubic-foot capacity remains separate from liquid volume; source pounds remain item weight and are not repurposed as capacity.

### Schema and fixture changes

- `weapon.damageEntries` is a list of `{usage, damage}` values using the source-supported `MELEE`, `RANGED`, `ONE_HANDED`, `TWO_HANDED`, `THROWN`, and `BONUS_ACTION_FOLLOW_UP` usage terms. The Longsword and Spear have separate one-/two-handed entries; thrown damage applicability is explicit; Hoopak ranged damage and Double-Bladed Scimitar follow-up damage are represented only in this list. Their special-mechanic records retain their non-damage facts.
- Item Property now contains PHB Thrown, PHB/XPHB Versatile, and the repaired source-specific Thrown constraints. Versatile property records no longer duplicate weapon damage.
- Item use components cover poison application, area hazards, tool checks, object durability, ignition, item attack damage, and a rope knot ability-check use. These use Step 2’s shared `DamageRoll`, `SavingThrow`, and condition reference contracts where their meanings match. The Common schema has no general ability-check contract, so the tool/rope check shapes remain small equipment-owned typed structures.
- Capacity now has `liquidVolume {amount, unit}` with ounce, pint, and gallon units, independent of `weightPounds` and `volumeCubicFeet`. PHB and XPHB Waterskins retain four pints; XPHB Waterskin also retains its source cubic-foot capacity. PHB/XPHB Tinderboxes retain their distinct action costs and listed ignition targets.
- The representative set now has 35 Items, 16 Item Properties, and 8 Item Masteries (59 unique records). The added and revised examples include PHB/XPHB poison, tools, rope, Tinderbox, and Waterskin. The existing 8 Mastery definitions remain unchanged.

### Remaining limitations and boundaries

This is still a representative batch, not full equipment conversion. Acid, Alchemist’s Fire, Holy Water, Oil, Caltrops, XPHB Ball Bearings, lantern fuel/hood operation, other tool-specific checks and procedures, rope binding/escape, and the extended PHB Thieves’ Tools/XGE trap-creation and trap-knowledge text are not fully represented. These examples need further Phase 3/4 review against the complete catalog; they are not declared semantically unsupported or omitted from future conversion. The current reusable components do not act as a universal effect engine. Dehydration prose and other effects depending on creatures, objects, or encounter state remain outside these fixtures. No Step 9 advanced magical item or variant content is added.

### Revised validation results

- Ajv 8.17.1 Draft 2020-12 strict mode compiled the shared Common, Item, Item Property, and Item Mastery schemas and accepted all 59 fixtures: 35 Items, 16 Properties, and 8 Masteries. All IDs are unique. Item/property/mastery/content/ammunition references resolve within the representative batch; referenced Step 2 Prone and Sleight of Hand records exist in the approved Step 2 dataset.
- Positive cases include one-/two-handed weapon damage, a Thrown profile, the PHB Net exception plus its restored Thrown property, both poison variants, capacity with pints, and tool/rope/tinderbox mechanics. Negative checks rejected missing damage usage, legacy `versatileDamage`, PHB Thrown with invented draw timing, XPHB Thrown without its source-required draw timing, and unsupported liquid units. A separate cross-reference check rejects removal of the Net’s required source-supported PHB Thrown reference.
- `items-base.json` and `items.json` remain unchanged. No production implementation, normalized dataset, or Phase 4 work was performed. `git diff --check` reported no whitespace errors (only line-ending conversion notices for JSON fixtures).

### Revised Phase 3 gate

The revised representative conversion, evidence, schema changes, validation outcomes, and remaining coverage gaps are submitted for explicit Phase 3 review. Phase 3 remains open pending approval; Phase 4 has not started.


## Authorized mechanic-area implementation - 2026-10-30

The user approved implementation of six investigated equipment mechanic areas while Phase 3 remains open. This section supersedes the preceding revision's stated gaps for the examples covered here. It does not approve Phase 3, authorize the per-record full-conversion manifest to be skipped, or start Phase 4.

### Schema and representative fixture coverage

The Item contract now keeps use activation, attack/save resolution, delivery, and outcomes distinct. It adds reusable, bounded structures for area hazards and trap procedures; light emission shape/mode, fuel and hood controls; equipment activities and typed checks; object durability and binding/escape; scoped d20 modifiers; limited uses; and audible signals. Existing Step 2 DamageRoll, SavingThrow, D20RollModifier, condition references, action references, and Rule AbilityCheck structures are reused where their semantics match. The Item domain owns these extensions; cross-domain mechanic consolidation remains deferred to Step Z.

PHB/XPHB representative records now include:

- Consumables: Basic Poison, Acid, Alchemist's Fire, Holy Water, and Oil. PHB attack-roll and XPHB saving-throw forms remain distinct. Outcomes cover direct damage, ongoing fire damage with an action/check to end it, poison coatings, and Oil's extra fire damage.
- Hazards and traps: Ball Bearings, Caltrops, Oil surface hazards, and Hunting Traps. Save outcomes, Prone, stop movement, speed changes, once-per-turn damage, recovery, ignition requirements, tether length, escape checks, and damage on a failed escape are typed. The PHB/XPHB Caltrops, Ball Bearings, and Hunting Trap differences are retained.
- Light and fuel: PHB/XPHB Torches, Hooded Lanterns, and Bullseye Lanterns cover radius and cone emissions, open/lowered modes, durations, oil references, pints where explicitly stated, cumulative fuel use, and source-stated hood timing.
- Equipment activities and checks: PHB/XPHB Thieves' Tools, Rope knot tying, Magnifying Glass ignition, and Manacles lock picking use shared ability-check structures where specified; PHB tool descriptions that leave ability choice unstated do not receive an invented ability.
- Binding and durability: PHB/XPHB Manacles, PHB and XPHB Nets, and XPHB Rope represent target prerequisites, binding outcomes, escape or break checks, assistance, restraint conditions, target-size exclusions, net AC/HP/immunities, and PHB Rope hit points. Rope burst checks retain their edition-specific ability/skill information.
- Scoped modifiers, uses, and signals: PHB/XPHB Crowbars, PHB/XPHB Antitoxin, XPHB Costume, PHB/XPHB Magnifying Glass, XPHB Healer's Kit, XPHB Bell, and XPHB Signal Whistle represent scoped advantage, exclusions/condition and duration, activation, ten kit uses, and audible ranges.

### Source evidence

Exact locators in immutable src/main/resources/data/items.json include Acid $.item[46] (PHB) and [45] (XPHB); Alchemist's Fire [59] (PHB) and [58] (XPHB); Basic Poison [170] and [169]; Holy Water [1008] and [1007]; Oil [1425] and [1424]; Ball Bearings [156] and [155]; Caltrops [346] and [345]; Hunting Trap [1039] and [1040]; Manacles [1271] and [1272]; Hempen Rope [990] and Rope [1868]; Thieves' Tools [2299] and [2300]; Tinderbox [2312] and [2313]; Torch [2330] and [2331]; Hooded Lantern [1010] and [1011]; Bullseye Lantern [335] and [336]; Antitoxin [96] and [95]; Costume [501]; Magnifying Glass [1265] and [1266]; Healer's Kit [967] and [968]; Crowbar [514] and [515]; XPHB Bell [190]; and XPHB Signal Whistle [2016]. PHB Net is items-base.json $.baseitem[137]; XPHB Net is items.json $.item[1401]. The source inventory retains hashes and the full inspected record evidence.

### Remaining unsupported interpretations and limitations

- XPHB Alchemist's Fire states that a failed save starts the burning hazard. The fixture records the initial 1d4 fire damage but not the follow-up hazard because Step 2 has no compatible canonical burning condition/procedure. No general status or effects engine was introduced.
- PHB Holy Water's creation ritual (class, spell slot, one hour, and powdered silver cost) is not an ordinary item-use mechanic and remains unrepresented in this Item fixture.
- The PHB Thieves' Tools additional trap knowledge/creation prose and other source text outside the selected ordinary use/check patterns remain for later source-accounted conversion. This batch does not claim full catalog coverage.
- Thirteen pre-existing reprint links remain outside this representative fixture set: 11 sourceMetadata.reprintedAs Item counterparts and 2 Item Property reprintedAsProperties. All newly added mechanical references resolve against the fixture batch or approved Step 2 normalized records.
- The per-record Step 3/Step 9 scope manifest remains required before full conversion. This representative schema revision did not add Step 9 items, magic variants, or supernatural effects.

### Validation results

- Ajv Draft 2020-12 compiled Common, Action, Rule, Item Property, Item Mastery, and Item schemas with strict validation enabled and the existing Rule schema's strictRequired compatibility warning disabled. All 90 representative records validated: 66 Items, 16 Item Properties, and 8 Item Masteries. All 90 IDs are unique.
- Five negative Item cases were rejected: missing consumable activation, unsupported light shape, missing trap escape check, zero limited uses, and an untyped damage outcome.
- The source-reference scan examined 135 typed references. Every newly added mechanics/reference target resolved; 13 PHB reprint links remain intentionally external to this representative batch.
- Original source data and normalized data were not modified. No production code or test files were changed. Phase 4 has not started.

### Phase 3 approval gate

The six-area Item schema revision, representative fixtures, source evidence, validation outcomes, and remaining limitations are submitted for explicit Phase 3 review. Phase 3 remains open. Stop here pending approval.


## Authorized ongoing-damage revision and item-creation investigation - 2026-10-30

The user authorized implementation of a reusable ongoing-damage structure and representative fixtures, while authorizing investigation only for creation/crafting. This section supersedes the earlier statement that XPHB Alchemist's Fire's follow-up Burning damage was unrepresented. The Burning hazard's full general rule is still outside the normalized Step 2 contracts; the Item record now carries the source-supported damage procedure and ending conditions it needs.

### Ongoing damage: source patterns and Item contract

A catalog review found these recurring Step 3 equipment patterns in immutable src/main/resources/data/items.json:

- PHB Alchemist's Fire, $.item[59]: on a hit, 1d4 fire damage at the start of each target turn; the target can use an action for a DC 10 Dexterity check to extinguish it.
- SCC Alchemist's Doom, $.item[57]: the same trigger, cadence, and ending check, with 2d6 fire damage.
- XPHB Alchemist's Fire, $.item[58]: a failed Dexterity save causes immediate 1d4 fire damage and starts the Burning hazard. The general XPHB Burning source is trapshazards.json $.hazard[7]: 1d4 Fire damage at the start of each turn, ended by taking an action to become Prone and roll, or by being doused, submerged, or suffocated.
- Keg of Alchemist's Fire, $.item[1172]: 6d6 fire damage on a hit and each following round; a creature can use an action with a blanket or carpet to reduce each tick by 2d6, with three such actions extinguishing the fire.
- Burnt Othur Fumes, $.item[341] (XDMG): an initial failed DC 13 Constitution save causes 3d6 poison damage; the target repeats the save at the start of each turn and takes 1d6 on each failure until three successful saves.
- Pale Tincture, $.item[1480] (XDMG): initial 1d6 poison damage and repeat DC 16 Constitution saves every 24 hours, with 1d6 on failure until seven successes. The poison also prevents the damage from being healed while the Poisoned condition persists.
- Prespra, $.item[1670]: initial 1d6 poison damage and repeat saves/damage at the start of each turn until a success; it also causes Poisoned and Blinded. Biza's Breath $.item[209] and Carrion Crawler Mucus $.item[380] repeat saves to end conditions without recurring damage, so they are adjacent item-effect patterns rather than ongoing-damage examples.

The reviewed matches distinguish ordinary gear from out-of-boundary analogues. Arcane Cannon's Acid Jet, items.json $.item[103], has recurring acid damage but is a very rare magical item and remains Step 9. Other magical rods, weapons, and hazards found in the catalog remain outside the Step 3/Step 9 boundary.

The Item schema now defines a narrow ONGOING_DAMAGE structure with:

- a trigger and optional application save/initial damage;
- the repeated DamageRoll, first-tick timing, cadence, and automatic or saving-throw resolution;
- typed endings for an ability check, an action with a condition, environmental termination, a successful-save count, or a staged suppression action.

It reuses Step 2 DamageRoll, SavingThrow, Rule AbilityCheck, condition references, and turn timing. The staged suppression amount is a small dice value, not an effect evaluator. It supports recurring damage without introducing a general effect engine.

Representative fixtures now cover PHB/XPHB Alchemist's Fire, SCC Alchemist's Doom, XDMG Burnt Othur Fumes, LoX Keg of Alchemist's Fire, and XDMG Pale Tincture. The XPHB Alchemist's Fire fixture carries its immediate failed-save damage and the subsequent Burning damage/termination separately. The two PHB/SCC flask items share the same ongoing procedure with different damage. Burnt Othur and Pale Tincture exercise repeating saves and success-count endings; the Keg exercises round cadence and multi-action suppression.

Remaining ongoing-effect limitations: Pale Tincture's Poisoned condition linkage and no-healing clause, and Prespra's Poisoned/Blinded co-effects, are not represented by ONGOING_DAMAGE. The Keg fixture represents its initial hit and recurring/suppression damage but has no inferred attack timing or range. Status-only repeated-save patterns such as Biza's Breath and Carrion Crawler Mucus remain outside this damage component. No additional condition/status framework or runtime mechanics was added.

### Item creation and crafting: investigation only

Creation is recurring across the relevant foundational equipment sources, not limited to Holy Water:

- PHB Holy Water has one explicit ritual at items.json $.item[1008].entries[1]: a Cleric or Paladin spends one hour, 25 gp of powdered silver, and a 1st-level spell slot. The XPHB Holy Water record $.item[1007] has no creation procedure.
- PHB Alchemist's Supplies has an Alchemical Crafting procedure at items-base.json $.baseitem[0].additionalEntries[4]. It produces one dose of any of six listed items during a long rest, with raw-material mass/cost and a value-based deduction. XPHB Alchemist's Supplies has a Craft product list at $.baseitem[1].additionalEntries[0].items[2], but does not state time, materials, or cost there.
- Ten PHB artisan-tool records have explicit create/repair/production procedures: Carpenter's Tools $.baseitem[29].additionalEntries[6-7], Cartographer's Tools [31].additionalEntries[5], Cobbler's Tools [40].additionalEntries[4-5], Cook's Utensils [42].additionalEntries[5], Potter's Tools [152].additionalEntries[5], Smith's Tools [190].additionalEntries[4], Tinker's Tools [204].additionalEntries[4], Weaver's Tools [218].additionalEntries[4-5], and Woodcarver's Tools [222].additionalEntries[4-5]. Alchemist's Supplies is the tenth record above. These sources cover materials, tool proficiency, rests/downtime, quantities, repairs, durations, and item-value calculations at differing levels of detail.
- Fourteen XPHB tool records have explicit Craft product lists, generally without recipe time, cost, or material details: items-base.json $.baseitem[1].additionalEntries[0].items[2], $.baseitem[26].additionalEntries[0].items[2], $.baseitem[30].additionalEntries[0].items[2], $.baseitem[32].additionalEntries[0].items[2], $.baseitem[41].additionalEntries[0].items[2], $.baseitem[68].additionalEntries[0].items[2], $.baseitem[98].additionalEntries[0].items[2], $.baseitem[108].additionalEntries[0].items[2], $.baseitem[126].additionalEntries[0].items[2], $.baseitem[153].additionalEntries[0].items[2], $.baseitem[191].additionalEntries[0].items[2], $.baseitem[205].additionalEntries[0].items[2], $.baseitem[219].additionalEntries[0].items[2], and $.baseitem[223].additionalEntries[0].items[2]. The lists name many equipment products, so this is a broad tool-to-item relationship rather than one exceptional ritual.
- The broader dependencies are in variantrules.json $.variantrule[61] (PHB Downtime Activity: Crafting, tool proficiency/facility/raw material and 5 gp/day progress) and [63] (XGE crafting nonmagical and magic items, tool/item dependencies, material cost, time, collaborators, and separate magic-item rules). Those rules are broader than the Step 3 Item catalog.

Recommendation: keep the Item identities and ordinary use mechanics in Step 3, but defer creation/crafting procedure modeling to Step 9 World Building review. The frequency and dependencies span tools, outputs, proficiency, time, money, raw materials, facilities, multiple crafters, and downtime rules; implementing Holy Water alone in Step 3 would create an isolated exception while leaving a much larger catalog pattern out. ROADMAP.md currently describes Step 9 as world-facing content and advanced equipment/magical variants, not general mundane production, so Step 9 Phase 1 must explicitly confirm this scope before implementation. If it does not fit that inventory, propose a separate Step Z scope decision. No creation mechanics or creation schema were implemented in this revision.

### Revised validation and Phase 3 gate

- Ajv Draft 2020-12 compiled Common, Action, Rule, Item Property, Item Mastery, and Item schemas with strict validation enabled and the existing Rule schema's strictRequired compatibility warning disabled. All 94 representative records validated: 70 Items, 16 Item Properties, and 8 Item Masteries.
- Ongoing-damage positive fixtures cover immediate and delayed application, automatic and save-gated repeats, turn/round/day cadence, and action/check, condition, environment, success-count, and staged-suppression endings. Negative ongoing-damage cases reject a missing end condition, unsupported cadence, a non-positive success threshold, and untyped repeated damage.
- The creation investigation caused no schema or fixture changes. The typed-reference scan checked 136 references; 13 pre-existing reprint links to counterpart records outside this representative batch remain unresolved. All newly added Step 2/Item references resolve. Source and normalized datasets remain unchanged; no production code or test files were changed. Step 3/Step 9 assignments remain unchanged.
- Phase 3 approval record: on 2026-10-30, the user explicitly approved and closed Phase 3 and authorized Phase 4. The approval accepts these representative fixtures, validation results, and documented limitations as the baseline. It does not waive the per-record Step 3/Step 9 scope manifest required before broader conversion.


## Explicit Phase 3 approval — 2026-10-30

The user explicitly approved and closed Step 3 Phase 3 and authorized Phase 4. The approval accepts the representative conversion, validation evidence, and documented limitations as the Phase 4 baseline. It does not waive the per-record Step 3/Step 9 scope manifest required before broader conversion. The approval supersedes the historical open-gate statements above.
