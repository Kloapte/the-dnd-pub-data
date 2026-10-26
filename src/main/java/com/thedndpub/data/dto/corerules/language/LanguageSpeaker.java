package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CreatureSpeaker.class),
        @JsonSubTypes.Type(CreatureFilterSpeaker.class)
})
public sealed interface LanguageSpeaker permits CreatureSpeaker, CreatureFilterSpeaker {
}
