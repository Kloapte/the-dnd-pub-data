package com.thedndpub.data.dto.monster;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PagedResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public PagedResponse {
        content = List.copyOf(content);
    }
}
