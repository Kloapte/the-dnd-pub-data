package com.thedndpub.data.dto.language.communication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageMessageRecognitionKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageMessageRecognition(
        LanguageMessageRecognitionKind kind,
        LanguagePresenceCheck otherwise) {
}
