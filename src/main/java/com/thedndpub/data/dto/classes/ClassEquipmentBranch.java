package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassEquipmentBranch(
        String key,
        List<ClassEquipmentOption> entries) {
    public ClassEquipmentBranch {
        entries = entries == null ? List.of() : List.copyOf(entries);
    }
}
