package com.thedndpub.data.dto.rule.mechanics.deserializers;

import com.thedndpub.data.dto.rule.mechanics.RuleActionTiming;
import com.thedndpub.data.dto.rule.mechanics.RuleAlternativeTiming;
import com.thedndpub.data.dto.rule.mechanics.RuleFixedGameTimeTiming;
import com.thedndpub.data.dto.rule.mechanics.RuleReactionTiming;
import com.thedndpub.data.dto.rule.mechanics.RuleTurnTiming;
import com.thedndpub.data.dto.rule.mechanics.RuleVariedGameTimeTiming;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleGameTimeUnit;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;
import com.thedndpub.data.dto.action.ActionTrigger;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class RuleTurnTimingDeserializer extends ValueDeserializer<RuleTurnTiming> {
    @Override
    public RuleTurnTiming deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        JsonNode node = context.readTree(parser);
        JsonNode kindNode = node.get("kind");
        if (kindNode == null || !kindNode.isString()) {
            throw new IllegalArgumentException("Turn timing requires a string kind discriminator");
        }
        validateFields(node, kindNode.asString());
        RuleTurnTimingKind kind = context.readTreeAsValue(kindNode, RuleTurnTimingKind.class);
        return switch (kind) {
            case ACTION, BONUS_ACTION, FREE -> new RuleActionTiming(kind);
            case REACTION -> new RuleReactionTiming(kind,
                    context.readTreeAsValue(required(node, "trigger"), ActionTrigger.class));
            case FIXED_GAMETIME -> {
                JsonNode amount = required(node, "amount");
                if (!amount.isNumber()) throw new IllegalArgumentException("Fixed game-time amount must be numeric");
                yield new RuleFixedGameTimeTiming(kind, amount.asDouble(),
                        context.readTreeAsValue(required(node, "unit"), RuleGameTimeUnit.class));
            }
            case VARIED_GAMETIME -> new RuleVariedGameTimeTiming(kind);
            case ALTERNATIVES -> new RuleAlternativeTiming(kind, readOptions(node, context));
        };
    }

    private static List<RuleTurnTiming> readOptions(JsonNode node, DeserializationContext context)
            throws JacksonException {
        JsonNode optionsNode = required(node, "options");
        if (!optionsNode.isArray()) throw new IllegalArgumentException("Turn timing options must be an array");
        List<RuleTurnTiming> options = new ArrayList<>();
        for (JsonNode option : optionsNode) {
            options.add(context.readTreeAsValue(option, RuleTurnTiming.class));
        }
        return options;
    }

    private static JsonNode required(JsonNode node, String property) {
        JsonNode value = node.get(property);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("Turn timing requires " + property);
        }
        return value;
    }

    private static void validateFields(JsonNode node, String kind) {
        Set<String> allowed = switch (kind) {
            case "action", "bonus-action", "free", "varied_gametime" -> Set.of("kind");
            case "reaction" -> Set.of("kind", "trigger");
            case "fixed_gametime" -> Set.of("kind", "amount", "unit");
            case "alternatives" -> Set.of("kind", "options");
            default -> throw new IllegalArgumentException("Unsupported turn timing kind: " + kind);
        };
        for (String property : node.propertyNames()) {
            if (!allowed.contains(property)) {
                throw new IllegalArgumentException("Turn timing " + kind + " does not allow " + property);
            }
        }
    }
}
