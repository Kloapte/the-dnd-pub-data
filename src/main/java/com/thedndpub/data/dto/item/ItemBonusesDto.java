package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemBonusesDto(
        String armorClass,
        String weapon,
        String weaponAttack,
        String weaponDamage,
        String spellAttack,
        String spellSaveDc,
        String savingThrow,
        String savingThrowConcentration,
        String abilityCheck,
        String proficiencyBonus) {
}
