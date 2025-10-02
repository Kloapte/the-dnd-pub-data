package com.thedndpub.data.dto.race;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EntryWithListDto extends EntryDto {
    private final List<EntryListItemDto> listItems = new ArrayList<>();

    public EntryWithListDto() {
        this.setType("list");
    }
}
