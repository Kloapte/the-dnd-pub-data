package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.action.enums.ActionCheckAbilitySelection;
import com.thedndpub.data.dto.action.enums.ActionCheckActor;
import com.thedndpub.data.dto.action.enums.ActionCheckFailure;
import com.thedndpub.data.dto.action.enums.ActionCheckOpposingAbilitySelection;
import com.thedndpub.data.dto.action.enums.ActionCheckRequirement;
import com.thedndpub.data.dto.action.enums.ActionCheckResolution;
import com.thedndpub.data.dto.action.enums.ActionCheckTarget;
import com.thedndpub.data.dto.action.enums.ActionCheckTargetLimit;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionCheckOrContest(
        ActionOperationKind kind,
        String operationId,
        ActionCheckResolution resolution,
        ActionCheckActor actor,
        List<ActionAbility> abilities,
        ActionCheckAbilitySelection abilitySelection,
        ActionReference skill,
        List<ActionReference> opposingSkills,
        ActionDcValue dc,
        ActionReference dcSource,
        List<ActionAbility> opposingAbilities,
        ActionCheckOpposingAbilitySelection opposingAbilitySelection,
        ActionCheckFailure failure,
        ActionCheckTargetLimit targetLimit,
        Boolean targetWithinReach,
        ActionConditionPredicate automaticSuccessWhen,
        Boolean replacesOneAttack,
        ActionCheckTarget target,
        List<ActionCheckParticipant> participants,
        ActionCheckRequirement requirement,
        String relativeSizeModifier) implements ActionOperation {

    public ActionCheckOrContest {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
        opposingSkills = opposingSkills == null ? List.of() : List.copyOf(opposingSkills);
        opposingAbilities = opposingAbilities == null ? List.of() : List.copyOf(opposingAbilities);
        participants = participants == null ? List.of() : List.copyOf(participants);
    }
}
