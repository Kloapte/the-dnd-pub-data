package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RaceReprintDeserializer.class)
public record RaceReprintDte(String uid, String tag) {
}
