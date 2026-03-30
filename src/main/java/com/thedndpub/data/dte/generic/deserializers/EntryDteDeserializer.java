package com.thedndpub.data.dte.generic.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.generic.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntryDteDeserializer extends JsonDeserializer<List<EntryDte>> {
    @Override
    public List<EntryDte> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<EntryDte> listDto = new ArrayList<>();

        for (JsonNode item : node) {
            EntryDte dte = new EntryDte();
            if (item.isTextual()) {
                dte.setText(item.asText());
                dte.setType("text");
                listDto.add(dte);
            }
            else if(item.isObject()) {
                dte = mapper.convertValue(item, EntryDte.class);
                listDto.add(dte);
            }
        }
        if(!listDto.isEmpty()) {
            return listDto;
        }
        return null;
    }
}
