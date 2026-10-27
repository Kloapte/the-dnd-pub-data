package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlankingLargeSpaceRule(String appliesToFlankerSize, String qualification) {
}
