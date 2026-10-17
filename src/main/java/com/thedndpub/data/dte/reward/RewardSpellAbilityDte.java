package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RewardSpellAbilityDeserializer.class)
public sealed interface RewardSpellAbilityDte permits RewardSpellAbilityCodeDte, RewardSpellAbilityChoiceDte {
}
