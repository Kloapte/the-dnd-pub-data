package com.thedndpub.data.dto.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.mechanics.StatusMechanic;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StatusRecord(
        String id,
        String name,
        StatusSourceMetadata sourceMetadata,
        List<StatusMechanic> mechanics) {
    private static final Pattern ID = Pattern.compile(
            "^status:[a-z0-9]+(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*$");

    public StatusRecord {
        Objects.requireNonNull(id, "Status id is required");
        if (!ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid Status id: " + id);
        Objects.requireNonNull(name, "Status name is required");
        if (name.isEmpty()) throw new IllegalArgumentException("Status name must not be empty");
        Objects.requireNonNull(sourceMetadata, "Status sourceMetadata is required");
        Objects.requireNonNull(mechanics, "Status mechanics are required");
        if (mechanics.isEmpty()) throw new IllegalArgumentException("Status mechanics must not be empty");
        mechanics = List.copyOf(mechanics);
    }
}
