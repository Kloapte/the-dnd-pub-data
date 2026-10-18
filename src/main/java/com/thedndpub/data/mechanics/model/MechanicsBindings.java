package com.thedndpub.data.mechanics.model;

import java.util.ArrayList;
import java.util.List;

final class MechanicsBindings {
    private MechanicsBindings() {
    }

    static List<String> evidenceIds(MechanicContextBinding binding) {
        return switch (binding) {
            case ActivatedBy activation -> activation.evidenceIds();
            case AppliesForDuration duration -> duration.evidenceIds();
            case AppliesOnSaveOutcome outcome -> outcome.evidenceIds();
            case IncludesTriggeringAttack inclusion -> inclusion.evidenceIds();
        };
    }

    static List<MechanicRevisionReference> references(MechanicRecord record) {
        List<MechanicRevisionReference> references = new ArrayList<>();
        for (MechanicContextBinding binding : record.context().bindings()) {
            if (binding instanceof ActivatedBy activation) {
                references.add(activation.activation());
            } else if (binding instanceof AppliesOnSaveOutcome outcome) {
                references.add(outcome.savingThrow());
            } else if (binding instanceof AppliesForDuration timing
                    && timing.duration() instanceof ReferencedDuration duration) {
                references.add(duration.mechanic());
            }
        }
        if (record.payload() instanceof EffectDurationMechanic duration
                && duration.specification() instanceof ReferencedDuration reference) {
            references.add(reference.mechanic());
        }
        return references;
    }
}
