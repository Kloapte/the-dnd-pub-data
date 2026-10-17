# Cross-catalog mechanics matrix

**All 36 domains and 31 concepts are considered: 1,116 intersections.** Every intersection has structured-field owner/occurrence counts, syntax owner/occurrence counts, reference leads, lexical string counts and exact example locators in `entry-evidence.json:catalogs.<domain>.concepts.<id>`.

Counts exhaustively measure the declared scanner predicates, not complete game effects. Direct selectors inspect authored root fields (or Magic Variant inherits fields), Class saving-throw/Monster action roles, reviewed typed entry nodes and constrained nested choice/spell-usage fields. They do not count all nested mechanics in every schema. The per-catalog model review below supplements these lower-bound direct counts.

`S` STRUCTURED: selected explicit role survives a normalized typed structure, without confirmed additional entry semantics. `P` PARTIAL: structured evidence exists plus unresolved descriptive/role coverage. `E` ENTRY_ONLY: explicit formula/label syntax or a manually reviewed effect occurs in EntryDte or related textual content without a normalized semantic effect model. `M` MIXED: structured role evidence and such content evidence coexist. `?` UNCERTAIN: only lexical/reference leads exist. `A` ABSENT: no evidence under the declared probes; not proof that arbitrary prose contains no such mechanic.

SYNTAX counts include related fields such as Monster action names and Encounter results, not just EntryDte leaves. `syntaxInsideBoundContent` records the narrower typed-content count separately. A dedicated textual table result is not relabeled as a shared EntryDte node.

A source field whose value is prose, such as Trap.trigger, is not counted as a structured trigger event. ClassAbilityEntryDte remains structured despite being nested inside entries. Conditions/spell references alone never confirm condition application or spell granting. Non-gameplay indexes/Recipes do not become effect catalogs due to tag/keyword hits.

## Concepts C01–C06

C01 Damage expressions and types; C02 Healing; C03 Temporary hit points; C04 Attack labels and modifiers; C05 Saving throws and DCs; C06 Ability and skill checks

| Catalog | C01 | C02 | C03 | C04 | C05 | C06 |
|---|---|---|---|---|---|---|
| Monsters | M | ? | ? | E | M | M |
| Spells | M | E | E | M | M | P |
| Actions | ? | ? | A | ? | E | ? |
| Feats | E | ? | ? | ? | E | ? |
| Optional Features | E | ? | ? | ? | ? | ? |
| Classes | A | A | A | A | S | A |
| Subclasses | A | A | A | A | A | A |
| Class Features | E | ? | ? | P | M | ? |
| Subclass Features | E | ? | ? | P | M | ? |
| Backgrounds | A | ? | A | ? | A | A |
| Races and Subraces | E | ? | ? | ? | E | ? |
| Items and Base Items | M | ? | ? | M | M | P |
| Magic Variants | E | ? | ? | M | M | ? |
| Conditions | ? | A | A | ? | ? | ? |
| Diseases | E | ? | A | ? | E | A |
| Languages | A | A | A | A | ? | ? |
| Skills | A | A | A | A | A | A |
| Senses | A | A | A | A | ? | A |
| Objects | E | A | ? | M | E | A |
| Bastion Facilities | E | ? | ? | A | E | A |
| Rewards | E | ? | ? | ? | E | ? |
| Traps | E | A | A | E | E | ? |
| Hazards | E | ? | ? | ? | E | A |
| Vehicles | E | A | A | E | E | ? |
| Vehicle Upgrades | E | A | A | E | E | A |
| Variant Rules | E | ? | ? | E | E | ? |
| Treasure Tables | A | A | A | A | A | A |
| Encounters | ? | ? | A | A | E | A |
| Character Creation Options | E | A | A | ? | E | ? |
| Cults | E | ? | ? | E | E | A |
| Boons | E | A | ? | A | E | A |
| Deities | ? | ? | A | A | A | A |
| Name Tables | A | A | A | A | A | A |
| Adventure Index | A | A | A | A | A | A |
| Books | A | A | A | A | A | A |
| Recipes | ? | A | A | A | A | A |

## Concepts C07–C12

C07 Advantage and disadvantage; C08 Condition classification/application; C09 Resistance, vulnerability and immunities; C10 Armor Class and modifiers; C11 Hit points, hit dice and thresholds; C12 Movement and speeds

| Catalog | C07 | C08 | C09 | C10 | C11 | C12 |
|---|---|---|---|---|---|---|
| Monsters | ? | P | P | P | P | P |
| Spells | ? | P | P | ? | ? | ? |
| Actions | ? | ? | A | A | ? | ? |
| Feats | ? | ? | P | ? | ? | ? |
| Optional Features | ? | ? | ? | ? | ? | ? |
| Classes | A | A | A | A | S | A |
| Subclasses | A | A | A | A | A | A |
| Class Features | ? | ? | ? | ? | ? | ? |
| Subclass Features | ? | ? | ? | ? | ? | ? |
| Backgrounds | ? | ? | ? | A | ? | ? |
| Races and Subraces | ? | ? | P | ? | ? | P |
| Items and Base Items | ? | ? | P | P | P | P |
| Magic Variants | ? | ? | P | P | ? | P |
| Conditions | E | ? | ? | A | A | E |
| Diseases | ? | ? | ? | A | ? | ? |
| Languages | A | A | A | A | A | A |
| Skills | A | A | A | A | A | A |
| Senses | A | ? | A | A | A | A |
| Objects | A | ? | P | P | P | P |
| Bastion Facilities | ? | ? | ? | A | ? | ? |
| Rewards | ? | ? | ? | ? | ? | ? |
| Traps | ? | ? | ? | ? | ? | ? |
| Hazards | ? | ? | ? | ? | ? | ? |
| Vehicles | ? | ? | S | P | P | P |
| Vehicle Upgrades | ? | ? | ? | ? | ? | ? |
| Variant Rules | ? | ? | ? | ? | ? | ? |
| Treasure Tables | A | A | A | A | A | A |
| Encounters | ? | ? | ? | A | ? | ? |
| Character Creation Options | ? | ? | ? | ? | ? | ? |
| Cults | ? | ? | ? | A | ? | ? |
| Boons | ? | ? | ? | A | ? | ? |
| Deities | ? | ? | ? | A | A | ? |
| Name Tables | A | A | A | A | A | A |
| Adventure Index | A | A | A | A | A | A |
| Books | A | A | A | A | A | A |
| Recipes | A | A | ? | A | A | ? |

## Concepts C13–C18

C13 Range, reach and area; C14 Duration and concentration; C15 Targets and restrictions; C16 Costs, charges, uses and recharge; C17 Action economy and initiative; C18 Spellcasting and spell availability

| Catalog | C13 | C14 | C15 | C16 | C17 | C18 |
|---|---|---|---|---|---|---|
| Monsters | ? | ? | ? | M | P | P |
| Spells | M | P | M | P | P | ? |
| Actions | ? | ? | ? | ? | P | ? |
| Feats | ? | ? | ? | P | ? | P |
| Optional Features | ? | ? | ? | M | ? | P |
| Classes | A | A | A | A | A | S |
| Subclasses | A | A | A | S | A | S |
| Class Features | ? | ? | ? | P | ? | ? |
| Subclass Features | ? | ? | ? | P | ? | ? |
| Backgrounds | ? | ? | ? | ? | A | P |
| Races and Subraces | ? | ? | ? | P | ? | P |
| Items and Base Items | P | ? | ? | P | ? | P |
| Magic Variants | ? | ? | ? | P | ? | P |
| Conditions | ? | ? | ? | A | ? | ? |
| Diseases | ? | ? | ? | ? | ? | ? |
| Languages | ? | A | A | ? | A | ? |
| Skills | A | A | A | ? | A | A |
| Senses | ? | A | A | A | A | A |
| Objects | ? | ? | ? | ? | ? | ? |
| Bastion Facilities | ? | ? | ? | ? | ? | ? |
| Rewards | ? | ? | ? | P | ? | P |
| Traps | ? | P | ? | ? | P | ? |
| Hazards | ? | ? | ? | ? | ? | ? |
| Vehicles | ? | ? | ? | ? | P | A |
| Vehicle Upgrades | ? | ? | ? | E | ? | ? |
| Variant Rules | ? | ? | ? | ? | ? | ? |
| Treasure Tables | A | A | A | A | A | A |
| Encounters | ? | ? | ? | ? | ? | ? |
| Character Creation Options | ? | ? | ? | ? | ? | ? |
| Cults | ? | ? | ? | E | ? | ? |
| Boons | ? | A | ? | ? | ? | ? |
| Deities | ? | ? | ? | ? | ? | ? |
| Name Tables | A | A | A | A | A | A |
| Adventure Index | A | A | A | A | A | A |
| Books | A | A | A | A | A | A |
| Recipes | ? | ? | A | ? | A | A |

## Concepts C19–C24

C19 Level/slot progression; C20 Proficiency and expertise grants; C21 Prerequisites and equipment restrictions; C22 Choices and alternatives; C23 Summoning and transformations; C24 Multiattack and action sequences

| Catalog | C19 | C20 | C21 | C22 | C23 | C24 |
|---|---|---|---|---|---|---|
| Monsters | ? | ? | ? | E | P | E |
| Spells | M | ? | ? | E | ? | ? |
| Actions | ? | ? | A | ? | A | ? |
| Feats | P | P | P | M | ? | ? |
| Optional Features | P | P | P | M | ? | ? |
| Classes | S | S | S | M | A | A |
| Subclasses | S | A | A | M | A | A |
| Class Features | ? | ? | ? | M | ? | ? |
| Subclass Features | ? | ? | A | M | ? | ? |
| Backgrounds | A | P | P | M | ? | A |
| Races and Subraces | ? | P | ? | M | ? | ? |
| Items and Base Items | ? | P | P | M | ? | ? |
| Magic Variants | ? | P | S | E | ? | ? |
| Conditions | A | A | A | A | A | A |
| Diseases | A | ? | A | A | ? | A |
| Languages | A | A | A | ? | A | A |
| Skills | A | A | A | A | A | A |
| Senses | A | A | A | A | A | A |
| Objects | A | ? | A | ? | A | A |
| Bastion Facilities | A | ? | S | E | ? | A |
| Rewards | ? | ? | A | M | ? | ? |
| Traps | ? | A | A | ? | A | ? |
| Hazards | ? | A | A | E | ? | A |
| Vehicles | A | A | A | ? | A | ? |
| Vehicle Upgrades | A | A | A | ? | A | A |
| Variant Rules | ? | ? | ? | E | ? | ? |
| Treasure Tables | A | A | A | A | A | A |
| Encounters | A | A | A | ? | A | A |
| Character Creation Options | ? | ? | P | ? | ? | ? |
| Cults | ? | ? | A | E | A | A |
| Boons | ? | ? | A | A | ? | A |
| Deities | A | ? | A | ? | ? | A |
| Name Tables | A | A | A | A | A | A |
| Adventure Index | A | A | A | A | A | A |
| Books | A | A | A | A | A | A |
| Recipes | A | A | A | ? | A | A |

