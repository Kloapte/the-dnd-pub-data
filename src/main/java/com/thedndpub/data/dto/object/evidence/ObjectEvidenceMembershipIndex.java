package com.thedndpub.data.dto.object.evidence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Immutable authored-source index, including empty entries for Objects without typed attacks. */
public final class ObjectEvidenceMembershipIndex {
    private final List<ObjectEvidenceMembership> memberships;
    private final Map<String, List<ObjectEvidenceMembership>> byObjectId;

    public ObjectEvidenceMembershipIndex(List<String> objectIds, List<ObjectEvidenceMembership> memberships) {
        this.memberships = List.copyOf(memberships);
        Map<String, List<ObjectEvidenceMembership>> index = new LinkedHashMap<>();
        for (String id : objectIds) {
            if (id == null || id.isBlank() || index.putIfAbsent(id, new ArrayList<>()) != null) {
                throw new IllegalArgumentException("Unique nonblank Object identities are required");
            }
        }
        for (ObjectEvidenceMembership membership : this.memberships) {
            List<ObjectEvidenceMembership> values = index.get(membership.objectId());
            if (values == null || membership.occurrenceIndex() != values.size()) {
                throw new IllegalArgumentException("Membership owner or sequence does not match index");
            }
            values.add(membership);
        }
        Map<String, List<ObjectEvidenceMembership>> immutable = new LinkedHashMap<>();
        index.forEach((id, values) -> immutable.put(id, List.copyOf(values)));
        byObjectId = Collections.unmodifiableMap(immutable);
    }

    public List<ObjectEvidenceMembership> memberships() {
        return memberships;
    }

    public Map<String, List<ObjectEvidenceMembership>> byObjectId() {
        return byObjectId;
    }

    public List<ObjectEvidenceMembership> findByObjectId(String id) {
        return byObjectId.getOrDefault(id, List.of());
    }
}
