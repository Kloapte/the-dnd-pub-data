package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ItemAbilityDte(
        Integer str,
        Integer dex,
        Integer con,
        @JsonProperty("int") Integer intScore,
        Integer wis,
        Integer cha,
        List<String> from,
        Integer count,
        Integer amount,
        List<ItemAbilityChoiceDte> choose,
        @JsonProperty("static") ItemAbilityScoresDte staticScores) {
}
