package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

/** A safe progression value at its original zero-based source column. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassPositionedTableCellDto(
        int columnIndex,
        ClassTableCellKind kind,
        Integer number,
        List<ClassHitDice> dice,
        Boolean rollable) {
    public ClassPositionedTableCellDto {
        if (columnIndex < 0) {
            throw new IllegalArgumentException("A progression column index cannot be negative");
        }
        Objects.requireNonNull(kind, "kind");
        if (kind == ClassTableCellKind.PRESENTATION_TEXT) {
            throw new IllegalArgumentException("Presentation text is not a public positioned cell");
        }
        dice = dice == null ? List.of() : List.copyOf(dice);
    }
}
