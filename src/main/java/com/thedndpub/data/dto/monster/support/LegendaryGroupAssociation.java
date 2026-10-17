package com.thedndpub.data.dto.monster.support;

import com.thedndpub.data.dto.monster.identity.MonsterId;

import java.util.Objects;

/** Separate exact support association, never a shared cross-catalog resolver occurrence. */
public record LegendaryGroupAssociation(
        MonsterId monsterId,
        LegendaryGroupId requestedGroup,
        LegendaryGroupAssociationStatus status,
        LegendaryGroupId targetId) {
    public LegendaryGroupAssociation {
        Objects.requireNonNull(monsterId, "monsterId");
        Objects.requireNonNull(requestedGroup, "requestedGroup");
        Objects.requireNonNull(status, "status");
        if ((status == LegendaryGroupAssociationStatus.RESOLVED) != (targetId != null)) {
            throw new IllegalArgumentException("Only resolved associations may carry a target");
        }
        if (targetId != null && !targetId.equals(requestedGroup)) {
            throw new IllegalArgumentException("Legendary groups require exact name/source matches");
        }
    }
}
