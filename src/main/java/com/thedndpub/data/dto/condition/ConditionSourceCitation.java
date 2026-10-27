package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionSourceCitation(String source, Integer page) {
}
