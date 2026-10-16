package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassNormalizationResult(
        List<ClassDefinition> classes,
        List<SubclassDefinition> subclasses,
        List<ClassFeatureDefinition> classFeatures,
        List<SubclassFeatureDefinition> subclassFeatures,
        List<ClassFeatureGraphEdge> featureGraph,
        List<ClassExternalReference> externalReferences,
        ClassValidationSummary validation,
        List<ClassFileMetadata> metadata) {
    public ClassNormalizationResult {
        classes = classes == null ? List.of() : List.copyOf(classes);
        subclasses = subclasses == null ? List.of() : List.copyOf(subclasses);
        classFeatures = classFeatures == null ? List.of() : List.copyOf(classFeatures);
        subclassFeatures = subclassFeatures == null ? List.of() : List.copyOf(subclassFeatures);
        featureGraph = featureGraph == null ? List.of() : List.copyOf(featureGraph);
        externalReferences = externalReferences == null ? List.of() : List.copyOf(externalReferences);
        metadata = metadata == null ? List.of() : List.copyOf(metadata);
    }
}
