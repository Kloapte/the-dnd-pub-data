package com.thedndpub.data.importer.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotation;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellFormulaSection;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.SpellSyntaxAnnotation;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.LabelSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;
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
import java.util.HashMap;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Builds Spell formula syntax observations from original source JSON without resolving copies. */
public final class SpellFormulaAnnotationAdapter {
    private static final String RESOURCE_PATTERN = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + "data/spells/*.json";
    private static final Set<String> FORMULA_TAGS = Set.of("damage", "dice", "scaledamage", "scaledice");

    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourceResolver;
    private final AuthoredSyntaxTextWalker textWalker = new AuthoredSyntaxTextWalker();
    private final BalancedMarkupParser parser = new BalancedMarkupParser();
    private final SyntaxObservationClassifier classifier = new SyntaxObservationClassifier();

    public SpellFormulaAnnotationAdapter(ObjectMapper objectMapper) {
        this(objectMapper, new PathMatchingResourcePatternResolver());
    }

    public SpellFormulaAnnotationAdapter(
            ObjectMapper objectMapper,
            ResourcePatternResolver resourceResolver) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.resourceResolver = Objects.requireNonNull(resourceResolver, "resourceResolver");
    }

    public SpellFormulaAnnotationIndex adapt(List<SpellDefinition> spellDefinitions) {
        Map<SpellId, SpellDefinition> definitionsById = indexSpellDefinitions(spellDefinitions);
        List<Resource> resources = sourceResources();
        List<SpellFormulaAnnotation> annotations = new ArrayList<>();
        List<SpellSyntaxAnnotation> syntaxAnnotations = new ArrayList<>();
        Set<SpellId> seenSourceSpells = new HashSet<>();

        for (Resource resource : resources) {
            byte[] sourceBytes = readSource(resource);
            JsonNode document = parseDocument(resource, sourceBytes);
            JsonNode spellRecords = document.get("spell");
            if (spellRecords == null || !spellRecords.isArray()) {
                throw new IllegalStateException("Missing Spell array in " + resourcePath(resource));
            }

            String resourcePath = resourcePath(resource);
            String fingerprint = sha256(sourceBytes);
            for (int index = 0; index < spellRecords.size(); index++) {
                JsonNode spellRecord = spellRecords.get(index);
                SpellId spellId = spellId(spellRecord, resourcePath, index);
                if (!definitionsById.containsKey(spellId)) {
                    throw new IllegalStateException("Source Spell identity is absent from normalized catalog: "
                            + spellId.value() + " at " + resourcePath + "#/spell/" + index);
                }
                if (!seenSourceSpells.add(spellId)) {
                    throw new IllegalStateException("Duplicate source Spell identity in formula adapter: "
                            + spellId.value());
                }

                String ownerPointer = "/spell/" + index;
                SyntaxSourceOwner owner = new SyntaxSourceOwner(resourcePath, fingerprint, "spell", ownerPointer);
                int[] occurrenceIndex = {0};
                int[] syntaxOccurrenceIndex = {0};
                textWalker.walk(spellRecord, owner, text -> appendAnnotations(
                        spellId, occurrenceIndex, syntaxOccurrenceIndex, text, annotations, syntaxAnnotations));
            }
        }

        if (!seenSourceSpells.equals(definitionsById.keySet())) {
            Set<SpellId> missingSourceSpells = new HashSet<>(definitionsById.keySet());
            missingSourceSpells.removeAll(seenSourceSpells);
            throw new IllegalStateException("Normalized Spells lack source roots in formula adapter: "
                    + missingSourceSpells.stream().map(SpellId::value).sorted().toList());
        }
        return new SpellFormulaAnnotationIndex(spellDefinitions, resources.size(), annotations, syntaxAnnotations);
    }

    private Map<SpellId, SpellDefinition> indexSpellDefinitions(List<SpellDefinition> spellDefinitions) {
        Map<SpellId, SpellDefinition> definitionsById = new HashMap<>();
        for (SpellDefinition definition : spellDefinitions) {
            SpellId expectedId = SpellIdentityFactory.id(
                    definition.identity().name(), definition.source().identity().code());
            if (!expectedId.equals(definition.id())) {
                throw new IllegalStateException("Spell definition identity does not match its stable ID: "
                        + definition.id().value());
            }
            if (definitionsById.putIfAbsent(definition.id(), definition) != null) {
                throw new IllegalStateException("Duplicate normalized Spell ID: " + definition.id().value());
            }
        }
        return Map.copyOf(definitionsById);
    }

    private List<Resource> sourceResources() {
        Resource[] resources;
        try {
            resources = resourceResolver.getResources(RESOURCE_PATTERN);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not enumerate Spell source resources", exception);
        }
        if (resources.length == 0) {
            throw new IllegalStateException("No Spell source resources found at " + RESOURCE_PATTERN);
        }
        Arrays.sort(resources, Comparator.comparing(SpellFormulaAnnotationAdapter::resourcePath));
        Set<String> resourcePaths = new HashSet<>();
        for (Resource resource : resources) {
            if (!resourcePaths.add(resourcePath(resource))) {
                throw new IllegalStateException("Duplicate Spell source resource path: " + resourcePath(resource));
            }
        }
        return List.of(resources);
    }

    private byte[] readSource(Resource resource) {
        try (var input = resource.getInputStream()) {
            return input.readAllBytes();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read Spell source " + resourcePath(resource), exception);
        }
    }

    private JsonNode parseDocument(Resource resource, byte[] sourceBytes) {
        try {
            JsonNode document = objectMapper.readTree(sourceBytes);
            if (document == null || !document.isObject()) {
                throw new IllegalStateException("Spell source root must be an object in " + resourcePath(resource));
            }
            return document;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not parse Spell source " + resourcePath(resource), exception);
        }
    }

    private SpellId spellId(JsonNode spellRecord, String resourcePath, int index) {
        if (!spellRecord.isObject()
                || !spellRecord.path("name").isTextual()
                || !spellRecord.path("source").isTextual()) {
            throw new IllegalStateException("Spell source root must have textual name and source at "
                    + resourcePath + "#/spell/" + index);
        }
        return SpellIdentityFactory.id(spellRecord.path("name").textValue(),
                spellRecord.path("source").textValue());
    }

    private void appendAnnotations(
            SpellId spellId,
            int[] formulaOccurrenceIndex,
            int[] syntaxOccurrenceIndex,
            SyntaxText text,
            List<SpellFormulaAnnotation> annotations,
            List<SpellSyntaxAnnotation> syntaxAnnotations) {
        for (SyntaxObservation observation : classifier.classify(parser.parse(text))) {
            MarkupTag tag = observation.tag();
            if (!FORMULA_TAGS.contains(tag.name().toLowerCase(Locale.ROOT))) {
                if (observation instanceof LabelSyntaxObservation
                        || observation instanceof ModifierSyntaxObservation) {
                    syntaxAnnotations.add(new SpellSyntaxAnnotation(
                            spellId,
                            syntaxOccurrenceIndex[0]++,
                            section(tag),
                            observation));
                }
                continue;
            }
            annotations.add(new SpellFormulaAnnotation(
                    spellId, formulaOccurrenceIndex[0]++, section(tag), observation));
        }
    }

    private SpellFormulaSection section(MarkupTag tag) {
        String pointer = tag.evidence().location().jsonPointer();
        String ownerPointer = tag.evidence().location().owner().jsonPointer();
        String relativePointer = pointer.substring(ownerPointer.length());
        for (String encodedSegment : relativePointer.split("/")) {
            String segment = encodedSegment.replace("~1", "/").replace("~0", "~");
            if (segment.equals("entries")) {
                return SpellFormulaSection.ENTRIES;
            }
            if (segment.equals("entriesHigherLevel")) {
                return SpellFormulaSection.ENTRIES_HIGHER_LEVEL;
            }
        }
        return SpellFormulaSection.OTHER_SOURCE_FIELD;
    }

    private static String resourcePath(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalStateException("Spell resource has no stable filename: " + resource.getDescription());
        }
        return "data/spells/" + filename;
    }

    private static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
