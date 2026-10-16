package com.thedndpub.data.dto.item;

import com.thedndpub.data.dte.item.ItemSourceDte;

public record ItemImportProvenance(
        ItemSourceDte sourceRecord,
        ResolvedItemDte resolvedRecord,
        ItemDefinitionKind definitionKind,
        String copyTargetName,
        String copyTargetSource,
        boolean copyResolved) {
}
