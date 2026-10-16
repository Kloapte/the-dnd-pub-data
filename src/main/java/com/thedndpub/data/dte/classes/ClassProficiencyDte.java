package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonDeserialize(using = ClassProficiencyDeserializer.class)
public record ClassProficiencyDte(
        String proficiency,
        String full,
        Boolean optional,
        @JsonIgnore boolean stringForm) {
}
