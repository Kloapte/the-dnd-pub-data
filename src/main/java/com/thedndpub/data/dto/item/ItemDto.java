package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemDto(
        String id,
        String name,
        SourceReference source,
        ItemDefinitionKind definitionKind,
        List<String> typeCodes,
        String alternateTypeCode,
        List<ItemTypeDto> types,
        String rarity,
        String valueRarity,
        Double weight,
        Integer valueCopper,
        String baseItem,
        String weaponCategory,
        ItemDamageDto damage,
        String range,
        Integer reload,
        String ammunitionType,
        List<ItemPropertyDto> properties,
        List<ItemMasteryDto> masteries,
        Integer armorClass,
        String strengthRequirement,
        Integer dexterityMaximum,
        Boolean stealthDisadvantage,
        ItemAttunementDto attunement,
        ItemChargeDto charges,
        ItemSpellScheduleDto attachedSpells,
        ItemContainerCapacityDto containerCapacity,
        List<ItemPackContentDto> packContents,
        List<String> groups,
        List<String> classifications,
        Boolean wondrous,
        Boolean weapon,
        Boolean armor,
        Boolean firearm,
        Boolean grantsProficiency,
        Boolean grantsLanguage,
        Integer speed,
        Integer carryingCapacity,
        List<SourceReference> additionalSources,
        List<SourceReference> secondarySources,
        List<String> referenceSources,
        List<ItemSourceRelationDto> reprintedAs,
        ItemAbilityDto ability,
        ItemBonusesDto bonuses,
        ItemProtectionDto protection,
        List<ItemLightDto> lights,
        ItemSpeedModificationDto speedModification,
        Boolean focusEnabled,
        ItemVehicleStatisticsDto vehicleStatistics,
        ItemReferencesDto references,
        String spellcastingFocusType,
        String rarityTier,
        String itemAge,
        String detailOne,
        String detailTwo,
        Integer spellScrollLevel,
        Integer criticalThreshold,
        Boolean poison,
        Boolean staff,
        Boolean sentient,
        Boolean cursed,
        Boolean tattoo,
        Boolean atomicPackContents,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public ItemDto {
        typeCodes = List.copyOf(typeCodes);
        types = List.copyOf(types);
        properties = List.copyOf(properties);
        masteries = List.copyOf(masteries);
        packContents = List.copyOf(packContents);
        groups = List.copyOf(groups);
        classifications = List.copyOf(classifications);
        additionalSources = List.copyOf(additionalSources);
        secondarySources = List.copyOf(secondarySources);
        referenceSources = List.copyOf(referenceSources);
        reprintedAs = List.copyOf(reprintedAs);
        lights = List.copyOf(lights);
        crossCatalogReferences = List.copyOf(crossCatalogReferences);
    }

    public static ItemDto from(ItemDefinition item) {
        return from(item, List.of());
    }

    public static ItemDto from(ItemDefinition item, List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
        return new ItemDto(item.id(), item.name(), item.source(), item.definitionKind(), item.typeCodes(),
                item.alternateTypeCode(), item.types(), item.rarity(), item.valueRarity(), item.weight(), item.valueCopper(), item.baseItem(),
                item.weaponCategory(), item.damage(), item.range(), item.reload(), item.ammunitionType(),
                item.properties(), item.masteries(), item.armorClass(), item.strengthRequirement(),
                item.dexterityMaximum(), item.stealthDisadvantage(), item.attunement(), item.charges(),
                item.attachedSpells(), item.containerCapacity(), item.packContents(), item.groups(),
                item.classifications(), item.wondrous(), item.weapon(), item.armor(), item.firearm(),
                item.grantsProficiency(), item.grantsLanguage(), item.speed(), item.carryingCapacity(),
                item.additionalSources(), item.secondarySources(), item.referenceSources(), item.reprintedAs(),
                item.ability(), item.bonuses(), item.protection(), item.lights(), item.speedModification(),
                item.focusEnabled(), item.vehicleStatistics(), item.references(), item.spellcastingFocusType(),
                item.rarityTier(), item.itemAge(), item.detailOne(), item.detailTwo(), item.spellScrollLevel(),
                item.criticalThreshold(), item.poison(), item.staff(), item.sentient(), item.cursed(),
                item.tattoo(), item.atomicPackContents(), crossCatalogReferences);
    }

    public ItemDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
        return new ItemDto(id, name, source, definitionKind, typeCodes, alternateTypeCode, types, rarity,
                valueRarity, weight, valueCopper, baseItem, weaponCategory, damage, range, reload, ammunitionType,
                properties, masteries, armorClass, strengthRequirement, dexterityMaximum, stealthDisadvantage,
                attunement, charges, attachedSpells, containerCapacity, packContents, groups, classifications,
                wondrous, weapon, armor, firearm, grantsProficiency, grantsLanguage, speed, carryingCapacity,
                additionalSources, secondarySources, referenceSources, reprintedAs, ability, bonuses, protection,
                lights, speedModification, focusEnabled, vehicleStatistics, references, spellcastingFocusType,
                rarityTier, itemAge, detailOne, detailTwo, spellScrollLevel, criticalThreshold, poison, staff,
                sentient, cursed, tattoo, atomicPackContents, crossCatalogReferences);
    }
}
