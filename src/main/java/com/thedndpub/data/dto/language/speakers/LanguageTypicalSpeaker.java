package com.thedndpub.data.dto.language.speakers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReferencedCreatureSpeaker.class),
        @JsonSubTypes.Type(value = FilteredCreatureSpeaker.class)
})
public sealed interface LanguageTypicalSpeaker permits ReferencedCreatureSpeaker, FilteredCreatureSpeaker {
}
