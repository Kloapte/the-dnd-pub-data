package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.RuleReference;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleJumpProcedureSelection(String kind, String selection, RuleReference horizontal,
        RuleReference vertical) implements RuleMovementDetails { }
