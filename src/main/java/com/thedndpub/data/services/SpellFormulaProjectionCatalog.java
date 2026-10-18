package com.thedndpub.data.services;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.api.SpellFormulaProjectionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Startup-validated publication metadata; no syntax payload or source scanning is needed on lookup. */
@Component
public final class SpellFormulaProjectionCatalog {
    private final Map<SpellId, SpellFormulaProjectionStatus> statuses;

    @Autowired
    public SpellFormulaProjectionCatalog(SpellCatalog spells, SpellFormulaAnnotationCatalog annotations) {
        this(spells.findAll(), annotations.index());
    }

    public SpellFormulaProjectionCatalog(List<SpellDefinition> spells, SpellFormulaAnnotationIndex index) {
        Objects.requireNonNull(index, "index");
        Map<SpellId, SpellFormulaProjectionStatus> projection = new LinkedHashMap<>();
        for (SpellDefinition spell : spells) {
            // Unlike findBySpellId(), get() preserves the distinction between missing and observed empty.
            var observations = index.bySpellId().get(spell.id());
            if (observations == null) {
                throw new IllegalArgumentException("Formula index is missing Spell ID: " + spell.id().value());
            }
            SpellFormulaProjectionStatus status = observations.isEmpty()
                    ? SpellFormulaProjectionStatus.NONE_OBSERVED
                    : SpellFormulaProjectionStatus.WITHHELD;
            if (projection.putIfAbsent(spell.id(), status) != null) {
                throw new IllegalArgumentException("Duplicate Spell ID in formula projection: " + spell.id().value());
            }
        }
        if (!projection.keySet().equals(index.bySpellId().keySet())) {
            throw new IllegalArgumentException("Formula index contains owners outside the Spell catalog");
        }
        statuses = Collections.unmodifiableMap(projection);
    }

    public SpellFormulaProjectionStatus statusFor(SpellId spellId) {
        Objects.requireNonNull(spellId, "spellId");
        SpellFormulaProjectionStatus status = statuses.get(spellId);
        if (status == null) {
            throw new IllegalArgumentException("Formula projection is unavailable for Spell ID: " + spellId.value());
        }
        return status;
    }
}
