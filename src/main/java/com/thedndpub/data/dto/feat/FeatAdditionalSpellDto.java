package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatAdditionalSpellDto(
        String name,
        FeatSpellAbilityDto ability,
        FeatSpellGrantNodeDto prepared,
        FeatSpellGrantNodeDto known,
        FeatSpellGrantNodeDto innate,
        FeatSpellGrantNodeDto expanded) {
}
