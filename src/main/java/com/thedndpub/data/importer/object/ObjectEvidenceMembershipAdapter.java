package com.thedndpub.data.importer.object;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.object.ObjectDefinition;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceAnchor;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceMembership;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceMembershipIndex;
import com.thedndpub.data.dto.object.evidence.ObjectMembershipKind;
import com.thedndpub.data.dte.object.ObjectAttackDte;
import com.thedndpub.data.dte.object.ObjectActionDte;
import com.thedndpub.data.dte.object.ObjectFileDte;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxObservationClassifier;
import com.thedndpub.data.mechanics.syntax.SyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Associates explicit Object source sections with syntax; does not interpret their contents. */
public final class ObjectEvidenceMembershipAdapter {
    private static final String RESOURCE = "data/objects.json";
    private final ObjectMapper mapper;
    private final Resource resource;

    public ObjectEvidenceMembershipAdapter(ObjectMapper mapper) {
        this(mapper, new ClassPathResource(RESOURCE));
    }

    public ObjectEvidenceMembershipAdapter(ObjectMapper mapper, Resource resource) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.resource = Objects.requireNonNull(resource, "resource");
    }

    public ObjectEvidenceMembershipIndex adapt(List<ObjectDefinition> definitions) {
        try (var input = resource.getInputStream()) {
            byte[] bytes = input.readAllBytes();
            var source = mapper.readerFor(ObjectFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .<ObjectFileDte>readValue(bytes);
            JsonNode document = mapper.readTree(bytes);
            String fingerprint = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            return build(source, document, fingerprint, definitions);
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not index original Object evidence", exception);
        }
    }

    private ObjectEvidenceMembershipIndex build(
            ObjectFileDte source,
            JsonNode document,
            String fingerprint,
            List<ObjectDefinition> definitions) {
        if (source.object() == null || source.object().isEmpty()) {
            throw new IllegalStateException("Nonempty Object source array is required");
        }
        Set<String> seen = new HashSet<>();
        List<String> ids = new ArrayList<>();
        List<ObjectEvidenceMembership> result = new ArrayList<>();
        for (int ownerIndex = 0; ownerIndex < source.object().size(); ownerIndex++) {
            var authored = source.object().get(ownerIndex);
            List<ObjectDefinition> matches = definitions.stream()
                    .filter(value -> value.name().equals(authored.name())
                            && value.source().identity().code().equals(authored.source()))
                    .toList();
            if (matches.size() != 1 || !seen.add(matches.getFirst().id())) {
                throw new IllegalStateException("Object source ownership must match one catalog definition");
            }
            ObjectDefinition definition = matches.getFirst();
            List<ObjectActionDte> actions = authored.actionEntries() == null ? List.of() : authored.actionEntries();
            if (!definition.actions().equals(actions)) {
                throw new IllegalStateException("Object actions differ from original source: " + definition.id());
            }
            ids.add(definition.id());
            String ownerPointer = "/object/" + ownerIndex;
            var owner = new SyntaxSourceOwner(RESOURCE, fingerprint, "object", ownerPointer);
            Map<String, SyntaxText> texts = new LinkedHashMap<>();
            new AuthoredSyntaxTextWalker().walk(document.at(ownerPointer), owner,
                    text -> texts.put(text.location().jsonPointer(), text));
            List<ObjectEvidenceMembership> memberships = new ArrayList<>();
            for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {
                var action = actions.get(actionIndex);
                for (int entryIndex = 0; entryIndex < action.entries().size(); entryIndex++) {
                    if (action.entries().get(entryIndex) instanceof ObjectAttackDte) {
                        String pointer = ownerPointer + "/actionEntries/" + actionIndex + "/entries/" + entryIndex;
                        appendAttack(definition.id(), pointer, texts, memberships);
                    }
                }
            }
            result.addAll(memberships);
        }
        if (seen.size() != definitions.size()) {
            throw new IllegalStateException("Object definitions and authored roots must match exactly");
        }
        return new ObjectEvidenceMembershipIndex(ids, result);
    }

    private void appendAttack(
            String objectId,
            String pointer,
            Map<String, SyntaxText> texts,
            List<ObjectEvidenceMembership> result) {
        SyntaxText metadata = Objects.requireNonNull(texts.get(pointer + "/attackType"));
        ObjectEvidenceAnchor attack = node(metadata, pointer);
        ObjectEvidenceAnchor attackSection = node(metadata, pointer + "/attackEntries");
        ObjectEvidenceAnchor hitSection = node(metadata, pointer + "/hitEntries");
        add(result, objectId, ObjectMembershipKind.HAS_ATTACK_SECTION, attack, attackSection, null, null);
        add(result, objectId, ObjectMembershipKind.HAS_HIT_SECTION, attack, hitSection, null, null);
        add(result, objectId, ObjectMembershipKind.HAS_AUTHORED_ATTACK_TYPE,
                attack, node(metadata, metadata.location().jsonPointer()), metadata.text(), null);
        appendSection(objectId, attackSection, texts, result);
        appendSection(objectId, hitSection, texts, result);
    }

    private void appendSection(
            String objectId,
            ObjectEvidenceAnchor section,
            Map<String, SyntaxText> texts,
            List<ObjectEvidenceMembership> result) {
        var parser = new BalancedMarkupParser();
        var classifier = new SyntaxObservationClassifier();
        String prefix = section.location().jsonPointer() + "/";
        for (SyntaxText text : texts.values()) {
            if (!text.location().jsonPointer().startsWith(prefix)) {
                continue;
            }
            ObjectEvidenceAnchor member = node(text, text.location().jsonPointer());
            add(result, objectId, ObjectMembershipKind.SECTION_CONTAINS_ENTRY, section, member, null, null);
            for (var observation : classifier.classify(parser.parse(text))) {
                var evidence = observation.tag().evidence();
                var tag = new ObjectEvidenceAnchor(evidence.location(), evidence.span());
                if (observation instanceof FormulaSyntaxObservation) {
                    add(result, objectId, ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG,
                            section, tag, null, observation);
                } else if (observation instanceof ModifierSyntaxObservation) {
                    add(result, objectId, ObjectMembershipKind.SECTION_CONTAINS_MODIFIER_TAG,
                            section, tag, null, observation);
                }
                add(result, objectId, ObjectMembershipKind.ENTRY_CONTAINS_SYNTAX,
                        member, tag, null, observation);
            }
        }
    }

    private ObjectEvidenceAnchor node(SyntaxText template, String pointer) {
        // The shared location validates owner/ancestor pointers for structural nodes too. No text span is invented.
        var hierarchy = template.location().hierarchy().stream()
                .filter(context -> pointer.startsWith(context.jsonPointer() + "/"))
                .toList();
        var location = new SyntaxTextLocation(template.location().owner(), pointer, hierarchy, SyntaxOrigin.AUTHORED);
        return new ObjectEvidenceAnchor(location, null);
    }

    private void add(
            List<ObjectEvidenceMembership> result,
            String objectId,
            ObjectMembershipKind kind,
            ObjectEvidenceAnchor parent,
            ObjectEvidenceAnchor child,
            String authoredValue,
            SyntaxObservation observation) {
        result.add(new ObjectEvidenceMembership(
                objectId, result.size(), kind, parent, child, authoredValue, observation));
    }
}
