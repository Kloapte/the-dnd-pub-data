package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassEquipmentEntryDeserializer extends JsonDeserializer<ClassEquipmentEntryDte> {
    @Override
    public ClassEquipmentEntryDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassEquipmentEntryDte(node.textValue(), null, null, null, null, null, true);
        }
        fields(node, parser, "item", "quantity", "equipmentType", "equipmentTypes", "value", "special");
        return new ClassEquipmentEntryDte(text(node, "item", parser), integer(node, "quantity", parser),
                text(node, "equipmentType", parser), list(node.get("equipmentTypes"), String.class, parser),
                integer(node, "value", parser), text(node, "special", parser), false);
    }
}
