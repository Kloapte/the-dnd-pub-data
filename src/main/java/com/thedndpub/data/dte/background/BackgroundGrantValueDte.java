package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonDeserialize(using = BackgroundGrantValueDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundGrantValueDte(Boolean granted, Integer count, BackgroundGrantChoiceDte choice) {
}
