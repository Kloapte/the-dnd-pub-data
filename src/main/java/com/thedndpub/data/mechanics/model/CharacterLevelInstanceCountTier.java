package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Total active instances from this inclusive character-level threshold until the next tier. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterLevelInstanceCountTier(int minimumCharacterLevel, int totalInstances) {
    public CharacterLevelInstanceCountTier {
        ModelChecks.require(minimumCharacterLevel > 1 && minimumCharacterLevel <= 20,
                "minimumCharacterLevel must be between 2 and 20");
        ModelChecks.require(totalInstances > 0, "totalInstances must be positive");
    }
}
