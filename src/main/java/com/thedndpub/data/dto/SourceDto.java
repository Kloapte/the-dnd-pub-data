package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SourceDto {
    private SourceType source;
    private String description;
    private Map<SourceType, Integer> otherSources;
    private LicenseType license;
    private String edition;
    private List<SourceType> reprintedAs;
    private Integer page;
}
