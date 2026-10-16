package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemAbilityScoresDte(Integer str, Integer dex, Integer con,
                                   @JsonProperty("int") Integer intScore, Integer wis, Integer cha) {
}
