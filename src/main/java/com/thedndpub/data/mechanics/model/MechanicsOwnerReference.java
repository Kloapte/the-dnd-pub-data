package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.spell.SpellId;

import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsOwnerReference(
        CatalogDomain domain,
        String id,
        SourceReference source) {
    public MechanicsOwnerReference {
        ModelChecks.required(domain, "domain");
        ModelChecks.required(source, "source");
        ModelChecks.text(id, "id");
        String prefix = domain.name().toLowerCase(Locale.ROOT).replace('_', '-');
        ModelChecks.require(id.startsWith(prefix + ":"), "Owner ID does not match its catalog domain");
        if (domain == CatalogDomain.SPELL) {
            new SpellId(id);
            String authoredSource = source.identity().code().toLowerCase(Locale.ROOT);
            ModelChecks.require(id.startsWith("spell:" + authoredSource + ":"), "Spell owner source mismatch");
        }
    }
}
