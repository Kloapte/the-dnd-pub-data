package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassEntryDeserializer extends JsonDeserializer<ClassEntryDte> {
    @Override
    public ClassEntryDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassTextEntryDte(node.textValue());
        }
        String type = text(node, "type", parser);
        if (type == null) {
            throw error(parser, "Class entry requires a type");
        }
        return switch (type) {
            case "entries", "inset", "quote", "list", "item" -> {
                switch (type) {
                    case "list" -> fields(node, parser, "type", "items", "style", "columns", "page");
                    case "quote" -> fields(node, parser, "type", "entries");
                    case "item" -> fields(node, parser, "type", "name", "entry", "entries");
                    default -> fields(node, parser, "type", "name", "entries", "source", "page");
                }
                yield new ClassBlockEntryDte(type, text(node, "name", parser), text(node, "entry", parser),
                        list(node.get("entries"), ClassEntryDte.class, parser),
                        list(node.get("items"), ClassEntryDte.class, parser), text(node, "style", parser),
                        integer(node, "columns", parser), text(node, "source", parser), integer(node, "page", parser));
            }
            case "refClassFeature", "refSubclassFeature", "refOptionalfeature", "refFeat" -> {
                switch (type) {
                    case "refClassFeature" -> fields(node, parser, "type", "classFeature");
                    case "refSubclassFeature" -> fields(node, parser, "type", "subclassFeature");
                    case "refFeat" -> fields(node, parser, "type", "feat");
                    default -> fields(node, parser, "type", "optionalfeature", "name", "preserve", "data");
                }
                String referenceField = switch (type) {
                    case "refClassFeature" -> "classFeature";
                    case "refSubclassFeature" -> "subclassFeature";
                    case "refFeat" -> "feat";
                    default -> "optionalfeature";
                };
                String uid = text(node, referenceField, parser);
                if (uid == null || uid.isBlank()) {
                    throw error(parser, "Missing " + referenceField + " UID");
                }
                yield new ClassReferenceEntryDte(type, text(node, "classFeature", parser),
                        text(node, "subclassFeature", parser), text(node, "optionalfeature", parser),
                        text(node, "feat", parser), text(node, "name", parser),
                        read(node.get("preserve"), ClassReferencePreserveDte.class, parser),
                        read(node.get("data"), ClassEntryMetadataDte.class, parser));
            }
            case "options" -> {
                fields(node, parser, "type", "count", "style", "entries");
                yield new ClassOptionsEntryDte(integer(node, "count", parser), text(node, "style", parser),
                        list(node.get("entries"), ClassEntryDte.class, parser));
            }
            case "abilityDc", "abilityAttackMod" -> {
                fields(node, parser, "type", "name", "attributes");
                yield new ClassAbilityEntryDte(type, text(node, "name", parser),
                        list(node.get("attributes"), String.class, parser));
            }
            case "statblock" -> {
                fields(node, parser, "type", "tag", "name", "source", "page", "style");
                String tag = text(node, "tag", parser);
                if (!"item".equals(tag) && !"creature".equals(tag)) {
                    throw error(parser, "Unsupported class statblock tag: " + tag);
                }
                yield new ClassStatblockEntryDte(text(node, "tag", parser), text(node, "name", parser),
                        text(node, "source", parser), integer(node, "page", parser), text(node, "style", parser));
            }
            case "table" -> {
                fields(node, parser, "type", "caption", "colLabels", "colStyles", "rows", "page", "footnotes", "data");
                List<List<ClassTableCellDte>> rows = new ArrayList<>();
                JsonNode rowNodes = node.get("rows");
                if (rowNodes == null || !rowNodes.isArray()) {
                    throw error(parser, "Class entry table requires rows");
                }
                for (JsonNode row : rowNodes) {
                    rows.add(list(row, ClassTableCellDte.class, parser));
                }
                yield new ClassTableEntryDte(text(node, "caption", parser),
                        list(node.get("colLabels"), String.class, parser),
                        list(node.get("colStyles"), String.class, parser), rows, integer(node, "page", parser),
                        list(node.get("footnotes"), String.class, parser),
                        read(node.get("data"), ClassEntryMetadataDte.class, parser));
            }
            default -> throw error(parser, "Unsupported class entry type: " + type);
        };
    }
}
