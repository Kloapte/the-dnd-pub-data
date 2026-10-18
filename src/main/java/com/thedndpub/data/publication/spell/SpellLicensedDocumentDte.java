package com.thedndpub.data.publication.spell;

public record SpellLicensedDocumentDte(
        String id,
        String version,
        String url,
        String sha256,
        String licenseId,
        String licenseUrl) {
}
