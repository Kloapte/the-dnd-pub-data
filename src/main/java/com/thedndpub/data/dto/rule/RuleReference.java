package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleReferenceType;
import com.thedndpub.data.dto.rule.enums.RuleReferenceOperation;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleReference(RuleReferenceType targetType, String id, String relationship,
        RuleReferenceOperation operation) {
    private static final Pattern CANONICAL_ID = Pattern.compile(
            "^[a-z][a-z0-9-]*:[a-z0-9-]+:[a-z0-9-]+$");

    public RuleReference {
        Objects.requireNonNull(targetType, "Reference targetType is required");
        Objects.requireNonNull(id, "Reference id is required");
        if (!CANONICAL_ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid reference id: " + id);
        if (relationship != null && relationship.isEmpty()) {
            throw new IllegalArgumentException("Reference relationship must not be empty");
        }
    }
}
