package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureChoiceDto(Integer count, String style, List<ClassChoiceReferenceDto> options,
        List<ClassFeatureChoiceDto> groups) {
    public ClassFeatureChoiceDto {
        options = options == null ? List.of() : List.copyOf(options);
        groups = groups == null ? List.of() : List.copyOf(groups);
    }
}
