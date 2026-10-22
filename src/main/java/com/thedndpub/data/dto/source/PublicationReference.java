package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PublicationReference(
        PublicationKind kind,
        String publicationId,
        String name,
        LocalDate published,
        LocalDate revised,
        PublicationGroup group,
        List<String> titleAliases,
        String parentSource) {

    public PublicationReference {
        Objects.requireNonNull(kind, "Publication kind is required");
        if (publicationId != null && !isNonWhitespaceToken(publicationId)) {
            throw new IllegalArgumentException("Publication ID must be a non-empty token");
        }
        if (name != null && name.isEmpty()) {
            throw new IllegalArgumentException("Publication name must not be empty");
        }
        if (parentSource != null && !isNonWhitespaceToken(parentSource)) {
            throw new IllegalArgumentException("Parent source must be a non-empty token");
        }
        if (titleAliases != null && titleAliases.isEmpty()) {
            throw new IllegalArgumentException("Title aliases must be omitted when empty");
        }
        titleAliases = titleAliases == null ? List.of() : List.copyOf(titleAliases);
        if (new HashSet<>(titleAliases).size() != titleAliases.size()) {
            throw new IllegalArgumentException("Title aliases must be unique");
        }
        if (titleAliases.stream().anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Title aliases must not be empty");
        }
    }

    private static boolean isNonWhitespaceToken(String value) {
        return !value.isEmpty() && value.chars().noneMatch(Character::isWhitespace);
    }
}
