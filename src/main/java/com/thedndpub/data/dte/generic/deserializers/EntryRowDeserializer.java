package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public class EntryRowDeserializer extends ValueDeserializer<List<List<String>>> {
    @Override
    public List<List<String>> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isArray()) {
            List<List<String>> rows = new ArrayList<>();
            for(JsonNode rowNode : node) {
                if(rowNode.isArray()) {
                    if(rowNode.isArray()) {
                        List<String> columns = new ArrayList<>();
                        for(JsonNode columnNode : rowNode) {
                            if(columnNode.isTextual()) {
                                columns.add(columnNode.asText());
                            }
                            else {
                                JsonNode rollNode = columnNode.get("roll");
                                String rollValue = "";
                                if(rollNode.hasNonNull("exact")) {
                                    rollValue = rollNode.get("exact").asInt() + " (exact)";
                                }
                                else {
                                    rollValue = rollNode.get("min").asInt() + "/" + rollNode.get("max").asInt() + " (minMax)";
                                }

                                columns.add(rollValue);
                                columns.add(columnNode.get("entry").asText());
                            }
                        }
                        rows.add(columns);
                    }
                    else {
                        throw new RuntimeException("Not multiple columns 2?");
                    }
                }
                else {
                    throw new RuntimeException("Not multiple rows");
                }
            }
            return rows;
        }
        else {
            throw new RuntimeException("Not a list");
        }
    }
}