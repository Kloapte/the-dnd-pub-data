package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DamageTraitDte(
        String type,
        String note,
        boolean conditional
) {
    public DamageTraitDte(String type, @Nullable String note) {
        this(type, note, note != null);
    }
}
