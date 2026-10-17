package com.thedndpub.data.importer.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.thedndpub.data.dto.monster.support.LegendaryGroupAuthoredRecord;
import com.thedndpub.data.dto.monster.support.LegendaryGroupAuthoredTemplate;
import com.thedndpub.data.dto.monster.support.LegendaryGroupInputs;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupMetadataDte;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupTemplateDte;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupsFileDte;
import com.thedndpub.data.mechanics.syntax.AuthoredSyntaxTextWalker;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;
import com.thedndpub.data.services.SourceRegistry;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Loads only the legendary-group dialect; Monster template ownership stays in its existing loader. */
public final class LegendaryGroupSourceLoader {
    public static final String GROUP_RESOURCE = "data/monsters/legendarygroups.json";
    public static final String TEMPLATE_RESOURCE = "data/monsters/template.json";

    private final ObjectMapper mapper;
    private final SourceRegistry sourceRegistry;
    private final AuthoredSyntaxTextWalker textWalker = new AuthoredSyntaxTextWalker();

    public LegendaryGroupSourceLoader(ObjectMapper mapper, SourceRegistry sourceRegistry) {
        this.mapper = Objects.requireNonNull(mapper, "mapper").copy()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
        this.mapper.coercionConfigFor(LogicalType.Textual)
                .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);
        this.mapper.coercionConfigFor(LogicalType.Integer)
                .setCoercion(CoercionInputShape.String, CoercionAction.Fail);
        this.sourceRegistry = Objects.requireNonNull(sourceRegistry, "sourceRegistry");
    }

    public LegendaryGroupInputs loadResources() {
        try {
            return read(readResource(GROUP_RESOURCE), readResource(TEMPLATE_RESOURCE));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load legendary-group support resources", exception);
        }
    }

    public LegendaryGroupInputs read(byte[] groupBytes, byte[] templateBytes) throws IOException {
        JsonNode groupDocument = readDocument(groupBytes);
        validateValuesAndSources(groupDocument, GROUP_RESOURCE);
        LegendaryGroupsFileDte source = mapper.treeToValue(groupDocument, LegendaryGroupsFileDte.class);
        requireMetadata(source.metadata(), "legendaryGroup", GROUP_RESOURCE);
        List<LegendaryGroupAuthoredRecord> groups = new ArrayList<>();
        String groupHash = sha256(groupBytes);
        for (int index = 0; index < source.legendaryGroup().size(); index++) {
            var owner = new SyntaxSourceOwner(
                    GROUP_RESOURCE, groupHash, "legendaryGroup", "/legendaryGroup/" + index);
            groups.add(new LegendaryGroupAuthoredRecord(
                    source.legendaryGroup().get(index), owner,
                    texts(groupDocument.path("legendaryGroup").get(index), owner)));
        }

        JsonNode templateDocument = readDocument(templateBytes);
        requireFields(templateDocument, Set.of("_meta", "monsterTemplate", "legendaryGroupTemplate"));
        requireMetadata(mapper.treeToValue(templateDocument.get("_meta"), LegendaryGroupMetadataDte.class),
                "monsterTemplate", TEMPLATE_RESOURCE);
        JsonNode templateArray = templateDocument.get("legendaryGroupTemplate");
        if (templateArray == null || !templateArray.isArray()) {
            throw new IllegalArgumentException(TEMPLATE_RESOURCE + " requires legendaryGroupTemplate array");
        }
        // monsterTemplate is validated and consumed independently by MonsterTemplateRepository.
        if (!templateDocument.path("monsterTemplate").isArray()) {
            throw new IllegalArgumentException(TEMPLATE_RESOURCE + " requires monsterTemplate array");
        }
        List<LegendaryGroupAuthoredTemplate> templates = new ArrayList<>();
        String templateHash = sha256(templateBytes);
        for (int index = 0; index < templateArray.size(); index++) {
            JsonNode node = templateArray.get(index);
            validateValuesAndSources(node, TEMPLATE_RESOURCE + "/legendaryGroupTemplate/" + index);
            var owner = new SyntaxSourceOwner(
                    TEMPLATE_RESOURCE, templateHash,
                    "legendaryGroupTemplate", "/legendaryGroupTemplate/" + index);
            templates.add(new LegendaryGroupAuthoredTemplate(
                    mapper.treeToValue(node, LegendaryGroupTemplateDte.class), owner, texts(node, owner)));
        }
        return new LegendaryGroupInputs(groups, templates);
    }

    private List<SyntaxText> texts(JsonNode node, SyntaxSourceOwner owner) {
        List<SyntaxText> texts = new ArrayList<>();
        textWalker.walk(node, owner, texts::add);
        return List.copyOf(texts);
    }

    private JsonNode readDocument(byte[] bytes) throws IOException {
        // Trailing-token validation belongs to the complete resource, not nested EntryDte dispatch.
        return mapper.readerFor(JsonNode.class)
                .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .readValue(bytes);
    }

    private void validateValuesAndSources(JsonNode node, String path) {
        if (node == null || node.isNull()) {
            throw new IllegalArgumentException(path + ": explicit null is not an observed support variant");
        }
        if (node.isObject()) {
            node.fields().forEachRemaining(field -> {
                if (field.getKey().equals("source")) {
                    JsonNode code = field.getValue();
                    if (!code.isTextual() || code.textValue().isBlank()) {
                        throw new IllegalArgumentException(path + "/source must be a nonempty string");
                    }
                    var registered = sourceRegistry.find(code.textValue());
                    if (registered == null || !registered.identity().code().equals(code.textValue())) {
                        throw new IllegalArgumentException(path + ": unregistered authored source " + code);
                    }
                }
                validateValuesAndSources(field.getValue(), path + "/" + field.getKey());
            });
        } else if (node.isArray()) {
            for (int index = 0; index < node.size(); index++) {
                validateValuesAndSources(node.get(index), path + "/" + index);
            }
        }
    }

    private static void requireFields(JsonNode root, Set<String> allowed) {
        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("Legendary-group template resource must be an object");
        }
        root.fieldNames().forEachRemaining(field -> {
            if (!allowed.contains(field)) {
                throw new IllegalArgumentException("Unexpected template resource root field: " + field);
            }
        });
    }

    private static void requireMetadata(LegendaryGroupMetadataDte metadata, String collection, String resource) {
        if (metadata == null || !metadata.internalCopies().equals(List.of(collection))) {
            throw new IllegalArgumentException("Unsupported internalCopies metadata in " + resource);
        }
    }

    private static byte[] readResource(String path) throws IOException {
        try (var input = new ClassPathResource(path).getInputStream()) {
            return input.readAllBytes();
        }
    }

    private static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
