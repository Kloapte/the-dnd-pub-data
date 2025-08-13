package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwriteDto {
    private boolean ability;
    private boolean languageProficiencies;
    private boolean skillProficiencies;
}
