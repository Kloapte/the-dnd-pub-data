package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.feat.FeatAdditionalSpellDto;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceDto(
        String id,
        String name,
        SourceReference source,
        RaceDefinitionKind definitionKind,
        RaceParentReferenceDto parent,
        String edition,
        List<String> creatureTypes,
        List<String> creatureTypeTags,
        List<String> size,
        RaceMovementDto movement,
        List<RaceAbilityDto> abilities,
        List<RaceGrantGroupDto> skillProficiencies,
        List<RaceGrantGroupDto> toolProficiencies,
        List<RaceGrantGroupDto> weaponProficiencies,
        List<RaceGrantGroupDto> armorProficiencies,
        List<RaceGrantGroupDto> languageProficiencies,
        List<RaceSkillToolLanguageGrantDto> skillToolLanguageProficiencies,
        List<RaceGrantGroupDto> featGrants,
        Integer darkvision,
        Integer blindsight,
        List<RaceResistanceDto> damageResistances,
        List<String> damageImmunities,
        List<String> damageVulnerabilities,
        List<String> conditionImmunities,
        List<FeatAdditionalSpellDto> additionalSpells,
        List<String> traitNames,
        List<String> traitTags,
        List<String> aliases,
        RaceLineageDto lineage,
        RaceAgeDto age,
        RaceHeightAndWeightDto heightAndWeight,
        List<RaceOverwriteDto> overwrites,
        List<SourceReference> additionalSources,
        List<SourceReference> otherSources,
        List<RaceReprintDto> reprintedAs,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public RaceDto {
        creatureTypes = immutable(creatureTypes);
        creatureTypeTags = immutable(creatureTypeTags);
        size = immutable(size);
        abilities = immutable(abilities);
        skillProficiencies = immutable(skillProficiencies);
        toolProficiencies = immutable(toolProficiencies);
        weaponProficiencies = immutable(weaponProficiencies);
        armorProficiencies = immutable(armorProficiencies);
        languageProficiencies = immutable(languageProficiencies);
        skillToolLanguageProficiencies = immutable(skillToolLanguageProficiencies);
        featGrants = immutable(featGrants);
        damageResistances = immutable(damageResistances);
        damageImmunities = immutable(damageImmunities);
        damageVulnerabilities = immutable(damageVulnerabilities);
        conditionImmunities = immutable(conditionImmunities);
        additionalSpells = immutable(additionalSpells);
        traitNames = immutable(traitNames);
        traitTags = immutable(traitTags);
        aliases = immutable(aliases);
        overwrites = immutable(overwrites);
        additionalSources = immutable(additionalSources);
        otherSources = immutable(otherSources);
        reprintedAs = immutable(reprintedAs);
        crossCatalogReferences = immutable(crossCatalogReferences);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    public RaceDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new RaceDto(id, name, source, definitionKind, parent, edition, creatureTypes, creatureTypeTags, size,
                movement, abilities, skillProficiencies, toolProficiencies, weaponProficiencies,
                armorProficiencies, languageProficiencies, skillToolLanguageProficiencies, featGrants,
                darkvision, blindsight, damageResistances, damageImmunities, damageVulnerabilities,
                conditionImmunities, additionalSpells, traitNames, traitTags, aliases, lineage, age,
                heightAndWeight, overwrites, additionalSources, otherSources, reprintedAs, references);
    }
}
