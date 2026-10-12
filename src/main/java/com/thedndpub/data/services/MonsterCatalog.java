package com.thedndpub.data.services;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.monster.MonsterDto;
import com.thedndpub.data.dto.monster.MonsterMapper;
import com.thedndpub.data.dto.monster.MonsterSummaryDto;
import com.thedndpub.data.dto.monster.NormalizedMonsterRecord;
import com.thedndpub.data.dto.monster.internal.MonsterClassification;
import com.thedndpub.data.dte.monster.MonsterCopyResolver;
import com.thedndpub.data.dte.monster.MonsterDte;
import com.thedndpub.data.dte.monster.MonsterIdentity;
import com.thedndpub.data.dte.monster.MonsterSourceLocation;
import com.thedndpub.data.dte.monster.MonsterSourceRecord;
import com.thedndpub.data.dte.monster.MonsterVersionExpander;
import com.thedndpub.data.dte.monster.ResolvedMonster;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Loads, resolves, and indexes the complete monster catalog once during application startup.
 */
@Component
public final class MonsterCatalog {
    private static final String RESOURCE_PATTERN = "classpath*:data/monsters/bestiary-*.json";

    private final Map<String, MonsterDto> monstersById;
    private final Map<String, MonsterDto> monstersByCanonicalIdentity;
    private final Map<String, MonsterClassification> classificationsById;
    private final List<MonsterSummaryDto> summaries;

