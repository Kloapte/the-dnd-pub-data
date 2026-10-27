package com.thedndpub.data.dto.action.serialzer;

import com.thedndpub.data.dto.action.ActionDcValue;
import com.thedndpub.data.dto.action.ActionNumericDc;
import com.thedndpub.data.dto.action.ActionStructuredDc;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public final class ActionDcValueSerializer extends ValueSerializer<ActionDcValue> {
    @Override
    public void serialize(ActionDcValue value, JsonGenerator generator, SerializationContext context)
            throws JacksonException {
        if (value instanceof ActionNumericDc numericDc) {
            generator.writeNumber(numericDc.value());
            return;
        }
        ActionStructuredDc structuredDc = (ActionStructuredDc) value;
        context.writeValue(generator, structuredDc.value());
    }
}
