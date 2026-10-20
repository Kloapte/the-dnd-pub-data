package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellScaling;
import com.thedndpub.data.dto.spell.SpellScalingValue;
import com.thedndpub.data.mechanics.model.CharacterLevelDamageTier;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhbCantripDamageScalingTests {
    private static final String RAY_PROSE = "The spell's damage increases by {@damage 1d8} when you reach "
            + "5th level ({@damage 2d8}), 11th level ({@damage 3d8}), and 17th level ({@damage 4d8}).";

    @Test
    void parsesCompleteCharacterLevelDamageProgression() {
        var scaling = PhbCantripDamageScaling.parse(
                raySchedule("1d8", "2d8", "3d8", "4d8"), "cold damage", RAY_PROSE,
                new DiceAmount(1, 8, 0));

        assertEquals(List.of(
                        new CharacterLevelDamageTier(5, new DiceAmount(2, 8, 0)),
                        new CharacterLevelDamageTier(11, new DiceAmount(3, 8, 0)),
                        new CharacterLevelDamageTier(17, new DiceAmount(4, 8, 0))),
                scaling.tiers());
    }

    @Test
    void rejectsMalformedScalingFormula() {
        assertThrows(SpellPatternMismatch.class, () -> PhbCantripDamageScaling.parse(
                raySchedule("1d8", "2d8+1", "3d8", "4d8"), "cold damage", RAY_PROSE,
                new DiceAmount(1, 8, 0)));
    }

    @Test
    void rejectsIncompleteThresholdList() {
        assertThrows(SpellPatternMismatch.class, () -> PhbCantripDamageScaling.parse(
                List.of(new SpellScaling("cold damage", List.of(
                        new SpellScalingValue("1", "1d8"),
                        new SpellScalingValue("5", "2d8"),
                        new SpellScalingValue("11", "3d8")))),
                "cold damage", RAY_PROSE, new DiceAmount(1, 8, 0)));
    }

    @Test
    void rejectsStructuredAndAuthoredProgressionConflict() {
        assertThrows(SpellPatternMismatch.class, () -> PhbCantripDamageScaling.parse(
                raySchedule("1d8", "3d8", "3d8", "4d8"), "cold damage", RAY_PROSE,
                new DiceAmount(1, 8, 0)));
    }

    @Test
    void rejectsUnsupportedCharacterLevelThresholdsAndSlotScalingProse() {
        assertThrows(SpellPatternMismatch.class, () -> PhbCantripDamageScaling.parse(
                List.of(new SpellScaling("cold damage", List.of(
                        new SpellScalingValue("1", "1d8"),
                        new SpellScalingValue("6", "2d8"),
                        new SpellScalingValue("11", "3d8"),
                        new SpellScalingValue("17", "4d8")))),
                "cold damage", RAY_PROSE, new DiceAmount(1, 8, 0)));
        assertThrows(SpellPatternMismatch.class, () -> PhbCantripDamageScaling.parse(
                raySchedule("1d8", "2d8", "3d8", "4d8"), "cold damage",
                "The spell's damage increases by {@damage 1d8} for each slot level above 1st.",
                new DiceAmount(1, 8, 0)));
    }

    @Test
    void damageModelRejectsTiersThatConflictWithItsBaseDice() {
        var scaling = PhbCantripDamageScaling.parse(
                raySchedule("1d8", "2d8", "3d8", "4d8"), "cold damage", RAY_PROSE,
                new DiceAmount(1, 8, 0));
        assertThrows(IllegalArgumentException.class, () -> new DamageMechanic(new DiceAmount(1, 8, 0),
                new FixedDamageType("cold"), new com.thedndpub.data.mechanics.model.CharacterLevelDamageScaling(
                List.of(new CharacterLevelDamageTier(5, new DiceAmount(2, 10, 0))))));
        assertEquals(3, scaling.tiers().size());
    }

    private List<SpellScaling> raySchedule(String base, String levelFive, String levelEleven, String levelSeventeen) {
        return List.of(new SpellScaling("cold damage", List.of(
                new SpellScalingValue("1", base),
                new SpellScalingValue("5", levelFive),
                new SpellScalingValue("11", levelEleven),
                new SpellScalingValue("17", levelSeventeen))));
    }
}
