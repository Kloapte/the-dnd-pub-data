package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundPrerequisiteDto(List<String> campaigns) {
    public BackgroundPrerequisiteDto {
        campaigns = List.copyOf(campaigns);
    }
}
