package com.thedndpub.data.dto.item;

import java.util.Objects;

/** Source-authored baseItem declaration associated with its catalog-owned Item ID. */
public record ItemBaseItemReference(String ownerItemId, String originalUid) {
    public ItemBaseItemReference {
        Objects.requireNonNull(ownerItemId, "ownerItemId");
        Objects.requireNonNull(originalUid, "originalUid");
    }
}
