package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.race.deserializers.ModEntryDteDeserializer;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@Data
public class ModDte {
    @JsonDeserialize(using = ModEntryDteDeserializer.class)
    private List<ModEntryDte> entries;
}
