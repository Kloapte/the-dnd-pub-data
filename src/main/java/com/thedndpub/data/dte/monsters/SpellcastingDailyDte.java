package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpellcastingDailyDte(
        @JsonProperty("1e")
        Object oneEach,
        @JsonProperty("2e")
        Object twoEach,
        @JsonProperty("3e")
        Object threeEach,

        @JsonProperty("2e")
        Object two
) {
}
