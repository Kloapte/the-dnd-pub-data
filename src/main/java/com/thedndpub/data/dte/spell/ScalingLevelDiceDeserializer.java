package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Normalizes the source's single-object and array scaling representations to a typed list.
 */
public final class ScalingLevelDiceDeserializer extends JsonDeserializer<List<ScalingLevelDiceDte>> {
    @Override
    public List<ScalingLevelDiceDte> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        List<ScalingLevelDiceDte> values = new ArrayList<>();
        if (node.isObject()) {
            values.add(readOne(node, parser));
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                if (!item.isObject()) {
                    throw JsonMappingException.from(parser,
                            "spell.scalingLevelDice array items must be objects; got " + item.getNodeType());
                }
                values.add(readOne(item, parser));
            }
        } else {
            throw JsonMappingException.from(parser,
                    "spell.scalingLevelDice must be an object or array; got " + node.getNodeType());
        }
        return List.copyOf(values);
    }

    private static ScalingLevelDiceDte readOne(JsonNode node, JsonParser parser) throws IOException {
        JsonNode label = node.get("label");
        if (label == null || !label.isTextual()) {
            throw JsonMappingException.from(parser,
                    "spell.scalingLevelDice.label must be a string");
        }
        JsonNode scalingNode = node.get("scaling");
        if (scalingNode == null || !scalingNode.isObject()) {
            throw JsonMappingException.from(parser,
                    "spell.scalingLevelDice.scaling must be an object keyed by level");
        }
        Map<String, String> scaling = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = scalingNode.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            if (!field.getValue().isTextual()) {
                throw JsonMappingException.from(parser,
                        "spell.scalingLevelDice.scaling." + field.getKey() + " must be a string");
            }
            scaling.put(field.getKey(), field.getValue().textValue());
        }
        Iterator<String> names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!name.equals("label") && !name.equals("scaling")) {
                throw JsonMappingException.from(parser,
                        "Unexpected spell.scalingLevelDice property '" + name + "'");
            }
        }
        return new ScalingLevelDiceDte(label.textValue(), Collections.unmodifiableMap(scaling));
    }
}
