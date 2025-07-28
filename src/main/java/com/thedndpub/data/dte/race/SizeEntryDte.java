package com.thedndpub.data.dte.race;

import lombok.Data;

import java.util.List;

@Data
public class SizeEntryDte {
    private String type;
    private String name;
    private List<String> entries;
}
