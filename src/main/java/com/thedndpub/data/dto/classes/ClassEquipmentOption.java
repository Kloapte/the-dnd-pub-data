package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassEquipmentOption(
        ClassEquipmentKind kind,
        ClassExternalReference reference,
        List<String> categories,
        Integer quantity,
        Integer valueCopper,
        String special) {
    public ClassEquipmentOption {
        categories = categories == null ? List.of() : List.copyOf(categories);
    }
}
