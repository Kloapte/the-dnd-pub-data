package com.thedndpub.data.dto.bastion;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import java.util.List;

public record FacilityDefinition(
        String id,
        String name,
        SourceReference source,
        Integer page,
        String facilityType,
        Integer level,
        List<FacilityPrerequisite> prerequisites,
        List<String> space,
        List<FacilityHireling> hirelings,
        List<String> orders,
        List<EntryDte> entries,
        Boolean hasFluffImages,
        Boolean hasFluff) {
    public FacilityDefinition {
        prerequisites = copy(prerequisites);
        space = copy(space);
        hirelings = copy(hirelings);
        orders = copy(orders);
        entries = copy(entries);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
