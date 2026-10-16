package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

/** Named source grants use dynamic keys, but each key's value has a closed, validated value type. */
@JsonDeserialize(using = FeatGrantDeserializer.class)
public record FeatGrantDte(Map<String, FeatGrantValueDte> values) {
}
