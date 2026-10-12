package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SaveDte(
        @JsonProperty("cha")
        String charisma,
        @JsonProperty("wis")
        String wisdom,
        @JsonProperty("int")
        String intelligence,
        @JsonProperty("dex")
        String dexterity,
        @JsonProperty("str")
        String strength,
        @JsonProperty("con")
        String constitution) {
}
