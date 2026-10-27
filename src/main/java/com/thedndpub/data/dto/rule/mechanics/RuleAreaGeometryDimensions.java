package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleAreaDimensionParameter;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleAreaOrigin;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleAreaOriginInclusion;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleAreaOriginPlacement;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleAreaShape;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAreaGeometryDimensions(String componentId, RuleMechanicKind kind, RuleMechanicSubtype subtype,
        RuleAreaShape shape, RuleAreaOrigin origin, List<Double> dimensions, String unit,
        RuleComponentApplicability appliesWhen, RuleAreaOriginPlacement originPlacement,
        List<RuleAreaDimensionParameter> dimensionParameters, RuleAreaOriginInclusion originIncluded, String coneWidthRule,
        RuleOriginBehavior originBehavior) implements RuleAreaGeometry {
    public RuleAreaGeometryDimensions {
        dimensions = dimensions == null ? List.of() : List.copyOf(dimensions);
        dimensionParameters = dimensionParameters == null ? List.of() : List.copyOf(dimensionParameters);
    }
}
