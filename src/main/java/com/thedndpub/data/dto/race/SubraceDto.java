package com.thedndpub.data.dto.race;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubraceDto extends RaceDto {
    private String parent;
    private SourceType parentSource;
    private OverwriteDto overwrite;

    public SubraceDto() {

    }

    public SubraceDto(RaceDto raceDto) {
        this.setName(raceDto.getName());
        this.setSource(raceDto.getSource());
        this.setCreatureType(raceDto.getCreatureType());
        this.setAge(raceDto.getAge());
        this.setVision(raceDto.getVision());
        this.setMovement(raceDto.getMovement());
        this.setSize(raceDto.getSize());
        this.setAbility(raceDto.getAbility());
        this.setModifiers(raceDto.getModifiers());
        this.setFeat(raceDto.getFeat());
        this.setWeaponProficiencies(raceDto.getWeaponProficiencies());
        this.setToolProficiencies(raceDto.getToolProficiencies());
        this.setSkillProficiencies(raceDto.getSkillProficiencies());
        this.setLanguageProficiencies(raceDto.getLanguageProficiencies());
        this.setArmorProficiencies(raceDto.getArmorProficiencies());
        this.setSoundPath(raceDto.getSoundPath());
        this.setAdditionalSpells(raceDto.getAdditionalSpells());
        this.setEntries(raceDto.getEntries());
        this.setVariants(raceDto.getVariants());
        this.setChildOf(raceDto.getChildOf());
    }
}
