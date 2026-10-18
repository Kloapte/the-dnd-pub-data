package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.publication.spell.SpellPublicationIndex;
import com.thedndpub.data.publication.spell.SpellPublicationSourceLoader;
import com.thedndpub.data.publication.spell.SpellPublicationValidator;
import org.springframework.stereotype.Component;

/** Internal permission boundary. Public Spell mappers deliberately do not consume this index in P1D. */
@Component
public final class SpellPublicationCatalog {
    private final SpellPublicationIndex index;

    public SpellPublicationCatalog(
            ObjectMapper mapper,
            SourceRegistry sources,
            SpellCatalog spells,
            SpellFormulaAnnotationCatalog formulas) {
        index = new SpellPublicationValidator(sources).validate(
                spells.findAll(), formulas.index(), new SpellPublicationSourceLoader(mapper).load());
    }

    public SpellPublicationIndex index() {
        return index;
    }
}
