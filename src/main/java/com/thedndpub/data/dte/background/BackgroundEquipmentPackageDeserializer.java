package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class BackgroundEquipmentPackageDeserializer extends StdDeserializer<BackgroundEquipmentPackageDte> {
    public BackgroundEquipmentPackageDeserializer() {
        super(BackgroundEquipmentPackageDte.class);
    }

    @Override
    public BackgroundEquipmentPackageDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (!node.isObject()) {
            throw context.weirdStringException(node.toString(), BackgroundEquipmentPackageDte.class,
                    "Expected a package with named equipment options");
        }
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        Map<String, List<BackgroundEquipmentEntryDte>> options = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            JsonNode value = field.getValue();
            if (!value.isArray()) {
                throw context.weirdStringException(value.toString(), BackgroundEquipmentPackageDte.class,
                        "Equipment option values must be arrays");
            }
            List<BackgroundEquipmentEntryDte> entries = new ArrayList<>();
            for (JsonNode entry : value) {
                entries.add(mapper.treeToValue(entry, BackgroundEquipmentEntryDte.class));
            }
            options.put(field.getKey(), List.copyOf(entries));
        }
        return new BackgroundEquipmentPackageDte(options);
    }
}
