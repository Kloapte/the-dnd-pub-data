package com.thedndpub.data.dte.race.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.race.ModEntryDte;
import java.util.ArrayList;
import java.util.List;

public class ModEntryDteDeserializer extends ValueDeserializer<List<ModEntryDte>> {
    @Override
    public List<ModEntryDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        List<ModEntryDte> listDto = new ArrayList<>();

        if(node.isObject()) {
            listDto.add(context.readTreeAsValue(node, ModEntryDte.class));
        }
        else if(node.isArray()) {
            for (JsonNode item : node) {
                listDto.add(context.readTreeAsValue(item, ModEntryDte.class));
            }
        }
        return listDto;
    }
}
