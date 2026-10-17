package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotation;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.SpellSyntaxAnnotation;
import com.thedndpub.data.importer.spell.SpellFormulaAnnotationAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/** Startup-loaded internal index for source formula syntax associated with existing Spell IDs. */
@Component
public final class SpellFormulaAnnotationCatalog {
    private final SpellFormulaAnnotationIndex index;

    public SpellFormulaAnnotationCatalog(ObjectMapper objectMapper, SpellCatalog spellCatalog) {
        index = new SpellFormulaAnnotationAdapter(objectMapper).adapt(spellCatalog.findAll());
    }

    public List<SpellFormulaAnnotation> findBySpellId(SpellId spellId) {
        Objects.requireNonNull(spellId, "spellId");
        return index.findBySpellId(spellId);
    }

    public List<SpellSyntaxAnnotation> findSyntaxBySpellId(SpellId spellId) {
        Objects.requireNonNull(spellId, "spellId");
        return index.findSyntaxBySpellId(spellId);
    }

    public SpellFormulaAnnotationIndex index() {
        return index;
    }
}
