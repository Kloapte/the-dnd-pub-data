package com.thedndpub.data.publication.spell;

/** Reviewed licensed-match evidence; its presence never authorizes publication. */
public record SpellLicensedFormulaEvidenceDte(
        String id,
        SpellLicenseEvidenceClassification classification,
        String occurrenceId,
        String reviewBindingSha256,
        String documentId,
        String spellHeading,
        int pdfPage,
        String column,
        String expression,
        String matchSha256,
        String noticeId) {
}
