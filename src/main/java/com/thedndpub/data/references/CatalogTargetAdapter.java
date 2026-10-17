package com.thedndpub.data.references;

import java.util.List;

public interface CatalogTargetAdapter {
    ReferenceTargetKind targetKind();

    List<TargetIdentity> exact(String name, String source);

    List<TargetIdentity> candidates(String name);

    List<TargetIdentity> identities();
}
