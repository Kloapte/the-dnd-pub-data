package com.thedndpub.data.dte.bastion;

import com.thedndpub.data.dte.common.entry.EntryDte;
import java.util.List;

public record FacilityDte(
        String name,
        String source,
        Integer page,
        String facilityType,
        Integer level,
        List<FacilityPrerequisiteDte> prerequisite,
        List<String> space,
        List<FacilityHirelingDte> hirelings,
        List<String> orders,
        List<EntryDte> entries,
        Boolean hasFluffImages,
        Boolean hasFluff) {
}
