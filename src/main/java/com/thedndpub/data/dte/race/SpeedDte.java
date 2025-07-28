package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.SpeedDtoDeserializer;
import lombok.Data;

@Data
@JsonDeserialize(using = SpeedDtoDeserializer.class)
public class SpeedDte {
    private int walk;
    private Integer flySpeed;
    private Boolean flyAvailable;
}
