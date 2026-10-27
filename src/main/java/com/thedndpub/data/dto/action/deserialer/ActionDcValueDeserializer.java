package com.thedndpub.data.dto.action.deserialer;

import com.thedndpub.data.dto.action.ActionDcValue;
import com.thedndpub.data.dto.action.ActionDifficultyClass;
import com.thedndpub.data.dto.action.ActionNumericDc;
import com.thedndpub.data.dto.action.ActionStructuredDc;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public final class ActionDcValueDeserializer extends ValueDeserializer<ActionDcValue> {
    @Override
    public ActionDcValue deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            return new ActionNumericDc(parser.getIntValue());
        }
        return new ActionStructuredDc(context.readValue(parser, ActionDifficultyClass.class));
    }
}
