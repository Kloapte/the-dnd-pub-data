import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.DndPubDataApplication;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.services.ClassDomainSnapshot;
import com.thedndpub.data.services.ClassesService;
import org.springframework.boot.SpringApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Records comparable public, normalized and resolver fingerprints from a packaged application. */
public final class ClassChoiceRuntimeProbe {
    public static void main(String[] arguments) throws Exception {
        try (var context = SpringApplication.run(DndPubDataApplication.class,
                "--server.port=0", "--server.address=127.0.0.1", "--spring.main.banner-mode=off")) {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            var domain = context.getBean(ClassDomainSnapshot.class).result();
            var service = context.getBean(ClassesService.class);
            var references = context.getBean(CrossCatalogResolutionCoordinator.class).snapshot().results();
            Map<String, String> sourceLocators = new TreeMap<>();
            domain.classFeatures().forEach(value -> sourceLocators.put(value.id().value(),
                    value.provenance().authored().location().pointer()));
            domain.subclassFeatures().forEach(value -> sourceLocators.put(value.id().value(),
                    value.provenance().authored().location().pointer()));
            List<Object> publicValues = new ArrayList<>();
            domain.classes().forEach(value -> publicValues.add(service.classById(value.id().value())));
            domain.subclasses().forEach(value -> publicValues.add(service.subclassById(value.id().value())));
            domain.classFeatures().forEach(value -> publicValues.add(service.classFeatureById(value.id().value())));
            domain.subclassFeatures().forEach(value -> publicValues.add(service.subclassFeatureById(value.id().value())));

            ArrayNode publicJson = mapper.valueToTree(publicValues);
            Map<String, Integer> statuses = new TreeMap<>();
            List<Map<String, String>> reconciliation = new ArrayList<>();
            for (JsonNode value : publicJson) {
                if (!value.path("id").asText().contains("-feature:")) {
                    continue;
                }
                String status = value.path("choiceProjectionStatus").asText("NOT_PRESENT");
                statuses.merge(status, 1, Integer::sum);
                if (!status.equals("NONE") && !status.equals("NOT_PRESENT")) {
                    reconciliation.add(Map.of(
                            "id", value.path("id").asText(),
                            "name", value.path("name").asText(),
                            "sourceLocator", sourceLocators.get(value.path("id").asText()),
                            "choiceProjectionStatus", status));
                }
                ((ObjectNode) value).remove("choiceProjectionStatus");
            }

            List<String> identities = new ArrayList<>();
            domain.classes().forEach(value -> identities.add(value.id().value()));
            domain.subclasses().forEach(value -> identities.add(value.id().value()));
            domain.classFeatures().forEach(value -> identities.add(value.id().value()));
            domain.subclassFeatures().forEach(value -> identities.add(value.id().value()));
            identities.sort(String::compareTo);

            Map<String, Object> report = new TreeMap<>();
            report.put("classDomainDefinitions", identities.size());
            report.put("classDomainIdentitiesSha256", hash(mapper, mapper.valueToTree(identities)));
            report.put("normalizedClassDomainSha256", hash(mapper, mapper.valueToTree(domain)));
            report.put("legacyPublicClassDomainSha256", hash(mapper, publicJson));
            report.put("resolverOccurrences", references.size());
            report.put("resolverSha256", hash(mapper, mapper.valueToTree(references)));
            report.put("choiceProjectionStatuses", statuses);
            report.put("choiceReconciliation", reconciliation);
            report.put("classResourceUrl", ClassChoiceRuntimeProbe.class.getClassLoader()
                    .getResource("data/classes/class-wizard.json").toString());
            Files.writeString(Path.of(arguments[0]), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));
        }
    }

    private static String hash(ObjectMapper mapper, JsonNode value) throws Exception {
        byte[] bytes = mapper.writeValueAsBytes(canonical(mapper, value));
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static JsonNode canonical(ObjectMapper mapper, JsonNode value) {
        if (value.isObject()) {
            ObjectNode result = mapper.createObjectNode();
            List<String> fields = new ArrayList<>();
            value.fieldNames().forEachRemaining(fields::add);
            fields.stream().sorted().filter(field -> !field.equals("provenance"))
                    .forEach(field -> result.set(field, canonical(mapper, value.get(field))));
            return result;
        }
        if (value.isArray()) {
            ArrayNode result = mapper.createArrayNode();
            value.forEach(child -> result.add(canonical(mapper, child)));
            return result;
        }
        return value;
    }
}
