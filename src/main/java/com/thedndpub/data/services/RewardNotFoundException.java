package com.thedndpub.data.services;

public final class RewardNotFoundException extends RuntimeException {
    public RewardNotFoundException(String id) {
        super("Reward not found: " + id);
    }
}
