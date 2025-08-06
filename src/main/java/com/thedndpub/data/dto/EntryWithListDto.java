package com.thedndpub.data.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class EntryWithListDto extends EntryDto {
    private final List<EntryListItemDto> listItems = new ArrayList<>();

    public EntryWithListDto() {
        this.setType("list");
    }
}
