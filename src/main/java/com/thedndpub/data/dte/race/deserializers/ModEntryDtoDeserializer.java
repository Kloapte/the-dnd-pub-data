package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.ModEntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModEntryDtoDeserializer extends JsonDeserializer<List<ModEntryDte>> {
    @Override
    public List<ModEntryDte> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        List<ModEntryDte> listDto = new ArrayList<>();

        if(node.isObject()) {
            listDto.add(mapper.convertValue(node, ModEntryDte.class));
        }
        else if(node.isArray()) {
            for (JsonNode item : node) {
                listDto.add(mapper.convertValue(item, ModEntryDte.class));
            }
        }
        return listDto;
    }
}
