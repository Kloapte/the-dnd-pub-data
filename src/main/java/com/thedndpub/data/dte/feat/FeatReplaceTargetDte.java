package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeatReplaceTargetDeserializer.class)
public sealed interface FeatReplaceTargetDte permits FeatReplaceNameDte, FeatReplaceIndexDte {
}
