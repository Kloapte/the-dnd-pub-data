package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SavingThrowBranch.class, name = "SAVING_THROW_BRANCH"),
        @JsonSubTypes.Type(value = TimedOutcomeBranch.class, name = "TIMED_OUTCOME_BRANCH")
})
public sealed interface DiseaseProgression permits SavingThrowBranch, TimedOutcomeBranch {
    String kind();
}
