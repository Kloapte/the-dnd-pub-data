package com.thedndpub.data.dto.item;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.item.ItemAttunementTagDte;
import com.thedndpub.data.dte.item.ItemNumberTextDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
import com.thedndpub.data.dte.item.ItemStringListDte;
import com.thedndpub.data.dte.item.ItemTypeDte;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@Component
public final class ItemMapper {
    public ItemDefinition map(
            ResolvedItemDte resolved,
            ItemDefinitionKind kind,
            Map<String, ItemTypeDte> typeDefinitions) {
        ItemSourceDte item = resolved.source();
        List<String> typeCodes = splitCodes(value(item.type(), resolved, ItemSourceDte::type));
        List<ItemTypeDto> types = typeCodes.stream()
                .map(code -> typeDefinition(code, item.source(), typeDefinitions))
                .filter(java.util.Objects::nonNull)
                .toList();
        List<com.thedndpub.data.dte.item.ItemPropertyReferenceDte> properties = resolved.resolvedProperties();
        List<com.thedndpub.data.dte.item.ItemMasteryReferenceDte> masteries =
                value(item.mastery(), resolved, ItemSourceDte::mastery);
        ItemNumberTextDte chargeCount = value(item.charges(), resolved, ItemSourceDte::charges);
        ItemNumberTextDte rechargeAmount = value(item.rechargeAmount(), resolved, ItemSourceDte::rechargeAmount);
        ItemAttunementDto attunement = attunement(item, resolved);
        ItemStringListDte focus = value(item.focus(), resolved, ItemSourceDte::focus);
        String sourceCode = require(item.source(), "item source");
        String name = require(item.name(), "item name");
        return new ItemDefinition(
                "item:" + slug(sourceCode) + ":" + slug(name),
                name,
                new SourceReference(sourceCode, page(item, resolved)),
                kind,
                typeCodes,
                value(item.typeAlt(), resolved, ItemSourceDte::typeAlt),
                types,
                value(item.rarity(), resolved, ItemSourceDte::rarity),
                value(item.valueRarity(), resolved, ItemSourceDte::valueRarity),
                value(item.weight(), resolved, ItemSourceDte::weight),
                integerValue(item.value(), resolved, ItemSourceDte::value),
                value(item.baseItem(), resolved, ItemSourceDte::baseItem),
                value(item.weaponCategory(), resolved, ItemSourceDte::weaponCategory),
                damage(item, resolved),
                value(item.range(), resolved, ItemSourceDte::range),
                value(item.reload(), resolved, ItemSourceDte::reload),
                value(item.ammoType(), resolved, ItemSourceDte::ammoType),
                properties == null ? List.of() : properties.stream()
                        .map(value -> new ItemPropertyDto(value.uid(), value.note())).toList(),
                masteries == null ? List.of() : masteries.stream()
                        .map(value -> new ItemMasteryDto(value.uid(), value.note())).toList(),
                value(item.ac(), resolved, ItemSourceDte::ac),
                stringValue(item.strength(), resolved, ItemSourceDte::strength),
                integerValue(item.dexterityMax(), resolved, ItemSourceDte::dexterityMax),
                value(item.stealth(), resolved, ItemSourceDte::stealth),
                attunement,
                chargeCount == null && rechargeAmount == null ? null : new ItemChargeDto(
                        chargeCount == null ? null : chargeCount.number(),
                        chargeCount == null ? null : chargeCount.text(),
                        value(item.recharge(), resolved, ItemSourceDte::recharge),
                        rechargeAmount == null ? null : rechargeAmount.number(),
                        rechargeAmount == null ? null : rechargeAmount.text()),
                attachedSpells(value(item.attachedSpells(), resolved, ItemSourceDte::attachedSpells)),
                container(item, resolved),
                packContents(item, resolved),
                list(value(item.group(), resolved, ItemSourceDte::group)),
                classifications(item, resolved),
                value(item.wondrous(), resolved, ItemSourceDte::wondrous),
                value(item.weapon(), resolved, ItemSourceDte::weapon),
                value(item.armor(), resolved, ItemSourceDte::armor),
                value(item.firearm(), resolved, ItemSourceDte::firearm),
                value(item.grantsProficiency(), resolved, ItemSourceDte::grantsProficiency),
                value(item.grantsLanguage(), resolved, ItemSourceDte::grantsLanguage),
                value(item.speed(), resolved, ItemSourceDte::speed),
                value(item.carryingCapacity(), resolved, ItemSourceDte::carryingCapacity),
                sources(value(item.additionalSources(), resolved, ItemSourceDte::additionalSources)),
                sources(value(item.otherSources(), resolved, ItemSourceDte::otherSources)),
                list(value(item.referenceSources(), resolved, ItemSourceDte::referenceSources)),
                reprints(value(item.reprintedAs(), resolved, ItemSourceDte::reprintedAs)),
                ability(value(item.ability(), resolved, ItemSourceDte::ability)),
                bonuses(item, resolved),
                protection(item, resolved),
                lights(value(item.light(), resolved, ItemSourceDte::light)),
                speedModification(value(item.modifySpeed(), resolved, ItemSourceDte::modifySpeed)),
                focusEnabled(focus),
                focus(focus),
                vehicleStatistics(item, resolved),
                references(item, resolved),
                value(item.scfType(), resolved, ItemSourceDte::scfType),
                value(item.tier(), resolved, ItemSourceDte::tier),
                value(item.age(), resolved, ItemSourceDte::age),
                value(item.detail1(), resolved, ItemSourceDte::detail1),
                value(item.detail2(), resolved, ItemSourceDte::detail2),
                value(item.spellScrollLevel(), resolved, ItemSourceDte::spellScrollLevel),
                value(item.critThreshold(), resolved, ItemSourceDte::critThreshold),
                value(item.poison(), resolved, ItemSourceDte::poison),
                value(item.staff(), resolved, ItemSourceDte::staff),
                value(item.sentient(), resolved, ItemSourceDte::sentient),
                value(item.curse(), resolved, ItemSourceDte::curse),
                value(item.tattoo(), resolved, ItemSourceDte::tattoo),
                value(item.atomicPackContents(), resolved, ItemSourceDte::atomicPackContents),
                signedBonuses(item, resolved));
    }

