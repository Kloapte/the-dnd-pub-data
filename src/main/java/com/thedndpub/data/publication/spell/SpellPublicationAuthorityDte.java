package com.thedndpub.data.publication.spell;

import java.util.List;

public record SpellPublicationAuthorityDte(
        String reviewerId,
        String authorityReference,
        List<SpellPublicationRole> roles) {
    public SpellPublicationAuthorityDte {
        roles = List.copyOf(roles);
    }
}
