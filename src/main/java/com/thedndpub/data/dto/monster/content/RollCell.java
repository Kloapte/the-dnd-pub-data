package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RollCell(Integer exact, Integer minimum, Integer maximum, Boolean pad) implements TableCellDto {
}
