package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Preserves the source timing union without imposing an action-cost taxonomy. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionTiming(Integer number, String unit, String special) {
}
