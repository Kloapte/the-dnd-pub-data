package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionCheckParticipant(
        String role,
        String selection,
        List<ActionAbilitySkillOption> options) {

    public ActionCheckParticipant {
        options = options == null ? List.of() : List.copyOf(options);
    }
}