## Concepts C25–C30

C25 Triggers and reactions; C26 Ongoing and conditional effects; C27 Rest-based recovery; C28 Environmental/exploration context; C29 Vehicle and facility mechanics; C30 Economy, quantities and roll tables

| Catalog | C25 | C26 | C27 | C28 | C29 | C30 |
|---|---|---|---|---|---|---|
| Monsters | E | ? | P | P | ? | E |
| Spells | ? | ? | ? | ? | A | M |
| Actions | ? | ? | ? | ? | A | E |
| Feats | ? | ? | M | ? | ? | E |
| Optional Features | ? | ? | ? | ? | ? | E |
| Classes | A | A | A | A | A | E |
| Subclasses | A | A | S | A | A | E |
| Class Features | ? | ? | ? | ? | A | E |
| Subclass Features | ? | ? | ? | ? | ? | E |
| Backgrounds | ? | ? | ? | ? | ? | E |
| Races and Subraces | ? | ? | P | ? | ? | E |
| Items and Base Items | ? | ? | ? | P | P | M |
| Magic Variants | ? | ? | ? | S | A | E |
| Conditions | ? | ? | ? | ? | A | A |
| Diseases | ? | ? | ? | A | A | E |
| Languages | A | ? | A | A | A | A |
| Skills | ? | ? | A | ? | A | A |
| Senses | A | ? | A | A | A | A |
| Objects | ? | ? | A | ? | ? | E |
| Bastion Facilities | ? | ? | ? | P | M | E |
| Rewards | ? | ? | P | A | ? | E |
| Traps | ? | ? | A | M | A | E |
| Hazards | ? | ? | ? | P | A | E |
| Vehicles | ? | ? | A | S | P | S |
| Vehicle Upgrades | ? | ? | ? | A | ? | E |
| Variant Rules | ? | ? | ? | ? | ? | E |
| Treasure Tables | A | A | A | A | A | S |
| Encounters | ? | ? | A | A | ? | E |
| Character Creation Options | ? | ? | ? | ? | ? | E |
| Cults | ? | ? | ? | ? | ? | E |
| Boons | ? | ? | ? | A | A | A |
| Deities | ? | ? | ? | ? | ? | ? |
| Name Tables | A | A | A | A | A | ? |
| Adventure Index | A | A | A | A | A | A |
| Books | A | A | A | A | A | A |
| Recipes | ? | ? | A | ? | A | ? |

## Concepts C31–C31

C31 Ability statistics/changes and senses

| Catalog | C31 |
|---|---|
| Monsters | P |
| Spells | ? |
| Actions | A |
| Feats | P |
| Optional Features | P |
| Classes | S |
| Subclasses | A |
| Class Features | ? |
| Subclass Features | ? |
| Backgrounds | P |
| Races and Subraces | P |
| Items and Base Items | P |
| Magic Variants | P |
| Conditions | A |
| Diseases | A |
| Languages | A |
| Skills | S |
| Senses | ? |
| Objects | S |
| Bastion Facilities | ? |
| Rewards | ? |
| Traps | A |
| Hazards | ? |
| Vehicles | S |
| Vehicle Upgrades | A |
| Variant Rules | ? |
| Treasure Tables | A |
| Encounters | A |
| Character Creation Options | ? |
| Cults | A |
| Boons | A |
| Deities | A |
| Name Tables | A |
| Adventure Index | A |
| Books | A |
| Recipes | A |

## Current DTE → normalized → public dispositions

The following reviews reflect Batches 1–4. Source/effective denominators are distinct. Java links identify actual source files, not proposed production types. Each catalog also has exhaustive per-tag counts and saved type examples in the machine evidence.

### Monsters

**4560 authored / 4851 effective.**

DTEs: [MonsterDte](../../../src/main/java/com/thedndpub/data/dte/monster/MonsterDte.java). Normalized: [MonsterDto](../../../src/main/java/com/thedndpub/data/dto/monster/MonsterDto.java), [Statistics](../../../src/main/java/com/thedndpub/data/dto/monster/statistics/Statistics.java), [Defenses](../../../src/main/java/com/thedndpub/data/dto/monster/defenses/Defenses.java), [Features](../../../src/main/java/com/thedndpub/data/dto/monster/features/Features.java), [SpellcastingBlock](../../../src/main/java/com/thedndpub/data/dto/monster/spellcasting/SpellcastingBlock.java). Public: [MonsterDto](../../../src/main/java/com/thedndpub/data/dto/monster/MonsterDto.java). Mapping/projection: [MonsterMapper.java](../../../src/main/java/com/thedndpub/data/dto/monster/MonsterMapper.java).

Internal: Resolved trees map to typed statistics, conditional defenses, rich content, action buckets and spell usage maps. Classification tags live separately in MonsterClassification. EntryDte validates authored content; MonsterMapper reads resolved JSON rather than mapping EntryDte instances.

Public: Stats, defenses, rich Feature.entries and spellcasting headers/footer are existing public content. Search condition/damage tags do not establish effect edges.

Gap/boundary: No formula/branch/target/Multiattack semantic model. Separate support gap: 188 legendary-group records have no discovered runtime body loader/model; only name/source group references survive.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 3531 / 4279 | 3367 / 9245 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].damageTags` |
| Attack labels and modifiers | 0 / 0 | 3446 / 16274 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].action[1].entries[0]` |
| Saving throws and DCs | 2599 / 4708 | 2478 / 6276 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Factol Skall AATM: `$.monster[1].savingThrowForced` |
| Ability and skill checks | 2463 / 2463 | 1 / 6 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Factol Skall AATM: `$.monster[1].skill` |
| Condition classification/application | 1861 / 2287 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Factol Skall AATM: `$.monster[1].conditionInflict` |
| Resistance, vulnerability and immunities | 2085 / 4076 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].immune` |
| Armor Class and modifiers | 3476 / 3476 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].ac` |
| Hit points, hit dice and thresholds | 3508 / 3508 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].hp` |
| Movement and speeds | 3478 / 3478 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].speed` |
| Costs, charges, uses and recharge | 1096 / 2535 | 856 / 881 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Factol Skall AATM: `$.monster[1].spellcasting[0].daily` |
| Action economy and initiative | 3446 / 5209 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].action` |
| Spellcasting and spell availability | 1165 / 1175 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Factol Skall AATM: `$.monster[1].spellcasting` |
| Choices and alternatives | 0 / 0 | 10 / 10 | [bestiary-awm.json](../../../src/main/resources/data/monsters/bestiary-awm.json) Saleeth the Couatl AWM: `$.monster[10].action[2].entries[0]` |
| Summoning and transformations | 42 / 69 | 0 / 0 | [bestiary-au.json](../../../src/main/resources/data/monsters/bestiary-au.json) Battle Familiar AU: `$.monster[2].summonedBySpell` |
| Multiattack and action sequences | 0 / 0 | 0 / 0 | [bestiary-mm.json](../../../src/main/resources/data/monsters/bestiary-mm.json) Adult Red Dragon MM: `$.monster[12].action[0].entries[0]` |
| Triggers and reactions | 0 / 0 | 76 / 152 | [bestiary-abh.json](../../../src/main/resources/data/monsters/bestiary-abh.json) Vampire Warden ABH: `$.monster[4].reaction[0].entries[0]` |
| Rest-based recovery | 6 / 6 | 0 / 0 | [bestiary-egw.json](../../../src/main/resources/data/monsters/bestiary-egw.json) Mossback Steward EGW: `$.monster[29].spellcasting[0].rest` |
| Environmental/exploration context | 1456 / 1456 | 0 / 0 | [bestiary-abh.json](../../../src/main/resources/data/monsters/bestiary-abh.json) Fiendish Icon ABH: `$.monster[0].environment` |
| Economy, quantities and roll tables | 0 / 0 | 601 / 839 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Factol Skall AATM: `$.monster[1].trait[3].entries[0]` |
| Ability statistics/changes and senses | 3529 / 23070 | 0 / 0 | [bestiary-aatm.json](../../../src/main/resources/data/monsters/bestiary-aatm.json) Animated Coffin AATM: `$.monster[0].str` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Spells

**969 authored / 969 effective.**

DTEs: [SpellDte](../../../src/main/java/com/thedndpub/data/dte/spell/SpellDte.java). Normalized: [SpellDefinition](../../../src/main/java/com/thedndpub/data/dto/spell/SpellDefinition.java), [SpellMechanics](../../../src/main/java/com/thedndpub/data/dto/spell/SpellMechanics.java), [SpellScaling](../../../src/main/java/com/thedndpub/data/dto/spell/SpellScaling.java). Public: [SpellDto](../../../src/main/java/com/thedndpub/data/dto/spell/api/SpellDto.java). Mapping/projection: [SpellPublicMapper.java](../../../src/main/java/com/thedndpub/data/dto/spell/api/SpellPublicMapper.java).

Internal: Casting alternatives, component variants, range geometry, duration/concentration, classifications and scaling already normalized. Shared entries and higher-level entries remain internally ordered.

Public: Existing structured casting/range/component/duration/classification/scaling metadata. Text qualifiers use existing withholding/presence conventions.

Gap/boundary: Damage/healing expressions, branch associations, target counts, secondary effects and prose area dimensions are not semantic effect models.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 361 / 361 | 362 / 790 | [spells-ai.json](../../../src/main/resources/data/spells/spells-ai.json) Jim's Magic Missile AI: `$.spell[5].damageInflict` |
| Healing | 0 / 0 | 0 / 0 | [spells-phb.json](../../../src/main/resources/data/spells/spells-phb.json) Healing Word PHB: `$.spell[175].entries[0]` |
| Temporary hit points | 0 / 0 | 0 / 0 | [spells-phb.json](../../../src/main/resources/data/spells/spells-phb.json) False Life PHB: `$.spell[126].entries[0]` |
| Attack labels and modifiers | 65 / 65 | 2 / 7 | [spells-ai.json](../../../src/main/resources/data/spells/spells-ai.json) Jim's Magic Missile AI: `$.spell[5].spellAttack` |
| Saving throws and DCs | 406 / 406 | 11 / 11 | [spells-ai.json](../../../src/main/resources/data/spells/spells-ai.json) Fast Friends AI: `$.spell[1].savingThrow` |
| Ability and skill checks | 57 / 57 | 0 / 0 | [spells-au.json](../../../src/main/resources/data/spells/spells-au.json) Illusory Dragon AU: `$.spell[13].abilityCheck` |
| Condition classification/application | 184 / 184 | 0 / 0 | [spells-ai.json](../../../src/main/resources/data/spells/spells-ai.json) Fast Friends AI: `$.spell[1].conditionInflict` |
| Resistance, vulnerability and immunities | 48 / 61 | 0 / 0 | [spells-au.json](../../../src/main/resources/data/spells/spells-au.json) Illusory Dragon AU: `$.spell[13].damageImmune` |
| Range, reach and area | 969 / 1612 | 0 / 0 | [spells-aag.json](../../../src/main/resources/data/spells/spells-aag.json) Air Bubble AAG: `$.spell[0].range` |
| Duration and concentration | 969 / 969 | 0 / 0 | [spells-aag.json](../../../src/main/resources/data/spells/spells-aag.json) Air Bubble AAG: `$.spell[0].duration` |
| Targets and restrictions | 77 / 77 | 0 / 0 | [spells-ai.json](../../../src/main/resources/data/spells/spells-ai.json) Fast Friends AI: `$.spell[1].affectsCreatureType` |
| Costs, charges, uses and recharge | 85 / 85 | 0 / 0 | [spells-aag.json](../../../src/main/resources/data/spells/spells-aag.json) Create Spelljamming Helm AAG: `$.spell[1].components.m.consume` |
| Action economy and initiative | 969 / 969 | 0 / 0 | [spells-aag.json](../../../src/main/resources/data/spells/spells-aag.json) Air Bubble AAG: `$.spell[0].time` |
| Level/slot progression | 41 / 41 | 157 / 162 | [spells-egw.json](../../../src/main/resources/data/spells/spells-egw.json) Sapping Sting EGW: `$.spell[10].scalingLevelDice` |
| Choices and alternatives | 0 / 0 | 23 / 44 | [spells-idrotf.json](../../../src/main/resources/data/spells/spells-idrotf.json) Create Magen IDRotF: `$.spell[0].entries[0]` |
| Economy, quantities and roll tables | 172 / 172 | 111 / 145 | [spells-aag.json](../../../src/main/resources/data/spells/spells-aag.json) Create Spelljamming Helm AAG: `$.spell[1].components.m.cost` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Actions

**48 authored / 48 effective.**

DTEs: [ActionDte](../../../src/main/java/com/thedndpub/data/dte/action/ActionDte.java). Normalized: [ActionDefinition](../../../src/main/java/com/thedndpub/data/dto/action/ActionDefinition.java). Public: [ActionDto](../../../src/main/java/com/thedndpub/data/dto/action/ActionDto.java). Mapping/projection: [ActionMapper.java](../../../src/main/java/com/thedndpub/data/dto/action/ActionMapper.java).

Internal: Ordered timing variants normalized; entry trees retained in import records.

Public: Timing and identity/provenance, without rule-entry prose.

Gap/boundary: Checks, outcomes, action restrictions and contest relationships remain entry semantics.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Saving throws and DCs | 0 / 0 | 2 / 2 | [actions.json](../../../src/main/resources/data/actions.json) Hide XPHB: `$.action[23].entries[0]` |
| Action economy and initiative | 47 / 47 | 0 / 0 | [actions.json](../../../src/main/resources/data/actions.json) Activate an Item DMG: `$.action[0].time` |
| Economy, quantities and roll tables | 0 / 0 | 1 / 1 | [actions.json](../../../src/main/resources/data/actions.json) Stabilize a Creature PHB: `$.action[40].entries[1]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Feats

