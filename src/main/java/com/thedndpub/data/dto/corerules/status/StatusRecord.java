package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalSourceMetadata;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatusRecord(
        String id,
        String name,
        CanonicalSourceMetadata sourceMetadata,
        List<StatusMechanic> mechanics) {
    public StatusRecord {
        if (id == null || !id.startsWith("status:") || name == null || name.isBlank() || sourceMetadata == null
                || mechanics == null || mechanics.isEmpty()) {
            throw new IllegalArgumentException("Status record requires identity, provenance, and mechanics");
        }
        mechanics = List.copyOf(mechanics);
    }
}
