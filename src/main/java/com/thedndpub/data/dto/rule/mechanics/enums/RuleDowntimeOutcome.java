package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleDowntimeOutcome {
    @JsonProperty("item-created") ITEM_CREATED,
    @JsonProperty("information-gained") INFORMATION_GAINED,
    @JsonProperty("resource-gained") RESOURCE_GAINED,
    @JsonProperty("complication") COMPLICATION
}