**305 authored / 314 effective.**

DTEs: [FeatDte](../../../src/main/java/com/thedndpub/data/dte/feat/FeatDte.java). Normalized: [FeatDefinition](../../../src/main/java/com/thedndpub/data/dto/feat/FeatDefinition.java), [FeatMechanics](../../../src/main/java/com/thedndpub/data/dto/feat/FeatMechanics.java), [FeatPrerequisiteProficiencyGroup](../../../src/main/java/com/thedndpub/data/dto/feat/FeatPrerequisiteProficiencyGroup.java). Public: [FeatDto](../../../src/main/java/com/thedndpub/data/dto/feat/FeatDto.java). Mapping/projection: [FeatMapper.java](../../../src/main/java/com/thedndpub/data/dto/feat/FeatMapper.java).

Internal: Ability/prerequisite/spell metadata plus Batch 3A grant/defense/sense/progression bundles and Batch 3B proficiency requirement groups are normalized. Entries and version lineage retained.

Public: Existing ability/prerequisite/spell/repeatability metadata. New Batch 3A/3B fields remain internal.

Gap/boundary: Prose resource resets and conditional benefits lack semantic models. G3/G4 are resolved, not current omissions.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 19 / 25 | [feats.json](../../../src/main/resources/data/feats.json) Agent of Order SatO: `$.feat[10].entries[1].items[0].entries[0]` |
| Saving throws and DCs | 0 / 0 | 9 / 10 | [feats.json](../../../src/main/resources/data/feats.json) Actor XPHB: `$.feat[6].entries[2].entries[0]` |
| Resistance, vulnerability and immunities | 14 / 15 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Boon of Blazing Dawn ABH: `$.feat[30].immune` |
| Costs, charges, uses and recharge | 50 / 107 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Dragonmark EFA: `$.feat[1].additionalSpells[0].prepared._.rest` |
| Spellcasting and spell availability | 86 / 86 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Dragonmark EFA: `$.feat[1].additionalSpells` |
| Level/slot progression | 4 / 4 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Eldritch Adept TCE: `$.feat[98].optionalfeatureProgression` |
| Proficiency and expertise grants | 38 / 50 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Anatomy RHW: `$.feat[0].skillProficiencies` |
| Prerequisites and equipment restrictions | 224 / 224 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Anatomy RHW: `$.feat[0].prerequisite` |
| Choices and alternatives | 213 / 361 | 32 / 69 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Dragonmark EFA: `$.feat[1].additionalSpells[0].prepared._.rest.1[0].choose` |
| Rest-based recovery | 2 / 2 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Dragonmark EFA: `$.feat[1].additionalSpells[0].prepared._.rest` |
| Economy, quantities and roll tables | 0 / 0 | 64 / 86 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Anatomy RHW: `$.feat[0].entries[3].entries[0]` |
| Ability statistics/changes and senses | 180 / 183 | 0 / 0 | [feats.json](../../../src/main/resources/data/feats.json) Aberrant Anatomy RHW: `$.feat[0].senses` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Optional Features

**221 authored / 221 effective.**

DTEs: [OptionalFeatureDte](../../../src/main/java/com/thedndpub/data/dte/optionalfeature/OptionalFeatureDte.java). Normalized: [OptionalFeatureDefinition](../../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureDefinition.java). Public: [OptionalFeatureDto](../../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureDto.java). Mapping/projection: [OptionalFeatureMapper.java](../../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureMapper.java).

Internal: Consumption amount/min/max, grants, senses, prerequisites and progression normalized. Entry trees stay in import records.

Public: Existing structured fields and selected prerequisite text; seven authored roots/14 strings require publication review.

Gap/boundary: A consumes declaration gives no generalized pool/reset rule or full effect.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 17 / 32 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Arcane Propulsion Armor TCE: `$.optionalfeature[4].entries[1].items[1]` |
| Costs, charges, uses and recharge | 91 / 91 | 0 / 0 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Ambush TCE: `$.optionalfeature[2].consumes` |
| Spellcasting and spell availability | 54 / 54 | 0 / 0 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Armor of Shadows PHB: `$.optionalfeature[7].additionalSpells` |
| Level/slot progression | 2 / 2 | 0 / 0 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Lessons of the First Ones XPHB: `$.optionalfeature[112].featProgression` |
| Proficiency and expertise grants | 1 / 1 | 0 / 0 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Beguiling Influence PHB: `$.optionalfeature[18].skillProficiencies` |
| Prerequisites and equipment restrictions | 95 / 95 | 0 / 0 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Agonizing Blast PHB: `$.optionalfeature[0].prerequisite` |
| Choices and alternatives | 5 / 6 | 12 / 13 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Blessed Warrior TCE: `$.optionalfeature[21].additionalSpells[0].known._[0].choose` |
| Economy, quantities and roll tables | 0 / 0 | 12 / 12 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Armor of Magical Strength TCE: `$.optionalfeature[6].entries[2]` |
| Ability statistics/changes and senses | 3 / 3 | 0 / 0 | [optionalfeatures.json](../../../src/main/resources/data/optionalfeatures.json) Blind Fighting TCE: `$.optionalfeature[22].senses` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Classes

**30 authored / 30 effective.**

DTEs: [ClassDte](../../../src/main/java/com/thedndpub/data/dte/classes/ClassDte.java). Normalized: [ClassDefinition](../../../src/main/java/com/thedndpub/data/dto/classes/ClassDefinition.java), [ClassProgressionTable](../../../src/main/java/com/thedndpub/data/dto/classes/ClassProgressionTable.java), [ClassSpellcasting](../../../src/main/java/com/thedndpub/data/dto/classes/ClassSpellcasting.java). Public: [ClassDto](../../../src/main/java/com/thedndpub/data/dto/classes/ClassDto.java). Mapping/projection: [ClassPublicMapper.java](../../../src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java).

Internal: Hit dice, saves, starting/multiclass options, feature graph, schedules and ordered progression cells normalized.

Public: Structured mechanics plus Batch 1 positionedCells. Presentation cells remain withheld.

