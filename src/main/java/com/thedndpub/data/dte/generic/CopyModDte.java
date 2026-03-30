package com.thedndpub.data.dte.generic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.generic.deserializers.ModEntriesDteDeserializer;
import lombok.Data;

import java.util.List;

@Data
public class CopyModDte {
    @JsonDeserialize(using = ModEntriesDteDeserializer.class)
    private List<CopyModEntryDte> entries;
}
