package com.thedndpub.data.dto.reward;

public record RewardId(String value) {
    public RewardId {
        if (value == null || !value.startsWith("reward:")) {
            throw new IllegalArgumentException("Reward ID must start with reward:");
        }
    }
}
