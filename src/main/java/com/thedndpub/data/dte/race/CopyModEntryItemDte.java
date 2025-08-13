package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.ModEntriesItemEntriesDteDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class CopyModEntryItemDte {
    private String name;
    private String type;
    @JsonDeserialize(using = ModEntriesItemEntriesDteDeserializer.class)
    private List<String> entries;
}
