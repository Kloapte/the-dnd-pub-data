package com.thedndpub.data.dto.magicvariant;

import com.thedndpub.data.dto.item.ItemAbilityChoiceDto;
import com.thedndpub.data.dto.item.ItemAbilityDto;
import com.thedndpub.data.dto.item.ItemAbilityScoresDto;
import com.thedndpub.data.dto.item.ItemAttunementTagDto;
import com.thedndpub.data.dto.item.ItemLightDto;
import com.thedndpub.data.dto.item.ItemNumberTextDto;
import com.thedndpub.data.dto.item.ItemSpellGrantDto;
import com.thedndpub.data.dto.item.ItemSpellScheduleDto;
import com.thedndpub.data.dto.item.ItemSpeedModificationDto;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.item.ItemAbilityChoiceDte;
import com.thedndpub.data.dte.item.ItemAbilityDte;
import com.thedndpub.data.dte.item.ItemAttachedSpellsDte;
import com.thedndpub.data.dte.item.ItemAttunementTagDte;
import com.thedndpub.data.dte.item.ItemLightDte;
import com.thedndpub.data.dte.item.ItemModifySpeedDte;
import com.thedndpub.data.dte.item.ItemNumberTextDte;
import com.thedndpub.data.dte.item.ItemSpellGrantDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantCopyOperationDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantCopyOperationKind;
import com.thedndpub.data.dte.magicvariant.MagicVariantDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantInheritanceDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantRequirementDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Component
public final class MagicVariantMapper {
    public MagicVariantDto map(
            ResolvedMagicVariantDte resolved,
            int eligibleItemCount) {
        MagicVariantDte variant = resolved.source();
        String sourceCode = resolved.sourceCode();
        MagicVariantKind kind = variant.copy() == null
                ? MagicVariantKind.GENERIC_RULE
                : MagicVariantKind.ITEM_SPECIFIC_TRANSFORMATION;
        MagicVariantBaseItemReferenceDto baseItem = resolved.baseItemName() == null
                ? null
                : new MagicVariantBaseItemReferenceDto(
                        resolved.baseItemName(), resolved.baseItemSource(), resolved.baseItemId());
        List<SourceReference> referenceSources = references(variant.referenceSources());
        List<SourceReference> secondarySources = variant.inherits() == null
                ? List.of()
                : sourceReferences(variant.inherits().otherSources());
        return new MagicVariantDto(
                "magic-variant:" + slug(sourceCode) + ":" + slug(variant.name()),
                require(variant.name(), "magic variant name"),
                new SourceReference(new SourceIdentity(sourceCode), null),
                variant.type(),
                variant.edition(),
                kind,
                requirements(variant.requires()),
                requirement(variant.excludes()),
                variant.ammo(),
                baseItem,
                transformation(variant.inherits(), resolved.resolvedOperations()),
                referenceSources,
                secondarySources,
                reprints(variant.inherits()),
                eligibleItemCount);
    }

    public static MagicVariantRequirementDto requirement(MagicVariantRequirementDte source) {
        if (source == null) {
            return null;
        }
        return new MagicVariantRequirementDto(
                source.type(), source.source(), values(source.name()), values(source.property()),
                source.weaponCategory(), source.dmgType(), source.scfType(), source.weapon(), source.armor(),
                source.arrow(), source.axe(), source.bolt(), source.bow(), source.crossbow(), source.net(),
                source.polearm(), source.spear(), source.sword(), source.bulletFirearm(), source.cellEnergy());
    }

    private static List<MagicVariantRequirementDto> requirements(List<MagicVariantRequirementDte> source) {
        return source == null ? List.of() : source.stream().map(MagicVariantMapper::requirement).toList();
    }

    private static MagicVariantTransformationDto transformation(
            MagicVariantInheritanceDte source,
            List<MagicVariantCopyOperationDte> operations) {
        TransformationValues values = new TransformationValues(source);
        for (MagicVariantCopyOperationDte operation : operations) {
            values.apply(operation);
        }
        return values.toDto();
    }

