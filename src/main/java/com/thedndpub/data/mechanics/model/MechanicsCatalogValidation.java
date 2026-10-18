package com.thedndpub.data.mechanics.model;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.services.SourceRegistry;

import java.util.List;

/** Explicit existing-catalog joins for future Spell adapters; never creates interpretations or references. */
public final class MechanicsCatalogValidation {
    private MechanicsCatalogValidation() {
    }

    public static void validateSpell(
            MechanicsDocument document,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex index,
            SourceRegistry registry) {
        MechanicsSourceValidation.validate(document, registry);
        ModelChecks.require(document.owner().domain() == CatalogDomain.SPELL, "Expected a Spell mechanics owner");
        SpellDefinition owner = definitions.stream()
                .filter(spell -> spell.id().value().equals(document.owner().id()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown authoritative Spell owner"));
        ModelChecks.require(owner.source().equals(document.owner().source()), "Authoritative Spell provenance mismatch");
        for (MechanicsObservationReference observation : document.observations()) {
            if (observation instanceof FormulaObservationReference formula) {
                var values = index.bySpellId().get(formula.spellId());
                ModelChecks.require(values != null && formula.occurrenceIndex() < values.size(),
                        "Missing authoritative formula occurrence");
                var authored = values.get(formula.occurrenceIndex());
                ModelChecks.require(authored.observation().tag().evidence().equals(formula.evidence()),
                        "Formula occurrence evidence mismatch");
            }
        }
        for (MechanicRecord record : document.interpretations()) {
            if (record.payload() instanceof EffectDurationMechanic duration) {
                duration(duration.specification(), definitions);
            }
            for (MechanicContextBinding binding : record.context().bindings()) {
                if (binding instanceof AppliesForDuration timing) {
                    duration(timing.duration(), definitions);
                }
            }
        }
    }

    private static void duration(DurationSpecification duration, List<SpellDefinition> definitions) {
        if (duration instanceof DeclaredSpellDurationReference reference) {
            SpellDefinition spell = definitions.stream().filter(value -> value.id().equals(reference.spellId()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown duration owner"));
            ModelChecks.require(reference.durationIndex() < spell.durations().size(), "Missing declared duration index");
        }
    }
}
