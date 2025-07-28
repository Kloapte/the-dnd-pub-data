package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.EntryDtoDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class ModEntryDte {
    private String mode;
    private String names;
    private String replace;
    @JsonDeserialize(using = EntryDtoDeserializer.class)
    private List<EntryDte> items;
}
