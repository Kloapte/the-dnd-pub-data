package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleFailedSaveConditionApplication.class, name = "APPLY_CONDITION"),
        @JsonSubTypes.Type(value = RuleFailedSaveChooseOne.class, name = "CHOOSE_ONE")
})
public sealed interface RuleFailedSave permits RuleFailedSaveConditionApplication, RuleFailedSaveChooseOne {
    String kind();
}
