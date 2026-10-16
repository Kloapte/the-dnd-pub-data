package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FeatClassLevelPrerequisiteDte(
        Integer level,
        @JsonProperty("class") FeatPrerequisiteClassDte classRequirement)
        implements FeatLevelPrerequisiteDte {
}
