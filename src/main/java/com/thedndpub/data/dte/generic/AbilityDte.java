package com.thedndpub.data.dte.generic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.generic.deserializers.ChooseDteDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbilityDte {
    private String name;
    @JsonDeserialize(using = ChooseDteDeserializer.class)
    private ChooseDte choose;
    private int dex;
    private int wis;
    private int cha;
    private int str;
    private int con;
    @JsonProperty("int")
    private int intel;
    @JsonProperty("static")
    private Object _static;
}
