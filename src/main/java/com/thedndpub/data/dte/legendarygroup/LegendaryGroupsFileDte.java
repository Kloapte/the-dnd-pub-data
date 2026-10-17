package com.thedndpub.data.dte.legendarygroup;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public record LegendaryGroupsFileDte(
        @JsonProperty("_meta") LegendaryGroupMetadataDte metadata,
        List<LegendaryGroupSourceDte> legendaryGroup) {
    public LegendaryGroupsFileDte {
        Objects.requireNonNull(legendaryGroup, "legendaryGroup");
        legendaryGroup = List.copyOf(legendaryGroup);
    }
}
