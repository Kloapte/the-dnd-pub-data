package com.thedndpub.data.dte.bastion;

import java.util.List;

public record FacilityProficiencyDte(List<String> skill) {
    public FacilityProficiencyDte {
        skill = skill == null ? List.of() : List.copyOf(skill);
    }
}
