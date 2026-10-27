package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSwimSpeedException(String kind, String mode, Boolean avoidsNormalExtraMovementCost)
        implements RuleMovementDetails { }
