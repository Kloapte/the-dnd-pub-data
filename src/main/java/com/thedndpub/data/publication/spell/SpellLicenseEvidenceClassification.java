package com.thedndpub.data.publication.spell;

/** Licensing review outcome; none of these values grants publication permission. */
public enum SpellLicenseEvidenceClassification {
    VERIFIED_LICENSED_MATCH,
    VERIFIED_NOT_COVERED,
    AMBIGUOUS_MATCH,
    NO_VERIFIABLE_EVIDENCE,
    REQUIRES_SEPARATE_PERMISSION
}
