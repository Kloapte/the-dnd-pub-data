# Step 2 Rule Schema Enum and Const Audit

**Scope:** Read-only comparison of `src/main/resources/schemas/rule/schema.json`, Rule-referenced definitions in `common.schema.json`, and Java DTOs under `src/main/java/com/thedndpub/data/dto/rule/` (including the shared Action DTOs used by Rule fields). Canonical usage was checked against all 231 records in `src/main/resources/normalized/core-rules/rules.json`. No code, tests, schemas, or canonical records were changed.

## Summary

The Rule contract contains 104 `enum` occurrences and 343 `const` occurrences; referenced shared definitions contain 20 and 72 respectively. Repeated occurrences arise from union branches and conditional schemas. The core Rule discriminator path is substantially safe: Jackson subtype annotations cover nested `kind` unions, the Rule mechanic deserializer dispatches on the paired top-level `kind`/`subtype`, and the area geometry union uses property deduction. These cases do not need extra Java enums merely because the wire discriminator is a String.

The main type-safety gap is schema enums carried by plain `String` or `List<String>` fields. Highest-value gaps are ability scores (the same six-value set is already represented by `ActionAbility`), Rule test/modifier options, movement procedure/speed/unit fields, area geometry options, and the recovery/rest/downtime fields. Many are used in the canonical catalog; several rare choices occur only once or not at all.

Most existing Rule enums match their schema values and Jackson wire encodings. Two notable exceptions are: `RuleMechanicSubtype` is a catalog-wide union of family-specific subtype enums (wire-compatible but wider than any one schema property), and `RuleComponentMemberKind` is technically redundant with its Jackson discriminator. No observed enum has a missing schema value in its intended DTO role. There is no Java enum for the canonical Rule ability value set.

## Priority findings

| Priority | Finding | Recommendation | Canonical Rule use |
|---|---|---|---|
| **P1** | `common.$defs.ability` is represented as strings in Rule fields although `ActionAbility` already has the exact six wire values. Affected fields include `RuleAbilityCheck.abilities`, `RuleAbilityScoreModifier.abilities`, `RuleSavingThrowValue.abilities`, `RuleDeprivationSavingThrow.ability`, and initiative/skill pairing ability fields. | Reuse a shared six-ability enum where packages and semantics permit; do not add a second differently serialized ability enum. | Yes: abilities occur in 22 nested array/value locations in the 231 records. |
| **P1** | Common `damageRoll.damageTypes` and `damageRoll.base.die` are `List<String>` / `String` in `RuleDamageRollValue` and `RuleDiceDamageBase`. Damage type values are the established 13-item set; damage dice include `d20`, while `RuleProficiencyDie` correctly stops at `d12`. | Add/reuse enums for damage type and damage die, keeping the proficiency die enum separate unless a shared die domain is deliberately established. | Yes: damage types occur in Rule and common-damage substructures. `die` occurs in both proficiency and damage contexts. |
| **P1** | Several directly exposed behavioral choices remain strings: `RuleTestModifier.test`, `direction`, and `select`; `RuleMovement.procedure`, `speedType`, and `unit`; `RuleVehicle.procedure` and `unit`; plus `RuleCover.level` and `RuleObscuration.level`/`sightEffect`. | Use field-appropriate enums with explicit JSON names, reusing enums only where their permitted sets and meanings match exactly. | Yes. The Rule dataset uses test modifiers, movement procedures, vehicle procedure, and cover/obscuration levels. |
| **P2** | Area-of-effect fields are strings despite finite schema choices: `RuleAreaGeometryDimensions.shape`, `origin`, `originPlacement`, `dimensionParameters`, and `originIncluded`; `RuleAreaGeometryShapeSet.shapes` and `originPlacement`. | Use enums for the finite options. Keep `RuleAreaGeometry`'s existing deduction-based subtype selection; it correctly distinguishes alternative object shapes without adding a wire discriminator. | Yes: area geometry occurs in 8 Rules (13 mechanics); both shape forms are represented. |
| **P2** | Proficiency, social, magic, and downtime finite values are strings: e.g. `RuleProficiencyBonusApplication.appliesTo`, `RuleProficiencyDiceAlternative.appliesTo`/`eligibleCreatureKinds`, `RuleSocial.attitude`, `RuleMagic.resource`, and `RuleDowntime.procedure`/`outcome`/cadence/selection fields. | Add enums for finite fields whose semantics are stable and used; retain the sparse, source-specific choices only where values are truly open under the schema. | Yes, though several individual subtype choices are rare. |
| **P2** | Rest and recovery DTOs have the largest remaining enum-string surface: unit, duration qualifier, activity, limit, interruptions, interruption outcome, interruption kind, recovery kinds, variants, permitted activities, recovery resources, effects, rest types, and rounding fields. | Prioritize frequently reused values and exact shared sets; do not use one broad “Rule value” enum for unrelated fields. | Yes. Rest mechanics occur in 5 Rules; nested recovery values recur across multiple mechanics. |
| **P3** | Numerous singleton string constants (for example `RuleCoverReference.targetType`, `RuleOriginBehavior.kind`, and `RuleMovement.unit` in area geometry) are represented by ordinary strings. Many other `const` fields are numeric/boolean. | Do not create one-value enums by default. If Java callers must be prevented from constructing noncanonical values, use a narrowly scoped constructor invariant or keep schema validation authoritative. | Some occur in the catalog; several are rare. |

