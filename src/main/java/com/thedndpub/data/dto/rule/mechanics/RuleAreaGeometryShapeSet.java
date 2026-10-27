package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAreaGeometryShapeSet(String componentId, RuleMechanicKind kind, String subtype,
        List<String> shapes, String originPlacement, RuleLineOfEffect lineOfEffect,
        String unseenOriginPlacement) implements RuleAreaGeometry {
    public RuleAreaGeometryShapeSet {
        shapes = shapes == null ? List.of() : List.copyOf(shapes);
    }
}
