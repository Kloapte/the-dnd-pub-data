package com.thedndpub.data.dto.rule.mechanics.deserializers;

import com.thedndpub.data.dto.rule.mechanics.*;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.regex.Pattern;

public final class RuleMechanicDeserializer extends ValueDeserializer<RuleMechanic> {
    private static final Pattern COMPONENT_ID = Pattern.compile("^[a-z][a-z0-9]*(?:-[a-z0-9]+)*$");

    @Override
    public RuleMechanic deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonNode node = context.readTree(parser);
        if (!node.isObject()) throw new IllegalArgumentException("Rule mechanic must be an object");
        JsonNode kindNode = node.get("kind");
        JsonNode subtypeNode = node.get("subtype");
        if (kindNode == null || !kindNode.isString() || subtypeNode == null || !subtypeNode.isString()) {
            throw new IllegalArgumentException("Rule mechanic requires string kind and subtype discriminators");
        }
        JsonNode componentIdNode = node.get("componentId");
        if (componentIdNode == null || !componentIdNode.isString()
                || !COMPONENT_ID.matcher(componentIdNode.asString()).matches()) {
            throw new IllegalArgumentException("Rule mechanic requires a valid componentId");
        }
        String kind = kindNode.asString();
        String subtype = subtypeNode.asString();
        Class<? extends RuleMechanic> target = resolveType(kind, subtype, node);
        return context.readTreeAsValue(node, target);
    }

    private static Class<? extends RuleMechanic> resolveType(String kind, String subtype, JsonNode node) {
        return switch (kind) {
            case "D20_TEST" -> switch (subtype) {
                case "TEST" -> RuleD20Test.class;
                case "ABILITY_CHECK" -> RuleAbilityCheck.class;
                case "DIFFICULTY_CLASS" -> RuleDifficultyClassDefinition.class;
                case "ATTACK_ROLL" -> RuleAttackRoll.class;
                case "SAVING_THROW" -> RuleSavingThrow.class;
                case "DEATH_SAVING_THROW" -> RuleDeathSavingThrow.class;
                case "TEST_MODIFIER" -> RuleTestModifier.class;
                default -> throw unknown(kind, subtype);
            };
            case "ABILITY_SCORE_RULE" -> switch (subtype) {
                case "MODIFIER_MAPPING" -> RuleAbilityScoreModifier.class;
                default -> throw unknown(kind, subtype);
            };
            case "PROFICIENCY_RULE" -> switch (subtype) {
                case "ABILITY_CHECK_PROFICIENCY_VARIANT" -> RuleProficiencyAbilityCheckVariant.class;
                case "BONUS_APPLICATION" -> RuleProficiencyBonusApplication.class;
                case "BONUS_PROGRESSION" -> RuleProficiencyBonusProgression.class;
                case "BONUS_SCALING" -> RuleProficiencyBonusScaling.class;
                case "BONUS_LEVEL_BASIS" -> RuleProficiencyBonusLevelBasis.class;
                case "PROFICIENCY_DICE_ALTERNATIVE" -> RuleProficiencyDiceAlternative.class;
                case "SKILL_PROFICIENCY_APPLICATION" -> RuleSkillProficiencyApplication.class;
                case "TOOL_SKILL_SYNERGY" -> RuleToolSkillProficiencySynergy.class;
                default -> throw unknown(kind, subtype);
            };
            case "POSITIONAL_MODIFIER" -> switch (subtype) {
                case "FLANKING" -> RuleFlanking.class;
                default -> throw unknown(kind, subtype);
            };
            case "SKILL_PROCEDURE" -> switch (subtype) {
                case "ABILITY_VARIATION" -> RuleSkillAbilityVariation.class;
                default -> throw unknown(kind, subtype);
            };
            case "MOVEMENT_SPATIAL" -> switch (subtype) {
                case "AREA_OF_EFFECT" -> node.has("shapes")
                        ? RuleAreaGeometryShapeSet.class
                        : RuleAreaGeometryDimensions.class;
                case "COVER" -> RuleCover.class;
                case "OBSCURATION" -> RuleObscuration.class;
                case "CLIMBING", "CRAWLING", "DIFFICULT_TERRAIN", "FALLING", "FLYING", "JUMPING",
                        "SWIMMING", "TELEPORTATION", "BURROWING", "HOVERING", "SPECIAL_SPEEDS",
                        "GRID_MOVEMENT" -> RuleMovement.class;
                default -> throw unknown(kind, subtype);
            };
            case "COMBAT_PROCEDURE" -> switch (subtype) {
                case "UNARMED_STRIKE" -> RuleUnarmedStrike.class;
                case "UNARMED_STRIKE_OPTION" -> RuleUnarmedOption.class;
                default -> throw unknown(kind, subtype);
            };
            case "DAMAGE_HEALTH_STATE" -> switch (subtype) {
                case "DAMAGE", "RESISTANCE", "VULNERABILITY" -> RuleDamage.class;
                case "HIT_POINTS" -> RuleHitPoints.class;
                case "HEALING" -> RuleHealing.class;
                case "DEATH" -> RuleDeath.class;
                default -> throw unknown(kind, subtype);
            };
            case "SPELL_MAGIC" -> switch (subtype) {
                case "CASTING", "SPELL_ATTACK", "RESOURCE", "ATTUNEMENT", "MAGICAL_EFFECT", "POSSESSION",
                        "TRANSFORMATION" -> RuleMagic.class;
                default -> throw unknown(kind, subtype);
            };
            case "DOWNTIME_PROCEDURE" -> switch (subtype) {
                case "ACTIVITY" -> RuleDowntime.class;
                default -> throw unknown(kind, subtype);
            };
            case "SOCIAL_PROCEDURE" -> switch (subtype) {
                case "ATTITUDE", "INFLUENCE", "LOYALTY", "MORALE" -> RuleSocial.class;
                default -> throw unknown(kind, subtype);
            };
            case "REST_PROCEDURE" -> switch (subtype) {
                case "SHORT_REST", "LONG_REST" -> RuleRest.class;
                default -> throw unknown(kind, subtype);
            };
            case "INITIATIVE_PROCEDURE" -> switch (subtype) {
                case "INITIATIVE_SCORE", "SIDE_INITIATIVE" -> RuleInitiative.class;
                default -> throw unknown(kind, subtype);
            };
            case "CARRYING_CAPACITY_RULE" -> switch (subtype) {
                case "SIZE_STRENGTH_CAPACITY", "OPTIONAL_ENCUMBRANCE" -> RuleCarryingCapacity.class;
                default -> throw unknown(kind, subtype);
            };
            case "VEHICLE_PROCEDURE" -> switch (subtype) {
                case "SHIP_MOVEMENT", "SHIP_COMBAT", "CREW", "REPAIR", "CRASHING" -> RuleVehicle.class;
                default -> throw unknown(kind, subtype);
            };
            default -> throw unknown(kind, subtype);
        };
    }

    private static IllegalArgumentException unknown(String kind, String subtype) {
        return new IllegalArgumentException("Unsupported Rule mechanic discriminator: " + kind + "/" + subtype);
    }
}
