package com.thedndpub.data.dte.reward;

import java.util.List;

public record RewardsFileDte(List<RewardDte> reward) {
    public RewardsFileDte {
        reward = reward == null ? null : List.copyOf(reward);
    }
}
