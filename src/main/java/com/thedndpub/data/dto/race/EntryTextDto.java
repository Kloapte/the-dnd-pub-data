package com.thedndpub.data.dto.race;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EntryTextDto extends EntryDto {
    private List<String> textItems = new ArrayList<>();

    public EntryTextDto() {
        this.setType("text");
    }
}
