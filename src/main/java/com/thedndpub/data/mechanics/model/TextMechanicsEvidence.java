package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TextMechanicsEvidence(
        String id,
        SourceReference source,
        SyntaxEvidence syntax,
        String textSha256,
        MechanicsOrigin origin,
        MechanicsOwnerReference authoredOwner) implements MechanicsEvidence {
    public TextMechanicsEvidence {
        ModelChecks.text(id, "id");
        ModelChecks.required(syntax, "syntax");
        ModelChecks.digest(textSha256);
        ModelChecks.digest(syntax.location().owner().resourceSha256());
        ModelChecks.require(syntax.location().origin() == SyntaxOrigin.AUTHORED,
                "An original text span must not identify generated text or an unavailable origin");
        ModelChecks.required(source, "source");
        ModelChecks.required(origin, "origin");
        ModelChecks.require(origin == MechanicsOrigin.AUTHORED || origin == MechanicsOrigin.INHERITED_UNCHANGED,
                "Original text/fields must be authored or inherited unchanged");
        if (origin == MechanicsOrigin.INHERITED_UNCHANGED) {
            ModelChecks.required(authoredOwner, "Inherited evidence requires its original owner");
        }
        if (authoredOwner != null) {
            ModelChecks.require(authoredOwner.source().identity().equals(source.identity()), "Authored owner/source mismatch");
        }
    }
}
