package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassEquipmentGroup(
        int order,
        boolean unconditional,
        List<ClassEquipmentBranch> branches) {
    public ClassEquipmentGroup {
        branches = branches == null ? List.of() : List.copyOf(branches);
    }
}
