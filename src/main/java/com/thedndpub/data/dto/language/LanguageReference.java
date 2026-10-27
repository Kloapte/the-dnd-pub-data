package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageReferenceType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageReference(
        LanguageReferenceType targetType,
        String id,
        String relationship,
        String operation) {
}
