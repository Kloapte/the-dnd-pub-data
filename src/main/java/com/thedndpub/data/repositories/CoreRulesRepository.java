package com.thedndpub.data.repositories;

import com.thedndpub.data.dto.corerules.CanonicalCoreRulesRecord;
import com.thedndpub.data.dto.corerules.CanonicalRecordIdentity;
import com.thedndpub.data.dto.corerules.CanonicalReference;
import com.thedndpub.data.dto.corerules.CanonicalSourceMetadata;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Repository
public class CoreRulesRepository {
    private static final Map<String, String> DOMAIN_RESOURCES = Map.of(
            "action", "actions.json",
            "condition", "conditions.json",
            "disease", "diseases.json",
            "language", "languages.json",
            "rule", "rules.json",
            "sense", "senses.json",
            "skill", "skills.json",
            "status", "statuses.json");
    private static final String RESOURCE_ROOT = "normalized/core-rules/";
    private static final String SOURCE_IDENTITIES_RESOURCE = RESOURCE_ROOT + "source-identities.json";
    private static final Pattern CANONICAL_ID = Pattern.compile(
            "^([a-z][a-z0-9]*(?:-[a-z0-9]+)*):([a-z0-9]+(?:-[a-z0-9]+)*):([a-z0-9]+(?:-[a-z0-9]+)*)$");

    private final Map<String, CanonicalCoreRulesRecord> recordsById;
    private final Map<String, JsonNode> documentsById;
    private final Map<String, List<CanonicalCoreRulesRecord>> recordsByDomain;
    private final Map<String, byte[]> domainJsonByDomain;
    private final Set<String> sourceIdentities;
    private final ObjectMapper objectMapper;

    public CoreRulesRepository(ObjectMapper objectMapper, ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        Set<String> identities = loadSourceIdentities(objectMapper, applicationContext);
        CatalogIndex catalogIndex = loadCatalog(objectMapper, applicationContext, identities);
        this.recordsById = Map.copyOf(catalogIndex.recordsById());
        this.documentsById = Map.copyOf(catalogIndex.documentsById());
        this.recordsByDomain = immutableLists(catalogIndex.recordsByDomain());
        this.domainJsonByDomain = Map.copyOf(catalogIndex.domainJsonByDomain());
        this.sourceIdentities = Set.copyOf(identities);
    }

    public List<CanonicalCoreRulesRecord> findAll() {
        return recordsById.values().stream()
                .sorted((left, right) -> left.identity().id().compareTo(right.identity().id()))
                .toList();
    }

    public List<CanonicalCoreRulesRecord> findAllByDomain(String domain) {
        return recordsByDomain.getOrDefault(domain, List.of());
    }

    public Optional<byte[]> findDomainJson(String domain) {
        byte[] value = domainJsonByDomain.get(domain);
        return Optional.ofNullable(value == null ? null : value.clone());
    }

    public Optional<CanonicalCoreRulesRecord> findById(String id) {
        return Optional.ofNullable(recordsById.get(id));
    }

