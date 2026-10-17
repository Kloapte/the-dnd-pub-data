package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ObjectSpeedDeserializer.class)
public sealed interface ObjectSpeedDte permits ObjectNumericSpeedDte, ObjectMovementSpeedDte {
}
