package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassTableCellDeserializer extends JsonDeserializer<ClassTableCellDte> {
    @Override
    public ClassTableCellDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassTableCellDte(node.textValue(), null, null, null, null, null);
        }
        if (node.isIntegralNumber()) {
            return new ClassTableCellDte(null, integer(node, parser), null, null, null, null);
        }
        String type = text(node, "type", parser);
        if ("dice".equals(type)) {
            fields(node, parser, "type", "toRoll", "rollable");
            return new ClassTableCellDte(null, null, type, null,
                    list(node.get("toRoll"), ClassHitDiceDte.class, parser), bool(node, "rollable", parser));
        }
        if ("bonus".equals(type) || "bonusSpeed".equals(type)) {
            fields(node, parser, "type", "value");
            return new ClassTableCellDte(null, null, type, integer(node, "value", parser), null, null);
        }
        throw error(parser, "Unsupported class table cell type: " + type);
    }
}
