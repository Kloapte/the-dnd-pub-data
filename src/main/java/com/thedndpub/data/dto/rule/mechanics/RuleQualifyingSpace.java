package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleCreaturePresenceQualifyingSpace.class, name = "CREATURE_PRESENCE"),
        @JsonSubTypes.Type(value = RuleFurnitureSizeQualifyingSpace.class, name = "FURNITURE_SIZE"),
        @JsonSubTypes.Type(value = RuleSurfaceMaterialQualifyingSpace.class, name = "SURFACE_MATERIAL"),
        @JsonSubTypes.Type(value = RuleLiquidDepthQualifyingSpace.class, name = "LIQUID_DEPTH"),
        @JsonSubTypes.Type(value = RuleNarrowOpeningQualifyingSpace.class, name = "NARROW_OPENING"),
        @JsonSubTypes.Type(value = RuleSlopeQualifyingSpace.class, name = "SLOPE"),
        @JsonSubTypes.Type(value = RuleDmAdjudicatedSimilarQualifyingSpace.class, name = "DM_ADJUDICATED_SIMILAR")
})
public sealed interface RuleQualifyingSpace permits RuleCreaturePresenceQualifyingSpace,
        RuleFurnitureSizeQualifyingSpace, RuleSurfaceMaterialQualifyingSpace, RuleLiquidDepthQualifyingSpace,
        RuleNarrowOpeningQualifyingSpace, RuleSlopeQualifyingSpace, RuleDmAdjudicatedSimilarQualifyingSpace {
    RuleQualifyingSpaceKind kind();
}
