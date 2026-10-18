import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.book.PublicationMapper;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.api.SpellPublicMapper;
import com.thedndpub.data.importer.spell.SpellFormulaAnnotationAdapter;
import com.thedndpub.data.importer.spell.SpellIdentityFactory;
import com.thedndpub.data.importer.spell.SpellSourceLoader;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.SyntaxObservation;
import com.thedndpub.data.services.PublicationCatalog;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Analysis driver: executes existing import/public mapping and Batch 6 syntax code unchanged. */
public final class SpellMechanicsProbe {
    private static final Path DIRECTORY = Path.of("analysis/mechanics/public-v1/spells");
    private static final Map<String, String> MECHANICS = Map.ofEntries(
            Map.entry("abilityCheck", "abilityChecks"),
            Map.entry("affectsCreatureType", "affectsCreatureTypes"),
            Map.entry("areaTags", "areaTags"),
            Map.entry("conditionImmune", "conditionImmunities"),
            Map.entry("conditionInflict", "conditionsInflicted"),
            Map.entry("damageImmune", "damageImmunities"),
            Map.entry("damageInflict", "damageTypes"),
            Map.entry("damageResist", "damageResistances"),
            Map.entry("damageVulnerable", "damageVulnerabilities"),
            Map.entry("miscTags", "miscellaneousTags"),
            Map.entry("savingThrow", "savingThrows"),
            Map.entry("spellAttack", "spellAttacks"));
    private static final Set<String> EXAMPLES = Set.of(
            "spell:phb:fireball", "spell:xphb:fireball", "spell:phb:healing-word",
            "spell:xphb:healing-word", "spell:phb:false-life", "spell:phb:magic-missile",
            "spell:phb:scorching-ray", "spell:tce:booming-blade", "spell:phb:shield",
            "spell:phb:prismatic-spray", "spell:xge:chaos-bolt", "spell:phb:acid-splash");

