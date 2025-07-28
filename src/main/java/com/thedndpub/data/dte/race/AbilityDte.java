package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbilityDte {
    private ChooseDte choose;
    private int dex;
    private int wis;
    private int cha;
    private int str;
    private int con;
    @JsonProperty("int")
    private int intel;
}
