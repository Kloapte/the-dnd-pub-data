package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureSummaryDto(
        String id,
        String name,
        SourceReference source,
        List<String> featureTypes) {
    public OptionalFeatureSummaryDto {
        featureTypes = List.copyOf(featureTypes);
    }

    public static OptionalFeatureSummaryDto from(OptionalFeatureDefinition definition) {
        return new OptionalFeatureSummaryDto(
                definition.id(), definition.name(), definition.source(), definition.featureTypes());
    }
}
