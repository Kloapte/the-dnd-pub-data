package com.thedndpub.data.importer.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.monster.MonsterDto;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxAnnotation;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxAnnotationIndex;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxContextKind;
import com.thedndpub.data.dto.monster.syntax.MonsterSyntaxSourceOwner;
import com.thedndpub.data.dte.monster.MonsterIdentity;
import com.thedndpub.data.dte.monster.MonsterSourceLocation;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.SyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxObservationClassifier;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

/** Builds immutable Monster syntax observations from original authored source JSON. */
public final class MonsterSyntaxAnnotationAdapter {
    private static final String RESOURCE_PATTERN = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + "data/monsters/bestiary-*.json";
    private static final Set<String> CONTAINER_FIELDS = Set.of(
            "trait", "action", "bonus", "reaction", "legendary", "mythic", "variant", "spellcasting");

    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourceResolver;
    private final AuthoredSyntaxTextWalker textWalker = new AuthoredSyntaxTextWalker();
    private final BalancedMarkupParser parser = new BalancedMarkupParser();
    private final SyntaxObservationClassifier classifier = new SyntaxObservationClassifier();

    public MonsterSyntaxAnnotationAdapter(ObjectMapper objectMapper) {
        this(objectMapper, new PathMatchingResourcePatternResolver());
    }

