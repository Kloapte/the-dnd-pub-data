package com.thedndpub.data.dte.generic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.generic.deserializers.ModEntriesItemDteDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class CopyModEntryItemDte {
    private String name;
    private String type;
    private String style;

    @JsonDeserialize(using = ModEntriesItemDteDeserializer.class)
    private List<CopyModEntryItemDte> entries;

    @JsonDeserialize(using = ModEntriesItemDteDeserializer.class)
    private List<CopyModEntryItemDte> items;
}
