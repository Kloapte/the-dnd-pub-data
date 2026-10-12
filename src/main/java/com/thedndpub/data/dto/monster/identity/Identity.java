package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Identity(MonsterId id, String name, List<String> aliases, Source source,
                       List<String> groups, List<String> sizes, CreatureType creatureType,
                       List<Alignment> alignments, String alignmentPrefix, ShortName shortName,
                       Boolean namedCreature, Boolean npc, String dragonAge, String dragonCastingColor,
                       Summoning summoning, Reference legendaryGroup) {
}
