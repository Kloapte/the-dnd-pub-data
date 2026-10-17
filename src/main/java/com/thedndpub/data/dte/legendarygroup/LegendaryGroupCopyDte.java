package com.thedndpub.data.dte.legendarygroup;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LegendaryGroupCopyDte(
        String name,
        String source,
        @JsonProperty("_mod") LegendaryGroupModificationsDte modifications) {
}
