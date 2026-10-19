package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CasterSubject.class, name = "CASTER"),
        @JsonSubTypes.Type(value = SpellTargetSubject.class, name = "SPELL_TARGET"),
        @JsonSubTypes.Type(value = UnknownSubject.class, name = "UNKNOWN")
})
public sealed interface SubjectReference permits
        CasterSubject,
        SpellTargetSubject,
        UnknownSubject {

}
