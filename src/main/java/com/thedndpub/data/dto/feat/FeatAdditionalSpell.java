package com.thedndpub.data.dto.feat;

import java.util.List;

/** Normalized source grant; spell selections remain unresolved source identities or query values. */
public record FeatAdditionalSpell(
        String name,
        String ability,
        List<String> abilityChoices,
        FeatSpellGrantNode prepared,
        FeatSpellGrantNode known,
        FeatSpellGrantNode innate,
        FeatSpellGrantNode expanded) {
    public FeatAdditionalSpell {
        abilityChoices = abilityChoices == null ? List.of() : List.copyOf(abilityChoices);
    }
}
