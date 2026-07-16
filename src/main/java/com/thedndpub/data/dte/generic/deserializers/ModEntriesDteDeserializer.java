package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.CopyModEntryDte;

import java.util.ArrayList;
import java.util.List;

public class ModEntriesDteDeserializer extends ValueDeserializer<List<CopyModEntryDte>> {
    @Override
    public List<CopyModEntryDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        List<CopyModEntryDte> dteList = new ArrayList<>();

        if (node.isArray()) {
            JavaType type = context.getTypeFactory().constructCollectionType(ArrayList.class, CopyModEntryDte.class);
            dteList = context.readTreeAsValue(node, type);
        } else {
            CopyModEntryDte dte = context.readTreeAsValue(node, CopyModEntryDte.class);
            dteList.add(dte);
        }

        return dteList;
    }
}
