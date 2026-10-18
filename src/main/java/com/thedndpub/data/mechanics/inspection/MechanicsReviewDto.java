package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.MechanicsFinding;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsReviewDto(
        MechanicsReviewStatus recordedStatus,
        MechanicsReviewStatus effectiveStatus,
        List<MechanicsFinding> findings) {
    public MechanicsReviewDto {
        ModelChecks.required(recordedStatus, "recordedStatus");
        ModelChecks.required(effectiveStatus, "effectiveStatus");
        findings = ModelChecks.list(findings);
    }
}
