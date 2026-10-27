package com.thedndpub.data.dto.language.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LanguageCategory {
    @JsonProperty("standard")
    STANDARD,
    @JsonProperty("exotic")
    EXOTIC,
    @JsonProperty("rare")
    RARE,
    @JsonProperty("secret")
    SECRET
}
