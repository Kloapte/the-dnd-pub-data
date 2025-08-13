package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.CopyModEntryItemDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModEntriesItemEntriesDteDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<String> dteList = new ArrayList<>();

        if (node.isArray()) {
            for(JsonNode subNode : node) {
                if(subNode.isTextual()) {
                    dteList.add(subNode.asText());
                }
                else {
                    if(subNode.get("type") != null && subNode.get("type").asText().equals("list")) {
                        for(JsonNode subItemNode : subNode.get("items")) {
                            for(JsonNode subItemEntryNode : subItemNode.get("entries")) {
                                dteList.add("[@title=" + subItemNode.get("name").asText() + "] " + subItemEntryNode.asText());
                            }
                        }
                    }
                    else  {
                        throw new RuntimeException("Not a list");
                    }
                }
            }
        }
        else {
            throw new RuntimeException("Not a list");
        }

        return dteList;
    }
}
