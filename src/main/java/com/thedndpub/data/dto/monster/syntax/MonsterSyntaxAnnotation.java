package com.thedndpub.data.dto.monster.syntax;

import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dte.monster.MonsterIdentity;
import com.thedndpub.data.mechanics.syntax.SyntaxObservation;

import java.util.Objects;

/** Internal markup observation located in an authored Monster source root. */
public record MonsterSyntaxAnnotation(
        MonsterId authoredMonsterId,
        MonsterIdentity authoredIdentity,
        int occurrenceIndex,
        String sourceContainer,
        MonsterSyntaxContextKind contextKind,
        String featureName,
        String featurePointer,
        SyntaxObservation observation) {
    public MonsterSyntaxAnnotation {
        Objects.requireNonNull(authoredMonsterId, "authoredMonsterId");
        Objects.requireNonNull(authoredIdentity, "authoredIdentity");
        Objects.requireNonNull(contextKind, "contextKind");
        Objects.requireNonNull(observation, "observation");
        if (occurrenceIndex < 0) {
            throw new IllegalArgumentException("Monster syntax occurrence index must not be negative");
        }
        if (sourceContainer == null || sourceContainer.isBlank()) {
            throw new IllegalArgumentException("Monster source container is required");
        }
        if ((featureName == null) != (featurePointer == null)) {
            throw new IllegalArgumentException("Feature name and its source pointer must be present together");
        }
    }
}
