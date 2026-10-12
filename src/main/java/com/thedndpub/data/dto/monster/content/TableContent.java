package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TableContent(String caption, List<String> columnLabels, List<String> columnStyles,
                           List<List<TableCellDto>> rows, String source, Integer page,
                           Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
}
