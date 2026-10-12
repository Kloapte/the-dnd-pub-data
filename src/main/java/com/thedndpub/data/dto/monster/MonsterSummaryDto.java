package com.thedndpub.data.dto.monster;

import com.thedndpub.data.dto.monster.MonsterDto.*;

import java.util.List;
import java.util.Set;

/**
 * Compact catalog projection; the detail DTO remains its source of truth.
 */
public record MonsterSummaryDto(MonsterId id, String name, String source, Set<String> editionIds,
                                ChallengeRating challengeRating, List<String> sizes,
                                CreatureType creatureType, List<Alignment> alignments,
                                ArmorClass representativeArmorClass, HitPoints hitPoints,
                                Set<String> environments) {
    public static MonsterSummaryDto from(MonsterDto monster) {
        return new MonsterSummaryDto(monster.identity().id(), monster.identity().name(),
                monster.identity().source().code(), monster.identity().source().availability().editionIds(),
                monster.statistics().challengeRating(), monster.identity().sizes(), monster.identity().creatureType(),
                monster.identity().alignments(), monster.statistics().armorClasses().stream().findFirst().orElse(null),
                monster.statistics().hitPoints(), monster.catalog().environments());
    }
}
