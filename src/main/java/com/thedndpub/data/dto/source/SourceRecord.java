package com.thedndpub.data.dto.source;

import com.thedndpub.data.dto.book.PublicationDefinition;

import java.util.Objects;

/** Registry result; publication data is optional and does not define source identity. */
public record SourceRecord(
        SourceIdentity identity,
        SourceMetadata metadata,
        PublicationDefinition publication,
        PublicationMetadataStatus publicationMetadataStatus) {
    public SourceRecord {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(publicationMetadataStatus, "publicationMetadataStatus");
        if ((publication != null) != (publicationMetadataStatus == PublicationMetadataStatus.AVAILABLE)) {
            throw new IllegalArgumentException("Publication metadata status must match publication availability");
        }
    }
}
