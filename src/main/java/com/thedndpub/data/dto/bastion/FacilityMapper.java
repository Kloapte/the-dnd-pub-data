package com.thedndpub.data.dto.bastion;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.bastion.FacilityDte;
import com.thedndpub.data.dte.bastion.FacilityExpertiseDte;
import com.thedndpub.data.dte.bastion.FacilityHirelingDte;
import com.thedndpub.data.dte.bastion.FacilityOtherSummaryDte;
import com.thedndpub.data.dte.bastion.FacilityPrerequisiteDte;
import com.thedndpub.data.dte.bastion.FacilityProficiencyDte;
import com.thedndpub.data.dte.bastion.FacilitySpellcastingFocusFlagDte;
import com.thedndpub.data.dte.bastion.FacilitySpellcastingFocusTypesDte;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public final class FacilityMapper {
    public FacilityDefinition map(FacilityDte source) {
        String id = "facility:" + source.source() + ":" + slug(source.name());
        return new FacilityDefinition(id, source.name(), new SourceReference(source.source(), source.page()),
                source.page(), source.facilityType(), source.level(),
                prerequisites(source.prerequisite()), source.space(), hirelings(source.hirelings()), source.orders(),
                source.entries(), source.hasFluffImages(), source.hasFluff());
    }

    private static List<FacilityPrerequisite> prerequisites(List<FacilityPrerequisiteDte> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream().map(value -> new FacilityPrerequisite(value.membership(), focus(value),
                otherSummary(value.otherSummary()), expertise(value), proficiency(value))).toList();
    }

    private static FacilitySpellcastingFocus focus(FacilityPrerequisiteDte source) {
        if (source.spellcastingFocus() instanceof FacilitySpellcastingFocusFlagDte flag) {
            return new FacilitySpellcastingFocusRequirement(flag.required());
        }
        if (source.spellcastingFocus() instanceof FacilitySpellcastingFocusTypesDte types) {
            return new FacilitySpellcastingFocusOptions(types.types());
        }
        return null;
    }

    private static FacilityOtherSummary otherSummary(FacilityOtherSummaryDte value) {
        return value == null ? null : new FacilityOtherSummary(value.entry(), value.entrySummary());
    }

    private static List<FacilityExpertise> expertise(FacilityPrerequisiteDte source) {
        return source.expertise() == null ? List.of()
                : source.expertise().stream().map(FacilityMapper::expertise).toList();
    }

    private static FacilityExpertise expertise(FacilityExpertiseDte value) {
        return new FacilityExpertise(value.skill());
    }

    private static List<FacilityProficiency> proficiency(FacilityPrerequisiteDte source) {
        return source.proficiency() == null ? List.of()
                : source.proficiency().stream().map(FacilityMapper::proficiency).toList();
    }

    private static FacilityProficiency proficiency(FacilityProficiencyDte value) {
        return new FacilityProficiency(value.skill());
    }

    private static List<FacilityHireling> hirelings(List<FacilityHirelingDte> source) {
        return source == null ? List.of() : source.stream()
                .map(value -> new FacilityHireling(value.exact(), value.min(), value.space())).toList();
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        String slug = normalized.replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            throw new IllegalArgumentException("Facility name cannot produce a stable ID: " + value);
        }
        return slug;
    }
}
