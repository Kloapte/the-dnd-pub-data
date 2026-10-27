package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSavingThrowRepeat(String ability, Double dc, String trigger, String dcTrackId) {
}
