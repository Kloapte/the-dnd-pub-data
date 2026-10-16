package com.thedndpub.data.importer.classes;

import java.util.Set;

/** Root-level presence is needed to distinguish inherited data from explicit null/empty overrides. */
public record ClassFieldPresence(Set<String> present, Set<String> explicitNulls) {
    public ClassFieldPresence {
        present = Set.copyOf(present);
        explicitNulls = Set.copyOf(explicitNulls);
    }
}
