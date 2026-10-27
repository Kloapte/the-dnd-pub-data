package com.thedndpub.data;

import com.thedndpub.data.dto.rule.RuleRecord;
import com.thedndpub.data.dto.rule.mechanics.*;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleCoverLevel;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDamageDie;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleMovementProcedure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
class RuleMechanicDtoTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:normalized/core-rules/rules.json")
    private Resource rulesDataset;

    @Test
    void deserializesAndSerializesAllCanonicalRulesWithoutChangingJsonMeaning() throws Exception {
        JsonNode source;
        RuleRecord[] rules;
        byte[] sourceBytes;
        try (InputStream input = rulesDataset.getInputStream()) {
            sourceBytes = input.readAllBytes();
        }
        source = objectMapper.readTree(sourceBytes);
        rules = objectMapper.readValue(sourceBytes, RuleRecord[].class);

        assertEquals(231, rules.length);
        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(rules));
        assertEquals(null, firstDifference("$", source, serialized),
                () -> "Canonical Rules JSON changed during DTO round-trip: "
                        + firstDifference("$", source, serialized));
    }

    @Test
    void schemaEnumsPreserveWireValuesAndRejectUnsupportedValues() throws Exception {
        assertEquals("\"three-quarters\"", objectMapper.writeValueAsString(RuleCoverLevel.THREE_QUARTERS));
        assertEquals(RuleCoverLevel.THREE_QUARTERS,
                objectMapper.readValue("\"three-quarters\"", RuleCoverLevel.class));
        assertEquals("\"fall-damage\"", objectMapper.writeValueAsString(RuleMovementProcedure.FALL_DAMAGE));
        assertEquals(RuleMovementProcedure.FALL_DAMAGE,
                objectMapper.readValue("\"fall-damage\"", RuleMovementProcedure.class));

        // d20 is schema-supported for damage dice even though no canonical Rule currently uses it.
        assertEquals("\"d20\"", objectMapper.writeValueAsString(RuleDamageDie.D20));
        assertEquals(RuleDamageDie.D20, objectMapper.readValue("\"d20\"", RuleDamageDie.class));
        assertThrows(Exception.class, () -> objectMapper.readValue("\"d100\"", RuleDamageDie.class));
        assertThrows(Exception.class, () -> objectMapper.readValue("\"three_quarters\"", RuleCoverLevel.class));
    }

    @Test
    void usesConcreteDtosForEachCanonicalSubtypeGroup() throws Exception {
        RuleRecord[] rules;
        try (InputStream input = rulesDataset.getInputStream()) {
            rules = objectMapper.readValue(input, RuleRecord[].class);
        }

        Set<Class<?>> concreteTypes = Arrays.stream(rules)
                .flatMap(rule -> rule.mechanics().stream())
                .map(Object::getClass)
                .collect(Collectors.toSet());
        assertEquals(34, concreteTypes.size());
        assertInstanceOf(RuleAbilityCheck.class, mechanic(rules, "D20_TEST", "ABILITY_CHECK"));
        assertInstanceOf(RuleTestModifier.class, mechanic(rules, "D20_TEST", "TEST_MODIFIER"));
        assertInstanceOf(RuleProficiencyBonusProgression.class,
                mechanic(rules, "PROFICIENCY_RULE", "BONUS_PROGRESSION"));
        assertInstanceOf(RuleMovement.class, mechanic(rules, "MOVEMENT_SPATIAL", "CLIMBING"));
        assertInstanceOf(RuleDamage.class, mechanic(rules, "DAMAGE_HEALTH_STATE", "RESISTANCE"));
        assertInstanceOf(RuleRest.class, mechanic(rules, "REST_PROCEDURE", "LONG_REST"));
    }

    @Test
    void enforcesRequiredRuleIdentityAndMagicDiscriminators() {
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"name\":\"Missing identity\",\"sourceMetadata\":{\"source\":\"TEST\"}}",
                RuleRecord.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"id\":\"rule:test:missing-source\",\"name\":\"Missing source\"}", RuleRecord.class));
        assertThrows(Exception.class, () -> readMechanic("SPELL_MAGIC", "UNKNOWN"));
        assertThrows(Exception.class, () -> readMechanic(
                "SPELL_MAGIC", "CASTING", "\"unexpectedField\":true"));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"id\":\"rule:test:missing-component\",\"name\":\"Missing component\","
                        + "\"sourceMetadata\":{\"source\":\"TEST\"},\"mechanics\":[{"
                        + "\"kind\":\"SPELL_MAGIC\",\"subtype\":\"CASTING\"}]}", RuleRecord.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"id\":\"rule:test:bad-resource\",\"name\":\"Bad resource\","
                        + "\"sourceMetadata\":{\"source\":\"TEST\"},\"mechanics\":[{"
                        + "\"kind\":\"SPELL_MAGIC\",\"subtype\":\"RESOURCE\","
                        + "\"componentId\":\"resource\",\"resource\":\"mana\"}]}", RuleRecord.class));
        assertThrows(Exception.class, () -> readMechanic("DAMAGE_HEALTH_STATE", "DEATH"));
    }

    @Test
    void dispatchesEverySubtypeDeclaredByTheRuleSchema() throws Exception {
        Object[][] variants = {
                {"D20_TEST", "TEST", RuleD20Test.class},
                {"D20_TEST", "ABILITY_CHECK", RuleAbilityCheck.class},
                {"D20_TEST", "DIFFICULTY_CLASS", RuleDifficultyClassDefinition.class},
                {"D20_TEST", "ATTACK_ROLL", RuleAttackRoll.class},
                {"D20_TEST", "SAVING_THROW", RuleSavingThrow.class},
                {"D20_TEST", "DEATH_SAVING_THROW", RuleDeathSavingThrow.class},
                {"D20_TEST", "TEST_MODIFIER", RuleTestModifier.class},
                {"ABILITY_SCORE_RULE", "MODIFIER_MAPPING", RuleAbilityScoreModifier.class},
                {"PROFICIENCY_RULE", "ABILITY_CHECK_PROFICIENCY_VARIANT", RuleProficiencyAbilityCheckVariant.class},
                {"PROFICIENCY_RULE", "BONUS_APPLICATION", RuleProficiencyBonusApplication.class},
                {"PROFICIENCY_RULE", "BONUS_PROGRESSION", RuleProficiencyBonusProgression.class},
                {"PROFICIENCY_RULE", "BONUS_SCALING", RuleProficiencyBonusScaling.class},
                {"PROFICIENCY_RULE", "BONUS_LEVEL_BASIS", RuleProficiencyBonusLevelBasis.class},
                {"PROFICIENCY_RULE", "PROFICIENCY_DICE_ALTERNATIVE", RuleProficiencyDiceAlternative.class},
                {"PROFICIENCY_RULE", "SKILL_PROFICIENCY_APPLICATION", RuleSkillProficiencyApplication.class},
                {"PROFICIENCY_RULE", "TOOL_SKILL_SYNERGY", RuleToolSkillProficiencySynergy.class},
                {"POSITIONAL_MODIFIER", "FLANKING", RuleFlanking.class},
                {"SKILL_PROCEDURE", "ABILITY_VARIATION", RuleSkillAbilityVariation.class},
                {"MOVEMENT_SPATIAL", "CLIMBING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "CRAWLING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "DIFFICULT_TERRAIN", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "FALLING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "FLYING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "JUMPING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "SWIMMING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "TELEPORTATION", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "BURROWING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "HOVERING", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "SPECIAL_SPEEDS", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "GRID_MOVEMENT", RuleMovement.class},
                {"MOVEMENT_SPATIAL", "COVER", RuleCover.class},
                {"MOVEMENT_SPATIAL", "OBSCURATION", RuleObscuration.class},
                {"MOVEMENT_SPATIAL", "AREA_OF_EFFECT", RuleAreaGeometryDimensions.class},
                {"COMBAT_PROCEDURE", "UNARMED_STRIKE", RuleUnarmedStrike.class},
                {"COMBAT_PROCEDURE", "UNARMED_STRIKE_OPTION", RuleUnarmedOption.class},
                {"DAMAGE_HEALTH_STATE", "DAMAGE", RuleDamage.class},
                {"DAMAGE_HEALTH_STATE", "RESISTANCE", RuleDamage.class},
                {"DAMAGE_HEALTH_STATE", "VULNERABILITY", RuleDamage.class},
                {"DAMAGE_HEALTH_STATE", "HIT_POINTS", RuleHitPoints.class},
                {"DAMAGE_HEALTH_STATE", "HEALING", RuleHealing.class},
                {"DAMAGE_HEALTH_STATE", "DEATH", RuleDeath.class},
                {"SPELL_MAGIC", "CASTING", RuleMagic.class},
                {"SPELL_MAGIC", "SPELL_ATTACK", RuleMagic.class},
                {"SPELL_MAGIC", "RESOURCE", RuleMagic.class},
                {"SPELL_MAGIC", "ATTUNEMENT", RuleMagic.class},
                {"SPELL_MAGIC", "MAGICAL_EFFECT", RuleMagic.class},
                {"SPELL_MAGIC", "POSSESSION", RuleMagic.class},
                {"SPELL_MAGIC", "TRANSFORMATION", RuleMagic.class},
                {"DOWNTIME_PROCEDURE", "ACTIVITY", RuleDowntime.class},
                {"SOCIAL_PROCEDURE", "ATTITUDE", RuleSocial.class},
                {"SOCIAL_PROCEDURE", "INFLUENCE", RuleSocial.class},
                {"SOCIAL_PROCEDURE", "LOYALTY", RuleSocial.class},
                {"SOCIAL_PROCEDURE", "MORALE", RuleSocial.class},
                {"REST_PROCEDURE", "SHORT_REST", RuleRest.class},
                {"REST_PROCEDURE", "LONG_REST", RuleRest.class},
                {"INITIATIVE_PROCEDURE", "INITIATIVE_SCORE", RuleInitiative.class},
                {"INITIATIVE_PROCEDURE", "SIDE_INITIATIVE", RuleInitiative.class},
                {"CARRYING_CAPACITY_RULE", "SIZE_STRENGTH_CAPACITY", RuleCarryingCapacity.class},
                {"CARRYING_CAPACITY_RULE", "OPTIONAL_ENCUMBRANCE", RuleCarryingCapacity.class},
                {"VEHICLE_PROCEDURE", "SHIP_MOVEMENT", RuleVehicle.class},
                {"VEHICLE_PROCEDURE", "SHIP_COMBAT", RuleVehicle.class},
                {"VEHICLE_PROCEDURE", "CREW", RuleVehicle.class},
                {"VEHICLE_PROCEDURE", "REPAIR", RuleVehicle.class},
                {"VEHICLE_PROCEDURE", "CRASHING", RuleVehicle.class}
        };

        for (Object[] variant : variants) {
            String extraProperties = switch ((String) variant[1]) {
                case "AREA_OF_EFFECT" -> "\"shape\":\"cone\"";
                case "DEATH" -> "\"procedure\":\"dying\"";
                default -> "";
            };
            RuleMechanic mechanic = readMechanic((String) variant[0], (String) variant[1], extraProperties);
            assertInstanceOf((Class<?>) variant[2], mechanic,
                    () -> "Incorrect DTO for " + variant[0] + "/" + variant[1]);
        }

        RuleMechanic alternateAreaGeometry = readMechanic(
                "MOVEMENT_SPATIAL", "AREA_OF_EFFECT",
                "\"shapes\":[\"cone\",\"cube\",\"cylinder\",\"emanation\",\"line\",\"sphere\"]");
        assertInstanceOf(RuleAreaGeometryShapeSet.class, alternateAreaGeometry);
    }

    @Test
    void dispatchesEveryDeclaredNestedUnionVariant() throws Exception {
        assertTurnTiming("{\"kind\":\"action\"}", RuleActionTiming.class);
        assertTurnTiming("{\"kind\":\"bonus-action\"}", RuleActionTiming.class);
        assertTurnTiming("{\"kind\":\"free\"}", RuleActionTiming.class);
        assertTurnTiming("{\"kind\":\"reaction\",\"trigger\":{\"kind\":\"SPELL_DEFINED_REACTION\","
                + "\"actor\":\"spell-caster\"}}", RuleReactionTiming.class);
        assertTurnTiming("{\"kind\":\"fixed_gametime\",\"amount\":2,\"unit\":\"hour\"}",
                RuleFixedGameTimeTiming.class);
        assertTurnTiming("{\"kind\":\"varied_gametime\"}", RuleVariedGameTimeTiming.class);
        assertTurnTiming("{\"kind\":\"alternatives\",\"options\":[{\"kind\":\"action\"},"
                + "{\"kind\":\"reaction\",\"trigger\":{\"kind\":\"SPELL_DEFINED_REACTION\","
                + "\"actor\":\"spell-caster\"}}]}", RuleAlternativeTiming.class);

        assertTrigger("SPELL_CASTING_OBSERVED", "observer", "spell-caster", null);
        assertTrigger("SPELL_DEFINED_REACTION", "spell-caster", null, null);
        assertTrigger("MELEE_ATTACK_MADE", "attacker", "creature", null);
        assertTrigger("OPPORTUNITY_ATTACK", "attacker", "marked-target", null);
        assertTrigger("ATTACK_AGAINST_AFFECTED", "attacker", "affected-creature", null);
        assertTrigger("SAVING_THROW_BY_AFFECTED", "affected-creature", "affected-creature", null);
        assertTrigger("CREATURE_LEAVES_REACH", "moving-creature", "opportunity-attacker",
                ",\"movementOrigins\":[\"action\"],\"requiresVisibility\":false");
        assertTrigger("PERCEIVABLE_CIRCUMSTANCE", "observer", null, null);
        assertTrigger("GRAPPLER_RELEASES", "grappler", "grappled-creature", null);
        assertTrigger("HIDDEN_CREATURE_MAKES_SOUND", "hidden-creature", "observer",
                ",\"volume\":\"louder-than-whisper\"");
        assertTrigger("ENEMY_FINDS_HIDDEN_CREATURE", "enemy", "hidden-creature", null);
        assertTrigger("HIDDEN_CREATURE_MAKES_ATTACK_ROLL", "hidden-creature", null, null);
        assertTrigger("HIDDEN_CREATURE_CASTS_VERBAL_SPELL", "hidden-creature", null, null);

        assertQualifyingSpace("{\"kind\":\"CREATURE_PRESENCE\",\"restriction\":\"not-tiny-and-not-ally\"}",
                RuleCreaturePresenceQualifyingSpace.class);
        assertQualifyingSpace("{\"kind\":\"FURNITURE_SIZE\",\"size\":\"same-or-larger-than-creature\"}",
                RuleFurnitureSizeQualifyingSpace.class);
        assertQualifyingSpace("{\"kind\":\"SURFACE_MATERIAL\",\"materials\":[\"ice\",\"rubble\"]}",
                RuleSurfaceMaterialQualifyingSpace.class);
        assertQualifyingSpace("{\"kind\":\"LIQUID_DEPTH\",\"depth\":\"shin-to-waist\"}",
                RuleLiquidDepthQualifyingSpace.class);
        assertQualifyingSpace("{\"kind\":\"NARROW_OPENING\",\"size\":\"one-size-smaller-than-creature\"}",
                RuleNarrowOpeningQualifyingSpace.class);
        assertQualifyingSpace("{\"kind\":\"SLOPE\",\"minimumDegrees\":20}",
                RuleSlopeQualifyingSpace.class);
        assertQualifyingSpace("{\"kind\":\"DM_ADJUDICATED_SIMILAR\"}",
                RuleDmAdjudicatedSimilarQualifyingSpace.class);

        assertInstanceOf(RuleInclusiveChallengeRatingBand.class, objectMapper.readValue(
                "{\"minimumChallengeRatingInclusive\":0,\"maximumChallengeRatingInclusive\":0.125,\"bonus\":2}",
                RuleProficiencyChallengeRatingBand.class));
        assertInstanceOf(RuleExclusiveChallengeRatingBand.class, objectMapper.readValue(
                "{\"minimumChallengeRatingExclusive\":0,\"maximumChallengeRatingInclusive\":0.125,\"bonus\":2}",
                RuleProficiencyChallengeRatingBand.class));

        assertThrows(Exception.class, () -> objectMapper.readValue("{\"kind\":\"reaction\"}", RuleTurnTiming.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"reaction\",\"trigger\":{\"kind\":\"SPELL_CASTING_OBSERVED\","
                        + "\"actor\":\"attacker\",\"target\":\"spell-caster\"}}", RuleTurnTiming.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"SPELL_CASTING_OBSERVED\",\"actor\":\"attacker\",\"target\":\"spell-caster\"}",
                com.thedndpub.data.dto.action.ActionTrigger.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"HIDDEN_CREATURE_MAKES_SOUND\",\"actor\":\"hidden-creature\","
                        + "\"target\":\"observer\"}", com.thedndpub.data.dto.action.ActionTrigger.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"CREATURE_LEAVES_REACH\",\"actor\":\"moving-creature\","
                        + "\"target\":\"opportunity-attacker\",\"movementOrigins\":[]}",
                com.thedndpub.data.dto.action.ActionTrigger.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"SPELL_DEFINED_REACTION\",\"actor\":\"spell-caster\",\"volume\":\"quiet\"}",
                com.thedndpub.data.dto.action.ActionTrigger.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"CREATURE_LEAVES_REACH\",\"actor\":\"moving-creature\","
                        + "\"target\":\"opportunity-attacker\",\"requiresVisibility\":null}",
                com.thedndpub.data.dto.action.ActionTrigger.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"CREATURE_PRESENCE\",\"restriction\":\"other\"}", RuleQualifyingSpace.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"SLOPE\",\"minimumDegrees\":25}", RuleQualifyingSpace.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"kind\":\"action\",\"trigger\":{}}", RuleTurnTiming.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"minimumChallengeRatingInclusive\":0,\"minimumChallengeRatingExclusive\":0,"
                        + "\"maximumChallengeRatingInclusive\":1,\"bonus\":2}",
                RuleProficiencyChallengeRatingBand.class));
        assertThrows(Exception.class, () -> objectMapper.readValue(
                "{\"maximumChallengeRatingInclusive\":1,\"bonus\":2}",
                RuleProficiencyChallengeRatingBand.class));
    }

    private void assertTurnTiming(String json, Class<? extends RuleTurnTiming> expectedType) throws Exception {
        RuleTurnTiming timing = objectMapper.readValue(json, RuleTurnTiming.class);
        assertInstanceOf(expectedType, timing);
        assertEquals(null, firstDifference("$", objectMapper.readTree(json),
                objectMapper.readTree(objectMapper.writeValueAsString(timing))));
    }

    private void assertTrigger(String kind, String actor, String target, String extras) throws Exception {
        String json = "{\"kind\":\"" + kind + "\",\"actor\":\"" + actor + "\""
                + (target == null ? "" : ",\"target\":\"" + target + "\"")
                + (extras == null ? "" : extras) + "}";
        com.thedndpub.data.dto.action.ActionTrigger trigger = objectMapper.readValue(
                json, com.thedndpub.data.dto.action.ActionTrigger.class);
        assertEquals(null, firstDifference("$", objectMapper.readTree(json),
                objectMapper.readTree(objectMapper.writeValueAsString(trigger))));
    }

    private void assertQualifyingSpace(String json, Class<? extends RuleQualifyingSpace> expectedType)
            throws Exception {
        RuleQualifyingSpace space = objectMapper.readValue(json, RuleQualifyingSpace.class);
        assertInstanceOf(expectedType, space);
        assertEquals(null, firstDifference("$", objectMapper.readTree(json),
                objectMapper.readTree(objectMapper.writeValueAsString(space))));
    }

    private static RuleMechanic mechanic(RuleRecord[] rules, String kind, String subtype) {
        return Arrays.stream(rules)
                .flatMap(rule -> rule.mechanics().stream())
                .filter(value -> value.kind().name().equals(kind) && value.subtype().name().equals(subtype))
                .findFirst()
                .orElseThrow();
    }

    private RuleMechanic readMechanic(String kind, String subtype) {
        return readMechanic(kind, subtype, "");
    }

    private RuleMechanic readMechanic(String kind, String subtype, String extraProperties) {
        String json = "{\"id\":\"rule:test:fixture\",\"name\":\"Fixture\","
                + "\"sourceMetadata\":{\"source\":\"TEST\"},\"mechanics\":[{\"kind\":\""
                + kind + "\",\"subtype\":\"" + subtype + "\",\"componentId\":\"test-component\""
                + (extraProperties.isEmpty() ? "" : "," + extraProperties) + "}]}";
        return objectMapper.readValue(json, RuleRecord.class).mechanics().getFirst();
    }

    static String firstDifference(String path, JsonNode expected, JsonNode actual) {
        if (expected.equals(actual)
                || expected.isNumber() && actual.isNumber()
                && expected.decimalValue().compareTo(actual.decimalValue()) == 0) {
            return null;
        }
        if (expected.isArray() && actual.isArray()) {
            if (expected.size() != actual.size()) {
                return path + " array length expected " + expected.size() + " but was " + actual.size();
            }
            for (int index = 0; index < expected.size(); index++) {
                String difference = firstDifference(path + "[" + index + "]", expected.get(index), actual.get(index));
                if (difference != null) {
                    return difference;
                }
            }
            return null;
        }
        if (expected.isObject() && actual.isObject()) {
            for (String name : expected.propertyNames()) {
                if (!actual.has(name)) {
                    return path + "." + name + " missing after round-trip";
                }
                String difference = firstDifference(path + "." + name, expected.get(name), actual.get(name));
                if (difference != null) {
                    return difference;
                }
            }
            for (String name : actual.propertyNames()) {
                if (!expected.has(name)) {
                    return path + "." + name + " was added by round-trip";
                }
            }
            return null;
        } else if (expected.isContainer() || actual.isContainer()) {
            if (!expected.isContainer() || !actual.isContainer()) {
                return path + " expected " + expected + " but was " + actual;
            }
            return path + " has incompatible container node types";
        }
        return path + " expected " + expected + " but was " + actual
                + " (expected node=" + expected.getClass().getSimpleName()
                + ", container=" + expected.isContainer() + ", array=" + expected.isArray()
                + "; actual node=" + actual.getClass().getSimpleName()
                + ", container=" + actual.isContainer() + ", array=" + actual.isArray() + ")";
    }
}
