package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RulesAvailability(Boolean basicRules, Boolean basicRules2024, Boolean srd,
                                Boolean srd52, Set<String> editionIds) {
}
