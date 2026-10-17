package com.thedndpub.data.dto.item;

/** Signed bonus values, kept separate by their authored Item source fields. */
public record ItemSignedBonuses(
        ItemSignedBonus bonusAc,
        ItemSignedBonus bonusWeapon,
        ItemSignedBonus bonusWeaponAttack,
        ItemSignedBonus bonusWeaponDamage,
        ItemSignedBonus bonusSpellAttack,
        ItemSignedBonus bonusSpellSaveDc,
        ItemSignedBonus bonusSavingThrow,
        ItemSignedBonus bonusSavingThrowConcentration,
        ItemSignedBonus bonusAbilityCheck,
        ItemSignedBonus bonusProficiencyBonus) {
    public boolean isEmpty() {
        return bonusAc == null
                && bonusWeapon == null
                && bonusWeaponAttack == null
                && bonusWeaponDamage == null
                && bonusSpellAttack == null
                && bonusSpellSaveDc == null
                && bonusSavingThrow == null
                && bonusSavingThrowConcentration == null
                && bonusAbilityCheck == null
                && bonusProficiencyBonus == null;
    }
}
