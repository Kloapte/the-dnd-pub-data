package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PublicationGroup {
    @JsonProperty("core")
    CORE,
    @JsonProperty("screen")
    SCREEN,
    @JsonProperty("setting")
    SETTING,
    @JsonProperty("setting-alt")
    SETTING_ALT,
    @JsonProperty("organized-play")
    ORGANIZED_PLAY,
    @JsonProperty("supplement")
    SUPPLEMENT,
    @JsonProperty("supplement-alt")
    SUPPLEMENT_ALT,
    @JsonProperty("other")
    OTHER,
    @JsonProperty("recipe")
    RECIPE,
    @JsonProperty("homecraft")
    HOMECRAFT
}
