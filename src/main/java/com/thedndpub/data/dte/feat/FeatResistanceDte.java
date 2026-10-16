package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeatResistanceDeserializer.class)
public sealed interface FeatResistanceDte permits FeatNamedResistanceDte, FeatChoiceResistanceDte {
}
