package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantDto {
    private String name;
    private SourceType source;
    List<VariantEntryDto> entries;
}
