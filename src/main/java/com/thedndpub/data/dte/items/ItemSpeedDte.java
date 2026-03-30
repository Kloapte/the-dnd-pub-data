package com.thedndpub.data.dte.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemSpeedDte {
    @JsonProperty("static")
    private ItemSpeedModifyDte _static;
    private ItemSpeedModifyEqualDte equal;
    private ItemSpeedModifyDte multiply;
    private ItemSpeedModifyDte bonus;
}
