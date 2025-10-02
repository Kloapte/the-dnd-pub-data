package com.thedndpub.data.dto.race;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EntryWithSubDto extends EntryDto {
    private List<EntryDto> subItems = new ArrayList<>();

    public EntryWithSubDto() {
        this.setType("subEntries");
    }
}
