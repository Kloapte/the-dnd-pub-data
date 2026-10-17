package com.thedndpub.data.dto.monster.syntax;

import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dte.monster.MonsterIdentity;
import com.thedndpub.data.dte.monster.MonsterSourceLocation;

import java.util.Objects;

/** A raw Monster root joined to its existing catalog identity and exact source-array location. */
public record MonsterSyntaxSourceOwner(
        MonsterId monsterId,
        MonsterIdentity authoredIdentity,
        MonsterSourceLocation sourceLocation,
        String sourceResourcePath) {
    public MonsterSyntaxSourceOwner {
        Objects.requireNonNull(monsterId, "monsterId");
        Objects.requireNonNull(authoredIdentity, "authoredIdentity");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
        if (sourceResourcePath == null || sourceResourcePath.isBlank()) {
            throw new IllegalArgumentException("Monster source resource path is required");
        }
    }
}
