package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoEffectOutcome.class, name = "NO_EFFECT"),
        @JsonSubTypes.Type(value = ChangeConditionLevelOutcome.class, name = "CHANGE_CONDITION_LEVEL"),
        @JsonSubTypes.Type(value = DamageOutcome.class, name = "DAMAGE"),
        @JsonSubTypes.Type(value = StartSymptomOutcome.class, name = "START_SYMPTOM"),
        @JsonSubTypes.Type(value = EndSymptomOutcome.class, name = "END_SYMPTOM"),
        @JsonSubTypes.Type(value = RuleProcedureOutcome.class, name = "RULE_PROCEDURE_OUTCOME"),
        @JsonSubTypes.Type(value = EndDiseaseOutcome.class, name = "END_DISEASE"),
        @JsonSubTypes.Type(value = ApplyConditionOutcome.class, name = "APPLY_CONDITION"),
        @JsonSubTypes.Type(value = ChangeAbilityScoreOutcome.class, name = "CHANGE_ABILITY_SCORE"),
        @JsonSubTypes.Type(value = ChangeSpeedOutcome.class, name = "CHANGE_SPEED"),
        @JsonSubTypes.Type(value = TransformCreatureOutcome.class, name = "TRANSFORM_CREATURE"),
        @JsonSubTypes.Type(value = GrantDiseaseImmunityOutcome.class, name = "GRANT_DISEASE_IMMUNITY"),
        @JsonSubTypes.Type(value = VomitPreviousDayFoodOutcome.class, name = "VOMIT_PREVIOUS_DAY_FOOD"),
        @JsonSubTypes.Type(value = KillHostOutcome.class, name = "KILL_HOST")
})
public sealed interface DiseaseProgressionOutcome permits
        NoEffectOutcome,
        ChangeConditionLevelOutcome,
        DamageOutcome,
        StartSymptomOutcome,
        EndSymptomOutcome,
        RuleProcedureOutcome,
        EndDiseaseOutcome,
        ApplyConditionOutcome,
        ChangeAbilityScoreOutcome,
        ChangeSpeedOutcome,
        TransformCreatureOutcome,
        GrantDiseaseImmunityOutcome,
        VomitPreviousDayFoodOutcome,
        KillHostOutcome {
    String kind();
}
