package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatLevelRequirementDto(Integer level, String className, Boolean visible) {
}
