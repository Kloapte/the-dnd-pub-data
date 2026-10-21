package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MechanicStrategyRegistry {
    private final Map<MechanicType, List<MechanicExtractionStrategy>> strategies;

    public MechanicStrategyRegistry(Iterable<MechanicExtractionStrategy> strategies) {
        Map<MechanicType, List<MechanicExtractionStrategy>> indexed = new EnumMap<>(MechanicType.class);
        for (MechanicExtractionStrategy strategy : strategies) {
            for (MechanicType type : strategy.supportedTypes()) {
                List<MechanicExtractionStrategy> family = indexed.computeIfAbsent(type, ignored -> new ArrayList<>());
                ModelChecks.require(family.stream().noneMatch(existing -> existing.strategyId().equals(strategy.strategyId())
                                && existing.version().equals(strategy.version())),
                        "Duplicate strategy identity for " + type);
                family.add(strategy);
            }
        }
        Map<MechanicType, List<MechanicExtractionStrategy>> immutable = new EnumMap<>(MechanicType.class);
        indexed.forEach((type, family) -> immutable.put(type, family.stream()
                .sorted(java.util.Comparator.comparing(MechanicExtractionStrategy::strategyId)
                        .thenComparing(MechanicExtractionStrategy::version)).toList()));
        this.strategies = Map.copyOf(immutable);
    }

    public static MechanicStrategyRegistry standard() {
        java.util.List<MechanicExtractionStrategy> standard = new java.util.ArrayList<>(java.util.Arrays.stream(
                MechanicType.values()).filter(type -> type != MechanicType.SCALING
                        && type != MechanicType.CONDITION && type != MechanicType.DURATION
                        && type != MechanicType.CONDITION_TERMINATION)
                .<MechanicExtractionStrategy>map(GenericMechanicStrategy::new).toList());
        standard.add(new GenericScalingExtractionStrategy());
        standard.add(new GenericConditionExtractionStrategy());
        standard.add(new GenericConditionDurationExtractionStrategy());
        standard.add(new GenericConditionTerminationExtractionStrategy());
        return new MechanicStrategyRegistry(standard);
    }

    public List<MechanicExtractionStrategy> strategies(MechanicType type) {
        return strategies.getOrDefault(type, List.of());
    }

    public MechanicExtractionStrategy strategy(MechanicType type) {
        return strategies(type).stream().findFirst().orElse(null);
    }
}
