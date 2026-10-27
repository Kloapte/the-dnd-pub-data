package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleType;
import com.thedndpub.data.dto.rule.mechanics.RuleMechanic;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import tools.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dto.rule.mechanics.deserializers.RuleMechanicDeserializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRecord(
        String id,
        String name,
        RuleSourceMetadata sourceMetadata,
        RuleType ruleType,
        @JsonDeserialize(contentUsing = RuleMechanicDeserializer.class) List<RuleMechanic> mechanics,
        List<RuleReference> references,
        RuleComposition composition) {
    private static final Pattern CANONICAL_RULE_ID = Pattern.compile(
            "^rule:[a-z0-9]+(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*$");

    public RuleRecord {
        Objects.requireNonNull(id, "Rule id is required");
        if (!CANONICAL_RULE_ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid Rule id: " + id);
        Objects.requireNonNull(name, "Rule name is required");
        if (name.isEmpty()) throw new IllegalArgumentException("Rule name must not be empty");
        Objects.requireNonNull(sourceMetadata, "Rule sourceMetadata is required");
        if (mechanics != null && mechanics.isEmpty()) throw new IllegalArgumentException("Rule mechanics must not be empty");
        if (references != null && references.isEmpty()) throw new IllegalArgumentException("Rule references must not be empty");
        mechanics = mechanics == null ? List.of() : List.copyOf(mechanics);
        references = references == null ? List.of() : List.copyOf(references);
    }
}
