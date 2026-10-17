package com.thedndpub.data.dto.bastion;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FacilityProficiency(List<String> skill) {
    public FacilityProficiency {
        skill = skill == null ? List.of() : List.copyOf(skill);
    }
}
