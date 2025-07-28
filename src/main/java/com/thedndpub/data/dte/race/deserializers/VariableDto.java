package com.thedndpub.data.dte.race.deserializers;

import lombok.Data;

import java.util.List;

@Data
public class VariableDto {
    private String color;
    private String damageType;
    private String area;
    private String savingThrow;
    private List<String> resist;
}
