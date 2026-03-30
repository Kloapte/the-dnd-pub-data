package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.items.deserializers.*;
import com.thedndpub.data.dte.generic.OtherSourceDte;
import com.thedndpub.data.dte.generic.CopyDte;
import com.thedndpub.data.dte.generic.AbilityDte;
import com.thedndpub.data.dte.generic.EntryDte;
import com.thedndpub.data.dte.generic.deserializers.EntryDteDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDte {
    private String name;
    private String source;
    private int page;
    @JsonDeserialize(using = SrdDeserializer.class)
    private boolean srd;
    @JsonDeserialize(using = SrdDeserializer.class)
    private boolean srd52;
    private boolean basicRules;
    private boolean basicRules2024;
    private List<OtherSourceDte> additionalSources;
    private List<OtherSourceDte> otherSources;
    @JsonDeserialize(using = ReprintedAsDeserializer.class)
    private List<String> reprintedAs;
    private CopyDte _copy;
    private String baseItem;
    private String type;
    private String tier;
    private String rarity;
    private Integer value;
    private String valueRarity;
    private String strength;
    private Boolean stealth;
    private String reqAttune;
    private List<RequiresAttunementDte> reqAttuneTags;
    private String reqAttuneAlt;
    private Integer weight;
    private String weaponCategory;
    private Boolean staff;
    private List<String> property;
    private List<String> items;
    private Boolean wondrous;
    private Boolean tattoo;
    private Boolean curse;
    private Boolean poison;
    private List<String> poisonTypes;
    private Boolean sentient;
    private List<String> immune;
    private List<String> conditionImmune;
    private List<String> resist;
    private List<String> vulnerable;
    private Boolean grantsProficiency;
    private Boolean grantsLanguage;
    private ItemSpeedDte modifySpeed;
    @JsonDeserialize(using = ItemAbilityDeserializer.class)
    private AbilityDte ability;
    private String range;
    private String dmg1;
    private String dmg2;
    private String dmgType;
    private String ammoType;
    private Integer ac;
    private Integer crew;
    private Integer crewMin;
    private Integer crewMax;
    private Integer vehAc;
    private Integer vehHp;
    private Integer vehSpeed;
    private Integer vehDmgThresh;
    private Integer capPassenger;
    private Integer capCargo;
    private Integer travelCost;
    private Integer shippingCost;
    private Integer reach;
    private String bonusWeapon;
    private String bonusWeaponAttack;
    private String bonusWeaponDamage;
    private String bonusAc;
    private String bonusSavingThrow;
    private String bonusSpellAttack;
    private String bonusSpellSaveDc;
    private String bonusProficiencyBonus;
    private String bonusSavingThrowConcentration;
    private String bonusAbilityCheck;
    private Integer critThreshold;
    @JsonDeserialize(using = ItemFocusDeserializer.class)
    private List<String> focus;
    private String recharge;
    private String rechargeAmount;
    private String charges;
    private ContainerCapacityDte containerCapacity;
    @JsonDeserialize(using = PackContentDeserializer.class)
    private List<PackContentDte> packContents;
    private Boolean atomicPackContents;
    private Integer carryingCapacity;
    private BarDimensionsDte barDimensions;
    private Integer speed;
    private String age;
    private Boolean hasRefs;
    @JsonDeserialize(using = EntryDteDeserializer.class)
    private List<EntryDte> entries;
    @JsonDeserialize(using = EntryDteDeserializer.class)
    private List<EntryDte> additionalEntries;
    private List<String> optionalfeatures;
    private List<String> lootTables;
    private List<String> alias;
    private List<String> miscTags;
    private List<String> group;
    @JsonDeserialize(using = MasteryAbilityDeserializer.class)
    private List<String> mastery;
    private String scfType;
    private List<LightDte> light;
    private Integer spellScrollLevel;
    @JsonDeserialize(using = AttachedSpellDeserializer.class)
    private List<ItemAttachedSpellDte> attachedSpells;
    private Boolean firearm;
    private String detail1;
    private String detail2;
    private String typeAlt;
    private List<String> seeAlsoVehicle;
    private List<String> seeAlsoDeck;

    private Boolean itemsHidden; //For itemGroup
    private String weightNote;
    private Object dexterityMax; //Always null so can ignore
    private Boolean hasFluffImages;
    private Boolean hasFluff;
}
