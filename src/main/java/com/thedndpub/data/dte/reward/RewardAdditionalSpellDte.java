package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RewardAdditionalSpellDeserializer.class)
public record RewardAdditionalSpellDte(
        RewardSpellAbilityDte ability,
        RewardSpellCollectionDte innate,
        RewardSpellListDte known,
        String resourceName) {
}
