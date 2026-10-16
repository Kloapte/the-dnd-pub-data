package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = ClassEquipmentGroupDeserializer.class)
public record ClassEquipmentGroupDte(
        Map<String, List<ClassEquipmentEntryDte>> branches) {
    public ClassEquipmentGroupDte {
        branches = ClassDteCollections.mapOfLists(branches);
    }
}
