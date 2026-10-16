package com.thedndpub.data.dte.action;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ActionTimeDeserializer.class)
public sealed interface ActionTimeDte permits ActionTimeUnitDte, ActionTimeSpecialDte {
}
