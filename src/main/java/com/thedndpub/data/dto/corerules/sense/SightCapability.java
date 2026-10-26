package com.thedndpub.data.dto.corerules.sense;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SightCapability(
        String kind,
        List<String> perceives,
        List<String> conditions,
        String darknessAs,
        String colorPerception,
        String dimLightAs,
        IllusionInteraction illusionInteraction) implements SenseCapability {
    public SightCapability {
        if (!"SIGHT".equals(kind)) throw new IllegalArgumentException("Invalid sight capability kind");
        if (perceives == null || perceives.isEmpty()) {
            throw new IllegalArgumentException("Sight capability requires perception modes");
        }
        perceives = List.copyOf(perceives);
        conditions = conditions == null ? null : List.copyOf(conditions);
    }
}
