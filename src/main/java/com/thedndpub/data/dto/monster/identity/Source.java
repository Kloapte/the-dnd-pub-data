package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Source(@JsonProperty("code") SourceIdentity code, Integer page, List<SourceReference> otherSources,
                     List<SourceReference> referenceSources, List<String> reprintedAs,
                     RulesAvailability availability) {
    public Source {
        otherSources = otherSources == null ? List.of() : List.copyOf(otherSources);
        referenceSources = referenceSources == null ? List.of() : List.copyOf(referenceSources);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }

    public Source(String code, Integer page, List<SourceReference> otherSources,
                  List<SourceReference> referenceSources, List<String> reprintedAs,
                  RulesAvailability availability) {
        this(new SourceIdentity(code), page, otherSources, referenceSources, reprintedAs, availability);
    }
}
