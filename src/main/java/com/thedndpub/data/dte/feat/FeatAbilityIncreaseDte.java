package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FeatAbilityIncreaseDte(
        Integer str,
        Integer dex,
        Integer con,
        @JsonProperty("int") Integer intScore,
        Integer wis,
        Integer cha,
        FeatAbilityChoiceDte choose,
        Integer max,
        Boolean hidden) {
}
