package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.thedndpub.data.mechanics.model.MechanicsModelFixtures.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MechanicsSourceEvidenceVerifierTests {
    private final ObjectMapper mapper = MechanicsJson.mapper();

    @Test
    void verifiesPinnedTextAndStructuredEvidenceAgainstRawSource() {
        MechanicsDocument document = document(List.of(ac()));
        MechanicsSourceContent source = source(document.sources().getFirst());
        assertDoesNotThrow(() -> MechanicsSourceEvidenceVerifier.verify(
                document, List.of(source), document.sources(), ownerPointers(document), mapper));
        assertThrows(IllegalArgumentException.class, () -> MechanicsSourceEvidenceVerifier.verify(
                document, List.of(source), List.of(), ownerPointers(document), mapper));
        SourceArtifactRevision stale = new SourceArtifactRevision(document.sources().getFirst().datasetVersion(),
                document.sources().getFirst().resourcePath(), "0".repeat(64));
        assertThrows(IllegalArgumentException.class, () -> MechanicsSourceEvidenceVerifier.verify(
                document, List.of(source), List.of(stale), ownerPointers(document), mapper));
    }

    @Test
    void rejectsOutOfBoundsTextSpanAndUnresolvableJsonPointer() {
        TextMechanicsEvidence body = shieldBody();
        TextMechanicsEvidence invalidSpan = new TextMechanicsEvidence("bad-span", body.source(),
                new com.thedndpub.data.mechanics.syntax.SyntaxEvidence(body.syntax().location(),
                        new SyntaxSpan(0, Integer.MAX_VALUE)), body.textSha256(), body.origin(), body.authoredOwner());
        MechanicsDocument invalidText = document(PHB,
                List.of(proposal(PHB, "bad-span-claim", ac().payload(), ac().context(), complete(), invalidSpan)),
                List.of(invalidSpan), List.of(), List.of());
        assertThrows(IllegalArgumentException.class, () -> MechanicsSourceEvidenceVerifier.verify(
                invalidText, List.of(source(invalidText.sources().getFirst())), invalidText.sources(),
                ownerPointers(invalidText), mapper));

        var owner = body.syntax().location().owner();
        StructuredFieldMechanicsEvidence missing = new StructuredFieldMechanicsEvidence("missing-field", body.source(),
                owner, "/spell/294/notARealField", List.of(), HASH, MechanicsOrigin.AUTHORED, PHB);
        MechanicsDocument invalidPointer = document(PHB,
                List.of(proposal(PHB, "bad-pointer-claim", ac().payload(), ac().context(), complete(), missing)),
                List.of(missing), List.of(), List.of());
        assertThrows(IllegalArgumentException.class, () -> MechanicsSourceEvidenceVerifier.verify(
                invalidPointer, List.of(source(invalidPointer.sources().getFirst())), invalidPointer.sources(),
                ownerPointers(invalidPointer), mapper));
    }

    @Test
    void rejectsMismatchedSourceFingerprintAndUtf16SurrogateBoundaries() {
        MechanicsDocument document = document(List.of(ac()));
        SourceArtifactRevision revision = document.sources().getFirst();
        assertThrows(IllegalArgumentException.class, () -> new MechanicsSourceContent(revision,
                "different pinned content".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        assertThrows(IllegalArgumentException.class, () -> MechanicsSourceEvidenceVerifier.validateSpan(
                "A\uD83D\uDE00B", new SyntaxSpan(2, 3)));
        assertThrows(IllegalArgumentException.class, () -> MechanicsSourceEvidenceVerifier.validateSpan(
                "short", new SyntaxSpan(0, 6)));
        assertDoesNotThrow(() -> MechanicsSourceEvidenceVerifier.validateSpan(
                "A\uD83D\uDE00B", new SyntaxSpan(1, 3)));
    }

    private static MechanicsSourceContent source(SourceArtifactRevision revision) {
        try (var stream = new org.springframework.core.io.ClassPathResource(revision.resourcePath()).getInputStream()) {
            return new MechanicsSourceContent(revision, stream.readAllBytes());
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static List<MechanicsSourceOwnerPointer> ownerPointers(MechanicsDocument document) {
        MechanicsEvidence evidence = document.evidence().getFirst();
        String pointer;
        MechanicsOwnerReference authoredOwner;
        if (evidence instanceof TextMechanicsEvidence text) {
            pointer = text.syntax().location().owner().jsonPointer();
            authoredOwner = text.authoredOwner();
        } else {
            StructuredFieldMechanicsEvidence field = (StructuredFieldMechanicsEvidence) evidence;
            pointer = field.owner().jsonPointer();
            authoredOwner = field.authoredOwner();
        }
        return List.of(new MechanicsSourceOwnerPointer(authoredOwner, document.sources().getFirst(), pointer));
    }
}
