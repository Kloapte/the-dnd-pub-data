package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ScalingQuantity;

public record ScalingRelationshipRule(ScalingQuantity affectedQuantity) {
    public ScalingRelationshipRule {
        ModelChecks.required(affectedQuantity, "affectedQuantity");
        ModelChecks.require(affectedQuantity != ScalingQuantity.UNSPECIFIED_NUMERIC_QUANTITY,
                "A scaling relationship must identify its affected quantity");
    }
}
