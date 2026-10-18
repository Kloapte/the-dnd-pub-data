package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class ItemRechargeDeserializer extends JsonDeserializer<ItemRechargeDte> {
    @Override
    public ItemRechargeDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() != JsonToken.VALUE_STRING) {
            throw JsonMappingException.from(parser, "Expected Item recharge to be a string or null");
        }
        return new ItemRechargeDte(parser.getText(), true);
    }

    @Override
    public ItemRechargeDte getNullValue(DeserializationContext context) {
        return new ItemRechargeDte(null, true);
    }

    @Override
    public ItemRechargeDte getAbsentValue(DeserializationContext context) {
        return new ItemRechargeDte(null, false);
    }
}
