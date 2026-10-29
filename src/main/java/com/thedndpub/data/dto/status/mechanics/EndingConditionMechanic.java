package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.StatusReference;
import com.thedndpub.data.dto.status.enums.StatusEndingWhen;
import com.thedndpub.data.dto.status.enums.StatusMechanicKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EndingConditionMechanic(
        StatusMechanicKind kind,
        StatusEndingWhen when,
        Boolean automatic,
        Boolean actionRequired,
        StatusReference reference) implements StatusMechanic {
    public EndingConditionMechanic {
        if (kind != StatusMechanicKind.ENDING_CONDITION) {
            throw new IllegalArgumentException("Ending condition kind is invalid");
        }
        Objects.requireNonNull(when, "Ending condition when is required");
        if (when == StatusEndingWhen.VOLUNTARILY) {
            if (actionRequired != Boolean.FALSE) {
                throw new IllegalArgumentException("Voluntary ending requires actionRequired=false");
            }
        } else if (automatic != Boolean.TRUE) {
            throw new IllegalArgumentException("This ending condition requires automatic=true");
        }
    }
}
