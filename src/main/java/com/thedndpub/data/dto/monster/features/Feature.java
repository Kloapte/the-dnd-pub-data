package com.thedndpub.data.dto.monster.features;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.content.MonsterContentDto;
import com.thedndpub.data.dto.monster.identity.Reference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Feature(String name, List<MonsterContentDto> entries, String sourceType,
                      String source, Integer page, Reference token, VariantReference versionReference) {
}
