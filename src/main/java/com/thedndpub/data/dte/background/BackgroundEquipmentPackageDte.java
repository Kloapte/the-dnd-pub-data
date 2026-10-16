package com.thedndpub.data.dte.background;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = BackgroundEquipmentPackageDeserializer.class)
public record BackgroundEquipmentPackageDte(Map<String, List<BackgroundEquipmentEntryDte>> options) {
    public BackgroundEquipmentPackageDte {
        options = Collections.unmodifiableMap(new TreeMap<>(options));
    }
}
