package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@JsonDeserialize(using = ClassEquipmentEntryDeserializer.class)
public record ClassEquipmentEntryDte(
        String item,
        Integer quantity,
        String equipmentType,
        List<String> equipmentTypes,
        Integer value,
        String special,
        @JsonIgnore boolean stringForm) {
    public ClassEquipmentEntryDte {
        equipmentTypes = ClassDteCollections.list(equipmentTypes);
    }
}
