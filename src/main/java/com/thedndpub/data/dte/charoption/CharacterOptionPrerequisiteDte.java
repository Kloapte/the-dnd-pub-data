package com.thedndpub.data.dte.charoption;

import java.util.List;

public record CharacterOptionPrerequisiteDte(List<CharacterOptionRaceRequirementDte> race, String note) {
    public CharacterOptionPrerequisiteDte {
        race = race == null ? null : List.copyOf(race);
    }
}