    public ItemVariantMatchData mapVariantMatchData(ItemDefinition definition, ResolvedItemDte resolved) {
        ItemSourceDte item = resolved.source();
        return new ItemVariantMatchData(
                definition.id(),
                definition.name(),
                definition.source().identity().code(),
                definition.typeCodes(),
                definition.alternateTypeCode(),
                definition.properties().stream().map(ItemPropertyDto::uid).toList(),
                definition.weaponCategory(),
                definition.damage() == null ? null : definition.damage().damageType(),
                definition.spellcastingFocusType(),
                definition.weapon(),
                definition.armor(),
                value(item.arrow(), resolved, ItemSourceDte::arrow),
                value(item.axe(), resolved, ItemSourceDte::axe),
                value(item.bolt(), resolved, ItemSourceDte::bolt),
                value(item.bow(), resolved, ItemSourceDte::bow),
                value(item.crossbow(), resolved, ItemSourceDte::crossbow),
                value(item.net(), resolved, ItemSourceDte::net),
                value(item.polearm(), resolved, ItemSourceDte::polearm),
                value(item.spear(), resolved, ItemSourceDte::spear),
                value(item.sword(), resolved, ItemSourceDte::sword),
                value(item.bulletFirearm(), resolved, ItemSourceDte::bulletFirearm),
                value(item.cellEnergy(), resolved, ItemSourceDte::cellEnergy));
    }

    private static ItemTypeDto typeDefinition(String code, String primarySource, Map<String, ItemTypeDte> definitions) {
        String[] parts = code.split("\\|", -1);
        String category = parts[0];
        String source = parts.length > 1 ? parts[1] : primarySource;
        ItemTypeDte definition = definitions.get(identity(category, source));
        return definition == null ? new ItemTypeDto(code, null, null)
                : new ItemTypeDto(category, definition.name(), definition.source());
    }

