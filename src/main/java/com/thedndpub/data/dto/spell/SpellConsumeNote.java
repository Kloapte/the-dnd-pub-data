package com.thedndpub.data.dto.spell;

import java.util.Objects;

public record SpellConsumeNote(String note) implements SpellConsume {
    public SpellConsumeNote {
        Objects.requireNonNull(note, "note");
    }
}
