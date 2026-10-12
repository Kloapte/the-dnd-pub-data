package com.thedndpub.data.dto.monster.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ProficiencyBonus(Integer value, String note, String derivationRuleset) {
}
