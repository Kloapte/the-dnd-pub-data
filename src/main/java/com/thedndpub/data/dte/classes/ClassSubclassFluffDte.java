package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClassSubclassFluffDte(
        @JsonProperty("_subclassFluff") ClassFluffReferenceDte subclassFluff) {
}
