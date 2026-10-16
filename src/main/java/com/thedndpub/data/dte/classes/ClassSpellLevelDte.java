package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = ClassSpellLevelDeserializer.class)
public record ClassSpellLevelDte(
        List<ClassSpellSelectionDte> spells,
        @JsonProperty("_") List<ClassSpellSelectionDte> unrestricted,
        Map<String, List<ClassSpellSelectionDte>> daily,
        Map<String, List<ClassSpellSelectionDte>> rest,
        Map<String, List<ClassSpellSelectionDte>> resource,
        List<ClassSpellSelectionDte> ritual) {
    public ClassSpellLevelDte {
        spells = ClassDteCollections.list(spells);
        unrestricted = ClassDteCollections.list(unrestricted);
        daily = ClassDteCollections.mapOfLists(daily);
        rest = ClassDteCollections.mapOfLists(rest);
        resource = ClassDteCollections.mapOfLists(resource);
        ritual = ClassDteCollections.list(ritual);
    }
}
