# Mechanics taxonomy

A mechanical concept is broader than a token. Each row identifies the fact actually supported by source syntax and the boundary that remains. Existing typed statistics are not extraction opportunities.

| ID | Concept | Source-field selectors (catalog-specific roles) | Syntax signals | Interpretation boundary |
|---|---|---|---|---|
| C01 | Damage expressions and types | `dmg1`, `dmg2`, `dmgType`, `damageInflict`, `damageTags`, `damageTagsLegendary`, `damageTagsSpell` | @damage, @scaledamage | Expression/classification, not applied damage |
| C02 | Healing | No selected direct field | Reviewed context required | A healing effect requires reviewed context |
| C03 | Temporary hit points | No selected direct field | Reviewed context required | Temporary HP are distinct from healing and base HP |
| C04 | Attack labels and modifiers | `spellAttack`, `bonusWeapon`, `bonusWeaponAttack`, `bonusWeaponDamage`, `bonusSpellAttack`, `critThreshold` | @atk, @atkr, @hit, @d20, @hityourspellattack, @h, @hom | Attack syntax/property, not a complete attack |
| C05 | Saving throws and DCs | `savingThrow`, `savingThrowForced`, `savingThrowForcedLegendary`, `savingThrowForcedSpell`, `save`, `bonusSpellSaveDc`, `bonusSavingThrow`, `bonusSavingThrowConcentration` | @actsave, @dc, @dcyourspellsave, @actsavefail, @actsavesuccess, @actsavesuccessorfail, @actsavefailby | DC tags alone do not distinguish a save from a check |
| C06 | Ability and skill checks | `abilityCheck`, `skill`, `bonusAbilityCheck` | @skillcheck | A skill reference is not a check or proficiency grant |
| C07 | Advantage and disadvantage | No selected direct field | Reviewed context required | Prose candidates; positive, negative and example contexts differ |
| C08 | Condition classification/application | `conditionInflict`, `conditionInflictLegendary`, `conditionInflictSpell` | Reviewed context required | Condition tags are counted as reference leads, not proof of application |
| C09 | Resistance, vulnerability and immunities | `resist`, `vulnerable`, `immune`, `conditionImmune`, `damageResist`, `damageVulnerable`, `damageImmune` | Reviewed context required | Authored defense versus an inferred bestowed defense |
| C10 | Armor Class and modifiers | `ac`, `bonusAc`, `vehAc`, `dexterityMax` | Reviewed context required | Property/stat, not a recalculated character AC |
| C11 | Hit points, hit dice and thresholds | `hp`, `hd`, `vehHp`, `vehDmgThresh`, `dt`, `mt` | Reviewed context required | Hit dice, base/component HP and thresholds are distinct values, not healing/damage application |
| C12 | Movement and speeds | `speed`, `pace`, `modifySpeed`, `vehSpeed` | Reviewed context required | Modes and source flags do not supply missing numeric speeds |
| C13 | Range, reach and area | `range`, `areaTags` | Reviewed context required | Range metadata is distinct from an effect area/target |
| C14 | Duration and concentration | `duration` | Reviewed context required | A definition duration is not automatically every effect duration |
| C15 | Targets and restrictions | `affectsCreatureType` | Reviewed context required | Creature-type classifications only partially describe targeting |
| C16 | Costs, charges, uses and recharge | `charges`, `recharge`, `rechargeAmount`, `consumes`, `reload` | @recharge | Amount, pool, consumption and reset are separate facts |
| C17 | Action economy and initiative | `time`, `legendaryActions`, `legendaryActionsLair`, `initiative`, `actionThresholds` | Reviewed context required | Action bucket identifies owner kind, not arbitrary resource costs |
| C18 | Spellcasting and spell availability | `spellcasting`, `spellcastingAbility`, `additionalSpells`, `attachedSpells`, `preparedSpells`, `casterProgression`, `spellScrollLevel` | Reviewed context required | Spell mentions remain distinct from grants and schedules |
| C19 | Level/slot progression | `scalingLevelDice`, `cantripProgression`, `preparedSpellsProgression`, `spellsKnownProgression`, `classTableGroups`, `subclassTableGroups`, `optionalfeatureProgression`, `featProgression` | @scaledice, @scaledamage | Explicit table axis differs from prose scaling |
| C20 | Proficiency and expertise grants | `skillProficiencies`, `toolProficiencies`, `languageProficiencies`, `weaponProficiencies`, `armorProficiencies`, `savingThrowProficiencies`, `skillToolLanguageProficiencies`, `expertise`, `startingProficiencies`, `proficiency`, `grantsProficiency`, `grantsLanguage` | Reviewed context required | Requirements and grants are separate roles |
| C21 | Prerequisites and equipment restrictions | `prerequisite`, `reqAttune`, `reqAttuneTags`, `requires`, `excludes`, `multiclassing` | Reviewed context required | Explicit predicates without character eligibility evaluation |
| C22 | Choices and alternatives | `choose`, `options`, `from`, `fromItems`, `fromGeneric`, `fromGroup`, `fromMatching` | @filter | Tag filter is selector syntax, never a chosen grant |
| C23 | Summoning and transformations | `summonedBySpell`, `summonedByClass`, `summonedBySpellLevel` | Reviewed context required | Monster provenance does not define a complete summoning effect |
| C24 | Multiattack and action sequences | No selected direct field | Reviewed context required | Sequence graphs require semantic review |
| C25 | Triggers and reactions | `trigger` | @acttrigger, @actresponse | Trap trigger field is role-anchored prose, not a typed event |
| C26 | Ongoing and conditional effects | No selected direct field | Reviewed context required | Markers cannot establish scope, subject or conditional effect |
| C27 | Rest-based recovery | `rest`, `restLong` | Reviewed context required | Structured spell usage bucket differs from a generalized resource reset |
| C28 | Environmental/exploration context | `terrain`, `environment`, `space`, `rating`, `light`, `carryingCapacity`, `containerCapacity` | Reviewed context required | Context, light/capacity and rating are not environmental simulation |
| C29 | Vehicle and facility mechanics | `hull`, `movement`, `weapon`, `control`, `station`, `actionStation`, `capCrew`, `capCargo`, `capPassenger`, `capCreature`, `crew`, `hirelings`, `orders` | Reviewed context required | Component/label metadata differs from execution or upgrades |
| C30 | Economy, quantities and roll tables | `coins`, `crMin`, `crMax`, `value`, `cost` | @dice, @chance | Cost/currency units, quantities, examples and name/recipe data are not automatically effects |
| C31 | Ability statistics/changes and senses | `str`, `dex`, `con`, `int`, `wis`, `cha`, `ability`, `primaryAbility`, `darkvision`, `blindsight`, `senses`, `bonusSenses` | Reviewed context required | Base scores, associated ability, score increase, sense definition and sense grant differ |

