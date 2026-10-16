package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FeatPrerequisiteAbilityDte(
        Integer str,
        Integer dex,
        Integer con,
        @JsonProperty("int") Integer intScore,
        Integer wis,
        Integer cha) {
}
