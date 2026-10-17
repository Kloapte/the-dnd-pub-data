package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public final class VehicleNumberOrTextDeserializer extends JsonDeserializer<VehicleNumberOrTextDte> {
    @Override
    public VehicleNumberOrTextDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isNumeric()) {
            return new VehicleNumberDte(parser.getDecimalValue());
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new VehicleTextDte(parser.getText());
        }
        return (VehicleNumberOrTextDte) context.handleUnexpectedToken(VehicleNumberOrTextDte.class, parser);
    }
}