## Enum inventory and Java mapping

The table groups repeated/conditional occurrences for a single schema property. The listed JSON values are the complete enum set from the schema. `Used` means the property occurs in at least one of the 231 canonical records; a `No` means it remains a schema-supported but currently unobserved choice or field.

### Rule schema

| Schema path | Java representation | JSON enum values | Catalog use / audit |
|---|---|---|---|
| `properties.ruleType` | `RuleRecord.ruleType: RuleType` | `CORE`, `OPTIONAL`, `VARIANT`, `VARIANT_OPTIONAL` | Used; exact enum match. |
| `properties.composition.properties.kind` | `RuleCompositionGroup.kind: String`; Jackson subtype mapping and constructor validation | `AND`, `OR` | Used; safe discriminator, but field could use existing `RuleCompositionKind` instead of String. |
| `$defs.d20Test.properties.includes.items` | `RuleD20Test.includes: List<String>` | `ABILITY_CHECK`, `ATTACK_ROLL`, `SAVING_THROW` | Used; candidate enum. |
| `$defs.difficultyClassDefinition.properties.targetNumberFor.items` | `RuleDifficultyClassDefinition.targetNumberFor: List<String>` | `ABILITY_CHECK`, `SAVING_THROW` | Used; candidate enum; compatible with but narrower than the preceding set. |
| `$defs.flankingRule.properties.participants.items` | `RuleFlanking.participants: List<String>` | `ATTACKER`, `ALLY` | Used; candidate enum. |
| `$defs.flankingHexSeparation.properties.targetSizeGroup` | `RuleFlankingHexSeparation.targetSizeGroup: String` | `TINY_SMALL_MEDIUM`, `LARGE`, `HUGE`, `GARGANTUAN` | Used; candidate enum. |
| `$defs.flankingHexSeparation.properties.comparison` | `RuleFlankingHexSeparation.comparison: String` | `EXACTLY`, `AT_LEAST` | Used; candidate enum. |
| `$defs.skillAbilityVariation.properties.eligibleRequester` | `RuleSkillAbilityVariation.eligibleRequester: String` | `DM_OR_PLAYER`, `DM` | Present in the catalog; candidate enum. |
| `$defs.skillAbilityPairingExample.properties.context` | `RuleSkillAbilityPairing.context: String` | `SWIMMING_STAMINA`, `DISPLAY_OF_RAW_STRENGTH` | Used by canonical examples; candidate enum. |
| `$defs.proficiencyBonusApplication.properties.appliesTo.items` | `RuleProficiencyBonusApplication.appliesTo: List<String>` | `SKILL_ASSOCIATED_ABILITY_CHECK`, `SAVING_THROW`, `WEAPON_ATTACK`, `TOOL_CHECK` | Used; candidate enum. |
| `$defs.proficiencyDiceAlternative.properties.appliesTo.items` | `RuleProficiencyDiceAlternative.appliesTo: List<String>` | `ABILITY_CHECK`, `ATTACK_ROLL`, `SAVING_THROW` | Used; candidate enum. |
| `$defs.proficiencyDiceAlternative.properties.eligibleCreatureKinds.items` | `RuleProficiencyDiceAlternative.eligibleCreatureKinds: List<String>` | `PLAYER_CHARACTER`, `NPC_WITH_LEVELS` | Used; candidate enum. |
| `$defs.proficiencyDieLevelBand.properties.die` | `RuleProficiencyDieLevelBand.die: RuleProficiencyDie` | `d4`, `d6`, `d8`, `d10`, `d12` | Used; exact values and serialization. `d20` is not allowed here. |
| `$defs.testModifier.properties.test` | `RuleTestModifier.test: String` | `ability-check`, `attack-roll`, `saving-throw`, `initiative`, `d20-test` | Used; candidate enum. |
| `$defs.testModifier.properties.direction` | `RuleTestModifier.direction: String` | `bonus`, `penalty` | Used; candidate enum. |
| `$defs.testModifier.properties.select` | `RuleTestModifier.select: String` | `higher`, `lower` | Used; candidate enum. |
| `$defs.movement.properties.subtype` | top-level Rule DTO `subtype: RuleMechanicSubtype` | `CLIMBING`, `CRAWLING`, `DIFFICULT_TERRAIN`, `FALLING`, `FLYING`, `JUMPING`, `SWIMMING`, `TELEPORTATION`, `BURROWING`, `HOVERING`, `SPECIAL_SPEEDS`, `GRID_MOVEMENT` | Used; wire values match. The Java enum is broader because it combines subtypes from all mechanic families. The deserializer enforces valid `kind`/`subtype` pairs. |
| `$defs.movement.properties.procedure` | `RuleMovement.procedure: String` | `movement-cost`, `distance-limit`, `fall-damage`, `speed-required`, `destination`, `movement-mode`, `speed-selection`, `speed-adjustment`, `fall-trigger`, `fall-prevention`, `jump-distance`, `grid-movement-cost` | Used; candidate enum. |
| `$defs.movement.properties.speedType` | `RuleMovement.speedType: String` | `walking`, `climb`, `fly`, `swim`, `burrow` | Used; candidate enum. |
| `$defs.movement.properties.unit` | `RuleMovement.unit: String` | `feet`, `speed-multiplier` | Used; candidate enum. |
| `$defs.movement.properties.qualifyingSpaces.items.oneOf[2].properties.materials.items` | `RuleSurfaceMaterialQualifyingSpace.materials: List<RuleSurfaceMaterial>` | `heavy-snow`, `ice`, `rubble`, `undergrowth` | Used; exact enum and JSON names. |
| `$defs.movement.allOf[*].then.properties.details.properties.kind` | `RuleMovementDetails.kind()` plus `@JsonTypeInfo`/`@JsonSubTypes` | `FALL_DAMAGE`, `FLYING_FALL_RULE`, `BURROWING_MOVEMENT`, `HOVER_PREVENTION`, `SWIMMING_MOVEMENT`, `SWIM_SPEED_EXCEPTION`, `SPECIAL_SPEED_SELECTION`, `SPECIAL_SPEED_ADJUSTMENT`, `GRID_MOVEMENT_COST`, `DIAGONAL_MOVEMENT_COST`, `CLIMBING_MOVEMENT`, `CLIMB_SPEED_EXCEPTION`, `FLIGHT_FALLING`, `FLY_SPEED_CAPABILITY`, `HIGH_JUMP`, `LONG_JUMP`, `JUMP_PROCEDURE_SELECTION`, `CRAWLING_MOVEMENT` | Used where present; safe nested Jackson discriminator. No separate enum needed solely for the `kind` string. |
| `$defs.areaGeometry.oneOf[0].properties.shape`; `$defs.areaGeometry.oneOf[1].properties.shapes.items` | `RuleAreaGeometryDimensions.shape: String`; `RuleAreaGeometryShapeSet.shapes: List<String>` | `cone`, `cube`, `cylinder`, `emanation`, `line`, `sphere` | Used; candidate enum. |
| `$defs.areaGeometry.oneOf[0].properties.origin` | `RuleAreaGeometryDimensions.origin: String` | `source`, `point-in-space`, `creature`, `object`, `shape-defined` | Used; candidate enum. |
| `$defs.areaGeometry.oneOf[0].properties.originPlacement`; `$defs.areaGeometry.oneOf[1].properties.originPlacement` | both area DTOs use `String` | `POINT_AND_DIRECTION`, `ANY_CUBE_FACE`, `CENTER_OF_TOP_OR_BOTTOM`, `CREATURE_OR_OBJECT`, `POINT_IN_SPACE` | Used; candidate enum. |
| `$defs.areaGeometry.oneOf[0].properties.dimensionParameters.items` | `RuleAreaGeometryDimensions.dimensionParameters: List<String>` | `MAXIMUM_LENGTH`, `SIDE_LENGTH`, `BASE_RADIUS`, `HEIGHT`, `EXTENT`, `LENGTH`, `WIDTH`, `RADIUS` | Used; candidate enum. |
| `$defs.areaGeometry.oneOf[0].properties.originIncluded` | `RuleAreaGeometryDimensions.originIncluded: String` | `INCLUDED`, `EXCLUDED_UNLESS_CREATOR_CHOOSES` | Used; candidate enum. |
| `$defs.cover.properties.level` | `RuleCover.level: String` | `half`, `three-quarters`, `total` | Used; candidate enum. |
| `$defs.obscuration.properties.level` | `RuleObscuration.level: String` | `lightly-obscured`, `heavily-obscured` | Used; candidate enum. |
| `$defs.obscuration.properties.sightEffect` | `RuleObscuration.sightEffect: String` | `disadvantage-on-sight-dependent-perception`, `blocks-vision` | Used; candidate enum. |
| `$defs.damage.properties.subtype`; `$defs.damage.allOf[0].if.properties.subtype` | `RuleDamage.subtype: RuleMechanicSubtype`; deserializer pair validation | `DAMAGE`, `RESISTANCE`, `VULNERABILITY` (conditional branch: `RESISTANCE`, `VULNERABILITY`) | Used; safe top-level discriminator. |
| `$defs.damage.properties.damageTypes.items` | `RuleDamage.damageTypes: List<String>` | `acid`, `bludgeoning`, `cold`, `fire`, `force`, `lightning`, `necrotic`, `piercing`, `poison`, `psychic`, `radiant`, `slashing`, `thunder` | Used; candidate enum. |
| `$defs.damage.properties.resolutionModifier.properties.operation` | `RuleDamageResolutionModifier.operation: String` | `HALVE`, `DOUBLE` | Used; candidate enum. |
| `$defs.damage.properties.resolutionModifier.properties.multiplier` | `RuleDamageResolutionModifier.multiplier: Double` | `0.5`, `2` | Used; numeric enum. Keep numeric wire representation; a numeric-valued enum is possible but should be justified against the simpler operation enum and cross-field consistency. |
| `$defs.damage.properties.resolutionModifier.properties.rounding` | `RuleDamageResolutionModifier.rounding: String` | `DOWN` | Used; singleton const-like choice; a separate enum is low value. |
| `$defs.hitPoints.properties.rule` | `RuleHitPoints.rule: String` | `temporary-hit-points`, `maximum`, `current`, `zero-hit-points` | Used; candidate enum. |
| `$defs.hitPoints.properties.replacementOrder` | `RuleHitPoints.replacementOrder: String` | `choose-source`, `newest-first` | Used; candidate enum. |
| `$defs.healing.properties.method` | `RuleHealing.method: String` | `hit-dice`, `rest`, `healing-effect`, `stabilization` | Used; candidate enum. |
| `$defs.healing.properties.unit` | `RuleHealing.unit: String` | `hit-points`, `hit-dice` | Used; candidate enum. |
| `$defs.deathRule.properties.procedure` | `RuleDeath.procedure: RuleDeathProcedure` | `instant-death`, `massive-damage`, `dying`, `stabilized` | Used; exact enum and JSON names. |
| `$defs.magic.properties.subtype` | `RuleMagic.subtype: RuleMechanicSubtype`; deserializer pair validation | `CASTING`, `SPELL_ATTACK`, `RESOURCE`, `ATTUNEMENT`, `MAGICAL_EFFECT`, `POSSESSION`, `TRANSFORMATION` | Used; safe top-level discriminator. |
| `$defs.magic.properties.resource` | `RuleMagic.resource: String` | `spell-slot`, `spell-point`, `charge` | Used; candidate enum. |
| `$defs.downtime.properties.procedure` | `RuleDowntime.procedure: String` | `craft`, `buy`, `sell`, `research`, `train`, `work`, `gamble`, `carouse`, `franchise`, `recuperate`, `relaxation` | Used; candidate enum. Repeated schema entries for recuperate/relaxation do not add values. |
| `$defs.downtime.properties.duration.properties.unit` | `RuleDuration.unit: String` | `day`, `week`, `month` | Used; candidate enum. Do not conflate with `RuleGameTimeUnit` (`minute`, `hour`, `day`) without preserving each contract's set. |
| `$defs.downtime.properties.outcome` | `RuleDowntime.outcome: String` | `item-created`, `information-gained`, `resource-gained`, `complication` | Used; candidate enum. |
| `$defs.downtime.properties.minimumLifestyle` | `RuleDowntime.minimumLifestyle: String` | `MODEST` | Present; singleton, enum adds little. |
| `$defs.downtime.properties.sourceDefinedRecoveryCadence` | `RuleDowntime.sourceDefinedRecoveryCadence: String` | `ADVANTAGE_WHILE_ACTIVITY_CONTINUES`, `AT_ACTIVITY_END` | Used; candidate enum. |
| `$defs.downtime.properties.recoverySelection` | `RuleDowntime.recoverySelection: String` | `CHOOSE_ONE_ON_SUCCESS`, `CHOOSE_ONE_AT_ACTIVITY_END` | Used; candidate enum. |
| `$defs.social.properties.subtype` | `RuleSocial.subtype: RuleMechanicSubtype`; deserializer pair validation | `ATTITUDE`, `INFLUENCE`, `LOYALTY`, `MORALE` | Used; safe top-level discriminator. |
| `$defs.social.properties.attitude` | `RuleSocial.attitude: String` | `friendly`, `indifferent`, `hostile` | Used; candidate enum. |
| `$defs.rest.properties.subtype` | `RuleRest.subtype: RuleMechanicSubtype`; deserializer pair validation | `SHORT_REST`, `LONG_REST` | Used; safe top-level discriminator. |
| `$defs.rest.properties.unit` | `RuleRest.unit: String` | `minute`, `minutes`, `hour`, `hours`, `day`, `days`, `week`, `weeks` | Used; candidate enum. |
| `$defs.rest.properties.durationQualifier` | `RuleRest.durationQualifier: String` | `EXACT`, `AT_LEAST` | Used; candidate enum. |
| `$defs.rest.properties.activityLimits.items.properties.activity` | `RuleRestActivityLimit.activity: String` | `SLEEP`, `LIGHT_ACTIVITY` | Used; candidate enum. |
| `$defs.rest.properties.activityLimits.items.properties.unit` | `RuleRestActivityLimit.unit: String` | `hour`, `hours` | Used; candidate enum. |
| `$defs.rest.properties.activityLimits.items.properties.limit` | `RuleRestActivityLimit.limit: String` | `MINIMUM`, `MAXIMUM` | Used; candidate enum. |
| `$defs.rest.properties.interruptions.items` | `RuleRest.interruptions: List<String>` | `INITIATIVE`, `NON_CANTRIP_SPELL`, `DAMAGE`, `PHYSICAL_EXERTION` | Used; candidate enum. |
| `$defs.rest.properties.interruptionOutcome` | `RuleRest.interruptionOutcome: String` | `NO_BENEFITS`, `RESUME_WITH_ADDED_DURATION` | Used; candidate enum. |
| `$defs.rest.properties.addedDurationPerInterruption.properties.unit` | `RuleRest` nested duration unit: String | `hour`, `hours` | Used; candidate enum. |
| `$defs.rest.properties.recoveryLimitations.items.properties.appliesWhen` | `RuleRestRecoveryLimitation.appliesWhen: String` | `SLEEPING_IN_MEDIUM_OR_HEAVY_ARMOR` | Used; singleton condition, keep String unless runtime construction must enforce the literal. |
| `$defs.rest.properties.interruption` | `RuleRest.interruption: String` | `combat`, `strenuous-activity`, `none` | Used; candidate enum. |
| `$defs.rest.properties.recovery.items` | `RuleRest.recovery: List<String>` | `hit-points`, `hit-dice`, `class-feature`, `spell-slots` | Used; candidate enum. |
| `$defs.rest.properties.variant` | `RuleRest.variant: String` | `EPIC_HEROISM`, `GRITTY_REALISM`, `HEALING_SURGE`, `SLOW_NATURAL_HEALING` | Used; candidate enum. |
| `$defs.rest.properties.permittedActivities.items` | `RuleRest.permittedActivities: List<String>` | `READING`, `TALKING`, `EATING`, `STANDING_WATCH`, `LIGHT_ACTIVITY` | Used; candidate enum. |
| `$defs.recoveryOutcome.oneOf[0].properties.resource` | `RuleRestoreAll.resource: String` | `HIT_POINTS`, `HIT_DICE`, `REDUCED_HIT_POINT_MAXIMUM`, `REDUCED_ABILITY_SCORES` | Used; candidate enum. |
| `$defs.recoveryOutcome.oneOf[3].properties.effect` | `RuleEndEffect.effect: String` | `PREVENTS_HIT_POINT_RECOVERY`, `DISEASE_OR_POISON` | Used; candidate enum. |
| `$defs.recoveryOutcome.oneOf[5].properties.resource` | `RuleResetLimitedUse.resource: String` | `HIT_POINTS`, `SPELL_SLOTS` | Used; narrower candidate enum; don't merge with the preceding resource field unless preserving variant-specific accepted values. |
| `$defs.recoveryOutcome.oneOf[5,8,9].properties.restType` | `RuleNoRestRecovery`, `RuleRechargeAsDefinedByFeature`, `RuleResetLimitedUse`.restType: String | `SHORT_REST`, `LONG_REST` | Used; a shared `RuleRestSubtype` enum is appropriate if all three fields share the exact set. |
| `$defs.recoveryOutcome.oneOf[1].properties.resource` | `RuleRestoreFraction.resource: String` | `HIT_POINTS`, `HIT_DICE`, `REDUCED_HIT_POINT_MAXIMUM`, `REDUCED_ABILITY_SCORES` | Used; candidate enum. |
| `$defs.initiative.properties.scoreConstruction` | `RuleInitiative.scoreConstruction: String` | `PASSIVE_DEXTERITY_CHECK`, `DEXTERITY_CHECK_SCORE` | Used; candidate enum. |
| `$defs.initiative.properties.tieResolution` | `RuleInitiative.tieResolution: String` | `REROLL_TIED_SIDES`, `HIGHEST_DEXTERITY_THEN_ROLL` | Used; candidate enum. |
| `$defs.carryingCapacity.properties.sizeMultipliers.items.properties.sizes.items` | `RuleSizeCapacityMultiplier.sizes: List<String>` | `TINY`, `SMALL`, `MEDIUM`, `LARGE`, `HUGE`, `GARGANTUAN` | Used; candidate enum. |
| `$defs.carryingCapacity.properties.encumbranceThresholds.items.properties.state` | `RuleEncumbranceThreshold.state: String` | `ENCUMBERED`, `HEAVILY_ENCUMBERED` | Used; candidate enum. |
| `$defs.carryingCapacity.properties.encumbranceThresholds.items.properties.{abilityCheckDisadvantage,attackDisadvantage,savingThrowDisadvantage}.items` | same-named `RuleEncumbranceThreshold` lists: `List<String>` | `strength`, `dexterity`, `constitution` | Used; reuse the six-value ability enum, with schema validation retaining the narrower three-value restriction. |
| `$defs.carryingCapacity.properties.heavyEncumbranceMaximum` | `RuleCarryingCapacity.heavyEncumbranceMaximum: String` | `STANDARD_CARRYING_CAPACITY` | Used; singleton const-like value, no one-value enum recommended. |
| `$defs.vehicle.properties.subtype` | `RuleVehicle.subtype: RuleMechanicSubtype`; deserializer pair validation | `SHIP_MOVEMENT`, `SHIP_COMBAT`, `CREW`, `REPAIR`, `CRASHING` | Used; safe top-level discriminator. |
| `$defs.vehicle.properties.procedure` | `RuleVehicle.procedure: String` | `initiative`, `movement`, `attack`, `damage`, `repair`, `crew-role` | Used; candidate enum. |
| `$defs.vehicle.properties.unit` | `RuleVehicle.unit: String` | `feet`, `miles`, `nautical-miles` | Used; candidate enum. |
| `$defs.unarmedOption.properties.option` | `RuleUnarmedOption.option: String` | `GRAPPLE`, `SHOVE` | Used; candidate enum. |
| `$defs.surfaceMovement.properties.kind` | `RuleSurfaceMovement.kind: String`; Jackson `RuleMovementDetails` subtype mapping | `CLIMBING_MOVEMENT`, `SWIMMING_MOVEMENT` | Used; safely validated discriminator. |
| `$defs.surfaceMovement.properties.mode` | `RuleSurfaceMovement.mode: String` | `climbing`, `swimming` | Used; candidate enum. |
| `$defs.surfaceMovement.properties.waivedWhenUsingSpeedType` | `RuleSurfaceMovement.waivedWhenUsingSpeedType: String` | `climb`, `swim` | Used; candidate enum. |
| `$defs.surfaceMovement.properties.optionalCheck.properties.trigger` | `RuleSurfaceMovementCheck.trigger: String` | `SLIPPERY_OR_FEW_HANDHOLDS`, `ROUGH_WATER_ANY_DISTANCE` | Used; candidate enum. |
| `$defs.flyingFallRule.properties.ruleStatus` | `RuleFlyingFallRule.ruleStatus: String` | `CORE`, `OPTIONAL` | Used; candidate enum (can reuse `RuleType` only if the enum is intentionally subset-validated). |
| `$defs.flyingFallRule.properties.fallingTriggers.items`; `$defs.flightFalling.properties.fallingTriggers.items` | `RuleFlyingFallRule` / `RuleFlightFalling`: `List<String>` | `PRONE`, `INCAPACITATED`, `FLY_SPEED_ZERO`, `OTHERWISE_UNABLE_TO_MOVE` (flight variant allows the first three) | Used; shared enum candidate with a narrower branch constraint. |
| `$defs.flyingFallRule.properties.prevention` | `RuleFlyingFallRule.prevention: String` | `HOVER`, `HOVER_OR_MAGIC_HOLD_ALOFT` | Used; candidate enum. |
| `$defs.burrowingMovement.properties.permittedMaterials.items` | `RuleBurrowingMovement.permittedMaterials: List<String>` | `sand`, `earth`, `mud`, `ice` | Used; candidate enum. |

