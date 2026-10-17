package com.thedndpub.data.dto.spell;

import java.util.Objects;

public record SpellMaterialDetails(String text, Integer cost, SpellConsume consume) implements SpellMaterial {
    public SpellMaterialDetails {
        Objects.requireNonNull(text, "text");
    }
}
