package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionCreatureGroup;
import com.thedndpub.data.dto.action.enums.ActionSkillTask;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSkillTaskGuidance(
        String kind,
        ActionReference skill,
        List<ActionSkillTask> tasks,
        List<ActionCreatureGroup> creatureGroups) {

    public ActionSkillTaskGuidance {
        tasks = tasks == null ? List.of() : List.copyOf(tasks);
        creatureGroups = creatureGroups == null ? List.of() : List.copyOf(creatureGroups);
    }
}
