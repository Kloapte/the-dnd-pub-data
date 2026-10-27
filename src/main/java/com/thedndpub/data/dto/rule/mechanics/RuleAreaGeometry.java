package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleAreaGeometryDimensions.class),
        @JsonSubTypes.Type(value = RuleAreaGeometryShapeSet.class)
})
public interface RuleAreaGeometry extends RuleMechanic {
}
