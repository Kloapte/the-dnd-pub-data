package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseInteractionEffect {
    @JsonProperty("bypasses-sight-restriction") BYPASSES_SIGHT_RESTRICTION,
    @JsonProperty("does-not-bypass-sight-restriction") DOES_NOT_BYPASS_SIGHT_RESTRICTION,
    @JsonProperty("unaffected-by-blindness") UNAFFECTED_BY_BLINDNESS
}
