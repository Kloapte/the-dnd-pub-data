package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseMedium {
    @JsonProperty("touch") TOUCH,
    @JsonProperty("vibration") VIBRATION,
    @JsonProperty("sound") SOUND,
    @JsonProperty("smell") SMELL,
    @JsonProperty("other") OTHER
}
