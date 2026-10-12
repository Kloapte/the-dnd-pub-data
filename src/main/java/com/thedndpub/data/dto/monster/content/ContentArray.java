package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ContentArray(List<ContentValue> values) implements ContentValue {
}
