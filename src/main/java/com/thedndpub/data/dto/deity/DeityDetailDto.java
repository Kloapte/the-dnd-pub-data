package com.thedndpub.data.dto.deity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DeityDetailDto(
        DeityId id,
        String name,
        SourceReference source,
        String pantheon,
        String category,
        List<String> alignment,
        List<String> domains,
        List<String> aliases,
        List<SourceReference> additionalSources,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public DeityDetailDto {
        alignment = immutable(alignment);
        domains = immutable(domains);
        aliases = immutable(aliases);
        additionalSources = immutable(additionalSources);
        crossCatalogReferences = immutable(crossCatalogReferences);
    }

    public static DeityDetailDto from(DeityDefinition definition) {
        return new DeityDetailDto(
                definition.id(), definition.name(), definition.source(), definition.pantheon(),
                definition.category(), definition.alignment(), definition.domains(), definition.alternateNames(),
                definition.additionalSources(), List.of());
    }

    public DeityDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new DeityDetailDto(id, name, source, pantheon, category, alignment, domains, aliases,
                additionalSources, references);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
