package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleComponentApplicabilityKind;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleComponentApplicability(RuleComponentApplicabilityKind kind, String componentId) {
    private static final Pattern COMPONENT_ID = Pattern.compile("^[a-z][a-z0-9]*(?:-[a-z0-9]+)*$");

    public RuleComponentApplicability {
        Objects.requireNonNull(kind, "Component applicability kind is required");
        Objects.requireNonNull(componentId, "Component applicability componentId is required");
        if (!COMPONENT_ID.matcher(componentId).matches()) {
            throw new IllegalArgumentException("Invalid applicability componentId: " + componentId);
        }
    }
}
