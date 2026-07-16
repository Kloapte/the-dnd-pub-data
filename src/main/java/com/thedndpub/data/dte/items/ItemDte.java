package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thedndpub.data.dte.items.deserializers.*;
import com.thedndpub.data.dte.generic.*;
import com.thedndpub.data.dte.generic.deserializers.EntryDteDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemDte(

        String name,
        String source,
        Integer page,

        @JsonDeserialize(using = SrdDeserializer.class)
        Boolean srd,

        @JsonDeserialize(using = SrdDeserializer.class)
        Boolean srd52,

        Boolean basicRules,
        Boolean basicRules2024,

        List<OtherSourceDte> additionalSources,
        List<OtherSourceDte> otherSources,

        @JsonDeserialize(using = ReprintedAsDeserializer.class)
        List<String> reprintedAs,

        CopyDte _copy,
        String baseItem,
        String type,
        String tier,
        String rarity,
        Integer value,
        String valueRarity,
        String strength,
        Boolean stealth,
        String reqAttune,
        List<RequiresAttunementDte> reqAttuneTags,
        String reqAttuneAlt,
        Integer weight,
        String weaponCategory,
        Boolean staff,
        List<String> property,
        List<String> items,
        Boolean wondrous,
        Boolean tattoo,
        Boolean curse,
        Boolean poison,
        List<String> poisonTypes,
        Boolean sentient,
        List<String> immune,
        List<String> conditionImmune,
        List<String> resist,
        List<String> vulnerable,
        Boolean grantsProficiency,
        Boolean grantsLanguage,
        ItemSpeedDte modifySpeed,

        @JsonDeserialize(using = ItemAbilityDeserializer.class)
        AbilityDte ability,

        String range,
        String dmg1,
        String dmg2,
        String dmgType,
        String ammoType,
        Integer ac,
        Integer crew,
        Integer crewMin,
        Integer crewMax,
        Integer vehAc,
        Integer vehHp,
        Integer vehSpeed,
        Integer vehDmgThresh,
        Integer capPassenger,
        Integer capCargo,
        Integer travelCost,
        Integer shippingCost,
        Integer reach,
        String bonusWeapon,
        String bonusWeaponAttack,
        String bonusWeaponDamage,
        String bonusAc,
        String bonusSavingThrow,
        String bonusSpellAttack,
        String bonusSpellSaveDc,
        String bonusProficiencyBonus,
        String bonusSavingThrowConcentration,
        String bonusAbilityCheck,
        Integer critThreshold,

        @JsonDeserialize(using = ItemFocusDeserializer.class)
        List<String> focus,

        String recharge,
        String rechargeAmount,
        String charges,
        ContainerCapacityDte containerCapacity,

        @JsonDeserialize(using = PackContentDeserializer.class)
        List<PackContentDte> packContents,

        Boolean atomicPackContents,
        Integer carryingCapacity,
        BarDimensionsDte barDimensions,
        Integer speed,
        String age,
        Boolean hasRefs,

        @JsonDeserialize(using = EntryDteDeserializer.class)
        List<EntryDte> entries,

        @JsonDeserialize(using = EntryDteDeserializer.class)
        List<EntryDte> additionalEntries,

        List<String> optionalfeatures,
        List<String> lootTables,
        List<String> alias,
        List<String> miscTags,
        List<String> group,

        @JsonDeserialize(using = MasteryAbilityDeserializer.class)
        List<String> mastery,

        String scfType,
        List<LightDte> light,
        Integer spellScrollLevel,

        @JsonDeserialize(using = AttachedSpellDeserializer.class)
        List<ItemAttachedSpellDte> attachedSpells,

        Boolean firearm,
        String detail1,
        String detail2,
        String typeAlt,
        List<String> seeAlsoVehicle,
        List<String> seeAlsoDeck,

        Boolean itemsHidden,
        String weightNote,
        Object dexterityMax,
        Boolean hasFluffImages,
        Boolean hasFluff

) {}