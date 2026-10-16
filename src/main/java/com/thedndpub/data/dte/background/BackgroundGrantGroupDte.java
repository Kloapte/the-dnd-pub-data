package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

@JsonDeserialize(using = BackgroundGrantGroupDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundGrantGroupDte(Map<String, BackgroundGrantValueDte> grants) {
    public BackgroundGrantGroupDte {
        grants = Collections.unmodifiableMap(new TreeMap<>(grants));
    }
}
