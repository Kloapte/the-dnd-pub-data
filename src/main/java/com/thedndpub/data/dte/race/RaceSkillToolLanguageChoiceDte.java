package com.thedndpub.data.dte.race;

import java.util.List;

public record RaceSkillToolLanguageChoiceDte(List<String> from, Integer count) {
    public RaceSkillToolLanguageChoiceDte {
        from = from == null ? List.of() : List.copyOf(from);
    }
}
