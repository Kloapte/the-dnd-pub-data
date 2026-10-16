package com.thedndpub.data.dto.monster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.identity.Alignment;
import com.thedndpub.data.dto.monster.identity.CreatureType;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.statistics.ArmorClass;
import com.thedndpub.data.dto.monster.statistics.ChallengeRating;
import com.thedndpub.data.dto.monster.statistics.HitPoints;

import java.util.List;
import java.util.Set;

/**
 * Compact catalog projection; the detail DTO remains its source of truth.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MonsterSummaryDto(MonsterId id, String name, String source, Set<String> editionIds,
                                ChallengeRating challengeRating, List<String> sizes,
                                CreatureType creatureType, List<Alignment> alignments,
                                ArmorClass representativeArmorClass, HitPoints hitPoints,
                                Set<String> environments) {
    public static MonsterSummaryDto from(MonsterDto monster) {
        Set<String> editionIds = monster.identity().source().availability() == null
                ? Set.of()
                : monster.identity().source().availability().editionIds();
        Set<String> environments = monster.catalog() == null
                ? Set.of()
                : monster.catalog().environments();
        return new MonsterSummaryDto(monster.identity().id(), monster.identity().name(),
                monster.identity().source().code().code(), editionIds,
                monster.statistics().challengeRating(), monster.identity().sizes(), monster.identity().creatureType(),
                monster.identity().alignments(), monster.statistics().armorClasses().stream().findFirst().orElse(null),
                monster.statistics().hitPoints(), environments);
    }
}
