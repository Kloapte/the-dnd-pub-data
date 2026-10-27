package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AllAbilityScorePointsRestoredTermination(String kind, List<String> abilities)
        implements DiseaseTermination {
    public AllAbilityScorePointsRestoredTermination {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
