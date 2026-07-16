package com.thedndpub.data.dte.race.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.ChooseDte;
import com.thedndpub.data.dte.race.ResistDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResistDteDeserializer extends ValueDeserializer<ResistDte> {
    @Override
    public ResistDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        ResistDte dto = new ResistDte();
        if(node != null && node.isArray()) {
            List<String> resistList = new ArrayList<>();
            for (JsonNode item : node) {
                if(item.get("choose") != null) {
                    ChooseDte choose = context.readTreeAsValue(item.get("choose"), ChooseDte.class);
                    dto.setChoose(choose);
                }
                else {
                    resistList.add(item.asText());
                }
            }
            if(!resistList.isEmpty()) {
                dto.setResist(resistList);
            }
            return dto;
        }

        return null;
    }
}
