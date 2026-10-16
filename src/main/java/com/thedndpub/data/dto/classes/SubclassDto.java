package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassDto(String id, String name, String shortName, SourceReference source,
        String parentClassId, String edition, List<ClassFeatureReferenceDto> features,
        List<ClassProgressionTable> tables, List<ClassChoiceProgression> choices,
        ClassSpellcasting spellcasting, ClassSourceMetadataDto sources) {
    public SubclassDto {
        features = features == null ? List.of() : List.copyOf(features);
        tables = tables == null ? List.of() : List.copyOf(tables);
        choices = choices == null ? List.of() : List.copyOf(choices);
    }
}
