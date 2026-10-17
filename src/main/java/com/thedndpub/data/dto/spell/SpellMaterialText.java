package com.thedndpub.data.dto.spell;

import java.util.Objects;

public record SpellMaterialText(String text) implements SpellMaterial {
    public SpellMaterialText {
        Objects.requireNonNull(text, "text");
    }
}