    public MonsterCatalog(
            ApplicationContext applicationContext,
            ObjectMapper objectMapper,
            MonsterCopyResolver copyResolver,
            MonsterVersionExpander versionExpander,
            MonsterMapper monsterMapper) {
        Objects.requireNonNull(applicationContext, "applicationContext");
        Objects.requireNonNull(objectMapper, "objectMapper");
        Objects.requireNonNull(copyResolver, "copyResolver");
        Objects.requireNonNull(versionExpander, "versionExpander");
        Objects.requireNonNull(monsterMapper, "monsterMapper");

        List<MonsterSourceRecord> sourceRecords = loadSourceRecords(applicationContext, objectMapper);
        List<ResolvedMonster> copiedMonsters = copyResolver.resolveAll(sourceRecords);
        List<ResolvedMonster> expandedMonsters = versionExpander.expandAll(copiedMonsters);
        List<NormalizedMonsterRecord> normalizedMonsters = monsterMapper.mapRecords(expandedMonsters);

        if (normalizedMonsters.isEmpty()) {
            throw new IllegalStateException("Monster catalog initialization produced no monsters");
        }

        Map<String, MonsterDto> idIndex = new TreeMap<>();
        Map<String, MonsterDto> identityIndex = new TreeMap<>();
        Map<String, MonsterClassification> classificationIndex = new TreeMap<>();
        List<MonsterSummaryDto> summaryRecords = new ArrayList<>(normalizedMonsters.size());
        for (NormalizedMonsterRecord record : normalizedMonsters) {
            MonsterDto monster = record.monster();
            String id = monster.identity().id().value();
            String canonicalIdentity = canonicalIdentity(
                    monster.identity().name(),
                    monster.identity().source().code());

            if (idIndex.putIfAbsent(id, monster) != null) {
                throw new IllegalStateException("Duplicate monster ID in normalized catalog: " + id);
            }
            if (identityIndex.putIfAbsent(canonicalIdentity, monster) != null) {
                throw new IllegalStateException(
                        "Duplicate monster name/source identity in normalized catalog: " + canonicalIdentity);
            }
            classificationIndex.put(id, record.classification());

            summaryRecords.add(MonsterSummaryDto.from(monster));
        }

        summaryRecords.sort(Comparator
                .comparing(MonsterSummaryDto::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(MonsterSummaryDto::source, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(summary -> summary.id().value()));

        monstersById = Collections.unmodifiableMap(new TreeMap<>(idIndex));
        monstersByCanonicalIdentity = Collections.unmodifiableMap(new TreeMap<>(identityIndex));
        classificationsById = Collections.unmodifiableMap(new TreeMap<>(classificationIndex));
        summaries = List.copyOf(summaryRecords);
    }

    public Map<String, MonsterDto> monstersById() {
        return monstersById;
    }

    public List<MonsterSummaryDto> summaries() {
        return summaries;
    }

    public Map<String, MonsterClassification> classificationsById() {
        return classificationsById;
    }

    public MonsterDto findById(String id) {
        return monstersById.get(id);
    }

    public MonsterDto findByNameAndSource(String name, String source) {
        return monstersByCanonicalIdentity.get(canonicalIdentity(name, source));
    }

    public int size() {
        return monstersById.size();
    }

    private static List<MonsterSourceRecord> loadSourceRecords(
            ApplicationContext applicationContext,
            ObjectMapper objectMapper) {
        try {
            Resource[] resources = applicationContext.getResources(RESOURCE_PATTERN);
            List<Resource> sortedResources = new ArrayList<>(List.of(resources));
            sortedResources.sort(Comparator.comparing(resource -> resource.getFilename().toLowerCase(Locale.ROOT)));

            if (sortedResources.isEmpty()) {
                throw new IllegalStateException("No monster source files found for " + RESOURCE_PATTERN);
            }

            List<MonsterSourceRecord> records = new ArrayList<>();
            for (Resource resource : sortedResources) {
                try {
                    records.addAll(readMonsterFile(resource, objectMapper));
                } catch (IOException exception) {
                    throw new IllegalStateException(
                            "Could not read monster source file " + resource.getFilename(),
                            exception);
                }
            }
            return List.copyOf(records);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not discover monster source resources", exception);
        }
    }

    private static List<MonsterSourceRecord> readMonsterFile(Resource resource, ObjectMapper objectMapper)
            throws IOException {
        String filename = Objects.requireNonNull(resource.getFilename(), "Monster resource has no filename");
        List<MonsterSourceRecord> records = new ArrayList<>();
        boolean foundMonsterCollection = false;

        try (InputStream input = resource.getInputStream(); var parser = objectMapper.getFactory().createParser(input)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IllegalStateException(filename + " must contain a JSON object at its root");
            }

            JsonToken rootToken;
            while ((rootToken = parser.nextToken()) != JsonToken.END_OBJECT) {
                if (parser.currentToken() != JsonToken.FIELD_NAME) {
                    throw new IllegalStateException(filename + " contains an unexpected root token " + parser.currentToken());
                }

                String collectionName = parser.currentName();
                JsonToken collectionToken = parser.nextToken();
                if (!"monster".equals(collectionName)) {
                    parser.skipChildren();
                    continue;
                }

                foundMonsterCollection = true;
                if (collectionToken != JsonToken.START_ARRAY) {
                    throw new IllegalStateException(filename + " $.monster must be an array");
                }

                int arrayIndex = 0;
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    JsonNode sourceNode = objectMapper.readTree(parser);
                    if (!(sourceNode instanceof ObjectNode sourceObject)) {
                        throw new IllegalStateException(
                                filename + " $.monster[" + arrayIndex + "] must be an object");
                    }

                    try {
                        MonsterDte monster = objectMapper.treeToValue(sourceObject, MonsterDte.class);
                        MonsterIdentity identity = MonsterIdentity.from(
                                monster.identity().name(),
                                monster.identity().source());
                        MonsterSourceLocation location = new MonsterSourceLocation(filename, arrayIndex);
                        records.add(new MonsterSourceRecord(identity, location, monster, sourceObject));
                    } catch (IOException | RuntimeException exception) {
                        String name = sourceObject.path("name").asText("<missing-name>");
                        String source = sourceObject.path("source").asText("<missing-source>");
                        throw new IllegalStateException(
                                filename + " $.monster[" + arrayIndex + "] (" + name + "|" + source
                                        + ") could not be imported",
                                exception);
                    }
                    arrayIndex++;
                }
            }

            if (rootToken == null) {
                throw new IllegalStateException(filename + " ended before the root JSON object was closed");
            }
            if (parser.nextToken() != null) {
                throw new IllegalStateException(filename + " contains trailing JSON content after the root object");
            }
        }

        if (!foundMonsterCollection) {
            throw new IllegalStateException(filename + " does not contain the required monster collection");
        }
        return records;
    }

    private static String canonicalIdentity(String name, String source) {
        return name.trim().toLowerCase(Locale.ROOT) + "\u0000" + source.trim().toLowerCase(Locale.ROOT);
    }
}
