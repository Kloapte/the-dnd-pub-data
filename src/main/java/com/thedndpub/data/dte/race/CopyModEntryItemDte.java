package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.EntryDtoDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class CopyModEntryItemDte {
    private String name;
    private String type;
    private List<String> entries;
}