    public MonsterSyntaxAnnotationAdapter(
            ObjectMapper objectMapper,
            ResourcePatternResolver resourceResolver) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.resourceResolver = Objects.requireNonNull(resourceResolver, "resourceResolver");
    }

    public MonsterSyntaxAnnotationIndex adapt(
            List<MonsterDto> effectiveMonsters,
            BiFunction<String, String, MonsterId> identityLookup) {
        Objects.requireNonNull(effectiveMonsters, "effectiveMonsters");
        Objects.requireNonNull(identityLookup, "identityLookup");

        List<Resource> resources = sourceResources();
        List<MonsterSyntaxSourceOwner> owners = new ArrayList<>();
        List<MonsterSyntaxAnnotation> annotations = new ArrayList<>();
        Set<MonsterId> seenMonsterIds = new HashSet<>();

        for (Resource resource : resources) {
            byte[] sourceBytes = readSource(resource);
            JsonNode document = parseDocument(resource, sourceBytes);
            JsonNode monsterRecords = document.get("monster");
            if (monsterRecords == null || !monsterRecords.isArray()) {
                throw new IllegalStateException("Missing Monster array in " + resourcePath(resource));
            }

            String resourcePath = resourcePath(resource);
            String fingerprint = sha256(sourceBytes);
            for (int index = 0; index < monsterRecords.size(); index++) {
                JsonNode monsterRecord = monsterRecords.get(index);
                if (!monsterRecord.isObject()
                        || !monsterRecord.path("name").isTextual()
                        || !monsterRecord.path("source").isTextual()) {
                    throw new IllegalStateException(resourcePath + " $.monster[" + index
                            + "] requires an object with authored name and source strings");
                }

                String name = monsterRecord.path("name").textValue();
                String source = monsterRecord.path("source").textValue();
                MonsterIdentity identity = MonsterIdentity.from(name, source);
                MonsterId monsterId = identityLookup.apply(name, source);
                if (monsterId == null) {
                    throw new IllegalStateException("Authored Monster root has no effective catalog identity: "
                            + name + "|" + source + " at " + resourcePath + "#/monster/" + index);
                }
                if (!seenMonsterIds.add(monsterId)) {
                    throw new IllegalStateException("Duplicate Monster source owner identity " + monsterId.value()
                            + " at " + resourcePath + "#/monster/" + index);
                }

                String ownerPointer = "/monster/" + index;
                SyntaxSourceOwner evidenceOwner = new SyntaxSourceOwner(
                        resourcePath, fingerprint, "monster", ownerPointer);
                MonsterSourceLocation sourceLocation = new MonsterSourceLocation(
                        resource.getFilename(), index);
                owners.add(new MonsterSyntaxSourceOwner(monsterId, identity, sourceLocation, resourcePath));

                int[] occurrenceIndex = {0};
                textWalker.walk(monsterRecord, evidenceOwner, text -> appendObservations(
                        monsterId, identity, monsterRecord, ownerPointer, occurrenceIndex,
                        text, annotations));
            }
        }

        return new MonsterSyntaxAnnotationIndex(effectiveMonsters, resources.size(), owners, annotations);
    }

    private void appendObservations(
            MonsterId monsterId,
            MonsterIdentity identity,
            JsonNode originalMonster,
            String ownerPointer,
            int[] occurrenceIndex,
            SyntaxText text,
            List<MonsterSyntaxAnnotation> output) {
        List<MarkupTag> tags;
        try {
            tags = parser.parse(text).preorder();
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Could not parse Monster syntax at "
                    + text.location().owner().resourcePath() + "#" + text.location().jsonPointer(), exception);
        }

        for (MarkupTag tag : tags) {
            MonsterContainerContext context = containerContext(originalMonster, ownerPointer, tag);
            SyntaxObservation observation = classifier.classify(tag);
            output.add(new MonsterSyntaxAnnotation(
                    monsterId,
                    identity,
                    occurrenceIndex[0]++,
                    context.sourceContainer(),
                    context.contextKind(),
                    context.featureName(),
                    context.featurePointer(),
                    observation));
        }
    }

    private MonsterContainerContext containerContext(
            JsonNode originalMonster,
            String ownerPointer,
            MarkupTag tag) {
        String pointer = tag.evidence().location().jsonPointer();
        List<String> segments = pointerSegments(pointer.substring(ownerPointer.length()));
        String sourceContainer = segments.isEmpty() ? "other" : segments.getFirst();
        String featureName = null;
        String featurePointer = null;
        MonsterSyntaxContextKind contextKind = contextKind(sourceContainer);

        for (int segmentIndex = 0; segmentIndex + 1 < segments.size(); segmentIndex++) {
            String possibleContainer = segments.get(segmentIndex);
            if (!CONTAINER_FIELDS.contains(possibleContainer)
                    || !isArrayIndex(segments.get(segmentIndex + 1))) {
                continue;
            }

            sourceContainer = possibleContainer;
            contextKind = contextKind(possibleContainer);
            String relativeFeaturePointer = pointerSuffix(segments, segmentIndex + 1);
            JsonNode candidate = originalMonster.at(relativeFeaturePointer);
            if (candidate.isObject() && candidate.path("name").isTextual()) {
                featureName = candidate.path("name").textValue();
                featurePointer = ownerPointer + relativeFeaturePointer;
            }
        }

        return new MonsterContainerContext(sourceContainer, contextKind, featureName, featurePointer);
    }

    private List<String> pointerSegments(String suffix) {
        if (suffix.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(suffix.substring(1).split("/", -1))
                .map(MonsterSyntaxAnnotationAdapter::decodePointerSegment)
                .toList();
    }

    private String pointerSuffix(List<String> segments, int endIndex) {
        StringBuilder pointer = new StringBuilder();
        for (int index = 0; index <= endIndex; index++) {
            pointer.append('/').append(segments.get(index).replace("~", "~0").replace("/", "~1"));
        }
        return pointer.toString();
    }

    private static String decodePointerSegment(String segment) {
        return segment.replace("~1", "/").replace("~0", "~");
    }

    private static boolean isArrayIndex(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private static MonsterSyntaxContextKind contextKind(String sourceContainer) {
        return switch (sourceContainer) {
            case "trait" -> MonsterSyntaxContextKind.TRAIT;
            case "action" -> MonsterSyntaxContextKind.ACTION;
            case "bonus" -> MonsterSyntaxContextKind.BONUS;
            case "reaction" -> MonsterSyntaxContextKind.REACTION;
            case "legendary" -> MonsterSyntaxContextKind.LEGENDARY;
            case "mythic" -> MonsterSyntaxContextKind.MYTHIC;
            case "variant" -> MonsterSyntaxContextKind.VARIANT;
            case "spellcasting" -> MonsterSyntaxContextKind.SPELLCASTING;
            default -> MonsterSyntaxContextKind.OTHER;
        };
    }

    private List<Resource> sourceResources() {
        Resource[] resources;
        try {
            resources = resourceResolver.getResources(RESOURCE_PATTERN);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not enumerate Monster source resources", exception);
        }
        if (resources.length == 0) {
            throw new IllegalStateException("No Monster source resources matched " + RESOURCE_PATTERN);
        }

        return Arrays.stream(resources)
                .sorted(Comparator.comparing(resource -> requireFilename(resource).toLowerCase(Locale.ROOT)))
                .toList();
    }

    private byte[] readSource(Resource resource) {
        try (var input = resource.getInputStream()) {
            return input.readAllBytes();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read Monster resource " + resourcePath(resource), exception);
        }
    }

    private JsonNode parseDocument(Resource resource, byte[] sourceBytes) {
        try (JsonParser parser = objectMapper.getFactory().createParser(sourceBytes)) {
            JsonNode document = objectMapper.readTree(parser);
            if (document == null || !document.isObject() || parser.nextToken() != null) {
                throw new IllegalStateException("Monster resource must contain one JSON object: "
                        + resourcePath(resource));
            }
            return document;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not parse Monster source resource "
                    + resourcePath(resource), exception);
        }
    }

    private static String requireFilename(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalStateException("Monster resource has no stable filename: " + resource.getDescription());
        }
        return filename;
    }

    private static String resourcePath(Resource resource) {
        return "data/monsters/" + requireFilename(resource);
    }

    private static String sha256(byte[] sourceBytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(sourceBytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private record MonsterContainerContext(
            String sourceContainer,
            MonsterSyntaxContextKind contextKind,
            String featureName,
            String featurePointer) {
    }
}
