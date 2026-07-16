package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.generic.EntryDte;
import com.thedndpub.data.dte.generic.deserializers.EntryDteDeserializer;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@Data
public class ModEntryDte {
    private String mode;
    private String names;
    private String replace;
    @JsonDeserialize(using = EntryDteDeserializer.class)
    private List<EntryDte> items;
}