Gap/boundary: Numeric-looking source text remains deliberately text; preparation formulas stay unevaluated. G1 is resolved.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Saving throws and DCs | 27 / 27 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].proficiency` |
| Hit points, hit dice and thresholds | 27 / 27 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].hd` |
| Spellcasting and spell availability | 19 / 49 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].spellcastingAbility` |
| Level/slot progression | 28 / 77 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].cantripProgression` |
| Proficiency and expertise grants | 27 / 54 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].startingProficiencies` |
| Prerequisites and equipment restrictions | 26 / 26 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].multiclassing` |
| Choices and alternatives | 27 / 38 | 25 / 200 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].startingProficiencies.skills[0].choose` |
| Economy, quantities and roll tables | 0 / 0 | 14 / 14 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer TCE: `$.class[0].startingEquipment.goldAlternative` |
| Ability statistics/changes and senses | 13 / 13 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Artificer EFA: `$.class[1].primaryAbility` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Subclasses

**330 authored / 330 effective.**

DTEs: [SubclassDte](../../../src/main/java/com/thedndpub/data/dte/classes/SubclassDte.java). Normalized: [SubclassDefinition](../../../src/main/java/com/thedndpub/data/dto/classes/SubclassDefinition.java), [ClassProgressionTable](../../../src/main/java/com/thedndpub/data/dto/classes/ClassProgressionTable.java). Public: [SubclassDto](../../../src/main/java/com/thedndpub/data/dto/classes/SubclassDto.java). Mapping/projection: [ClassPublicMapper.java](../../../src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java).

Internal: Parent context, copy-effective graph, spell overrides and progression tables normalized.

Public: Structured graph/schedules/tables with original safe-cell column indexes.

Gap/boundary: Conditional feature replacements/effects are not inferred; no remaining G1 column-position defect.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Costs, charges, uses and recharge | 11 / 13 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Alchemist EFA: `$.subclass[8].additionalSpells[0].innate.9.daily` |
| Spellcasting and spell availability | 134 / 147 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Alchemist TCE: `$.subclass[0].additionalSpells` |
| Level/slot progression | 16 / 26 | 0 / 0 | [class-bard.json](../../../src/main/resources/data/classes/class-bard.json) College of Swords XGE: `$.subclass[6].optionalfeatureProgression` |
| Choices and alternatives | 18 / 99 | 5 / 27 | [class-bard.json](../../../src/main/resources/data/classes/class-bard.json) College of Lore PHB: `$.subclass[0].additionalSpells[0].known.6[0].choose` |
| Rest-based recovery | 1 / 1 | 0 / 0 | [class-rogue.json](../../../src/main/resources/data/classes/class-rogue.json) Phantom RHW: `$.subclass[23].additionalSpells[0].innate.9.rest` |
| Economy, quantities and roll tables | 0 / 0 | 2 / 36 | [class-fighter.json](../../../src/main/resources/data/classes/class-fighter.json) Psi Warrior XPHB: `$.subclass[23].subclassTableGroups[0].rows[2][0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Class Features

**677 authored / 677 effective.**

DTEs: [ClassFeatureDte](../../../src/main/java/com/thedndpub/data/dte/classes/ClassFeatureDte.java). Normalized: [ClassFeatureDefinition](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureDefinition.java), [ClassFeatureNode](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureNode.java), [ClassFeatureAbilityDeclaration](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureAbilityDeclaration.java), [ClassFeatureChoice](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureChoice.java). Public: [ClassFeatureDto](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureDto.java). Mapping/projection: [ClassFeatureContentNormalizer.java](../../../src/main/java/com/thedndpub/data/importer/classes/ClassFeatureContentNormalizer.java).

Internal: Typed content graph preserves sections, tables, options, references, ability declarations and consumption. Parent/level identity preserved.

Public: References, reference-backed choices and consumption; ability declarations, prose and feature-entry tables withheld.

Gap/boundary: Already typed abilityDc/abilityAttackMod declarations need no prose extraction. Non-reference choice options are absent from the public choice list; this is an incomplete public view, not a lost internal grant.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 17 / 25 | [class-barbarian.json](../../../src/main/resources/data/classes/class-barbarian.json) Brutal Strike XPHB: `$.classFeature[39].entries[0]` |
| Attack labels and modifiers | 11 / 11 | 0 / 0 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Spellcasting TCE: `$.classFeature[21].entries[5].entries[2]` |
| Saving throws and DCs | 12 / 12 | 4 / 4 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Spellcasting TCE: `$.classFeature[21].entries[5].entries[1]` |
| Costs, charges, uses and recharge | 24 / 24 | 0 / 0 | [class-cleric.json](../../../src/main/resources/data/classes/class-cleric.json) Channel Divinity: Harness Divine Power TCE: `$.classFeature[3].consumes` |
| Choices and alternatives | 12 / 12 | 81 / 147 | [class-cleric.json](../../../src/main/resources/data/classes/class-cleric.json) Divine Order XPHB: `$.classFeature[24].entries[1].entries[0]` |
| Economy, quantities and roll tables | 0 / 0 | 32 / 50 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Replicate Magic Item EFA: `$.classFeature[2].entries[1].entries[2].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Subclass Features

**1499 authored / 1499 effective.**

DTEs: [SubclassFeatureDte](../../../src/main/java/com/thedndpub/data/dte/classes/SubclassFeatureDte.java). Normalized: [SubclassFeatureDefinition](../../../src/main/java/com/thedndpub/data/dto/classes/SubclassFeatureDefinition.java), [ClassFeatureNode](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureNode.java), [ClassFeatureAbilityDeclaration](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureAbilityDeclaration.java), [ClassFeatureChoice](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureChoice.java). Public: [SubclassFeatureDto](../../../src/main/java/com/thedndpub/data/dto/classes/SubclassFeatureDto.java). Mapping/projection: [ClassFeatureContentNormalizer.java](../../../src/main/java/com/thedndpub/data/importer/classes/ClassFeatureContentNormalizer.java).

Internal: Same typed content graph, level/parent/copy context and consumption.

Public: References, reference-backed choices and consumption. Full internal option content remains withheld.

Gap/boundary: Do not treat a count plus a public subset of referenced options as the complete authored choice universe.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 122 / 178 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Chemical Mastery EFA: `$.subclassFeature[6].entries[1].entries[0]` |
| Attack labels and modifiers | 2 / 2 | 0 / 0 | [class-fighter.json](../../../src/main/resources/data/classes/class-fighter.json) Spellcasting PHB: `$.subclassFeature[28].entries[4].entries[0].entries[2]` |
| Saving throws and DCs | 4 / 4 | 11 / 11 | [class-fighter.json](../../../src/main/resources/data/classes/class-fighter.json) Combat Superiority PHB: `$.subclassFeature[8].entries[3].entries[0].entries[1]` |
| Costs, charges, uses and recharge | 115 / 115 | 0 / 0 | [class-cleric.json](../../../src/main/resources/data/classes/class-cleric.json) Channel Divinity: Touch of Death DMG: `$.subclassFeature[3].consumes` |
| Choices and alternatives | 28 / 28 | 65 / 110 | [class-barbarian.json](../../../src/main/resources/data/classes/class-barbarian.json) Totem Spirit PHB: `$.subclassFeature[18].entries[2]` |
| Economy, quantities and roll tables | 0 / 0 | 127 / 185 | [class-artificer.json](../../../src/main/resources/data/classes/class-artificer.json) Experimental Elixir EFA: `$.subclassFeature[2].entries[3].rows[0][1]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Backgrounds

**171 authored / 171 effective.**

DTEs: [BackgroundDte](../../../src/main/java/com/thedndpub/data/dte/background/BackgroundDte.java). Normalized: [BackgroundDefinition](../../../src/main/java/com/thedndpub/data/dto/background/BackgroundDefinition.java). Public: [BackgroundDto](../../../src/main/java/com/thedndpub/data/dto/background/BackgroundDto.java). Mapping/projection: [BackgroundMapper.java](../../../src/main/java/com/thedndpub/data/dto/background/BackgroundMapper.java).

Internal: Ability/proficiency/feat/equipment/spell/prerequisite groups normalized after copy handling; entries remain in import provenance.

Public: Existing structured options; descriptive feature/characteristic-table content withheld.

Gap/boundary: No demonstrated new grant-loss defect. Background feature prose does not establish unconditional mechanical grants.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Spellcasting and spell availability | 15 / 15 | 0 / 0 | [backgrounds.json](../../../src/main/resources/data/backgrounds.json) Azorius Functionary GGR: `$.background[11].additionalSpells` |
| Proficiency and expertise grants | 146 / 327 | 0 / 0 | [backgrounds.json](../../../src/main/resources/data/backgrounds.json) Aberrant Heir EFA: `$.background[0].skillProficiencies` |
| Prerequisites and equipment restrictions | 4 / 4 | 0 / 0 | [backgrounds.json](../../../src/main/resources/data/backgrounds.json) Gate Warden SatO: `$.background[60].prerequisite` |
| Choices and alternatives | 92 / 167 | 45 / 91 | [backgrounds.json](../../../src/main/resources/data/backgrounds.json) Aberrant Heir EFA: `$.background[0].ability[0].choose` |
| Economy, quantities and roll tables | 0 / 0 | 5 / 5 | [backgrounds.json](../../../src/main/resources/data/backgrounds.json) Astral Drifter AAG: `$.background[8].entries[1].entries[0]` |
| Ability statistics/changes and senses | 70 / 70 | 0 / 0 | [backgrounds.json](../../../src/main/resources/data/backgrounds.json) Aberrant Heir EFA: `$.background[0].ability` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Races and Subraces

**258 authored / 356 effective.**

DTEs: [RaceDte](../../../src/main/java/com/thedndpub/data/dte/race/RaceDte.java), [SubraceDte](../../../src/main/java/com/thedndpub/data/dte/race/SubraceDte.java). Normalized: [RaceDefinition](../../../src/main/java/com/thedndpub/data/dto/race/RaceDefinition.java). Public: [RaceDto](../../../src/main/java/com/thedndpub/data/dto/race/RaceDto.java). Mapping/projection: [RaceDefinitionNormalizer.java](../../../src/main/java/com/thedndpub/data/dto/race/RaceDefinitionNormalizer.java).

Internal: Abilities, movement numeric/flag alternatives, senses, defenses, grants, spell schedules and lineage normalized. Entry trees retained internally.

Public: Existing structured metadata/grants, without trait prose.

Gap/boundary: Conditional traits and resets remain prose. Parent identity does not itself imply automatic merging of every parent mechanic.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 37 / 65 | [races.json](../../../src/main/resources/data/races.json) Aarakocra DMG: `$.race[0].entries[0].entries[0]` |
| Saving throws and DCs | 0 / 0 | 8 / 10 | [races.json](../../../src/main/resources/data/races.json) Aasimar MPMM: `$.race[4].entries[5].entries[1].items[0].entry` |
| Resistance, vulnerability and immunities | 51 / 59 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Aasimar DMG: `$.race[3].resist` |
| Movement and speeds | 156 / 156 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Aarakocra DMG: `$.race[0].speed` |
| Costs, charges, uses and recharge | 51 / 101 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Aasimar DMG: `$.race[3].additionalSpells[0].innate.3.daily` |
| Spellcasting and spell availability | 80 / 80 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Aarakocra MPMM: `$.race[2].additionalSpells` |
| Proficiency and expertise grants | 144 / 197 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Aarakocra DMG: `$.race[0].languageProficiencies` |
| Choices and alternatives | 78 / 94 | 17 / 17 | [races.json](../../../src/main/resources/data/races.json) Aarakocra MPMM: `$.race[2].additionalSpells[0].ability.choose` |
| Rest-based recovery | 4 / 4 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Firbolg VGM: `$.race[46].additionalSpells[0].known.1.rest` |
| Economy, quantities and roll tables | 0 / 0 | 39 / 44 | [races.json](../../../src/main/resources/data/races.json) Aetherborn PSK: `$.race[7].entries[7].entries[4]` |
| Ability statistics/changes and senses | 193 / 239 | 0 / 0 | [races.json](../../../src/main/resources/data/races.json) Aarakocra DMG: `$.race[0].ability` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Items and Base Items

**2742 authored / 2742 effective.**

DTEs: [ItemSourceDte](../../../src/main/java/com/thedndpub/data/dte/item/ItemSourceDte.java), [ItemSourceDte](../../../src/main/java/com/thedndpub/data/dte/item/ItemSourceDte.java). Normalized: [ItemDefinition](../../../src/main/java/com/thedndpub/data/dto/item/ItemDefinition.java), [ItemFocus](../../../src/main/java/com/thedndpub/data/dto/item/ItemFocus.java), [ItemSignedBonuses](../../../src/main/java/com/thedndpub/data/dto/item/ItemSignedBonuses.java), [ItemChargeDto](../../../src/main/java/com/thedndpub/data/dto/item/ItemChargeDto.java), [ItemProtectionDto](../../../src/main/java/com/thedndpub/data/dto/item/ItemProtectionDto.java). Public: [ItemDto](../../../src/main/java/com/thedndpub/data/dto/item/ItemDto.java). Mapping/projection: [ItemMapper.java](../../../src/main/java/com/thedndpub/data/dto/item/ItemMapper.java).

Internal: Damage/type, AC, properties, charges, attunement, abilities, bonuses, defenses, light/capacity/movement and spell schedules normalized. Batch 2 preserves focus roles; Batch 4 adds exact signed-token/integer values.

Public: Legacy structured equipment fields, legacy bonus strings and charge strings. New focus/signed-bonus structures remain internal. Some charge strings already contain markup.

Gap/boundary: Focus and signed-bonus gaps are resolved. Item property/mastery UID/note is not a model of the referenced rule effect. Charge/reset expression semantics and conditional effects remain separate.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 342 / 834 | 304 / 424 | [items-base.json](../../../src/main/resources/data/items-base.json) Antimatter Rifle DMG: `$.baseitem[2].dmg1` |
| Attack labels and modifiers | 248 / 264 | 49 / 56 | [items.json](../../../src/main/resources/data/items.json) +1 All-Purpose Tool TCE: `$.item[0].bonusSpellAttack` |
| Saving throws and DCs | 62 / 62 | 568 / 750 | [items.json](../../../src/main/resources/data/items.json) +1 All-Purpose Tool TCE: `$.item[0].bonusSpellSaveDc` |
| Ability and skill checks | 3 / 3 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Stone of Good Luck DMG: `$.item[2220].bonusAbilityCheck` |
| Resistance, vulnerability and immunities | 162 / 174 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Acid Absorbing Tattoo TCE: `$.item[47].resist` |
| Armor Class and modifiers | 179 / 224 | 0 / 0 | [items-base.json](../../../src/main/resources/data/items-base.json) Breastplate PHB: `$.baseitem[23].ac` |
| Hit points, hit dice and thresholds | 35 / 65 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Airship DMG: `$.item[53].vehHp` |
| Movement and speeds | 98 / 98 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Airship DMG: `$.item[53].vehSpeed` |
| Range, reach and area | 113 / 113 | 0 / 0 | [items-base.json](../../../src/main/resources/data/items-base.json) Antimatter Rifle DMG: `$.baseitem[2].range` |
| Costs, charges, uses and recharge | 331 / 852 | 0 / 0 | [items-base.json](../../../src/main/resources/data/items-base.json) Antimatter Rifle DMG: `$.baseitem[2].reload` |
| Spellcasting and spell availability | 461 / 461 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Adze of Annam BGG: `$.item[51].attachedSpells` |
| Proficiency and expertise grants | 47 / 47 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Akmon, Hammer of Purphoros MOT: `$.item[55].grantsProficiency` |
| Prerequisites and equipment restrictions | 899 / 1152 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) +1 All-Purpose Tool TCE: `$.item[0].reqAttune` |
| Choices and alternatives | 3 / 3 | 24 / 33 | [items.json](../../../src/main/resources/data/items.json) Book of Vile Darkness DMG: `$.item[271].ability.choose` |
| Environmental/exploration context | 128 / 128 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Adventurer's Ring FRHoF: `$.item[50].light` |
| Vehicle and facility mechanics | 134 / 183 | 0 / 0 | [items-base.json](../../../src/main/resources/data/items-base.json) Antimatter Rifle DMG: `$.baseitem[2].weapon` |
| Economy, quantities and roll tables | 897 / 897 | 486 / 982 | [items-base.json](../../../src/main/resources/data/items-base.json) Alchemist's Supplies PHB: `$.baseitem[0].value` |
| Ability statistics/changes and senses | 72 / 72 | 0 / 0 | [items.json](../../../src/main/resources/data/items.json) Amulet of Health DMG: `$.item[82].ability` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Magic Variants

