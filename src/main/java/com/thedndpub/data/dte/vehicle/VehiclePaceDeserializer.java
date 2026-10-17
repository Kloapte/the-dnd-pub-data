package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

public final class VehiclePaceDeserializer extends JsonDeserializer<VehiclePaceDte> {
    @Override
    public VehiclePaceDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new VehicleNumericPaceDte(node.intValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "vehicle pace must be an integer or object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!Set.of("fly", "swim", "walk").contains(name)) {
                throw JsonMappingException.from(parser, "unexpected vehicle pace property: " + name);
            }
        }
        return new VehicleDetailedPaceDte(value(node.get("fly"), parser), value(node.get("swim"), parser),
                value(node.get("walk"), parser));
    }

    private static VehicleNumberOrTextDte value(JsonNode node, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isIntegralNumber()) {
            return new VehicleNumberDte(BigDecimal.valueOf(node.longValue()));
        }
        if (node.isTextual()) {
            return new VehicleTextDte(node.textValue());
        }
        throw JsonMappingException.from(parser, "pace modes must be integers or strings");
    }
}
