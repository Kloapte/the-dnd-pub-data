package com.thedndpub.data.dte.legendarygroup;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LegendaryGroupArrayMode {
    @JsonProperty("appendArr")
    APPEND,
    @JsonProperty("prependArr")
    PREPEND
}
