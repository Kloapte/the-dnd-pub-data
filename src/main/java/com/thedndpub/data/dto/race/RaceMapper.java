package com.thedndpub.data.dto.race;

import com.thedndpub.data.dto.feat.FeatAdditionalSpellDto;
import com.thedndpub.data.dto.feat.FeatSpellAbilityDto;
import com.thedndpub.data.dto.feat.FeatSpellArrayDto;
import com.thedndpub.data.dto.feat.FeatSpellCountDto;
import com.thedndpub.data.dto.feat.FeatSpellFlagDto;
import com.thedndpub.data.dto.feat.FeatSpellGrantNodeDto;
import com.thedndpub.data.dto.feat.FeatSpellObjectDto;
import com.thedndpub.data.dto.feat.FeatSpellTextDto;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.background.BackgroundGrantValueDte;
import com.thedndpub.data.dte.feat.FeatChoiceResistanceDte;
import com.thedndpub.data.dte.feat.FeatAdditionalSpellDte;
import com.thedndpub.data.dte.feat.FeatNamedResistanceDte;
import com.thedndpub.data.dte.feat.FeatResistanceDte;
import com.thedndpub.data.dte.feat.FeatSpellAbilityDte;
import com.thedndpub.data.dte.feat.FeatSpellAbilityChoiceDte;
import com.thedndpub.data.dte.feat.FeatSpellAbilityNameDte;
import com.thedndpub.data.dte.feat.FeatSpellArrayNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellCountNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellFlagNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellGrantNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellObjectNodeDte;
import com.thedndpub.data.dte.feat.FeatSpellTextNodeDte;
import com.thedndpub.data.dte.race.RaceAbilityDte;
import com.thedndpub.data.dte.race.RaceAgeDte;
import com.thedndpub.data.dte.race.RaceDte;
import com.thedndpub.data.dte.race.RaceHeightAndWeightDte;
import com.thedndpub.data.dte.race.RaceLineageDte;
import com.thedndpub.data.dte.race.RaceReprintDte;
import com.thedndpub.data.dte.race.RaceSkillToolLanguageGrantDte;
import com.thedndpub.data.dte.race.RaceSourceReferenceDte;
import com.thedndpub.data.dte.race.RaceSpeedDte;
import com.thedndpub.data.dte.race.RaceSpeedValueDte;
import com.thedndpub.data.dte.race.SubraceDte;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public final class RaceMapper {
    public RaceDto toDto(RaceDefinition definition) {
        RaceMechanics mechanics = definition.mechanics();
        RaceDte raceRoot = definition.sourceData().race();
        SubraceDte subraceRoot = definition.sourceData().subrace();
        List<RaceSourceReferenceDte> additionalSources = raceRoot != null
                ? raceRoot.additionalSources()
                : List.of();
        List<RaceSourceReferenceDte> otherSources = raceRoot != null
                ? raceRoot.otherSources()
                : subraceRoot.otherSources();
        List<RaceReprintDte> reprints = raceRoot != null
                ? raceRoot.reprintedAs()
                : subraceRoot.reprintedAs();

        return new RaceDto(
                definition.id().value(),
                definition.name(),
                definition.source(),
                definition.kind(),
                parentReference(definition, subraceRoot),
                mechanics.edition(),
                mechanics.creatureTypes(),
                mechanics.creatureTypeTags(),
                mechanics.size(),
                movement(mechanics.speed()),
                mapAbilities(mechanics.ability()),
                mapGrantGroups(mechanics.skillProficiencies()),
                mapGrantGroups(mechanics.toolProficiencies()),
                mapGrantGroups(mechanics.weaponProficiencies()),
                mapGrantGroups(mechanics.armorProficiencies()),
                mapGrantGroups(mechanics.languageProficiencies()),
                mapCombinedGrants(mechanics.skillToolLanguageProficiencies()),
                mapGrantGroups(mechanics.featGrants()),
                mechanics.darkvision(),
                mechanics.blindsight(),
                mapResistances(mechanics.resistances(), mechanics.implementationResistances()),
                mechanics.immunities(),
                mechanics.vulnerabilities(),
                mechanics.conditionImmunities(),
                mapSpells(mechanics.additionalSpells()),
                definition.effectiveTraitNames(),
                mechanics.traitTags(),
                mechanics.aliases(),
                lineage(mechanics.lineage()),
                age(mechanics.age()),
                heightAndWeight(mechanics.heightAndWeight()),
                mapOverwrites(mechanics.overwrite()),
                mapSources(additionalSources),
                mapSources(otherSources),
                mapReprints(reprints), List.of());
    }

    public RaceSummaryDto toSummary(RaceDefinition definition) {
        RaceSpeedValueDte walking = definition.mechanics().speed() == null
                ? null
                : definition.mechanics().speed().walk();
        Integer walkingSpeed = walking == null || walking.sourceFlag() != null ? null : walking.feet();
        SubraceDte subrace = definition.sourceData().subrace();
        return new RaceSummaryDto(
                definition.id().value(),
                definition.name(),
                definition.source(),
                definition.kind(),
                parentReference(definition, subrace),
                definition.mechanics().size(),
                walkingSpeed,
                lineage(definition.mechanics().lineage()));
    }

    private static RaceParentReferenceDto parentReference(RaceDefinition definition, SubraceDte subrace) {
        if (definition.parentId() == null || subrace == null) {
            return null;
        }
        String parentName = subrace.raceName();
        String parentSource = subrace.raceSource();
        if (parentName == null && subrace.copy() != null) {
            parentName = subrace.copy().raceName();
            parentSource = subrace.copy().raceSource();
        }
        return new RaceParentReferenceDto(
                definition.parentId().value(), parentName, new SourceIdentity(parentSource));
    }

    private static List<RaceAbilityDto> mapAbilities(List<RaceAbilityDte> abilities) {
        return abilities.stream()
                .map(value -> new RaceAbilityDto(
                        value.str(),
                        value.dex(),
                        value.con(),
                        value.intScore(),
                        value.wis(),
                        value.cha(),
                        value.choice() == null
                                ? null
                                : new RaceAbilityChoiceDto(value.choice().from(),
                                        value.choice().count(), value.choice().amount())))
                .toList();
    }

    private static List<RaceGrantGroupDto> mapGrantGroups(List<BackgroundGrantGroupDte> groups) {
        return groups.stream()
                .map(group -> new RaceGrantGroupDto(group.grants().entrySet().stream()
                        .map(entry -> mapGrant(entry.getKey(), entry.getValue()))
                        .toList()))
                .toList();
    }

    private static RaceGrantDto mapGrant(String name, BackgroundGrantValueDte value) {
        var choice = value.choice();
        return new RaceGrantDto(
                name,
                value.granted(),
                value.count(),
                choice == null
                        ? null
                        : new RaceGrantChoiceDto(choice.count(), choice.from(), choice.categories(),
                                choice.fromFilter()));
    }

    private static List<RaceSkillToolLanguageGrantDto> mapCombinedGrants(
            List<RaceSkillToolLanguageGrantDte> grants) {
        return grants.stream()
                .map(grant -> new RaceSkillToolLanguageGrantDto(grant.choose().stream()
                        .map(choice -> new RaceSkillToolLanguageChoiceDto(choice.from(), choice.count()))
                        .toList()))
                .toList();
    }

    private static List<RaceResistanceDto> mapResistances(
            List<FeatResistanceDte> resistances,
            List<String> implementationResistances) {
        List<RaceResistanceDto> mapped = new ArrayList<>();
        resistances.stream()
                .map(resistance -> {
                    if (resistance instanceof FeatNamedResistanceDte named) {
                        return new RaceResistanceDto(named.type(), null);
                    }
                    if (resistance instanceof FeatChoiceResistanceDte choice) {
                        var value = choice.choose();
                        return new RaceResistanceDto(null, new RaceGrantChoiceDto(
                                value.count(), value.from(), List.of(), value.fromFilter()));
                    }
                    throw new IllegalStateException("Unsupported race resistance: "
                            + resistance.getClass().getName());
                })
                .forEach(mapped::add);
        implementationResistances.stream()
                .map(type -> new RaceResistanceDto(type, null))
                .forEach(mapped::add);
        return List.copyOf(mapped);
    }

    private static List<FeatAdditionalSpellDto> mapSpells(
            List<com.thedndpub.data.dte.feat.FeatAdditionalSpellDte> spells) {
        return spells.stream()
                .map(spell -> new FeatAdditionalSpellDto(
                        spell.name(),
                        spellAbility(spell.ability()),
                        spellValue(spell.prepared()),
                        spellValue(spell.known()),
                        spellValue(spell.innate()),
                        spellValue(spell.expanded())))
                .toList();
    }

    private static FeatSpellAbilityDto spellAbility(FeatSpellAbilityDte ability) {
        if (ability instanceof FeatSpellAbilityNameDte named) {
            return new FeatSpellAbilityDto(named.ability(), List.of());
        }
        if (ability instanceof FeatSpellAbilityChoiceDte choice) {
            return new FeatSpellAbilityDto(null, choice.choose());
        }
        return null;
    }

    private static FeatSpellGrantNodeDto spellValue(FeatSpellGrantNodeDte value) {
        if (value == null) {
            return null;
        }
        if (value instanceof FeatSpellTextNodeDte text) {
            return new FeatSpellTextDto(text.value());
        }
        if (value instanceof FeatSpellCountNodeDte count) {
            return new FeatSpellCountDto(count.value());
        }
        if (value instanceof FeatSpellFlagNodeDte flag) {
            return new FeatSpellFlagDto(flag.value());
        }
        if (value instanceof FeatSpellArrayNodeDte array) {
            return new FeatSpellArrayDto(array.values().stream().map(RaceMapper::spellValue).toList());
        }
        if (value instanceof FeatSpellObjectNodeDte object) {
            Map<String, FeatSpellGrantNodeDto> values = object.values().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> spellValue(entry.getValue()),
                            (left, right) -> left,
                            java.util.TreeMap::new));
            return new FeatSpellObjectDto(values);
        }
        throw new IllegalStateException("Unsupported race spell grant node: " + value.getClass().getName());
    }

    private static RaceMovementDto movement(RaceSpeedDte speed) {
        if (speed == null) {
            return null;
        }
        if (speed.walk() == null && speed.fly() == null && speed.swim() == null
                && speed.climb() == null && speed.burrow() == null) {
            return null;
        }
        return new RaceMovementDto(
                movementValue(speed.walk()),
                movementValue(speed.fly()),
                movementValue(speed.swim()),
                movementValue(speed.climb()),
                movementValue(speed.burrow()));
    }

    private static RaceMovementValueDto movementValue(RaceSpeedValueDte value) {
        return value == null ? null : new RaceMovementValueDto(value.feet(), value.sourceFlag());
    }

    private static RaceLineageDto lineage(RaceLineageDte lineage) {
        if (lineage == null) {
            return null;
        }
        SourceIdentity source = lineage.description() == null || lineage.description().isBlank()
                ? null
                : new SourceIdentity(lineage.description());
        return new RaceLineageDto(lineage.flag(), source);
    }

    private static RaceAgeDto age(RaceAgeDte age) {
        return age == null ? null : new RaceAgeDto(age.mature(), age.max());
    }

    private static RaceHeightAndWeightDto heightAndWeight(RaceHeightAndWeightDte value) {
        if (value == null || (value.baseHeight() == null && value.heightMod() == null
                && value.baseWeight() == null && value.weightMod() == null)) {
            return null;
        }
        return new RaceHeightAndWeightDto(value.baseHeight(), value.heightMod(),
                value.baseWeight(), value.weightMod());
    }

    private static List<RaceOverwriteDto> mapOverwrites(Map<String, Boolean> overwrites) {
        return overwrites.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .map(entry -> new RaceOverwriteDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    private static List<SourceReference> mapSources(List<RaceSourceReferenceDte> references) {
        if (references == null) {
            return List.of();
        }
        return references.stream()
                .map(reference -> new SourceReference(reference.source(), reference.page()))
                .toList();
    }

    private static List<RaceReprintDto> mapReprints(List<RaceReprintDte> references) {
        if (references == null) {
            return List.of();
        }
        return references.stream()
                .map(reference -> {
                    String[] parts = reference.uid().split("\\|", -1);
                    String source = parts.length > 1 ? parts[parts.length - 1] : null;
                    return new RaceReprintDto(
                            reference.uid(),
                            reference.tag(),
                            parts.length > 1 ? parts[0] : reference.uid(),
                            source == null || source.isBlank() ? null : new SourceIdentity(source));
                })
                .sorted(Comparator.comparing(RaceReprintDto::uid, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
