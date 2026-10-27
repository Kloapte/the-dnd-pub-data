package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFailedSaveForcedMovement(String kind, String movement, Integer distance, String unit)
        implements RuleFailedSaveOutcome { }
