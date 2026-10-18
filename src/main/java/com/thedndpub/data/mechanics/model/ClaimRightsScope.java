package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClaimRightsScope(
        MechanicRevisionReference claim,
        List<String> claimPaths) {
    public ClaimRightsScope {
        ModelChecks.required(claim, "claim");
        claimPaths = ModelChecks.list(claimPaths);
        ModelChecks.require(!claimPaths.isEmpty(), "Rights scope must identify claim fields");
        for (String pointer : claimPaths) {
            ModelChecks.pointer(pointer);
            ModelChecks.require(pointer.equals("/payload") || pointer.startsWith("/payload/")
                    || pointer.equals("/context") || pointer.startsWith("/context/"), "Invalid rights field scope");
        }
    }

    public boolean coversWholeClaim() {
        return claimPaths.contains("/payload") && claimPaths.contains("/context");
    }
}
