package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilityHireling(Integer exact, Integer min, String space) {
}
