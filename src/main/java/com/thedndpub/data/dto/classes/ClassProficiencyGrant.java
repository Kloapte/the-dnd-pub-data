package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassProficiencyGrant(
        String proficiency,
        String descriptiveRestriction,
        Boolean optional) {
}
