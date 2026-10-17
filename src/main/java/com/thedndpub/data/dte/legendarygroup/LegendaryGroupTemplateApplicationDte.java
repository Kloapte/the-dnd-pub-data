package com.thedndpub.data.dte.legendarygroup;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record LegendaryGroupTemplateApplicationDte(
        @JsonProperty("_mod") LegendaryGroupModificationsDte modifications) {
    public LegendaryGroupTemplateApplicationDte {
        Objects.requireNonNull(modifications, "modifications");
    }
}
