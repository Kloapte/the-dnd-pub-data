package com.thedndpub.data.dte.race;

import lombok.Data;

import java.util.List;

@Data
public class VariableDte {
    private String color;
    private String damageType;
    private String area;
    private String savingThrow;
    private List<String> resist;
}
