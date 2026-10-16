package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceReprintDto(String uid, String tag, String name, SourceIdentity source) {
}
