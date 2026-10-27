package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AllAbilityScorePointsRestoredTermination.class,
                name = "ALL_LOST_ABILITY_SCORE_POINTS_RESTORED"),
        @JsonSubTypes.Type(value = ConditionLevelThresholdTermination.class, name = "CONDITION_LEVEL_AT_OR_BELOW")
})
public sealed interface DiseaseTermination
        permits AllAbilityScorePointsRestoredTermination, ConditionLevelThresholdTermination {
    String kind();
}