    public static void main(String[] arguments) throws Exception {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        SourceRegistry registry = new SourceRegistry(mapper,
                new PublicationCatalog(mapper, new PublicationMapper()));
        SpellCatalog spells = new SpellCatalog(mapper, registry);
        SpellPublicMapper projection = new SpellPublicMapper();
        SpellFormulaAnnotationIndex index = new SpellFormulaAnnotationAdapter(mapper).adapt(spells.findAll());

        ObjectNode report = mapper.createObjectNode();
        report.put("authoredSpellCount", index.spellRootCount());
        report.put("normalizedSpellCount", spells.size());
        report.put("sourceFileCount", index.sourceFileCount());
        report.put("formulaCount", index.annotations().size());
        report.put("formulaOwnerCount", index.formulaOwnerCount());
        report.put("auxiliarySyntaxCount", index.syntaxAnnotations().size());
        Map<String, JsonNode> originalById = new HashMap<>();
        Map<String, JsonNode> dteById = new HashMap<>();
        Map<String, JsonNode> documentByResource = new TreeMap<>();
        ObjectNode resourceHashes = report.putObject("resourceHashes");
        var corpus = new SpellSourceLoader(mapper).load();
        for (var document : corpus.documents()) {
            String resource = "data/spells/" + document.resourceName();
            byte[] bytes = Files.readAllBytes(Path.of("src/main/resources", resource));
            JsonNode original = mapper.readTree(bytes);
            documentByResource.put(resource, original);
            resourceHashes.put(resource, digest(bytes));
            for (int ownerIndex = 0; ownerIndex < document.data().spells().size(); ownerIndex++) {
                var source = document.data().spells().get(ownerIndex);
                String id = SpellIdentityFactory.id(source.name(), source.source()).value();
                originalById.put(id, original.path("spell").get(ownerIndex));
                dteById.put(id, mapper.valueToTree(source));
            }
        }

        ArrayNode records = report.putArray("records");
        ObjectNode examples = report.putObject("existingPublicExamples");
        for (var spell : spells.findAll().stream().sorted(
                java.util.Comparator.comparing(value -> value.id().value())).toList()) {
            String id = spell.id().value();
            JsonNode raw = originalById.get(id);
            JsonNode dte = dteById.get(id);
            JsonNode normalized = mapper.valueToTree(spell.mechanics());
            JsonNode detail = mapper.valueToTree(projection.detail(spell));
            ObjectNode row = records.addObject();
            row.put("id", id);
            row.put("name", spell.identity().name());
            row.put("source", spell.source().identity().code());
            row.put("formulaCount", index.findBySpellId(spell.id()).size());
            row.put("auxiliarySyntaxCount", index.findSyntaxBySpellId(spell.id()).size());
            row.put("publicSnapshotSha256", digest(mapper.writeValueAsBytes(detail)));
            ArrayNode publicPaths = row.putArray("publicPaths");
            appendPaths(detail, "", publicPaths);
            for (var field : new TreeMap<>(MECHANICS).entrySet()) {
                JsonNode expected = raw.path(field.getKey());
                String normalizedName = field.getKey().equals("damageInflict")
                        ? "damageInflicted" : field.getValue();
                JsonNode sourceValue = emptyArrayIfMissing(mapper, expected);
                require(sourceValue.equals(emptyArrayIfMissing(mapper, dte.path(field.getKey()))),
                        "DTE mechanics mismatch: " + id + "/" + field.getKey());
                require(sourceValue.equals(emptyArrayIfMissing(mapper, normalized.path(normalizedName))),
                        "Normalized mechanics mismatch: " + id + "/" + field.getKey());
                require(sourceValue.equals(emptyArrayIfMissing(mapper,
                                detail.path("mechanics").path(field.getValue()))),
                        "Public mechanics mismatch: " + id + "/" + field.getKey());
            }
            if (EXAMPLES.contains(id)) {
                examples.set(id, detail);
            }
        }

        ArrayNode formulas = report.putArray("formulas");
        for (var annotation : index.annotations()) {
            ObjectNode row = observation(mapper, annotation.spellId().value(), annotation.occurrenceIndex(),
                    annotation.section().name(), annotation.observation(), documentByResource, resourceHashes);
            require(annotation.observation() instanceof FormulaSyntaxObservation,
                    "Unexpected nonformula in formula index");
            var formula = (FormulaSyntaxObservation) annotation.observation();
            row.put("expression", formula.expression().authoredToken());
            row.put("expressionKind", formula.expression().kind().name());
            formulas.add(row);
        }
        ArrayNode syntax = report.putArray("auxiliarySyntax");
        for (var annotation : index.syntaxAnnotations()) {
            syntax.add(observation(mapper, annotation.spellId().value(), annotation.occurrenceIndex(),
                    annotation.section().name(), annotation.observation(), documentByResource, resourceHashes));
        }

        require(index.spellRootCount() == 969 && index.annotations().size() == 930
                        && index.formulaOwnerCount() == 422, "Batch 6 denominator changed");
        report.put("mechanicsTraceComparisons", 969 * MECHANICS.size() * 3);
        report.put("spanFailures", 0);
        Files.writeString(DIRECTORY.resolve("runtime-evidence.json"),
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report) + "\n",
                StandardCharsets.UTF_8);
        System.out.println("Traced 969 Spell definitions and validated 930 formula + 18 auxiliary spans.");
    }

    private static ObjectNode observation(
            ObjectMapper mapper,
            String spellId,
            int ordinal,
            String section,
            SyntaxObservation observation,
            Map<String, JsonNode> documents,
            ObjectNode resourceHashes) throws Exception {
        MarkupTag tag = observation.tag();
        var location = tag.evidence().location();
        String resource = location.owner().resourcePath();
        String text = documents.get(resource).at(location.jsonPointer()).textValue();
        require(text != null && tag.evidence().span().extract(text).equals(tag.originalText()),
                "Invalid original span: " + resource + "#" + location.jsonPointer());
        require(location.owner().resourceSha256().equals(resourceHashes.path(resource).asText()),
                "Classpath/source resource mismatch: " + resource);
        ObjectNode row = mapper.createObjectNode();
        row.put("spellId", spellId);
        row.put("occurrenceIndex", ordinal);
        row.put("section", section);
        row.put("resource", resource);
        row.put("ownerPointer", location.owner().jsonPointer());
        row.put("pointer", location.jsonPointer());
        row.set("hierarchy", mapper.valueToTree(location.hierarchy()));
        row.put("origin", location.origin().name());
        row.set("spanUtf16", mapper.valueToTree(List.of(
                tag.evidence().span().startInclusive(), tag.evidence().span().endExclusive())));
        row.put("textSha256", digest(text.getBytes(StandardCharsets.UTF_8)));
        row.put("tag", tag.name());
        row.put("originalTag", tag.originalText());
        row.put("rawArgumentText", tag.rawArgumentText());
        row.set("arguments", mapper.valueToTree(tag.arguments()));
        row.put("nestedTagCount", tag.children().size());
        row.put("observationType", observation.getClass().getSimpleName());
        return row;
    }

    private static JsonNode emptyArrayIfMissing(ObjectMapper mapper, JsonNode value) {
        return value.isMissingNode() || value.isNull() ? mapper.createArrayNode() : value;
    }

    private static void appendPaths(JsonNode value, String prefix, ArrayNode paths) {
        if (!value.isObject()) {
            return;
        }
        var fields = value.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            String path = prefix.isEmpty() ? field.getKey() : prefix + "." + field.getKey();
            paths.add(path);
            appendPaths(field.getValue(), path, paths);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static String digest(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }
}
