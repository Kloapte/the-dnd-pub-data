package com.thedndpub.data.mechanics.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyntaxCorpusTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final BalancedMarkupParser parser = new BalancedMarkupParser();
    private final SyntaxObservationClassifier classifier = new SyntaxObservationClassifier();
    private final AuthoredSyntaxTextWalker walker = new AuthoredSyntaxTextWalker();

    @Test
    void allPinnedAuthoredAndSupportTagsHaveExactSpansAndMatchTheIndependentAssessment() throws Exception {
        JsonNode assessment = mapper.readTree(Path.of("analysis/mechanics/corpus-evidence.json").toFile());
        Set<String> definitionRoots = new HashSet<>();
        Map<String, Map<String, Long>> expectedVariants = new TreeMap<>();
        var groups = assessment.path("catalogs").fields();
        while (groups.hasNext()) {
            var group = groups.next();
            if ("definition-source".equals(group.getValue().path("role").asText())) {
                definitionRoots.add(group.getValue().path("root").asText());
            }
            var tags = group.getValue().path("tagVariants").fields();
            while (tags.hasNext()) {
                var tag = tags.next();
                var variants = tag.getValue().fields();
                while (variants.hasNext()) {
                    var variant = variants.next();
                    expectedVariants.computeIfAbsent(tag.getKey(), ignored -> new TreeMap<>())
                            .merge(variant.getKey(), variant.getValue().asLong(), Long::sum);
                }
            }
        }
        Map<String, Long> allTags = new TreeMap<>();
        Map<String, Long> definitionTags = new TreeMap<>();
        Map<String, Map<String, Long>> variants = new TreeMap<>();
        Map<String, Long> scopes = new TreeMap<>();
        Map<String, Long> observationKinds = new TreeMap<>();
        Map<String, Long> supportedTags = new TreeMap<>();
        Map<String, Long> expressionKinds = new TreeMap<>();
        Set<String> taggedFiles = new HashSet<>();
        Set<String> supportedFiles = new HashSet<>();
        Set<String> taggedOwners = new HashSet<>();
        Set<String> supportedOwners = new HashSet<>();
        long[] ownerCounts = new long[3];
        long[] stringCount = new long[1];
        MessageDigest spanFingerprint = MessageDigest.getInstance("SHA-256");

        for (JsonNode manifest : assessment.path("files")) {
            String file = manifest.path("file").asText();
            String resource = file.substring("src/main/resources/".length());
            byte[] bytes;
            try (var input = getClass().getClassLoader().getResourceAsStream(resource)) {
                assertNotNull(input, resource);
                bytes = input.readAllBytes();
            }
            String sha256 = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            assertEquals(manifest.path("sha256").asText(), sha256, file);
            JsonNode document = mapper.readTree(bytes);
            var collections = document.fields();
            while (collections.hasNext()) {
                var collection = collections.next();
                boolean definition = definitionRoots.contains(collection.getKey());
                boolean excluded = collection.getKey().equals("crochetPattern");
                JsonNode records = collection.getValue();
                int count = records.isArray() ? records.size() : 1;
                ownerCounts[excluded ? 2 : definition ? 0 : 1] += count;
                if (excluded) {
                    continue;
                }
                for (int index = 0; index < count; index++) {
                    String pointer = SyntaxPointers.append("", collection.getKey());
                    if (records.isArray()) {
                        pointer = SyntaxPointers.append(pointer, Integer.toString(index));
                    }
                    SyntaxSourceOwner owner = new SyntaxSourceOwner(resource, sha256, collection.getKey(), pointer);
                    JsonNode record = records.isArray() ? records.get(index) : records;
                    walker.walk(record, owner, text -> {
                        stringCount[0]++;
                        assertEquals(text.text(), document.at(text.location().jsonPointer()).textValue());
                        MarkupParseResult result = parser.parse(text);
                        Set<Integer> topLevel = result.tags().stream()
                                .map(tag -> tag.evidence().span().startInclusive()).collect(Collectors.toSet());
                        for (SyntaxObservation observation : classifier.classify(result)) {
                            MarkupTag tag = observation.tag();
                            assertEquals(tag.originalText(), tag.evidence().span().extract(text.text()));
                            assertEquals(text.location(), tag.evidence().location());
                            String name = tag.name().toLowerCase(Locale.ROOT);
                            allTags.merge(name, 1L, Long::sum);
                            if (definition) {
                                definitionTags.merge(name, 1L, Long::sum);
                            }
                            String arity = tag.arguments().size() + "args";
                            if (!topLevel.contains(tag.evidence().span().startInclusive())) {
                                arity += ":nested";
                            }
                            variants.computeIfAbsent(name, ignored -> new TreeMap<>()).merge(arity, 1L, Long::sum);
                            String scope = (definition ? "definition" : "support") + ":" + text.location().origin();
                            scopes.merge(scope, 1L, Long::sum);
                            observationKinds.merge(observation.getClass().getSimpleName(), 1L, Long::sum);
                            String ownerKey = resource + "#" + owner.jsonPointer();
                            taggedFiles.add(resource);
                            taggedOwners.add(ownerKey);
                            if (!(observation instanceof OpaqueSyntaxObservation)) {
                                supportedTags.merge(name, 1L, Long::sum);
                                supportedFiles.add(resource);
                                supportedOwners.add(ownerKey);
                            }
                            if (observation instanceof FormulaSyntaxObservation formula) {
                                expressionKinds.merge(formula.expression().kind().name(), 1L, Long::sum);
                            } else if (observation instanceof ModifierSyntaxObservation modifier) {
                                expressionKinds.merge(modifier.expression().kind().name(), 1L, Long::sum);
                            }
                            String evidence = ownerKey + "#" + text.location().jsonPointer() + "#"
                                    + tag.evidence().span() + "#" + tag.originalText() + "\n";
                            spanFingerprint.update(evidence.getBytes(StandardCharsets.UTF_8));
                        }
                    });
                }
            }
        }
        assertEquals(List.of(14_399L, 812L, 20L), List.of(ownerCounts[0], ownerCounts[1], ownerCounts[2]));
        assertEquals(175, assessment.path("files").size());
        assertEquals(73, allTags.size());
        assertEquals(91_994L, allTags.values().stream().mapToLong(Long::longValue).sum());
        assertEquals(90_294L, definitionTags.values().stream().mapToLong(Long::longValue).sum());
        assertEquals(expectedVariants, variants);
        assertEquals(counts(assessment.path("summary").path("tags")), allTags);
        assertEquals(counts(assessment.path("summary").path("definitionTags")), definitionTags);
        assertEquals(21, supportedTags.size());
        assertTrue(stringCount[0] > 0);

        ObjectNode report = mapper.createObjectNode();
        report.put("filesScanned", 175);
        report.put("authoredDefinitionOwners", ownerCounts[0]);
        report.put("supportUnits", ownerCounts[1]);
        report.put("excludedHomecraftOwners", ownerCounts[2]);
        report.put("effectiveDefinitionsMaterialized", 0);
        report.put("textLeavesScanned", stringCount[0]);
        report.put("tagOccurrences", 91_994);
        report.put("definitionRootOccurrences", 90_294);
        report.put("tagFamilies", 73);
        report.put("supportedOccurrences", supportedTags.values().stream().mapToLong(Long::longValue).sum());
        report.put("opaqueOccurrences", observationKinds.getOrDefault("OpaqueSyntaxObservation", 0L));
        report.put("malformedOccurrences", 0);
        report.put("spanReconstructionFailures", 0);
        report.put("ownersWithTags", taggedOwners.size());
        report.put("ownersWithSupportedTags", supportedOwners.size());
        report.put("filesWithTags", taggedFiles.size());
        report.put("filesWithSupportedTags", supportedFiles.size());
        report.put("spanFingerprint", HexFormat.of().formatHex(spanFingerprint.digest()));
        report.set("scopes", mapper.valueToTree(scopes));
        report.set("observationKinds", mapper.valueToTree(observationKinds));
        report.set("supportedTags", mapper.valueToTree(supportedTags));
        report.set("expressionKinds", mapper.valueToTree(expressionKinds));
        Path output = Path.of("target/batch-5-syntax-corpus.json");
        Files.createDirectories(output.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), report);
    }

    private Map<String, Long> counts(JsonNode value) {
        Map<String, Long> result = new TreeMap<>();
        value.fields().forEachRemaining(field -> result.put(field.getKey(), field.getValue().asLong()));
        return result;
    }
}
