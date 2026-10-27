package com.thedndpub.data.dto.rule.mechanics;

import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestType;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRechargeAsDefinedByFeature(String kind, String resource, RuleRestType restType)
        implements RuleRecoveryOutcome { }
