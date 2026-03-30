package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.generic.CopyModEntryDte;
import com.thedndpub.data.dte.generic.deserializers.ModEntriesDteDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class VersionModDte {
    @JsonDeserialize(using = ModEntriesDteDeserializer.class)
    private List<CopyModEntryDte> entries;
}
