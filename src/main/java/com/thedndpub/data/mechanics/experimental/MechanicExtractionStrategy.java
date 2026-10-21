package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellDefinition;

import java.util.Set;

public interface MechanicExtractionStrategy {
    String strategyId();
    String version();
    Set<MechanicType> supportedTypes();
    StrategyExtraction extract(MechanicCandidate candidate, SourceFragment fragment, SpellDefinition spell);
}
