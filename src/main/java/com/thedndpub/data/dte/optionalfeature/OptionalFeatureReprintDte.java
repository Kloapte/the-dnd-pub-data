package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OptionalFeatureReprintDeserializer.class)
public record OptionalFeatureReprintDte(String uid, String tag) {
}
