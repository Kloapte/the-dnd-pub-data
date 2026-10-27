package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleComponentMember(String kind, String componentId) implements RuleCompositionMember {
    private static final Pattern COMPONENT_ID = Pattern.compile("^[a-z][a-z0-9]*(?:-[a-z0-9]+)*$");

    public RuleComponentMember {
        if (!"COMPONENT".equals(kind)) throw new IllegalArgumentException("Composition member kind must be COMPONENT");
        Objects.requireNonNull(componentId, "Composition componentId is required");
        if (!COMPONENT_ID.matcher(componentId).matches()) {
            throw new IllegalArgumentException("Invalid composition componentId: " + componentId);
        }
    }
}
