package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.CopyModEntryItemDte;

import java.util.ArrayList;
import java.util.List;

public class ModEntriesItemDteDeserializer extends ValueDeserializer<List<CopyModEntryItemDte>> {
    @Override
    public List<CopyModEntryItemDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

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
                    CopyModEntryItemDte dte = context.readTreeAsValue(subNode, CopyModEntryItemDte.class);
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
                CopyModEntryItemDte dte = context.readTreeAsValue(node, CopyModEntryItemDte.class);
                dteList.add(dte);
            }
        }

        return dteList;
    }
}
