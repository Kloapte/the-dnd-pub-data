package com.thedndpub.data.dte.monster;

public final class MonsterResolutionException extends RuntimeException {
    public MonsterResolutionException(String message) {
        super(message);
    }

    public MonsterResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
