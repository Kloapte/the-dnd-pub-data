package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemSummaryDto(
        String id,
        String name,
        SourceReference source,
        ItemDefinitionKind definitionKind,
        List<String> typeCodes,
        String rarity,
        Double weight,
        Integer valueCopper) {
    public ItemSummaryDto {
        typeCodes = List.copyOf(typeCodes);
    }

    public static ItemSummaryDto from(ItemDefinition item) {
        return new ItemSummaryDto(item.id(), item.name(), item.source(), item.definitionKind(),
                item.typeCodes(), item.rarity(), item.weight(), item.valueCopper());
    }
}
