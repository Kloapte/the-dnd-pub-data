package com.thedndpub.data.dte.items.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.generic.AbilityDte;
import com.thedndpub.data.dte.generic.ChooseDte;
import com.thedndpub.data.dte.items.PackContentDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackContentDeserializer extends JsonDeserializer<List<PackContentDte>> {
    @Override
    public List<PackContentDte> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            List<PackContentDte> packContent = new ArrayList<>();
            for(JsonNode subNode : node) {
                if(subNode.isTextual()) {
                    PackContentDte dte = new PackContentDte();
                    dte.setItem(subNode.asText());
                    packContent.add(dte);
                }
                else {
                    packContent.add(mapper.convertValue(subNode, PackContentDte.class));
                }
            }
            return packContent;
        }
        else {
            throw new RuntimeException("Not a list");
        }
    }
}