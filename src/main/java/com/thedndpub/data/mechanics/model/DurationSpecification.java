package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeclaredSpellDurationReference.class, name = "DECLARED_SPELL_DURATION"),
        @JsonSubTypes.Type(value = FixedDuration.class, name = "FIXED"),
        @JsonSubTypes.Type(value = UntilTurnBoundary.class, name = "UNTIL_TURN_BOUNDARY"),
        @JsonSubTypes.Type(value = ReferencedDuration.class, name = "MECHANIC_DURATION"),
        @JsonSubTypes.Type(value = UnknownDuration.class, name = "UNKNOWN")
})
public sealed interface DurationSpecification permits
        DeclaredSpellDurationReference,
        FixedDuration,
        UntilTurnBoundary,
        ReferencedDuration,
        UnknownDuration {

}
