package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.ResistDte;
import com.thedndpub.data.dte.race.SpeedDte;

import java.io.IOException;

public class SpeedDtoDeserializer extends JsonDeserializer<SpeedDte> {
    @Override
    public SpeedDte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        SpeedDte dte = new SpeedDte();

        if(node != null && node.isInt()) {
            dte.setWalk(node.asInt());
        }
        else if(node != null && node.isObject()) {
            JsonNode walkNode = node.get("walk");
            if (walkNode != null) {
                dte.setWalk(walkNode.asInt());
            }

            JsonNode flyNode = node.get("fly");
            if (flyNode != null) {
                if (flyNode.isInt()) {
                    dte.setFlySpeed(flyNode.asInt());
                    dte.setFlyAvailable(true);
                } else if (flyNode.isBoolean()) {
                    dte.setFlyAvailable(flyNode.asBoolean());
                }
            }
        }

        return dte;
    }
}
