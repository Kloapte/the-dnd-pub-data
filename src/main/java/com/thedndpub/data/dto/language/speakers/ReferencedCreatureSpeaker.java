package com.thedndpub.data.dto.language.speakers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.language.enums.LanguageSpeakerTargetType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReferencedCreatureSpeaker(LanguageSpeakerTargetType targetType, String id)
        implements LanguageTypicalSpeaker {
}
