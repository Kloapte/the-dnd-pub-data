package com.thedndpub.data.dto.spell;

import java.util.Objects;

public record SpellReprintReference(SpellId targetId, SpellIdentity target) {
    public SpellReprintReference {
        Objects.requireNonNull(targetId, "targetId");
        Objects.requireNonNull(target, "target");
    }
}
