package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntryDtoDeserializer extends JsonDeserializer<List<EntryDte>> {
    @Override
    public List<EntryDte> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<EntryDte> listDto = new ArrayList<>();

        for (JsonNode item : node) {
            EntryDte dto = new EntryDte();
            if (item.isTextual()) {
                dto.setText(item.asText());
                listDto.add(dto);
            }
            else if(item.isObject()) {
                dto = mapper.convertValue(item, EntryDte.class);
                listDto.add(dto);
            }
        }
        if(!listDto.isEmpty()) {
            return listDto;
        }
        return null;
    }
}
