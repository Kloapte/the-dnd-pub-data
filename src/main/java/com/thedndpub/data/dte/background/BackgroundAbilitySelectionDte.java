package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundAbilitySelectionDte(BackgroundWeightedAbilityDte weighted) {
}
