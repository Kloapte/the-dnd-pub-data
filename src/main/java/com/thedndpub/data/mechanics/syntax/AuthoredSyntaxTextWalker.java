package com.thedndpub.data.mechanics.syntax;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Generic authored-input adapter. Walk original JSON, never serialized DTE wrapper fields.
 * No catalog annotation index or effective-definition lineage is created here.
 */
public final class AuthoredSyntaxTextWalker {
    private static final Set<String> TRANSFORMS = Set.of(
            "_copy", "_mod", "_versions", "_abstract", "_implementations", "_templates");

    public void walk(JsonNode originalOwner, SyntaxSourceOwner owner, Consumer<SyntaxText> consumer) {
        Objects.requireNonNull(originalOwner, "originalOwner");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(consumer, "consumer");
        visit(originalOwner, owner, owner.jsonPointer(), List.of(), SyntaxOrigin.AUTHORED, consumer);
    }

    private void visit(
            JsonNode node,
            SyntaxSourceOwner owner,
            String pointer,
            List<SyntaxContext> hierarchy,
            SyntaxOrigin origin,
            Consumer<SyntaxText> consumer) {
        if (node.isTextual()) {
            consumer.accept(new SyntaxText(new SyntaxTextLocation(owner, pointer, hierarchy, origin), node.textValue()));
            return;
        }
        if (!node.isContainerNode()) {
            return;
        }
        String type = node.isObject() && node.path("type").isTextual() ? node.get("type").textValue() : null;
        List<SyntaxContext> nested = new ArrayList<>(hierarchy);
        nested.add(new SyntaxContext(pointer, type));
        List<SyntaxContext> childHierarchy = List.copyOf(nested);
        if (node.isArray()) {
            for (int index = 0; index < node.size(); index++) {
                visit(node.get(index), owner, SyntaxPointers.append(pointer, Integer.toString(index)),
                        childHierarchy, origin, consumer);
            }
        } else {
            var fields = node.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                SyntaxOrigin childOrigin = origin;
                if (TRANSFORMS.contains(field.getKey())) {
                    childOrigin = SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION;
                }
                visit(field.getValue(), owner, SyntaxPointers.append(pointer, field.getKey()),
                        childHierarchy, childOrigin, consumer);
            }
        }
    }
}
