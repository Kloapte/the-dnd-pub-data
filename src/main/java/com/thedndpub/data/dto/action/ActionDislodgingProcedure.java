package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionDislodgingProcedure(
        String actionCost,
        String initiator,
        List<String> initiatorOptions,
        List<String> opponentOptions,
        Boolean opponentChoosesOption) {

    public ActionDislodgingProcedure {
        initiatorOptions = initiatorOptions == null ? List.of() : List.copyOf(initiatorOptions);
        opponentOptions = opponentOptions == null ? List.of() : List.copyOf(opponentOptions);
    }
}
