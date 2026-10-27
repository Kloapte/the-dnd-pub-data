package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionD20RollModifier;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.action.enums.ActionRollSelection;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTestKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTestModifierDirection;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleTestModifier(RuleMechanicKind kind, RuleMechanicSubtype subtype, ActionD20RollModifier modifier,
        RuleTestKind test, RuleTestModifierDirection direction, Double amount, Integer diceCount, ActionRollSelection select,
        Boolean cancelsOpposite, Boolean multipleInstancesStack, String componentId,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
