package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public record BackgroundsFileDte(
        BackgroundMetadataDte _meta,
        List<BackgroundDte> background) {
}