    private static Integer page(ItemSourceDte item, ResolvedItemDte resolved) {
        if (item.page() != null) {
            return item.page();
        }
        var copy = item.copy();
        if (copy == null || copy.preserve() == null || !Boolean.TRUE.equals(copy.preserve().page())) {
            return null;
        }
        return preservedParentPage(resolved.copiedFrom());
    }

    private static Integer preservedParentPage(ResolvedItemDte parent) {
        if (parent == null) {
            return null;
        }
        if (parent.source().page() != null) {
            return parent.source().page();
        }
        var parentCopy = parent.source().copy();
        if (parentCopy == null || parentCopy.preserve() == null
                || !Boolean.TRUE.equals(parentCopy.preserve().page())) {
            return null;
        }
        return preservedParentPage(parent.copiedFrom());
    }

    private static ItemDamageDto damage(ItemSourceDte item, ResolvedItemDte resolved) {
        String primary = value(item.dmg1(), resolved, ItemSourceDte::dmg1);
        String versatile = value(item.dmg2(), resolved, ItemSourceDte::dmg2);
        String type = value(item.dmgType(), resolved, ItemSourceDte::dmgType);
        return primary == null && versatile == null && type == null ? null : new ItemDamageDto(primary, versatile, type);
    }

    private static ItemAttunementDto attunement(ItemSourceDte item, ResolvedItemDte resolved) {
        var required = value(item.reqAttune(), resolved, ItemSourceDte::reqAttune);
        List<ItemAttunementTagDte> tags = value(item.reqAttuneTags(), resolved, ItemSourceDte::reqAttuneTags);
        if (required == null && (tags == null || tags.isEmpty())) {
            return null;
        }
        Boolean requiredFlag = required == null ? null
                : required.flag() == null ? true : required.flag();
        List<ItemAttunementTagDto> restrictions = tags == null ? List.of() : tags.stream()
                .map(ItemMapper::attunementTag).toList();
        return new ItemAttunementDto(requiredFlag, restrictions);
    }

    private static ItemAttunementTagDto attunementTag(ItemAttunementTagDte tag) {
        return new ItemAttunementTagDto(tag.className(), tag.background(), tag.spellcasting(),
                tag.skillProficiency(), list(tag.alignment()), tag.creatureType(), tag.race(), tag.psionics(),
                tag.languageProficiency(), tag.size(), tag.intScore());
    }

    private static ItemSpellScheduleDto attachedSpells(
            com.thedndpub.data.dte.item.ItemAttachedSpellsDte value) {
        if (value == null) {
            return null;
        }
        return new ItemSpellScheduleDto(value.direct(), value.ability(), spellGrant(value.will()),
                spellGrant(value.daily()), spellGrant(value.rest()), spellGrant(value.charges()),
                spellGrant(value.limited()), value.other(), value.ritual());
    }

    private static ItemSpellGrantDto spellGrant(com.thedndpub.data.dte.item.ItemSpellGrantDte value) {
        return value == null ? null : new ItemSpellGrantDto(value.direct(), value.byUses());
    }

    private static ItemAbilityDto ability(com.thedndpub.data.dte.item.ItemAbilityDte value) {
        if (value == null) {
            return null;
        }
        var staticScores = value.staticScores();
        ItemAbilityScoresDto scores = staticScores == null ? null : new ItemAbilityScoresDto(
                staticScores.str(), staticScores.dex(), staticScores.con(), staticScores.intScore(),
                staticScores.wis(), staticScores.cha());
        List<ItemAbilityChoiceDto> choices = value.choose() == null ? List.of() : value.choose().stream()
                .map(choice -> new ItemAbilityChoiceDto(list(choice.from()), choice.count(), choice.amount()))
                .toList();
        return new ItemAbilityDto(value.str(), value.dex(), value.con(), value.intScore(), value.wis(),
                value.cha(), list(value.from()), value.count(), value.amount(), choices, scores);
    }

