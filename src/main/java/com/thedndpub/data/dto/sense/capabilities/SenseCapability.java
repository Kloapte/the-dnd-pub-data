package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SightCapability.class, name = "SIGHT"),
        @JsonSubTypes.Type(value = NonvisualPerceptionCapability.class, name = "NONVISUAL_PERCEPTION"),
        @JsonSubTypes.Type(value = ConditionInteractionCapability.class, name = "CONDITION_INTERACTION")
})
public sealed interface SenseCapability permits SightCapability, NonvisualPerceptionCapability,
        ConditionInteractionCapability {
}
