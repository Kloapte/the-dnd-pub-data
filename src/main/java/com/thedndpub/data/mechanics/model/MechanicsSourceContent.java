package com.thedndpub.data.mechanics.model;

/** Raw bytes supplied by a trusted pinned-resource loader for source-backed evidence verification. */
public record MechanicsSourceContent(SourceArtifactRevision revision, byte[] bytes) {
    public MechanicsSourceContent {
        ModelChecks.required(revision, "revision");
        ModelChecks.required(bytes, "bytes");
        bytes = bytes.clone();
        ModelChecks.require(MechanicsHashes.sha256(bytes).equals(revision.resourceSha256()),
                "Source bytes do not match their pinned revision");
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }
}
