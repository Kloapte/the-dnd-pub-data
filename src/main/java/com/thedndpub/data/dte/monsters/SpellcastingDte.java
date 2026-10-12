package com.thedndpub.data.dte.monsters;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thedndpub.data.dte.monsters.deserializers.WillDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpellcastingDte(
        String name,
        String type,
        List<String> headerEntries,
        @JsonDeserialize(contentUsing = WillDeserializer.class)
        List<String> will,
        SpellcastingDailyDte daily
) {
}
