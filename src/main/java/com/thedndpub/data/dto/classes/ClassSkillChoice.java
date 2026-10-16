package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSkillChoice(
        Integer any,
        Integer count,
        List<ClassExternalReference> from) {
    public ClassSkillChoice {
        from = from == null ? List.of() : List.copyOf(from);
    }
}
