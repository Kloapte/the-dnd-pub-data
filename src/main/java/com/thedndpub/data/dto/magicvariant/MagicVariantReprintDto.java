package com.thedndpub.data.dto.magicvariant;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MagicVariantReprintDto(String name, String source) {
}
