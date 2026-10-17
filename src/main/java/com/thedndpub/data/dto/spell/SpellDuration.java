package com.thedndpub.data.dto.spell;

import java.util.List;

public record SpellDuration(
        String type,
        Boolean concentration,
        SpellDurationAmount duration,
        List<String> ends) {
    public SpellDuration {
        ends = ends == null ? null : List.copyOf(ends);
    }
}
