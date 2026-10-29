package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillSourceCitation(String source, Integer page) {
    public SkillSourceCitation {
        Objects.requireNonNull(source, "Citation source is required");
        if (source.isEmpty()) throw new IllegalArgumentException("Citation source must not be empty");
        Objects.requireNonNull(page, "Citation page is required");
        if (page < 1) throw new IllegalArgumentException("Citation page must be positive");
    }
}
