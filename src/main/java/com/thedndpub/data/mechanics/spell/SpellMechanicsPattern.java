package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.MechanicsDocument;

/** A deterministic, source-bounded extraction adapter for a set of authored Spell owners. */
interface SpellMechanicsPattern {
    String patternId();

    boolean supports(SpellDefinition spell);

    /** Wider offline inventory applicability; production catalogs continue using {@link #supports}. */
    default boolean supportsForInventory(SpellDefinition spell) {
        return supports(spell);
    }

    MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original);

    /** Executes wider assess-only candidates without changing production matcher eligibility. */
    default MechanicsDocument extractForInventory(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        return extract(spell, source, original);
    }
}
