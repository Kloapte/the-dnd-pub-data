package com.thedndpub.data.dto.skill;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SkillSourceMetadata(
        String source,
        Integer page,
        SkillSourceFlags flags,
        List<SkillSourceCitation> citations,
        List<SkillReference> reprintedAs) {
    public SkillSourceMetadata {
        Objects.requireNonNull(source, "Skill source metadata source is required");
        if (source.isEmpty()) throw new IllegalArgumentException("Skill source metadata source must not be empty");
        if (page != null && page < 1) throw new IllegalArgumentException("Skill source page must be positive");
        if (citations != null && citations.isEmpty()) {
            throw new IllegalArgumentException("Skill citations must not be empty when present");
        }
        if (reprintedAs != null && reprintedAs.isEmpty()) {
            throw new IllegalArgumentException("Skill reprintedAs must not be empty when present");
        }
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