**230 authored / 230 effective.**

DTEs: [MagicVariantDte](../../../src/main/java/com/thedndpub/data/dte/magicvariant/MagicVariantDte.java). Normalized: [MagicVariantDto](../../../src/main/java/com/thedndpub/data/dto/magicvariant/MagicVariantDto.java). Public: [MagicVariantDto](../../../src/main/java/com/thedndpub/data/dto/magicvariant/MagicVariantDto.java). Mapping/projection: [MagicVariantMapper.java](../../../src/main/java/com/thedndpub/data/dto/magicvariant/MagicVariantMapper.java).

Internal: Requirement alternatives, exclusions and inherited typed transforms retained; matching is distinct from concrete item generation.

Public: Existing reviewed matching/transformation metadata; inherited prose withheld.

Gap/boundary: Do not apply Item signed-bonus grammar to every variant expression without a separate source audit; generated/conditional transforms differ.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 39 / 48 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Ammunition of Slaying XDMG: `$.magicvariant[34].inherits.entries[0]` |
| Attack labels and modifiers | 67 / 67 | 18 / 18 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) +1 Ammunition DMG: `$.magicvariant[0].inherits.bonusWeapon` |
| Saving throws and DCs | 2 / 2 | 35 / 42 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Luck Blade DMG: `$.magicvariant[155].inherits.bonusSavingThrow` |
| Resistance, vulnerability and immunities | 30 / 33 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Armor of Acid Resistance DMG: `$.magicvariant[38].inherits.resist` |
| Armor Class and modifiers | 26 / 26 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) +1 Armor DMG: `$.magicvariant[2].inherits.bonusAc` |
| Movement and speeds | 5 / 5 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Aquatic Wave-Swept Weapon AU: `$.magicvariant[36].inherits.modifySpeed` |
| Costs, charges, uses and recharge | 32 / 88 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Armor of Weightlessness BMT: `$.magicvariant[66].inherits.charges` |
| Spellcasting and spell availability | 30 / 30 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Antimagic Armor BMT: `$.magicvariant[35].inherits.attachedSpells` |
| Proficiency and expertise grants | 4 / 4 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Demon Armor XDMG: `$.magicvariant[89].inherits.grantsLanguage` |
| Prerequisites and equipment restrictions | 227 / 414 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) +1 Ammunition DMG: `$.magicvariant[0].requires` |
| Choices and alternatives | 0 / 0 | 18 / 18 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Enspelled Armor (Cantrip) XDMG: `$.magicvariant[108].inherits.entries[0]` |
| Environmental/exploration context | 13 / 13 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Aquatic Wave-Swept Weapon AU: `$.magicvariant[36].inherits.light` |
| Economy, quantities and roll tables | 0 / 0 | 49 / 87 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Acheron Blade EGW: `$.magicvariant[27].inherits.entries[1].entries[0]` |
| Ability statistics/changes and senses | 1 / 1 | 0 / 0 | [magicvariants.json](../../../src/main/resources/data/magicvariants.json) Hammer of Thunderbolts XDMG: `$.magicvariant[140].inherits.ability` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Conditions

**30 authored / 30 effective.**

DTEs: [ConditionDte](../../../src/main/java/com/thedndpub/data/dte/condition/ConditionDte.java). Normalized: [ConditionDefinition](../../../src/main/java/com/thedndpub/data/dto/condition/ConditionDefinition.java). Public: [ConditionDto](../../../src/main/java/com/thedndpub/data/dto/condition/ConditionDto.java). Mapping/projection: [ConditionMapper.java](../../../src/main/java/com/thedndpub/data/dto/condition/ConditionMapper.java).

Internal: Definition/provenance normalized; typed rule entries retained in import records. Five Status roots are additionally parsed/validated by ConditionCatalog but only their count is retained there.

Public: Identity/source/reprint metadata, without condition instructions.

