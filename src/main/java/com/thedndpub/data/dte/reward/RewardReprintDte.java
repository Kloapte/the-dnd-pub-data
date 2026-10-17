package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RewardReprintDeserializer.class)
public sealed interface RewardReprintDte permits RewardReprintUidDte, RewardReprintTokenDte {
}
