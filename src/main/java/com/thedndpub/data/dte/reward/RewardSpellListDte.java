package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RewardSpellListDte(@JsonProperty("_") List<String> spellIds) {
    public RewardSpellListDte {
        spellIds = spellIds == null ? null : List.copyOf(spellIds);
    }
}
