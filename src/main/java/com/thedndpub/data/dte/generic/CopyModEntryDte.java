package com.thedndpub.data.dte.generic;

import com.thedndpub.data.dte.generic.deserializers.ReplaceDeserializer;
import com.thedndpub.data.dte.generic.deserializers.ModEntriesItemDteDeserializer;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@Data
public class CopyModEntryDte {
    private String mode;
    @JsonDeserialize(using = ReplaceDeserializer.class)
    private String replace;
    private String names;
    private String with;
    private String flags;
    private int index;
    @JsonDeserialize(using = ModEntriesItemDteDeserializer.class)
    private List<CopyModEntryItemDte> items;
}
