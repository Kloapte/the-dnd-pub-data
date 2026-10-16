package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassTableCell(
        ClassTableCellKind kind,
        Integer number,
        String text,
        List<ClassHitDice> dice,
        Boolean rollable) {
    public ClassTableCell {
        dice = dice == null ? List.of() : List.copyOf(dice);
    }
}
