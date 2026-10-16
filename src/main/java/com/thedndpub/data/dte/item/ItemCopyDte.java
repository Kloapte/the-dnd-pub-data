package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemCopyDte(
        String name,
        String source,
        @JsonProperty("_mod") ItemCopyModificationDte modification,
        @JsonProperty("_preserve") ItemCopyPreserveDte preserve) {
}
