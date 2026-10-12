package com.thedndpub.data.dto.monster.features;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VariantReference(String name, String addAs, String addHeadersAs) {
}
