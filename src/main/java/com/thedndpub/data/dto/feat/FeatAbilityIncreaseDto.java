package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatAbilityIncreaseDto(
        Integer str,
        Integer dex,
        Integer con,
        @JsonProperty("int") Integer intScore,
        Integer wis,
        Integer cha,
        FeatAbilityChoiceDto choose,
        Integer max) {
}
