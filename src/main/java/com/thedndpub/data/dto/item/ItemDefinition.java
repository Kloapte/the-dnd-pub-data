package com.thedndpub.data.dto.item;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

public record ItemDefinition(
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
        List<com.thedndpub.data.dto.source.SourceReference> secondarySources,
        List<String> referenceSources,
        List<ItemSourceRelationDto> reprintedAs,
        ItemAbilityDto ability,
        ItemBonusesDto bonuses,
        ItemProtectionDto protection,
        List<ItemLightDto> lights,
        ItemSpeedModificationDto speedModification,
        Boolean focusEnabled,
        ItemFocus focus,
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
        ItemSignedBonuses signedBonuses,
        ItemRechargeDeclaration standaloneRecharge) {
    public ItemDefinition {
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
    }
}
