package com.thedndpub.data.dto.corerules.sense;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LocationPrecision(String kind, List<String> targets) {
    public LocationPrecision {
        if (kind == null) throw new IllegalArgumentException("Location precision kind is required");
        targets = targets == null ? null : List.copyOf(targets);
    }
}
