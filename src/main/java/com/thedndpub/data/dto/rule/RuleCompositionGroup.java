package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.HashSet;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleCompositionGroup(String kind, List<RuleCompositionMember> members) implements RuleCompositionMember {
    public RuleCompositionGroup {
        if (!"AND".equals(kind) && !"OR".equals(kind)) {
            throw new IllegalArgumentException("Composition group kind must be AND or OR");
        }
        Objects.requireNonNull(members, "Composition group members are required");
        if (members.size() < 2 || new HashSet<>(members).size() != members.size()) {
            throw new IllegalArgumentException("Composition group requires at least two unique members");
        }
        members = members == null ? List.of() : List.copyOf(members);
    }
}
