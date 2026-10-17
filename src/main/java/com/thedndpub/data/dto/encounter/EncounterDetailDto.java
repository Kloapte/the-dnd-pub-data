package com.thedndpub.data.dto.encounter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EncounterDetailDto(EncounterId id, String name, SourceReference source,
        List<EncounterTableSummaryDto> tables, int rowCount,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public EncounterDetailDto {
        tables = List.copyOf(tables);
        crossCatalogReferences = List.copyOf(crossCatalogReferences);
    }

    public static EncounterDetailDto from(EncounterDefinition definition) {
        return new EncounterDetailDto(definition.id(), definition.name(), definition.source(),
                definition.tables().stream().map(EncounterTableSummaryDto::from).toList(), definition.rowCount(),
                List.of());
    }

    public EncounterDetailDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new EncounterDetailDto(id, name, source, tables, rowCount, references);
    }
}