## Classification rules

DIRECT values preserve an authored field or typed structural relationship. SYNTAX observations preserve a tag/formula/label without effect interpretation. LOCAL_CONTEXT requires a bounded association and reviewed gold set. PROSE_INFERENCE describes natural-language interpretation. UNSUPPORTED means the fact cannot currently be established.

A damage expression is not an applied effect. A condition/spell/item reference can express removal, immunity, an example, a prerequisite or an option. Reference-only tags are therefore review leads for application/grant categories. Dice and chance tags establish syntax only and also occur in recipe, name and narrative content.

Additional source-grounded categories beyond a combat-only vocabulary are base/component HP and thresholds; source ability/sense declarations; and quantity/table structure. Light, carrying/container capacity and encumbrance belong to exploration/equipment metadata and are already retained in ItemDefinition; no prose parser is proposed.

## Existing mechanics versus document structures

Class abilityDc/abilityAttackMod nodes, choice counts, Spell duration structures, proficiency groups and typed Item bonuses already carry mechanics. Entry list/table/section types carry structure. Their presence alone cannot distinguish rules, examples, lore or food instructions. Tables must retain order and exact range representations even when their content is withheld publicly.

This vocabulary does not define an executable effect language, target defaults, prerequisite evaluation, roll system or event engine.
