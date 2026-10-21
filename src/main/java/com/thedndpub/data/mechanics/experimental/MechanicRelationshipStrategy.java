package com.thedndpub.data.mechanics.experimental;

import java.util.List;
import java.util.Set;

public interface MechanicRelationshipStrategy {
    String strategyId();

    String version();

    Set<MechanicRelationshipType> supportedTypes();

    RelationshipResolutionResult resolve(
            List<ExtractedMechanic> mechanics,
            MechanicInterpretationContext context);
}