Gap/boundary: Restrictions, exceptions and condition interactions lack effect models; Status descriptions are not retained in normalized definitions.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Advantage and disadvantage | 0 / 0 | 0 / 0 | [conditionsdiseases.json](../../../src/main/resources/data/conditionsdiseases.json) Blinded PHB: `$.condition[0].entries[0].items[1]` |
| Movement and speeds | 0 / 0 | 0 / 0 | [conditionsdiseases.json](../../../src/main/resources/data/conditionsdiseases.json) Grappled PHB: `$.condition[10].entries[0].items[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Diseases

**29 authored / 29 effective.**

DTEs: [DiseaseDte](../../../src/main/java/com/thedndpub/data/dte/condition/DiseaseDte.java). Normalized: [DiseaseDefinition](../../../src/main/java/com/thedndpub/data/dto/condition/DiseaseDefinition.java). Public: [DiseaseDto](../../../src/main/java/com/thedndpub/data/dto/condition/DiseaseDto.java). Mapping/projection: [DiseaseMapper.java](../../../src/main/java/com/thedndpub/data/dto/condition/DiseaseMapper.java).

Internal: Identity/classification normalized; full typed entries retained internally.

Public: Identity/classification/provenance metadata.

Gap/boundary: Infection, stages, checks, treatment and recovery are descriptive rather than a structured disease process.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 4 / 5 | [conditionsdiseases.json](../../../src/main/resources/data/conditionsdiseases.json) Bluerot GoS: `$.disease[3].entries[1]` |
| Saving throws and DCs | 0 / 0 | 21 / 39 | [conditionsdiseases.json](../../../src/main/resources/data/conditionsdiseases.json) Arcane Blight IDRotF: `$.disease[0].entries[0]` |
| Economy, quantities and roll tables | 0 / 0 | 15 / 21 | [conditionsdiseases.json](../../../src/main/resources/data/conditionsdiseases.json) Arcane Blight IDRotF: `$.disease[0].entries[2]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Languages

**201 authored / 201 effective.**

DTEs: [LanguageDte](../../../src/main/java/com/thedndpub/data/dte/language/LanguageDte.java). Normalized: [NormalizedLanguageDefinition](../../../src/main/java/com/thedndpub/data/dto/language/NormalizedLanguageDefinition.java). Public: [LanguageDto](../../../src/main/java/com/thedndpub/data/dto/language/LanguageDto.java). Mapping/projection: [LanguageMapper.java](../../../src/main/java/com/thedndpub/data/dto/language/LanguageMapper.java).

Internal: Classification/script/speaker/dialect/source metadata; internal entries where authored.

Public: Existing language metadata.

Gap/boundary: Language descriptions/mentions are not proficiency grants; no gameplay extraction justified.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Saving throws and DCs | 0 / 0 | 1 / 1 | [languages.json](../../../src/main/resources/data/languages.json) Druidic PHB: `$.language[51].entries[0]` |
| Choices and alternatives | 0 / 0 | 33 / 36 | [languages.json](../../../src/main/resources/data/languages.json) Abyssal GGR: `$.language[3].typicalSpeakers[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Skills

**36 authored / 36 effective.**

DTEs: [SkillDte](../../../src/main/java/com/thedndpub/data/dte/skill/SkillDte.java). Normalized: [SkillDefinition](../../../src/main/java/com/thedndpub/data/dto/skill/SkillDefinition.java). Public: [SkillDto](../../../src/main/java/com/thedndpub/data/dto/skill/SkillDto.java). Mapping/projection: [SkillMapper.java](../../../src/main/java/com/thedndpub/data/dto/skill/SkillMapper.java).

Internal: Associated ability normalized; descriptive examples retained in import records.

Public: Associated ability and identity.

Gap/boundary: Example DCs or alternate abilities are not a universal check specification.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Ability statistics/changes and senses | 36 / 36 | 0 / 0 | [skills.json](../../../src/main/resources/data/skills.json) Acrobatics PHB: `$.skill[0].ability` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Senses

**8 authored / 8 effective.**

DTEs: [SenseDte](../../../src/main/java/com/thedndpub/data/dte/sense/SenseDte.java). Normalized: [SenseDefinition](../../../src/main/java/com/thedndpub/data/dto/sense/SenseDefinition.java). Public: [SenseDto](../../../src/main/java/com/thedndpub/data/dto/sense/SenseDto.java). Mapping/projection: [SenseMapper.java](../../../src/main/java/com/thedndpub/data/dto/sense/SenseMapper.java).

Internal: Identity/reprints normalized; definition text retained in source/import content.

Public: Metadata only.

Gap/boundary: No common numerical sense range exists on these definitions; owner-specific ranges must remain distinct.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Objects

**37 authored / 37 effective.**

DTEs: [ObjectSourceDte](../../../src/main/java/com/thedndpub/data/dte/object/ObjectSourceDte.java). Normalized: [ObjectDefinition](../../../src/main/java/com/thedndpub/data/dto/object/ObjectDefinition.java). Public: [ObjectDetailDto](../../../src/main/java/com/thedndpub/data/dto/object/ObjectDetailDto.java). Mapping/projection: [ObjectDefinitionMapper.java](../../../src/main/java/com/thedndpub/data/dto/object/ObjectDefinitionMapper.java).

Internal: Stats, movement, defenses and dedicated typed Object actions retained, including seven explicit attack/hit containers.

Public: Safe stats; descriptive special values use presence conventions; actions withheld.

Gap/boundary: Container boundaries are direct. Targets, damage type and riders usually remain text; no Object-to-creature equivalence.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 28 / 30 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].actionEntries[0].entries[0].hitEntries[0]` |
| Attack labels and modifiers | 7 / 7 | 17 / 36 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].actionEntries[0].entries[0]` |
| Saving throws and DCs | 0 / 0 | 7 / 20 | [objects.json](../../../src/main/resources/data/objects.json) Boilerdrak DSotDQ: `$.object[4].actionEntries[0].entries[0]` |
| Resistance, vulnerability and immunities | 21 / 27 | 0 / 0 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].immune` |
| Armor Class and modifiers | 37 / 37 | 0 / 0 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].ac` |
| Hit points, hit dice and thresholds | 37 / 37 | 0 / 0 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].hp` |
| Movement and speeds | 4 / 4 | 0 / 0 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].speed` |
| Economy, quantities and roll tables | 0 / 0 | 3 / 4 | [objects.json](../../../src/main/resources/data/objects.json) Boilerdrak DSotDQ: `$.object[4].entries[0]` |
| Ability statistics/changes and senses | 6 / 35 | 0 / 0 | [objects.json](../../../src/main/resources/data/objects.json) Animated Ghaunadaur Statue WDMM: `$.object[0].str` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Bastion Facilities

**70 authored / 70 effective.**

DTEs: [FacilityDte](../../../src/main/java/com/thedndpub/data/dte/bastion/FacilityDte.java). Normalized: [FacilityDefinition](../../../src/main/java/com/thedndpub/data/dto/bastion/FacilityDefinition.java). Public: [FacilityDetailDto](../../../src/main/java/com/thedndpub/data/dto/bastion/FacilityDetailDto.java). Mapping/projection: [FacilityMapper.java](../../../src/main/java/com/thedndpub/data/dto/bastion/FacilityMapper.java).

Internal: Levels, space, hireling variants, order labels and structured prerequisites normalized; entries retained internally.

Public: Metadata and reviewed prerequisite structure.

Gap/boundary: Order label does not encode production, timing, cost, granting or operating rules.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 3 / 3 | [bastions.json](../../../src/main/resources/data/bastions.json) Lightning Generator RHW: `$.facility[32].entries[1].entries[0]` |
| Saving throws and DCs | 0 / 0 | 1 / 1 | [bastions.json](../../../src/main/resources/data/bastions.json) Theater XDMG: `$.facility[63].entries[1].entries[3]` |
| Prerequisites and equipment restrictions | 34 / 34 | 0 / 0 | [bastions.json](../../../src/main/resources/data/bastions.json) Amethyst Dragon Den FRHoF: `$.facility[0].prerequisite` |
| Choices and alternatives | 0 / 0 | 4 / 7 | [bastions.json](../../../src/main/resources/data/bastions.json) Cabinet of Curiosities RHW: `$.facility[8].entries[1].entries[1].rows[0][1]` |
| Environmental/exploration context | 70 / 70 | 0 / 0 | [bastions.json](../../../src/main/resources/data/bastions.json) Amethyst Dragon Den FRHoF: `$.facility[0].space` |
| Vehicle and facility mechanics | 64 / 128 | 0 / 0 | [bastions.json](../../../src/main/resources/data/bastions.json) Amethyst Dragon Den FRHoF: `$.facility[0].hirelings` |
| Economy, quantities and roll tables | 0 / 0 | 12 / 27 | [bastions.json](../../../src/main/resources/data/bastions.json) Armory XDMG: `$.facility[4].entries[1].entries[1]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Rewards

**278 authored / 278 effective.**

DTEs: [RewardDte](../../../src/main/java/com/thedndpub/data/dte/reward/RewardDte.java). Normalized: [RewardDefinition](../../../src/main/java/com/thedndpub/data/dto/reward/RewardDefinition.java). Public: [RewardDetailDto](../../../src/main/java/com/thedndpub/data/dto/reward/RewardDetailDto.java). Mapping/projection: [RewardMapper.java](../../../src/main/java/com/thedndpub/data/dto/reward/RewardMapper.java).

Internal: Type/rarity and spell benefits normalized; typed entries include three inline Object statblocks.

Public: Metadata and existing structured spell benefits.

