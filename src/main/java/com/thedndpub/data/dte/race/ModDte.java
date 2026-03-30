package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.ModEntryDteDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class ModDte {
    @JsonDeserialize(using = ModEntryDteDeserializer.class)
    private List<ModEntryDte> entries;
}
