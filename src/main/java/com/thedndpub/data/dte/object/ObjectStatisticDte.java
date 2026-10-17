package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ObjectStatisticDeserializer.class)
public sealed interface ObjectStatisticDte permits ObjectNumericStatisticDte, ObjectSpecialStatisticDte {
}
