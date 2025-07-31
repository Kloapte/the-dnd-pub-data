package com.thedndpub.data.assemblers;

import com.thedndpub.data.dte.race.*;
import com.thedndpub.data.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class RaceAssembler {
    public RaceDto mapRaceDteToDto(RaceDte dte) {
        RaceDto dto = new RaceDto();
        dto.setName(dte.getName());
        dto.setSource(this.mapSource(dte));
        dto.setCreatureType(this.mapCreatureType(dte.getCreatureTypes(), dte.getCreatureTypeTags()));
        dto.setAge(this.mapAge(dte.getAge()));
        dto.setVision(this.mapVision(dte.getDarkvision(), dte.getBlindsight()));
        dto.setMovement(this.mapMovement(dte.getSpeed(), dte.getSize()));
        dto.setSize(this.mapSize(dte.getSize(), dte.getHeightAndWeight(), dte.getSizeEntry()));
        dto.setAbility(this.mapAbility(dte.getAbility()));
        dto.setModifiers(this.mapModifiers(dte.getResist(), dte.getImmune(), dte.getVulnerable()));
        dto.setFeat(this.mapFeats(dte.getFeats()));
        dto.setWeaponProficiencies(this.mapWeaponProficiencies(dte.getWeaponProficiencies()));
        dto.setToolProficiencies(this.mapToolProficiencies(dte.getToolProficiencies()));
        return dto;
    }


    private SourceDto mapSource(RaceDte dte) {
        SourceDto dto = new SourceDto();
        dto.setSource(SourceType.fromString(dte.getSource()));
        dto.setDescription(dto.getSource().getName());
        dto.setLicense(this.mapLicense(dte.isSrd() || dte.isSrd52(), dte.isBasicRules() || dte.isBasicRules2024()));
        if(dte.getOtherSources() != null && !dte.getOtherSources().isEmpty()) {
            dto.setOtherSources(new HashMap<>());
            for(OtherSourceDte otherSource : dte.getOtherSources()) {
                dto.getOtherSources().put(SourceType.fromString(otherSource.getSource()), otherSource.getPage());
            }
        }
        if(dte.getEdition() != null) {
            dto.setEdition(dte.getEdition());
        }
        if(dte.getPage() != null) {
            dto.setPage(dte.getPage());
        }
        if(dte.getReprintedAs() != null && !dte.getReprintedAs().isEmpty()) {
            dto.setReprintedAs(new ArrayList<>());
            for(String reprintedAs : dte.getReprintedAs()) {
                dto.getReprintedAs().add(SourceType.fromString(reprintedAs.split("\\|")[1]));
            }
        }
        return dto;

    }

    private LicenseType mapLicense(boolean srd, boolean basic) {
        if(srd) {
            return LicenseType.OPEN;
        }
        if(basic) {
            return LicenseType.BASIC;
        }
        return LicenseType.RESTRICTED;
    }

    private VisionDto mapVision(int darkvision, int blindsight) {
        VisionDto vision = new VisionDto();
        boolean hasValue = false;
        if(blindsight > 0) {
            vision.setBlindsight(blindsight);
            hasValue = true;
        }
        if(darkvision > 0) {
            vision.setDarkvision(darkvision);
            hasValue = true;
        }

        if(hasValue) {
            return vision;
        }
        return null;
    }

    private MovementDto mapMovement(SpeedDte dte, List<String> sizes) {
        MovementDto movement = new MovementDto();
        if(dte != null && dte.getWalk() != null && dte.getWalk() > 0) {
            movement.setWalk(dte.getWalk());
        }
        else {
            movement.setWalk(this.determineWalkSpeed(sizes));
            movement.setWalkEstimated(true);
        }

        if(dte != null && dte.getFlySpeed() != null && dte.getFlySpeed() > 0) {
            movement.setFly(dte.getFlySpeed());
        }
        else if(dte != null && dte.getFlyAvailable() != null && dte.getFlyAvailable()){
            movement.setFly(movement.getWalk());
            movement.setFlyEstimated(true);
            movement.setFlyAvailable(true);
        }

        return movement;
    }

    private int determineWalkSpeed(List<String> sizes) {
        if(sizes != null) {
            if (sizes.contains("L")) {
                return 30;
            } else if (sizes.contains("M")) {
                return 30;
            } else if (sizes.contains("S")) {
                return 25;
            }
        }
        return 30;
    }

    private SizeDto mapSize(List<String> sizes, HeightAndWeightDte heightAndWeightDte, SizeEntryDte sizeEntryDte) {
        SizeDto size = new SizeDto();
        size.setSizeTags(sizes);
        if(heightAndWeightDte != null) {
            size.setBaseWeight(heightAndWeightDte.getBaseWeight());
            size.setWeightMod(heightAndWeightDte.getWeightMod());
            size.setBaseHeight(heightAndWeightDte.getBaseHeight());
            size.setHeightMod(heightAndWeightDte.getHeightMod());
        }
        if(sizeEntryDte != null && sizeEntryDte.getEntries() != null) {
            for(String sizeEntry : sizeEntryDte.getEntries()) {
                String sizeEntryFirstLetter = sizeEntry.substring(0, 1);
                if(!size.getSizeTags().contains(sizeEntryFirstLetter)) {
                    size.getSizeTags().add(sizeEntryFirstLetter);
                }
            }
        }
        return size;
    }

    private AbilityDto mapAbility(List<AbilityDte> abilitiesDte) {
        AbilityDto ability = new AbilityDto();
        if(abilitiesDte == null || abilitiesDte.isEmpty()) {
            ability.setChoices(List.of(ChoiceDto.ANY_ABILITY_2(), ChoiceDto.ANY_ABILITY_1(), ChoiceDto.ANY_ABILITY_1()));
        }
        else {
            AbilityDte first = abilitiesDte.getFirst();

            if(first.getChoose() != null) {
                ability.setChoices(this.mapChoice(abilitiesDte.getFirst().getChoose(), "ability"));
            }

            if(first.getCon() != 0) {
                ability.setConstitution(first.getCon());
            }
            if(first.getCha() != 0) {
                ability.setConstitution(first.getCha());
            }
            if(first.getDex() != 0) {
                ability.setConstitution(first.getDex());
            }
            if(first.getIntel() != 0) {
                ability.setConstitution(first.getIntel());
            }
            if(first.getStr() != 0) {
                ability.setConstitution(first.getStr());
            }
            if(first.getWis() != 0) {
                ability.setConstitution(first.getWis());
            }
        }
        return ability;
    }

    private List<ChoiceDto> mapChoice(ChooseDte dte, String type) {


        List<String> options = new ArrayList<>();
        Integer amount = 1;
        int count = 1;
        switch (type) {
            case "ability" -> {
                for (String option : dte.getFrom()) {
                    options.add(this.mapAbilityFromShortToLong(option));
                }
            }
            case "resist", "feat", "tool_any" -> {
                amount = null;
                options.addAll(dte.getFrom());
            }
            case "tool" -> {
                amount = null;
                for(String option : dte.getFrom()) {
                    option = option.replaceAll("'", "").replaceAll(" ", "_");
                    options.add(ToolProficiencyType.fromString(option).name());
                }
            }
            case "weapon" -> {
                if (dte.getFromFilter() != null) {
                    // "type=martial weapon|miscellaneous=mundane"
                    List<String> filterOptions = Arrays.stream(dte.getFromFilter().split("\\|")).toList();
                    for (String filterOption : filterOptions) {
                        //"type=martial weapon
                        String weaponTypeString = filterOption.split("=")[1].replaceAll(" ", "_");
                        options.add(WeaponProficiencyType.fromString(weaponTypeString).toString());
                        amount = null;
                    }
                } else {
                    throw new RuntimeException("Weapon choice contains no filter, unsupported");
                }
            }
        }

        if(dte.getAmount() != 0) {
            amount = dte.getAmount();
        }

        if(dte.getCount() != 0) {
            count = dte.getCount();
        }

        List<ChoiceDto> choices = new ArrayList<>();

        for(int i = 0; i < count; i++) {
            choices.add(new ChoiceDto(options, amount));
        }

        return choices;
    }

    private String mapAbilityFromShortToLong(String abilityName) {
        return switch (abilityName) {
            case "dex" -> "dexterity";
            case "str" -> "strength";
            case "con" -> "constitution";
            case "wis" -> "wisdom";
            case "cha" -> "charisma";
            case "int" -> "intelligence";
            default -> "";
        };
    }

    private WeaponProficiencyDto mapWeaponProficiencies(List<WeaponProficiencyDte> dteList) {
        if(dteList != null && !dteList.isEmpty()) {
            WeaponProficiencyDto weaponProficiency = new WeaponProficiencyDto();

            WeaponProficiencyDte dte = dteList.getFirst();
            if(dte.getChoose() != null) {
                weaponProficiency.setChoices(this.mapChoice(dte.getChoose(), "weapon"));
            }
            else {
                weaponProficiency.setProficiencies(new ArrayList<>());

                if(dte.isBattleaxe()) {
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.BATTLEAXE);
                }
                if(dte.isHandaxe()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.HANDAXE);
                }
                if(dte.isLightHammer()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.LIGHT_HAMMER);
                }
                if(dte.isWarhammer()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.WARHAMMER);
                }
                if(dte.isLongsword()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.LONGSWORD);
                }
                if(dte.isShortsword()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.SHORTSWORD);
                }
                if(dte.isLongbow()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.LONGBOW);
                }
                if(dte.isFirearms()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.FIREARMS);
                }
                if(dte.isSpear()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.SPEAR);
                }
                if(dte.isJavelin()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.JAVELIN);
                }
                if(dte.isRapier()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.RAPIER);
                }
                if(dte.isHandCrossbow()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.HAND_CROSSBOW);
                }
                if(dte.isLightCrossbow()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.LIGHT_CROSSBOW);
                }
                if(dte.isTrident()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.TRIDENT);
                }
                if(dte.isNet()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.NET);
                }
                if(dte.isGreatsword()){
                    weaponProficiency.getProficiencies().add(WeaponProficiencyType.GREATSWORD);
                }
            }

            return weaponProficiency;
        }

        return null;
    }

    private CreatureTypeDto mapCreatureType(List<String> creatureTypesDte, List<String> creatureTypeTagsDte) {
        if(creatureTypesDte != null || creatureTypeTagsDte != null) {
            CreatureTypeDto creatureType = new CreatureTypeDto();
            if(creatureTypesDte != null && !creatureTypesDte.isEmpty()) {
                creatureType.setType(creatureTypesDte.getFirst());
            }
            if(creatureTypeTagsDte != null && !creatureTypeTagsDte.isEmpty()) {
                creatureType.setTag(creatureTypeTagsDte.getFirst());
            }

            return creatureType;
        }
        return null;
    }

    private AgeDto mapAge(AgeDte dte) {
        if(dte != null) {
            AgeDto ageDto = new AgeDto();
            if(dte.getMax() != null) {
                ageDto.setMax(dte.getMax());
            }
            if(dte.getMature() != null) {
                ageDto.setMature(dte.getMature());
            }
            return ageDto;
        }
        return null;
    }

    private ModifiersDto mapModifiers(ResistDte resistDte, List<String> immunitiesDte, List<String> vulnerabilitiesDte) {
        if(resistDte != null || immunitiesDte != null || vulnerabilitiesDte != null){
            ModifiersDto modifiersDto = new ModifiersDto();

            if(resistDte != null) {
                if(resistDte.getResist() != null) {
                    modifiersDto.setResists(resistDte.getResist());
                }
                if(resistDte.getChoose() != null) {
                    this.mapChoice(resistDte.getChoose(), "resist");
                }
            }
            if(immunitiesDte != null && !immunitiesDte.isEmpty()) {
                modifiersDto.setImmunities(immunitiesDte);
            }
            if(vulnerabilitiesDte != null && !vulnerabilitiesDte.isEmpty()) {
                modifiersDto.setVulnerabilities(vulnerabilitiesDte);
            }

            return modifiersDto;
        }
        return null;
    }

    private ChoiceDto mapFeats(List<FeatDte> feats) {
        if(feats != null && !feats.isEmpty()) {
            ChooseDte dte = new ChooseDte();
            dte.setCount(1);
            dte.setFrom(List.of("any"));
            return this.mapChoice(dte, "feat").getFirst();
        }
        return null;
    }

    private ToolProficiencyDto mapToolProficiencies(List<ToolProficiencyDte> toolProficienciesDte) {
        if(toolProficienciesDte != null && !toolProficienciesDte.isEmpty()) {
            ToolProficiencyDto dto = new ToolProficiencyDto();
            for(ToolProficiencyDte dte : toolProficienciesDte) {
                if(dte.getAny() > 0) {
                    ChooseDte choose = new ChooseDte();
                    choose.setCount(dte.getAny());
                    choose.setFrom(List.of(ToolProficiencyType.ANY.name()));
                    dto.setChoices(this.mapChoice(choose, "tool_any"));
                }
                if(dte.getAnyArtisansTool() > 0){
                    ChooseDte choose = new ChooseDte();
                    choose.setCount(dte.getAnyArtisansTool());
                    choose.setFrom(List.of(ToolProficiencyType.ANY_ARTISAN.name()));
                    dto.setChoices(this.mapChoice(choose, "tool_any"));
                }
                if(dte.getAnyMusicalInstrument() > 0){
                    ChooseDte choose = new ChooseDte();
                    choose.setCount(dte.getAnyMusicalInstrument());
                    choose.setFrom(List.of(ToolProficiencyType.ANY_MUSICAL_INSTRUMENT.name()));
                    dto.setChoices(this.mapChoice(choose, "tool_any"));
                }
                if(dte.getChoose() != null) {
                    dto.setChoices(this.mapChoice(dte.getChoose(), "tool"));
                }

                if(dte.isNavigatorsTools() || dte.isThievesTools() || dte.isTinkersTools() || dte.isPoisonersKit()) {
                    dto.setProficiencies(new ArrayList<>());

                    if(dte.isNavigatorsTools()) {
                        dto.getProficiencies().add(ToolProficiencyType.NAVIGATORS_TOOLS);
                    }
                    if(dte.isThievesTools()) {
                        dto.getProficiencies().add(ToolProficiencyType.THIEVES_TOOLS);
                    }
                    if(dte.isTinkersTools()) {
                        dto.getProficiencies().add(ToolProficiencyType.TINKERS_TOOLS);
                    }
                    if(dte.isPoisonersKit()) {
                        dto.getProficiencies().add(ToolProficiencyType.POISONERS_KIT);
                    }
                }
            }

            return dto;
        }
        return null;
    }
}

//TODO toolprofs
//TODO skillprofs
//TODO langprofs
//TODO armorprofs
//TODO additionalSpells
//TODO sound / image
//TODO entries
//TODO versions / subraces
//TOOD copy?