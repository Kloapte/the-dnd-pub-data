package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

public final class FeatLevelPrerequisiteDeserializer extends StdDeserializer<FeatLevelPrerequisiteDte> {
    public FeatLevelPrerequisiteDeserializer() {
        super(FeatLevelPrerequisiteDte.class);
    }

    @Override
    public FeatLevelPrerequisiteDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new FeatSimpleLevelPrerequisiteDte(node.intValue());
        }
        if (node.isObject() && node.has("level") && node.has("class")) {
            var rootFields = node.fieldNames();
            while (rootFields.hasNext()) {
                String field = rootFields.next();
                if (!field.equals("level") && !field.equals("class")) {
                    throw context.weirdStringException(field, FeatLevelPrerequisiteDte.class,
                            "Unexpected class-qualified level property");
                }
            }
            JsonNode classNode = node.get("class");
            if (!node.get("level").canConvertToInt() || !classNode.isObject()
                    || !classNode.has("name") || !classNode.get("name").isTextual()) {
                throw context.weirdStringException(node.toString(), FeatLevelPrerequisiteDte.class,
                        "Invalid class-qualified level requirement");
            }
            var classFields = classNode.fieldNames();
            while (classFields.hasNext()) {
                String field = classFields.next();
                if (!field.equals("name") && !field.equals("visible")) {
                    throw context.weirdStringException(field, FeatLevelPrerequisiteDte.class,
                            "Unexpected class prerequisite property");
                }
            }
            Boolean visible = classNode.has("visible") && classNode.get("visible").isBoolean()
                    ? classNode.get("visible").booleanValue()
                    : null;
            return new FeatClassLevelPrerequisiteDte(
                    node.get("level").intValue(),
                    new FeatPrerequisiteClassDte(classNode.get("name").textValue(), visible));
        }
        throw context.weirdStringException(node.toString(), FeatLevelPrerequisiteDte.class,
                "Expected an integer level or a class-qualified level object");
    }
}
