package com.thedndpub.data.mechanics.syntax;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Syntax categories only. This vocabulary is independent of the reference resolver. */
public final class SyntaxObservationClassifier {
    private static final Set<String> FORMULAS = Set.of("dice", "damage", "scaledice", "scaledamage");
    private static final Set<String> MODIFIERS = Set.of("hit", "d20");
    private static final Set<String> LABELS = Set.of(
            "atk", "atkr", "h", "hom", "dc", "recharge", "actsave", "actsavefail", "actsavesuccess",
            "actsavesuccessorfail", "actsavefailby", "acttrigger", "actresponse", "hityourspellattack",
            "dcyourspellsave");
    private static final Set<String> REFERENCES = Set.of(
            "spell", "item", "feat", "optfeature", "skill", "language", "condition", "creature", "status",
            "disease", "sense", "action", "class", "subclass", "classfeature", "subclassfeature");
    private static final Set<String> FORMATTING = Set.of("b", "bold", "i", "italic", "note", "link", "color");

    public List<SyntaxObservation> classify(MarkupParseResult parsed) {
        return parsed.preorder().stream().map(this::classify).toList();
    }

    public SyntaxObservation classify(MarkupTag tag) {
        String name = tag.name().toLowerCase(Locale.ROOT);
        if (FORMULAS.contains(name) && !tag.arguments().isEmpty()) {
            return new FormulaSyntaxObservation(tag, SyntaxExpression.recognize(tag.arguments().getFirst()));
        }
        if (MODIFIERS.contains(name) && !tag.arguments().isEmpty()) {
            return new ModifierSyntaxObservation(tag, SyntaxExpression.recognize(tag.arguments().getFirst()));
        }
        if (LABELS.contains(name)) {
            return new LabelSyntaxObservation(tag);
        }
        OpaqueTagCategory category;
        if (REFERENCES.contains(name)) {
            category = OpaqueTagCategory.REFERENCE;
        } else if (FORMATTING.contains(name)) {
            category = OpaqueTagCategory.FORMATTING;
        } else {
            category = OpaqueTagCategory.UNSUPPORTED;
        }
        return new OpaqueSyntaxObservation(tag, category);
    }
}
