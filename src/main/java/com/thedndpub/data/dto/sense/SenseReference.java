package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.enums.SenseReferenceOperation;
import com.thedndpub.data.dto.sense.enums.SenseReferenceType;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseReference(
        SenseReferenceType targetType,
        String id,
        String relationship,
        SenseReferenceOperation operation) {
    private static final Pattern ID = Pattern.compile(
            "^[a-z][a-z0-9]*(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*$");

    public SenseReference {
        Objects.requireNonNull(targetType, "Reference targetType is required");
        Objects.requireNonNull(id, "Reference id is required");
        if (!ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid Sense reference id: " + id);
        if (relationship != null && relationship.isEmpty()) {
            throw new IllegalArgumentException("Reference relationship must not be empty");
        }
    }
}
