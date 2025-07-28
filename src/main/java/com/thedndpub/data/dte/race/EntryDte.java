package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.EntryDtoDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EntryDte {
    private String name;
    private String text;
    private String type;
    private String style;
    @JsonDeserialize(using = EntryDtoDeserializer.class)
    private List<EntryDte> entries;
    @JsonDeserialize(using = EntryDtoDeserializer.class)
    private List<EntryDte> items;
    private String entry;
    private String caption;
    private List<String> colLabels;
    private List<String> colStyles;
    private List<List<String>> rows;
    private DataDte data;
}
