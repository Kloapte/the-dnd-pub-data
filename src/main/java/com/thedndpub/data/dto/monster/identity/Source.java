package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Source(String code, Integer page, List<SourceReference> otherSources,
                     List<SourceReference> referenceSources, List<String> reprintedAs,
                     RulesAvailability availability) {
}