Gap/boundary: Other benefit prose/inline statblocks withheld; not an automatic generic reward grant.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 7 / 9 | [rewards.json](../../../src/main/resources/data/rewards.json) Charm of Biting Cold IDRotF: `$.reward[79].entries[1]` |
| Saving throws and DCs | 0 / 0 | 16 / 16 | [rewards.json](../../../src/main/resources/data/rewards.json) Blessing of the Frostmaiden IDRotF: `$.reward[16].entries[0]` |
| Costs, charges, uses and recharge | 26 / 26 | 0 / 0 | [rewards.json](../../../src/main/resources/data/rewards.json) Athreos's Devotee MOT: `$.reward[2].additionalSpells[0].innate._.daily` |
| Spellcasting and spell availability | 89 / 89 | 0 / 0 | [rewards.json](../../../src/main/resources/data/rewards.json) Arcane Study Charm XDMG: `$.reward[1].additionalSpells` |
| Choices and alternatives | 2 / 2 | 1 / 1 | [rewards.json](../../../src/main/resources/data/rewards.json) Charm of Exorcism RHW: `$.reward[91].additionalSpells[0].ability.choose` |
| Rest-based recovery | 1 / 1 | 0 / 0 | [rewards.json](../../../src/main/resources/data/rewards.json) Boon of Dimensional Travel DMG: `$.reward[30].additionalSpells[0].innate._.rest` |
| Economy, quantities and roll tables | 0 / 0 | 10 / 11 | [rewards.json](../../../src/main/resources/data/rewards.json) Blessing of the Solipsistic Mind PaBTSO: `$.reward[19].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Traps

**37 authored / 37 effective.**

DTEs: [TrapDte](../../../src/main/java/com/thedndpub/data/dte/traphazard/TrapDte.java). Normalized: [TrapDefinition](../../../src/main/java/com/thedndpub/data/dto/traphazard/TrapDefinition.java). Public: [TrapDetailDto](../../../src/main/java/com/thedndpub/data/dto/traphazard/TrapDetailDto.java). Mapping/projection: [TrapMapper.java](../../../src/main/java/com/thedndpub/data/dto/traphazard/TrapMapper.java).

Internal: Rating/timing and distinct trigger/effect/stage/countermeasure containers retained; stage entries retain their DTE structures.

Public: Rating/type and selected timing labels.

Gap/boundary: Stage role explicit; DC-to-check, saving throw branches, reset and damage applications require interpretation.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 26 / 63 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Balance and Ruin BMT: `$.trap[0].entries[4].entries[0]` |
| Attack labels and modifiers | 0 / 0 | 4 / 6 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Bear Trap XGE: `$.trap[1].effect[0]` |
| Saving throws and DCs | 0 / 0 | 37 / 121 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Balance and Ruin BMT: `$.trap[0].entries[2].entries[0]` |
| Duration and concentration | 8 / 8 | 0 / 0 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Collapsing Roof XDMG: `$.trap[3].duration` |
| Action economy and initiative | 4 / 4 | 0 / 0 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Malfunctioning Phase Transference Emitter AZfyT: `$.trap[16].initiative` |
| Environmental/exploration context | 25 / 25 | 0 / 0 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Balance and Ruin BMT: `$.trap[0].rating` |
| Economy, quantities and roll tables | 0 / 0 | 2 / 5 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Morbid Memory VRGR: `$.trap[17].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Hazards

**73 authored / 73 effective.**

DTEs: [HazardDte](../../../src/main/java/com/thedndpub/data/dte/traphazard/HazardDte.java). Normalized: [HazardDefinition](../../../src/main/java/com/thedndpub/data/dto/traphazard/HazardDefinition.java). Public: [HazardDetailDto](../../../src/main/java/com/thedndpub/data/dto/traphazard/HazardDetailDto.java). Mapping/projection: [HazardMapper.java](../../../src/main/java/com/thedndpub/data/dto/traphazard/HazardMapper.java).

Internal: Type/rating metadata plus typed descriptive entries.

Public: Identity/type/rating only.

Gap/boundary: Environmental effects and exceptions remain prose; no generic trigger/effect model supplied.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 25 / 67 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Avalanche IDRotF: `$.hazard[1].entries[2]` |
| Saving throws and DCs | 0 / 0 | 46 / 65 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Avalanche IDRotF: `$.hazard[1].entries[2]` |
| Choices and alternatives | 0 / 0 | 1 / 1 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Russet Mold VGM: `$.hazard[53].entries[0]` |
| Environmental/exploration context | 21 / 21 | 0 / 0 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Arcane Effluence NF: `$.hazard[0].rating` |
| Economy, quantities and roll tables | 0 / 0 | 20 / 42 | [trapshazards.json](../../../src/main/resources/data/trapshazards.json) Arcane Effluence NF: `$.hazard[0].entries[1]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Vehicles

**39 authored / 39 effective.**

DTEs: [VehicleDte](../../../src/main/java/com/thedndpub/data/dte/vehicle/VehicleDte.java). Normalized: [VehicleDefinition](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleDefinition.java), [VehicleMechanics](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleMechanics.java). Public: [VehicleDetailDto](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleDetailDto.java). Mapping/projection: [VehicleDetailDto.java](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleDetailDto.java).

Internal: Base/component stats, capacity, movement modes, thresholds and action/station content retained.

Public: Selected safe stats/capacities/components; top-level AC/HP currently summary-only.

Gap/boundary: Component mode-plus-entry records lack numerical speed leaves. Action-station effects/repair instructions stay prose.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 34 / 75 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].entries[2]` |
| Attack labels and modifiers | 0 / 0 | 34 / 183 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].entries[4].rows[4][1]` |
| Saving throws and DCs | 0 / 0 | 14 / 23 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].entries[0]` |
| Resistance, vulnerability and immunities | 16 / 30 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].immune` |
| Armor Class and modifiers | 4 / 4 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].ac` |
| Hit points, hit dice and thresholds | 8 / 8 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].hp` |
| Movement and speeds | 39 / 58 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].speed` |
| Action economy and initiative | 14 / 24 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Battle Balloon AI: `$.vehicle[4].actionThresholds` |
| Environmental/exploration context | 39 / 39 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].terrain` |
| Vehicle and facility mechanics | 38 / 163 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Apparatus of Kwalish DMG: `$.vehicle[0].capCrew` |
| Economy, quantities and roll tables | 18 / 18 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Bombard AAG: `$.vehicle[5].cost` |
| Ability statistics/changes and senses | 14 / 84 | 0 / 0 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Battle Balloon AI: `$.vehicle[4].str` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Vehicle Upgrades

**31 authored / 31 effective.**

DTEs: [VehicleUpgradeDte](../../../src/main/java/com/thedndpub/data/dte/vehicle/VehicleUpgradeDte.java). Normalized: [VehicleUpgradeDefinition](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleUpgradeDefinition.java). Public: [VehicleUpgradeDetailDto](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleUpgradeDetailDto.java). Mapping/projection: [VehicleUpgradeDetailDto.java](../../../src/main/java/com/thedndpub/data/dto/vehicle/VehicleUpgradeDetailDto.java).

Internal: Ordered authored upgrade categories and typed entries.

Public: Identity/categories only.

Gap/boundary: No authored structured cost/compatibility/benefit schema; shared category codes do not define compatible vehicles.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 9 / 9 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Acidic Bile Sprayer BGDIA: `$.vehicleUpgrade[0].entries[1].entries[0]` |
| Attack labels and modifiers | 0 / 0 | 1 / 3 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Styx Sprayer BGDIA: `$.vehicleUpgrade[27].entries[1].entries[0]` |
| Saving throws and DCs | 0 / 0 | 11 / 12 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Acidic Bile Sprayer BGDIA: `$.vehicleUpgrade[0].entries[1].entries[0]` |
| Costs, charges, uses and recharge | 0 / 0 | 1 / 1 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Acidic Bile Sprayer BGDIA: `$.vehicleUpgrade[0].entries[1].name` |
| Economy, quantities and roll tables | 0 / 0 | 5 / 5 | [vehicles.json](../../../src/main/resources/data/vehicles.json) Concussive Rounds GoS: `$.vehicleUpgrade[6].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Variant Rules

**231 authored / 231 effective.**

DTEs: [VariantRuleDte](../../../src/main/java/com/thedndpub/data/dte/variantrule/VariantRuleDte.java). Normalized: [VariantRuleDefinition](../../../src/main/java/com/thedndpub/data/dto/variantrule/VariantRuleDefinition.java). Public: [VariantRuleDetailDto](../../../src/main/java/com/thedndpub/data/dto/variantrule/VariantRuleDetailDto.java). Mapping/projection: [VariantRuleMapper.java](../../../src/main/java/com/thedndpub/data/dto/variantrule/VariantRuleMapper.java).

Internal: Source classifications, recursive sections and typed tables retained.

Public: Metadata and structural counts/references.

