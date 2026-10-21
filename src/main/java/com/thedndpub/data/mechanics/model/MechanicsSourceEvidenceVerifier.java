package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Source-aware verification boundary. Structural document construction alone does not verify provenance. */
public final class MechanicsSourceEvidenceVerifier {
    private MechanicsSourceEvidenceVerifier() {
    }

    public static void verify(MechanicsDocument document, List<MechanicsSourceContent> resources,
                              List<SourceArtifactRevision> trustedPinnedRevisions,
                              List<MechanicsSourceOwnerPointer> trustedOwnerPointers, ObjectMapper mapper) {
        ModelChecks.required(document, "document");
        ModelChecks.required(mapper, "mapper");
        Map<String, SourceArtifactRevision> trusted = new HashMap<>();
        for (SourceArtifactRevision revision : ModelChecks.list(trustedPinnedRevisions)) {
            ModelChecks.require(trusted.putIfAbsent(revision.resourcePath(), revision) == null,
                    "Duplicate trusted source revision");
        }
        for (SourceArtifactRevision revision : document.sources()) {
            ModelChecks.require(revision.equals(trusted.get(revision.resourcePath())),
                    "Document source revision is not in the trusted pinned manifest");
        }
        List<MechanicsSourceOwnerPointer> trustedOwners = ModelChecks.list(trustedOwnerPointers);
        Map<String, MechanicsSourceContent> content = new HashMap<>();
        for (MechanicsSourceContent resource : ModelChecks.list(resources)) {
            ModelChecks.require(content.putIfAbsent(resource.revision().resourcePath(), resource) == null,
                    "Duplicate source content resource");
        }
        ModelChecks.require(content.keySet().equals(document.sources().stream()
                        .map(SourceArtifactRevision::resourcePath).collect(java.util.stream.Collectors.toSet())),
                "Source content must exactly match document revisions");
        Map<String, JsonNode> trees = new HashMap<>();
        for (SourceArtifactRevision revision : document.sources()) {
            MechanicsSourceContent resource = content.get(revision.resourcePath());
            ModelChecks.require(resource.revision().equals(revision), "Source revision fingerprint mismatch");
            try {
                trees.put(revision.resourcePath(), mapper.readTree(resource.bytes()));
            } catch (IOException exception) {
                throw new IllegalArgumentException("Pinned source is not valid JSON", exception);
            }
        }
        for (MechanicsEvidence evidence : document.evidence()) {
            if (evidence instanceof TextMechanicsEvidence text) {
                var location = text.syntax().location();
                JsonNode tree = sourceTree(document, trees, location.owner().resourcePath(),
                        location.owner().resourceSha256());
                requireTrustedOwner(trustedOwners, location.owner().resourcePath(),
                        location.owner().jsonPointer(), text.authoredOwner());
                verifyAuthoredOwner(tree, location.owner().collection(), location.owner().jsonPointer(),
                        text.source().identity().code(), text.source().page(), text.authoredOwner());
                JsonNode textNode = at(tree, location.jsonPointer());
                ModelChecks.require(textNode.isTextual(), "Text evidence pointer must identify source text");
                String value = textNode.textValue();
                ModelChecks.require(MechanicsHashes.sha256(value).equals(text.textSha256()),
                        "Text evidence fingerprint does not match pinned source");
                validateSpan(value, text.syntax().span());
            } else if (evidence instanceof StructuredFieldMechanicsEvidence field) {
                JsonNode tree = sourceTree(document, trees, field.owner().resourcePath(),
                        field.owner().resourceSha256());
                requireTrustedOwner(trustedOwners, field.owner().resourcePath(),
                        field.owner().jsonPointer(), field.authoredOwner());
                verifyAuthoredOwner(tree, field.owner().collection(), field.owner().jsonPointer(),
                        field.source().identity().code(), field.source().page(), field.authoredOwner());
                JsonNode value = at(tree, field.jsonPointer());
                ModelChecks.require(!value.isMissingNode(), "Structured evidence pointer does not resolve");
                ModelChecks.require(MechanicsHashes.sha256(value.toString()).equals(field.valueSha256()),
                        "Structured evidence value differs from pinned source");
            }
        }
    }

    private static void requireTrustedOwner(List<MechanicsSourceOwnerPointer> trustedOwners,
                                            String resourcePath, String ownerPointer,
                                            MechanicsOwnerReference authoredOwner) {
        ModelChecks.require(trustedOwners.stream().anyMatch(trusted ->
                        trusted.owner().equals(authoredOwner)
                                && trusted.revision().resourcePath().equals(resourcePath)
                                && trusted.jsonPointer().equals(ownerPointer)),
                "Evidence owner pointer is not the pinned root for its authored owner");
    }

    private static void verifyAuthoredOwner(JsonNode tree, String collection, String ownerPointer,
                                            String sourceCode, int page, MechanicsOwnerReference authoredOwner) {
        ModelChecks.required(authoredOwner, "Source-verified evidence requires an authored owner");
        ModelChecks.require(authoredOwner.source().identity().code().equals(sourceCode)
                        && authoredOwner.source().page() == page,
                "Evidence source identity differs from its authored owner");
        ModelChecks.require(collection.equals("spell") && ownerPointer.startsWith("/spell/"),
                "Spell evidence must identify its source owner under the pinned spell collection");
        JsonNode owner = at(tree, ownerPointer);
        ModelChecks.require(owner.isObject() && owner.path("source").isTextual()
                        && owner.path("source").textValue().equals(sourceCode)
                        && owner.path("page").canConvertToInt() && owner.path("page").intValue() == page,
                "Evidence owner pointer does not identify its authored source and page");
    }

    private static JsonNode sourceTree(MechanicsDocument document, Map<String, JsonNode> trees,
                                       String path, String sha256) {
        SourceArtifactRevision revision = document.sources().stream()
                .filter(candidate -> candidate.resourcePath().equals(path)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Evidence resource is not in document revisions"));
        ModelChecks.require(revision.resourceSha256().equals(sha256),
                "Evidence resource fingerprint differs from document revision");
        return trees.get(path);
    }

    private static JsonNode at(JsonNode tree, String pointer) {
        try {
            return tree.at(JsonPointer.compile(pointer));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid source JSON pointer", exception);
        }
    }

    static void validateSpan(String value, SyntaxSpan span) {
        ModelChecks.require(span.endExclusive() <= value.length(), "Text evidence span exceeds source text");
        ModelChecks.require(!splitsSurrogatePair(value, span.startInclusive())
                        && !splitsSurrogatePair(value, span.endExclusive()),
                "Text evidence span splits a UTF-16 surrogate pair");
    }

    private static boolean splitsSurrogatePair(String value, int offset) {
        return offset > 0 && offset < value.length()
                && Character.isHighSurrogate(value.charAt(offset - 1))
                && Character.isLowSurrogate(value.charAt(offset));
    }
}
