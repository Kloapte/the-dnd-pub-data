package com.thedndpub.data.dto.corerules.sense;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.CanonicalSourceMetadata;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseRecord(
        String id,
        String name,
        CanonicalSourceMetadata sourceMetadata,
        List<SenseCapability> capabilities,
        List<CanonicalReference> references) {
    public SenseRecord {
        if (id == null || !id.startsWith("sense:") || name == null || name.isBlank()
                || sourceMetadata == null || capabilities == null || capabilities.isEmpty()) {
            throw new IllegalArgumentException("Sense record requires identity, provenance, and capabilities");
        }
        capabilities = List.copyOf(capabilities);
        references = references == null ? null : List.copyOf(references);
    }
}
