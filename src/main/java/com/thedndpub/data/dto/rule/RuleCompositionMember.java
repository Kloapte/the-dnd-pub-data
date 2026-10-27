package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleComponentMember.class, name = "COMPONENT"),
        @JsonSubTypes.Type(value = RuleCompositionGroup.class, name = "AND"),
        @JsonSubTypes.Type(value = RuleCompositionGroup.class, name = "OR")
})
public sealed interface RuleCompositionMember permits RuleComponentMember, RuleCompositionGroup {
}
