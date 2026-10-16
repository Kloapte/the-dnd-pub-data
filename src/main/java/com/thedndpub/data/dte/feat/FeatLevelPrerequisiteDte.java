package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeatLevelPrerequisiteDeserializer.class)
public sealed interface FeatLevelPrerequisiteDte
        permits FeatSimpleLevelPrerequisiteDte, FeatClassLevelPrerequisiteDte {
}
