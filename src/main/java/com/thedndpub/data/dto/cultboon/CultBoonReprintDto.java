package com.thedndpub.data.dto.cultboon;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CultBoonReprintDto(String name, SourceIdentity source, String tag) {
    public static CultBoonReprintDto from(CultBoonReprint reprint) {
        return new CultBoonReprintDto(reprint.name(), reprint.source(), reprint.tag());
    }
}
