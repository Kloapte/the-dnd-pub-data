package com.thedndpub.data.dto.language.communication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.LanguageReference;
import com.thedndpub.data.dto.language.enums.LanguagePresenceCheckKind;
import com.thedndpub.data.dto.language.enums.LanguagePresenceCheckOutcome;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguagePresenceCheck(
        LanguagePresenceCheckKind kind,
        String ability,
        LanguageReference skill,
        Integer dc,
        LanguagePresenceCheckOutcome outcome) {
}
