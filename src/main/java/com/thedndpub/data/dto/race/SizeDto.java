package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SizeDto {
    private List<String> sizeTags;
    private Integer baseHeight;
    private String heightMod;
    private Integer baseWeight;
    private String weightMod;
}