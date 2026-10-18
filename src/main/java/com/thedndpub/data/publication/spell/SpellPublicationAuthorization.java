package com.thedndpub.data.publication.spell;

import com.thedndpub.data.dto.spell.SpellFormulaAnnotation;

import java.util.Objects;

/** Internal validated authorization, unused by public projection in P1D. */
public record SpellPublicationAuthorization(
        SpellFormulaAnnotation annotation,
        String evidenceId,
        String noticeId,
        String decisionId) {
    public SpellPublicationAuthorization {
        Objects.requireNonNull(annotation, "annotation");
        Objects.requireNonNull(evidenceId, "evidenceId");
        Objects.requireNonNull(noticeId, "noticeId");
        Objects.requireNonNull(decisionId, "decisionId");
    }
}
