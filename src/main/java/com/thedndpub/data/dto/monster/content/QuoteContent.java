package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record QuoteContent(String attribution, List<MonsterContentDto> entries,
                           Map<String, ContentValue> sourceProperties) implements MonsterContentDto {
}
