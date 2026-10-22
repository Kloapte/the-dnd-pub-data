package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashSet;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Source(
        String code,
        List<String> codeAliases,
        List<PublicationReference> publications) {

    public Source {
        if (code == null || code.isEmpty() || code.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Source code must be a non-empty token");
        }
        if (codeAliases != null && codeAliases.isEmpty()) {
            throw new IllegalArgumentException("Code aliases must be omitted when empty");
        }
        if (publications != null && publications.isEmpty()) {
            throw new IllegalArgumentException("Publications must be omitted when empty");
        }
        codeAliases = codeAliases == null ? List.of() : List.copyOf(codeAliases);
        publications = publications == null ? List.of() : List.copyOf(publications);
        if (codeAliases.stream().anyMatch(alias -> alias.isEmpty()
                || alias.chars().anyMatch(Character::isWhitespace))) {
            throw new IllegalArgumentException("Code aliases must be non-empty tokens");
        }
        if (new HashSet<>(codeAliases).size() != codeAliases.size()) {
            throw new IllegalArgumentException("Code aliases must be unique");
        }
    }
}
