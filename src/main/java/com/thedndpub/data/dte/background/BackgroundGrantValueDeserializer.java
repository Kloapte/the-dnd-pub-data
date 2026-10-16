package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class BackgroundGrantValueDeserializer extends StdDeserializer<BackgroundGrantValueDte> {
    public BackgroundGrantValueDeserializer() {
        super(BackgroundGrantValueDte.class);
    }

    @Override
    public BackgroundGrantValueDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isBoolean()) {
            return new BackgroundGrantValueDte(node.booleanValue(), null, null);
        }
        if (node.isIntegralNumber()) {
            return new BackgroundGrantValueDte(null, node.intValue(), null);
        }
        if (!node.isObject()) {
            throw context.weirdStringException(node.toString(), BackgroundGrantValueDte.class,
                    "Expected a boolean, count, or structured choice");
        }
        JsonNode choose = node.has("choose") ? node.get("choose") : node;
        if (node.has("choose") && node.size() != 1) {
            throw context.weirdStringException(node.toString(), BackgroundGrantValueDte.class,
                    "A choose grant cannot contain sibling fields");
        }
        if (choose.isObject()) {
            choose.fieldNames().forEachRemaining(field -> {
                if (!Set.of("from", "count", "category", "fromFilter").contains(field)) {
                    throw new IllegalArgumentException("Unsupported background choice field: " + field);
                }
            });
        }
        JsonNode fromNode = choose.isObject() ? choose.get("from") : null;
        List<String> from = new ArrayList<>();
        if (fromNode != null) {
            if (!fromNode.isArray()) {
                throw context.weirdStringException(fromNode.toString(), BackgroundGrantValueDte.class,
                        "Choice from must be an array");
            }
            fromNode.forEach(value -> {
                if (!value.isTextual()) {
                    throw new IllegalArgumentException("Background choice alternatives must be strings");
                }
                from.add(value.asText());
            });
        }
        JsonNode countNode = choose.isObject() ? choose.get("count") : null;
        if (countNode != null && !countNode.isIntegralNumber()) {
            throw context.weirdStringException(countNode.toString(), BackgroundGrantValueDte.class,
                    "Choice count must be an integer");
        }
        JsonNode categoryNode = node.has("category") ? node : node.get("anyFromCategory");
        List<String> categories = new ArrayList<>();
        if (categoryNode != null && categoryNode.isObject()) {
            JsonNode categoryList = categoryNode.get("category");
            if (categoryList != null && categoryList.isArray()) {
                categoryList.forEach(value -> {
                    if (!value.isTextual()) {
                        throw new IllegalArgumentException("Background feat categories must be strings");
                    }
                    categories.add(value.asText());
                });
            } else {
                throw context.weirdStringException(categoryNode.toString(), BackgroundGrantValueDte.class,
                        "anyFromCategory requires a category string array");
            }
        } else if (categoryNode != null && categoryNode.isTextual()) {
            categories.add(categoryNode.asText());
        } else if (categoryNode != null && categoryNode.isBoolean() && categoryNode.booleanValue()) {
            categories.add("any");
        }
        boolean wrappedChoice = node.has("choose") && node.size() == 1;
        boolean directChoice = node.has("from") && node.size() <= 2
                && (node.has("count") || node.size() == 1)
                && (!node.has("count") || node.get("count").isIntegralNumber());
        boolean categoryChoice = node.has("anyFromCategory")
                || node.has("category") && node.size() <= 2
                && (!node.has("count") || node.get("count").isIntegralNumber());
        boolean filterChoice = choose.isObject() && choose.has("fromFilter")
                && choose.get("fromFilter").isTextual();
        if (!wrappedChoice && !directChoice && !categoryChoice && !filterChoice) {
            throw context.weirdStringException(node.toString(), BackgroundGrantValueDte.class,
                    "Unsupported grant choice structure");
        }
        return new BackgroundGrantValueDte(null,
                countNode != null && countNode.isIntegralNumber() ? countNode.intValue() : null,
                new BackgroundGrantChoiceDte(List.copyOf(from),
                        countNode != null && countNode.isIntegralNumber() ? countNode.intValue() : null,
                        categories,
                        choose.has("fromFilter") ? choose.get("fromFilter").asText() : null));
    }
}
