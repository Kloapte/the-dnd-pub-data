package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsOrigin;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.SyntaxContext;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.mechanics.syntax.SyntaxText;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Per-extraction evidence builder over verified original bytes, never a source DTO or shared mutable catalog. */
public final class SpellMechanicsEvidenceSource {
    private static final java.util.Set<String> SHIELD_EVIDENCE_OWNERS = java.util.Set.of(
            "spell:phb:shield", "spell:xphb:shield");
    private static final java.util.Set<String> LEGACY_SHIELD_ALIAS_OWNERS = java.util.Set.of(
            "spell:phb:fireball", "spell:phb:healing-word");
    private final SpellMechanicsSourceOwner source;
    private final MechanicsOwnerReference owner;
    private final JsonNode tree;
    private final String evidenceIdNamespace;
    private final String legacyEvidenceIdNamespace;
    private final Map<String, MechanicsEvidence> evidence = new LinkedHashMap<>();

    public SpellMechanicsEvidenceSource(
            SpellDefinition definition,
            SpellMechanicsSourceOwner source,
            byte[] originalBytes,
            ObjectMapper mapper) {
        this.source = ModelChecks.required(source, "source");
        evidenceIdNamespace = SHIELD_EVIDENCE_OWNERS.contains(source.id().value()) ? "shield" : "spell";
        legacyEvidenceIdNamespace = LEGACY_SHIELD_ALIAS_OWNERS.contains(source.id().value()) ? "shield" : null;
        ModelChecks.require(definition.id().equals(source.id()) && definition.source().equals(source.source()),
                "Normalized and authored Spell owners differ");
        ModelChecks.require(MechanicsHashes.sha256(originalBytes).equals(source.revision().resourceSha256()),
                "Spell evidence resource fingerprint mismatch");
        try {
            tree = mapper.readTree(originalBytes);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot read original Spell evidence resource", exception);
        }
        ModelChecks.require(node("").isObject(), "Missing original Spell root");
        owner = new MechanicsOwnerReference(CatalogDomain.SPELL, definition.id().value(), definition.source());
        verifyText("/name", definition.identity().name());
        verifyText("/source", definition.source().identity().code());
        verifyInteger("/page", definition.source().page());
    }

    public MechanicsOwnerReference owner() {
        return owner;
    }

    public SpellMechanicsSourceOwner source() {
        return source;
    }

    public List<MechanicsEvidence> evidence() {
        return List.copyOf(evidence.values());
    }

    JsonNode node(String relativePointer) {
        ModelChecks.pointer(relativePointer);
        return tree.at(source.jsonPointer() + relativePointer);
    }

    void verifyText(String pointer, String value) {
        JsonNode original = node(pointer);
        ModelChecks.require(value == null ? absent(original) : original.isTextual() && value.equals(original.textValue()),
                "Normalized text does not agree with original Spell evidence at " + pointer);
    }

    void verifyInteger(String pointer, Integer value) {
        JsonNode original = node(pointer);
        ModelChecks.require(value == null ? absent(original)
                        : original.isIntegralNumber() && original.canConvertToInt() && value == original.intValue(),
                "Normalized number does not agree with original Spell evidence at " + pointer);
    }

    void verifyBoolean(String pointer, Boolean value) {
        JsonNode original = node(pointer);
        ModelChecks.require(value == null ? absent(original) : original.isBoolean() && value == original.booleanValue(),
                "Normalized flag does not agree with original Spell evidence at " + pointer);
    }

    void verifyListSize(String pointer, int size) {
        JsonNode original = node(pointer);
        ModelChecks.require(original.isArray() ? original.size() == size : absent(original) && size == 0,
                "Normalized collection does not agree with original Spell evidence at " + pointer);
    }

    void verifyPresence(String pointer, boolean present) {
        ModelChecks.require(present == !absent(node(pointer)), "Normalized field presence differs at " + pointer);
    }

    void verifyTextList(String pointer, List<String> values) {
        JsonNode original = node(pointer);
        if (absent(original)) {
            ModelChecks.require(values == null || values.isEmpty(), "Normalized list differs from absent source field");
            return;
        }
        ModelChecks.require(original.isArray() && values != null && original.size() == values.size(),
                "Normalized list does not match original source field");
        for (int index = 0; index < values.size(); index++) {
            ModelChecks.require(original.get(index).isTextual() && values.get(index).equals(original.get(index).textValue()),
                    "Normalized list value/order does not match original source field");
        }
    }

