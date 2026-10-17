package com.thedndpub.data.cultboon;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.BoonsController;
import com.thedndpub.data.controllers.CultsAndBoonsApiExceptionHandler;
import com.thedndpub.data.controllers.CultsController;
import com.thedndpub.data.dto.cultboon.BoonDto;
import com.thedndpub.data.dto.cultboon.CultBoonMapper;
import com.thedndpub.data.dto.cultboon.CultDto;
import com.thedndpub.data.dte.cultboon.CultsAndBoonsFileDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ReferenceSemanticForm;
import com.thedndpub.data.references.ReferenceTargetKind;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.references.PublicReferenceProjection;
import com.thedndpub.data.services.BoonsService;
import com.thedndpub.data.services.CultCatalog;
import com.thedndpub.data.services.BoonCatalog;
import com.thedndpub.data.services.CultsAndBoonsSourceValidator;
import com.thedndpub.data.services.CultsService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CultsAndBoonsPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CultBoonMapper mapper;

    @Autowired
    private CultCatalog cultCatalog;

    @Autowired
    private BoonCatalog boonCatalog;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private CultsAndBoonsSourceValidator sourceValidator;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Autowired
    private PublicReferenceProjection publicReferences;

    @Test
    void fullCorpusLoadsBothIndependentRootCollectionsAndSourceIdentities() throws Exception {
        CultsAndBoonsFileDte source = readSource();

        assertEquals(30, source.cult().size());
        assertEquals(12, source.boon().size());
        assertEquals(30, cultCatalog.size());
        assertEquals(12, boonCatalog.size());
        assertEquals(30, cultCatalog.definitions().stream().map(cult -> cult.id().value()).distinct().count());
        assertEquals(12, boonCatalog.definitions().stream().map(boon -> boon.id().value()).distinct().count());
        assertTrue(cultCatalog.definitions().stream().allMatch(cult ->
                sourceRegistry.find(cult.source().identity().code()) != null));
        assertTrue(boonCatalog.definitions().stream().allMatch(boon ->
                sourceRegistry.find(boon.source().identity().code()) != null));
    }

    @Test
    void sourceDistributionAndTypesRemainDistinctByCollection() {
        assertEquals(15, cultCatalog.definitions().stream()
                .filter(cult -> cult.source().identity().code().equals("MTF")).count());
        assertEquals(10, cultCatalog.definitions().stream()
                .filter(cult -> cult.source().identity().code().equals("MPMM")).count());
        assertEquals(1, cultCatalog.definitions().stream()
                .filter(cult -> cult.source().identity().code().equals("PSD")).count());
        assertEquals(4, cultCatalog.definitions().stream()
                .filter(cult -> cult.source().identity().code().equals("PotA")).count());
        assertTrue(boonCatalog.definitions().stream()
                .allMatch(boon -> "MTF".equals(boon.source().identity().code())));
        assertTrue(boonCatalog.definitions().stream().allMatch(boon -> "Demonic".equals(boon.type())));
        assertEquals(13, cultCatalog.definitions().stream()
                .filter(cult -> "Diabolical".equals(cult.type())).count());
        assertEquals(5, cultCatalog.definitions().stream()
                .filter(cult -> "Elder Evil".equals(cult.type())).count());
        assertEquals(8, cultCatalog.definitions().stream()
                .filter(cult -> "Demonic".equals(cult.type())).count());
        assertEquals(4, cultCatalog.definitions().stream()
                .filter(cult -> "Elemental".equals(cult.type())).count());
    }

    @Test
    void readableSourceAwareIdsAreStableAndHaveNoNameSourceCollisions() {
        assertEquals("cult:mtf:cult-of-asmodeus", cultCatalog.findByName("Cult of Asmodeus").getFirst().id().value());
        assertEquals("boon:mtf:demonic-boon-of-baphomet",
                boonCatalog.findByName("Demonic Boon of Baphomet").getFirst().id().value());
        assertEquals(30, cultCatalog.definitions().stream()
                .map(cult -> identity(cult.name(), cult.source().identity().code())).distinct().count());
        assertEquals(12, boonCatalog.definitions().stream()
                .map(boon -> identity(boon.name(), boon.source().identity().code())).distinct().count());
        assertEquals(List.of("MPMM", "MTF"), cultCatalog.findByName("Cult of Geryon").stream()
                .map(cult -> cult.source().identity().code()).toList());
        assertThrows(UnsupportedOperationException.class, () -> cultCatalog.definitions().clear());
    }

    @Test
    void typedMetadataAndDescriptiveEntriesRemainAvailableInternally() {
        var baalzebul = cultCatalog.findByName("Cult of Baalzebul").getFirst();
        var baphomet = boonCatalog.findByName("Demonic Boon of Baphomet").getFirst();

        assertTrue(baalzebul.goalEntry().contains("Restoration of honor"));
        assertTrue(baalzebul.cultistsEntry().contains("Any NPC"));
        assertTrue(baalzebul.signatureSpellsEntry().contains("{@spell"));
        assertFalse(cultCatalog.importById(baalzebul.id().value()).provenance().entries().isEmpty());
        assertTrue(baphomet.abilityEntry().contains("+4 bonus"));
        assertTrue(baphomet.signatureSpellsEntry().contains("{@spell"));
        assertFalse(boonCatalog.importById(baphomet.id().value()).provenance().entries().isEmpty());
    }

    @Test
    void reprintVariantsPreserveAuthoredUidSourceAndTagWithoutInventingTargetIds() {
        var geryon = cultCatalog.definitions().stream()
                .filter(cult -> "Cult of Geryon".equals(cult.name()))
                .filter(cult -> !cult.reprintedAs().isEmpty())
                .findFirst()
                .orElseThrow();
        var zariel = cultCatalog.definitions().stream()
                .filter(cult -> "Cult of Zariel".equals(cult.name()))
                .filter(cult -> !cult.reprintedAs().isEmpty())
                .findFirst()
                .orElseThrow();
        var baphomet = boonCatalog.findByName("Demonic Boon of Baphomet").getFirst();

        assertEquals(2, cultCatalog.definitions().stream().mapToInt(cult -> cult.reprintedAs().size()).sum());
        assertEquals(8, boonCatalog.definitions().stream().mapToInt(boon -> boon.reprintedAs().size()).sum());
        assertEquals("Cult of Geryon|MPMM", geryon.reprintedAs().getFirst().authoredUid());
        assertEquals("MPMM", geryon.reprintedAs().getFirst().source().code());
        assertEquals("boon", zariel.reprintedAs().getFirst().tag());
        assertEquals("Cult of Baphomet|MPMM", baphomet.reprintedAs().getFirst().authoredUid());
        assertTrue(baphomet.reprintedAs().getFirst().tag().equals("boon"));
    }

    @Test
    void publicDtosExposeMetadataButOmitEntryProseAndMechanicalNarrative() throws Exception {
        CultDto cult = CultDto.from(cultCatalog.findByName("Cult of Baalzebul").getFirst(), List.of());
        BoonDto boon = BoonDto.from(boonCatalog.findByName("Demonic Boon of Baphomet").getFirst(), List.of());
        String cultJson = objectMapper.writeValueAsString(cult);
        String boonJson = objectMapper.writeValueAsString(boon);

        assertTrue(cultJson.contains("Cult of Baalzebul"));
        assertTrue(cultJson.contains("Diabolical"));
        assertFalse(cultJson.contains("Restoration of honor"));
        assertFalse(cultJson.contains("signatureSpells"));
        assertFalse(cultJson.contains("{@spell"));
        assertTrue(boonJson.contains("Demonic Boon of Baphomet"));
        assertFalse(boonJson.contains("+4 bonus"));
        assertFalse(boonJson.contains("abilityEntry"));
        assertFalse(boonJson.contains("{@spell"));
        assertFalse(cultJson.contains("targetId"));
        assertFalse(boonJson.contains("targetId"));
    }

    @Test
    void sourceAndIdentityErrorsAreRejected() throws Exception {
        CultsAndBoonsFileDte original = readSource();
        var invalidSourceCult = new com.thedndpub.data.dte.cultboon.CultDte(
                "Invalid Source Cult", "UNKNOWN", 1, "Test", List.of(), null,
                null, null, null, null);
        assertThrows(IllegalStateException.class, () -> new CultCatalog(
                List.of(invalidSourceCult), mapper, sourceValidator));

        var duplicateCult = original.cult().getFirst();
        assertThrows(IllegalStateException.class, () -> new CultCatalog(
                List.of(duplicateCult, duplicateCult), mapper, sourceValidator));
    }

    @Test
    void unknownRootFieldsAndUnsupportedReprintShapesFailStrictDeserialization() {
        String unknownRoot = """
                {"cult":[{"name":"Cult","source":"MTF","page":1,"type":"Test",
                "entries":[],"unrecognized":true}],"boon":[]}
                """;
        assertThrows(Exception.class, () -> objectMapper.readerFor(CultsAndBoonsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(unknownRoot));

        String unknownReprintField = """
                {"cult":[{"name":"Cult","source":"MTF","page":1,"type":"Test",
                "entries":[],"reprintedAs":[{"uid":"Cult|MTF","tag":"cult","extra":true}]}],"boon":[]}
                """;
        assertThrows(Exception.class, () -> objectMapper.readerFor(CultsAndBoonsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(unknownReprintField));

        String badReprintType = """
                {"cult":[{"name":"Cult","source":"MTF","page":1,"type":"Test",
                "entries":[],"reprintedAs":[42]}],"boon":[]}
                """;
        assertThrows(Exception.class, () -> objectMapper.readerFor(CultsAndBoonsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(badReprintType));
    }

    @Test
    void sourceQualifiedMarkupIsValidatedWithoutPromotingItToPublicRelationships() throws Exception {
        assertNotNull(sourceRegistry.find("SCAG"));
        assertNotNull(sourceRegistry.find("VGM"));
        assertNotNull(sourceRegistry.find("PHB"));
        assertNotNull(sourceRegistry.find("PotA"));

        CultsAndBoonsFileDte original = readSource();
        var sourceText = new com.thedndpub.data.dte.cultboon.CultTextDte("{@spell harmless|NOTASOURCE}");
        var cult = new com.thedndpub.data.dte.cultboon.CultDte(
                "Cult", "MTF", 1, "Test", List.of(new com.thedndpub.data.dte.common.entry.TextEntryDte("{@spell harmless|NOTASOURCE}")),
                null, null, null, sourceText, null);
        assertThrows(IllegalStateException.class, () -> new CultCatalog(
                List.of(cult), mapper, sourceValidator));
    }

    @Test
    void typeFiltersAndNameLookupsAreCaseInsensitiveAndPreserveAmbiguity() {
        CultsService cults = new CultsService(cultCatalog, publicReferences);
        BoonsService boons = new BoonsService(boonCatalog, publicReferences);

        assertEquals(10, cults.find(null, "mtf", "diabolical").size());
        assertEquals(12, boons.find(null, "MTF", "demonic").size());
        assertEquals(1, cults.findByName("cult of asmodeus", "MTF").size());
        assertEquals(1, cults.findByName("Cult of Geryon", "MTF").size());
        assertEquals(2, cults.findByName("Cult of Geryon", null).size());
        assertEquals(0, boons.findByName("Demonic Boon of Baphomet", "MPMM").size());
    }

    @Test
    void restEndpointsReturnSummariesDetailsAndNotFoundResponses() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                        new CultsController(new CultsService(cultCatalog, publicReferences)),
                        new BoonsController(new BoonsService(boonCatalog, publicReferences)))
                .setControllerAdvice(new CultsAndBoonsApiExceptionHandler())
                .build();

        mockMvc.perform(get("/api/cults").param("source", "mtf").param("type", "diabolical"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("cult:mtf:cult-of-asmodeus"))
                .andExpect(jsonPath("$[0].type").value("Diabolical"));
        mockMvc.perform(get("/api/cults/cult:mtf:cult-of-asmodeus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.source").value("MTF"));
        mockMvc.perform(get("/api/cults/by-name/Cult of Asmodeus").param("source", "MTF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cult of Asmodeus"));
        mockMvc.perform(get("/api/boons").param("type", "demonic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12));
        mockMvc.perform(get("/api/boons/by-name/Demonic Boon of Baphomet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Demonic Boon of Baphomet"));
        mockMvc.perform(get("/api/boons/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sourceQualifiedReprintsRemainOutsideExistingResolverSnapshot() {
        var snapshot = resolutionCoordinator.snapshot();
        var historical = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("cult")
                        && !result.reference().owner().catalog().equals("boon")
                        && !result.reference().owner().catalog().equals("object")
                        && !result.reference().owner().catalog().equals("facility")
                        && !result.reference().owner().catalog().equals("encounter")
                        && !result.reference().owner().catalog().equals("reward")
                        && !result.reference().owner().catalog().equals("trap")
                        && !result.reference().owner().catalog().equals("hazard")
                        && !result.reference().owner().catalog().equals("vehicle")
                        && !result.reference().owner().catalog().equals("vehicle-upgrade")
                        && !result.reference().owner().catalog().equals("variant-rule")
                        && !result.reference().owner().catalog().equals("deity")
                        && !result.reference().owner().catalog().equals("treasure-table"))
                .toList();

        assertEquals(3_875, historical.size());
        assertEquals(1_697, historical.stream().filter(result -> result.outcome().name().equals("RESOLVED")).count());
        assertEquals(1_108, historical.stream().filter(result -> result.outcome().name().equals("AMBIGUOUS")).count());
        assertEquals(30, historical.stream().filter(result -> result.outcome().name().equals("UNRESOLVED")).count());
        assertEquals(1_040, historical.stream().filter(result -> result.outcome().name().equals("DEFERRED")).count());
        assertEquals(361, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("variant-rule")).count());
        assertEquals(33, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("deity")).count());
        assertEquals(1_332, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("treasure-table")).count());
        assertEquals(2_371, snapshot.results().size() - historical.size());
        assertEquals(4, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("object")).count());
        assertFalse(snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("cult")
                        || result.reference().owner().catalog().equals("boon"))
                .anyMatch(result -> result.reference().fieldLocator().startsWith("reprintedAs")));
        assertTrue(cultCatalog.definitions().stream().flatMap(cult -> cult.reprintedAs().stream())
                .noneMatch(reprint -> reprint.authoredUid().isBlank()));
        assertTrue(boonCatalog.definitions().stream().flatMap(boon -> boon.reprintedAs().stream())
                .noneMatch(reprint -> reprint.authoredUid().isBlank()));
    }

    @Test
    void explicitMarkupLinksAreResolvedWithoutBecomingGrantsOrExpandedFilters() throws Exception {
        var references = resolutionCoordinator.snapshot().results().stream()
                .filter(result -> result.reference().owner().catalog().equals("cult")
                        || result.reference().owner().catalog().equals("boon"))
                .toList();
        assertEquals(75, references.size());
        assertEquals(65, references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL).count());
        assertEquals(5, references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.ITEM)
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.DEFINITION).count());
        assertEquals(4, references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SKILL).count());
        assertEquals(1, references.stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.FILTER)
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.ITEM)
                .count());
        assertTrue(references.stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.FILTER)
                .allMatch(result -> result.outcome() == ResolutionOutcome.DEFERRED
                        && result.resolvedTargetId() == null));
        assertTrue(references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL
                        || result.reference().targetKind() == ReferenceTargetKind.SKILL)
                .allMatch(result -> result.reference().parsedSource() == null
                        && result.resolvedTargetId() == null));
        assertTrue(references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.ITEM)
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.DEFINITION)
                .allMatch(result -> result.outcome() == ResolutionOutcome.RESOLVED
                        && result.resolvedTargetId() != null));
        assertEquals(65, references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL)
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(4, references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SKILL)
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());

        var itemReference = references.stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.ITEM)
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.DEFINITION)
                .findFirst()
                .orElseThrow();
        var projected = publicReferences.forOwner(
                itemReference.reference().owner().catalog(),
                itemReference.reference().owner().stableId());
        assertTrue(projected.stream().anyMatch(reference ->
                reference.fieldLocator().equals(itemReference.reference().fieldLocator())
                        && itemReference.resolvedTargetId().equals(reference.targetId())));

        String detailJson;
        if ("cult".equals(itemReference.reference().owner().catalog())) {
            detailJson = objectMapper.writeValueAsString(new CultsService(cultCatalog, publicReferences)
                    .findById(itemReference.reference().owner().stableId()));
        } else {
            detailJson = objectMapper.writeValueAsString(new BoonsService(boonCatalog, publicReferences)
                    .findById(itemReference.reference().owner().stableId()));
        }
        assertTrue(detailJson.contains(itemReference.resolvedTargetId()));
        assertFalse(detailJson.contains("candidates"));
        assertFalse(detailJson.contains("diagnostic"));
        assertFalse(detailJson.contains("{@"));
    }

    @Test
    void classpathResourceLoadsFromPackagedJar() throws Exception {
        Path jarPath = temporaryDirectory.resolve("cults-boons-test.jar");
        try (InputStream input = new ClassPathResource("data/cultsboons.json").getInputStream();
                JarOutputStream jarOutput = new JarOutputStream(Files.newOutputStream(jarPath))) {
            jarOutput.putNextEntry(new JarEntry("data/cultsboons.json"));
            input.transferTo(jarOutput);
            jarOutput.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            try (InputStream packaged = classLoader.getResourceAsStream("data/cultsboons.json")) {
                assertNotNull(packaged);
                CultsAndBoonsFileDte source = objectMapper.readerFor(CultsAndBoonsFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(packaged);
                assertEquals(30, source.cult().size());
                assertEquals(12, source.boon().size());
            }
        }
    }

    private CultsAndBoonsFileDte readSource() throws Exception {
        try (InputStream input = new ClassPathResource("data/cultsboons.json").getInputStream()) {
            return objectMapper.readerFor(CultsAndBoonsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
    }

    private static String identity(String name, String source) {
        return name.toLowerCase() + "|" + source.toLowerCase();
    }
}
