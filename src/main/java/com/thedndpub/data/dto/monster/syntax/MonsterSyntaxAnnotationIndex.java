package com.thedndpub.data.dto.monster.syntax;

import com.thedndpub.data.dto.monster.MonsterDto;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Immutable authored-source syntax index. It intentionally contains no effective annotations. */
public final class MonsterSyntaxAnnotationIndex {
    private final int sourceFileCount;
    private final List<MonsterSyntaxSourceOwner> sourceOwners;
    private final List<MonsterSyntaxAnnotation> annotations;
    private final Map<MonsterId, List<MonsterSyntaxAnnotation>> byAuthoredMonsterId;
    private final int effectiveMonsterCount;

    public MonsterSyntaxAnnotationIndex(
            List<MonsterDto> effectiveMonsters,
            int sourceFileCount,
            List<MonsterSyntaxSourceOwner> sourceOwners,
            List<MonsterSyntaxAnnotation> annotations) {
        if (sourceFileCount < 0) {
            throw new IllegalArgumentException("Source file count must not be negative");
        }
        this.sourceFileCount = sourceFileCount;
        this.sourceOwners = List.copyOf(sourceOwners);
        this.annotations = List.copyOf(annotations);
        this.effectiveMonsterCount = effectiveMonsters.size();

        Set<MonsterId> effectiveIds = new HashSet<>();
        for (MonsterDto monster : effectiveMonsters) {
            Objects.requireNonNull(monster, "effective Monster");
            if (!effectiveIds.add(monster.identity().id())) {
                throw new IllegalArgumentException("Duplicate effective Monster ID: "
                        + monster.identity().id().value());
            }
        }

        Map<MonsterId, MonsterSyntaxSourceOwner> ownersById = new LinkedHashMap<>();
        Map<MonsterId, List<MonsterSyntaxAnnotation>> authoredIndex = new LinkedHashMap<>();
        for (MonsterSyntaxSourceOwner owner : this.sourceOwners) {
            if (!effectiveIds.contains(owner.monsterId())) {
                throw new IllegalArgumentException("Authored Monster root is absent from effective catalog: "
                        + owner.monsterId().value());
            }
            if (ownersById.putIfAbsent(owner.monsterId(), owner) != null) {
                throw new IllegalArgumentException("Duplicate authored Monster source owner: "
                        + owner.monsterId().value());
            }
            authoredIndex.put(owner.monsterId(), new ArrayList<>());
        }

        Map<MonsterId, Integer> expectedOccurrenceIndexes = new LinkedHashMap<>();
        for (MonsterSyntaxAnnotation annotation : this.annotations) {
            if (!ownersById.containsKey(annotation.authoredMonsterId())) {
                throw new IllegalArgumentException("Syntax observation has no authored Monster owner: "
                        + annotation.authoredMonsterId().value());
            }
            int expectedIndex = expectedOccurrenceIndexes.getOrDefault(annotation.authoredMonsterId(), 0);
            if (annotation.occurrenceIndex() != expectedIndex) {
                throw new IllegalArgumentException("Non-sequential Monster syntax occurrence index for "
                        + annotation.authoredMonsterId().value());
            }
            authoredIndex.get(annotation.authoredMonsterId()).add(annotation);
            expectedOccurrenceIndexes.put(annotation.authoredMonsterId(), expectedIndex + 1);
        }

        Map<MonsterId, List<MonsterSyntaxAnnotation>> immutableIndex = new LinkedHashMap<>();
        authoredIndex.forEach((id, values) -> immutableIndex.put(id, List.copyOf(values)));
        byAuthoredMonsterId = Collections.unmodifiableMap(immutableIndex);
    }

    public int sourceFileCount() {
        return sourceFileCount;
    }

    public int authoredMonsterRootCount() {
        return sourceOwners.size();
    }

    public int effectiveMonsterCount() {
        return effectiveMonsterCount;
    }

    public List<MonsterSyntaxSourceOwner> sourceOwners() {
        return sourceOwners;
    }

    public List<MonsterSyntaxAnnotation> annotations() {
        return annotations;
    }

    public Map<MonsterId, List<MonsterSyntaxAnnotation>> byAuthoredMonsterId() {
        return byAuthoredMonsterId;
    }

    public List<MonsterSyntaxAnnotation> findByAuthoredMonsterId(MonsterId monsterId) {
        return byAuthoredMonsterId.getOrDefault(Objects.requireNonNull(monsterId, "monsterId"), List.of());
    }

    public long authoredOwnerCountWithObservations() {
        return byAuthoredMonsterId.values().stream().filter(values -> !values.isEmpty()).count();
    }

    public long observationCount(SyntaxOrigin origin) {
        Objects.requireNonNull(origin, "origin");
        return annotations.stream()
                .filter(annotation -> annotation.observation().tag().evidence().location().origin() == origin)
                .count();
    }

    public boolean effectiveObservationsMaterialized() {
        return false;
    }
}
