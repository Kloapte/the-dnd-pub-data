package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalMechanicsV2Dto(
        int apiVersion, String origin, String outcome, List<ExperimentalMechanicDto> mechanics,
        List<ExperimentalMechanicRelationshipDto> relationships, List<ExperimentalEvidenceDto> evidence,
        List<ExperimentalDiagnosticDto> diagnostics,
        ExperimentalMechanicsMetadataDto metadata) {
    public ExperimentalMechanicsV2Dto {
        mechanics = List.copyOf(mechanics);
        relationships = List.copyOf(relationships);
        evidence = List.copyOf(evidence);
        diagnostics = List.copyOf(diagnostics);
        if (apiVersion != 2 || !"EXPERIMENTAL".equals(origin)) throw new IllegalArgumentException("Invalid experimental projection");
        var mechanicsById = mechanics.stream().collect(java.util.stream.Collectors.toMap(
                ExperimentalMechanicDto::id, item -> item));
        var mechanicIds = mechanicsById.keySet();
        ModelChecks.require(mechanicIds.size() == mechanics.size(), "Experimental mechanic IDs must be unique");
        var evidenceIds = evidence.stream().map(ExperimentalEvidenceDto::id).collect(java.util.stream.Collectors.toSet());
        ModelChecks.require(evidenceIds.size() == evidence.size(), "Experimental evidence IDs must be unique");
        var relationshipIds = relationships.stream().map(ExperimentalMechanicRelationshipDto::id)
                .collect(java.util.stream.Collectors.toSet());
        ModelChecks.require(relationshipIds.size() == relationships.size(), "Experimental relationship IDs must be unique");
        var relationshipEndpoints = relationships.stream().map(item -> item.type() + "|"
                + item.sourceMechanicId() + "|" + item.targetMechanicId()).toList();
        ModelChecks.require(relationshipEndpoints.stream().distinct().count() == relationshipEndpoints.size(),
                "Experimental interpretations cannot duplicate a relationship between the same endpoints");
        for (ExperimentalMechanicRelationshipDto relationship : relationships) {
            ModelChecks.require(mechanicIds.contains(relationship.sourceMechanicId())
                            && mechanicIds.contains(relationship.targetMechanicId()),
                    "Experimental relationship endpoints must exist");
            ModelChecks.require(relationship.ownerId().value().startsWith("spell:"),
                    "Experimental relationship owner must be source-qualified");
            ModelChecks.require(metadata != null && relationship.ownerId().equals(metadata.ownerId()),
                    "Experimental relationship owner does not match interpretation owner");
            ModelChecks.require(relationship.sourceRevision().datasetVersion().equals(metadata.datasetVersion())
                            && relationship.sourceRevision().resourcePath().equals(metadata.sourceResource())
                            && relationship.sourceRevision().resourceSha256().equals(metadata.sourceSha256()),
                    "Experimental relationship source revision does not match interpretation source");
            ModelChecks.require(relationship.evidenceIds().stream().allMatch(evidenceIds::contains),
                    "Experimental relationship references missing evidence");
            var supportingMechanicIds = evidence.stream()
                    .filter(item -> relationship.evidenceIds().contains(item.id()))
                    .flatMap(item -> item.mechanicIds().stream())
                    .collect(java.util.stream.Collectors.toSet());
            ModelChecks.require(supportingMechanicIds.containsAll(
                            List.of(relationship.sourceMechanicId(), relationship.targetMechanicId())),
                    "Relationship evidence must support both endpoint mechanics");
            ModelChecks.require(relationship.sourceMechanicId().compareTo(relationship.targetMechanicId()) != 0,
                    "Experimental relationship endpoints must differ");
            if (relationship.type() == MechanicRelationshipType.SAVING_THROW_DAMAGE_OUTCOME) {
                ModelChecks.require(mechanicsById.get(relationship.sourceMechanicId()).properties()
                                instanceof com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic
                                && mechanicsById.get(relationship.targetMechanicId()).properties()
                                instanceof com.thedndpub.data.mechanics.model.DamageMechanic,
                        "Save/damage relationship endpoints have incompatible mechanic families");
            } else if (relationship.type() == MechanicRelationshipType.ATTACK_DAMAGE_OUTCOME) {
                ModelChecks.require(mechanicsById.get(relationship.sourceMechanicId()).properties()
                                instanceof com.thedndpub.data.mechanics.model.AttackRollMechanic
                                && mechanicsById.get(relationship.targetMechanicId()).properties()
                                instanceof com.thedndpub.data.mechanics.model.DamageMechanic,
                        "Attack/damage relationship endpoints have incompatible mechanic families");
            } else if (relationship.type() == MechanicRelationshipType.SCALING_APPLIES_TO) {
                var source = mechanicsById.get(relationship.sourceMechanicId()).properties();
                var target = mechanicsById.get(relationship.targetMechanicId()).properties();
                ModelChecks.require(source instanceof com.thedndpub.data.mechanics.model.GenericScalingMechanic,
                        "Scaling relationship source must be a typed scaling mechanic");
                var quantity = relationship.scalingRule().affectedQuantity();
                boolean targetValid = switch (quantity) {
                    case DAMAGE_AMOUNT -> target instanceof com.thedndpub.data.mechanics.model.DamageMechanic;
                    case HEALING_AMOUNT -> target instanceof com.thedndpub.data.mechanics.model.HealingMechanic;
                    case ATTACK_COUNT -> target instanceof com.thedndpub.data.mechanics.model.AttackRollMechanic;
                    case PROJECTILE_COUNT -> target instanceof com.thedndpub.data.mechanics.model.AttackRollMechanic
                            || target instanceof com.thedndpub.data.mechanics.model.DamageMechanic;
                    case TARGET_COUNT -> target instanceof com.thedndpub.data.mechanics.model.AttackRollMechanic
                            || target instanceof com.thedndpub.data.mechanics.model.DamageMechanic
                            || target instanceof com.thedndpub.data.mechanics.model.HealingMechanic;
                    case UNSPECIFIED_NUMERIC_QUANTITY -> false;
                };
                ModelChecks.require(targetValid, "Scaling relationship target does not match its affected quantity");
            } else if (relationship.type() == MechanicRelationshipType.SAVE_CONDITION_OUTCOME) {
                var source = mechanicsById.get(relationship.sourceMechanicId()).properties();
                var target = mechanicsById.get(relationship.targetMechanicId()).properties();
                ModelChecks.require(source instanceof com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic
                                && isConditionMechanic(target),
                        "Save/condition relationship endpoints have incompatible mechanic families");
            } else if (relationship.type() == MechanicRelationshipType.CONDITION_DURATION) {
                var source = mechanicsById.get(relationship.sourceMechanicId()).properties();
                var target = mechanicsById.get(relationship.targetMechanicId()).properties();
                ModelChecks.require(isConditionMechanic(source)
                                && target instanceof com.thedndpub.data.mechanics.model.EffectDurationMechanic,
                        "Condition/duration relationship endpoints have incompatible mechanic families");
            } else if (relationship.type() == MechanicRelationshipType.CONDITION_TERMINATION) {
                var source = mechanicsById.get(relationship.sourceMechanicId()).properties();
                var target = mechanicsById.get(relationship.targetMechanicId()).properties();
                boolean repeatedSave = source instanceof com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic
                        && isConditionMechanic(target)
                        && relationship.terminationRule().trigger()
                        == ConditionTerminationTrigger.REPEATED_SAVE_SUCCESS;
                boolean spellDuration = isConditionMechanic(source)
                        && target instanceof com.thedndpub.data.mechanics.model.EffectDurationMechanic
                        && relationship.terminationRule().trigger()
                        == ConditionTerminationTrigger.SPELL_DURATION_ENDS;
                boolean actionEvent = isConditionMechanic(source)
                        && target instanceof com.thedndpub.data.mechanics.model.ConditionTerminationEventMechanic
                        && relationship.terminationRule().trigger()
                        == ConditionTerminationTrigger.ACTION_SUCCESSFUL_CHECK;
                ModelChecks.require(repeatedSave || spellDuration || actionEvent,
                        "Condition termination endpoints do not match the declared termination trigger");
            }
        }
        validateAcyclicRelationships(relationships);
        for (ExperimentalEvidenceDto item : evidence) {
            ModelChecks.require(mechanicIds.containsAll(item.mechanicIds()), "Evidence references a missing mechanic");
            ModelChecks.require(relationshipIds.containsAll(item.relationshipIds()),
                    "Evidence references a missing relationship");
        }
        for (ExperimentalMechanicRelationshipDto relationship : relationships) {
            for (String evidenceId : relationship.evidenceIds()) {
                ExperimentalEvidenceDto item = evidence.stream().filter(candidate -> candidate.id().equals(evidenceId))
                        .findFirst().orElseThrow();
                ModelChecks.require(item.relationshipIds().contains(relationship.id()),
                        "Relationship evidence association is not reciprocal");
                ModelChecks.require(item.sourceRevision().equals(relationship.sourceRevision().datasetVersion())
                                && item.sourceResource().equals(relationship.sourceRevision().resourcePath())
                                && item.sourceSha256().equals(relationship.sourceRevision().resourceSha256()),
                        "Relationship evidence belongs to a stale or different source revision");
            }
        }
    }

    private static boolean isConditionMechanic(com.thedndpub.data.mechanics.model.MechanicPayload payload) {
        return payload instanceof com.thedndpub.data.mechanics.model.ConditionChangeMechanic
                || payload instanceof com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
    }

    private static void validateAcyclicRelationships(List<ExperimentalMechanicRelationshipDto> relationships) {
        java.util.Map<String, List<String>> edges = new java.util.HashMap<>();
        relationships.forEach(item -> edges.computeIfAbsent(item.sourceMechanicId(), ignored -> new java.util.ArrayList<>())
                .add(item.targetMechanicId()));
        java.util.Set<String> visiting = new java.util.HashSet<>();
        java.util.Set<String> visited = new java.util.HashSet<>();
        for (String mechanicId : edges.keySet()) {
            ModelChecks.require(!hasCycle(mechanicId, edges, visiting, visited),
                    "Experimental mechanic relationships must be acyclic");
        }
    }

    private static boolean hasCycle(String mechanicId, java.util.Map<String, List<String>> edges,
            java.util.Set<String> visiting, java.util.Set<String> visited) {
        if (visited.contains(mechanicId)) return false;
        if (!visiting.add(mechanicId)) return true;
        for (String target : edges.getOrDefault(mechanicId, List.of())) {
            if (hasCycle(target, edges, visiting, visited)) return true;
        }
        visiting.remove(mechanicId);
        visited.add(mechanicId);
        return false;
    }
}
