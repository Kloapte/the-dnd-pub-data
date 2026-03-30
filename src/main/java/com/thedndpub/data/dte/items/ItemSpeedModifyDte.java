package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemSpeedModifyDte {
    private int walk;
    private int burrow;
    private int swim;
    private int fly;
    @JsonProperty("*")
    private int all;
}
