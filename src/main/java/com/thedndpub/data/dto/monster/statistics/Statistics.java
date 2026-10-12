package com.thedndpub.data.dto.monster.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Statistics(AbilityScores abilities, Initiative initiative, List<ArmorClass> armorClasses,
                         HitPoints hitPoints, Movement movement, ChallengeRating challengeRating,
                         ProficiencyBonus proficiencyBonus, Map<String, String> savingThrows,
                         Map<String, SkillValue> skills, PassivePerception passivePerception,
                         List<String> senses, NullableList<String> languages, Integer level,
                         List<String> forcedSaves, List<String> legendaryForcedSaves,
                         List<String> spellForcedSaves, String proficiencyBonusNote) {
}
