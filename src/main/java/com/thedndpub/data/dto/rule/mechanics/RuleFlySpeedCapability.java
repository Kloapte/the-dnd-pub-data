package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlySpeedCapability(String kind, String travelMode, List<String> staysAloftUntil)
        implements RuleMovementDetails {
    public RuleFlySpeedCapability { staysAloftUntil = staysAloftUntil == null ? List.of() : List.copyOf(staysAloftUntil); }
}
