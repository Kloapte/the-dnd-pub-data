package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceMembershipIndex;
import com.thedndpub.data.importer.object.ObjectEvidenceMembershipAdapter;
import org.springframework.stereotype.Component;

/** Startup-only internal evidence index, with no public DTO or controller integration. */
@Component
public final class ObjectEvidenceMembershipCatalog {
    private final ObjectEvidenceMembershipIndex index;

    public ObjectEvidenceMembershipCatalog(ObjectMapper mapper, ObjectCatalog objects) {
        index = new ObjectEvidenceMembershipAdapter(mapper).adapt(objects.definitions());
    }

    public ObjectEvidenceMembershipIndex index() {
        return index;
    }
}
