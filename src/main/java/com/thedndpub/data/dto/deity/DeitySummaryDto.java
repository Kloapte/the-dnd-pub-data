package com.thedndpub.data.dto.deity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DeitySummaryDto(
        DeityId id,
        String name,
        SourceReference source,
        String pantheon,
        String category,
        List<String> alignment,
        List<String> domains) {
    public DeitySummaryDto {
        alignment = immutable(alignment);
        domains = immutable(domains);
    }

    public static DeitySummaryDto from(DeityDefinition definition) {
        return new DeitySummaryDto(
                definition.id(), definition.name(), definition.source(), definition.pantheon(),
                definition.category(), definition.alignment(), definition.domains());
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
