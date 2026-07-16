package com.thedndpub.data.dte.race.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.race.SpeedDte;

public class SpeedDteDeserializer extends ValueDeserializer<SpeedDte> {
    @Override
    public SpeedDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

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
