package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.ChooseDte;
import com.thedndpub.data.dte.race.ResistDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResistDtoDeserializer extends JsonDeserializer<ResistDte> {
    @Override
    public ResistDte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);


        ResistDte dto = new ResistDte();
        if(node != null && node.isArray()) {
            List<String> resistList = new ArrayList<>();
            for (JsonNode item : node) {
                if(item.get("choose") != null) {
                    ChooseDte choose = mapper.convertValue(item.get("choose"), ChooseDte.class);
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
