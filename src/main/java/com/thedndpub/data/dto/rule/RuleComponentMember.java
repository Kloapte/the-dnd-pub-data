package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleComponentMemberKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleComponentMember(RuleComponentMemberKind kind, String componentId) implements RuleCompositionMember {
    public RuleComponentMember {
        Objects.requireNonNull(kind, "Composition member kind is required");
        Objects.requireNonNull(componentId, "Composition componentId is required");
    }
}
