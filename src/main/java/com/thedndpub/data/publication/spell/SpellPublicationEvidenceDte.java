package com.thedndpub.data.publication.spell;

import java.util.List;

public record SpellPublicationEvidenceDte(
        int schemaVersion,
        String crosswalkSha256,
        List<SpellLicensedDocumentDte> documents,
        List<SpellLicensedFormulaEvidenceDte> matches) {
    public SpellPublicationEvidenceDte {
        documents = List.copyOf(documents);
        matches = List.copyOf(matches);
    }
}
