package com.thedndpub.data.dte.legendarygroup;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public record LegendaryGroupVersionDte(
        String name,
        String source,
        @JsonProperty("_templates") List<LegendaryGroupTemplateReferenceDte> templates) {
    public LegendaryGroupVersionDte {
        Objects.requireNonNull(templates, "templates");
        templates = List.copyOf(templates);
    }
}
