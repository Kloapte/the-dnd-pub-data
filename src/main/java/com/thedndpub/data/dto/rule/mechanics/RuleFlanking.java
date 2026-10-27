package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionD20RollModifier;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlankingParticipant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlanking(RuleMechanicKind kind, RuleMechanicSubtype subtype, String componentId,
        Boolean requiresMiniatures, String targetRole, List<RuleFlankingParticipant> participants,
        Boolean participantsMustSeeTarget, RuleReference participantsCannotHaveCondition,
        Boolean requiresAdjacentPositions, RuleFlankingSquareGrid squareGrid,
        RuleFlankingHexGrid hexGrid, RuleFlankingLargeSpaceRule largeOrLargerSpaceRule,
        ActionD20RollModifier benefit) implements RuleMechanic {
    public RuleFlanking {
        participants = participants == null ? List.of() : List.copyOf(participants);
    }
}
