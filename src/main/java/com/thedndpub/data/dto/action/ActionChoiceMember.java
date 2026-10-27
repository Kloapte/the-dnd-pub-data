package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionChoiceMember(String operationId) {
}
