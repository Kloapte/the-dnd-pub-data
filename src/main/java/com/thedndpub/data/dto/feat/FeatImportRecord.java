package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dte.feat.FeatDte;

/** Internal source import and normalized projection kept together without exposing import fields publicly. */
public record FeatImportRecord(
        FeatDefinition definition,
        FeatDte sourceRecord,
        FeatExpansionProvenance expansionProvenance) {
}
