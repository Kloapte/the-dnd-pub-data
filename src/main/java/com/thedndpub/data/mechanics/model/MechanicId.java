package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicId(
        String value) {
    public MechanicId {
        ModelChecks.text(value, "mechanicId");
        ModelChecks.require(value.matches("mechanic:[a-z0-9-]+:[a-z0-9:-]+:[a-z0-9]+(?:-[a-z0-9]+)*"),
                "Invalid source-aware mechanic ID");
    }

    public static MechanicId of(MechanicsOwnerReference owner, String stableSlot) {
        ModelChecks.text(stableSlot, "stableSlot");
        ModelChecks.require(stableSlot.matches("[a-z0-9]+(?:-[a-z0-9]+)*"), "Invalid stable claim slot");
        return new MechanicId("mechanic:" + owner.id() + ":" + stableSlot);
    }

    @JsonValue
    public String serializedValue() {
        return value;
    }

    public String ownerId() {
        return value.substring("mechanic:".length(), value.lastIndexOf(':'));
    }
}
