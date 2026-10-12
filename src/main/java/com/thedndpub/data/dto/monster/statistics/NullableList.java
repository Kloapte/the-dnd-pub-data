package com.thedndpub.data.dto.monster.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record NullableList<T>(boolean explicitNull, List<T> values) {
}
