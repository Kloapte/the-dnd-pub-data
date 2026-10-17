package com.thedndpub.data.dte.bastion;

import java.util.List;

public record FacilityPrerequisiteDte(
        List<String> membership,
        FacilitySpellcastingFocusDte spellcastingFocus,
        FacilityOtherSummaryDte otherSummary,
        List<FacilityExpertiseDte> expertise,
        List<FacilityProficiencyDte> proficiency) {
}
