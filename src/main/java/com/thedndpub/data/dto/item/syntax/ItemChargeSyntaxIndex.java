package com.thedndpub.data.dto.item.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Immutable authored-source syntax index. Effective copy annotations are not materialized. */
public final class ItemChargeSyntaxIndex {
    private final int sourceFileCount;
    private final int effectiveDefinitionCount;
    private final List<ItemChargeSyntaxOwner> authoredOwners;
    private final List<ItemChargeSyntaxObservation> observations;
    private final Map<String, List<ItemChargeSyntaxObservation>> byAuthoredItemId;

    public ItemChargeSyntaxIndex(
            int sourceFileCount,
            int effectiveDefinitionCount,
            List<ItemChargeSyntaxOwner> authoredOwners,
            List<ItemChargeSyntaxObservation> observations) {
        if (sourceFileCount < 1 || effectiveDefinitionCount < 0) {
            throw new IllegalArgumentException("Source file and definition counts are invalid");
        }
        this.sourceFileCount = sourceFileCount;
        this.effectiveDefinitionCount = effectiveDefinitionCount;
        this.authoredOwners = List.copyOf(authoredOwners);
        this.observations = List.copyOf(observations);

        Map<String, List<ItemChargeSyntaxObservation>> mutableIndex = new LinkedHashMap<>();
        for (ItemChargeSyntaxOwner owner : this.authoredOwners) {
            Objects.requireNonNull(owner, "authored owner");
            if (mutableIndex.putIfAbsent(owner.itemId(), new ArrayList<>()) != null) {
                throw new IllegalArgumentException("Duplicate authored Item owner: " + owner.itemId());
            }
        }

        Map<String, Integer> nextOccurrenceByOwner = new LinkedHashMap<>();
        for (ItemChargeSyntaxObservation observation : this.observations) {
            Objects.requireNonNull(observation, "observation");
            List<ItemChargeSyntaxObservation> ownerObservations = mutableIndex.get(observation.authoredItemId());
            if (ownerObservations == null) {
                throw new IllegalArgumentException("Observation has no authored Item owner: "
                        + observation.authoredItemId());
            }
            int expectedIndex = nextOccurrenceByOwner.getOrDefault(observation.authoredItemId(), 0);
            if (observation.occurrenceIndex() != expectedIndex) {
                throw new IllegalArgumentException("Non-sequential Item syntax occurrence index for "
                        + observation.authoredItemId());
            }
            ownerObservations.add(observation);
            nextOccurrenceByOwner.put(observation.authoredItemId(), expectedIndex + 1);
        }

        Map<String, List<ItemChargeSyntaxObservation>> immutableIndex = new LinkedHashMap<>();
        mutableIndex.forEach((itemId, values) -> immutableIndex.put(itemId, List.copyOf(values)));
        byAuthoredItemId = Collections.unmodifiableMap(immutableIndex);
    }

    public int sourceFileCount() {
        return sourceFileCount;
    }

    public int effectiveDefinitionCount() {
        return effectiveDefinitionCount;
    }

    public int authoredItemCount() {
        return authoredOwners.size();
    }

    public List<ItemChargeSyntaxOwner> authoredOwners() {
        return authoredOwners;
    }

    public List<ItemChargeSyntaxObservation> observations() {
        return observations;
    }

    public Map<String, List<ItemChargeSyntaxObservation>> byAuthoredItemId() {
        return byAuthoredItemId;
    }

    public List<ItemChargeSyntaxObservation> findByAuthoredItemId(String itemId) {
        return byAuthoredItemId.getOrDefault(Objects.requireNonNull(itemId, "itemId"), List.of());
    }

    public long authoredItemsWithObservations() {
        return byAuthoredItemId.values().stream().filter(values -> !values.isEmpty()).count();
    }

    public boolean effectiveObservationsMaterialized() {
        return false;
    }
}
