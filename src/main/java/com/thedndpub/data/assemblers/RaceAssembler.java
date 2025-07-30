package com.thedndpub.data.assemblers;

import com.thedndpub.data.dte.race.*;
import com.thedndpub.data.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RaceAssembler {
    public RaceDto mapRaceDteToDto(RaceDte dte) {
        RaceDto dto = new RaceDto();
        dto.setName(dte.getName());
        dto.setSource(this.mapSource(dte.getSource()));
        dto.setSourceDescription(this.mapSource(dte.getSource()).getSourceName());
        dto.setVision(this.mapVision(dte.getDarkvision(), dte.getBlindsight()));
        dto.setMovement(this.mapMovement(dte.getSpeed(), dte.getSize()));
        dto.setSize(this.mapSize(dte.getSize(), dte.getHeightAndWeight()));
        dto.setAbility(this.mapAbility(dte.getAbility()));
        dto.setWeaponProficiencies(this.mapWeaponProficiencies(dte.getWeaponProficiencies()));
        return dto;
    }

    private SourceType mapSource(String dte) {
        return SourceType.fromString(dte);
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

    private SizeDto mapSize(List<String> sizes, HeightAndWeightDte heightAndWeightDte) {
        SizeDto size = new SizeDto();
        size.setSizeTags(sizes);
        if(heightAndWeightDte != null) {
            size.setBaseWeight(heightAndWeightDte.getBaseWeight());
            size.setWeightMod(heightAndWeightDte.getWeightMod());
            size.setBaseHeight(heightAndWeightDte.getBaseHeight());
            size.setHeightMod(heightAndWeightDte.getHeightMod());
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
        Integer count = 1;
        if(type.equals("ability")) {
            for(String option : dte.getFrom()) {
                options.add(this.mapAbilityFromShortToLong(option));
            }
        }
        else if(type.equals("weapon")) {
            if(dte.getFromFilter() != null){
                // "type=martial weapon|miscellaneous=mundane"
                List<String> filterOptions = Arrays.stream(dte.getFromFilter().split("\\|")).toList();
                for(String filterOption : filterOptions) {
                    //"type=martial weapon
                    String weaponTypeString = filterOption.split("=")[1].replaceAll(" ", "_");
                    options.add(WeaponProficiencyType.fromString(weaponTypeString).toString());
                    amount = null;
                }
            }
            else {
                throw new RuntimeException("Weapon choice contains no filter, unsupported");
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
}
