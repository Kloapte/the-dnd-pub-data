package com.thedndpub.data.mechanics.model;

/** Pinned source-manifest association between one source-aware owner and its authored JSON root. */
public record MechanicsSourceOwnerPointer(
        MechanicsOwnerReference owner,
        SourceArtifactRevision revision,
        String jsonPointer) {
    public MechanicsSourceOwnerPointer {
        ModelChecks.required(owner, "owner");
        ModelChecks.required(revision, "revision");
        ModelChecks.pointer(jsonPointer);
    }
}
