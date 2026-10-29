package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.enums.StatusActor;
import com.thedndpub.data.dto.status.enums.StatusMechanicKind;
import com.thedndpub.data.dto.status.enums.StatusRoll;
import com.thedndpub.data.dto.status.enums.StatusRollDirection;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RollModifierMechanic(
        StatusMechanicKind kind,
        StatusRoll roll,
        StatusRollDirection direction,
        StatusActor actor) implements StatusMechanic {
    public RollModifierMechanic {
        if (kind != StatusMechanicKind.ROLL_MODIFIER) {
            throw new IllegalArgumentException("Roll modifier kind is invalid");
        }
        Objects.requireNonNull(roll, "Roll modifier roll is required");
        Objects.requireNonNull(direction, "Roll modifier direction is required");
    }
}
