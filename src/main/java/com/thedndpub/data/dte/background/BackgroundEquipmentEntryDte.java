package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BackgroundEquipmentEntryDeserializer.class)
public record BackgroundEquipmentEntryDte(
        String item,
        String special,
        String equipmentType,
        Integer value,
        String displayName,
        Integer quantity,
        Integer containsValue,
        Integer worthValue) {
}
