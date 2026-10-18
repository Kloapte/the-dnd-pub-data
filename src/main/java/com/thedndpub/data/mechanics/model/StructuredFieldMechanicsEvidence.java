package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.syntax.SyntaxContext;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StructuredFieldMechanicsEvidence(
        String id,
        SourceReference source,
        SyntaxSourceOwner owner,
        String jsonPointer,
        List<SyntaxContext> hierarchy,
        String valueSha256,
        MechanicsOrigin origin,
        MechanicsOwnerReference authoredOwner) implements MechanicsEvidence {
    public StructuredFieldMechanicsEvidence {
        ModelChecks.text(id, "id");
        ModelChecks.required(owner, "owner");
        hierarchy = ModelChecks.list(hierarchy);
        new SyntaxTextLocation(owner, jsonPointer, hierarchy, SyntaxOrigin.AUTHORED);
        ModelChecks.digest(owner.resourceSha256());
        ModelChecks.digest(valueSha256);
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