### Rule-referenced common schema definitions

| Common schema path | Java representation | Permitted JSON values | Catalog use / audit |
|---|---|---|---|
| `$defs.reference.properties.targetType` | `RuleReference.targetType: RuleReferenceType` | `action`, `background`, `class`, `class-feature`, `condition`, `creature`, `disease`, `feat`, `item`, `language`, `object`, `optional-feature`, `rule`, `sense`, `skill`, `source`, `spell`, `status`, `subclass`, `subclass-feature`, `vehicle`, `race` | Used; enum values and `@JsonProperty` spellings match exactly. |
| `$defs.reference.properties.operation` | `RuleReference.operation: RuleReferenceOperation` | `END_STATUS`, `END_CONDITION` | Used; exact enum match. |
| `$defs.ability` | Several Rule ability fields are strings; `ActionAbility` already matches | `strength`, `dexterity`, `constitution`, `intelligence`, `wisdom`, `charisma` | Used; highest-priority reusable enum gap. |
| `$defs.difficultyClass` | `ActionDcValue` (used in `RuleAbilityCheck` / `RuleSavingThrowValue`) | `FIXED`, `EFFECT_DEFINED`, `ABILITY_PLUS_PROFICIENCY`, `FIXED_PLUS_SPELL_LEVEL`, `GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE` variants | Used; typed polymorphic classes preserve the five-way union. |
| `$defs.d20RollModifier` | `ActionD20RollModifier` | `roll`: `attack`, `ability-check`, `saving-throw`, `d20-test`; `direction`: `advantage`, `disadvantage`, `bonus`, `penalty`; `select`: `higher`, `lower` | Used; `roll` and `direction` are enums; `select` remains String and is an enum gap when present. |
| `$defs.attackRoll` | `ActionAttackRoll` | `forms`: `melee`, `ranged`, `spell`, `weapon`, `unarmed`; `target`: `armor-class`, `creature`, `object` | Used; typed enum-backed fields match the schema. |
| `$defs.savingThrow` | `ActionSavingThrow` / `RuleSavingThrowValue` | `abilitySelection`: `target-chooses`, `rule-specified`, `effect`; ability values are the common six | Used; `RuleSavingThrowValue.abilitySelection` and its ability list remain Strings. |
| `$defs.damageRoll` | `RuleDamageRollValue` and `RuleDamageRollBase` | damage types: `acid`, `bludgeoning`, `cold`, `fire`, `force`, `lightning`, `necrotic`, `piercing`, `poison`, `psychic`, `radiant`, `slashing`, `thunder`; damage die: `d4`, `d6`, `d8`, `d10`, `d12`, `d20` | Used; damage types and die are Strings. `DICE`/`FIXED` are safely selected through `RuleDamageRollBase` Jackson annotations. |
| `$defs.turnTiming` / `$defs.gameTimeTiming` | `RuleTurnTimingKind`, Jackson `@JsonSubTypes`, `RuleGameTimeUnit` | timing: `action`, `bonus-action`, `free`, `reaction`, `fixed_gametime`, `varied_gametime`, `alternatives`; units: `minute`, `hour`, `day` | Used; enum values and JSON names match. Jackson now owns the nested discriminator mapping. |
| `$defs.componentApplicability` | `RuleComponentApplicabilityKind` plus DTO | `ON_HIT`, `ON_FAILED_SAVE`, `ON_CRITICAL_HIT` | Used; exact enum match. |
| `$defs.conditionReference.allOf[1].properties.targetType` | `RuleConditionReference.targetType: RuleReferenceType` | `condition` | Used; underlying enum includes the value, but the DTO does not enforce this narrower const. Add a targeted invariant only if non-schema-validated construction is a supported use case. |
| `$defs.actionTrigger` (transitively referenced by turn timing) | `ActionTrigger` polymorphic DTOs and Action enums | Each `kind` variant fixes actor/target/optional literals; `movementOrigins`: `action`, `bonus-action`, `reaction`, `speed`; `excludedMovementSources`: `disengage`, `teleportation`, `involuntary-movement` | Used; Jackson subtype mapping and typed Action enums enforce the trigger alternatives. No parallel Rule enums recommended. |

