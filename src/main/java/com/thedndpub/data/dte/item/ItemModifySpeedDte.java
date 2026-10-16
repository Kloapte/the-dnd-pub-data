package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ItemModifySpeedDte(
        Map<String, String> equal,
        Map<String, ItemNumberTextDte> multiply,
        @JsonProperty("static") Map<String, ItemNumberTextDte> staticValues,
        Map<String, ItemNumberTextDte> bonus) {
}
