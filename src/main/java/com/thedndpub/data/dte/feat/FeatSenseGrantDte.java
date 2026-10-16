package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize(using = FeatSenseGrantDeserializer.class)
public record FeatSenseGrantDte(Map<String, Integer> ranges) {
}
