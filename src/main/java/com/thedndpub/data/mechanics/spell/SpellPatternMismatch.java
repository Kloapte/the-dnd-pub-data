package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.UnsupportedReason;

/** Controlled pattern abstention. Integrity/programming exceptions are deliberately not caught with this. */
final class SpellPatternMismatch extends RuntimeException {
    private final UnsupportedReason reason;
    private final String relativePointer;

    SpellPatternMismatch(UnsupportedReason reason, String relativePointer) {
        super("Spell source does not satisfy the bounded pattern");
        this.reason = reason;
        this.relativePointer = relativePointer;
    }

    UnsupportedReason reason() {
        return reason;
    }

    String relativePointer() {
        return relativePointer;
    }
}
