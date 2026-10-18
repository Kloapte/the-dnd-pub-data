package com.thedndpub.data.publication.spell;

import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxExpression;

/** A frozen review candidate, never an approval or a public formula projection. */
public record SpellPublicationCandidateDte(
        String spellId,
        String sourceCode,
        int occurrenceIndex,
        SyntaxEvidence sourceEvidence,
        SyntaxExpression expression,
        SpellPublicationNotation notation,
        String tagSha256,
        String reviewBindingSha256,
        String evidenceId,
        String noticeId,
        SpellPublicationState initialReviewState) {

    public String occurrenceId() {
        return spellId + "#formula-" + occurrenceIndex;
    }
}
