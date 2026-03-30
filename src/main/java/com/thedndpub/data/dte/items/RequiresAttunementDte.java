package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RequiresAttunementDte {
    List<String> alignment;
    String creatureType;
    @JsonProperty("class")
    String _class;
    Boolean spellcasting;
    String background;
    String race;
    String skillProficiency;
    Boolean psionics;
    String languageProficiency;
    String size;
    @JsonProperty("int")
    String _int;
}
