package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterChallengeRatingDeserializer.class)
public sealed interface MonsterChallengeRatingDte permits MonsterChallengeRatingValueDte, MonsterChallengeRatingDetailsDte {
}
