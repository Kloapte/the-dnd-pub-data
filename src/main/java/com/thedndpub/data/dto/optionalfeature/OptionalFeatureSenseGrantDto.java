package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureSenseGrantDto(Integer blindsight, Integer darkvision, Integer truesight) {
}
