package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConcealedMessage(
        String kind,
        String channel,
        MessageComprehension comprehension,
        MessageRecognition recognition,
        MessageDeciphering deciphering,
        Integer communicationDurationMultiplier) {
}
