package com.thedndpub.data.dto.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dte.object.ObjectDefenseDte;
import com.thedndpub.data.dte.object.ObjectNamedDefenseDte;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ObjectDefenseDto(String name, Boolean specialValuePresent) {
    public static ObjectDefenseDto from(ObjectDefenseDte defense) {
        if (defense instanceof ObjectNamedDefenseDte named) {
            return new ObjectDefenseDto(named.name(), null);
        }
        return new ObjectDefenseDto(null, true);
    }
}
