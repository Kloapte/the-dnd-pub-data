package com.thedndpub.data.dte.race;

import lombok.Data;

@Data
public class SpeedDte {
    private Integer walk;
    private Integer flySpeed;
    private Boolean flyAvailable;
    private Object climb; //TODO
    private Object fly; //TODO
    private Boolean canHover;
    private Object swim; //TODO
    private Object burrow;
}
