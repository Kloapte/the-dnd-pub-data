package com.thedndpub.data.dte.generic.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.generic.CopyModEntryItemDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModEntriesItemDteDeserializer extends JsonDeserializer<List<CopyModEntryItemDte>> {
    @Override
    public List<CopyModEntryItemDte> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<CopyModEntryItemDte> dteList = new ArrayList<>();

        if (node.isArray()) {
//                dteList = mapper.convertValue(node, new TypeReference<ArrayList<CopyModEntryItemDte>>() {});
            for (JsonNode subNode : node) {
                if (subNode.isTextual()) {
                    CopyModEntryItemDte entry = new CopyModEntryItemDte();
                    entry.setType("text");
                    entry.setName(subNode.asText());
                    dteList.add(entry);
                } else {
                    CopyModEntryItemDte dte = mapper.convertValue(subNode, CopyModEntryItemDte.class);
                    dteList.add(dte);
                }
            }
        } else {
            if(node.isTextual()) {
                CopyModEntryItemDte entry = new CopyModEntryItemDte();
                entry.setType("text");
                entry.setName(node.asText());
                dteList.add(entry);
            }
            else {
                CopyModEntryItemDte dte = mapper.convertValue(node, CopyModEntryItemDte.class);
                dteList.add(dte);
            }
        }

        return dteList;
    }
}
