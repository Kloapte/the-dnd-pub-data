package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAreaGeometryDimensions(String componentId, RuleMechanicKind kind, String subtype, String shape,
        String origin, List<Double> dimensions, String unit, RuleComponentApplicability appliesWhen,
        String originPlacement, List<String> dimensionParameters, String originIncluded, String coneWidthRule,
        RuleOriginBehavior originBehavior) implements RuleAreaGeometry {
    public RuleAreaGeometryDimensions {
        dimensions = dimensions == null ? List.of() : List.copyOf(dimensions);
        dimensionParameters = dimensionParameters == null ? List.of() : List.copyOf(dimensionParameters);
    }
}
