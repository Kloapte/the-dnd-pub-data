package com.thedndpub.data.dte.cultboon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CultBoonReprintDeserializer.class)
public sealed interface CultBoonReprintDte permits CultBoonReprintUidDte, CultBoonTaggedReprintDte {
    String uid();

    default String tag() {
        return null;
    }
}
