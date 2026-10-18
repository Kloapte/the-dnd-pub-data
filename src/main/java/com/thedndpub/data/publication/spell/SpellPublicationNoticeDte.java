package com.thedndpub.data.publication.spell;

public record SpellPublicationNoticeDte(
        String id,
        String documentId,
        String requiredAttribution,
        String requiredAttributionSha256,
        String changesDisclosure,
        String changesDisclosureSha256,
        String disclaimerReference) {
}
