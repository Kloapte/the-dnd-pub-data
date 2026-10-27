package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleDowntimeProcedure {
    @JsonProperty("craft") CRAFT,
    @JsonProperty("buy") BUY,
    @JsonProperty("sell") SELL,
    @JsonProperty("research") RESEARCH,
    @JsonProperty("train") TRAIN,
    @JsonProperty("work") WORK,
    @JsonProperty("gamble") GAMBLE,
    @JsonProperty("carouse") CAROUSE,
    @JsonProperty("franchise") FRANCHISE,
    @JsonProperty("recuperate") RECUPERATE,
    @JsonProperty("relaxation") RELAXATION
}
