package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LongRestCompletedTrigger.class, name = "LONG_REST_COMPLETED"),
        @JsonSubTypes.Type(value = ElapsedTimeTrigger.class, name = "ELAPSED_TIME"),
        @JsonSubTypes.Type(value = DamageWhileConditionActiveTrigger.class, name = "DAMAGE_WHILE_CONDITION_ACTIVE"),
        @JsonSubTypes.Type(value = LongRestOrExcitementTrigger.class, name = "LONG_REST_OR_EXCITEMENT"),
        @JsonSubTypes.Type(value = RepeatingElapsedTimeTrigger.class, name = "REPEATING_ELAPSED_TIME"),
        @JsonSubTypes.Type(value = PartyDefeatedAndCreatureStabilizedTrigger.class,
                name = "PARTY_DEFEATED_AND_CREATURE_STABILIZED"),
        @JsonSubTypes.Type(value = DailyAtTrigger.class, name = "DAILY_AT"),
        @JsonSubTypes.Type(value = GreatStressTrigger.class, name = "GREAT_STRESS")
})
public sealed interface DiseaseProgressionTrigger permits
        LongRestCompletedTrigger,
        ElapsedTimeTrigger,
        DamageWhileConditionActiveTrigger,
        LongRestOrExcitementTrigger,
        RepeatingElapsedTimeTrigger,
        PartyDefeatedAndCreatureStabilizedTrigger,
        DailyAtTrigger,
        GreatStressTrigger {
    String kind();
}
