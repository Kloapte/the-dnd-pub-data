package com.thedndpub.data.dto.charoption;

import java.util.List;

public record CharacterOptionPrerequisite(List<CharacterOptionRaceRequirement> races, String note) {
    public CharacterOptionPrerequisite {
        races = List.copyOf(races);
    }
}
