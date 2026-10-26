package com.thedndpub.data.dto.corerules.sense;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SightCapability.class, name = "SIGHT"),
        @JsonSubTypes.Type(value = NonvisualPerception.class, name = "NONVISUAL_PERCEPTION"),
        @JsonSubTypes.Type(value = ConditionInteraction.class, name = "CONDITION_INTERACTION")
})
public sealed interface SenseCapability permits SightCapability, NonvisualPerception, ConditionInteraction {
    String kind();
}
