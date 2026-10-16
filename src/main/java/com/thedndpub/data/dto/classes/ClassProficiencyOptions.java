package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassProficiencyOptions(
        List<ClassSkillChoice> skills,
        List<ClassProficiencyGrant> weapons,
        List<ClassProficiencyGrant> armor,
        List<ClassArmorProficiencySet> armorPackages,
        List<ClassToolProficiencySet> toolPackages,
        List<ClassWeaponProficiencySet> weaponPackages) {
    public ClassProficiencyOptions {
        skills = skills == null ? List.of() : List.copyOf(skills);
        weapons = weapons == null ? List.of() : List.copyOf(weapons);
        armor = armor == null ? List.of() : List.copyOf(armor);
        armorPackages = armorPackages == null ? List.of() : List.copyOf(armorPackages);
        toolPackages = toolPackages == null ? List.of() : List.copyOf(toolPackages);
        weaponPackages = weaponPackages == null ? List.of() : List.copyOf(weaponPackages);
    }
}
