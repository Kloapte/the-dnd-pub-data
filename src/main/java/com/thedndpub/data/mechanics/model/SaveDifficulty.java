package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedSaveDifficulty.class, name = "FIXED"),
        @JsonSubTypes.Type(value = UnknownSaveDifficulty.class, name = "UNKNOWN")
})
public sealed interface SaveDifficulty permits
        FixedSaveDifficulty,
        UnknownSaveDifficulty {

}