The common schema also defines `actor`, `conditionPredicate`, and `operationApplicability`, but those definitions are not referenced directly or transitively by `rule.schema.json`; they are outside this Rule DTO audit. The rule-referenced common enum set above includes only reachable definitions.

## Const audit: discriminator safety versus fixed-value gaps

`const` is not inherently an enum. The Rule schema has 343 constant occurrences: 86 constrain `kind`, 44 constrain `subtype`, and 213 constrain other properties. The referenced common schema contributes another 72 constants. Most `kind`/`subtype` constraints are safely selected by the existing Java polymorphism: `RuleMechanicDeserializer` for top-level Rule mechanics; `RuleMovementDetails`, `RuleRecoveryOutcome`, `RuleFailedSave`, `RuleQualifyingSpace`, `RuleDamageRollBase`, `RuleTurnTiming`, `RuleCompositionMember`, and `RuleAreaGeometry` for nested alternatives. The `RuleComponentApplicabilityKind` and `RuleMechanicKind` / `RuleMechanicSubtype` fields are enums. These are validated discriminators, not missing enum fields.

Other `const` values commonly represent fixed numbers, booleans, literal arrays, canonical references, or one-off labels. DTO fields retain the schema property's JSON type, but Java does not generally enforce the exact constant. Examples to be aware of include:

