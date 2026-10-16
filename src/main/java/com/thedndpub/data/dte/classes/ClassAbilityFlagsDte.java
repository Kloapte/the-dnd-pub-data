package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClassAbilityFlagsDte(
        Boolean str,
        Boolean dex,
        Boolean con,
        @JsonProperty("int") Boolean intelligence,
        Boolean wis,
        Boolean cha) {
}
