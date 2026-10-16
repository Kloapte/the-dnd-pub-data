package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClassCopyDte(
        String name,
        String source,
        String shortName,
        String className,
        String classSource,
        String subclassShortName,
        String subclassSource,
        Integer level,
        @JsonProperty("_preserve") ClassCopyPreserveDte preserve) {
}
