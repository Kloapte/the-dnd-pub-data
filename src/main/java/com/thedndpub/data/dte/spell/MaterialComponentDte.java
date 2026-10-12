package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MaterialComponentDeserializer.class)
public sealed interface MaterialComponentDte permits MaterialTextDte, MaterialDetailsDte {
}
