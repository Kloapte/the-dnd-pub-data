package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OtherSyntaxObservationReference(
        String id,
        SyntaxEvidence evidence,
        String tagName) implements MechanicsObservationReference {
    public OtherSyntaxObservationReference {
        ModelChecks.text(id, "id");
        ModelChecks.text(tagName, "tagName");
        ModelChecks.required(evidence, "evidence");
        ModelChecks.digest(evidence.location().owner().resourceSha256());
    }
}
