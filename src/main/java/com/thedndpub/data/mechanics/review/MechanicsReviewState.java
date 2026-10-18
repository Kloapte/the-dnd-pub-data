package com.thedndpub.data.mechanics.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.FindingSeverity;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsFinding;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsReviewState(
        MechanicRevisionReference claim,
        ValidatedHumanMechanicsReview humanDecision,
        List<MechanicsFinding> findings) {
    public MechanicsReviewState {
        ModelChecks.required(claim, "claim");
        findings = ModelChecks.list(findings);
    }

    public static MechanicsReviewState pending(MechanicRevisionReference claim) {
        return new MechanicsReviewState(claim, null, List.of());
    }

    public MechanicsReviewStatus recordedStatus() {
        if (humanDecision == null) {
            return MechanicsReviewStatus.PENDING_REVIEW;
        }
        return switch (humanDecision.decision().action()) {
            case VERIFY -> MechanicsReviewStatus.VERIFIED;
            case REJECT -> MechanicsReviewStatus.REJECTED;
            case REQUEST_MECHANICS_REVIEW -> MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW;
        };
    }

    public MechanicsReviewStatus effectiveStatus() {
        boolean applicable = humanDecision != null && humanDecision.decision().claim().equals(claim);
        if (applicable && humanDecision.decision().action() == MechanicsReviewAction.REJECT) {
            return MechanicsReviewStatus.REJECTED;
        }
        if ((humanDecision != null && !applicable)
                || findings.stream().anyMatch(finding -> finding.severity() == FindingSeverity.REQUIRES_REVIEW)) {
            return MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW;
        }
        return recordedStatus();
    }

    public MechanicsReviewState withFindings(List<MechanicsFinding> additional) {
        List<MechanicsFinding> combined = new ArrayList<>(findings);
        for (MechanicsFinding finding : additional) {
            if (!combined.contains(finding)) {
                combined.add(finding);
            }
        }
        return new MechanicsReviewState(claim, humanDecision, combined);
    }
}
