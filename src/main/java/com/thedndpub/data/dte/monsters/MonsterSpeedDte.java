package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonsterSpeedDte {
    private Object walk;
    private Integer flySpeed;
    private Boolean flyAvailable;
    private Object climb; //TODO
    private Object fly; //TODO
    private Boolean canHover;
    private Object swim; //TODO
    private Object burrow;
    private Object alternate;
    private Object choose;
}
