package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckRequirement {
    @JsonProperty("DM_MAY_REQUIRE") DM_MAY_REQUIRE,
    @JsonProperty("ALWAYS_REQUIRED") ALWAYS_REQUIRED
}