    private static ItemBonusesDto bonuses(ItemSourceDte item, ResolvedItemDte resolved) {
        ItemBonusesDto value = new ItemBonusesDto(
                value(item.bonusAc(), resolved, ItemSourceDte::bonusAc),
                value(item.bonusWeapon(), resolved, ItemSourceDte::bonusWeapon),
                value(item.bonusWeaponAttack(), resolved, ItemSourceDte::bonusWeaponAttack),
                value(item.bonusWeaponDamage(), resolved, ItemSourceDte::bonusWeaponDamage),
                value(item.bonusSpellAttack(), resolved, ItemSourceDte::bonusSpellAttack),
                value(item.bonusSpellSaveDc(), resolved, ItemSourceDte::bonusSpellSaveDc),
                value(item.bonusSavingThrow(), resolved, ItemSourceDte::bonusSavingThrow),
                value(item.bonusSavingThrowConcentration(), resolved, ItemSourceDte::bonusSavingThrowConcentration),
                value(item.bonusAbilityCheck(), resolved, ItemSourceDte::bonusAbilityCheck),
                value(item.bonusProficiencyBonus(), resolved, ItemSourceDte::bonusProficiencyBonus));
        return java.util.stream.Stream.of(value.armorClass(), value.weapon(), value.weaponAttack(),
                value.weaponDamage(), value.spellAttack(), value.spellSaveDc(), value.savingThrow(),
                value.savingThrowConcentration(), value.abilityCheck(), value.proficiencyBonus())
                .allMatch(java.util.Objects::isNull) ? null : value;
    }

    private static ItemSignedBonuses signedBonuses(ItemSourceDte item, ResolvedItemDte resolved) {
        ItemSignedBonuses result = new ItemSignedBonuses(
                signedBonus(value(item.bonusAc(), resolved, ItemSourceDte::bonusAc), "bonusAc"),
                signedBonus(value(item.bonusWeapon(), resolved, ItemSourceDte::bonusWeapon), "bonusWeapon"),
                signedBonus(value(item.bonusWeaponAttack(), resolved, ItemSourceDte::bonusWeaponAttack),
                        "bonusWeaponAttack"),
                signedBonus(value(item.bonusWeaponDamage(), resolved, ItemSourceDte::bonusWeaponDamage),
                        "bonusWeaponDamage"),
                signedBonus(value(item.bonusSpellAttack(), resolved, ItemSourceDte::bonusSpellAttack),
                        "bonusSpellAttack"),
                signedBonus(value(item.bonusSpellSaveDc(), resolved, ItemSourceDte::bonusSpellSaveDc),
                        "bonusSpellSaveDc"),
                signedBonus(value(item.bonusSavingThrow(), resolved, ItemSourceDte::bonusSavingThrow),
                        "bonusSavingThrow"),
                signedBonus(value(item.bonusSavingThrowConcentration(), resolved,
                        ItemSourceDte::bonusSavingThrowConcentration), "bonusSavingThrowConcentration"),
                signedBonus(value(item.bonusAbilityCheck(), resolved, ItemSourceDte::bonusAbilityCheck),
                        "bonusAbilityCheck"),
                signedBonus(value(item.bonusProficiencyBonus(), resolved, ItemSourceDte::bonusProficiencyBonus),
                        "bonusProficiencyBonus"));
        return result.isEmpty() ? null : result;
    }

    private static ItemSignedBonus signedBonus(String token, String sourceField) {
        return ItemSignedBonusParser.parse(token, sourceField);
    }

    private static ItemProtectionDto protection(ItemSourceDte item, ResolvedItemDte resolved) {
        List<String> resistance = list(value(item.resist(), resolved, ItemSourceDte::resist));
        List<String> immunity = list(value(item.immune(), resolved, ItemSourceDte::immune));
        List<String> vulnerability = list(value(item.vulnerable(), resolved, ItemSourceDte::vulnerable));
        List<String> conditionImmunity = list(value(item.conditionImmune(), resolved, ItemSourceDte::conditionImmune));
        List<String> poisonTypes = list(value(item.poisonTypes(), resolved, ItemSourceDte::poisonTypes));
        return resistance.isEmpty() && immunity.isEmpty() && vulnerability.isEmpty()
                && conditionImmunity.isEmpty() && poisonTypes.isEmpty()
                ? null : new ItemProtectionDto(resistance, immunity, vulnerability, conditionImmunity, poisonTypes);
    }

