package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundCharacteristicTableDto(String name, List<String> columns, int rowCount) {
    public BackgroundCharacteristicTableDto {
        columns = List.copyOf(columns);
    }
}
