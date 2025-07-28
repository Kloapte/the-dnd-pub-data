package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.ModEntryDte;
import lombok.Data;

import java.util.List;

@Data
public class ModDto {
    @JsonDeserialize(using = ModEntryDtoDeserializer.class)
    private List<ModEntryDte> entries;
}