    SyntaxText text(String pointer) {
        JsonNode original = node(pointer);
        if (!original.isTextual()) {
            throw new SpellPatternMismatch(UnsupportedReason.PROVENANCE_UNAVAILABLE, pointer);
        }
        return new SyntaxText(location(pointer), original.textValue());
    }

    TextMechanicsEvidence textEvidence(String pointer, String phrase) {
        String text = text(pointer).text();
        int start = phrase == null ? 0 : text.indexOf(phrase);
        if (start < 0 || phrase != null && text.indexOf(phrase, start + 1) >= 0) {
            throw new SpellPatternMismatch(UnsupportedReason.AMBIGUOUS_INTERPRETATION, pointer);
        }
        int end = phrase == null ? text.length() : start + phrase.length();
        if (end == start) {
            throw new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer);
        }
        String identity = MechanicsHashes.sha256(source.revision().resourceSha256()
                + source.jsonPointer() + pointer + ":" + start + ":" + end).substring(0, 24);
        String id = evidenceIdNamespace + "-text:" + identity;
        var result = new TextMechanicsEvidence(id, owner.source(),
                new SyntaxEvidence(location(pointer), new SyntaxSpan(start, end)), MechanicsHashes.sha256(text),
                MechanicsOrigin.AUTHORED, owner);
        retain(result);
        if (legacyEvidenceIdNamespace != null) {
            retain(new TextMechanicsEvidence(legacyEvidenceIdNamespace + "-text:" + identity, owner.source(),
                    result.syntax(), result.textSha256(), result.origin(), result.authoredOwner()));
        }
        return result;
    }

    StructuredFieldMechanicsEvidence fieldEvidence(String pointer) {
        ModelChecks.require(!node(pointer).isMissingNode(), "Cannot fabricate evidence for a missing source field");
        String identity = MechanicsHashes.sha256(source.revision().resourceSha256()
                + source.jsonPointer() + pointer).substring(0, 24);
        String id = evidenceIdNamespace + "-field:" + identity;
        var result = new StructuredFieldMechanicsEvidence(id, owner.source(), sourceOwner(),
                source.jsonPointer() + pointer, hierarchy(pointer), MechanicsHashes.sha256(node(pointer).toString()),
                MechanicsOrigin.AUTHORED, owner);
        retain(result);
        if (legacyEvidenceIdNamespace != null) {
            retain(new StructuredFieldMechanicsEvidence(legacyEvidenceIdNamespace + "-field:" + identity,
                    result.source(), result.owner(), result.jsonPointer(), result.hierarchy(), result.valueSha256(),
                    result.origin(), result.authoredOwner()));
        }
        return result;
    }

    StructuredFieldMechanicsEvidence availableAncestor(String pointer) {
        String ancestor = pointer;
        while (node(ancestor).isMissingNode()) {
            int separator = ancestor.lastIndexOf('/');
            ancestor = separator <= 0 ? "" : ancestor.substring(0, separator);
        }
        return fieldEvidence(ancestor);
    }

    private void retain(MechanicsEvidence value) {
        MechanicsEvidence previous = evidence.putIfAbsent(value.id(), value);
        ModelChecks.require(previous == null || previous.equals(value), "Conflicting evidence identity");
    }

    private SyntaxTextLocation location(String pointer) {
        return new SyntaxTextLocation(sourceOwner(), source.jsonPointer() + pointer, hierarchy(pointer), SyntaxOrigin.AUTHORED);
    }

    private SyntaxSourceOwner sourceOwner() {
        return new SyntaxSourceOwner(source.revision().resourcePath(), source.revision().resourceSha256(),
                "spell", source.jsonPointer());
    }

    private List<SyntaxContext> hierarchy(String pointer) {
        List<SyntaxContext> result = new ArrayList<>();
        String full = source.jsonPointer() + pointer;
        String current = source.jsonPointer();
        while (full.startsWith(current + "/")) {
            JsonNode container = tree.at(current);
            String authoredType = container.isObject() ? container.path("type").textValue() : null;
            result.add(new SyntaxContext(current, authoredType));
            int next = full.indexOf('/', current.length() + 1);
            if (next < 0) {
                break;
            }
            current = full.substring(0, next);
        }
        return List.copyOf(result);
    }

    private static boolean absent(JsonNode value) {
        return value.isMissingNode() || value.isNull();
    }
}
