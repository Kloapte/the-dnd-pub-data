package com.thedndpub.data.dto.language.communication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageCapabilityKind;
import com.thedndpub.data.dto.language.enums.LanguageMessageChannel;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageConcealedMessage(
        LanguageCapabilityKind kind,
        LanguageMessageChannel channel,
        LanguageMessageComprehension comprehension,
        LanguageMessageRecognition recognition,
        LanguageMessageDeciphering deciphering,
        Integer communicationDurationMultiplier) {
}
