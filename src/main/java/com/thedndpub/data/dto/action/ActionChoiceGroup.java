package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionChoiceGroup(
        String id,
        ActionChoiceCardinality cardinality,
        ActionChoiceTrigger trigger,
        List<ActionChoiceMember> members) {

    public ActionChoiceGroup {
        members = members == null ? List.of() : List.copyOf(members);
    }
}
