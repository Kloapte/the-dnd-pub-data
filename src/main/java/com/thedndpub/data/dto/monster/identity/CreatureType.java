package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CreatureType(String value, List<String> choices, List<TypeTag> tags, String swarmSize,
                           Boolean sidekickHidden, List<String> sidekickTags, String sidekickType) {
}
