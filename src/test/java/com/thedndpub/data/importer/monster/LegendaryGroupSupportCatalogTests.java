package com.thedndpub.data.importer.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.identity.Reference;
import com.thedndpub.data.dto.monster.support.LegendaryGroupAssociationStatus;
import com.thedndpub.data.dto.monster.support.LegendaryGroupBodyEntry;
import com.thedndpub.data.dto.monster.support.LegendaryGroupDefinition;
import com.thedndpub.data.dto.monster.support.LegendaryGroupId;
import com.thedndpub.data.dto.monster.support.LegendaryGroupInputs;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupArrayMode;
import com.thedndpub.data.dte.legendarygroup.LegendaryGroupArrayModificationDte;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.services.LegendaryGroupSupportCatalog;
import com.thedndpub.data.services.MonsterCatalog;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LegendaryGroupSupportCatalogTests {
    private static final String EMPTY_TEMPLATES = """
            {"_meta":{"internalCopies":["monsterTemplate"]},"monsterTemplate":[],"legendaryGroupTemplate":[]}
            """;

    @Autowired
    private LegendaryGroupSupportCatalog catalog;
    @Autowired
    private MonsterCatalog monsters;
    @Autowired
    private SourceRegistry sourceRegistry;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void authoredAndEffectiveSupportDenominatorsRemainSeparateFromMonsters() throws Exception {
        var roots = catalog.authoredInputs().groups();
        assertEquals(188, roots.size());
        assertEquals(115, roots.stream().filter(root -> root.declaration().lairActions() != null).count());
        assertEquals(158, roots.stream().filter(root -> root.declaration().regionalEffects() != null).count());
        assertEquals(3, roots.stream().filter(root -> root.declaration().mythicEncounter() != null).count());
        assertEquals(16, roots.stream().filter(root -> root.declaration().copy() != null).count());
        assertEquals(21, roots.stream().filter(root -> root.declaration().versions() != null).count());
        assertEquals(21, roots.stream().filter(root -> root.declaration().versions() != null)
                .mapToInt(root -> root.declaration().versions().size()).sum());
        assertEquals(1, catalog.authoredInputs().templates().size());
        assertEquals(209, catalog.definitions().size());
        assertEquals(209, catalog.definitionsById().size());
        assertEquals(152, catalog.definitions().stream().filter(group -> group.lairActions() != null).count());
        assertEquals(184, catalog.definitions().stream().filter(group -> group.regionalEffects() != null).count());
        assertEquals(3, catalog.definitions().stream().filter(group -> group.mythicEncounter() != null).count());
        assertEquals(4851, monsters.size());

        Map<String, Object> report = new TreeMap<>();
        report.put("authoredRoots", roots.size());
        report.put("copyEffectiveRoots", 188);
        report.put("expandedSupportGroups", catalog.definitions().size());
        report.put("effectiveMonsterDefinitions", monsters.size());
        report.put("effectiveMonsterAssociations", catalog.associations().size());
        report.put("associatedSupportGroups", catalog.associations().values().stream()
                .map(association -> association.targetId()).distinct().count());
        report.put("versionAssociations", catalog.associations().values().stream()
                .filter(association -> catalog.definitionsById().get(association.targetId())
                        .provenance().versionParent() != null).count());
        report.put("effectiveBodyEntries", catalog.definitions().stream()
                .mapToInt(definition -> bodies(definition).size()).sum());
        report.put("unresolvedAssociations", catalog.associations().values().stream()
                .filter(match -> match.status() == LegendaryGroupAssociationStatus.UNRESOLVED).count());
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(Path.of("target/batch-7-runtime-report.json").toFile(), report);
    }

    @Test
    void everyDirectAuthoredBodyAndMetadataSurvivesWithoutFlattening() {
        for (var root : catalog.authoredInputs().groups()) {
            var definition = catalog.definitionsById().get(root.id());
            var declaration = root.declaration();
            assertEquals(declaration.page(), definition.source().page());
            assertEquals(declaration.source(), definition.source().identity().code());
            assertNotNull(sourceRegistry.find(declaration.source()));
            if (declaration.additionalSources() == null) {
                assertNull(definition.additionalSources());
            } else {
                assertEquals(declaration.additionalSources().stream().map(reference -> reference.source()).toList(),
                        definition.additionalSources().stream().map(reference -> reference.identity().code()).toList());
            }
            assertDirectBody(declaration.regionalEffects(), definition.regionalEffects());
            assertDirectBody(declaration.mythicEncounter(), definition.mythicEncounter());
            assertDirectBody(declaration.lairActions(), definition.lairActions());
        }
    }

    @Test
    void allCopiesRetainParentBodiesAndInstructionOrdering() {
        int appends = 0;
        int prepends = 0;
        for (var root : catalog.authoredInputs().groups()) {
            var copy = root.declaration().copy();
            if (copy == null) {
                continue;
            }
            var parent = require(copy.name(), copy.source());
            var child = catalog.definitionsById().get(root.id());
            assertEquals(List.of(parent.id()), child.provenance().copyLineage());
            var modification = copy.modifications().lairActions();
            int insertedCount = modification.items().size();
            int parentCount = parent.lairActions().size();
            assertEquals(insertedCount + parentCount, child.lairActions().size());
            if (modification.mode() == LegendaryGroupArrayMode.APPEND) {
                appends++;
                assertEquals(parent.lairActions(), child.lairActions().subList(0, parentCount));
                assertEquals(modification.items(), child.lairActions().subList(parentCount,
                        child.lairActions().size()).stream().map(LegendaryGroupBodyEntry::content).toList());
                assertEquals(parent.regionalEffects(), child.regionalEffects()
                        .subList(0, parent.regionalEffects().size()));
                assertEquals(copy.modifications().regionalEffects().items(), child.regionalEffects()
                        .subList(parent.regionalEffects().size(), child.regionalEffects().size())
                        .stream().map(LegendaryGroupBodyEntry::content).toList());
            } else {
                prepends++;
                assertEquals(parent.lairActions(), child.lairActions().subList(insertedCount,
                        child.lairActions().size()));
                assertEquals(modification.items(), child.lairActions().subList(0, insertedCount)
                        .stream().map(LegendaryGroupBodyEntry::content).toList());
            }
        }
        assertEquals(5, appends);
        assertEquals(11, prepends);
        var greenHag = require("Green Hag", "MM");
        assertEquals("VGM", greenHag.provenance().copyLineage().getFirst().source().code());
        assertTrue(greenHag.lairActions().getLast().jsonPointer().contains("/_copy/_mod/"));
    }

    @Test
    void allVersionsApplyOnlyTheirAuthoredTemplateAndPreserveDistinctOrigins() {
        var template = catalog.authoredInputs().templates().getFirst();
        for (var version : catalog.definitions()) {
            var parentId = version.provenance().versionParent();
            if (parentId == null) {
                continue;
            }
            var parent = catalog.definitionsById().get(parentId);
            assertEquals(List.of(template.id()), version.provenance().templates());
            assertNull(version.source().page());
            assertNull(version.additionalSources());
            assertTrue(version.provenance().declarationPointer().endsWith("/_versions/0"));
            int parentSize = parent.lairActions() == null ? 0 : parent.lairActions().size();
            assertEquals(parentSize + 1, version.lairActions().size());
            if (parentSize > 0) {
                assertEquals(parent.lairActions(), version.lairActions().subList(0, parentSize));
            }
            assertEquals(template.owner(), version.lairActions().getLast().originOwner());
            assertEquals("/legendaryGroupTemplate/0/apply/_mod/lairActions/items/0",
                    version.lairActions().getLast().jsonPointer());
            assertEquals(parent.regionalEffects(), version.regionalEffects()
                    .subList(0, parent.regionalEffects().size()));
            assertEquals(template.declaration().apply().modifications().regionalEffects().items(),
                    version.regionalEffects().subList(parent.regionalEffects().size(), version.regionalEffects().size())
                            .stream().map(LegendaryGroupBodyEntry::content).toList());
        }
        assertEquals(1, require("Shadow Dragon (Lunar Dragon)", "BAM").lairActions().size());
        assertEquals(1, require("Shadow Dragon (Solar Dragon)", "BAM").lairActions().size());
        assertFalse(require("Shadow Dragon (Sapphire Dragon)", "FTD").id()
                .equals(require("Shadow Dragon (Sapphire Dragon)", "SADS").id()));
    }

    @Test
    void everyEffectiveEntryResolvesToTheExactOriginalSharedEntryAndFileHash() throws Exception {
        Map<String, JsonNode> documents = new HashMap<>();
        Map<String, String> hashes = new HashMap<>();
        for (var definition : catalog.definitions()) {
            for (var body : bodies(definition)) {
                var owner = body.originOwner();
                if (!documents.containsKey(owner.resourcePath())) {
                    byte[] bytes = resource(owner.resourcePath());
                    documents.put(owner.resourcePath(), mapper.readTree(bytes));
                    hashes.put(owner.resourcePath(), HexFormat.of().formatHex(
                            MessageDigest.getInstance("SHA-256").digest(bytes)));
                }
                JsonNode original = documents.get(owner.resourcePath()).at(body.jsonPointer());
                assertFalse(original.isMissingNode(), body.jsonPointer());
                assertEquals(mapper.treeToValue(original, EntryDte.class), body.content());
                assertEquals(hashes.get(owner.resourcePath()), owner.resourceSha256());
            }
        }
        assertEquals(2, documents.size());
    }

    @Test
    void authoredSyntaxInputsKeepPointersHierarchyTransformOriginAndUtf16Spans() throws Exception {
        var parser = new BalancedMarkupParser();
        int tags = 0;
        int transformTags = 0;
        int templateTags = 0;
        var texts = Stream.concat(catalog.authoredInputs().groups().stream().flatMap(root -> root.syntaxTexts().stream()),
                catalog.authoredInputs().templates().stream().flatMap(template -> template.syntaxTexts().stream())).toList();
        Map<String, JsonNode> documents = Map.of(
                LegendaryGroupSourceLoader.GROUP_RESOURCE,
                mapper.readTree(resource(LegendaryGroupSourceLoader.GROUP_RESOURCE)),
                LegendaryGroupSourceLoader.TEMPLATE_RESOURCE,
                mapper.readTree(resource(LegendaryGroupSourceLoader.TEMPLATE_RESOURCE)));
        for (var text : texts) {
            var location = text.location();
            assertEquals(documents.get(location.owner().resourcePath()).at(location.jsonPointer()).textValue(),
                    text.text());
            assertFalse(location.hierarchy().isEmpty());
            for (var tag : parser.parse(text).preorder()) {
                tags++;
                assertEquals(tag.originalText(), tag.evidence().span().extract(text.text()));
                if (location.origin() == SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION) {
                    transformTags++;
                }
                if (location.owner().collection().equals("legendaryGroupTemplate")) {
                    templateTags++;
                }
            }
        }
        assertEquals(1081, tags);
        assertEquals(25, transformTags);
        assertEquals(2, templateTags);
    }

    @Test
    void effectiveMonsterAssociationsUseOnlyExactAuthoredNameSourcePairs() {
        long expected = monsters.monstersById().values().stream()
                .filter(monster -> monster.identity().legendaryGroup() != null).count();
        assertEquals(307, expected);
        assertEquals(expected, catalog.associations().size());
        for (var association : catalog.associations().values()) {
            var monster = monsters.findById(association.monsterId().value());
            var reference = monster.identity().legendaryGroup();
            assertEquals(reference.name(), association.requestedGroup().name());
            assertEquals(reference.source(), association.requestedGroup().source().code());
            assertEquals(LegendaryGroupAssociationStatus.RESOLVED, association.status());
            assertEquals(association.requestedGroup(), association.targetId());
            assertNotNull(catalog.definitionsById().get(association.targetId()));
        }
        var unresolved = catalog.associate(new MonsterId("synthetic"), new Reference("Aboleth", "PHB"));
        assertEquals(LegendaryGroupAssociationStatus.UNRESOLVED, unresolved.status());
        assertNull(unresolved.targetId());
        assertNotNull(require("Aboleth", "MM"));
        assertNull(catalog.findByNameAndSource("Aboleth", "PHB"));
    }

    @Test
    void nestedSourceAndNormalizedCollectionsAreImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitionsById().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.associations().clear());
        var inputs = catalog.authoredInputs();
        assertThrows(UnsupportedOperationException.class, () -> inputs.groups().clear());
        assertThrows(UnsupportedOperationException.class, () -> inputs.groups().getFirst().syntaxTexts().clear());
        var group = require("Aboleth", "MM");
        assertThrows(UnsupportedOperationException.class, () -> group.lairActions().clear());
        var list = group.lairActions().stream().map(LegendaryGroupBodyEntry::content)
                .filter(ListEntryDte.class::isInstance).map(ListEntryDte.class::cast).findFirst().orElseThrow();
        assertThrows(UnsupportedOperationException.class, () -> list.items().clear());
        var table = catalog.definitions().stream().flatMap(definition -> bodies(definition).stream())
                .map(LegendaryGroupBodyEntry::content).filter(NamedEntryDte.class::isInstance)
                .map(NamedEntryDte.class::cast).flatMap(named -> named.entries().stream())
                .filter(TableEntryDte.class::isInstance).map(TableEntryDte.class::cast).findFirst().orElseThrow();
        assertThrows(UnsupportedOperationException.class, () -> table.rows().getFirst().clear());
        assertThrows(UnsupportedOperationException.class, () -> table.colLabels().clear());
        List<EntryDte> mutableItems = new ArrayList<>(List.of(new TextEntryDte("original")));
        var snapshot = new LegendaryGroupArrayModificationDte(LegendaryGroupArrayMode.APPEND,
                List.of(new ListEntryDte(null, null, mutableItems)));
        mutableItems.clear();
        assertEquals(1, ((ListEntryDte) snapshot.items().getFirst()).items().size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.items().clear());
    }

    @Test
    void unknownFieldsModesAndSourceShapeChangesAreRejectedRatherThanDiscarded() {
        List<String> malformedRoots = List.of(
                "{\"name\":\"Test\",\"source\":\"MM\",\"unexpected\":true}",
                """
                {"name":"Test","source":"MM",
                 "lairActions":[{"type":"list","items":[],"unknown":0}]}
                """,
                """
                {"name":"Test","source":"MM","_copy":{"name":"Parent","source":"MM",
                 "_mod":{"lairActions":{"mode":"replaceTxt","items":[]}}}}
                """,
                """
                {"name":"Test","source":"MM","_copy":{"name":"Parent","source":"MM",
                 "_mod":{"unknown":{"mode":"appendArr","items":[]}}}}
                """,
                "{\"name\":\"Test\",\"source\":\"MM\",\"page\":\"10\"}",
                "{\"name\":17,\"source\":\"MM\"}",
                "{\"name\":\"Test\",\"source\":\"MM\",\"lairActions\":null}",
                "{\"name\":\"Test\",\"source\":\"UNKNOWN\"}",
                "{\"name\":\"Test\",\"source\":\"MM\",\"lairActions\":[{\"type\":\"unknown\"}]}"
        );
        for (String root : malformedRoots) {
            assertThrows(Exception.class, () -> input(root), root);
        }
        assertThrows(Exception.class, () -> new LegendaryGroupSourceLoader(mapper, sourceRegistry).read(
                groupBytes("{\"name\":\"Test\",\"source\":\"MM\"}"),
                EMPTY_TEMPLATES.replace("\"legendaryGroupTemplate\":[]", "\"unexpected\":[]")
                        .getBytes(StandardCharsets.UTF_8)));
        assertThrows(Exception.class, () -> new LegendaryGroupSourceLoader(mapper, sourceRegistry).read(
                (new String(groupBytes(""), StandardCharsets.UTF_8) + " {}").getBytes(StandardCharsets.UTF_8),
                EMPTY_TEMPLATES.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void missingParentsTemplatesCyclesAndDuplicateIdentitiesFailExplicitly() {
        List<String> invalidGraphs = List.of(
                "{\"name\":\"Child\",\"source\":\"MM\",\"_copy\":{\"name\":\"Missing\",\"source\":\"MM\"}}",
                "{\"name\":\"Loop\",\"source\":\"MM\",\"_copy\":{\"name\":\"Loop\",\"source\":\"MM\"}}",
                "{\"name\":\"Same\",\"source\":\"MM\"},{\"name\":\"Same\",\"source\":\"MM\"}",
                """
                {"name":"Base","source":"MM","_versions":[{"name":"Variant","source":"MM",
                 "_templates":[{"name":"Missing","source":"FTD"}]}]}
                """,
                """
                {"name":"Base","source":"MM",
                 "_versions":[{"name":"Base","source":"MM","_templates":[]}]}
                """,
                "{\"name\":\"A\",\"source\":\"MM\",\"_copy\":{\"name\":\"B\",\"source\":\"MM\"}},"
                        + "{\"name\":\"B\",\"source\":\"MM\",\"_copy\":{\"name\":\"A\",\"source\":\"MM\"}}"
        );
        for (String roots : invalidGraphs) {
            assertThrows(Exception.class, () -> new LegendaryGroupResolver().resolve(input(roots)), roots);
        }
    }

    @Test
    void unsupportedAndDuplicateTemplatesFailWithoutAffectingTheMonsterTemplateDialect() throws Exception {
        byte[] groups = resource(LegendaryGroupSourceLoader.GROUP_RESOURCE);
        var templateDocument = mapper.readTree(resource(LegendaryGroupSourceLoader.TEMPLATE_RESOURCE));
        var templates = (ArrayNode) templateDocument.get("legendaryGroupTemplate");
        templates.add(templates.get(0).deepCopy());
        var loader = new LegendaryGroupSourceLoader(mapper, sourceRegistry);
        var duplicateInputs = loader.read(groups, mapper.writeValueAsBytes(templateDocument));
        assertThrows(IllegalArgumentException.class, () -> new LegendaryGroupResolver().resolve(duplicateInputs));

        templateDocument = mapper.readTree(resource(LegendaryGroupSourceLoader.TEMPLATE_RESOURCE));
        var application = (ObjectNode) templateDocument.at("/legendaryGroupTemplate/0/apply");
        application.putObject("_root").put("name", "not-an-observed-group-operation");
        byte[] unsupported = mapper.writeValueAsBytes(templateDocument);
        assertThrows(Exception.class, () -> loader.read(groups, unsupported));
    }

    @Test
    void nestedCopyLineageKeepsTheOriginalOwnerOfEachArrayEntry() throws Exception {
        var resolved = new LegendaryGroupResolver().resolve(input("""
                {"name":"A","source":"MM","lairActions":["a"]},
                {"name":"B","source":"VGM","_copy":{"name":"A","source":"MM",
                 "_mod":{"lairActions":{"mode":"appendArr","items":["b"]}}}},
                {"name":"C","source":"MM","_copy":{"name":"B","source":"VGM",
                 "_mod":{"lairActions":{"mode":"prependArr","items":["c"]}}}}
                """));
        var child = resolved.get(LegendaryGroupId.from("C", "MM"));
        assertEquals(List.of(LegendaryGroupId.from("B", "VGM"), LegendaryGroupId.from("A", "MM")),
                child.provenance().copyLineage());
        assertEquals(List.of(new TextEntryDte("c"), new TextEntryDte("a"), new TextEntryDte("b")),
                child.lairActions().stream().map(LegendaryGroupBodyEntry::content).toList());
        assertEquals(List.of("/legendaryGroup/2", "/legendaryGroup/0", "/legendaryGroup/1"),
                child.lairActions().stream().map(entry -> entry.originOwner().jsonPointer()).toList());
    }

    @Test
    void absenceEmptyBodiesZeroAndChildOverridesRemainDistinct() throws Exception {
        var resolved = new LegendaryGroupResolver().resolve(input("""
                {"name":"Base","source":"MM","page":0,"lairActions":["parent"],"regionalEffects":[]},
                {"name":"Child","source":"MM","_copy":{"name":"Base","source":"MM"},"lairActions":[]}
                """));
        var base = resolved.get(LegendaryGroupId.from("Base", "MM"));
        var child = resolved.get(LegendaryGroupId.from("Child", "MM"));
        assertEquals(0, base.source().page());
        assertTrue(base.regionalEffects().isEmpty());
        assertNull(base.mythicEncounter());
        assertTrue(child.lairActions().isEmpty());
        assertTrue(child.regionalEffects().isEmpty());
        assertNull(child.source().page());
        assertNull(child.mythicEncounter());
    }

    @Test
    void repeatedResolutionKeepsOrderingAndSupportIdentitiesDeterministic() {
        var repeated = new LegendaryGroupResolver().resolve(catalog.authoredInputs());
        assertEquals(catalog.definitionsById(), repeated);
        assertEquals(new ArrayList<>(catalog.definitionsById().keySet()), new ArrayList<>(repeated.keySet()));
        assertEquals("legendary-group:MM:Aboleth", require("Aboleth", "MM").id().value());
        assertEquals(188, catalog.authoredInputs().groups().stream().map(root -> root.id()).distinct().count());
    }

    private LegendaryGroupDefinition require(String name, String source) {
        var result = catalog.findByNameAndSource(name, source);
        assertNotNull(result, name + "|" + source);
        return result;
    }

    private LegendaryGroupInputs input(String roots) throws Exception {
        return new LegendaryGroupSourceLoader(mapper, sourceRegistry).read(
                groupBytes(roots), EMPTY_TEMPLATES.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] groupBytes(String roots) {
        return ("{\"_meta\":{\"internalCopies\":[\"legendaryGroup\"]},\"legendaryGroup\":[" + roots + "]}")
                .getBytes(StandardCharsets.UTF_8);
    }

    private byte[] resource(String name) throws Exception {
        try (var input = getClass().getClassLoader().getResourceAsStream(name)) {
            assertNotNull(input);
            return input.readAllBytes();
        }
    }

    private static List<LegendaryGroupBodyEntry> bodies(LegendaryGroupDefinition definition) {
        List<LegendaryGroupBodyEntry> result = new ArrayList<>();
        if (definition.lairActions() != null) {
            result.addAll(definition.lairActions());
        }
        if (definition.regionalEffects() != null) {
            result.addAll(definition.regionalEffects());
        }
        if (definition.mythicEncounter() != null) {
            result.addAll(definition.mythicEncounter());
        }
        return List.copyOf(result);
    }

    private static void assertDirectBody(List<EntryDte> authored, List<LegendaryGroupBodyEntry> effective) {
        if (authored != null) {
            assertEquals(authored, effective.stream().map(LegendaryGroupBodyEntry::content).toList());
        }
    }
}