Gap/boundary: Examples and alternative rule instructions are not executable overrides or always-active modifiers.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 12 / 35 | [variantrules.json](../../../src/main/resources/data/variantrules.json) Crashing AAG: `$.variantrule[33].entries[3].rows[0][1]` |
| Attack labels and modifiers | 0 / 0 | 1 / 1 | [variantrules.json](../../../src/main/resources/data/variantrules.json) Ocean Environs GoS: `$.variantrule[163].entries[7].entries[1]` |
| Saving throws and DCs | 0 / 0 | 23 / 62 | [variantrules.json](../../../src/main/resources/data/variantrules.json) Automatic Success DMG: `$.variantrule[17].entries[0]` |
| Choices and alternatives | 0 / 0 | 5 / 8 | [variantrules.json](../../../src/main/resources/data/variantrules.json) Armor Training XPHB: `$.variantrule[13].entries[0]` |
| Economy, quantities and roll tables | 0 / 0 | 49 / 302 | [variantrules.json](../../../src/main/resources/data/variantrules.json) Automatic Success DMG: `$.variantrule[17].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Treasure Tables

**75 authored / 75 effective.**

DTEs: [IndividualTreasureDte](../../../src/main/java/com/thedndpub/data/dte/treasure/IndividualTreasureDte.java), [TreasureHoardDte](../../../src/main/java/com/thedndpub/data/dte/treasure/TreasureHoardDte.java), [DragonTreasureDte](../../../src/main/java/com/thedndpub/data/dte/treasure/DragonTreasureDte.java), [GemTableDte](../../../src/main/java/com/thedndpub/data/dte/treasure/GemTableDte.java), [ArtObjectTableDte](../../../src/main/java/com/thedndpub/data/dte/treasure/ArtObjectTableDte.java), [MagicItemTableDte](../../../src/main/java/com/thedndpub/data/dte/treasure/MagicItemTableDte.java). Normalized: [TreasureTableDefinition](../../../src/main/java/com/thedndpub/data/dto/treasure/TreasureTableDefinition.java), [IndividualTreasureDefinition](../../../src/main/java/com/thedndpub/data/dto/treasure/IndividualTreasureDefinition.java), [TreasureHoardDefinition](../../../src/main/java/com/thedndpub/data/dto/treasure/TreasureHoardDefinition.java), [MagicItemTableDefinition](../../../src/main/java/com/thedndpub/data/dto/treasure/MagicItemTableDefinition.java). Public: [TreasureTableDetailDto](../../../src/main/java/com/thedndpub/data/dto/treasure/TreasureTableDetailDto.java). Mapping/projection: [TreasureTableMapper.java](../../../src/main/java/com/thedndpub/data/dto/treasure/TreasureTableMapper.java).

Internal: Six typed table variants plus auxiliary dragon rows preserve exact ranges/order, amounts, currencies, item selectors and nested results.

Public: Safe metadata, counts and numeric range summaries; complete results/expressions withheld.

Gap/boundary: Not shared EntryDte tables. Formula parsing may retain quantity syntax; no uniform roll system, item grant or filter expansion.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Economy, quantities and roll tables | 20 / 44 | 0 / 0 | [loot.json](../../../src/main/resources/data/loot.json) Challenge 0-4 DMG: `$.individual[0].crMin` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Encounters

**42 authored / 42 effective.**

DTEs: [EncounterDte](../../../src/main/java/com/thedndpub/data/dte/encounter/EncounterDte.java). Normalized: [EncounterDefinition](../../../src/main/java/com/thedndpub/data/dto/encounter/EncounterDefinition.java). Public: [EncounterDetailDto](../../../src/main/java/com/thedndpub/data/dto/encounter/EncounterDetailDto.java). Mapping/projection: [EncounterMapper.java](../../../src/main/java/com/thedndpub/data/dto/encounter/EncounterMapper.java).

Internal: 93 dedicated tables/2,299 ordered results with dice, range, level and attitude flags retained.

Public: Table/range/count/level metadata; result prose withheld.

Gap/boundary: Dice in results do not create structured creature quantities, composition or spawn instructions.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Saving throws and DCs | 0 / 0 | 3 / 3 | [encounters.json](../../../src/main/resources/data/encounters.json) Coastal XGE: `$.encounter[5].tables[2].table[14].result` |
| Economy, quantities and roll tables | 0 / 0 | 40 / 1594 | [encounters.json](../../../src/main/resources/data/encounters.json) Airborne Encounters EFA: `$.encounter[0].tables[0].table[1].result` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Character Creation Options

**44 authored / 44 effective.**

DTEs: [CharacterCreationOptionDte](../../../src/main/java/com/thedndpub/data/dte/charoption/CharacterCreationOptionDte.java). Normalized: [CharacterOptionDefinition](../../../src/main/java/com/thedndpub/data/dto/charoption/CharacterOptionDefinition.java). Public: [CharacterCreationOptionDto](../../../src/main/java/com/thedndpub/data/dto/charoption/CharacterCreationOptionDto.java). Mapping/projection: [CharacterOptionMapper.java](../../../src/main/java/com/thedndpub/data/dto/charoption/CharacterOptionMapper.java).

Internal: Category/prerequisite metadata and full typed entry structures retained.

Public: Metadata/prerequisite groups.

Gap/boundary: Table/entry benefits and gifts remain descriptive; no automatic eligibility/selected benefit.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 1 / 6 | [charcreationoptions.json](../../../src/main/resources/data/charcreationoptions.json) Touch of Death VRGR: `$.charoption[38].entries[3].entries[0].entries[0]` |
| Saving throws and DCs | 0 / 0 | 5 / 5 | [charcreationoptions.json](../../../src/main/resources/data/charcreationoptions.json) Mist Walker VRGR: `$.charoption[20].entries[3].entries[2].entries[0]` |
| Prerequisites and equipment restrictions | 2 / 2 | 0 / 0 | [charcreationoptions.json](../../../src/main/resources/data/charcreationoptions.json) Alagondar Scion IDRotF: `$.charoption[0].prerequisite` |
| Economy, quantities and roll tables | 0 / 0 | 6 / 11 | [charcreationoptions.json](../../../src/main/resources/data/charcreationoptions.json) Echoing Soul VRGR: `$.charoption[7].entries[3].entries[2].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Cults

**30 authored / 30 effective.**

DTEs: [CultDte](../../../src/main/java/com/thedndpub/data/dte/cultboon/CultDte.java). Normalized: [CultDefinition](../../../src/main/java/com/thedndpub/data/dto/cultboon/CultDefinition.java). Public: [CultDto](../../../src/main/java/com/thedndpub/data/dto/cultboon/CultDto.java). Mapping/projection: [CultBoonMapper.java](../../../src/main/java/com/thedndpub/data/dto/cultboon/CultBoonMapper.java).

Internal: Type, goal/cultists/signature-spell wrappers and typed entries internally retained.

Public: Identity/classification/reference metadata.

Gap/boundary: Signature-spell wrapper text is not a typed spell-grant schedule; powers need prose interpretation.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 4 / 4 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Cult of Atropus, the World Born Dead MTF: `$.cult[1].entries[0].entries[0]` |
| Attack labels and modifiers | 0 / 0 | 1 / 2 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Cult of Yeenoghu MPMM: `$.cult[26].entries[1].entries[0].entries[0]` |
| Saving throws and DCs | 0 / 0 | 6 / 6 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Cult of Atropus, the World Born Dead MTF: `$.cult[1].entries[0].entries[0]` |
| Costs, charges, uses and recharge | 0 / 0 | 4 / 4 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Cult of Atropus, the World Born Dead MTF: `$.cult[1].entries[0].name` |
| Choices and alternatives | 0 / 0 | 1 / 1 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Cult of Tharizdun, the Chained God MTF: `$.cult[20].entries[0].entries[0]` |
| Economy, quantities and roll tables | 0 / 0 | 2 / 2 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Cult of Geryon MPMM: `$.cult[10].entries[1].entries[1].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Boons

**12 authored / 12 effective.**

DTEs: [BoonDte](../../../src/main/java/com/thedndpub/data/dte/cultboon/BoonDte.java). Normalized: [BoonDefinition](../../../src/main/java/com/thedndpub/data/dto/cultboon/BoonDefinition.java). Public: [BoonDto](../../../src/main/java/com/thedndpub/data/dto/cultboon/BoonDto.java). Mapping/projection: [CultBoonMapper.java](../../../src/main/java/com/thedndpub/data/dto/cultboon/CultBoonMapper.java).

Internal: Type, ability/signature-spell wrappers and entries retained separately from Rewards.

Public: Identity/classification/reference metadata.

Gap/boundary: abilityEntry is descriptive text, not a typed ability-score modifier.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| Damage expressions and types | 0 / 0 | 1 / 1 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Demonic Boon of Yeenoghu MTF: `$.boon[10].entries[1].entries[0]` |
| Saving throws and DCs | 0 / 0 | 1 / 1 | [cultsboons.json](../../../src/main/resources/data/cultsboons.json) Demonic Boon of Orcus MTF: `$.boon[9].entries[1].entries[0]` |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Deities

**563 authored / 563 effective.**

DTEs: [DeityDte](../../../src/main/java/com/thedndpub/data/dte/deity/DeityDte.java). Normalized: [DeityDefinition](../../../src/main/java/com/thedndpub/data/dto/deity/DeityDefinition.java). Public: [DeityDetailDto](../../../src/main/java/com/thedndpub/data/dto/deity/DeityDetailDto.java). Mapping/projection: [DeityMapper.java](../../../src/main/java/com/thedndpub/data/dto/deity/DeityMapper.java).

Internal: Pantheon-aware identity, alignment/domain and source metadata; lore/piety remains internal.

Public: Existing safe grouping/symbol/name/source metadata.

Gap/boundary: Piety text is not a generalized resource; domains are labels, not subclass relationships.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Name Tables

**10 authored / 10 effective.**

DTEs: [NameTableDte](../../../src/main/java/com/thedndpub/data/dte/nametable/NameTableDte.java). Normalized: [NameTableDefinition](../../../src/main/java/com/thedndpub/data/dto/nametable/NameTableDefinition.java). Public: [NameTableDto](../../../src/main/java/com/thedndpub/data/dto/nametable/NameTableDto.java). Mapping/projection: [NameTableMapper.java](../../../src/main/java/com/thedndpub/data/dto/nametable/NameTableMapper.java).

Internal: Dedicated option/row types preserve 61 tables/2,822 rows and exact terminal-zero ranges.

Public: Metadata/counts only.

Gap/boundary: Rollable selection content without gameplay effects; not a shared EntryDte table.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Adventure Index

**101 authored / 101 effective.**

DTEs: [AdventureDte](../../../src/main/java/com/thedndpub/data/dte/adventure/AdventureDte.java). Normalized: [AdventureDefinition](../../../src/main/java/com/thedndpub/data/dto/adventure/AdventureDefinition.java). Public: [AdventureDto](../../../src/main/java/com/thedndpub/data/dto/adventure/AdventureDto.java). Mapping/projection: [AdventureMapper.java](../../../src/main/java/com/thedndpub/data/dto/adventure/AdventureMapper.java).

Internal: Publication/navigation and authored adventure-level metadata, without chapter bodies.

Public: Metadata index.

Gap/boundary: Level recommendations/headings do not define encounter difficulty or rules mechanics.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Books

**66 authored / 66 effective.**

DTEs: [BookDte](../../../src/main/java/com/thedndpub/data/dte/book/BookDte.java). Normalized: [PublicationDefinition](../../../src/main/java/com/thedndpub/data/dto/book/PublicationDefinition.java). Public: [PublicationDto](../../../src/main/java/com/thedndpub/data/dto/book/PublicationDto.java). Mapping/projection: [PublicationMapper.java](../../../src/main/java/com/thedndpub/data/dto/book/PublicationMapper.java).

Internal: Publication/source/navigation data, without full book bodies.

Public: Existing publication metadata.

Gap/boundary: No rules-body mechanics exist in this source index.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.

### Recipes

**316 authored / 316 effective.**

DTEs: [RecipeDte](../../../src/main/java/com/thedndpub/data/dte/recipe/RecipeDte.java). Normalized: [RecipeDefinition](../../../src/main/java/com/thedndpub/data/dto/recipe/RecipeDefinition.java). Public: [RecipeDetailDto](../../../src/main/java/com/thedndpub/data/dto/recipe/RecipeDetailDto.java). Mapping/projection: [RecipeMapper.java](../../../src/main/java/com/thedndpub/data/dto/recipe/RecipeMapper.java).

Internal: Dedicated culinary ingredient/equipment/instruction/cook-note trees, quantities and servings retained.

Public: Existing metadata-only food/cocktail index.

Gap/boundary: No gameplay crafting domain. Numerical/conditional recipe instructions are negative controls for mechanics detection.

| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |
|---|---:|---:|---|
| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |

Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.
