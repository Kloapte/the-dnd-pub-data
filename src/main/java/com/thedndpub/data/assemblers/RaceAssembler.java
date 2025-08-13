package com.thedndpub.data.assemblers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.*;
import com.thedndpub.data.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RaceAssembler {
    public RaceDto mapRaceDteToDto(RaceDte dte) {
        return this.mapRaceDteToDto(dte, dte.getName());
    }

    public RaceDto mapRaceDteToDto(RaceDte dte, String name) {
        RaceDto dto = new RaceDto();
        dto.setName(name);
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
        dto.setSkillProficiencies(this.mapSkillProficiencies(dte.getSkillProficiencies()));
        dto.setLanguageProficiencies(this.mapLanguageProficiencies(dto.getName(), dte.getLanguageProficiencies()));
        dto.setArmorProficiencies(this.mapArmorProficiencies(dte.getArmorProficiencies()));
        dto.setSoundPath(this.mapSound(dte.getSoundClip()));
        dto.setAdditionalSpells(this.mapAdditionalSpells(dte.getAdditionalSpells()));
        dto.setEntries(this.mapEntries(dte.getEntries(), dte.getName()));
        dto.setVariants(this.mapVariants(dte.get_versions()));
        return dto;
    }

    public RaceDto mapRaceCopyToDto(RaceDte dte, RaceDto original) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //Doing this to make a unlinked copy
            RaceDto copy = mapper.readValue(mapper.writeValueAsString(original), RaceDto.class);
            copy.setName(dte.getName());
            copy.setCopyOf(copy.getSource());
            copy.setSource(this.mapSource(dte));
            this.implementCopy(copy, dte.get_copy().get_mod());

            return copy;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            List<String> options = new ArrayList<>();
            for(AbilityType type : AbilityType.values()) {
                options.add(type.name());
            }
            ChooseDte choose1Bonus = new ChooseDte();
            choose1Bonus.setAmount(1);
            choose1Bonus.setCount(2);
            choose1Bonus.setFrom(options);
            ChooseDte choose2Bonus = new ChooseDte();
            choose2Bonus.setAmount(2);
            choose2Bonus.setCount(1);
            choose2Bonus.setFrom(options);

            ability.setChoices(new ArrayList<>());
            ability.getChoices().addAll(this.mapChoice(choose1Bonus, "ability_any"));
            ability.getChoices().addAll(this.mapChoice(choose2Bonus, "ability_any"));
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
            case "ability_any" -> {
                options.addAll(dte.getFrom());
            }
            case "resist", "feat", "tool_any", "skill_any", "language_any" -> {
                amount = null;
                options.addAll(dte.getFrom());
            }
            case "tool", "skill", "language" -> {
                amount = null;
                for(String option : dte.getFrom()) {
                    option = option.replaceAll("'", "").replaceAll(" ", "_");
                    switch (type) {
                        case "tool" -> options.add(ToolProficiencyType.fromString(option).name());
                        case "skill" -> options.add(SkillProficiencyType.fromString(option).name());
                        case "language" -> options.add(LanguageProficiencyType.fromString(option).name());
                    }
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
            case "dex" -> AbilityType.DEXTERITY.name();
            case "str" -> AbilityType.STRENGTH.name();
            case "con" -> AbilityType.CONSTITUTION.name();
            case "wis" -> AbilityType.WISDOM.name();
            case "cha" -> AbilityType.CHARISMA.name();
            case "int" -> AbilityType.INTELLIGENCE.name();
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

    private SkillProficiencyDto mapSkillProficiencies(List<SkillProficiencyDte> skillProficienciesDte) {
        if(skillProficienciesDte != null && !skillProficienciesDte.isEmpty()) {
            SkillProficiencyDte dte = skillProficienciesDte.getFirst();
            SkillProficiencyDto skillProficiencyDto = new SkillProficiencyDto();
            //Choose
            if(dte.getChoose() != null) {
                skillProficiencyDto.setChoices(this.mapChoice(dte.getChoose(), "skill"));
            }
            //Any
            if(dte.getAny() > 0) {
                ChooseDte choose = new ChooseDte();
                choose.setCount(dte.getAny());
                choose.setFrom(new ArrayList<>());
                for(SkillProficiencyType type : SkillProficiencyType.values()) {
                    choose.getFrom().add(type.name());
                }
                skillProficiencyDto.setChoices(this.mapChoice(choose, "skill_any"));
            }
            //Losse skills
            if(dte.hasSkillProficiency()) {
                skillProficiencyDto.setProficiencies(new ArrayList<>());
                if (dte.isAcrobatics()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.ACROBATICS);
                }
                if(dte.isAthletics()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.ATHLETICS);
                }
                if(dte.isIntimidation()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.INTIMIDATION);
                }
                if(dte.isStealth()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.STEALTH);
                }
                if(dte.isPerformance()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.PERFORMANCE);
                }
                if(dte.isPersuasion()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.PERSUASION);
                }
                if(dte.isPerception()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.PERCEPTION);
                }
                if(dte.isSurvival()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.SURVIVAL);
                }
                if(dte.isHistory()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.HISTORY);
                }
                if(dte.isNature()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.NATURE);
                }
                if(dte.isDeception()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.DECEPTION);
                }
                if(dte.isAnimalHandling()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.ANIMAL_HANDLING);
                }
                if(dte.isSleightOfHand()) {
                    skillProficiencyDto.getProficiencies().add(SkillProficiencyType.SLEIGHT_OF_HAND);
                }
            }

            if(skillProficiencyDto.getProficiencies() != null && skillProficiencyDto.getProficiencies().isEmpty()) {
                skillProficiencyDto.setProficiencies(null);
            }
            if(skillProficiencyDto.getProficiencies() == null && skillProficiencyDto.getChoices() == null) {
                return null;
            }
            return skillProficiencyDto;
        }
        return null;
    }

    private LanguageProficiencyDto mapLanguageProficiencies(String raceName, List<LanguageProficiencyDte> languageProficienciesDte) {
        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();

        if(languageProficienciesDte != null && !languageProficienciesDte.isEmpty()) {
            LanguageProficiencyDte dte = languageProficienciesDte.getFirst();

            if(dte.getChoose() != null) {
                languageProficiencyDto.setChoices(this.mapChoice(dte.getChoose(), "language"));
            }
            if(dte.getAnyStandard() > 0){
                ChooseDte choose = new ChooseDte();
                choose.setCount(dte.getAnyStandard());
                choose.setFrom(new ArrayList<>());
                for(LanguageProficiencyType type : LanguageProficiencyType.values()) {
                    if(type.isStandard()) {
                        choose.getFrom().add(type.name());
                    }
                }
                languageProficiencyDto.setChoices(this.mapChoice(choose, "language_any"));
            }
            if(dte.hasLanguageProficiency()) {
                languageProficiencyDto.setProficiencies(new ArrayList<>());
                if(dte.isCommon()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                }
                if(dte.isOther()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.OTHER);
                }
                if(dte.isAuran()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.AURAN);
                }
                if(dte.isCelestial()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.CELESTIAL);
                }
                if(dte.isGoblin()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.GOBLIN);
                }
                if(dte.isSylvan()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.SYLVAN);
                }
                if(dte.isDraconic()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.DRACONIC);
                }
                if(dte.isDwarvish()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.DWARVISH);
                }
                if(dte.isElvish()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ELVISH);
                }
                if(dte.isGiant()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.GIANT);
                }
                if(dte.isPrimordial()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.PRIMORDIAL);
                }
                if(dte.isGnomish()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.GNOMISH);
                }
                if(dte.isTerran()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.TERRAN);
                }
                if(dte.isUndercommon()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.UNDERCOMMON);
                }
                if(dte.isOrc()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ORC);
                }
                if(dte.isHalfling()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.HALFLING);
                }
                if(dte.isAquan()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.AQUAN);
                }
                if(dte.isInfernal()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.INFERNAL);
                }
                if(dte.isAbyssal()) {
                    languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ABYSSAL);
                }
            }

            return languageProficiencyDto;
        }
        else {
            //Default languages if none are known
            languageProficiencyDto.setProficiencies(new ArrayList<>());
            languageProficiencyDto.setDefaultLanguage(true);

            ChooseDte chooseDte = new ChooseDte();
            chooseDte.setCount(1);
            chooseDte.setFrom(Arrays.stream(LanguageProficiencyType.values()).map(Enum::name).toList());
            List<ChoiceDto> anyChoices = this.mapChoice(chooseDte, "language_any");

            if(raceName.toLowerCase().contains("dragonborn")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.DRACONIC);
            }
            else if(raceName.toLowerCase().contains("dwarf")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.DWARVISH);
            }
            else if(raceName.toLowerCase().contains("half-elf")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ELVISH);
                languageProficiencyDto.setChoices(anyChoices);
            }
            else if(raceName.toLowerCase().contains("elf")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ELVISH);
            }
            else if(raceName.toLowerCase().contains("gnome")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.GNOMISH);
            }
            else if(raceName.toLowerCase().contains("orc")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ORC);
            }
            else if(raceName.toLowerCase().contains("halfling")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.HALFLING);
            }
            else if(raceName.toLowerCase().contains("human")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.setChoices(anyChoices);
            }
            else if(raceName.toLowerCase().contains("tiefling")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.INFERNAL);
            }
            else if(raceName.toLowerCase().contains("aasimar")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.CELESTIAL);
            }
            else if(raceName.toLowerCase().contains("goliath")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.GIANT);
            }
            else if(raceName.toLowerCase().contains("genasi")) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.PRIMORDIAL);
            }

            if(languageProficiencyDto.getProficiencies().isEmpty()) {
                languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.COMMON);
                languageProficiencyDto.setChoices(anyChoices);
            }

            return languageProficiencyDto;
        }
    }

    private List<ArmorProficiencyType> mapArmorProficiencies(List<ArmorProficiencyDte> armorProficienciesDte) {
        if(armorProficienciesDte != null && !armorProficienciesDte.isEmpty()){
            List<ArmorProficiencyType> armorProficiencies = new ArrayList<>();
            ArmorProficiencyDte dte = armorProficienciesDte.getFirst();
            if(dte.isLight()) {
                armorProficiencies.add(ArmorProficiencyType.LIGHT);
            }
            if(dte.isMedium()) {
                armorProficiencies.add(ArmorProficiencyType.MEDIUM);
            }

            return armorProficiencies;
        }
        return null;
    }

    private String mapSound(SoundClipDte dte) {
        if(dte != null) {
            return dte.getPath();
        }
        return null;
    }

    private List<AdditionalSpellDto> mapAdditionalSpells(List<AdditionalSpellDte> additionalSpellsDte) {
        if(additionalSpellsDte != null && !additionalSpellsDte.isEmpty()) {
            List<AdditionalSpellDto> additionalSpellsDto = new ArrayList<>();

            for(AdditionalSpellDte dte: additionalSpellsDte) {
                if(dte.getKnown() != null) {
                    additionalSpellsDto.addAll(this.mapSpellDte(dte.getKnown(), dte.getAbility(), AdditionalSpellType.KNOWN, dte.getName()));
                }
                if(dte.getInnate() != null) {
                    additionalSpellsDto.addAll(this.mapSpellDte(dte.getInnate(), dte.getAbility(), AdditionalSpellType.INNATE, dte.getName()));
                }
            }
            if(!additionalSpellsDto.isEmpty()) {
                return additionalSpellsDto;
            }
            else {
                return null;
            }
        }
        return null;
    }

    private List<AdditionalSpellDto> mapSpellDte(SpellDte dte, SpellAbilityDte abilityDte, AdditionalSpellType type, String subRace) {
        List<AdditionalSpellDto> additionalSpells = new ArrayList<>();
        if(dte.getLevel1() != null) {
            additionalSpells.addAll(this.mapLevelSpellDte(dte.getLevel1(), abilityDte, type, 1, subRace));
        }
        if(dte.getLevel3() != null) {
            additionalSpells.addAll(this.mapLevelSpellDte(dte.getLevel3(), abilityDte, type, 3, subRace));
        }
        if(dte.getLevel5() != null) {
            additionalSpells.addAll(this.mapLevelSpellDte(dte.getLevel5(), abilityDte, type, 5, subRace));
        }
        if(dte.getLevelUnknown() != null) {
            additionalSpells.addAll(this.mapLevelSpellDte(dte.getLevelUnknown(), abilityDte, type, 1, subRace));
        }
        return additionalSpells;
    }

    private List<AdditionalSpellDto> mapLevelSpellDte(LevelSpellKnownDte dte, SpellAbilityDte abilityDte, AdditionalSpellType type, int level, String subRace) {
        List<AdditionalSpellDto> additionalSpells = new ArrayList<>();
        if(dte.getSpells() != null && !dte.getSpells().isEmpty()) {
            for(String spell : dte.getSpells()) {
                AdditionalSpellDto additionalSpell = new AdditionalSpellDto();
                additionalSpell.setName(spell);
                additionalSpell.setType(type);
                additionalSpell.setDaily(dte.isDaily());
                additionalSpell.setRest(dte.isRest());

                if(subRace != null) {
                    additionalSpell.setSubRaceName(subRace);
                }

                if(dte.getLevel() > 0) {
                    additionalSpell.setLevel(dte.getLevel());
                }
                else {
                    additionalSpell.setLevel(level);
                }

                if(abilityDte != null) {
                    if (abilityDte.getAbility() != null) {
                        additionalSpell.setAbility(AbilityType.fromString(this.mapAbilityFromShortToLong(abilityDte.getAbility())));
                    } else if (abilityDte.getChoose() != null) {
                        additionalSpell.setAbilityChoices(this.mapChoice(abilityDte.getChoose(), "ability"));
                    }
                }

                additionalSpells.add(additionalSpell);
            }
        }
        else if(dte.getChoose() != null){
            ChoiceDto choice = new ChoiceDto();
            choice.setFrom(List.of(dte.getChoose().split("\\|")[1]));

            AdditionalSpellDto additionalSpell = new AdditionalSpellDto();
            additionalSpell.setType(type);
            additionalSpell.setDaily(dte.isDaily());
            additionalSpell.setRest(dte.isRest());
            additionalSpell.setSpellChoices(List.of(choice));

            if(abilityDte != null) {
                if (abilityDte.getAbility() != null) {
                    additionalSpell.setAbility(AbilityType.fromString(this.mapAbilityFromShortToLong(abilityDte.getAbility())));
                } else if (abilityDte.getChoose() != null) {
                    additionalSpell.setAbilityChoices(this.mapChoice(abilityDte.getChoose(), "ability"));
                }
            }

        }
        else {
            throw new RuntimeException("Unsupported AdditionalLevelSpell");
        }

        return additionalSpells;
    }

    private List<EntryDto> mapEntries(List<EntryDte> entriesDte, String race) {
        List<EntryDto> entries = new ArrayList<>();

        if(entriesDte != null) {
            for (EntryDte dte : entriesDte) {
                EntryDto dto;
                switch (this.determineEntryType(dte)) {
                    case "entries":
                        dto = new EntryWithSubDto();
                        dto.setName(dte.getName());
                        if(dte.getEntries() != null) {
                            List<EntryDto> subItems = this.mapEntries(dte.getEntries(), race);
                            if(subItems != null) {
                                boolean hasOnlyText = true;
                                for(EntryDto subItem : subItems) {
                                    if (!subItem.getType().equals("text")) {
                                        hasOnlyText = false;
                                        break;
                                    }
                                }

                                if(hasOnlyText) {
                                    dto = new EntryTextDto();
                                    dto.setName(dte.getName());
                                    for(EntryDto subItem : subItems) {
                                        ((EntryTextDto)dto).getTextItems().addAll(((EntryTextDto)subItem).getTextItems());
                                    }
                                }
                                else {
                                    ((EntryWithSubDto)dto).getSubItems().addAll(subItems);
                                }
                            }
                        }
                        entries.add(dto);
                        break;
                    case "list":
                        if(dte.getItems() != null) {
                            dto = new EntryWithListDto();
                            dto.setName(dte.getName());
                            for (EntryDte subEntry : dte.getItems()) {
                                EntryListItemDto itemDto = new EntryListItemDto();
                                itemDto.setTitle(subEntry.getName());
                                if(subEntry.getEntries() != null) {
                                    //This is always one for some reason... if not throws an error
                                    if(subEntry.getEntries().size() > 1) {
                                        throw new RuntimeException("Has more then one item");
                                    }
                                    for(EntryDte subSubEntry : subEntry.getEntries()) {
                                        itemDto.setText(subSubEntry.getText());
                                    }
                                }
                                else if(subEntry.getText() == null) {
                                    itemDto.setText(subEntry.getEntry());
                                }
                                else {
                                    itemDto.setText(subEntry.getText());
                                }
                                ((EntryWithListDto) dto).getListItems().add(itemDto);
                            }
                            entries.add(dto);
                        }
                        else {
                            throw new RuntimeException("List but no items");
                        }
                        break;
                    case "table":
                        dto = new EntryWithTableDto();
                        ((EntryWithTableDto) dto).setTitle(dte.getCaption());
                        for(int i = 0; i < dte.getColLabels().size(); i++) {
                            ((EntryWithTableDto) dto).getHeaders().put(i, dte.getColLabels().get(i));
                        }
                        for(List<String> rowDte : dte.getRows()) {
                            HashMap<Integer, String> row = new HashMap<>();
                            for(int i = 0; i < rowDte.size(); i++) {
                                row.put(i, rowDte.get(i));
                            }
                            ((EntryWithTableDto) dto).getRows().add(row);
                        }
                        entries.add(dto);
                        break;
                    case "inset":
                        dto = new EntryTextDto();
                        dto.setName(dte.getName());
                        for (EntryDte subEntry : dte.getEntries()) {
                            ((EntryTextDto) dto).getTextItems().add(subEntry.getText());
                        }
                        entries.add(dto);
                        break;
                    case "text":
                        dto = new EntryTextDto();
                        dto.setName(dte.getName());
                        ((EntryTextDto) dto).getTextItems().add(dte.getText());
                        entries.add(dto);
                        break;
                }
            }
        }

        if(!entries.isEmpty()) {
            return entries;
        }
        else {
            return null;
        }
    }

    private String determineEntryType(EntryDte dte) {
        if(dte.getType() != null) {
            switch (dte.getType()) {
                case "entries":
                    return "entries";
                case "list":
                    return "list";
                case "table":
                    return "table";
                case "inset":
                    return "inset";
                default:
                    if (dte.getText() != null) {
                        throw new RuntimeException("Unknown type with text: " + dte.getType());
                    } else {
                        throw new RuntimeException("Unknown type: " + dte.getType());
                    }
            }
        }
        else {
            if (dte.getText() != null) {
                return "text";
            } else {
                throw new RuntimeException("Unknown type for: " + dte.getName());
            }
        }
    }

    private void implementCopy(RaceDto dto, CopyModDte copy) {
        for(CopyModEntryDte modEntry : copy.getEntries()) {

            if(modEntry.getMode().contains("replace")) {
                for(EntryDto entry: dto.getEntries()) {
                    if(entry.getName() != null && entry.getName().equals(modEntry.getReplace())) {
                        ((EntryTextDto) entry).getTextItems().clear();
                        for(CopyModEntryItemDte item : modEntry.getItems()) {
                            ((EntryTextDto) entry).getTextItems().addAll(item.getEntries());
                        }
                    }
                }
            }
            else if(modEntry.getMode().contains("append")) {
                for(CopyModEntryItemDte item : modEntry.getItems()) {
                    EntryTextDto entryTextDto = new EntryTextDto();
                    entryTextDto.setName(item.getName());
                    entryTextDto.setTextItems(item.getEntries());
                    dto.getEntries().add(entryTextDto);
                }
            }
            else {
                throw new RuntimeException("Unsupported copy impementation");
            }
        }
    }

    private List<VariantDto> mapVariants(List<VersionDte> versionsDte) {
        if(versionsDte != null) {
            //Kobold
            //Goliath
            List<VariantDto> variants = new ArrayList<>();
            for(VersionDte versionDte : versionsDte) {
                VariantDto variant = new VariantDto();
                variant.setName(versionDte.getName());
                if(versionDte.get_mod() != null) {
                    variant.setEntries(new ArrayList<>());
                    for(CopyModEntryDte entry : versionDte.get_mod().getEntries()) {
                        variant.getEntries().add(this.mapEntryVariant(entry));
                    }

                    variants.add(variant);
                }
                else if(versionDte.get_abstract() != null && versionDte.get_implementations() != null) {
                    List<VersionDte> implementedVersions = this.implementAbstact(versionDte);
                    variants.addAll(this.mapVariants(implementedVersions));
                }
                else {
                    throw new RuntimeException("Unsupported variant");
                }
            }

            return variants;
        }
        return null;
    }

    private VariantEntryDto mapEntryVariant(CopyModEntryDte entry) {
        VariantEntryDto variantEntryDto = new VariantEntryDto();
        if(entry.getMode().contains("replace")) {
            variantEntryDto.setMode("REPLACE");
            variantEntryDto.setEntryName(entry.getReplace());
        }
        else if(entry.getMode().contains("append")) {
            variantEntryDto.setMode("APPEND");
            variantEntryDto.setEntryName(entry.getReplace());
        }
        else if(entry.getMode().contains("remove")) {
            variantEntryDto.setMode("REMOVE");
            variantEntryDto.setEntryName(entry.getNames());
        }
        else {
            throw new RuntimeException("Unsupported mode");
        }

        if(entry.getItems() != null) {
            CopyModEntryItemDte item = entry.getItems().getFirst();
            EntryTextDto textEntry = new EntryTextDto();
            textEntry.setName(item.getName());
            textEntry.setTextItems(item.getEntries());
            variantEntryDto.setReplaceWith(textEntry);
        }

        return variantEntryDto;
    }

    private List<VersionDte> implementAbstact(VersionDte dteAbstract) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<VersionDte> implementedAbstracts = new ArrayList<>();

            for(ImplementationDte implementationDte : dteAbstract.get_implementations()) {
                String abstractString = mapper.writeValueAsString(dteAbstract.get_abstract());
                if(implementationDte.get_variables().getColor() != null) {
                    abstractString = abstractString.replaceAll("\\{\\{color}}", implementationDte.get_variables().getColor());
                }
                if(implementationDte.get_variables().getArea() != null) {
                    abstractString = abstractString.replaceAll("\\{\\{area}}", implementationDte.get_variables().getArea());
                }
                //TODO voor subraces maar is list?
//                if(implementationDte.get_variables().getResist() != null) {
//                    abstractString = abstractString.replaceAll("\\{\\{color}}", implementationDte.get_variables().getResist());
//                }
                if(implementationDte.get_variables().getDamageType() != null) {
                    abstractString = abstractString.replaceAll("\\{\\{damageType}}", implementationDte.get_variables().getDamageType());
                }
                if(implementationDte.get_variables().getSavingThrow() != null) {
                    abstractString = abstractString.replaceAll("\\{\\{savingThrow}}", implementationDte.get_variables().getSavingThrow());
                }
                implementedAbstracts.add(mapper.readValue(abstractString, VersionDte.class));
            }

            return implementedAbstracts;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /// //////////Subraces
    public SubraceDto mapSubRaceToRaceDto(SubraceDte subraceDte) {
        String name = subraceDte.getName();
        if(name == null) {
            name = subraceDte.getRaceName();
        }
        if(subraceDte.getAlias() != null) {
            name = subraceDte.getAlias().getFirst();
        }
        SubraceDto subrace = new SubraceDto(this.mapRaceDteToDto(subraceDte, name));
        subrace.setParent(subraceDte.getRaceName());
        subrace.setParentSource(SourceType.fromString(subraceDte.getRaceSource()));
        subrace.setOverwrite(this.mapOverwrite(subraceDte.getOverwrite()));
        subrace.setProficiencies(this.mapSkillToolLanguageProficiencies(subraceDte.getSkillToolLanguageProficiencies()));
        return subrace;
    }

    private OverwriteDto mapOverwrite(OverwriteDte dte) {
        if(dte != null) {
            OverwriteDto overwrite = new OverwriteDto();
            overwrite.setAbility(dte.isAbility());
            overwrite.setSkillProficiencies(dte.isSkillProficiencies());
            overwrite.setLanguageProficiencies(dte.isLanguageProficiencies());
            return overwrite;
        }
        return null;
    }

    private List<ChoiceDto> mapSkillToolLanguageProficiencies(List<SkillToolLanguageProficienciesDte> dteList) {
        if(dteList != null) {
            SkillToolLanguageProficienciesDte dte = dteList.getFirst();
            List<ChoiceDto> choices = new ArrayList<>();

            for(int i = 0; i < dte.getChoose().getFirst().getCount(); i++) {
                ChoiceDto choice = new ChoiceDto();
                List<String> from = new ArrayList<>();
                for(SkillProficiencyType type : SkillProficiencyType.values()) {
                    from.add("skill_" + type.name());
                }
                for(ToolProficiencyType type : ToolProficiencyType.values()) {
                    from.add("tool_" + type.name());
                }
                choice.setFrom(from);
                choices.add(choice);
            }

            return choices;
        }
        else {
            return null;
        }
    }

    public RaceDto mapSubraceIntoParent(RaceDto parent, SubraceDto subrace) {
        //Keep parent as base
        SubraceDto race = new SubraceDto(parent);
        //Add name and subrace name together
        race.setName(parent.getName() + " - " + subrace.getName());

        //Override if applicable in overwrite
        //If not add on top, except language if default
        if(subrace.getOverwrite() != null) {
            if(subrace.getOverwrite().isAbility()) {
                race.setAbility(subrace.getAbility());
            }
            else if(subrace.getAbility() != null) {
                //adding
                if(subrace.getAbility().getChoices() != null) {
                    race.getAbility().getChoices().addAll(subrace.getAbility().getChoices());
                }
                if(subrace.getAbility().getCharisma() != null) {
                    race.getAbility().setCharisma(subrace.getAbility().getCharisma());
                }
                if(subrace.getAbility().getConstitution() != null) {
                    race.getAbility().setConstitution(subrace.getAbility().getConstitution());
                }
                if(subrace.getAbility().getStrength() != null) {
                    race.getAbility().setStrength(subrace.getAbility().getStrength());
                }
                if(subrace.getAbility().getDexterity() != null) {
                    race.getAbility().setDexterity(subrace.getAbility().getDexterity());
                }
                if(subrace.getAbility().getIntelligence() != null) {
                    race.getAbility().setIntelligence(subrace.getAbility().getIntelligence());
                }
                if(subrace.getAbility().getWisdom() != null) {
                    race.getAbility().setWisdom(subrace.getAbility().getWisdom());
                }
            }

            if(subrace.getOverwrite().isSkillProficiencies()) {
                race.setSkillProficiencies(subrace.getSkillProficiencies());
            }
            else if(subrace.getSkillProficiencies() != null) {
                if(subrace.getSkillProficiencies().getChoices() != null) {
                    race.getSkillProficiencies().setChoices(subrace.getSkillProficiencies().getChoices());
                }

                if(subrace.getSkillProficiencies().getProficiencies() != null) {
                    if(race.getSkillProficiencies() == null || race.getSkillProficiencies().getProficiencies() == null) {
                        race.setSkillProficiencies(new SkillProficiencyDto());
                        race.getSkillProficiencies().setProficiencies(subrace.getSkillProficiencies().getProficiencies());
                    }
                    race.getSkillProficiencies().getProficiencies().addAll(subrace.getSkillProficiencies().getProficiencies());
                }
            }

            if(subrace.getOverwrite().isLanguageProficiencies()) {
                race.setLanguageProficiencies(subrace.getLanguageProficiencies());
            }
            else if(subrace.getLanguageProficiencies() != null && !subrace.getLanguageProficiencies().isDefaultLanguage()) {
                if(subrace.getLanguageProficiencies().getChoices() != null) {
                    race.getLanguageProficiencies().getChoices().addAll(subrace.getLanguageProficiencies().getChoices());
                }

                if(subrace.getLanguageProficiencies().getProficiencies() != null) {
                    race.getLanguageProficiencies().getProficiencies().addAll(subrace.getLanguageProficiencies().getProficiencies());
                }

            }
        }

        //proficiencies on top of others (except ones from override)
        //weapon
        if(subrace.getWeaponProficiencies() != null) {
            if(subrace.getWeaponProficiencies().getChoices() != null) {
                race.getWeaponProficiencies().getChoices().addAll(subrace.getWeaponProficiencies().getChoices());
            }

            if(subrace.getWeaponProficiencies().getProficiencies() != null) {
                if(race.getWeaponProficiencies() == null || race.getWeaponProficiencies().getProficiencies() == null) {
                    race.setWeaponProficiencies(new WeaponProficiencyDto());
                    race.getWeaponProficiencies().setProficiencies(subrace.getWeaponProficiencies().getProficiencies());
                }
                else {
                    race.getWeaponProficiencies().getProficiencies().addAll(subrace.getWeaponProficiencies().getProficiencies());
                }
            }
        }
        //tool
        if(subrace.getToolProficiencies() != null) {
            if(subrace.getToolProficiencies().getChoices() != null) {
                if(race.getToolProficiencies() == null || race.getToolProficiencies().getChoices() == null) {
                    race.setToolProficiencies(new ToolProficiencyDto());
                    race.getToolProficiencies().setChoices(new ArrayList<>());
                }
                race.getToolProficiencies().getChoices().addAll(subrace.getToolProficiencies().getChoices());
            }

            if(subrace.getToolProficiencies().getProficiencies() != null) {
                if (race.getToolProficiencies() == null || race.getToolProficiencies().getProficiencies() == null) {
                    race.setToolProficiencies(new ToolProficiencyDto());
                    race.getToolProficiencies().setProficiencies(subrace.getToolProficiencies().getProficiencies());
                }
                else {
                    race.getToolProficiencies().getProficiencies().addAll(subrace.getToolProficiencies().getProficiencies());
                }
            }
        }
        //armor
        if(subrace.getArmorProficiencies() != null) {
            if(race.getArmorProficiencies() == null) {
                race.setArmorProficiencies(subrace.getArmorProficiencies());
            }
            else {
                race.getArmorProficiencies().addAll(subrace.getArmorProficiencies());
            }
        }

        //proficiencies choice list
        if(subrace.getProficiencies() != null) {
            if(race.getProficiencies() == null) {
                race.setProficiencies(subrace.getProficiencies());
            }
            else {
                race.getProficiencies().addAll(subrace.getProficiencies());
            }
        }

        //override movement only if not estimated
        if(subrace.getMovement() != null) {
            if(!subrace.getMovement().isWalkEstimated() && !subrace.getMovement().isFlyEstimated()) {
                race.setMovement(subrace.getMovement());
            }
        }

        //add spells
        if(subrace.getAdditionalSpells() != null) {
            if(race.getAdditionalSpells() == null) {
                race.setAdditionalSpells(subrace.getAdditionalSpells());
            }
            else {
                race.getAdditionalSpells().addAll(subrace.getAdditionalSpells());
            }
        }

        //add entries
        if(subrace.getEntries() != null) {
            race.getEntries().addAll(subrace.getEntries());
        }

        //replace variants if present
        if(subrace.getVariants() != null) {
            System.out.println("Subrace has variants: " + race.getName());
            race.setVariants(subrace.getVariants());
        }

        //Adding modifiers
        if(subrace.getModifiers() != null) {
            if(race.getModifiers() == null) {
                race.setModifiers(subrace.getModifiers());
            }
            else {
                //This never hapens
                throw new RuntimeException("Parent already had modifiers");
            }
        }

        //creature type? --> none have any
//        if(subrace.getCreatureType() != null) {
//            System.out.println("Subrace has creature type: " + race.getName());
//        }

        return race;
    }

    public RaceDto mapVariantIntoParent(RaceDto parent, VariantDto variant) {
        parent.setName(mapVariantName(parent.getName(), variant.getName()));
        if(variant.getSource() != null) {
            parent.getSource().setSource(variant.getSource());
            parent.getSource().setDescription(variant.getSource().getName());
        }
        List<EntryDto> finalEntries = new ArrayList<>();

        for(VariantEntryDto variantEntryDto : variant.getEntries()) {
            if(variantEntryDto.getMode().equals("APPEND")) {
                finalEntries.add(variantEntryDto.getReplaceWith());
            }
        }

        variant.getEntries().removeAll(finalEntries);

        for(EntryDto parentEntry : parent.getEntries()) {
            boolean isModified = false;
            for(VariantEntryDto variantEntry : variant.getEntries()) {
                if(variantEntry.getEntryName() != null) {
                    if (variantEntry.getEntryName().equalsIgnoreCase(parentEntry.getName())) {
                        if(variantEntry.getMode().equals("REPLACE")) {
                            isModified = true;
                            finalEntries.add(variantEntry.getReplaceWith());
                        }

                        if(variantEntry.getMode().equals("REMOVE")) {
                            isModified = true;
                            //this simply makes it so it is not readded and therefore removed
                        }

                    }
                }
            }

            if(!isModified) {
                finalEntries.add(parentEntry);
            }
        }

        parent.setEntries(finalEntries);
        parent.setVariants(null);

        return parent;
    }

    private String mapVariantName(String parentName, String variantName) {
        variantName = variantName.replaceAll(parentName, "");
        variantName = variantName.replaceAll("Variant;", "");
        variantName = variantName.replaceAll(";", "");
        variantName = variantName.replaceAll("Dragonborn", ""); //special for dragonborn names deduplication
        variantName = variantName.replaceAll("Gem", ""); //special for dragonborn names deduplication
        variantName = variantName.replaceAll("Metallic", ""); //special for dragonborn names deduplication
        variantName = variantName.replaceAll("Chromatic", ""); //special for dragonborn names deduplication
        variantName = variantName.replaceAll("\\(", "");
        variantName = variantName.replaceAll("\\)", "");
        variantName = variantName.trim();



        return parentName + " - " + variantName.replaceAll(" {2}", "");
    }
}

//TODO map variant in main race