    private static List<ItemLightDto> lights(List<com.thedndpub.data.dte.item.ItemLightDte> values) {
        return values == null ? List.of() : values.stream()
                .map(value -> new ItemLightDto(value.bright(), value.dim(), value.shape())).toList();
    }

    private static ItemSpeedModificationDto speedModification(
            com.thedndpub.data.dte.item.ItemModifySpeedDte value) {
        if (value == null) {
            return null;
        }
        ItemSpeedModificationDto result = new ItemSpeedModificationDto(
                value.equal() == null ? Map.of() : value.equal(),
                numberText(value.multiply()), numberText(value.staticValues()), numberText(value.bonus()));
        return result.equal().isEmpty() && result.multiply().isEmpty() && result.staticValues().isEmpty()
                && result.bonus().isEmpty() ? null : result;
    }

    private static Map<String, ItemNumberTextDto> numberText(
            Map<String, ItemNumberTextDte> values) {
        if (values == null) {
            return Map.of();
        }
        Map<String, ItemNumberTextDto> result = new java.util.TreeMap<>();
        values.forEach((key, value) -> result.put(key, new ItemNumberTextDto(value.number(), value.text())));
        return Map.copyOf(result);
    }

    private static Boolean focusEnabled(ItemStringListDte focus) {
        return focus == null ? null : focus.enabled();
    }

    private static ItemFocus focus(ItemStringListDte focus) {
        if (focus == null) {
            return null;
        }
        if (focus.enabled() != null) {
            return new ItemBooleanFocus(focus.enabled());
        }
        return new ItemClassFocus(focus.values());
    }

    private static ItemVehicleStatisticsDto vehicleStatistics(ItemSourceDte item, ResolvedItemDte resolved) {
        ItemBarDimensionsDto dimensions = null;
        var sourceDimensions = value(item.barDimensions(), resolved, ItemSourceDte::barDimensions);
        if (sourceDimensions != null) {
            dimensions = new ItemBarDimensionsDto(sourceDimensions.l(), sourceDimensions.w(), sourceDimensions.h());
        }
        ItemVehicleStatisticsDto result = new ItemVehicleStatisticsDto(
                value(item.crew(), resolved, ItemSourceDte::crew),
                value(item.crewMin(), resolved, ItemSourceDte::crewMin),
                value(item.crewMax(), resolved, ItemSourceDte::crewMax),
                value(item.vehAc(), resolved, ItemSourceDte::vehAc),
                value(item.vehHp(), resolved, ItemSourceDte::vehHp),
                value(item.vehDmgThresh(), resolved, ItemSourceDte::vehDmgThresh),
                value(item.vehSpeed(), resolved, ItemSourceDte::vehSpeed),
                value(item.capPassenger(), resolved, ItemSourceDte::capPassenger),
                value(item.capCargo(), resolved, ItemSourceDte::capCargo),
                value(item.carryingCapacity(), resolved, ItemSourceDte::carryingCapacity),
                value(item.travelCost(), resolved, ItemSourceDte::travelCost),
                value(item.shippingCost(), resolved, ItemSourceDte::shippingCost),
                dimensions);
        return java.util.stream.Stream.of(result.crew(), result.crewMinimum(), result.crewMaximum(),
                result.armorClass(), result.hitPoints(), result.damageThreshold(), result.speed(),
                result.passengerCapacity(), result.cargoCapacity(), result.carryingCapacity(),
                result.travelCost(), result.shippingCost(), result.barDimensions())
                .allMatch(java.util.Objects::isNull) ? null : result;
    }

