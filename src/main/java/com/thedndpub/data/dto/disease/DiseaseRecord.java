package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.effects.DiseaseEffect;
import com.thedndpub.data.dto.disease.progression.DiseaseProgression;
import com.thedndpub.data.dto.disease.recovery.DiseaseRecovery;
import com.thedndpub.data.dto.disease.symptoms.DiseaseSymptom;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseRecord(
        String id,
        String name,
        DiseaseSourceMetadata sourceMetadata,
        String classification,
        DiseaseInfection infection,
        DiseaseInterval incubation,
        List<DiseaseSymptom> symptoms,
        DiseaseApplicability applicability,
        List<DiseaseEffect> effects,
        List<DiseaseProgression> progression,
        List<DiseaseRecovery> recovery,
        DiseaseEffectProfile effectProfile,
        List<DiseaseReference> references,
        List<DiseaseConditionGatedRestriction> conditionGatedRestrictions,
        DiseaseTermination termination,
        List<String> targetTraits) {

    public DiseaseRecord {
        symptoms = symptoms == null ? List.of() : List.copyOf(symptoms);
        effects = effects == null ? List.of() : List.copyOf(effects);
        progression = progression == null ? List.of() : List.copyOf(progression);
        recovery = recovery == null ? List.of() : List.copyOf(recovery);
        references = references == null ? List.of() : List.copyOf(references);
        conditionGatedRestrictions = conditionGatedRestrictions == null
                ? List.of()
                : List.copyOf(conditionGatedRestrictions);
        targetTraits = targetTraits == null ? List.of() : List.copyOf(targetTraits);
    }
}
