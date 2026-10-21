package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Optional caster action that resolves damage automatically against an established spell link. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionActivatedDamage(
        MechanicRevisionReference persistentEffect,
        DamageActivationActor actor,
        DamageActivationTiming timing,
        DamageActivationTarget target,
        DamageActivationResolution resolution,
        boolean optional) {
    public ActionActivatedDamage {
        ModelChecks.required(persistentEffect, "persistentEffect");
        ModelChecks.required(actor, "actor");
        ModelChecks.required(timing, "timing");
        ModelChecks.required(target, "target");
        ModelChecks.required(resolution, "resolution");
        ModelChecks.require(optional, "Action-activated Witch Bolt damage is optional");
    }

    public static ActionActivatedDamage witchBolt(MechanicRevisionReference effect) {
        return new ActionActivatedDamage(effect, DamageActivationActor.SPELL_CASTER,
                DamageActivationTiming.ACTION_ON_SUBSEQUENT_TURN,
                DamageActivationTarget.ESTABLISHED_LINK_TARGET,
                DamageActivationResolution.AUTOMATIC_WITHOUT_ATTACK_OR_SAVE, true);
    }
}
