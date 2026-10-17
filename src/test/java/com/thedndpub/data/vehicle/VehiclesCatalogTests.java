package com.thedndpub.data.vehicle;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.VehicleUpgradesController;
import com.thedndpub.data.controllers.VehiclesController;
import com.thedndpub.data.dto.vehicle.VehicleDefinition;
import com.thedndpub.data.dte.vehicle.VehicleActionObjectDte;
import com.thedndpub.data.dte.vehicle.VehicleActionTextDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedArmorClassDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleDetailedSpeedDte;
import com.thedndpub.data.dte.vehicle.VehicleDte;
import com.thedndpub.data.dte.vehicle.VehicleFileDte;
import com.thedndpub.data.dte.vehicle.VehicleHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleNumberDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericArmorClassDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericHitPointsDte;
import com.thedndpub.data.dte.vehicle.VehicleNumericSpeedDte;
import com.thedndpub.data.dte.vehicle.VehicleSpeedDte;
import com.thedndpub.data.dte.vehicle.VehicleStringDte;
import com.thedndpub.data.dte.vehicle.VehicleStringListDte;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.VehicleCatalog;
import com.thedndpub.data.services.VehicleUpgradeCatalog;
import com.thedndpub.data.services.VehicleUpgradesService;
import com.thedndpub.data.services.VehiclesService;
import com.thedndpub.data.services.VehiclesSource;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
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
import java.util.HashSet;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class VehiclesCatalogTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleCatalog vehicles;

    @Autowired
    private VehicleUpgradeCatalog upgrades;

    @Autowired
    private VehiclesService vehiclesService;

    @Autowired
    private VehicleUpgradesService upgradesService;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private CrossCatalogResolutionCoordinator resolutionCoordinator;

    @Test
    void loadsEveryStrictlyTypedRootAndValidatesAuthoredSourcesAndIdentity() throws Exception {
        VehicleFileDte source;
        try (InputStream input = new ClassPathResource("data/vehicles.json").getInputStream()) {
            source = objectMapper.readerFor(VehicleFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }

        assertEquals(39, source.vehicle().size());
        assertEquals(31, source.vehicleUpgrade().size());
        assertEquals(39, vehicles.size());
        assertEquals(31, upgrades.size());
        assertEquals(39, new HashSet<>(vehicles.definitions().stream().map(VehicleDefinition::id).toList()).size());
        assertEquals(31, new HashSet<>(upgrades.definitions().stream().map(value -> value.id()).toList()).size());
        assertTrue(vehicles.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertTrue(upgrades.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals("vehicle:DMG:apparatus-of-kwalish",
                vehicles.findByName("Apparatus of Kwalish").getFirst().id().value());
        assertTrue(vehicles.findByName("Apparatus of Kwalish").size() >= 2);
        assertEquals(Map.ofEntries(Map.entry("DMG", 1L), Map.entry("XDMG", 1L), Map.entry("MTF", 3L),
                        Map.entry("AI", 2L), Map.entry("AAG", 16L), Map.entry("BGDIA", 4L),
                        Map.entry("GoS", 6L), Map.entry("EFA", 3L), Map.entry("DD", 1L),
                        Map.entry("DitLCoT", 1L), Map.entry("VEoR", 1L)),
                sourceCounts(vehicles.definitions().stream()
                        .map(value -> value.source().identity().code()).toList()));
        assertEquals(Map.of("BGDIA", 9L, "GoS", 22L),
                sourceCounts(upgrades.definitions().stream()
                        .map(value -> value.source().identity().code()).toList()));
        assertEquals(java.util.List.of("Apparatus of Kwalish|XDMG"), vehicles.findByName("Apparatus of Kwalish")
                .stream().filter(value -> value.source().identity().code().equals("DMG"))
                .findFirst().orElseThrow().authoredData().reprintedAs());
        assertTrue(vehicles.summaries().stream().map(value -> value.name().toLowerCase()).toList()
                .equals(vehicles.summaries().stream().map(value -> value.name().toLowerCase()).sorted().toList()));
        assertThrows(UnsupportedOperationException.class, () -> vehicles.definitions().clear());
        assertThrows(UnsupportedOperationException.class, () -> upgrades.definitions().clear());
    }

    @Test
    void preservesObservedPolymorphicMechanicsAndOptionalMetadata() {
        Map<String, Long> vehicleTypes = vehicles.definitions().stream()
                .collect(Collectors.groupingBy(VehicleDefinition::vehicleType, Collectors.counting()));
        assertEquals(6, vehicleTypes.size());
        assertTrue(vehicleTypes.keySet().containsAll(java.util.Set.of(
                "OBJECT", "SHIP", "SPELLJAMMER", "INFWAR", "ELEMENTAL_AIRSHIP", "CREATURE")));
        assertEquals(8, upgrades.definitions().stream().flatMap(value -> value.upgradeType().stream())
                .map(String::trim).distinct().count());

        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.mechanics().size() instanceof VehicleStringDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.mechanics().size() instanceof VehicleStringListDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value -> value.authoredData().capCargo()
                instanceof VehicleNumberDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value -> value.authoredData().capCargo()
                instanceof com.thedndpub.data.dte.vehicle.VehicleTextDte));
        var keelboat = vehicles.findByName("Keelboat").stream()
                .filter(value -> value.source().identity().code().equals("GoS"))
                .findFirst()
                .orElseThrow();
        assertEquals(new java.math.BigDecimal("0.5"),
                ((VehicleNumberDte) keelboat.authoredData().capCargo()).value());
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.authoredData().ac() instanceof VehicleNumericArmorClassDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.authoredData().ac() instanceof VehicleDetailedArmorClassDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.authoredData().hp() instanceof VehicleNumericHitPointsDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.authoredData().hp() instanceof VehicleDetailedHitPointsDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.authoredData().speed() instanceof VehicleNumericSpeedDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value ->
                value.authoredData().speed() instanceof VehicleDetailedSpeedDte));
        assertTrue(vehicles.definitions().stream().flatMap(value -> value.authoredData().action() == null
                        ? java.util.stream.Stream.empty() : value.authoredData().action().stream())
                .anyMatch(value -> value instanceof VehicleActionTextDte));
        assertTrue(vehicles.definitions().stream().flatMap(value -> value.authoredData().action() == null
                        ? java.util.stream.Stream.empty() : value.authoredData().action().stream())
                .anyMatch(value -> value instanceof VehicleActionObjectDte));
        assertTrue(vehicles.definitions().stream().anyMatch(value -> value.authoredData().intScore() != null
                && value.authoredData().intScore() == 0));
        assertTrue(vehicles.definitions().stream().anyMatch(value -> value.authoredData().hasToken()));
        assertTrue(vehicles.definitions().stream().anyMatch(value -> value.authoredData().otherSources() != null));
    }

    @Test
    void rejectsUnknownRootAndPolymorphicFields() {
        String unknownRoot = "{\"name\":\"Test Vehicle\",\"source\":\"PHB\","
                + "\"vehicleType\":\"SHIP\",\"unrecognized\":true}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VehicleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownRoot));

        String unknownArmorClass = "{\"name\":\"Test Vehicle\",\"source\":\"PHB\","
                + "\"vehicleType\":\"SHIP\",\"ac\":[{\"ac\":12,\"unrecognized\":1}]}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VehicleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(unknownArmorClass));

        String malformedSpeed = "{\"name\":\"Test Vehicle\",\"source\":\"PHB\","
                + "\"vehicleType\":\"SHIP\",\"speed\":{\"fly\":{\"number\":10,\"extra\":1}}}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(VehicleDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(malformedSpeed));
    }

    @Test
    void publicProjectionsOmitDescriptionsMarkupAndUnapprovedFields() throws Exception {
        var vehicle = vehicles.definitions().stream().filter(value -> value.authoredData().entries() != null
                        && !value.authoredData().entries().isEmpty())
                .findFirst().orElseThrow();
        String vehicleJson = objectMapper.writeValueAsString(vehiclesService.byId(vehicle.id().value()));
        String upgradeJson = objectMapper.writeValueAsString(upgradesService.byId(
                upgrades.definitions().getFirst().id().value()));

        assertFalse(vehicleJson.contains("{@"));
        assertFalse(upgradeJson.contains("{@"));
        assertFalse(objectMapper.readTree(vehicleJson).has("entries"));
        assertFalse(objectMapper.readTree(upgradeJson).has("entries"));
        assertFalse(vehicleJson.contains("tokenCredit"));
        assertFalse(vehicleJson.contains("hasFluff"));
        assertFalse(upgradeJson.contains("authoredEntries"));
        assertNotNull(vehiclesService.byId(vehicle.id().value()).id());
        VehicleDefinition zeroIntelligence = vehicles.definitions().stream()
                .filter(value -> value.mechanics().intelligence() != null && value.mechanics().intelligence() == 0)
                .findFirst()
                .orElseThrow();
        String zeroValueJson = objectMapper.writeValueAsString(vehiclesService.byId(zeroIntelligence.id().value()));
        assertTrue(zeroValueJson.contains("\"intelligence\":0"));
    }

    @Test
    void supportedReferenceOccurrencesAreSeparateAndPriorSnapshotRemainsStable() {
        var results = resolutionCoordinator.snapshot().results();
        var vehicleReferences = results.stream()
                .filter(result -> result.reference().owner().catalog().equals("vehicle")
                        || result.reference().owner().catalog().equals("vehicle-upgrade"))
                .toList();
        assertEquals(4, vehicleReferences.size());
        assertEquals(1, vehicleReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(2, vehicleReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(0, vehicleReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1, vehicleReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
        assertTrue(vehicleReferences.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED)
                .allMatch(result -> result.resolvedTargetId() != null && result.reference().parsedSource() != null));
        assertTrue(vehicleReferences.stream()
                .filter(result -> result.outcome() != ResolutionOutcome.RESOLVED)
                .allMatch(result -> result.resolvedTargetId() == null));

        var priorSnapshot = results.stream()
                .filter(result -> !result.reference().owner().catalog().equals("vehicle"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle-upgrade"))
                .filter(result -> !result.reference().owner().catalog().equals("variant-rule"))
                .filter(result -> !result.reference().owner().catalog().equals("deity"))
                .filter(result -> !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(4_516, priorSnapshot.size());
        assertEquals(1_917, priorSnapshot.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.RESOLVED).count());
        assertEquals(1_485, priorSnapshot.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS).count());
        assertEquals(44, priorSnapshot.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.UNRESOLVED).count());
        assertEquals(1_070, priorSnapshot.stream()
                .filter(result -> result.outcome() == ResolutionOutcome.DEFERRED).count());
    }

    @Test
    void restRoutesFilterBySourceAndAuthoredCategory() throws Exception {
        MockMvc mockMvc = mockMvc();
        var upgrade = upgrades.definitions().getFirst();
        mockMvc.perform(get("/api/vehicles").param("source", "GoS").param("type", "SHIP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("GoS"))
                .andExpect(jsonPath("$[0].vehicleType").value("SHIP"));
        mockMvc.perform(get("/api/vehicle-upgrades").param("source", "GoS").param("type", "SHP:W"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].source.source").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.is("GoS"))));
        mockMvc.perform(get("/api/vehicles/{id}", vehicles.definitions().getFirst().id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        mockMvc.perform(get("/api/vehicle-upgrades/by-name/{name}", upgrade.name())
                        .param("source", upgrade.source().identity().code()))
                .andExpect(status().isOk());
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(
                new VehiclesController(vehiclesService),
                new VehicleUpgradesController(upgradesService)).build();
    }

    private static Map<String, Long> sourceCounts(java.util.List<String> sources) {
        return sources.stream().collect(Collectors.groupingBy(value -> value, Collectors.counting()));
    }

    @Test
    void classpathLoadingWorksFromPackagedJar() throws Exception {
        Path jar = temporaryDirectory.resolve("vehicles-source.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar));
                InputStream input = new ClassPathResource("data/vehicles.json").getInputStream()) {
            output.putNextEntry(new JarEntry("data/vehicles.json"));
            input.transferTo(output);
            output.closeEntry();
        }

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader jarLoader = new URLClassLoader(new URL[]{jar.toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {
            Thread.currentThread().setContextClassLoader(jarLoader);
            VehiclesSource packagedSource = new VehiclesSource(new ObjectMapper());
            assertEquals(39, packagedSource.file().vehicle().size());
            assertEquals(31, packagedSource.file().vehicleUpgrade().size());
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}