    public Optional<byte[]> findCanonicalJson(String id) {
        JsonNode document = documentsById.get(id);
        if (document == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.writeValueAsBytes(document));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize canonical record " + id, exception);
        }
    }

    public boolean containsSourceIdentity(String id) {
        return sourceIdentities.contains(id);
    }

    public boolean containsDomain(String domain) {
        return DOMAIN_RESOURCES.containsKey(domain);
    }

    public int size() {
        return recordsById.size();
    }

    private Set<String> loadSourceIdentities(ObjectMapper objectMapper, ApplicationContext context) {
        try (InputStream input = context.getResource("classpath:" + SOURCE_IDENTITIES_RESOURCE).getInputStream()) {
            String[] values = objectMapper.readValue(input, String[].class);
            if (values == null || values.length == 0) {
                throw new IllegalArgumentException("Source identity index must not be empty");
            }
            Set<String> identities = new HashSet<>();
            for (String id : values) {
                if (id == null || !CANONICAL_ID.matcher(id).matches() || !identities.add(id)) {
                    throw new IllegalArgumentException("Invalid or duplicate source identity: " + id);
                }
            }
            return identities;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load Core Rules source identity index", exception);
        }
    }

    private CatalogIndex loadCatalog(
            ObjectMapper objectMapper,
            ApplicationContext context,
            Set<String> sourceIdentityIndex) {
        Map<String, CanonicalCoreRulesRecord> records = new HashMap<>();
        Map<String, JsonNode> documents = new HashMap<>();
        Map<String, List<CanonicalCoreRulesRecord>> recordsByType = new HashMap<>();
        Map<String, byte[]> domainJsonByType = new HashMap<>();

        for (Map.Entry<String, String> domainResource : DOMAIN_RESOURCES.entrySet()) {
            String domain = domainResource.getKey();
            String resourcePath = RESOURCE_ROOT + domainResource.getValue();
            byte[] resourceBytes = readDomainResource(context, resourcePath);
            JsonNode domainRecords;
            try {
                domainRecords = objectMapper.readTree(resourceBytes);
            } catch (Exception exception) {
                throw new IllegalStateException("Invalid canonical JSON in " + resourcePath, exception);
            }
            if (!domainRecords.isArray() || domainRecords.isEmpty()) {
                throw invalidDataset(resourcePath, "expected a non-empty JSON array");
            }
            domainJsonByType.put(domain, resourceBytes);
            for (JsonNode document : domainRecords) {
                CanonicalCoreRulesRecord record = readRecord(
                        objectMapper, document, domain, resourcePath, sourceIdentityIndex);
                String id = record.identity().id();
                if (records.putIfAbsent(id, record) != null) {
                    throw invalidDataset(resourcePath, "duplicate canonical identity " + id);
                }
                documents.put(id, document.deepCopy());
                recordsByType.computeIfAbsent(domain, ignored -> new ArrayList<>()).add(record);
            }
        }
        if (records.size() != 585) {
            throw new IllegalStateException("Expected 585 canonical Core Rules records, loaded " + records.size());
        }
        recordsByType.values().forEach(values ->
                values.sort((left, right) -> left.identity().id().compareTo(right.identity().id())));
        return new CatalogIndex(records, documents, recordsByType, domainJsonByType);
    }

    private byte[] readDomainResource(ApplicationContext context, String path) {
        try (InputStream input = context.getResource("classpath:" + path).getInputStream()) {
            byte[] contents = input.readAllBytes();
            if (contents.length == 0) throw invalidDataset(path, "resource is empty");
            return contents;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to load canonical Core Rules resource " + path, exception);
        }
    }

    private CanonicalCoreRulesRecord readRecord(
            ObjectMapper objectMapper,
            JsonNode document,
            String expectedDomain,
            String resourcePath,
            Set<String> sourceIdentityIndex) {
        if (document == null || !document.isObject()) throw invalidDataset(resourcePath, "record must be an object");
        String id = requiredText(document, "id", resourcePath);
        String name = requiredText(document, "name", resourcePath);
        JsonNode sourceNode = document.get("sourceMetadata");
        if (sourceNode == null || !sourceNode.isObject()) {
            throw invalidDataset(resourcePath, id + " is missing sourceMetadata");
        }
        try {
            CanonicalSourceMetadata metadata = objectMapper.treeToValue(sourceNode, CanonicalSourceMetadata.class);
            validateIdentity(id, name, metadata.source(), expectedDomain, resourcePath);
            List<CanonicalReference> references = new ArrayList<>();
            collectReferences(document, references);
            for (CanonicalReference reference : references) {
                if (!sourceIdentityIndex.contains(reference.id())) {
                    throw invalidDataset(resourcePath,
                            id + " references an identity absent from pinned source catalogs: " + reference.id());
                }
            }
            return new CanonicalCoreRulesRecord(
                    new CanonicalRecordIdentity(id, name, expectedDomain), metadata, references, resourcePath);
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Invalid canonical record in " + resourcePath + ": " + id, exception);
        }
    }

    private static void collectReferences(JsonNode node, List<CanonicalReference> references) {
        if (node.isArray()) {
            node.forEach(child -> collectReferences(child, references));
            return;
        }
        if (!node.isObject()) return;
        JsonNode targetType = node.get("targetType");
        JsonNode id = node.get("id");
        if (targetType != null && targetType.isTextual() && id != null && id.isTextual()) {
            JsonNode relationship = node.get("relationship");
            JsonNode operation = node.get("operation");
            references.add(new CanonicalReference(
                    targetType.asText(),
                    id.asText(),
                    relationship == null ? null : relationship.asText(),
                    operation == null ? null : operation.asText()));
        }
        node.properties().forEach(entry -> collectReferences(entry.getValue(), references));
    }

    private static void validateIdentity(
            String id,
            String name,
            String source,
            String expectedDomain,
            String resourcePath) {
        var matcher = CANONICAL_ID.matcher(id);
        if (!matcher.matches()
                || !expectedDomain.equals(matcher.group(1))
                || !matcher.group(2).equals(canonicalSlug(source))
                || !matcher.group(3).equals(canonicalSlug(name))) {
            throw invalidDataset(resourcePath, "identity does not match domain, source, and name: " + id);
        }
    }

    private static String requiredText(JsonNode object, String property, String resourcePath) {
        JsonNode value = object.get(property);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw invalidDataset(resourcePath, "record is missing required text property " + property);
        }
        return value.asText();
    }

    private static String canonicalSlug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private static IllegalStateException invalidDataset(String resourcePath, String reason) {
        return new IllegalStateException("Invalid Core Rules dataset at " + resourcePath + ": " + reason);
    }

    private static <T> Map<String, List<T>> immutableLists(Map<String, List<T>> values) {
        Map<String, List<T>> result = new HashMap<>();
        values.forEach((domain, records) -> result.put(domain, List.copyOf(records)));
        return Collections.unmodifiableMap(result);
    }

    private record CatalogIndex(
            Map<String, CanonicalCoreRulesRecord> recordsById,
            Map<String, JsonNode> documentsById,
            Map<String, List<CanonicalCoreRulesRecord>> recordsByDomain,
            Map<String, byte[]> domainJsonByDomain) {
    }
}
