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
            case ConditionOnSaveOutcome outcome -> outcome.evidenceIds();
            case ConditionEndsOnSaveSuccess ending -> ending.evidenceIds();
            case ConditionEndsWithSpell ending -> ending.evidenceIds();
            case RequiresConcentration concentration -> concentration.evidenceIds();
            case IncludesTriggeringAttack inclusion -> inclusion.evidenceIds();
            case ModifierOnSaveOutcome outcome -> outcome.evidenceIds();
            case AppliesWhen applies -> applies.evidenceIds();
        };
    }

    static List<MechanicRevisionReference> references(MechanicRecord record) {
        List<MechanicRevisionReference> references = new ArrayList<>();
        for (MechanicContextBinding binding : record.context().bindings()) {
            if (binding instanceof ActivatedBy activation) {
                references.add(activation.activation());
            } else if (binding instanceof AppliesOnSaveOutcome outcome) {
                references.add(outcome.savingThrow());
            } else if (binding instanceof ConditionOnSaveOutcome outcome) {
                references.add(outcome.savingThrow());
            } else if (binding instanceof ConditionEndsOnSaveSuccess ending) {
                references.add(ending.savingThrow());
            } else if (binding instanceof ModifierOnSaveOutcome outcome) {
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
        if (record.payload() instanceof ConditionChangeMechanic condition && condition.choiceGroup() != null) {
            references.add(condition.choiceGroup());
        }
        return references;
    }
}
