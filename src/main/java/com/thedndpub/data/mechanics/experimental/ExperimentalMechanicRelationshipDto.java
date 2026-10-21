package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalMechanicRelationshipDto(
        String id,
        MechanicRelationshipType type,
        SpellId ownerId,
        SourceArtifactRevision sourceRevision,
        String sourceMechanicId,
        String targetMechanicId,
        List<SaveDamageOutcomeRule> outcomes,
        List<String> evidenceIds,
        List<AttackDamageOutcomeRule> attackOutcomes,
        ScalingRelationshipRule scalingRule,
        List<ConditionSaveOutcomeRule> conditionOutcomes,
        ConditionTerminationRule terminationRule) {
    public ExperimentalMechanicRelationshipDto {
        ModelChecks.text(id, "id");
        ModelChecks.required(type, "type");
        ModelChecks.required(ownerId, "ownerId");
        ModelChecks.required(sourceRevision, "sourceRevision");
        ModelChecks.text(sourceMechanicId, "sourceMechanicId");
        ModelChecks.text(targetMechanicId, "targetMechanicId");
        ModelChecks.require(!sourceMechanicId.equals(targetMechanicId), "Relationship endpoints must differ");
        outcomes = ModelChecks.list(outcomes);
        attackOutcomes = ModelChecks.list(attackOutcomes);
        conditionOutcomes = ModelChecks.list(conditionOutcomes);
        evidenceIds = evidenceIds.stream().distinct().sorted().toList();
        if (type == MechanicRelationshipType.SAVING_THROW_DAMAGE_OUTCOME) {
            ModelChecks.require(!outcomes.isEmpty() && attackOutcomes.isEmpty(),
                    "A save/damage relationship requires only explicit save outcomes");
            ModelChecks.require(conditionOutcomes.isEmpty() && scalingRule == null && terminationRule == null,
                    "A save/damage relationship cannot contain condition or scaling rules");
            ModelChecks.require(outcomes.stream().map(SaveDamageOutcomeRule::outcome).distinct().count()
                            == outcomes.size(),
                    "A save/damage relationship cannot define an outcome more than once");
        } else if (type == MechanicRelationshipType.ATTACK_DAMAGE_OUTCOME) {
            ModelChecks.require(!attackOutcomes.isEmpty() && outcomes.isEmpty(),
                    "An attack/damage relationship requires only explicit attack outcomes");
            ModelChecks.require(conditionOutcomes.isEmpty() && scalingRule == null && terminationRule == null,
                    "An attack/damage relationship cannot contain condition or scaling rules");
            ModelChecks.require(attackOutcomes.stream().map(AttackDamageOutcomeRule::outcome).distinct().count()
                            == attackOutcomes.size(),
                    "An attack/damage relationship cannot define an outcome more than once");
        } else if (type == MechanicRelationshipType.SCALING_APPLIES_TO) {
            ModelChecks.require(outcomes.isEmpty() && attackOutcomes.isEmpty() && scalingRule != null
                            && conditionOutcomes.isEmpty() && terminationRule == null,
                    "A scaling relationship requires only one scaling rule");
        } else if (type == MechanicRelationshipType.SAVE_CONDITION_OUTCOME) {
            ModelChecks.require(outcomes.isEmpty() && attackOutcomes.isEmpty() && scalingRule == null
                            && !conditionOutcomes.isEmpty() && terminationRule == null,
                    "A save/condition relationship requires explicit condition outcomes");
            ModelChecks.require(conditionOutcomes.stream().distinct().count() == conditionOutcomes.size(),
                    "A save/condition relationship cannot duplicate an outcome rule");
            ModelChecks.require(conditionOutcomes.stream().map(item -> item.outcome() + "|" + item.timing())
                            .distinct().count() == conditionOutcomes.size(),
                    "A save/condition relationship cannot assign contradictory effects to one outcome and timing");
        } else if (type == MechanicRelationshipType.CONDITION_DURATION) {
            ModelChecks.require(outcomes.isEmpty() && attackOutcomes.isEmpty() && scalingRule == null
                            && conditionOutcomes.isEmpty() && terminationRule == null,
                    "A condition/duration relationship carries only its typed endpoints");
        } else if (type == MechanicRelationshipType.CONDITION_TERMINATION) {
            ModelChecks.require(outcomes.isEmpty() && attackOutcomes.isEmpty() && scalingRule == null
                            && conditionOutcomes.isEmpty() && terminationRule != null,
                    "A condition termination relationship requires one termination rule");
        } else {
            ModelChecks.require(outcomes.isEmpty() && attackOutcomes.isEmpty() && scalingRule == null
                            && conditionOutcomes.isEmpty() && terminationRule == null,
                    "Relationship type has incompatible rule payloads");
        }
        ModelChecks.require(!evidenceIds.isEmpty(), "A mechanic relationship requires source evidence");
    }

    public ExperimentalMechanicRelationshipDto(
            String id,
            MechanicRelationshipType type,
            com.thedndpub.data.dto.spell.SpellId ownerId,
            com.thedndpub.data.mechanics.model.SourceArtifactRevision sourceRevision,
            String sourceMechanicId,
            String targetMechanicId,
            List<SaveDamageOutcomeRule> outcomes,
            List<String> evidenceIds) {
        this(id, type, ownerId, sourceRevision, sourceMechanicId, targetMechanicId, outcomes, evidenceIds,
                List.of(), null, List.of(), null);
    }

    public ExperimentalMechanicRelationshipDto(
            String id, MechanicRelationshipType type, SpellId ownerId, SourceArtifactRevision sourceRevision,
            String sourceMechanicId, String targetMechanicId, List<SaveDamageOutcomeRule> outcomes,
            List<String> evidenceIds, List<AttackDamageOutcomeRule> attackOutcomes) {
        this(id, type, ownerId, sourceRevision, sourceMechanicId, targetMechanicId, outcomes, evidenceIds,
                attackOutcomes, null, List.of(), null);
    }

    public ExperimentalMechanicRelationshipDto(
            String id, MechanicRelationshipType type, SpellId ownerId, SourceArtifactRevision sourceRevision,
            String sourceMechanicId, String targetMechanicId, List<SaveDamageOutcomeRule> outcomes,
            List<String> evidenceIds, List<AttackDamageOutcomeRule> attackOutcomes,
            ScalingRelationshipRule scalingRule) {
        this(id, type, ownerId, sourceRevision, sourceMechanicId, targetMechanicId, outcomes, evidenceIds,
                attackOutcomes, scalingRule, List.of(), null);
    }
}
