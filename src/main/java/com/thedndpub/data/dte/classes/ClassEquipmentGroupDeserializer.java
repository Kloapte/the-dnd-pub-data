package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassEquipmentGroupDeserializer extends JsonDeserializer<ClassEquipmentGroupDte> {
    @Override
    public ClassEquipmentGroupDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        fields(node, parser, "_", "a", "b", "c", "A", "B", "C");
        return new ClassEquipmentGroupDte(listsByKey(node, ClassEquipmentEntryDte.class, parser));
    }
}
