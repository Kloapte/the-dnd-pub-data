package com.thedndpub.data.dte.traphazard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TrapStageDeserializer.class)
public sealed interface TrapStageDte permits TrapStageTextDte, TrapStageGroupDte {
}
