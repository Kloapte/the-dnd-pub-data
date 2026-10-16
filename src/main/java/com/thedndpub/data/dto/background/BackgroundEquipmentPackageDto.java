package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundEquipmentPackageDto(Map<String, List<BackgroundEquipmentEntryDto>> options) {
    public BackgroundEquipmentPackageDto {
        options = Collections.unmodifiableMap(new TreeMap<>(options));
    }
}
