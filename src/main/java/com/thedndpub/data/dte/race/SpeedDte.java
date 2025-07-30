package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.SpeedDtoDeserializer;
import lombok.Data;

@Data
public class SpeedDte {
    private Integer walk;
    private Integer flySpeed;
    private Boolean flyAvailable;
}
