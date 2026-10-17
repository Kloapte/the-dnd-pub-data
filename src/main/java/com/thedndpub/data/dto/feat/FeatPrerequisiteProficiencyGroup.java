package com.thedndpub.data.dto.feat;

import java.util.List;

/** Proficiency requirements attached to one authored prerequisite alternative. */
public record FeatPrerequisiteProficiencyGroup(
        int prerequisiteAlternativeIndex,
        List<FeatProficiencyPrerequisite> requirements) {
    public FeatPrerequisiteProficiencyGroup {
        requirements = requirements == null ? null : List.copyOf(requirements);
    }
}
