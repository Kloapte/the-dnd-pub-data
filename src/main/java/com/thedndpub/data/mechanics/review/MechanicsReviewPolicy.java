package com.thedndpub.data.mechanics.review;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.HashSet;
import java.util.List;

/** Trusted caller configuration, never loaded from an interpretation's JSON. No authentication is implied. */
public final class MechanicsReviewPolicy {
    public static final String CONTEXT_ATTRIBUTE = MechanicsReviewPolicy.class.getName();
    private final List<MechanicsReviewerAuthority> authorities;

    public MechanicsReviewPolicy(List<MechanicsReviewerAuthority> authorities) {
        this.authorities = ModelChecks.list(authorities);
        var ids = new HashSet<String>();
        for (var authority : this.authorities) {
            ModelChecks.require(ids.add(authority.reviewerId()), "Duplicate mechanics reviewer authority");
        }
    }

    public static MechanicsReviewPolicy none() {
        return new MechanicsReviewPolicy(List.of());
    }

    public void validate(HumanMechanicsReviewDecision decision) {
        ModelChecks.required(decision, "decision");
        ModelChecks.require(authorities.stream().anyMatch(authority ->
                authority.reviewerId().equals(decision.reviewerId())
                        && authority.authorityReference().equals(decision.authorityReference())),
                "Human review has no trusted mechanics reviewer authority");
    }
}
