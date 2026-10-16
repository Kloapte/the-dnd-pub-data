package com.thedndpub.data.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PublicationDto(
        PublicationId id,
        String name,
        String group,
        String published,
        String revised,
        String author,
        List<String> aliases,
        String parentSource) {
    public PublicationDto {
        aliases = List.copyOf(aliases);
    }

    public static PublicationDto from(PublicationDefinition publication) {
        return new PublicationDto(
                publication.id(),
                publication.name(),
                publication.group(),
                publication.published() == null ? null : publication.published().toString(),
                publication.revised() == null ? null : publication.revised().toString(),
                publication.author(),
                publication.aliases(),
                publication.parentSource());
    }
}
