package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.enums.StatusMechanicKind;
import com.thedndpub.data.dto.status.enums.StatusSaveAbility;
import com.thedndpub.data.dto.status.enums.StatusSaveFailure;
import com.thedndpub.data.dto.status.enums.StatusSaveRequirement;
import com.thedndpub.data.dto.status.enums.StatusSaveTrigger;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MaintenanceSaveMechanic(
        StatusMechanicKind kind,
        StatusSaveAbility ability,
        StatusSaveTrigger trigger,
        Boolean separateEachTrigger,
        MaintenanceDc dc,
        StatusSaveFailure failure,
        StatusSaveRequirement requirement) implements StatusMechanic {
    public MaintenanceSaveMechanic {
        if (kind != StatusMechanicKind.MAINTENANCE_SAVE) {
            throw new IllegalArgumentException("Maintenance save kind is invalid");
        }
        Objects.requireNonNull(ability, "Maintenance save ability is required");
        Objects.requireNonNull(trigger, "Maintenance save trigger is required");
        Objects.requireNonNull(dc, "Maintenance save DC is required");
        if (trigger == StatusSaveTrigger.DAMAGE && requirement != null) {
            throw new IllegalArgumentException("Damage-triggered saves do not allow a requirement");
        }
    }
}
