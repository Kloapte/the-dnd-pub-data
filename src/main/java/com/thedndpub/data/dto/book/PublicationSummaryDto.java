package com.thedndpub.data.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PublicationSummaryDto(PublicationId id, String name, String group, String published) {
    public static PublicationSummaryDto from(PublicationDefinition publication) {
        return new PublicationSummaryDto(
                publication.id(),
                publication.name(),
                publication.group(),
                publication.published() == null ? null : publication.published().toString());
    }
}
