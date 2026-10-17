package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ObjectDefenseDeserializer.class)
public sealed interface ObjectDefenseDte permits ObjectNamedDefenseDte, ObjectSpecialDefenseDte {
}
