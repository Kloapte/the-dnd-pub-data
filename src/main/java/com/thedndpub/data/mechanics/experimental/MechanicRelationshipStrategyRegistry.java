package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MechanicRelationshipStrategyRegistry {
    private final Map<MechanicRelationshipType, List<MechanicRelationshipStrategy>> strategies;

    public MechanicRelationshipStrategyRegistry(Iterable<MechanicRelationshipStrategy> strategies) {
        Map<MechanicRelationshipType, List<MechanicRelationshipStrategy>> indexed =
                new EnumMap<>(MechanicRelationshipType.class);
        for (MechanicRelationshipStrategy strategy : strategies) {
            for (MechanicRelationshipType type : strategy.supportedTypes()) {
                List<MechanicRelationshipStrategy> family = indexed.computeIfAbsent(type, ignored -> new ArrayList<>());
                ModelChecks.require(family.stream().noneMatch(existing -> existing.strategyId().equals(strategy.strategyId())
                                && existing.version().equals(strategy.version())),
                        "Duplicate relationship strategy identity for " + type);
                family.add(strategy);
            }
        }
        Map<MechanicRelationshipType, List<MechanicRelationshipStrategy>> immutable =
                new EnumMap<>(MechanicRelationshipType.class);
        indexed.forEach((type, family) -> immutable.put(type, family.stream()
                .sorted(java.util.Comparator.comparing(MechanicRelationshipStrategy::strategyId)
                        .thenComparing(MechanicRelationshipStrategy::version)).toList()));
        this.strategies = Map.copyOf(immutable);
    }

    public static MechanicRelationshipStrategyRegistry standard() {
        return new MechanicRelationshipStrategyRegistry(List.of(
                new SavingThrowDamageOutcomeStrategy(), new AttackDamageOutcomeStrategy(),
                new ScalingRelationshipStrategy(), new ConditionRelationshipStrategy()));
    }

    public List<MechanicRelationshipStrategy> strategies(MechanicRelationshipType type) {
        return strategies.getOrDefault(type, List.of());
    }

    public List<MechanicRelationshipStrategy> allStrategies() {
        return strategies.values().stream().flatMap(List::stream)
                .collect(java.util.stream.Collectors.toMap(
                        strategy -> strategy.strategyId() + "|" + strategy.version(),
                        strategy -> strategy,
                        (first, ignored) -> first,
                        java.util.TreeMap::new))
                .values().stream().toList();
    }
}
