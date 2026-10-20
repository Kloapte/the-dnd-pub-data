package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.MechanicsOrigin;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.spell.SpellMechanicsSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** Explicit, offline fixture loading. Not a Spring component and never injected into inspection catalogs. */
public final class BenchmarkFixtures {
    private static final JsonMapper HASH_MAPPER = JsonMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();

    private BenchmarkFixtures() {
    }

    public static String fingerprint(FixtureContent content) {
        try {
            return MechanicsHashes.sha256(HASH_MAPPER.writeValueAsBytes(content));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot fingerprint fixture content", exception);
        }
    }

    public static SpellBenchmarkFixture parse(byte[] bytes, BenchmarkReviewPolicy policy) throws IOException {
        var fixture = MechanicsJson.mapper().readValue(bytes, SpellBenchmarkFixture.class);
        policy.validate(fixture);
        return fixture;
    }

    public static List<SpellBenchmarkFixture> load(
            List<Resource> resources,
            List<SpellMechanicsSourceOwner> owners,
            SpellFormulaAnnotationIndex annotations,
            BenchmarkReviewPolicy policy) throws IOException {
        var result = new ArrayList<SpellBenchmarkFixture>();
        var identities = new HashSet<String>();
        var ownerIds = new HashSet<String>();
        Map<String, JsonNode> sourceTrees = new HashMap<>();
        for (Resource resource : resources) {
            SpellBenchmarkFixture fixture = parse(resource.getContentAsByteArray(), policy);
            ModelChecks.require(identities.add(fixture.id()), "Duplicate fixture identity");
            ModelChecks.require(ownerIds.add(fixture.owner().id()), "Duplicate fixture Spell owner");
            validateSource(fixture, owners, annotations, sourceTrees);
            result.add(fixture);
        }
        result.sort(Comparator.comparing(value -> value.owner().id()));
        return List.copyOf(result);
    }

    private static void validateSource(
            SpellBenchmarkFixture fixture,
            List<SpellMechanicsSourceOwner> owners,
            SpellFormulaAnnotationIndex annotations,
            Map<String, JsonNode> trees) throws IOException {
        var owner = owners.stream().filter(value -> value.id().value().equals(fixture.owner().id())).toList();
        ModelChecks.require(owner.size() == 1, "Fixture requires exactly one authoritative authored Spell owner");
        ModelChecks.require(owner.getFirst().source().equals(fixture.owner().source())
                        && owner.getFirst().revision().equals(fixture.source())
                        && owner.getFirst().jsonPointer().equals(fixture.ownerPointer()),
                "Fixture owner/source revision or original root pointer mismatch");
        String resource = fixture.source().resourcePath();
        if (!trees.containsKey(resource)) {
            byte[] bytes = new ClassPathResource(resource).getContentAsByteArray();
            ModelChecks.require(MechanicsHashes.sha256(bytes).equals(fixture.source().resourceSha256()),
                    "Pinned fixture resource fingerprint changed");
            trees.put(resource, MechanicsJson.mapper().readTree(bytes));
        }
        JsonNode tree = trees.get(resource);
        for (ExpectedMechanic claim : fixture.mechanics()) {
            for (ExpectedEvidence evidence : claim.evidence()) {
                validateEvidence(fixture, evidence.evidence(), tree);
            }
        }
        for (DeferredExpectation deferred : fixture.deferred()) {
            for (MechanicsEvidence evidence : deferred.evidence()) {
                validateEvidence(fixture, evidence, tree);
            }
        }
        for (ExpectedFormula formula : fixture.formulas()) {
            var reference = formula.reference();
            ModelChecks.require(reference.spellId().equals(owner.getFirst().id())
                            && reference.source().equals(fixture.source()), "Formula owner/source mismatch");
            var authored = annotations.bySpellId().get(reference.spellId());
            ModelChecks.require(authored != null && reference.occurrenceIndex() < authored.size(),
                    "Unknown authoritative formula observation");
            var tag = authored.get(reference.occurrenceIndex()).observation().tag();
            ModelChecks.require(tag.evidence().equals(reference.evidence())
                            && tag.originalText().equals(formula.exactToken()), "Formula evidence/token mismatch");
            String text = tree.at(reference.evidence().location().jsonPointer()).textValue();
            ModelChecks.require(text != null && reference.evidence().span().extract(text).equals(formula.exactToken()),
                    "Formula UTF-16 span mismatch");
        }
    }

    private static void validateEvidence(SpellBenchmarkFixture fixture, MechanicsEvidence evidence, JsonNode tree) {
        ModelChecks.require(evidence.source().equals(fixture.owner().source()), "Foreign edition evidence");
        switch (evidence) {
            case TextMechanicsEvidence text -> {
                validateLocation(fixture, text.syntax().location());
                ModelChecks.require(text.origin() == MechanicsOrigin.AUTHORED
                                && fixture.owner().equals(text.authoredOwner()), "Expected authored evidence owner");
                JsonNode value = tree.at(text.syntax().location().jsonPointer());
                ModelChecks.require(value.isTextual(), "Expected a source string at text evidence pointer");
                ModelChecks.require(MechanicsHashes.sha256(value.textValue()).equals(text.textSha256()),
                        "Source string fingerprint mismatch");
                var span = text.syntax().span();
                ModelChecks.require(span.endExclusive() <= value.textValue().length()
                                && span.startInclusive() < span.endExclusive(), "Invalid original UTF-16 span");
                validateUtf16Boundary(value.textValue(), span.startInclusive());
                validateUtf16Boundary(value.textValue(), span.endExclusive());
            }
            case StructuredFieldMechanicsEvidence field -> {
                validateLocation(fixture, new SyntaxTextLocation(field.owner(), field.jsonPointer(),
                        field.hierarchy(), SyntaxOrigin.AUTHORED));
                ModelChecks.require(field.origin() == MechanicsOrigin.AUTHORED
                                && fixture.owner().equals(field.authoredOwner()), "Expected authored field owner");
                JsonNode value = tree.at(field.jsonPointer());
                ModelChecks.require(!value.isMissingNode(), "Missing structured source field");
                ModelChecks.require(MechanicsHashes.sha256(value.toString()).equals(field.valueSha256()),
                        "Structured field fingerprint mismatch");
            }
            default -> throw new IllegalArgumentException("v1 fixtures require original authored evidence");
        }
    }

    private static void validateLocation(SpellBenchmarkFixture fixture, SyntaxTextLocation location) {
        SyntaxSourceOwner owner = location.owner();
        ModelChecks.require(owner.resourcePath().equals(fixture.source().resourcePath())
                        && owner.resourceSha256().equals(fixture.source().resourceSha256())
                        && owner.collection().equals("spell") && owner.jsonPointer().equals(fixture.ownerPointer()),
                "Fixture evidence source owner mismatch");
    }

    private static void validateUtf16Boundary(String text, int offset) {
        ModelChecks.require(offset == 0 || offset == text.length()
                        || !Character.isHighSurrogate(text.charAt(offset - 1))
                        || !Character.isLowSurrogate(text.charAt(offset)),
                "Evidence boundary splits a UTF-16 surrogate pair");
    }
}
