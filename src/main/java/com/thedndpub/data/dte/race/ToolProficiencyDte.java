package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ToolProficiencyDte {
    private String name;
    private ChooseDte choose;
    private int anyMusicalInstrument;
    private int anyArtisansTool;
    private int any;
    @JsonProperty("poisoner's kit")
    private boolean poisonersKit;
    @JsonProperty("tinker's tools")
    private boolean tinkersTools;
    @JsonProperty("thieves' tools")
    private boolean thievesTools;
    @JsonProperty("navigator's tools")
    private boolean navigatorsTools;
}
