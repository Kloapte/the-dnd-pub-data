package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RaceSpeedDeserializer.class)
public record RaceSpeedDte(
        RaceSpeedValueDte walk,
        RaceSpeedValueDte fly,
        RaceSpeedValueDte swim,
        RaceSpeedValueDte climb,
        RaceSpeedValueDte burrow) {
}
