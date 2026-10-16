package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionReference(String name, SourceIdentity source, String tag) {
}
