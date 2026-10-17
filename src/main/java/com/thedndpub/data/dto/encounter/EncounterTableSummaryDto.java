package com.thedndpub.data.dto.encounter;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EncounterTableSummaryDto(Integer minLevel, Integer maxLevel, String diceExpression,
        int rowCount, List<EncounterRollRangeDto> rollRanges, Integer page, Boolean rollAttitude) {
    public EncounterTableSummaryDto {
        rollRanges = List.copyOf(rollRanges);
    }

    static EncounterTableSummaryDto from(EncounterTable table) {
        return new EncounterTableSummaryDto(table.minLevel(), table.maxLevel(), table.diceExpression(),
                table.rows().size(), table.rows().stream()
                        .map(row -> new EncounterRollRangeDto(row.min(), row.max())).toList(), table.page(),
                table.rollAttitude());
    }
}
