package com.thedndpub.data.dte.book;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.Set;

public final class BookHeaderDeserializer extends JsonDeserializer<BookHeaderDte> {
    private static final Set<String> FIELDS = Set.of(
            "header", "depth", "index", "source", "statblock", "tag", "uid");

    @Override
    public BookHeaderDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new TextBookHeaderDte(node.textValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Book content header must be a string or object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String field = names.next();
            if (!FIELDS.contains(field)) {
                throw JsonMappingException.from(parser, "Unexpected book header property '" + field + "'");
            }
        }
        JsonNode header = node.get("header");
        if (header == null || !header.isTextual()) {
            throw JsonMappingException.from(parser, "Structured book header requires a string 'header'");
        }
        return new StructuredBookHeaderDte(
                header.textValue(),
                optionalInteger(node, "depth", parser),
                optionalInteger(node, "index", parser),
                optionalString(node, "source", parser),
                optionalBoolean(node, "statblock", parser),
                optionalString(node, "tag", parser),
                optionalString(node, "uid", parser));
    }

    private static Integer optionalInteger(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null) {
            return null;
        }
        if (!value.isIntegralNumber()) {
            throw JsonMappingException.from(parser, "Book header '" + field + "' must be an integer");
        }
        return value.intValue();
    }

    private static String optionalString(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null) {
            return null;
        }
        if (!value.isTextual()) {
            throw JsonMappingException.from(parser, "Book header '" + field + "' must be a string");
        }
        return value.textValue();
    }

    private static Boolean optionalBoolean(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null) {
            return null;
        }
        if (!value.isBoolean()) {
            throw JsonMappingException.from(parser, "Book header '" + field + "' must be a boolean");
        }
        return value.booleanValue();
    }
}
