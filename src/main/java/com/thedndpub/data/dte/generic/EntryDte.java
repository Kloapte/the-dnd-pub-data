package com.thedndpub.data.dte.generic;

import com.thedndpub.data.dte.generic.deserializers.EntryRowDeserializer;
import com.thedndpub.data.dte.generic.deserializers.EntryDteDeserializer;
import com.thedndpub.data.dte.race.DataDte;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@Data
public class EntryDte {
    private String name;
    private String text;
    private String type;
    private String style;
    @JsonDeserialize(using = EntryDteDeserializer.class)
    private List<EntryDte> entries;
    @JsonDeserialize(using = EntryDteDeserializer.class)
    private List<EntryDte> items;
    private String entry;
    private String caption;
    private List<String> colLabels;
    private List<String> colStyles;
    @JsonDeserialize(using = EntryRowDeserializer.class)
    private List<List<String>> rows;
    private DataDte data;
    private List<String> footnotes;

    private int columns; //Ignore does nothing, some random thing in some items
    private int page; //Ignore does nothing, some random thing in some items
    private String source; //Ignore only happens once for MOT somewhere
}
