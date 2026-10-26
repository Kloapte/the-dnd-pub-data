package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.AbilityScore;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MaintenanceSave(
        String kind,
        AbilityScore ability,
        String trigger,
        Boolean separateEachTrigger,
        MaintenanceDc dc,
        String failure,
        String requirement) implements StatusMechanic {
    public MaintenanceSave {
        if (!"MAINTENANCE_SAVE".equals(kind) || ability == null || trigger == null || dc == null || failure == null) {
            throw new IllegalArgumentException("Invalid maintenance save mechanic");
        }
    }
}
