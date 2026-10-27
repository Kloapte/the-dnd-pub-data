package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionCapability;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionRecord(
        String id,
        String name,
        ActionSourceMetadata sourceMetadata,
        ActionTiming timing,
        List<ActionCapability> capabilities,
        List<ActionRequirement> requirements,
        List<ActionOperation> operations,
        List<ActionReference> references,
        List<ActionChoiceGroup> choiceGroups,
        List<ActionSkillTaskGuidance> skillTaskGuidance) {

    public ActionRecord {
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        requirements = requirements == null ? List.of() : List.copyOf(requirements);
        operations = operations == null ? List.of() : List.copyOf(operations);
        references = references == null ? List.of() : List.copyOf(references);
        choiceGroups = choiceGroups == null ? List.of() : List.copyOf(choiceGroups);
        skillTaskGuidance = skillTaskGuidance == null ? List.of() : List.copyOf(skillTaskGuidance);
    }
}
