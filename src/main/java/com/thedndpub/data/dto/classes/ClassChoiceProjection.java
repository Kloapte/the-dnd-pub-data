package com.thedndpub.data.dto.classes;

import java.util.ArrayList;
import java.util.List;

/** Assesses the existing reference-only choice projection without publishing withheld content. */
final class ClassChoiceProjection {
    private ClassChoiceProjection() {
    }

    static ClassChoiceProjectionStatus status(List<ClassFeatureNode> content) {
        List<ClassFeatureChoice> choices = new ArrayList<>();
        collectChoices(content, choices);
        if (choices.isEmpty()) {
            return ClassChoiceProjectionStatus.NONE;
        }
        if (choices.stream().noneMatch(choice -> hasPublicAlternative(choice.options()))) {
            return ClassChoiceProjectionStatus.WITHHELD;
        }
        if (choices.stream().allMatch(ClassChoiceProjection::isCompletelyProjected)) {
            return ClassChoiceProjectionStatus.COMPLETE;
        }
        return ClassChoiceProjectionStatus.PARTIAL;
    }

    private static void collectChoices(List<ClassFeatureNode> nodes, List<ClassFeatureChoice> choices) {
        for (ClassFeatureNode node : nodes) {
            if (node instanceof ClassFeatureChoice choice) {
                choices.add(choice);
                collectChoices(choice.options(), choices);
            } else if (node instanceof ClassFeatureSection section) {
                collectChoices(section.entries(), choices);
                collectChoices(section.items(), choices);
            }
        }
    }

    private static boolean hasPublicAlternative(List<ClassFeatureNode> nodes) {
        for (ClassFeatureNode node : nodes) {
            if (node instanceof ClassFeatureReferenceNode || node instanceof ClassExternalFeatureNode) {
                return true;
            }
            if (node instanceof ClassFeatureChoice choice && hasPublicAlternative(choice.options())) {
                return true;
            }
            if (node instanceof ClassFeatureSection section
                    && (hasPublicAlternative(section.entries()) || hasPublicAlternative(section.items()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCompletelyProjected(ClassFeatureChoice choice) {
        // An empty, unlabelled node is dropped by the existing public choice mapper.
        if (choice.options().isEmpty() && choice.count() == null && choice.style() == null) {
            return false;
        }
        return choice.options().stream().allMatch(ClassChoiceProjection::isCompletelyProjected);
    }

    private static boolean isCompletelyProjected(ClassFeatureNode node) {
        return switch (node) {
            case ClassFeatureReferenceNode ignored -> true;
            case ClassExternalFeatureNode reference ->
                    (reference.name() == null || reference.name().equals(reference.reference().name()))
                            && reference.required() == null;
            case ClassFeatureChoice choice -> isCompletelyProjected(choice);
            // Section structure and labels are flattened; other entry variants are withheld.
            default -> false;
        };
    }
}
