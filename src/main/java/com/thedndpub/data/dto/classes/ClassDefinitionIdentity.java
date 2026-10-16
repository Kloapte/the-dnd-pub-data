package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Full source identity; feature source and both parent sources are intentionally distinct. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassDefinitionIdentity(
        ClassDefinitionKind kind,
        String name,
        SourceIdentity source,
        String className,
        SourceIdentity classSource,
        String subclassShortName,
        SourceIdentity subclassSource,
        Integer level) {
    public ClassDefinitionIdentity {
        Objects.requireNonNull(kind, "kind");
        requireText(name, "name");
        Objects.requireNonNull(source, "source");
        if (kind != ClassDefinitionKind.CLASS) {
            requireText(className, "className");
            Objects.requireNonNull(classSource, "classSource");
        }
        if (kind == ClassDefinitionKind.CLASS_FEATURE || kind == ClassDefinitionKind.SUBCLASS_FEATURE) {
            if (level == null || level < 1) {
                throw new IllegalArgumentException("Feature identity requires a positive level");
            }
        }
        if (kind == ClassDefinitionKind.SUBCLASS_FEATURE) {
            requireText(subclassShortName, "subclassShortName");
            Objects.requireNonNull(subclassSource, "subclassSource");
        }
    }

    public String lookupKey() {
        return Stream.of(kind.name(), name, source.code(), className,
                        classSource == null ? null : classSource.code(), subclassShortName,
                        subclassSource == null ? null : subclassSource.code(), level == null ? null : level.toString())
                .map(value -> value == null ? "" : value.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining("\u0000"));
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank() || value.contains("|") || value.contains("\u0000")) {
            throw new IllegalArgumentException("Invalid identity " + field + ": " + value);
        }
    }
}
