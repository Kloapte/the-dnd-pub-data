package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.capabilities.SenseCapability;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseRecord(
        String id,
        String name,
        SenseSourceMetadata sourceMetadata,
        List<SenseCapability> capabilities,
        List<SenseReference> references) {
    private static final Pattern ID = Pattern.compile(
            "^sense:[a-z0-9]+(?:-[a-z0-9]+)*:[a-z0-9]+(?:-[a-z0-9]+)*$");

    public SenseRecord {
        Objects.requireNonNull(id, "Sense id is required");
        if (!ID.matcher(id).matches()) throw new IllegalArgumentException("Invalid Sense id: " + id);
        Objects.requireNonNull(name, "Sense name is required");
        if (name.isEmpty()) throw new IllegalArgumentException("Sense name must not be empty");
        Objects.requireNonNull(sourceMetadata, "Sense sourceMetadata is required");
        Objects.requireNonNull(capabilities, "Sense capabilities are required");
        if (capabilities.isEmpty()) throw new IllegalArgumentException("Sense capabilities must not be empty");
        if (references != null && references.isEmpty()) {
            throw new IllegalArgumentException("Sense references must not be empty when present");
        }
        capabilities = List.copyOf(capabilities);
        references = references == null ? List.of() : List.copyOf(references);
    }
}