    private static ItemReferencesDto references(ItemSourceDte item, ResolvedItemDte resolved) {
        List<String> classFeatures = list(value(item.classFeatures(), resolved, ItemSourceDte::classFeatures));
        List<String> optionalFeatures = list(value(item.optionalfeatures(), resolved, ItemSourceDte::optionalfeatures));
        List<String> vehicles = list(value(item.seeAlsoVehicle(), resolved, ItemSourceDte::seeAlsoVehicle));
        List<String> decks = list(value(item.seeAlsoDeck(), resolved, ItemSourceDte::seeAlsoDeck));
        List<String> aliases = list(value(item.alias(), resolved, ItemSourceDte::alias));
        return classFeatures.isEmpty() && optionalFeatures.isEmpty() && vehicles.isEmpty()
                && decks.isEmpty() && aliases.isEmpty()
                ? null : new ItemReferencesDto(classFeatures, optionalFeatures, vehicles, decks, aliases);
    }

    private static ItemContainerCapacityDto container(ItemSourceDte item, ResolvedItemDte resolved) {
        var capacity = value(item.containerCapacity(), resolved, ItemSourceDte::containerCapacity);
        return capacity == null ? null : new ItemContainerCapacityDto(capacity.weight(), capacity.weightless(),
                capacity.volume(), capacity.item());
    }

    private static List<ItemPackContentDto> packContents(ItemSourceDte item, ResolvedItemDte resolved) {
        var values = value(item.packContents(), resolved, ItemSourceDte::packContents);
        return values == null ? List.of() : values.stream()
                .map(value -> new ItemPackContentDto(value.item(), value.quantity(), value.special())).toList();
    }

    private static List<String> classifications(ItemSourceDte item, ResolvedItemDte resolved) {
        List<String> result = new ArrayList<>();
        result.addAll(list(value(item.miscTags(), resolved, ItemSourceDte::miscTags)));
        var focus = value(item.focus(), resolved, ItemSourceDte::focus);
        if (focus != null) {
            result.addAll(focus.values());
        }
        return List.copyOf(result);
    }

    private static List<SourceReference> sources(List<com.thedndpub.data.dte.item.ItemSourceLocatorDte> values) {
        return values == null ? List.of() : values.stream()
                .map(value -> new SourceReference(value.source(), value.page())).toList();
    }

    private static List<ItemSourceRelationDto> reprints(
            List<com.thedndpub.data.dte.item.ItemReprintDte> values) {
        return values == null ? List.of() : values.stream()
                .map(value -> new ItemSourceRelationDto(value.uid(), value.tag())).toList();
    }

    private static List<String> splitCodes(String value) {
        return value == null || value.isBlank() ? List.of() : List.of(value);
    }

    private static String identity(String code, String source) {
        return normalize(code) + "\u0000" + normalize(source);
    }

    private static String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Cannot create an item ID from blank text");
        }
        return normalized;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item " + field + " must not be blank");
        }
        return value;
    }

    private static <T> T value(T current, ResolvedItemDte resolved, Function<ItemSourceDte, T> field) {
        if (current != null) {
            return current;
        }
        ResolvedItemDte parent = resolved.copiedFrom();
        while (parent != null) {
            T inherited = field.apply(parent.source());
            if (inherited != null) {
                return inherited;
            }
            parent = parent.copiedFrom();
        }
        return null;
    }

    private static Integer integerValue(
            com.thedndpub.data.dte.item.ItemNullableIntegerDte current,
            ResolvedItemDte resolved,
            Function<ItemSourceDte, com.thedndpub.data.dte.item.ItemNullableIntegerDte> field) {
        if (current != null && current.present()) {
            return current.value();
        }
        for (ResolvedItemDte parent = resolved.copiedFrom(); parent != null; parent = parent.copiedFrom()) {
            var inherited = field.apply(parent.source());
            if (inherited != null && inherited.present()) {
                return inherited.value();
            }
        }
        return null;
    }

    private static String stringValue(
            com.thedndpub.data.dte.item.ItemNullableStringDte current,
            ResolvedItemDte resolved,
            Function<ItemSourceDte, com.thedndpub.data.dte.item.ItemNullableStringDte> field) {
        if (current != null && current.present()) {
            return current.value();
        }
        for (ResolvedItemDte parent = resolved.copiedFrom(); parent != null; parent = parent.copiedFrom()) {
            var inherited = field.apply(parent.source());
            if (inherited != null && inherited.present()) {
                return inherited.value();
            }
        }
        return null;
    }

    private static <T> List<T> list(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
