package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemAttunementDto(Boolean required, List<ItemAttunementTagDto> restrictions) {
    public ItemAttunementDto {
        restrictions = List.copyOf(restrictions);
    }
}