| Schema path / property family | DTO representation | Values / status |
|---|---|---|
| `$defs.abilityCheck.properties.passiveScoreConstruction.{kind,base,addsCheckBonus,advantageAdjustment,disadvantageAdjustment}` | `RulePassiveScoreConstruction`: String / numeric / Boolean fields | `PASSIVE_SCORE_CONSTRUCTION`, `10`, `true`, `5`, `-5`; DTO field types are appropriate, exact values are not constructor-enforced. |
| `$defs.areaGeometry` fixed `unit`, `coneWidthRule`, origin behavior, cover reference, and per-shape conditional constants | `RuleAreaGeometryDimensions`, `RuleOriginBehavior`, `RuleLineOfEffect`, `RuleCoverReference` | Includes singleton Strings and typed references. These are not multi-valued enums; several remain plain Strings with no Java literal guard. |
| `$defs.jumpProcedure` and `$defs.jumpProcedureSelection` constants | `RuleHighJump`, `RuleLongJump`, `RuleJump*` records | Numeric thresholds/multipliers and fixed labels are represented by numeric/String fields. Keep their JSON scalar types; validate constants through schema or narrow invariants if required. |
| `$defs.recoveryOutcome` fixed `resource`, `basis`, `formula`, `minimumPerDie`, and `rounding` values | `RuleRestore*`, `RuleSpendResourceToRecover`, `RuleEndEffect`, and related recovery DTOs | Includes `HIT_DICE`, `CHARACTER_LEVEL`, `DIE_PLUS_CONSTITUTION_MODIFIER`, `1`, and `DOWN`; these are const constraints, not enum gaps. |
| `$defs.cover`, `$defs.damage`, `$defs.rest`, `$defs.initiative`, `$defs.unarmedSave`, and other mechanic literals | Corresponding Rule records/value records | Boolean and numeric constants remain correctly typed primitives/wrappers; many literal String values lack a Java invariant but should not each become one-value enums. |
| common `$defs.actionTrigger` variants | `ActionTrigger` subtype mapping and fields | Fixed `kind`/actor/target/volume/resolution literals are handled by Jackson subtype selection or enum fields. |
| common `$defs.difficultyClass`, `$defs.componentApplicability`, `$defs.turnTiming`, `$defs.damageRoll` | typed union DTOs | `kind` constants are covered by `@JsonTypeInfo`/`@JsonSubTypes`; retain the discriminated records. |

