package com.thedndpub.data.dte.treasure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class MagicItemChooseSerializer extends JsonSerializer<MagicItemChooseDte> {
    @Override
    public void serialize(MagicItemChooseDte value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeStartObject();
        if (value instanceof FromItemsChoiceDte choice) {
            generator.writeObjectField("fromItems", choice.fromItems());
        } else if (value instanceof FromGenericChoiceDte choice) {
            generator.writeObjectField("fromGeneric", choice.fromGeneric());
        } else if (value instanceof FromGroupChoiceDte choice) {
            generator.writeObjectField("fromGroup", choice.fromGroup());
        } else if (value instanceof FromMatchingChoiceDte choice) {
            generator.writeObjectField("fromMatching", choice.fromMatching());
        } else {
            throw new IllegalArgumentException("Unsupported magic-item choice: " + value.getClass());
        }
        generator.writeEndObject();
    }
}
