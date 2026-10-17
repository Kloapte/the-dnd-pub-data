package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.feat.FeatDte;
import com.thedndpub.data.dte.feat.ExpandedFeatDte;
import com.thedndpub.data.dte.feat.FeatLevelPrerequisiteDte;
import com.thedndpub.data.dte.feat.FeatSimpleLevelPrerequisiteDte;
import com.thedndpub.data.dte.feat.FeatClassLevelPrerequisiteDte;
import com.thedndpub.data.dte.feat.FeatAdditionalSpellDte;
import com.thedndpub.data.dte.feat.FeatSpellAbilityNameDte;
import com.thedndpub.data.dte.feat.FeatSpellAbilityChoiceDte;
import com.thedndpub.data.dte.feat.FeatSpellGrantNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellTextNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellArrayNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellObjectNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellCountNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellFlagNodeDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public final class FeatMapper {
    public FeatDefinition map(FeatDte source) {
        return map(new ExpandedFeatDte(source, null, null, null, null));
    }

    public FeatDefinition map(ExpandedFeatDte expanded) {
        FeatDte source = expanded.definition();
        if (source.name() == null || source.source() == null) {
            throw new IllegalArgumentException("Feat name and source are required");
        }
        List<FeatReferenceDto> reprints = parseReferences(source.reprintedAs());
        return new FeatDefinition(
                idFor(source.name(), source.source()),
                source.name(),
                new SourceReference(new SourceIdentity(source.source()), source.page()),
                source.category(),
                mapPrerequisites(source),
                mapAbilityIncreases(source),
                source.additionalSources() == null ? List.of() : source.additionalSources(),
                reprints,
                source.repeatable(),
                mapAdditionalSpells(source.additionalSpells()),
                source.entries() == null ? List.of() : source.entries(),
                expanded.isVariant(),
                expanded.isVariant() ? idFor(expanded.parentName(), expanded.parentSource()) : null,
                FeatMechanicsMapper.map(source),
                mapStructuredProficiencyPrerequisites(source));
    }

    public FeatId idFor(String name, String source) {
        if (name == null || source == null) {
            throw new IllegalArgumentException("Feat name and source are required for an ID");
        }
        return new FeatId("feat:" + slug(source) + ":" + slug(name));
    }

    private static List<FeatAdditionalSpell> mapAdditionalSpells(List<FeatAdditionalSpellDte> spells) {
        if (spells == null) {
            return List.of();
        }
        return spells.stream().map(spell -> new FeatAdditionalSpell(
                spell.name(), spell.ability() instanceof FeatSpellAbilityNameDte named ? named.ability() : null,
                spell.ability() instanceof FeatSpellAbilityChoiceDte choice ? choice.choose() : List.of(),
                mapSpellNode(spell.prepared()),
                mapSpellNode(spell.known()), mapSpellNode(spell.innate()), mapSpellNode(spell.expanded())))
                .toList();
    }
    private static FeatSpellGrantNode mapSpellNode(FeatSpellGrantNodeDte node) {
        if (node instanceof FeatSpellTextNodeDte text) {
            return new FeatSpellTextNode(text.value());
        }
        if (node instanceof FeatSpellCountNodeDte count) {
            return new FeatSpellCountNode(count.value());
        }
        if (node instanceof FeatSpellFlagNodeDte flag) {
            return new FeatSpellFlagNode(flag.value());
        }
        if (node instanceof FeatSpellArrayNodeDte array) {
            return new FeatSpellArrayNode(array.values().stream().map(FeatMapper::mapSpellNode).toList());
        }
        if (node instanceof FeatSpellObjectNodeDte object) {
            java.util.Map<String, FeatSpellGrantNode> values = new java.util.TreeMap<>();
            object.values().forEach((key, value) -> values.put(key, mapSpellNode(value)));
            return new FeatSpellObjectNode(values);
        }
        return null;
    }

    private static List<FeatPrerequisiteDto> mapPrerequisites(FeatDte source) {
        if (source.prerequisite() == null) {
            return List.of();
        }
        return source.prerequisite().stream().map(group -> {
            FeatLevelRequirementDto level = mapLevel(group.level());
            return new FeatPrerequisiteDto(
                    group.ability() == null ? List.of() : group.ability().stream()
                            .map(value -> new FeatAbilityThresholdDto(
                                    value.str(), value.dex(), value.con(), value.intScore(), value.wis(), value.cha()))
                            .toList(),
                    group.background() == null ? List.of() : group.background().stream()
                            .map(value -> value.name()).toList(),
                    group.campaign(), group.exclusiveFeatCategory(), group.feat(), group.featCategory(), group.feature(),
                    level,
                    group.proficiency() == null ? List.of() : group.proficiency().stream()
                            .map(value -> String.join(";", java.util.stream.Stream.of(
                                    value.armor(), value.weapon(), value.weaponGroup())
                                    .filter(java.util.Objects::nonNull).toList()))
                            .toList(),
                    group.race() == null ? List.of() : group.race().stream()
                            .map(value -> value.subrace() == null ? value.name() : value.name() + ":" + value.subrace())
                            .toList(),
                    group.spellcasting(), group.spellcasting2020(), group.spellcastingFeature());
        }).toList();
    }

    private static List<FeatPrerequisiteProficiencyGroup> mapStructuredProficiencyPrerequisites(FeatDte source) {
        if (source.prerequisite() == null) {
            return null;
        }

        List<FeatPrerequisiteProficiencyGroup> groups = new ArrayList<>();
        for (int alternativeIndex = 0; alternativeIndex < source.prerequisite().size(); alternativeIndex++) {
            var prerequisite = source.prerequisite().get(alternativeIndex);
            if (prerequisite.proficiency() == null) {
                continue;
            }

            List<FeatProficiencyPrerequisite> requirements = prerequisite.proficiency().stream()
                    .map(value -> new FeatProficiencyPrerequisite(
                            value.armor(), value.weapon(), value.weaponGroup()))
                    .toList();
            groups.add(new FeatPrerequisiteProficiencyGroup(alternativeIndex, requirements));
        }
        return groups.isEmpty() ? null : List.copyOf(groups);
    }

    private static FeatLevelRequirementDto mapLevel(FeatLevelPrerequisiteDte level) {
        if (level instanceof FeatSimpleLevelPrerequisiteDte simple) {
            return new FeatLevelRequirementDto(simple.level(), null, null);
        }
        if (level instanceof FeatClassLevelPrerequisiteDte classLevel) {
            return new FeatLevelRequirementDto(classLevel.level(),
                    classLevel.classRequirement().name(), classLevel.classRequirement().visible());
        }
        return null;
    }

    private static List<FeatAbilityIncreaseDto> mapAbilityIncreases(FeatDte source) {
        if (source.ability() == null) {
            return List.of();
        }
        return source.ability().stream().map(value -> new FeatAbilityIncreaseDto(
                value.str(), value.dex(), value.con(), value.intScore(), value.wis(), value.cha(),
                value.choose() == null ? null : new FeatAbilityChoiceDto(
                        value.choose().from(), value.choose().amount(), value.choose().count()),
                value.max())).toList();
    }

    private static List<FeatReferenceDto> parseReferences(List<String> references) {
        if (references == null) {
            return List.of();
        }
        List<FeatReferenceDto> parsed = new ArrayList<>();
        for (String value : references) {
            String[] parts = value.split("\\|", -1);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalArgumentException("Invalid feat reprint reference: " + value);
            }
            parsed.add(new FeatReferenceDto(parts[0], new SourceIdentity(parts[1])));
        }
        return List.copyOf(parsed);
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return normalized;
    }
}
