package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassProficienciesDte(
        List<ClassSkillChoiceDte> skills,
        List<ClassProficiencyDte> weapons,
        List<ClassProficiencyDte> armor,
        List<String> tools,
        List<ClassArmorProficienciesDte> armorProficiencies,
        List<ClassToolProficienciesDte> toolProficiencies,
        List<ClassWeaponProficienciesDte> weaponProficiencies) {
    public ClassProficienciesDte {
        skills = ClassDteCollections.list(skills);
        weapons = ClassDteCollections.list(weapons);
        armor = ClassDteCollections.list(armor);
        tools = ClassDteCollections.list(tools);
        armorProficiencies = ClassDteCollections.list(armorProficiencies);
        toolProficiencies = ClassDteCollections.list(toolProficiencies);
        weaponProficiencies = ClassDteCollections.list(weaponProficiencies);
    }
}
