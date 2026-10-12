package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ContentObject(Map<String, ContentValue> properties) implements ContentValue {
}
