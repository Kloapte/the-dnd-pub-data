package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceIdentity;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSpellFilterClause(
        ClassSpellFilterField field,
        List<Integer> levels,
        List<String> names,
        List<String> schools,
        List<SourceIdentity> sources) {
    public ClassSpellFilterClause {
        levels = levels == null ? List.of() : List.copyOf(levels);
        names = names == null ? List.of() : List.copyOf(names);
        schools = schools == null ? List.of() : List.copyOf(schools);
        sources = sources == null ? List.of() : List.copyOf(sources);
    }
}
