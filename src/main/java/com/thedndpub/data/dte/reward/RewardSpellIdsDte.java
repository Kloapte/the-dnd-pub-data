package com.thedndpub.data.dte.reward;

import java.util.List;

public record RewardSpellIdsDte(List<String> spellIds) implements RewardSpellCollectionDte {
    public RewardSpellIdsDte {
        spellIds = spellIds == null ? null : List.copyOf(spellIds);
    }
}
