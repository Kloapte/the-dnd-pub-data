package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RaceAbilityDte(
        Integer str,
        Integer dex,
        Integer con,
        @com.fasterxml.jackson.annotation.JsonProperty("int") Integer intScore,
        Integer wis,
        Integer cha,
        @JsonProperty("choose") RaceAbilityChoiceDte choice) {
}
