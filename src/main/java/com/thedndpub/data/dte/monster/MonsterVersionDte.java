package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Alternate source stat block; its source modification instructions remain typed.
 */
public record MonsterVersionDte(String name, String source,
                                @JsonProperty("_mod") MonsterCopyModDte mod,
                                @JsonProperty("_abstract") MonsterVersionAbstractDte abstractTemplate,
                                @JsonProperty("_implementations") List<MonsterTemplateImplementationDte> implementations,
                                List<MonsterArmorClassDte> ac, MonsterHitPointsDte hp, MonsterSpeedDte speed,
                                List<MonsterAlignmentDte> alignment, MonsterChallengeRatingDte cr,
                                MonsterCreatureTypeDte type,
                                List<MonsterFeatureDte> trait, List<MonsterFeatureDte> action,
                                List<MonsterFeatureDte> bonus,
                                List<MonsterFeatureDte> reaction, List<MonsterSpellcastingDte> spellcasting,
                                List<MonsterDefenseDte> resist, List<MonsterDefenseDte> immune,
                                List<MonsterDefenseDte> vulnerable,
                                List<MonsterDefenseDte> conditionImmune, List<String> conditionInflict,
                                List<String> damageTags,
                                List<String> miscTags, List<String> languages, List<String> senses,
                                java.util.Map<String, String> save,
                                java.util.Map<String, MonsterSkillValueDte> skill, List<String> savingThrowForced,
                                MonsterPassiveDte passive, Integer level, @JsonProperty("str") Integer strength,
                                @JsonProperty("dex") Integer dexterity,
                                @JsonProperty("con") Integer constitution, @JsonProperty("int") Integer intelligence,
                                @JsonProperty("wis") Integer wisdom, @JsonProperty("cha") Integer charisma,
                                String size, List<String> traitTags, Boolean hasToken, Double foundryTokenScale,
                                String pbNote,
                                List<String> variant) {
}
