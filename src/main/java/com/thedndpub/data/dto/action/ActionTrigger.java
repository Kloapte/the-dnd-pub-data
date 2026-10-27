package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionExcludedMovementSource;
import com.thedndpub.data.dto.action.enums.ActionMovementOrigin;
import com.thedndpub.data.dto.action.enums.ActionTriggerKind;
import com.thedndpub.data.dto.action.enums.ActionTriggerTarget;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(using = ActionTriggerDeserializer.class)
public record ActionTrigger(
        ActionTriggerKind kind,
        ActionActor actor,
        ActionTriggerTarget target,
        Boolean requiresVisibility,
        Boolean requiresHostility,
        List<ActionMovementOrigin> movementOrigins,
        List<ActionExcludedMovementSource> excludedMovementSources,
        String resolutionTiming,
        String volume) {

    public ActionTrigger {
        movementOrigins = movementOrigins == null ? List.of() : List.copyOf(movementOrigins);
        excludedMovementSources = excludedMovementSources == null ? List.of() : List.copyOf(excludedMovementSources);
        validateContract(kind, actor, target, requiresVisibility, requiresHostility,
                movementOrigins, excludedMovementSources, resolutionTiming, volume);
    }

    private static void validateContract(ActionTriggerKind kind, ActionActor actor, ActionTriggerTarget target,
            Boolean requiresVisibility, Boolean requiresHostility, List<ActionMovementOrigin> movementOrigins,
            List<ActionExcludedMovementSource> excludedMovementSources, String resolutionTiming, String volume) {
        Objects.requireNonNull(kind, "Action trigger kind is required");
        switch (kind) {
            case SPELL_CASTING_OBSERVED -> requireActorAndTarget(kind, actor, target,
                    ActionActor.OBSERVER, ActionTriggerTarget.SPELL_CASTER,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case SPELL_DEFINED_REACTION -> requireActorAndNoTarget(kind, actor, target,
                    ActionActor.SPELL_CASTER, requiresVisibility, requiresHostility,
                    movementOrigins, excludedMovementSources, resolutionTiming, volume);
            case MELEE_ATTACK_MADE -> requireActorAndTarget(kind, actor, target,
                    ActionActor.ATTACKER, ActionTriggerTarget.CREATURE,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case OPPORTUNITY_ATTACK -> requireActorAndTarget(kind, actor, target,
                    ActionActor.ATTACKER, ActionTriggerTarget.MARKED_TARGET,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case ATTACK_AGAINST_AFFECTED -> requireActorAndTarget(
                    kind, actor, target, ActionActor.ATTACKER, ActionTriggerTarget.AFFECTED_CREATURE,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case SAVING_THROW_BY_AFFECTED -> requireActorAndTarget(
                    kind, actor, target, ActionActor.AFFECTED_CREATURE, ActionTriggerTarget.AFFECTED_CREATURE,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case CREATURE_LEAVES_REACH -> {
                requireActorAndTargetIdentity(kind, actor, target,
                        ActionActor.MOVING_CREATURE, ActionTriggerTarget.OPPORTUNITY_ATTACKER);
                requireNoSoundVolume(kind, volume);
                if (resolutionTiming != null && !resolutionTiming.equals("immediately-before-departure")) {
                    throw new IllegalArgumentException("Invalid creature-leaves-reach resolutionTiming");
                }
                if (movementOrigins.stream().distinct().count() != movementOrigins.size()
                        || excludedMovementSources.stream().distinct().count() != excludedMovementSources.size()) {
                    throw new IllegalArgumentException("Movement trigger lists must not contain duplicates");
                }
            }
            case PERCEIVABLE_CIRCUMSTANCE -> requireActorAndNoTarget(kind, actor, target, ActionActor.OBSERVER,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources,
                    resolutionTiming, volume);
            case GRAPPLER_RELEASES -> requireActorAndTarget(kind, actor, target,
                    ActionActor.GRAPPLER, ActionTriggerTarget.GRAPPLED_CREATURE,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case HIDDEN_CREATURE_MAKES_SOUND -> {
                requireActorAndTargetIdentity(kind, actor, target,
                        ActionActor.HIDDEN_CREATURE, ActionTriggerTarget.OBSERVER);
                if (!"louder-than-whisper".equals(volume)) {
                    throw new IllegalArgumentException(
                            "Hidden-creature sound trigger requires louder-than-whisper volume");
                }
                requireNoMovementDetails(kind, requiresVisibility, requiresHostility,
                        movementOrigins, excludedMovementSources, resolutionTiming);
            }
            case ENEMY_FINDS_HIDDEN_CREATURE -> requireActorAndTarget(
                    kind, actor, target, ActionActor.ENEMY, ActionTriggerTarget.HIDDEN_CREATURE,
                    requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources, resolutionTiming,
                    volume);
            case HIDDEN_CREATURE_MAKES_ATTACK_ROLL, HIDDEN_CREATURE_CASTS_VERBAL_SPELL ->
                    requireActorAndNoTarget(kind, actor, target, ActionActor.HIDDEN_CREATURE,
                            requiresVisibility, requiresHostility, movementOrigins, excludedMovementSources,
                            resolutionTiming, volume);
        }
    }

    private static void requireActorAndTarget(ActionTriggerKind kind, ActionActor actor, ActionTriggerTarget target,
            ActionActor expectedActor, ActionTriggerTarget expectedTarget, Boolean requiresVisibility,
            Boolean requiresHostility, List<ActionMovementOrigin> movementOrigins,
            List<ActionExcludedMovementSource> excludedMovementSources, String resolutionTiming, String volume) {
        requireActorAndTargetIdentity(kind, actor, target, expectedActor, expectedTarget);
        requireNoMovementDetails(kind, requiresVisibility, requiresHostility,
                movementOrigins, excludedMovementSources, resolutionTiming);
        requireNoSoundVolume(kind, volume);
    }

    private static void requireActorAndTargetIdentity(ActionTriggerKind kind, ActionActor actor,
            ActionTriggerTarget target, ActionActor expectedActor, ActionTriggerTarget expectedTarget) {
        requireActor(actor, kind, expectedActor);
        if (target != expectedTarget) {
            throw new IllegalArgumentException("Action trigger " + kind + " requires target " + expectedTarget);
        }
    }

    private static void requireActorAndNoTarget(ActionTriggerKind kind, ActionActor actor,
            ActionTriggerTarget target, ActionActor expectedActor, Boolean requiresVisibility,
            Boolean requiresHostility, List<ActionMovementOrigin> movementOrigins,
            List<ActionExcludedMovementSource> excludedMovementSources, String resolutionTiming, String volume) {
        requireActor(actor, kind, expectedActor);
        if (target != null) {
            throw new IllegalArgumentException("Action trigger " + kind + " does not allow a target");
        }
        requireNoMovementDetails(kind, requiresVisibility, requiresHostility,
                movementOrigins, excludedMovementSources, resolutionTiming);
        requireNoSoundVolume(kind, volume);
    }

    private static void requireActor(ActionActor actor, ActionTriggerKind kind, ActionActor expectedActor) {
        if (actor != expectedActor) {
            throw new IllegalArgumentException("Action trigger " + kind + " requires actor " + expectedActor);
        }
    }

    private static void requireNoMovementDetails(ActionTriggerKind kind, Boolean requiresVisibility,
            Boolean requiresHostility, List<ActionMovementOrigin> movementOrigins,
            List<ActionExcludedMovementSource> excludedMovementSources, String resolutionTiming) {
        if (requiresVisibility != null || requiresHostility != null || !movementOrigins.isEmpty()
                || !excludedMovementSources.isEmpty() || resolutionTiming != null) {
            throw new IllegalArgumentException("Action trigger " + kind + " does not allow movement details");
        }
    }

    private static void requireNoSoundVolume(ActionTriggerKind kind, String volume) {
        if (volume != null) {
            throw new IllegalArgumentException("Action trigger " + kind + " does not allow volume");
        }
    }
}
