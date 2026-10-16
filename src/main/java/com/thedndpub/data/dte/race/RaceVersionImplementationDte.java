package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import java.util.List;

public record RaceVersionImplementationDte(
        @JsonProperty("_variables") RaceTemplateVariablesDte variables,
        List<String> resist,
        Integer darkvision,
        RaceSpeedDte speed,
        List<String> traitTags,
        List<BackgroundGrantGroupDte> skillProficiencies) {
    public RaceVersionImplementationDte {
        resist = resist == null ? null : List.copyOf(resist);
        traitTags = traitTags == null ? null : List.copyOf(traitTags);
        skillProficiencies = skillProficiencies == null ? null : List.copyOf(skillProficiencies);
    }
}
