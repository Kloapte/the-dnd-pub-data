package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSpellSelection(
        ClassSpellSelectionKind kind,
        ClassSpellReference spell,
        List<ClassSpellReference> from,
        List<ClassSpellFilterClause> filter,
        boolean unrestricted,
        Integer count) {
    public ClassSpellSelection {
        from = from == null ? List.of() : List.copyOf(from);
        filter = filter == null ? List.of() : List.copyOf(filter);
    }
}
