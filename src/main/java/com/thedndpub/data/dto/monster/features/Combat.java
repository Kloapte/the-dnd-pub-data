package com.thedndpub.data.dto.monster.features;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Combat(Integer legendaryActions, Integer legendaryActionsLair) {
}
