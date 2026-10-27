package com.thedndpub.data.dto.action;

import com.thedndpub.data.dto.action.deserialer.ActionDcValueDeserializer;
import com.thedndpub.data.dto.action.serialzer.ActionDcValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = ActionDcValueDeserializer.class)
@JsonSerialize(using = ActionDcValueSerializer.class)
public sealed interface ActionDcValue permits ActionNumericDc, ActionStructuredDc {
}
