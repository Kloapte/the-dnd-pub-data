package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.ModEntriesItemDteDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class CopyModEntryDte {
    private String mode;
    private String replace;
    @JsonDeserialize(using = ModEntriesItemDteDeserializer.class)
    private List<CopyModEntryItemDte> items;
}
