package com.thedndpub.data.dto.spell.api;

/** Publication completeness of indexed formula syntax, not completeness of a Spell's effects. */
public enum SpellFormulaProjectionStatus {
    /** No formula observations in the bounded authored-source index; mechanics may still exist. */
    NONE_OBSERVED,
    /** Indexed formula observations exist, but no observation payload is published. */
    WITHHELD,
    /** Reserved for a separately approved, partially published observation payload. */
    PARTIAL,
    /** Reserved for a separately approved, completely published indexed observation payload. */
    COMPLETE
}
