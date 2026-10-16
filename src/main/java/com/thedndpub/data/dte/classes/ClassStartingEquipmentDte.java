package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ClassStartingEquipmentDte(
        Boolean additionalFromBackground,
        @JsonProperty("default") List<String> defaultEntries,
        String goldAlternative,
        List<ClassEquipmentGroupDte> defaultData,
        List<String> entries) {
    public ClassStartingEquipmentDte {
        defaultEntries = ClassDteCollections.list(defaultEntries);
        defaultData = ClassDteCollections.list(defaultData);
        entries = ClassDteCollections.list(entries);
    }
}
