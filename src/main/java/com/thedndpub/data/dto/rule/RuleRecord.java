package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleType;
import com.thedndpub.data.dto.rule.mechanics.RuleMechanic;

import java.util.List;
import java.util.Objects;
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
    public RuleRecord {
        Objects.requireNonNull(id, "Rule id is required");
        Objects.requireNonNull(name, "Rule name is required");
        Objects.requireNonNull(sourceMetadata, "Rule sourceMetadata is required");
        mechanics = mechanics == null ? List.of() : List.copyOf(mechanics);
        references = references == null ? List.of() : List.copyOf(references);
    }
}
