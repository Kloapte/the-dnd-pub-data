package com.thedndpub.data.dte.magicvariant;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.item.ItemAbilityDte;
import com.thedndpub.data.dte.item.ItemAttachedSpellsDte;
import com.thedndpub.data.dte.item.ItemBooleanTextDte;
import com.thedndpub.data.dte.item.ItemLightDte;
import com.thedndpub.data.dte.item.ItemModifySpeedDte;
import com.thedndpub.data.dte.item.ItemNumberTextDte;
import com.thedndpub.data.dte.item.ItemNullableStringDte;
import com.thedndpub.data.dte.item.ItemSourceLocatorDte;
import com.thedndpub.data.dte.item.ItemAttunementTagDte;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record MagicVariantInheritanceDte(
        String namePrefix,
        String nameSuffix,
        String nameRemove,
        String source,
        Integer page,
        String type,
        String rarity,
        String tier,
        String bonusAc,
        String bonusWeapon,
        String bonusWeaponAttack,
        String bonusWeaponDamage,
        String bonusSpellDamage,
        String bonusSavingThrow,
        Boolean curse,
        Boolean sentient,
        Boolean wondrous,
        Boolean barding,
        Boolean stealth,
        Boolean grantsProficiency,
        Boolean grantsLanguage,
        Boolean hasRefs,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        Boolean hasFluffImages,
        ItemBooleanTextDte reqAttune,
        List<ItemAttunementTagDte> reqAttuneTags,
        ItemNumberTextDte charges,
        String recharge,
        ItemNumberTextDte rechargeAmount,
        @JsonDeserialize(using = com.thedndpub.data.dte.item.ItemAttachedSpellsDeserializer.class)
        ItemAttachedSpellsDte attachedSpells,
        ItemAbilityDte ability,
        List<String> classFeatures,
        List<String> lootTables,
        List<String> propertyAdd,
        List<String> resist,
        List<String> immune,
        List<String> vulnerable,
        List<String> reprintedAs,
        List<ItemSourceLocatorDte> otherSources,
        List<EntryDte> entries,
        List<ItemLightDte> light,
        ItemModifySpeedDte modifySpeed,
        Integer spellScrollLevel,
        ItemNullableStringDte strength,
        String valueExpression,
        Double valueMult,
        String weightExpression,
        Double weightMult) {
}
