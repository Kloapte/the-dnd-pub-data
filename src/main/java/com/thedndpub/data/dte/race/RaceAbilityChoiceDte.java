package com.thedndpub.data.dte.race;

import java.util.List;

public record RaceAbilityChoiceDte(List<String> from, Integer count, Integer amount) {
    public RaceAbilityChoiceDte {
        from = from == null ? List.of() : List.copyOf(from);
    }
}
