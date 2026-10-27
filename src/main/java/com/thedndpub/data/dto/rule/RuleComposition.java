package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleCompositionKind;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleComposition(RuleCompositionKind kind, List<RuleCompositionMember> members) {
    public RuleComposition {
        Objects.requireNonNull(kind, "Composition kind is required");
        Objects.requireNonNull(members, "Composition members are required");
        members = List.copyOf(members);
    }
}
