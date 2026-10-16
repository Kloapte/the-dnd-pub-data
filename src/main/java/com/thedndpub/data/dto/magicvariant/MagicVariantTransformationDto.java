package com.thedndpub.data.dto.magicvariant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.item.ItemAbilityDto;
import com.thedndpub.data.dto.item.ItemAttunementTagDto;
import com.thedndpub.data.dto.item.ItemLightDto;
import com.thedndpub.data.dto.item.ItemSpellScheduleDto;
import com.thedndpub.data.dto.item.ItemSpeedModificationDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MagicVariantTransformationDto(
        String namePrefix,
        String nameSuffix,
        String nameRemove,
        String outputSource,
        Integer outputPage,
        String itemType,
        String rarity,
        String tier,
        String bonusArmorClass,
        String bonusWeapon,
        String bonusWeaponAttack,
        String bonusWeaponDamage,
        String bonusSpellDamage,
        String bonusSavingThrow,
        Boolean attunementRequired,
        List<ItemAttunementTagDto> attunementRestrictions,
        Integer charges,
        String chargesFormula,
        String recharge,
        Integer rechargeAmount,
        String rechargeAmountFormula,
        ItemAbilityDto ability,
        ItemSpellScheduleDto attachedSpells,
        List<String> classFeatureReferences,
        List<String> addedProperties,
        List<String> resistances,
        List<String> immunities,
        List<String> vulnerabilities,
        Boolean curse,
        Boolean sentient,
        Boolean wondrous,
        Boolean barding,
        Boolean stealthDisadvantage,
        Boolean grantsProficiency,
        Boolean grantsLanguage,
        List<ItemLightDto> lights,
        ItemSpeedModificationDto speedModification,
        Integer spellScrollLevel,
        String strengthRequirement,
        String valueExpression,
        Double valueMultiplier,
        String weightExpression,
        Double weightMultiplier) {
    public MagicVariantTransformationDto {
        attunementRestrictions = List.copyOf(attunementRestrictions);
        classFeatureReferences = List.copyOf(classFeatureReferences);
        addedProperties = List.copyOf(addedProperties);
        resistances = List.copyOf(resistances);
        immunities = List.copyOf(immunities);
        vulnerabilities = List.copyOf(vulnerabilities);
        lights = List.copyOf(lights);
    }
}
