package com.thedndpub.data.dto.magicvariant;

import com.thedndpub.data.dto.item.ItemVariantMatchData;
import com.thedndpub.data.dte.magicvariant.MagicVariantRequirementDte;

import java.util.List;
import java.util.Locale;

public final class MagicVariantMatcher {
    private MagicVariantMatcher() {
    }

    public static boolean matches(MagicVariantRequirementDte requirement, ItemVariantMatchData item) {
        return matchesText(requirement.type(), item.typeCodes(), item.alternateTypeCode())
                && matchesText(requirement.source(), values(item.source()), null)
                && matchesExactNames(values(requirement.name()), item.name())
                && matchesAny(values(requirement.property()), item.propertyCodes())
                && matchesText(requirement.weaponCategory(), values(item.weaponCategory()), null)
                && matchesText(requirement.dmgType(), values(item.damageType()), null)
                && matchesText(requirement.scfType(), values(item.spellcastingFocusType()), null)
                && matchesFlag(requirement.weapon(), item.weapon())
                && matchesFlag(requirement.armor(), item.armor())
                && matchesFlag(requirement.arrow(), item.arrow())
                && matchesFlag(requirement.axe(), item.axe())
                && matchesFlag(requirement.bolt(), item.bolt())
                && matchesFlag(requirement.bow(), item.bow())
                && matchesFlag(requirement.crossbow(), item.crossbow())
                && matchesFlag(requirement.net(), item.net())
                && matchesFlag(requirement.polearm(), item.polearm())
                && matchesFlag(requirement.spear(), item.spear())
                && matchesFlag(requirement.sword(), item.sword())
                && matchesFlag(requirement.bulletFirearm(), item.bulletFirearm())
                && matchesFlag(requirement.cellEnergy(), item.cellEnergy());
    }

    public static boolean matchesAnyRequirement(
            List<MagicVariantRequirementDte> requirements,
            ItemVariantMatchData item) {
        return requirements != null && requirements.stream().anyMatch(requirement -> matches(requirement, item));
    }

    public static boolean isExcluded(
            MagicVariantRequirementDte exclusion,
            ItemVariantMatchData item) {
        if (exclusion == null) {
            return false;
        }
        return matchesSingleText(exclusion.type(), item.typeCodes(), item.alternateTypeCode())
                || matchesSingleText(exclusion.source(), values(item.source()), null)
                || !values(exclusion.name()).isEmpty() && matchesExactNames(values(exclusion.name()), item.name())
                || excludesAny(values(exclusion.property()), item.propertyCodes())
                || matchesSingleText(exclusion.weaponCategory(), values(item.weaponCategory()), null)
                || matchesSingleText(exclusion.dmgType(), values(item.damageType()), null)
                || matchesSingleText(exclusion.scfType(), values(item.spellcastingFocusType()), null)
                || excludesFlag(exclusion.weapon(), item.weapon())
                || excludesFlag(exclusion.armor(), item.armor())
                || excludesFlag(exclusion.arrow(), item.arrow())
                || excludesFlag(exclusion.axe(), item.axe())
                || excludesFlag(exclusion.bolt(), item.bolt())
                || excludesFlag(exclusion.bow(), item.bow())
                || excludesFlag(exclusion.crossbow(), item.crossbow())
                || excludesFlag(exclusion.net(), item.net())
                || excludesFlag(exclusion.polearm(), item.polearm())
                || excludesFlag(exclusion.spear(), item.spear())
                || excludesFlag(exclusion.sword(), item.sword())
                || excludesFlag(exclusion.bulletFirearm(), item.bulletFirearm())
                || excludesFlag(exclusion.cellEnergy(), item.cellEnergy());
    }

    private static boolean matchesText(String expected, List<String> candidates, String alternate) {
        return expected == null || matchesSingleText(expected, candidates, alternate);
    }

    private static boolean matchesSingleText(String expected, List<String> candidates, String alternate) {
        if (expected == null) {
            return false;
        }
        if (candidates != null && candidates.stream().anyMatch(candidate -> equalsIgnoreCase(expected, candidate))) {
            return true;
        }
        return equalsIgnoreCase(expected, alternate);
    }

    private static boolean matchesExactNames(List<String> expected, String actual) {
        return expected == null || expected.isEmpty()
                || expected.stream().anyMatch(value -> equalsIgnoreCase(value, actual));
    }

    private static boolean matchesAny(List<String> expected, List<String> actual) {
        return expected == null || expected.isEmpty()
                || expected.stream().anyMatch(value -> actual.stream().anyMatch(item -> equalsIgnoreCase(value, item)));
    }

    private static boolean matchesFlag(Boolean expected, Boolean actual) {
        return expected == null || expected.equals(actual);
    }

    private static boolean excludesFlag(Boolean expected, Boolean actual) {
        return expected != null && expected.equals(actual);
    }

    private static boolean excludesAny(List<String> expected, List<String> actual) {
        return expected != null && !expected.isEmpty()
                && expected.stream().anyMatch(value -> actual.stream().anyMatch(item -> equalsIgnoreCase(value, item)));
    }

    private static List<String> values(String value) {
        return value == null ? List.of() : List.of(value);
    }

    private static List<String> values(com.thedndpub.data.dte.magicvariant.MagicVariantStringValuesDte values) {
        return values == null ? List.of() : values.values();
    }

    private static boolean equalsIgnoreCase(String expected, String actual) {
        return actual != null && expected.trim().toLowerCase(Locale.ROOT)
                .equals(actual.trim().toLowerCase(Locale.ROOT));
    }
}
