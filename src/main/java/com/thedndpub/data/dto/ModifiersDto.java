package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModifiersDto {
    private List<String> resists;
    private ChoiceDto resistChoices;
    private List<String> immunities;
    private List<String> vulnerabilities;
}
