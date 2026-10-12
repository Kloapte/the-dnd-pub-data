package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MonsterIdentityDte(String name, String source, Integer page, List<String> alias,
                                 List<MonsterSourceReferenceDte> otherSources,
                                 List<MonsterSourceReferenceDte> referenceSources,
                                 List<String> reprintedAs, List<String> group,
                                 MonsterLegendaryGroupReferenceDte legendaryGroup,
                                 List<String> size, MonsterCreatureTypeDte type, List<MonsterAlignmentDte> alignment,
                                 String alignmentPrefix, String dragonAge, String dragonCastingColor,
                                 MonsterShortNameDte shortName,
                                 Boolean isNamedCreature, Boolean isNpc, Integer level, String summonedByClass,
                                 String summonedBySpell, Integer summonedBySpellLevel) {
}
