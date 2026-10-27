package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConditionLevelChangeSymptom.class, name = "CONDITION_LEVEL_CHANGE"),
        @JsonSubTypes.Type(value = TemporaryConditionSymptom.class, name = "TEMPORARY_CONDITION"),
        @JsonSubTypes.Type(value = SenseImpairmentSymptom.class, name = "SENSE_IMPAIRMENT"),
        @JsonSubTypes.Type(value = AbilityRollModifierSymptom.class, name = "ABILITY_ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = ConditionSymptom.class, name = "CONDITION"),
        @JsonSubTypes.Type(value = AttackRollModifierSymptom.class, name = "ATTACK_ROLL_MODIFIER")
})
public sealed interface DiseaseSymptom permits
        ConditionLevelChangeSymptom,
        TemporaryConditionSymptom,
        SenseImpairmentSymptom,
        AbilityRollModifierSymptom,
        ConditionSymptom,
        AttackRollModifierSymptom {
    String kind();
}
