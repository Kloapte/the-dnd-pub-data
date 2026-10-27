package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleCompositionKind;

import java.util.List;
import java.util.HashSet;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleComposition(RuleCompositionKind kind, List<RuleCompositionMember> members) {
    public RuleComposition {
        Objects.requireNonNull(kind, "Composition kind is required");
        Objects.requireNonNull(members, "Composition members are required");
        if (members.size() < 2 || new HashSet<>(members).size() != members.size()) {
            throw new IllegalArgumentException("Composition requires at least two unique members");
        }
        members = members == null ? List.of() : List.copyOf(members);
    }
}