Additional String-valued consts that are **not** selected by polymorphic dispatch include `RuleAbilityScoreModifier.appliesTo` (`D20_TEST`), `RuleSavingThrowValue.kind` (`SAVING_THROW`), `RuleDamageRollValue.kind` (`DAMAGE_ROLL`), `RuleOriginBehavior.kind` (`EMANATION`), `RuleLineOfEffect.kind` (`STRAIGHT_LINE_FROM_ORIGIN`), and `RuleCoverReference.targetType` (`rule`). They currently deserialize as unconstrained strings. These are single literals, so they do not warrant dedicated one-value enums; retain schema validation or add targeted invariants only if noncanonical input or direct construction must be guarded. Similarly, `RuleConditionReference.targetType` uses the broad `RuleReferenceType` enum but does not itself restrict the value to `condition`.

The DTO layer is not a replacement for JSON Schema validation. The canonical dataset validator catches exact-value constraints. For API/request deserialization or direct Java construction, the non-discriminator constants above are not all enforced by DTO constructors; decide whether that is an intended boundary before adding validation. Do not create enums for booleans/numbers, fixed references, or singleton strings solely to imitate `const`.

## Existing enum conformance

| Java enum | Schema comparison | Result |
|---|---|---|
| `RuleType` | Rule `ruleType` | Exact set and default JSON enum-name encoding. |
| `RuleCompositionKind` | Rule composition `kind` | Exact `AND`/`OR` values; currently not used by `RuleCompositionGroup.kind`, which is a String despite the enum already existing. |
| `RuleMechanicKind` | Top-level mechanics `kind` values | Exact family set, including `VEHICLE_PROCEDURE`. |
| `RuleMechanicSubtype` | Union of top-level mechanic subtype constants/enums | Wire-compatible values; intentionally wider than each individual family. Pair validity remains enforced by the deserializer. |
| `RuleReferenceType` | common reference `targetType` | Exact 22 values, with explicit `@JsonProperty` for hyphenated/lowercase values; includes `race`. |
| `RuleReferenceOperation` | common reference `operation` | Exact two values. |
| `RuleDeathProcedure` | Rule death procedure | Exact set and wire names. |
| `RuleGameTimeUnit` | common game-time timing unit | Exact lowercase values; Java enum identifiers are lowercase, so Jackson's normal enum name serialization matches. |
| `RuleProficiencyDie` | Rule proficiency die level band | Exact `d4` through `d12`; not a mismatch that it omits `d20`, which belongs only to common damage rolls. |
| `RuleQualifyingSpaceKind`, `RuleQualifyingSpaceRestriction`, `RuleQualifyingSpaceSize`, `RuleSurfaceMaterial`, `RuleLiquidDepth` | nested movement predicates | Exact values and explicit wire names where needed. |
| `RuleComponentApplicabilityKind`, `RuleTurnTimingKind` | common applicability/timing | Exact values and wire names. |
| `RuleComponentMemberKind` | Rule composition member | Exact singleton `COMPONENT`; however, Jackson already maps `COMPONENT` and AND/OR through `RuleCompositionMember`, so the new singleton enum is redundant if that discriminator remains validated there. |

