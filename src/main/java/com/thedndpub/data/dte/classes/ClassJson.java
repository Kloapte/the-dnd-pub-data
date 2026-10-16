package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Strict helpers used only at polymorphic JSON boundaries; trees never enter source models. */
final class ClassJson {
    private ClassJson() {
    }

    static void fields(JsonNode node, JsonParser parser, String... names) throws IOException {
        if (!node.isObject()) {
            throw error(parser, "Expected an object");
        }
        Set<String> allowed = Set.of(names);
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!allowed.contains(field)) {
                throw error(parser, "Unsupported class source field: " + field);
            }
        }
    }

    static String text(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isTextual()) {
            throw error(parser, field + " must be text");
        }
        return value.textValue();
    }

    static Integer integer(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        return integer(value, parser);
    }

    static int integer(JsonNode node, JsonParser parser) throws IOException {
        if (!node.isIntegralNumber() || !node.canConvertToInt()) {
            throw error(parser, "Expected a 32-bit integer");
        }
        return node.intValue();
    }

    static Boolean bool(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isBoolean()) {
            throw error(parser, field + " must be boolean");
        }
        return value.booleanValue();
    }

    static <T> T read(JsonNode node, Class<T> type, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (type == String.class && !node.isTextual()) {
            throw error(parser, "Expected text");
        }
        if (type == Integer.class && (!node.isIntegralNumber() || !node.canConvertToInt())) {
            throw error(parser, "Expected a 32-bit integer");
        }
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        try (JsonParser nested = node.traverse(mapper)) {
            return mapper.readerFor(type)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .without(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                    .readValue(nested);
        }
    }

    static <T> List<T> list(JsonNode node, Class<T> type, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isArray()) {
            throw error(parser, "Expected an array of " + type.getSimpleName());
        }
        List<T> values = new ArrayList<>();
        for (JsonNode child : node) {
            if (child.isNull()) {
                throw error(parser, "Null array elements are not supported");
            }
            values.add(read(child, type, parser));
        }
        return List.copyOf(values);
    }

    static <T> Map<String, List<T>> listsByKey(JsonNode node, Class<T> type, JsonParser parser)
            throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isObject()) {
            throw error(parser, "Expected an object of arrays");
        }
        Map<String, List<T>> values = new LinkedHashMap<>();
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            values.put(field.getKey(), list(field.getValue(), type, parser));
        }
        return values;
    }

    static JsonMappingException error(JsonParser parser, String message) {
        return JsonMappingException.from(parser, message);
    }
}
