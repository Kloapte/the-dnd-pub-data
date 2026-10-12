package com.thedndpub.data.dte.monster;

public record MonsterChallengeRatingDetailsDte(String cr, String coven, String lair, Integer xp,
                                               Integer xpLair) implements MonsterChallengeRatingDte {
}
