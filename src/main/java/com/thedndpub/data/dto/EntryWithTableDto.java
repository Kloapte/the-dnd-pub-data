package com.thedndpub.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EntryWithTableDto extends EntryDto {
    private String title;
    private Map<Integer, String> headers = new HashMap<>();
    private List<Map<Integer, String>> rows = new ArrayList<>();

    public EntryWithTableDto() {
        this.setType("table");
    }
}
