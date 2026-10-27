package com.thedndpub.data.dto.rule.mechanics;

import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestType;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleNoRestRecoveryResource;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleNoRestRecovery(String kind, RuleNoRestRecoveryResource resource, RuleRestType restType)
        implements RuleRecoveryOutcome { }
