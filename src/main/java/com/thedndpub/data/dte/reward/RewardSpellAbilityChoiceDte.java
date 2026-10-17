package com.thedndpub.data.dte.reward;

import java.util.List;

public record RewardSpellAbilityChoiceDte(List<String> choose) implements RewardSpellAbilityDte {
    public RewardSpellAbilityChoiceDte {
        choose = choose == null ? null : List.copyOf(choose);
    }
}
