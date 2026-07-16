package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.EntryDte;

import java.util.ArrayList;
import java.util.List;

public class EntryDteDeserializer extends ValueDeserializer<List<EntryDte>> {
    @Override
    public List<EntryDte> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        List<EntryDte> listDto = new ArrayList<>();

        for (JsonNode item : node) {
            EntryDte dte = new EntryDte();
            if (item.isTextual()) {
                dte.setText(item.asText());
                dte.setType("text");
                listDto.add(dte);
            }
            else if(item.isObject()) {
                try {
                    dte = context.readTreeAsValue(item, EntryDte.class);
                    listDto.add(dte);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw context.weirdStringException(
                            item.toString(),
                            String.class,
                            "Error with EntryDte"
                    );
                }
            }
        }
        if(!listDto.isEmpty()) {
            return listDto;
        }
        return null;
    }
}
