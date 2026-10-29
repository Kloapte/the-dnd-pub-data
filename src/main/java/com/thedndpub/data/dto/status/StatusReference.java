package com.thedndpub.data.dto.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.enums.StatusReferenceOperation;
import com.thedndpub.data.dto.status.enums.StatusReferenceType;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StatusReference(
        StatusReferenceType targetType,
        String id,
        String relationship,
        StatusReferenceOperation operation) {
    private static final Pattern ID = Pattern.compile(
            "^[a-z][a-z0-9]*(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*$");

    public StatusReference {
        Objects.requireNonNull(targetType, "Reference targetType is required");
        Objects.requireNonNull(id, "Reference id is required");
        if (!ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid Status reference id: " + id);
        if (relationship != null && relationship.isEmpty()) {
            throw new IllegalArgumentException("Reference relationship must not be empty");
        }
    }
}