No existing enum's serialized values were found to differ from the relevant schema set. The issue is incomplete application of enum types to the DTO fields, rather than incorrect enum constants. `RuleMechanicSubtype`'s broader union is acceptable only because the separate kind/subtype dispatcher rejects invalid pairings; it should not be reused as the type for unrelated enum-valued properties.

## String fields that should remain Strings

These are schema strings without a finite enum and should not be converted merely to reduce String declarations:

- canonical identifiers and reference IDs (`RuleRecord.id`, `RuleReference.id`, component IDs);
- display names and source codes (`name`, `source`, citation metadata);
- reference relationship labels, which are open strings in the schema;
- explicit free-form rule/source values where the schema defines a string pattern or minimum length rather than a closed set;
- descriptions/labels only where actually present in the approved Rule contract.

## Const-path inventory coverage

All `const` occurrences were traversed, including nested union branches and conditionals. To keep this review readable, repeated branch paths are grouped by their containing definition below; exact literal values remain authoritative in the named schema paths.

| Schema definitions containing constants | Constant-constrained property names (branch repetitions grouped) |
|---|---|
| `abilityCheck`, `d20Test`, `difficultyClassDefinition`, `attackRoll`, `savingThrow`, `deathSavingThrow`, `testModifier` | `kind`, `subtype`, fixed numeric/Boolean test values, fixed ability/trigger/result values |
| `abilityScoreModifier`, `abilityCheckProficiencyVariant`, `proficiencyBonusApplication`, `proficiencyBonusProgression`, `proficiencyBonusScaling`, `proficiencyBonusLevelBasis`, `proficiencyDiceAlternative`, `skillProficiencyApplication`, `toolSkillProficiencySynergy` | `kind`, `subtype`, source/applicability/assignment/replacement values, fixed counts and flags |
| `flankingRule`, `skillAbilityVariation`, `skillAbilityPairingExample` | `kind`, `subtype`, role/position/decision/status values, fixed participation and visibility flags |
| `movement`, `cover`, `obscuration`, `areaGeometry`, `burrowingMovement`, `surfaceMovement`, `fallingDamageProcedure`, `flyingFallRule`, `flightFalling`, `hoverPrevention`, `flySpeedCapability`, `jumpProcedure`, `jumpProcedureSelection`, `gridMovementCost`, `crawlingMovement`, `climbSpeedException`, `swimSpeedException`, `specialSpeedSelection`, `specialSpeedAdjustment` | Discriminator kinds/subtypes, fixed units/modes/conditions, shape-specific values, fixed dimensions/DCs/distances/multipliers/flags, reference type/ID literals |
| `damage`, `hitPoints`, `healing`, `deathRule`, `recoveryOutcome`, `rest`, `downtime`, `social`, `initiative`, `carryingCapacity`, `vehicle`, `unarmedOption`, `unarmedSave`, `unarmedStrike` | Discriminator kinds/subtypes, fixed resource/procedure/effect/ability/target values, numeric limits, Boolean flags, fixed reference identifiers |
| `compositionMember`, `conditionReference` | `kind`, target type, component/reference literals |

The common-schema const inventory for Rule-reachable definitions is in `actionTrigger`, `difficultyClass`, `attackRoll`, `savingThrow`, `damageRoll`, `componentApplicability`, `turnTiming`, and `gameTimeTiming`. This includes actor/target constraints and fixed values inside each trigger/union branch; those discriminator branches are handled by the corresponding Action DTOs or the Rule DTO union annotations. `common.$defs.conditionReference` is not referenced by `rule.schema.json`; the Rule schema defines its own condition reference using the shared generic `reference` definition.

The complete Rule schema contains constants across 55 definitions; the common schema has 72 const occurrences in its full file, while only referenced/transitively referenced common definitions are in Rule scope. Repeated conditional branches are counted separately in the schema occurrence totals, even where they constrain the same property/value more than once.

## Audit recommendation

Prioritize enum typing for shared abilities, damage types/dice, and directly exposed Rule option fields that consumers can filter or branch on. Prefer existing Action enums when the permitted JSON value set is identical. Keep Jackson-enforced union discriminators and schema constants as they are unless a real runtime construction boundary needs stricter invariants. This avoids redundant enums while making finite, semantically meaningful values safe to consume in Java.
