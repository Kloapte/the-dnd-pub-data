package com.thedndpub.data.dto.monster.defenses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.statistics.NullableList;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Defenses(NullableList<Defense> resistances, NullableList<Defense> immunities,
                       NullableList<Defense> vulnerabilities, NullableList<Defense> conditionImmunities) {
}
