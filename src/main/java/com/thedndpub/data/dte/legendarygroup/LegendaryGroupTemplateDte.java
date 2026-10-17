package com.thedndpub.data.dte.legendarygroup;

import java.util.Objects;

public record LegendaryGroupTemplateDte(
        String name,
        String source,
        Integer page,
        String ref,
        LegendaryGroupTemplateApplicationDte apply) {
    public LegendaryGroupTemplateDte {
        Objects.requireNonNull(apply, "apply");
    }
}
