package com.thedndpub.data.dte.race;

import java.util.List;

public record RaceSkillToolLanguageGrantDte(List<RaceSkillToolLanguageChoiceDte> choose) {
    public RaceSkillToolLanguageGrantDte {
        choose = choose == null ? List.of() : List.copyOf(choose);
    }
}
