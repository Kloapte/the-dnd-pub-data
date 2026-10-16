package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundEquipmentEntryDto(
        String kind,
        BackgroundItemReferenceDto item,
        String text,
        String equipmentType,
        String displayName,
        Integer quantity,
        Integer valueCopper,
        Integer containsValueCopper,
        Integer worthValueCopper) {
}
