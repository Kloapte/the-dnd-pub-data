package com.thedndpub.data.dto.action;

import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionExcludedMovementSource;
import com.thedndpub.data.dto.action.enums.ActionMovementOrigin;
import com.thedndpub.data.dto.action.enums.ActionTriggerKind;
import com.thedndpub.data.dto.action.enums.ActionTriggerTarget;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ActionTriggerDeserializer extends ValueDeserializer<ActionTrigger> {
    @Override
    public ActionTrigger deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonNode node = context.readTree(parser);
        if (!node.isObject()) throw new IllegalArgumentException("Action trigger must be an object");

        ActionTriggerKind kind = readRequired(node, "kind", ActionTriggerKind.class, context);
        Set<String> allowed = allowedFields(kind);
        for (String property : node.propertyNames()) {
            if (!allowed.contains(property)) {
                throw new IllegalArgumentException("Action trigger " + kind + " does not allow " + property);
            }
        }

        ActionActor actor = readRequired(node, "actor", ActionActor.class, context);
        ActionTriggerTarget target = node.has("target")
                ? readRequired(node, "target", ActionTriggerTarget.class, context)
                : null;
        Boolean requiresVisibility = readOptionalBoolean(node, "requiresVisibility");
        Boolean requiresHostility = readOptionalBoolean(node, "requiresHostility");
        List<ActionMovementOrigin> movementOrigins = readOptionalArray(
                node, "movementOrigins", ActionMovementOrigin.class, context);
        List<ActionExcludedMovementSource> excludedMovementSources = readOptionalArray(
                node, "excludedMovementSources", ActionExcludedMovementSource.class, context);
        String resolutionTiming = readOptionalString(node, "resolutionTiming");
        String volume = readOptionalString(node, "volume");

        return new ActionTrigger(kind, actor, target, requiresVisibility, requiresHostility, movementOrigins,
                excludedMovementSources, resolutionTiming, volume);
    }

    private static Set<String> allowedFields(ActionTriggerKind kind) {
        return switch (kind) {
            case SPELL_CASTING_OBSERVED, MELEE_ATTACK_MADE, OPPORTUNITY_ATTACK, ATTACK_AGAINST_AFFECTED,
                    SAVING_THROW_BY_AFFECTED, GRAPPLER_RELEASES, ENEMY_FINDS_HIDDEN_CREATURE ->
                    Set.of("kind", "actor", "target");
            case SPELL_DEFINED_REACTION, PERCEIVABLE_CIRCUMSTANCE, HIDDEN_CREATURE_MAKES_ATTACK_ROLL,
                    HIDDEN_CREATURE_CASTS_VERBAL_SPELL -> Set.of("kind", "actor");
            case CREATURE_LEAVES_REACH -> Set.of("kind", "actor", "target", "requiresVisibility",
                    "requiresHostility", "movementOrigins", "excludedMovementSources", "resolutionTiming");
            case HIDDEN_CREATURE_MAKES_SOUND -> Set.of("kind", "actor", "target", "volume");
        };
    }

    private static <T> T readRequired(JsonNode node, String property, Class<T> type, DeserializationContext context)
            throws JacksonException {
        JsonNode value = node.get(property);
        if (value == null || value.isNull()) throw new IllegalArgumentException("Action trigger requires " + property);
        return context.readTreeAsValue(value, type);
    }

    private static Boolean readOptionalBoolean(JsonNode node, String property) {
        JsonNode value = node.get(property);
        if (value == null) return null;
        if (value.isNull() || !value.isBoolean()) {
            throw new IllegalArgumentException(property + " must be a boolean");
        }
        return value.asBoolean();
    }

    private static String readOptionalString(JsonNode node, String property) {
        JsonNode value = node.get(property);
        if (value == null) return null;
        if (value.isNull() || !value.isString()) throw new IllegalArgumentException(property + " must be a string");
        return value.asString();
    }

    private static <T> List<T> readOptionalArray(JsonNode node, String property, Class<T> type,
            DeserializationContext context) throws JacksonException {
        JsonNode value = node.get(property);
        if (value == null) return List.of();
        if (value.isNull() || !value.isArray() || value.isEmpty()) {
            throw new IllegalArgumentException(property + " must be a nonempty array when present");
        }
        List<T> values = new ArrayList<>();
        for (JsonNode item : value) values.add(context.readTreeAsValue(item, type));
        return values;
    }
}
