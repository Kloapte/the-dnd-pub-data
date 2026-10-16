package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BackgroundCopyDte(
        String name,
        String source,
        @JsonProperty("_mod") BackgroundCopyModificationDte modification) {
}
