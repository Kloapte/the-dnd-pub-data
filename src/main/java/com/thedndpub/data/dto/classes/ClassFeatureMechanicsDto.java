package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureMechanicsDto(
        List<String> referencedFeatureIds,
        List<String> optionalFeatureIds,
        List<String> featIds,
        List<ClassFeatureChoiceDto> choices,
        ClassFeatureConsumption consumption) {
    public ClassFeatureMechanicsDto {
        referencedFeatureIds = immutable(referencedFeatureIds);
        optionalFeatureIds = immutable(optionalFeatureIds);
        featIds = immutable(featIds);
        choices = immutable(choices);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
