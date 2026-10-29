package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.enums.StatusMechanicKind;
import com.thedndpub.data.dto.status.enums.StatusTurnApplicability;
import com.thedndpub.data.dto.status.enums.StatusTurnRestrictionKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TurnRestrictionMechanic(
        StatusMechanicKind kind,
        StatusTurnRestrictionKind restriction,
        StatusTurnApplicability appliesWhen) implements StatusMechanic {
    public TurnRestrictionMechanic {
        if (kind != StatusMechanicKind.TURN_RESTRICTION) {
            throw new IllegalArgumentException("Turn restriction kind is invalid");
        }
        Objects.requireNonNull(restriction, "Turn restriction is required");
        Objects.requireNonNull(appliesWhen, "Turn restriction appliesWhen is required");
    }
}
