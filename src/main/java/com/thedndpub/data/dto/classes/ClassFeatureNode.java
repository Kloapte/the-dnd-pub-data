package com.thedndpub.data.dto.classes;

/** Internal content graph; descriptions are retained without interpreting them as effects. */
public sealed interface ClassFeatureNode permits ClassFeatureText, ClassFeatureSection,
        ClassFeatureReferenceNode, ClassExternalFeatureNode, ClassFeatureChoice,
        ClassFeatureAbilityDeclaration, ClassFeatureStatblock, ClassFeatureTable {
}
