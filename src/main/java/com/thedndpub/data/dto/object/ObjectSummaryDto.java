package com.thedndpub.data.dto.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ObjectSummaryDto(
        String id, String name, SourceReference source, String category, List<String> size,
        ObjectStatisticDto armorClass, ObjectStatisticDto hitPoints) {
    public ObjectSummaryDto {
        size = size == null ? List.of() : List.copyOf(size);
    }

    public static ObjectSummaryDto from(ObjectDefinition definition) {
        return new ObjectSummaryDto(definition.id(), definition.name(), definition.source(), definition.category(),
                definition.size(), ObjectStatisticDto.from(definition.armorClass()),
                ObjectStatisticDto.from(definition.hitPoints()));
    }
}
