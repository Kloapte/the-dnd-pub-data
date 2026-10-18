package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.MechanicsDocument;

/** A deterministic, source-bounded extraction adapter for a set of authored Spell owners. */
interface SpellMechanicsPattern {
    String patternId();

    boolean supports(SpellDefinition spell);

    MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original);
}
