package com.thedndpub.data.dto.language.communication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.LanguageReference;
import com.thedndpub.data.dto.language.enums.LanguageDirectionality;
import com.thedndpub.data.dto.language.enums.LanguageIntelligibilityKind;
import com.thedndpub.data.dto.language.enums.LanguageIntelligibilityMode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageIntelligibility(
        LanguageIntelligibilityKind kind,
        LanguageIntelligibilityMode mode,
        LanguageDirectionality directionality,
        LanguageReference language) {
}
