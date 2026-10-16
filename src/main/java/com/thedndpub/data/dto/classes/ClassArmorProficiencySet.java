package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassArmorProficiencySet(
        Boolean light,
        Boolean medium,
        Boolean heavy,
        Boolean shield) {
}
