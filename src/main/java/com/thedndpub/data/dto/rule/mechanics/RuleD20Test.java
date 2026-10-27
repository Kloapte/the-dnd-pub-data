package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTestTarget;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleD20Test(
        RuleMechanicKind kind,
        RuleMechanicSubtype subtype,
        List<RuleTestTarget> includes,
        Boolean dmDeterminesWhenWarranted,
        String componentId,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
    public RuleD20Test {
        includes = includes == null ? List.of() : List.copyOf(includes);
    }
}
