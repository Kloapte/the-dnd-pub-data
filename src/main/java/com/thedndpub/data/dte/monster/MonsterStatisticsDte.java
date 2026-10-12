package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record MonsterStatisticsDte(
        @JsonProperty("str") Integer strength, @JsonProperty("dex") Integer dexterity,
        @JsonProperty("con") Integer constitution, @JsonProperty("int") Integer intelligence,
        @JsonProperty("wis") Integer wisdom, @JsonProperty("cha") Integer charisma,
        MonsterInitiativeDte initiative, MonsterHitPointsDte hp, MonsterChallengeRatingDte cr,
        MonsterSpeedDte speed, java.util.List<MonsterArmorClassDte> ac, Map<String, String> save,
        Map<String, MonsterSkillValueDte> skill,
        MonsterPassiveDte passive, List<String> senses, List<String> languages,
        List<String> savingThrowForced,
        List<String> savingThrowForcedLegendary, List<String> savingThrowForcedSpell) {
}