    private static List<SourceReference> references(List<String> sources) {
        return sources == null ? List.of() : sources.stream()
                .map(source -> new SourceReference(new SourceIdentity(source), null))
                .toList();
    }

    private static List<SourceReference> sourceReferences(List<com.thedndpub.data.dte.item.ItemSourceLocatorDte> values) {
        return values == null ? List.of() : values.stream()
                .map(value -> new SourceReference(value.source(), value.page()))
                .toList();
    }

    private static List<MagicVariantReprintDto> reprints(MagicVariantInheritanceDte source) {
        if (source == null || source.reprintedAs() == null) {
            return List.of();
        }
        return source.reprintedAs().stream().map(MagicVariantMapper::reprint).toList();
    }

    private static MagicVariantReprintDto reprint(String uid) {
        int separator = uid.lastIndexOf('|');
        if (separator < 0 || separator == uid.length() - 1) {
            return new MagicVariantReprintDto(uid, null);
        }
        return new MagicVariantReprintDto(uid.substring(0, separator), uid.substring(separator + 1));
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing " + field);
        }
        return value;
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isBlank()) {
            throw new IllegalStateException("Cannot create a stable ID from '" + value + "'");
        }
        return normalized;
    }

    private static <T> List<T> list(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static List<String> values(com.thedndpub.data.dte.magicvariant.MagicVariantStringValuesDte values) {
        return values == null ? List.of() : values.values();
    }

    private static final class TransformationValues {
        private String namePrefix;
        private String nameSuffix;
        private String nameRemove;
        private String outputSource;
        private Integer outputPage;
        private String itemType;
        private String rarity;
        private String tier;
        private String bonusArmorClass;
        private String bonusWeapon;
        private String bonusWeaponAttack;
        private String bonusWeaponDamage;
        private String bonusSpellDamage;
        private String bonusSavingThrow;
        private Boolean attunementRequired;
        private List<ItemAttunementTagDto> attunementRestrictions = List.of();
        private Integer charges;
        private String chargesFormula;
        private String recharge;
        private Integer rechargeAmount;
        private String rechargeAmountFormula;
        private ItemAbilityDto ability;
        private ItemSpellScheduleDto attachedSpells;
        private List<String> classFeatureReferences = List.of();
        private List<String> addedProperties = List.of();
        private List<String> resistances = List.of();
        private List<String> immunities = List.of();
        private List<String> vulnerabilities = List.of();
        private Boolean curse;
        private Boolean sentient;
        private Boolean wondrous;
        private Boolean barding;
        private Boolean stealthDisadvantage;
        private Boolean grantsProficiency;
        private Boolean grantsLanguage;
        private List<ItemLightDto> lights = List.of();
        private ItemSpeedModificationDto speedModification;
        private Integer spellScrollLevel;
        private String strengthRequirement;
        private String valueExpression;
        private Double valueMultiplier;
        private String weightExpression;
        private Double weightMultiplier;

        private TransformationValues(MagicVariantInheritanceDte source) {
            if (source == null) {
                return;
            }
            namePrefix = source.namePrefix();
            nameSuffix = source.nameSuffix();
            nameRemove = source.nameRemove();
            outputSource = source.source();
            outputPage = source.page();
            itemType = source.type();
            rarity = source.rarity();
            tier = source.tier();
            bonusArmorClass = source.bonusAc();
            bonusWeapon = source.bonusWeapon();
            bonusWeaponAttack = source.bonusWeaponAttack();
            bonusWeaponDamage = source.bonusWeaponDamage();
            bonusSpellDamage = source.bonusSpellDamage();
            bonusSavingThrow = source.bonusSavingThrow();
            attunementRequired = attunementRequired(source.reqAttune());
            attunementRestrictions = attunementTags(source.reqAttuneTags());
            charges = number(source.charges());
            chargesFormula = formula(source.charges());
            recharge = source.recharge();
            rechargeAmount = number(source.rechargeAmount());
            rechargeAmountFormula = formula(source.rechargeAmount());
            ability = ability(source.ability());
            attachedSpells = spells(source.attachedSpells());
            classFeatureReferences = list(source.classFeatures());
            addedProperties = list(source.propertyAdd());
            resistances = list(source.resist());
            immunities = list(source.immune());
            vulnerabilities = list(source.vulnerable());
            curse = source.curse();
            sentient = source.sentient();
            wondrous = source.wondrous();
            barding = source.barding();
            stealthDisadvantage = source.stealth();
            grantsProficiency = source.grantsProficiency();
            grantsLanguage = source.grantsLanguage();
            lights = lights(source.light());
            speedModification = speed(source.modifySpeed());
            spellScrollLevel = source.spellScrollLevel();
            strengthRequirement = source.strength() == null ? null : source.strength().value();
            valueExpression = source.valueExpression();
            valueMultiplier = source.valueMult();
            weightExpression = source.weightExpression();
            weightMultiplier = source.weightMult();
        }

        private void apply(MagicVariantCopyOperationDte operation) {
            String path = operation.targetPath();
            switch (operation.operation()) {
                case SET_PROPERTY -> set(path, operation.stringValue(), operation.integerValue());
                case APPEND_IF_ABSENT_STRING -> append(path, operation.stringValue());
                case APPEND_ENTRY, REMOVE_PROPERTY -> {
                    // Narrative entries and source-availability flags stay in import provenance.
                }
            }
        }

        private void set(String path, String stringValue, Integer integerValue) {
            switch (path) {
                case "inherits.namePrefix" -> namePrefix = stringValue;
                case "inherits.nameSuffix" -> nameSuffix = stringValue;
                case "inherits.page" -> outputPage = integerValue;
                case "inherits.rarity" -> rarity = stringValue;
                case "inherits.source" -> outputSource = stringValue;
                default -> throw new IllegalStateException("Unsupported normalized copy target '" + path + "'");
            }
        }

        private void append(String path, String value) {
            if ("inherits.propertyAdd".equals(path)) {
                addedProperties = appendDistinct(addedProperties, value);
            } else if ("inherits.resist".equals(path)) {
                resistances = appendDistinct(resistances, value);
            } else {
                throw new IllegalStateException("Unsupported normalized append target '" + path + "'");
            }
        }

        private MagicVariantTransformationDto toDto() {
            if (isEmpty()) {
                return null;
            }
            return new MagicVariantTransformationDto(
                    namePrefix, nameSuffix, nameRemove, outputSource, outputPage, itemType, rarity, tier,
                    bonusArmorClass, bonusWeapon, bonusWeaponAttack, bonusWeaponDamage, bonusSpellDamage,
                    bonusSavingThrow, attunementRequired, attunementRestrictions, charges, chargesFormula,
                    recharge, rechargeAmount, rechargeAmountFormula, ability, attachedSpells,
                    classFeatureReferences, addedProperties, resistances, immunities, vulnerabilities,
                    curse, sentient, wondrous, barding, stealthDisadvantage, grantsProficiency, grantsLanguage,
                    lights, speedModification, spellScrollLevel, strengthRequirement, valueExpression,
                    valueMultiplier, weightExpression, weightMultiplier);
        }

        private boolean isEmpty() {
            return namePrefix == null && nameSuffix == null && nameRemove == null && outputSource == null
                    && outputPage == null && itemType == null && rarity == null && tier == null
                    && bonusArmorClass == null && bonusWeapon == null && bonusWeaponAttack == null
                    && bonusWeaponDamage == null && bonusSpellDamage == null && bonusSavingThrow == null
                    && attunementRequired == null && attunementRestrictions.isEmpty() && charges == null
                    && chargesFormula == null && recharge == null && rechargeAmount == null
                    && rechargeAmountFormula == null && ability == null && attachedSpells == null
                    && classFeatureReferences.isEmpty() && addedProperties.isEmpty() && resistances.isEmpty()
                    && immunities.isEmpty() && vulnerabilities.isEmpty() && curse == null && sentient == null
                    && wondrous == null && barding == null && stealthDisadvantage == null
                    && grantsProficiency == null && grantsLanguage == null && lights.isEmpty()
                    && speedModification == null && spellScrollLevel == null && strengthRequirement == null
                    && valueExpression == null && valueMultiplier == null && weightExpression == null
                    && weightMultiplier == null;
        }
    }

    private static Boolean attunementRequired(com.thedndpub.data.dte.item.ItemBooleanTextDte source) {
        if (source == null) {
            return null;
        }
        if (source.flag() != null) {
            return source.flag();
        }
        return source.text() == null ? null : Boolean.TRUE;
    }

    private static List<ItemAttunementTagDto> attunementTags(List<ItemAttunementTagDte> source) {
        return source == null ? List.of() : source.stream()
                .map(value -> new ItemAttunementTagDto(value.className(), value.background(), value.spellcasting(),
                        value.skillProficiency(), list(value.alignment()), value.creatureType(), value.race(),
                        value.psionics(), value.languageProficiency(), value.size(), value.intScore()))
                .toList();
    }

    private static Integer number(ItemNumberTextDte source) {
        return source == null ? null : source.number();
    }

    private static String formula(ItemNumberTextDte source) {
        if (source == null || source.text() == null) {
            return null;
        }
        String text = source.text();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^\\{@dice\\s+([^}]+)}$").matcher(text);
        if (matcher.matches()) {
            return matcher.group(1).trim();
        }
        return text.contains("{@") ? null : text;
    }

    private static ItemAbilityDto ability(ItemAbilityDte source) {
        if (source == null) {
            return null;
        }
        List<ItemAbilityChoiceDto> choices = source.choose() == null ? List.of() : source.choose().stream()
                .map(MagicVariantMapper::abilityChoice)
                .toList();
        var staticScores = source.staticScores() == null ? null
                : new ItemAbilityScoresDto(source.staticScores().str(), source.staticScores().dex(),
                source.staticScores().con(), source.staticScores().intScore(), source.staticScores().wis(),
                source.staticScores().cha());
        return new ItemAbilityDto(source.str(), source.dex(), source.con(), source.intScore(), source.wis(),
                source.cha(), list(source.from()), source.count(), source.amount(), choices, staticScores);
    }

    private static ItemAbilityChoiceDto abilityChoice(ItemAbilityChoiceDte source) {
        return new ItemAbilityChoiceDto(list(source.from()), source.count(), source.amount());
    }

    private static ItemSpellScheduleDto spells(ItemAttachedSpellsDte source) {
        if (source == null) {
            return null;
        }
        return new ItemSpellScheduleDto(list(source.direct()), source.ability(), spellGrant(source.will()),
                spellGrant(source.daily()), spellGrant(source.rest()), spellGrant(source.charges()),
                spellGrant(source.limited()), list(source.other()), list(source.ritual()));
    }

    private static ItemSpellGrantDto spellGrant(ItemSpellGrantDte source) {
        return source == null ? null : new ItemSpellGrantDto(list(source.direct()), source.byUses() == null
                ? Map.of() : source.byUses());
    }

    private static List<ItemLightDto> lights(List<ItemLightDte> source) {
        return source == null ? List.of() : source.stream()
                .map(value -> new ItemLightDto(value.bright(), value.dim(), value.shape()))
                .toList();
    }

    private static ItemSpeedModificationDto speed(ItemModifySpeedDte source) {
        if (source == null) {
            return null;
        }
        return new ItemSpeedModificationDto(source.equal() == null ? Map.of() : source.equal(),
                numberText(source.multiply()), numberText(source.staticValues()), numberText(source.bonus()));
    }

    private static Map<String, ItemNumberTextDto> numberText(Map<String, ItemNumberTextDte> source) {
        if (source == null) {
            return Map.of();
        }
        return source.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> new ItemNumberTextDto(entry.getValue().number(), entry.getValue().text())));
    }

    private static List<String> appendDistinct(List<String> values, String value) {
        List<String> result = new ArrayList<>(values);
        if (!result.contains(value)) {
            result.add(value);
        }
        return List.copyOf(result);
    }
}
