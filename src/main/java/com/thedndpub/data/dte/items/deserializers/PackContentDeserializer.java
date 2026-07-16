package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.items.PackContentDte;

import java.util.ArrayList;
import java.util.List;

public class PackContentDeserializer extends ValueDeserializer<List<PackContentDte>> {
    @Override
    public List<PackContentDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if (node.isArray()) {
            List<PackContentDte> packContent = new ArrayList<>();
            for(JsonNode subNode : node) {
                if(subNode.isTextual()) {
                    PackContentDte dte = new PackContentDte();
                    dte.setItem(subNode.asText());
                    packContent.add(dte);
                }
                else {
                    packContent.add(context.readTreeAsValue(subNode, PackContentDte.class));
                }
            }
            return packContent;
        }
        else {
            throw new RuntimeException("Not a list");
        }
    }
}